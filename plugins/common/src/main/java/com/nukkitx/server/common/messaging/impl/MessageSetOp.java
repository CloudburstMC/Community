package com.nukkitx.server.common.messaging.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.nukkitx.server.common.messaging.Message;
import com.nukkitx.server.common.messaging.MessageRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageSetOp implements Message {
    @NonNull
    protected UUID id;
    protected boolean state;

    @Override
    public void handle(MessageRegistry registry) {
        Player player = Server.getInstance().getOnlinePlayers().get(this.id);
        if (player != null) {
            player.setOp(this.state);
        }
    }

    @Override
    public void read(DataIn in) throws IOException {
        this.id = new UUID(in.readLong(), in.readLong());
        this.state = in.readBoolean();
    }

    @Override
    public void write(DataOut out) throws IOException {
        out.writeLong(this.id.getMostSignificantBits());
        out.writeLong(this.id.getLeastSignificantBits());
        out.writeBoolean(this.state);
    }
}
