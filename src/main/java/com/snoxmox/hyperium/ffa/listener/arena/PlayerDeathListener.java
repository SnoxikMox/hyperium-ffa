package com.snoxmox.hyperium.ffa.listener.arena;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.configuration.ArenaConfiguration;
import com.snoxmox.hyperium.ffa.configuration.MenuConfiguration;
import com.snoxmox.hyperium.ffa.configuration.MessageConfiguration;
import com.snoxmox.hyperium.ffa.user.User;
import com.snoxmox.hyperium.ffa.user.UserService;
import com.snoxmox.hyperium.ffa.user.stats.Statistic;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public record PlayerDeathListener(UserService userService, MessageConfiguration messageConfiguration, Plugin plugin, ArenaConfiguration arenaConfiguration, MenuConfiguration menuConfiguration) implements Listener {

    @EventHandler
    void onDeath(final PlayerDeathEvent event) {
        final Player player = event.getPlayer();
        event.deathMessage(Component.empty());
        event.getDrops().clear();
        event.setDroppedExp(0);

        final User user = this.userService.getUser(player.getUniqueId());
        if (user.getCurrentArena() == null) return;
        if (this.arenaConfiguration.globalSpawnLocation == null)
            this.arenaConfiguration.globalSpawnLocation = Bukkit.getWorlds().getFirst().getSpawnLocation();
        this.arenaConfiguration.save();
        final Arena arena = user.getCurrentArena();
        user.setLastArena(arena);
        user.setCurrentArena(null);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            player.spigot().respawn();
            player.setFireTicks(0);
            player.teleport(this.arenaConfiguration.globalSpawnLocation);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.getInventory().setItem(4, this.menuConfiguration().respawnItem);
        }, 1L);
    }


    @EventHandler
    void playerInteractRespawnItem(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final User user = this.userService.getUser(player.getUniqueId());
        if (user.getCurrentArena() != null) return;
        if (event.getItem() == null) return;
        if (!event.getItem().isSimilar(this.menuConfiguration.respawnItem)) return;
        final Arena lastArena = user.getLastArena();
        if (lastArena == null) {
            return;
        }
        lastArena.userJoinArena(player, user);
    }

    @EventHandler
    void givePoints(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();

        if (killer == null) return;

        killer.setHealth(killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        killer.setFoodLevel(20);

        final User kUser = this.userService.getUser(killer.getUniqueId());
        final Arena arena = kUser.getCurrentArena();
        if (arena == null) return;
        kUser.equipKit(killer, arena.getKit());
        final String arenaName = arena.getDisplayName() != null ? arena.getDisplayName() : "Domyślna nazwa";
        final String kitName = (arena.getKit() != null && arena.getKit().getDisplayName() != null)
                ? arena.getKit().getDisplayName()
                : "Dośyślna nazwa";
        final Map<String, String> ph = Map.of(
                "victim", player.getName(),
                "killer", killer.getName(),
                "victim_health", String.format("%.1f", Math.max(0.0, player.getHealth())),
                "killer_health", String.format("%.1f", Math.max(0.0, killer.getHealth())),
                "arena", arenaName,
                "kit", kitName
        );
        Bukkit.getOnlinePlayers().forEach(online -> this.messageConfiguration.killBroadcast.send(online, ph));
    }

}
