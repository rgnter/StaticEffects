package me.MrWener.StaticEffects.plugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Logger {
    // Prefix of Logger
    public static final String PREFIX = "§cStaticEffects";
    public static final String SEPARATOR = "§8::";
    // Colors
    public static final ChatColor MESSAGE_COLOR = ChatColor.GRAY;
    // Console
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    /**
     * @param message Message object that will be send to the console
     */
    public static <T extends String> void send(T message) {
        console.sendMessage(PREFIX + " " + SEPARATOR + " " + MESSAGE_COLOR + message.trim());
    }
}
