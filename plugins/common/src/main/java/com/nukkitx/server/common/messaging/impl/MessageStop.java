package com.nukkitx.server.common.messaging.impl;

import cn.nukkit.Server;
import com.nukkitx.server.common.messaging.Message;
import com.nukkitx.server.common.messaging.MessageRegistry;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class MessageStop implements Message {
    @Override
    public void handle(MessageRegistry registry) {
        Server.getInstance().shutdown();
    }

    @Override
    public void read(DataIn in) throws IOException {
    }

    @Override
    public void write(DataOut out) throws IOException {
    }
}
