package com.nukkitx.server.client;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import com.nukkitx.server.common.SharedConstants;
import lombok.NonNull;

/**
 * @author DaPorkchop_
 */
public class ClientListener implements Listener {
    @EventHandler
    public void onPlayerJoin(@NonNull PlayerJoinEvent event) {
        if (SharedConstants.OP_UUIDS.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().setOp(true);
        } else {
            event.getPlayer().setOp(false);
        }
    }

    @EventHandler
    public void onPlayerLeave(@NonNull PlayerQuitEvent event) {
        event.getPlayer().setOp(false);
    }
}
