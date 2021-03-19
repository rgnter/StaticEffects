package me.MrWener.StaticEffects.plugin.ui.commands;

import com.sun.istack.internal.NotNull;
import me.MrWener.StaticEffects.plugin.PluginLoader;
import me.MrWener.StaticEffects.plugin.effects.EffectGroup;
import me.MrWener.StaticEffects.plugin.storage.DataSystem;
import me.MrWener.StaticEffects.plugin.storage.data.WebserverDataSystem;
import me.MrWener.StaticEffects.plugin.ui.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandInterface implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("staticeffects")) {
            if (args.length <= 0) {
                // Any arguments
                commandSender.sendMessage("§8:: §c§lStaticEffects §8:: §av" + PluginLoader.instance.getDescription().getVersion());
                commandSender.sendMessage("§7Subcommands:");
                commandSender.sendMessage("§7/se <name> - Will give you group!");
                commandSender.sendMessage("§7/se <name> <player> - Will give you group to specified player!");
            } else {
                if (args.length > 0 && args.length < 2) {
                    String kit = args[0];
                    if (!(commandSender instanceof Player)) {
                        Messages.send(commandSender, "messages.not-an-entity");
                        return true;
                    }
                    Player player = (Player) commandSender;

                    EffectGroup selectedKit;
                    selectedKit = PluginLoader.manager.getGroup(kit);

                    if (selectedKit == null && (!PluginLoader.manager.isGroupValid(selectedKit))) {
                        Messages.send(player, "messages.group-not-found");
                        return true;
                    }
                    if (PluginLoader.effects.containsPlayer(player.getUniqueId())) {
                        PluginLoader.effects.removeEffectsFromKit(player.getUniqueId());
                    }
                    PluginLoader.effects.addPlayer(player.getUniqueId(), selectedKit);
                    Messages.send(player, "messages.group-set");
                    return true;
                }
                if (args.length > 1 && args.length < 3) {
                    String kit = args[0];

                    Player player = Bukkit.getPlayer(args[1]);
                    if (!Bukkit.getOnlinePlayers().contains(player)) {
                        Messages.send(commandSender, "messages.player-not-found");
                        return true;
                    }

                    EffectGroup selectedKit = PluginLoader.manager.getGroup(kit);
                    if (selectedKit == null && (!PluginLoader.manager.isGroupValid(selectedKit))) {
                        Messages.send(commandSender, "messages.group-not-found");
                        return true;
                    }

                    PluginLoader.effects.addPlayer(player.getUniqueId(), selectedKit);
                    Messages.send(commandSender, "messages.group-set");
                    return true;
                }
            }
        }

        if (command.getName().equalsIgnoreCase("staticeffectsadmin")) {
            if (!commandSender.hasPermission("staticeffects.admin")) {
                commandSender.sendMessage("§cYou don't have permission!");
                return true;
            }
            if (args.length <= 0) {
                commandSender.sendMessage("§8:: §c§lStaticEffects Admin §8:: §av" + PluginLoader.instance.getDescription().getVersion());
                commandSender.sendMessage("§7Subcommands:");
                commandSender.sendMessage("§e/sea §aloadFromDatabase §8- §7Will load datas from database");
                commandSender.sendMessage("§e/sea §aloadFromFlatFile §8- §7Will load datas from flatfile");
                commandSender.sendMessage("§e/sea §asaveToDatabase §8- §7Will save datas to database");
                commandSender.sendMessage("§e/sea §asaveToFlatFile §8- §7Will save datas to flatfile");
                commandSender.sendMessage("§e/sea §asavePlayer §c<player_name> §8- §7Will save player to current data system.");
                commandSender.sendMessage("§e/sea §aloadPlayer §c<player_name> §8- §7Will load player from current data system to cache.");
                commandSender.sendMessage("§e/sea §aremovePlayer §c<player_name> §8- §7Will remove player from current data system and cache.");
                commandSender.sendMessage("§e/sea §achangeDataSystem §c<player_name> §8- §7Will switch data systems.");
                commandSender.sendMessage("§e/sea §aloadPlayer §c<player_name §8- §7Loads player from data system");
                commandSender.sendMessage("§e/sea §asavePlayaer §c<player_name §8- §7Saves player to data system");
                commandSender.sendMessage("§f");
                commandSender.sendMessage("§e/sea §areload §8- §7Reloads config");
                commandSender.sendMessage("§e/sea §achangeDataSystem §8- §7Changes data system");
                commandSender.sendMessage("§e/sea §acurrentDataSystem §8- §7Shows current data system");
                commandSender.sendMessage("§e/sea §aruntime §8- §7Shows runtime informations");
                commandSender.sendMessage("§e/sea §ainfo §8- §7Shows cache informations");
                commandSender.sendMessage("§e/sea §awebserverStatus §8- §7Checks if webserver is operative");
                commandSender.sendMessage("§e/sea §agc §8- §7Explicitly run garbage collector");
            } else {
                //command <0> <1> <2>
                String subCommand = args[0];
                if (subCommand.equalsIgnoreCase("loadFromDatabase")) {
                    commandSender.sendMessage("§cThis may overwrite old data.");
                    loadFromDatabase();
                    commandSender.sendMessage("§aLoaded " + PluginLoader.effects.getEffectedPlayers().size() + " players from Database");
                    return true;
                } else if (subCommand.equalsIgnoreCase("loadFromFlatFile")) {
                    commandSender.sendMessage("§cThis may overwrite old data.");
                    loadFromFlatFile();
                    commandSender.sendMessage("§aLoaded " + PluginLoader.effects.getEffectedPlayers().size() + " players from FlatFile");
                    return true;
                } else if (subCommand.equalsIgnoreCase("saveToDatabase")) {
                    commandSender.sendMessage("§cThis may overwrite old data.");
                    boolean success = saveToDatabase();
                    if (success) {
                        commandSender.sendMessage("§aSaved " + PluginLoader.effects.getEffectedPlayers().size() + " players to Database");
                    } else {
                        commandSender.sendMessage("§cCannot save to database.");
                    }
                    return true;
                } else if (subCommand.equalsIgnoreCase("saveToFlatFile")) {
                    commandSender.sendMessage("§cThis may overwrite old data.");
                    boolean success = saveToFlatFile();
                    if (success) {
                        commandSender.sendMessage("§aSaved " + PluginLoader.effects.getEffectedPlayers().size() + " players to Database");
                    } else {
                        commandSender.sendMessage("§cCannot save to flatfile.");
                    }
                    return true;
                } else if (subCommand.equalsIgnoreCase("savePlayer")) {
                    String name;
                    if (args.length < 2) {
                        commandSender.sendMessage("§cSpecify name of player!");
                        return true;
                    }
                    name = args[1];
                    if (Bukkit.getPlayer(name) == null) {
                        commandSender.sendMessage("§cPlayer is not online!");
                        return true;
                    }
                    if (!PluginLoader.effects.containsPlayer(Bukkit.getPlayer(name).getUniqueId())) {
                        commandSender.sendMessage("§cPlayer does not have any group!");
                        return true;
                    }
                    commandSender.sendMessage("§cThis may overwrite player's old data.");

                    String response = savePlayer(name, PluginLoader.data);
                    commandSender.sendMessage("§a" + response);
                    return true;
                } else if (subCommand.equalsIgnoreCase("loadPlayer")) {
                    String name;
                    if (args.length < 2) {
                        commandSender.sendMessage("§cSpecify name of player!");
                        return true;
                    }
                    name = args[1];
                    if (Bukkit.getPlayer(name) == null) {
                        commandSender.sendMessage("§cPlayer is not online!");
                        return true;
                    }
                    commandSender.sendMessage("§cThis may overwrite player's current data.");

                    String response = loadPlayer(name, PluginLoader.data);
                    commandSender.sendMessage("§a" + response);
                    return true;
                } else if (subCommand.equalsIgnoreCase("removePlayer")) {
                    String name;
                    if (args.length < 2) {
                        commandSender.sendMessage("§cSpecify name of player!");
                        return true;
                    }
                    name = args[1];
                    if (Bukkit.getPlayer(name) == null) {
                        commandSender.sendMessage("§cPlayer is not online!");
                        return true;
                    }
                    commandSender.sendMessage("§cThis may overwrite player's old data.");
                    String response = removePlayer(name, PluginLoader.data);
                    commandSender.sendMessage("§a" + response);
                    return true;
                } else if (subCommand.equalsIgnoreCase("hasGroup")) {
                    String name;
                    if (args.length < 2) {
                        commandSender.sendMessage("§cSpecify name of player!");
                        return true;
                    }
                    name = args[1];
                    if (Bukkit.getPlayer(name) == null) {
                        commandSender.sendMessage("§cPlayer is not online!");
                        return true;
                    }
                    Player player = Bukkit.getPlayer(name);
                    if (PluginLoader.effects.containsPlayer(player.getUniqueId())) {
                        commandSender.sendMessage("§a" + name + " has group named: " + PluginLoader.effects.getPlayerGroup(player.getUniqueId()).getName());
                    } else if (PluginLoader.data.containsPlayer(player.getUniqueId())) {
                        commandSender.sendMessage("§a" + name + " is saved in data, but not in cache.");
                    } else {
                        commandSender.sendMessage("§cNope.");
                    }
                    return true;
                } else if (subCommand.equalsIgnoreCase("changeDataSystem") || subCommand.equalsIgnoreCase("switchDataSystem")) {
                    String response = changeDataSystem(PluginLoader.data);
                    commandSender.sendMessage("§a" + response);
                    return true;
                } else if (subCommand.equalsIgnoreCase("info")) {
                    commandSender.sendMessage("IP: " + Bukkit.getServer().getIp());
                    try {
                        commandSender.sendMessage("WebServer IP: " + ((WebserverDataSystem) PluginLoader.webserver).getUrl());
                    } catch (Exception x) {
                        commandSender.sendMessage("WebServer IP: N/A");
                    }
                    commandSender.sendMessage("§7DataSystem: §a" + PluginLoader.data.getDataSystemName());
                    commandSender.sendMessage("§7Cached players size: §a" + PluginLoader.effects.getEffectedPlayers().size());
                    commandSender.sendMessage("§7Saved players size: §a" + PluginLoader.data.loadData().size());
                    return true;
                } else if (subCommand.equalsIgnoreCase("runtime")) {
                    commandSender.sendMessage("§f");
                    commandSender.sendMessage("§aProcessor: ");
                    commandSender.sendMessage("§7Available processors: §a" + Runtime.getRuntime().availableProcessors());
                    commandSender.sendMessage("§f");
                    commandSender.sendMessage("§aRAM: ");
                    commandSender.sendMessage("§7Total memory: §a" + (Runtime.getRuntime().totalMemory() % 1012) + "mb");
                    commandSender.sendMessage("§7Max memory: §a" + (Runtime.getRuntime().maxMemory() % 1012) + "mb");
                    commandSender.sendMessage("§7Free memory: §a" + (Runtime.getRuntime().freeMemory() % 1012) + "mb");
                    commandSender.sendMessage("§f");
                    return true;
                } else if (subCommand.equalsIgnoreCase("reload")) {
                    PluginLoader.instance.reloadConfig();
                    commandSender.sendMessage("§aConfig reloaded");
                    PluginLoader.instance.runGarbageCollector();
                    return true;
                } else if (subCommand.equalsIgnoreCase("gc")) {
                    PluginLoader.instance.runGarbageCollector();
                    commandSender.sendMessage("§aGC completed.");
                    return true;
                } else if (subCommand.equalsIgnoreCase("currentDataSystem") || subCommand.equalsIgnoreCase("cds")) {
                    commandSender.sendMessage("§aCurrent DataSystem: " + PluginLoader.data.getDataSystemName());
                    return true;
                } else if (subCommand.equalsIgnoreCase("webserverStatus")) {
                    if (PluginLoader.data.getType() != DataSystem.DataSystemType.WEBSEVRER) {
                        commandSender.sendMessage("§cCurrent data system is not WebServer... You can't use this command.");
                        return true;
                    }
                    commandSender.sendMessage("§7Operative: " + (PluginLoader.webserver.isOperative() ? "§ayes§7." : "§cnope§7."));
                } else {
                    commandSender.sendMessage("§cCommand not found!");
                }
            }
        }
        return true;
    }

    private void loadFromDatabase() {
        // Saving flatfile data to database
        PluginLoader.effects.setEffectedPlayers(PluginLoader.webserver.loadData());
    }

    private void loadFromFlatFile() {
        // Saving database data to flatfile
        PluginLoader.effects.setEffectedPlayers(PluginLoader.flatfile.loadData());
    }

    private boolean saveToDatabase() {
        // Saving flatfile data to database
        return PluginLoader.webserver.saveData(PluginLoader.effects.getEffectedPlayers());
    }

    private boolean saveToFlatFile() {
        // Saving database data to flatfile
        return PluginLoader.flatfile.saveData(PluginLoader.effects.getEffectedPlayers());
    }

    private String savePlayer(@NotNull String name, @NotNull DataSystem system) {
        UUID uuid = Bukkit.getPlayer(name).getUniqueId();
        return system.saveUser(uuid, PluginLoader.effects.getPlayerGroup(uuid).getUuid());
    }

    private String loadPlayer(@NotNull String name, @NotNull DataSystem system) {
        UUID uuid = Bukkit.getPlayer(name).getUniqueId();
        return system.loadUser(uuid);
    }

    private String removePlayer(@NotNull String name, @NotNull DataSystem system) {
        UUID uuid = Bukkit.getPlayer(name).getUniqueId();
        return system.removeUser(uuid);
    }

    private String changeDataSystem(@NotNull DataSystem system) {
        return system.switchDataSystem();
    }
}
