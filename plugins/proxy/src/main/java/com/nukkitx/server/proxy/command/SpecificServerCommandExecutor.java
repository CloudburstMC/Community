package com.nukkitx.server.proxy.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.command.Command;
import org.itxtech.nemisys.command.CommandExecutor;
import org.itxtech.nemisys.command.CommandSender;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class SpecificServerCommandExecutor implements CommandExecutor {
    @NonNull
    protected final String target;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!sender.isPlayer()) {
            sender.sendMessage("§4Must be a player!");
            return true;
        }
        Player player = (Player) sender;
        if (!player.getClient().getDescription().equals(this.target)) {
            player.transfer(player.getServer().getClientByDesc(this.target));
        }
        return true;
    }
}
