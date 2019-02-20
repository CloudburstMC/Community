package com.nukkitx.server.proxy;

import com.nukkitx.server.common.SharedConstants;
import com.nukkitx.server.common.messaging.MessageRegistry;
import lombok.NonNull;
import org.itxtech.nemisys.event.EventHandler;
import org.itxtech.nemisys.event.Listener;
import org.itxtech.nemisys.event.client.PluginMsgRecvEvent;

/**
 * @author DaPorkchop_
 */
public class ProxyListener implements Listener {
    @EventHandler
    public void onPluginMessage(@NonNull PluginMsgRecvEvent event)  {
        if (event.getChannel().equals(SharedConstants.CHANNEL_NAME))    {
            event.setCancelled();
            MessageRegistry.INSTANCE.handle(event.getData());
        }
    }
}
