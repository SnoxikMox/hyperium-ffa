package com.snoxmox.hyperium.ffa.listener.world;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.arena.ArenaManager;
import com.snoxmox.hyperium.ffa.events.ArenaJoinEvent;
import com.snoxmox.hyperium.ffa.events.ArenaQuitEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public record PlayerWorldChangeListener(ArenaManager arenaManager) implements Listener {

    @EventHandler
    void onWorldChange(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final World fromWorld = event.getFrom();
        final World toWorld = player.getWorld();

        final Arena arenaOld = this.arenaManager.getArena(fromWorld);
        final Arena arenaNew = this.arenaManager.getArena(toWorld);

        if (arenaOld == null && arenaNew == null) {
            return;
        }
        if (arenaOld != null && arenaOld.equals(arenaNew)) {
            return;
        }
        if (arenaOld != null && arenaNew == null) {
            Bukkit.getPluginManager().callEvent(new ArenaQuitEvent(arenaOld, player));
            return;
        }
        Bukkit.getPluginManager().callEvent(new ArenaJoinEvent(arenaNew, player));
    }

}
