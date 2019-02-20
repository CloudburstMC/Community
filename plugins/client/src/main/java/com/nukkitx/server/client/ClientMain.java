package com.nukkitx.server.client;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import com.nukkitx.server.common.SharedConstants;
import com.nukkitx.server.common.messaging.Message;
import com.nukkitx.server.common.messaging.MessageRegistry;
import lombok.NonNull;
import org.itxtech.synapseapi.SynapseAPI;
import org.itxtech.synapseapi.SynapseEntry;

import java.util.function.BiConsumer;

/**
 * @author DaPorkchop_
 */
public class ClientMain extends PluginBase {
    public static ClientMain INSTANCE;

    public SynapseEntry synapseEntry;

    @SuppressWarnings("unchecked")
    public static void send(@NonNull Message message) {
        MessageRegistry.INSTANCE.send(message, (channel, a) -> INSTANCE.synapseEntry.sendPluginMessage(INSTANCE, channel, a));
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        SynapseAPI.getInstance().getMessenger().registerOutgoingPluginChannel(this, SharedConstants.CHANNEL_NAME);
        SynapseAPI.getInstance().getMessenger().registerIncomingPluginChannel(this, SharedConstants.CHANNEL_NAME, new PluginMessageListener());

        this.synapseEntry = SynapseAPI.getInstance().getSynapseEntries().values().iterator().next();
    }

    @Override
    public void onDisable() {
    }
}
