package com.nukkitx.server.proxy;

import lombok.Getter;
import net.daporkchop.lib.config.Config;

/**
 * @author DaPorkchop_
 */
@Config(staticInstance = true)
@Getter
public class ProxyConfig {
    public static ProxyConfig INSTANCE;

    public static String getDefaultJoinMessage() {
        return "Welcome!";
    }

    public static String getDefaultHubCommand() {
        return "/hub";
    }

    public static String getDefaultHubName() {
        return "hub";
    }

    @Config.Comment({
            "A message that will be sent to players when they join.",
            "May contain formatting codes using '§' as well as newlines."
    })
    @Config.Default(objectValue = "com.nukkitx.server.proxy.ProxyConfig#getDefaultJoinMessage")
    protected String joinMessage;
    @Config.Comment({
            "A message that will be sent to players when they join for the first time, or execute /rules.",
            "May contain formatting codes using '§' as well as newlines."
    })
    @Config.Default(objectValue = "com.nukkitx.server.proxy.ProxyConfig#getDefaultJoinMessage")
    protected String rules;
    @Config.Comment({
            "The command to send the player back to the hub.",
            "Defaults to /hub"
    })
    @Config.Default(objectValue = "com.nukkitx.server.proxy.ProxyConfig#getDefaultHubCommand")
    //TODO: i really need to redo this default system
    protected String hubCommand;
    @Config.Comment({
            "The name of the hub server.",
            "Defaults to \"hub\""
    })
    @Config.Default(objectValue = "com.nukkitx.server.proxy.ProxyConfig#getDefaultHubName")
    protected String hubName;
}
