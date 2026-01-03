package com.snoxmox.hyperium.ffa.listener.arena;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.events.ArenaJoinEvent;
import com.snoxmox.hyperium.ffa.events.ArenaQuitEvent;
import com.snoxmox.hyperium.ffa.user.User;
import com.snoxmox.hyperium.ffa.user.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record ArenaJoinQuitListener(UserService userService) implements Listener {

    @EventHandler
    void arenaJoinEvent(final ArenaJoinEvent event) {
        final Player player = event.getPlayer();
        final Arena arena = event.getArena();
        final User user = this.userService.getUser(player.getUniqueId());
        arena.userJoinArena(player, user);

    }

    @EventHandler
    void arenaQuitEvent(final ArenaQuitEvent event) {
        final Player player = event.getPlayer();
        final User user = this.userService.getUser(player.getUniqueId());
        final Arena arena = user.getCurrentArena();
        arena.userLeaveArena(player, user);
    }

}
