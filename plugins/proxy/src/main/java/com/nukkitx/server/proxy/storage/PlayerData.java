package com.nukkitx.server.proxy.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.util.NBTSerializable;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class PlayerData implements NBTSerializable {
    private static final int version = 1;

    @NonNull
    protected String name = "";

    protected boolean admin;

    protected long banExpiry = -1L;

    @Override
    public void write(@NonNull CompoundTag tag) {
        tag.putInt("version", version);

        tag.putString("name", this.name);
        tag.putByte("admin", this.admin ? (byte) 1 : 0);
        tag.putLong("banExpiry", this.banExpiry);
    }

    @Override
    public void read(@NonNull CompoundTag tag) {
        if (tag.getInt("version") != version)   {
            throw new IllegalStateException(String.format("Invalid version: %d", tag.getInt("version")));
        }

        this.name = tag.getString("name");
        this.admin = tag.getByte("admin") == 1;
        this.banExpiry = tag.getLong("banExpiry");
    }
}
