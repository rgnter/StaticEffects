package me.MrWener.StaticEffects.plugin.ui;

import me.MrWener.StaticEffects.plugin.PluginLoader;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messages {
    public static void send(CommandSender player, String messagePath) {
        String message = PluginLoader.instance.getConfig().getString(messagePath);
        if (!(player instanceof Player)) {
            if (message != null) {
                player.sendMessage(Messages.colorRefactor(message));
            } else {
                player.sendMessage("Message missing: " + messagePath);
            }
        } else {
            if (message != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            } else {
                player.sendMessage("Message missing: " + messagePath);
            }
        }
    }

    public static String colorRefactor(String input) {
        // Input string
        String strippedInput = input;

        for (int i = 0; i < strippedInput.toCharArray().length; i++) {
            // Character
            char character = strippedInput.charAt(i);
            // Index of character
            int index = i;

            // If character equals &
            if (character == '&') {
                // All colors
                for (ChatColor color : ChatColor.values()) {
                    // Temporary character index
                    int tempIndex = index;
                    // Possible color after &
                    char possibleColor = strippedInput.charAt(tempIndex + 1);
                    // If possible color is after &, then replace
                    if (possibleColor == color.getChar()) {
                        StringBuffer stringBuffer = new StringBuffer(strippedInput);
                        stringBuffer.setCharAt(index, '§');

                        strippedInput = stringBuffer.toString();
                    }
                }
            }
        }
        return strippedInput;
    }
}
