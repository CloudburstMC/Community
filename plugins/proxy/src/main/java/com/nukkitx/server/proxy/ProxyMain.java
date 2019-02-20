package com.nukkitx.server.proxy;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.nukkitx.server.common.messaging.Message;
import com.nukkitx.server.common.messaging.MessageRegistry;
import com.nukkitx.server.proxy.command.ProxyCommandExecutor;
import com.nukkitx.server.proxy.command.ServerCommandExecutor;
import com.nukkitx.server.proxy.command.SpecificServerCommandExecutor;
import com.nukkitx.server.proxy.storage.CompactUUIDSerializer;
import com.nukkitx.server.proxy.storage.KeyHasherName;
import com.nukkitx.server.proxy.storage.KeyHasherUUID;
import com.nukkitx.server.proxy.storage.PlayerData;
import com.nukkitx.server.proxy.storage.ServerData;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.config.PConfig;
import net.daporkchop.lib.config.decoder.PorkConfigDecoder;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.container.map.data.ConstantLengthLookup;
import net.daporkchop.lib.db.container.map.data.SectoredDataLookup;
import net.daporkchop.lib.db.container.map.index.hashtable.BucketingHashTableIndexLookup;
import net.daporkchop.lib.db.container.map.index.tree.FasterTreeIndexLookup;
import net.daporkchop.lib.db.container.map.key.DefaultKeyHasher;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.hash.util.Digest;
import net.daporkchop.lib.nbt.util.IndirectNBTSerializer;
import org.itxtech.nemisys.command.PluginCommand;
import org.itxtech.nemisys.plugin.PluginBase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public class ProxyMain extends PluginBase {
    public static ProxyMain INSTANCE;

    @SuppressWarnings("unchecked")
    public static void broadcast(@NonNull Message message) {
        MessageRegistry.INSTANCE.send(message, INSTANCE.getServer().getClients().values().stream()
                .map(client -> (BiConsumer<String, byte[]>) client::sendPluginMesssage)
                .toArray(BiConsumer[]::new));
    }

    public PorkDB db;
    private DBMap<UUID, PlayerData> playerDataMap;
    public LoadingCache<UUID, PlayerData> playerData;
    private DBMap<String, UUID> playerNameLookupMap;
    public LoadingCache<String, UUID> playerNameLookup;
    private DBMap<String, ServerData> serverDataMap;
    public LoadingCache<String, ServerData> serverData;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.onReload();

        this.getLogger().info("Opening databases...");
        try {
            this.db = PorkDB.builder(new File(this.getDataFolder(), "database")).build();

            this.playerDataMap = this.db.<UUID, PlayerData>map("playerData")
                    .setCompression(Compression.GZIP_NORMAL)
                    .setIndexLookup(new FasterTreeIndexLookup<>(4, 1))
                    .setDataLookup(new SectoredDataLookup(1024))
                    .setKeyHasher(new KeyHasherUUID())
                    .setValueSerializer(new IndirectNBTSerializer<>(PlayerData::new))
                    .build();
            this.playerData = this.cached(this.playerDataMap, PlayerData::new);

            this.playerNameLookupMap = this.db.<String, UUID>map("playerNames")
                    .setIndexLookup(new FasterTreeIndexLookup<>(4, 1))
                    .setDataLookup(new ConstantLengthLookup())
                    .setKeyHasher(new DefaultKeyHasher<>(Digest.SHA_256))
                    .setKeyHasher(new KeyHasherName(16))
                    .setValueSerializer(new CompactUUIDSerializer())
                    .build();
            this.playerNameLookup = this.cached(this.playerNameLookupMap, () -> new UUID(0L, 0L));

            this.serverDataMap = this.db.<String, ServerData>map("serverData")
                    .setCompression(Compression.GZIP_NORMAL)
                    .setIndexLookup(new BucketingHashTableIndexLookup<>(16, 4))
                    .setDataLookup(new SectoredDataLookup(1024))
                    .setKeyHasher(new KeyHasherName(32))
                    .setValueSerializer(new IndirectNBTSerializer<>(ServerData::new))
                    .build();
            this.serverData = this.cached(this.serverDataMap, ServerData::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.getLogger().info("Databases opened!");

        {
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(TimeUnit.HOURS.toMillis(1));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    this.onReload();
                }
            });
            t.setDaemon(true);
            t.start();
        }

        this.getServer().getPluginManager().registerEvents(new ProxyListener(), this);

        ((PluginCommand) this.getServer().getCommandMap().getCommand("hub")).setExecutor(new SpecificServerCommandExecutor("hub"));
        ((PluginCommand) this.getServer().getCommandMap().getCommand("server")).setExecutor(new ServerCommandExecutor());
        ((PluginCommand) this.getServer().getCommandMap().getCommand("proxy")).setExecutor(new ProxyCommandExecutor());
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Closing databases...");
        try {
            this.playerData.invalidateAll();
            this.playerNameLookup.invalidateAll();
            this.serverData.invalidateAll();

            this.db.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.getLogger().info("Databases closed!");
    }

    public void onReload() {
        try {
            File configFile = new File(this.getDataFolder(), "config.cfg");
            boolean fresh = !configFile.exists();
            if (fresh) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }
            new PConfig(new PorkConfigDecoder()).load(ProxyConfig.class, configFile);
            if (fresh) {
                this.getLogger().debug("Writing fresh config...");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                new PConfig(new PorkConfigDecoder()).save(ProxyConfig.INSTANCE, DataOut.wrap(baos));
                this.getLogger().debug("Config written! " + baos.size() + " bytes");
                try (DataOut out = DataOut.wrap(configFile)) {
                    out.write(baos.toByteArray());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.getLogger().info(ProxyConfig.INSTANCE.joinMessage);
    }

    private <K, V> LoadingCache<K, V> cached(@NonNull DBMap<K, V> map, @NonNull Supplier<V> emptyValueSupplier) {
        return CacheBuilder.newBuilder()
                .maximumSize(256L)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .removalListener((RemovalListener<K, V>) notification -> {
                    if (notification.getValue() instanceof UUID && ((UUID) notification.getValue()).getMostSignificantBits() == 0L && ((UUID) notification.getValue()).getLeastSignificantBits() == 0L) {
                        return; //don't save placeholder uuids
                    }
                    map.put(notification.getKey(), notification.getValue());
                })
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K key) throws Exception {
                        V val = map.get(key);
                        return val == null ? emptyValueSupplier.get() : val;
                    }
                });
    }
}
