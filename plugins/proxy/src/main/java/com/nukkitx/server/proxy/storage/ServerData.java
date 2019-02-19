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
public class ServerData implements NBTSerializable {
    private static final int version = 1;

    @NonNull
    protected String displayName;

    protected boolean hidden;
    protected boolean chatSynced;

    @Override
    public void write(@NonNull CompoundTag tag) {
        tag.putInt("version", version);

        tag.putString("displayName", this.displayName);
        tag.putByte("hidden", this.hidden ? (byte) 1 : 0);
        tag.putByte("chatSynced", this.chatSynced ? (byte) 1 : 0);
    }

    @Override
    public void read(@NonNull CompoundTag tag) {
        if (tag.getInt("version") != version)   {
            throw new IllegalStateException(String.format("Invalid version: %d", tag.getInt("version")));
        }

        this.displayName = tag.getString("displayName");
        this.hidden = tag.getByte("hidden") == 1;
        this.chatSynced = tag.getByte("chatSynced") == 1;
    }
}
