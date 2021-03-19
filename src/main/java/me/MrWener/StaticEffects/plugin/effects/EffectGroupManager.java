package me.MrWener.StaticEffects.plugin.effects;

import me.MrWener.StaticEffects.plugin.Integral;
import me.MrWener.StaticEffects.plugin.PluginLoader;
import me.MrWener.StaticEffects.plugin.utils.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EffectGroupManager implements Integral {
    private PluginLoader plugin;
    private FileConfiguration config;
    private ConfigurationSection sectionOfGroups;

    private Map<UUID, EffectGroup> groups = new HashMap<>();

    public EffectGroupManager(PluginLoader plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.sectionOfGroups = config.getConfigurationSection("groups");
    }

    /**
     * Loads all kits from Configuration
     */
    public void loadGroups() {
        // Count of invalid kits
        int invalid = 0;
        try {
            // Searching for all kits
            for (String key : sectionOfGroups.getKeys(false)) {
                EffectGroup group = getGroupFromConfig(key);
                if (isGroupValid(group)) {
                    registerGroup(group);
                } else {
                    invalid++;
                }
            }
            Logger.send("Found " + groups.size() + " group/s");
            Logger.send("Found " + invalid + " invalid groups!");
        } catch (NullPointerException ex) {
            // When configuration is empty it throws NullPointerException
            Logger.send("Not found any kits!");
        }
    }

    /**
     * Clears kits map
     */
    public void unloadGroups() {
        groups.clear();
    }

    /**
     * Loads kit from config
     *
     * @param name name of Kit
     * @return
     */
    public EffectGroup getGroupFromConfig(String name) {
        if (config.getConfigurationSection(sectionOfGroups.getCurrentPath() + "." + name) == null) {
            // If group does not exists returns null
            return null;
        }
        String displayName = config.getString
                (sectionOfGroups.getCurrentPath() + "." + name + ".display-name");
        // TODO Effects
        List<PotionEffect> effects = EffectParser.parseEffects(config, sectionOfGroups.getCurrentPath() + "." + name + ".effects");

        List<String> aliases = config.getStringList
                (sectionOfGroups.getCurrentPath() + "." + name + ".aliases");

        UUID uuid = null;
        try {
            uuid = UUID.fromString(config.getString(sectionOfGroups.getCurrentPath() + "." + name + ".uuid"));
        } catch (IllegalArgumentException x) {
            Logger.send("Invalid UUID for " + name + " generating new one!");
            uuid = UUID.randomUUID();
        }

        EffectGroup finalGroup = new EffectGroup(name, displayName, uuid, effects, aliases);

        if (isGroupValid(finalGroup) && finalGroup != null) {
            // if kit is valid
            return finalGroup;
        } else {
            return null;
        }
    }

    /**
     * @param name Name of group
     * @return EffectGroup from kits map
     */
    public EffectGroup getGroup(String name) {
        for (UUID groupUUID : groups.keySet()) {
            if (groups.get(groupUUID).getName().equalsIgnoreCase(name)) {
                return groups.get(groupUUID);
            }
        }
        return getGroupByAlias(name);
    }

    public EffectGroup getGroup(UUID uuid) {
        return groups.get(uuid);
    }

    /**
     * Searching kit with same alias
     *
     * @param alias Alias of kit
     * @return EffectGroup with alias
     */
    public EffectGroup getGroupByAlias(String alias) {
        for (UUID groupUUID : groups.keySet()) {
            if (groups.get(groupUUID).getAliases().contains(alias)) {
                return groups.get(groupUUID);
            }
        }
        return null;
    }

    /**
     * Creates kit
     *
     * @param name        Name of kit
     * @param displayName Display name of kit
     * @param effects     Effects of kit
     * @param aliases     Aliases of kit
     * @return EffectGroup
     */
    public EffectGroup createGroup(String name, String displayName, List<PotionEffect> effects, List<String> aliases) {
        return new EffectGroup(name, displayName, generateUUID(), effects, aliases);
    }

    /**
     * Register kit to kits map
     *
     * @param group EffectGroup that will be registered
     */
    public void registerGroup(EffectGroup group) {
        groups.put(group.getUuid(), group);
        config.set(sectionOfGroups.getCurrentPath() + "." + group.getName() + ".display-name", group.getDisplayName());
        config.set(sectionOfGroups.getCurrentPath() + "." + group.getName() + ".uuid", group.getUuid().toString());
        config.set(sectionOfGroups.getCurrentPath() + "." + group.getName() + ".aliases", group.getAliases());
        config.set(sectionOfGroups.getCurrentPath() + "." + group.getName() + ".effects", group.getEffects());
        plugin.saveDefaultConfig();
    }

    /**
     * Removing kit from config and kits map
     *
     * @param name
     */
    public void removeGroup(String name) {
        config.set(sectionOfGroups.getCurrentPath() + "." + name, null);
        PluginLoader.instance.saveDefaultConfig();

        groups.remove(name);
    }

    /**
     * @param name
     * @return
     */
    public boolean groupExist(String name) {
        if (config.isSet("kits." + name) && getGroup(name) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isGroupValid(EffectGroup group) {
        try {
            if (group != null) {
                if (group.getName() != null) {
                    if (group.getUuid() != null) {
                        return true;
                    }
                }
            }
        } catch (NullPointerException ex) {
            return false;
        }
        return false;
    }

    public UUID generateUUID() {
        return UUID.randomUUID();
    }

    @Override
    public void onEnable() {
        Logger.send("Loading groups...");
        loadGroups();
        Logger.send("Done!");

    }

    @Override
    public void onDisable() {
        Logger.send("Unloading groups...");
        unloadGroups();
        Logger.send("Done!");
    }


}
