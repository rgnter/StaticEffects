package me.MrWener.StaticEffects.plugin.storage;

import me.MrWener.StaticEffects.plugin.PluginLoader;
import me.MrWener.StaticEffects.plugin.effects.EffectGroup;
import me.MrWener.StaticEffects.plugin.storage.data.WebserverDataSystem;

import java.util.Map;
import java.util.UUID;

public interface DataSystem {

    enum DataSystemType {
        WEBSEVRER, FLATFILE
    }

    boolean saveData(Map<UUID, EffectGroup> data);

    Map<UUID, EffectGroup> loadData();

    String saveUser(UUID uuid, UUID group);

    String removeUser(UUID uuid);

    String loadUser(UUID uuid);

    boolean containsPlayer(UUID uuid);

    String getDataSystemName();

    default String switchDataSystem() {
        DataSystem current = PluginLoader.data;

        if (current.getClass() == WebserverDataSystem.class) {
            DataSystem changed = PluginLoader.flatfile;
            //saving to current datasystem
            current.saveData(PluginLoader.effects.getEffectedPlayers());

            //saving to changed datasystem
            Map<UUID, EffectGroup> merged = PluginLoader.effects.getEffectedPlayers();
            merged.putAll(changed.loadData());
            current.saveData(merged);

            // change config
            PluginLoader.instance.getConfig().set("data.use-database", false);
            PluginLoader.instance.saveConfig();

            // switching
            PluginLoader.data = changed;
            return "DataSystem from " + current.getDataSystemName() + " changed to " + changed.getDataSystemName();
        } else {
            DataSystem changed = PluginLoader.webserver;
            //saving to current datasystem
            current.saveData(PluginLoader.effects.getEffectedPlayers());

            //saving to changed datasystem
            Map<UUID, EffectGroup> merged = PluginLoader.effects.getEffectedPlayers();
            merged.putAll(changed.loadData());
            current.saveData(merged);
            // change config
            PluginLoader.instance.getConfig().set("data.use-database", true);
            PluginLoader.instance.saveConfig();
            // switching
            PluginLoader.data = changed;
            return "DataSystem from " + current.getDataSystemName() + " changed to " + changed.getDataSystemName();
        }
    }

    DataSystemType getType();

}
