package com.nukkitx.server.proxy;

import com.nukkitx.server.common.SharedConstants;
import com.nukkitx.server.common.messaging.MessageRegistry;
import com.nukkitx.server.common.messaging.impl.MessageSetOp;
import com.nukkitx.server.proxy.storage.PlayerData;
import lombok.NonNull;
import org.itxtech.nemisys.event.EventHandler;
import org.itxtech.nemisys.event.Listener;
import org.itxtech.nemisys.event.client.PluginMsgRecvEvent;
import org.itxtech.nemisys.event.synapse.player.SynapsePlayerConnectEvent;
import org.itxtech.nemisys.event.synapse.player.SynapsePlayerCreationEvent;
import org.itxtech.nemisys.synapse.SynapsePlayer;

import java.util.UUID;

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

    @EventHandler
    public void onPlayerJoin(@NonNull SynapsePlayerConnectEvent event)  {
        SynapsePlayer player = event.getPlayer();
        player.sendMessage(ProxyConfig.INSTANCE.getJoinMessage());
        ProxyMain.INSTANCE.playerNameLookup.put(player.getName(), player.getUuid());
        PlayerData data = ProxyMain.INSTANCE.playerData.getUnchecked(player.getUuid());
        if (!player.getName().equals(data.getName()))   {
            if (!data.getName().isEmpty())  {
                ProxyMain.INSTANCE.playerNameLookup.put(data.getName(), new UUID(0L, 0L));
            } else {
                player.sendMessage(ProxyConfig.INSTANCE.getRules());
            }
            data.setName(player.getName());
        }
        if (data.isAdmin()) {
            ProxyMain.broadcast(new MessageSetOp(player.getUuid(), true));
        }
    }
}
