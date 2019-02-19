package com.nukkitx.server.proxy.storage;

import lombok.NonNull;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.binary.serialization.impl.ConstantLengthSerializer;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.util.UUID;

/**
 * @author DaPorkchop_
 */
public class CompactUUIDSerializer extends ConstantLengthSerializer<UUID> {
    public CompactUUIDSerializer() {
        super(16);
    }

    @Override
    protected void doWrite(@NonNull UUID val, @NonNull DataOut out) throws IOException {
        out.writeLong(val.getMostSignificantBits());
        out.writeLong(val.getLeastSignificantBits());
    }

    @Override
    protected UUID doRead(@NonNull DataIn in) throws IOException {
        return new UUID(in.readLong(), in.readLong());
    }
}
