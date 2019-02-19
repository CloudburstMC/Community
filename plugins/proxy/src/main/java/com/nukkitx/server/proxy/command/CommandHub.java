package com.nukkitx.server.proxy.command;

import com.nukkitx.server.proxy.ProxyConfig;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.command.Command;
import org.itxtech.nemisys.command.CommandSender;

/**
 * @author DaPorkchop_
 */
public class CommandHub extends Command {
    public CommandHub() {
        super(ProxyConfig.INSTANCE.getHubCommand());
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        Player player = (Player) sender;
        if (!player.getClient().getDescription().equals("hub"))  {
            player.transfer(player.getServer().getClientByDesc("hub"));
        }
        return false;
    }
}
