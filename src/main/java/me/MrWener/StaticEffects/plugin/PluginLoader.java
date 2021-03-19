package me.MrWener.StaticEffects.plugin;

import me.MrWener.StaticEffects.plugin.effects.EffectGroupManager;
import me.MrWener.StaticEffects.plugin.effects.StaticEffect;
import me.MrWener.StaticEffects.plugin.storage.DataSystem;
import me.MrWener.StaticEffects.plugin.storage.data.FlatFileDataSystem;
import me.MrWener.StaticEffects.plugin.storage.data.WebserverDataSystem;
import me.MrWener.StaticEffects.plugin.ui.commands.CommandInterface;
import me.MrWener.StaticEffects.plugin.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginLoader extends JavaPlugin {
    public static PluginLoader instance;
    public static EffectGroupManager manager;
    public static StaticEffect effects;
    public static DataSystem data;

    public static WebserverDataSystem webserver;
    public static FlatFileDataSystem flatfile;

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        instance = this;
        manager = new EffectGroupManager(instance);
        effects = new StaticEffect();

        flatfile = new FlatFileDataSystem(this);
        webserver = new WebserverDataSystem(this.getConfig().getString("data.webserver-url"), flatfile);

        if (this.getConfig().getBoolean("data.use-database")) {
            data = webserver;
            Logger.send("Using WebserverDataSystem!");
        } else {
            data = flatfile;
            Logger.send("Using FlatFileDataSystem!");
        }

        manager.onEnable();
        effects.onEnable();

        Bukkit.getPluginManager().registerEvents(effects, this);
        this.getCommand("staticeffects").setExecutor(new CommandInterface());
        this.getCommand("staticeffectsadmin").setExecutor(new CommandInterface());
        this.getCommand("seainfo").setExecutor(new CommandInterface());
        effects.runRefreshSequence();
    }

    @Override
    public void onDisable() {
        manager.onDisable();
        effects.onDisable();
    }

    public void runGarbageCollector() {
        System.gc();
    }
}
