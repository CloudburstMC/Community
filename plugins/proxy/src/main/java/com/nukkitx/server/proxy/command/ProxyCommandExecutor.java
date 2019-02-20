package com.nukkitx.server.proxy.command;

import com.nukkitx.server.common.messaging.impl.MessageSetOp;
import com.nukkitx.server.proxy.ProxyMain;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.command.Command;
import org.itxtech.nemisys.command.CommandExecutor;
import org.itxtech.nemisys.command.CommandSender;

import java.util.UUID;

/**
 * @author DaPorkchop_
 */
public class ProxyCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender.isPlayer() && !ProxyMain.INSTANCE.playerData.getUnchecked(((Player) sender).getUuid()).isAdmin()) {
            sender.sendMessage("§4Missing permission!");
            return true;
        }
        if (strings.length < 1) {
            sender.sendMessage("§cAction required!");
            return true;
        }
        switch (strings[0]) {
            case "reload":
                sender.sendMessage("§aReloading config...");
                ProxyMain.INSTANCE.onReload();
                sender.sendMessage("§aReloaded.");
                break;
            case "op":
            case "deop":
                if (strings.length < 2) {
                    sender.sendMessage("§cUsername required!");
                    return true;
                }
                UUID uuid;
                Player player = ProxyMain.INSTANCE.getServer().getPlayer(strings[1]);
                if (player == null) {
                    uuid = ProxyMain.INSTANCE.playerNameLookup.getUnchecked(strings[1]);
                    if (uuid.getMostSignificantBits() == 0L && uuid.getLeastSignificantBits() == 0L) {
                        uuid = null;
                    }
                } else {
                    uuid = player.getUuid();
                }
                if (uuid == null) {
                    sender.sendMessage(String.format("§cCouldn't find UUID for player: \"%s\"", strings[1]));
                    return true;
                }
                ProxyMain.INSTANCE.playerData.getUnchecked(uuid).setAdmin(strings[0].charAt(0) == 'o');
                ProxyMain.broadcast(new MessageSetOp(uuid, strings[0].charAt(0) == 'o'));
                sender.sendMessage(String.format("§aPlayer \"%s\" %sopped!", strings[1], strings[0].charAt(0) == 'o' ? "" : "de"));
                break;
            case "stop":
                sender.sendMessage("§aStopping network...");
                break;
            default:
                sender.sendMessage(String.format("§cUnknown action: \"%s\"", strings[0]));
        }
        return true;
    }
}
