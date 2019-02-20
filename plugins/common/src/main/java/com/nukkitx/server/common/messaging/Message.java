package com.nukkitx.server.common.messaging;

import lombok.NonNull;
import net.daporkchop.lib.binary.Data;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public interface Message extends Data {
    void handle(@NonNull MessageRegistry registry);
}
