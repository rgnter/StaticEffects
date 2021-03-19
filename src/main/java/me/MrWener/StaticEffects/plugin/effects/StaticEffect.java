package me.MrWener.StaticEffects.plugin.effects;

import me.MrWener.StaticEffects.plugin.Integral;
import me.MrWener.StaticEffects.plugin.PluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaticEffect implements Integral, Listener {

    private Map<UUID, EffectGroup> effectedPlayers = new HashMap<>();

    /**
     * Removing player from Map
     *
     * @param uuid UUID of player
     */
    public void removePlayer(UUID uuid) {
        if (containsPlayer(uuid)) {
            removeEffectsFromKit(uuid);
            effectedPlayers.remove(uuid);
            if (containsPlayer(uuid)) {
                // recursion removing all uuids (if there are 2 of them with same UUID)
                removePlayer(uuid);
            }
        }
    }

    /**
     * Adding player to Map
     *
     * @param uuid  UUID of player
     * @param group player's Group
     */
    public void addPlayer(UUID uuid, EffectGroup group) {
        if (containsPlayer(uuid)) {
            removePlayer(uuid);
            addPlayer(uuid, group);
        } else {
            effectedPlayers.put(uuid, group);
            playEffectsFromKit(uuid);
        }
    }

    /**
     * @param uuid UUID of player
     * @return player's group
     */
    public EffectGroup getPlayerGroup(UUID uuid) {
        if (containsPlayer(uuid)) {
            return effectedPlayers.get(uuid);
        }
        return null;
    }


    public boolean containsPlayer(UUID uuid) {
        return effectedPlayers.containsKey(uuid);
    }

    /**
     * Plays effect to player
     *
     * @param uuid Players uuid
     */
    public void playEffectsFromKit(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        // Player is not online
        if (player == null) {
            return;
        }
        EffectGroup kit = getPlayerGroup(uuid);
        for (PotionEffect effect : kit.getEffects()) {
            for (PotionEffect playerEffect : player.getActivePotionEffects()) {
                if (playerEffect.getType().equals(effect.getType())) {
                    player.removePotionEffect(playerEffect.getType());
                }
            }
            player.addPotionEffect(effect);
        }
    }

    public void removeEffectsFromKit(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        // Player is not online
        if (player == null) {
            return;
        }
        EffectGroup kit = getPlayerGroup(uuid);
        if (kit == null) {
            return;
        }
        for (PotionEffect effect : kit.getEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    @Override
    public void onEnable() {
        setEffectedPlayers(PluginLoader.data.loadData());
    }

    @Override
    public void onDisable() {
        PluginLoader.data.saveData(getEffectedPlayers());
    }

    public Map<UUID, EffectGroup> getEffectedPlayers() {
        return effectedPlayers;
    }

    public void setEffectedPlayers(Map<UUID, EffectGroup> data) {
        effectedPlayers = data;
    }

    /**
     * Plays effect on death
     *
     * @param ev
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent ev) {
        if (containsPlayer(ev.getPlayer().getUniqueId())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(PluginLoader.instance, new Runnable() {
                @Override
                public void run() {
                    playEffectsFromKit(ev.getPlayer().getUniqueId());
                }
            }, (1 * 2));
        }
    }

    /**
     * Disables using another potion, preceding duplication
     *
     * @param ev
     */
    @EventHandler
    public void onPotionUse(PlayerItemConsumeEvent ev) {
        ItemStack item = ev.getItem();
        if (containsPlayer(ev.getPlayer().getUniqueId())) {
            // potion
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                for (PotionEffect effect : getPlayerGroup(ev.getPlayer().getUniqueId()).getEffects()) {
                    if (meta.getBasePotionData().getType().toString().equalsIgnoreCase(effect.getType().getName())) {
                        ev.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    /**
     * @param event
     */
    @EventHandler
    public void onPotion(PotionSplashEvent event) {
        for (Entity entity : event.getAffectedEntities()) {
            // Itz Hrač
            if (entity instanceof Player) {
                // Instance Hrača
                Player player = (Player) entity;
                // Ak ma group
                if (containsPlayer(player.getUniqueId())) {
                    // Jeho potiony
                    for (PotionEffect thrownEffect : event.getPotion().getEffects()) {
                        // Potion ktory na nho bol hodeny
                        for (PotionEffect staticEffect : getPlayerGroup(player.getUniqueId()).getEffects()) {
                            // ak su zhodne zablokuje sa
                            if (staticEffect.getType() == thrownEffect.getType()) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }


    public void runRefreshSequence() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PluginLoader.instance, new Runnable() {
            @Override
            public void run() {
                try {
                    for (UUID player : getEffectedPlayers().keySet()) {
                        playEffectsFromKit(player);
                    }
                } catch (Exception x) {

                }
            }
        }, (5 * 20), (5 * 20));
    }
}
