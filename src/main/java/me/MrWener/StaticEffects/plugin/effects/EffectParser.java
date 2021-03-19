package me.MrWener.StaticEffects.plugin.effects;

import me.MrWener.StaticEffects.plugin.utils.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class EffectParser {
    public static List<PotionEffect> parseEffects(FileConfiguration config, String path) {
        List<PotionEffect> effects = new ArrayList<>();

        if (config.getConfigurationSection(path) != null) {
            for (String effectKey : config.getConfigurationSection(path).getKeys(false)) {
                String fullPath = path + "." + effectKey;

                String effectName = config.getString(fullPath + ".effect");

                // Spigot hasnt effect called HASTE, only fast digging.
                switch (effectName) {
                    case "haste":
                        effectName = PotionEffectType.FAST_DIGGING.getName();
                }

                int amplifier = config.getInt(fullPath + ".amplifier");

                boolean particles = config.getBoolean(fullPath + ".show-particles");
                boolean ambient = config.getBoolean(fullPath + ".ambient");

                try {
                    effects.add(new PotionEffect(PotionType.valueOf(effectName.toUpperCase()).getEffectType(), Integer.MAX_VALUE, amplifier, ambient, particles));
                } catch (java.lang.IllegalArgumentException x) {
                    try {
                        effects.add(new PotionEffect(PotionEffectType.getByName(effectName.toUpperCase()), Integer.MAX_VALUE, amplifier, ambient, particles));
                    } catch (IllegalArgumentException xd) {
                        Logger.send("Invalid effect name: " + effectName.toUpperCase() + "... Skipping!");
                    }
                }
            }
        }
        return effects;
    }

}
