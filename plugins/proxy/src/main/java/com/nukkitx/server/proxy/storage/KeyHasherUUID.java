package com.nukkitx.server.proxy.storage;

import lombok.NonNull;
import net.daporkchop.lib.db.container.map.key.KeyHasher;
import net.daporkchop.lib.encoding.ToBytes;

import java.util.UUID;

/**
 * @author DaPorkchop_
 */
public class KeyHasherUUID implements KeyHasher<UUID> {
    @Override
    public byte[] hash(@NonNull UUID key) {
        return ToBytes.toBytes(key);
    }

    @Override
    public int getHashLength() {
        return 16;
    }
}
