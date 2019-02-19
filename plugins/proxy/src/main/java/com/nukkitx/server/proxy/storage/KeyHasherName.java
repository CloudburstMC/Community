package com.nukkitx.server.proxy.storage;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.db.container.map.key.KeyHasher;
import net.daporkchop.lib.encoding.ToBytes;

import java.util.UUID;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class KeyHasherName implements KeyHasher<String> {
    protected final int hashLength;

    @Override
    public byte[] hash(@NonNull String key) {
        byte[] b = new byte[this.hashLength];
        byte[] text = key.getBytes(UTF8.utf8);
        System.arraycopy(text, 0, b, 0, Math.min(this.hashLength, text.length));
        return b;
    }
}
