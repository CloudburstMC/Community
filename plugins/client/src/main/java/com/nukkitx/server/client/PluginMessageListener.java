package com.nukkitx.server.client;

import com.nukkitx.server.common.SharedConstants;
import com.nukkitx.server.common.messaging.MessageRegistry;
import org.itxtech.synapseapi.SynapseEntry;

/**
 * @author DaPorkchop_
 */
public class PluginMessageListener implements org.itxtech.synapseapi.messaging.PluginMessageListener {
    @Override
    public void onPluginMessageReceived(SynapseEntry synapseEntry, String s, byte[] bytes) {
        if (s.equals(SharedConstants.CHANNEL_NAME)) {
            MessageRegistry.INSTANCE.handle(bytes);
        }
    }
}
