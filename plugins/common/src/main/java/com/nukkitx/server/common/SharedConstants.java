package com.nukkitx.server.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author DaPorkchop_
 */
public interface SharedConstants {
    String CHANNEL_NAME = "PROXY_COMMS";

    Set<UUID> OP_UUIDS = Collections.synchronizedSet(new HashSet<>());
}
