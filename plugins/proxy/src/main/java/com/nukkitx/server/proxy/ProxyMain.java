package com.nukkitx.server.proxy;

import com.nukkitx.server.proxy.command.CommandHub;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.config.PConfig;
import net.daporkchop.lib.config.decoder.PorkConfigDecoder;
import org.itxtech.nemisys.plugin.PluginBase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author DaPorkchop_
 */
public class ProxyMain extends PluginBase {
    public static ProxyMain INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.onReload();

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

        this.getServer().getCommandMap().register("proxy", new CommandHub());
        this.getServer().getPluginManager().registerEvents(new ProxyListener(), this);
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
                try (DataOut out = DataOut.wrap(configFile))    {
                    out.write(baos.toByteArray());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.getLogger().info(ProxyConfig.INSTANCE.joinMessage);
    }
}
