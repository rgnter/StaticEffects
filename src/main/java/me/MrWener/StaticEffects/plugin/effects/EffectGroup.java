package me.MrWener.StaticEffects.plugin.effects;

import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public class EffectGroup {
    // Name of EffectGroup
    private String name;
    // Display name of EffectGroup
    private String displayName;
    // UUID of effect group
    private UUID uuid;

    // Effects of EffectGroup
    private List<PotionEffect> effects;
    // Aliases of EffectGroup
    private List<String> aliases;

    /**
     * Creates EffectGroup
     *
     * @param name        name of EffectGroup
     * @param displayName display name of EffectGroup
     * @param uuid        uuid of EffectGroup
     * @param effects     effects of EffectGroup
     * @param aliases     aliases of EffectGroup
     */
    public EffectGroup(String name, String displayName, UUID uuid, List<PotionEffect> effects, List<String> aliases) {
        this.name = name;
        this.displayName = displayName;
        this.uuid = uuid;
        this.effects = effects;
        this.aliases = aliases;
    }

    /**
     * @return name of EffectGroup
     */
    public String getName() {
        return name;
    }

    /**
     * @return uuid of EffectGroup
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return display name of EffectGroup
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return effects of EffectGroup
     */
    public List<PotionEffect> getEffects() {
        return effects;
    }

    /**
     * @return aliases of EffectGroup
     */
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
