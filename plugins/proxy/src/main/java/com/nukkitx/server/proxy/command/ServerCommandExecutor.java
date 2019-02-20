package com.nukkitx.server.proxy.command;

import com.nukkitx.server.proxy.ProxyMain;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.command.Command;
import org.itxtech.nemisys.command.CommandExecutor;
import org.itxtech.nemisys.command.CommandSender;

/**
 * @author DaPorkchop_
 */
public class ServerCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!sender.isPlayer()) {
            sender.sendMessage("§4Not a player!");
            return true;
        } else if (strings.length < 1) {
            sender.sendMessage("§cTarget server required!");
            return true;
        }
        Client client = ProxyMain.INSTANCE.getServer().getClientByDesc(strings[0].toLowerCase());
        if (client == null) {
            sender.sendMessage(String.format("§cInvalid target server: \"%s\"", strings[0].toLowerCase()));
        } else {
            ((Player) sender).transfer(client);
            sender.sendMessage(String.format("§aConnecting to \"%s\"...", strings[0].toLowerCase()));
        }
        return true;
    }
}
