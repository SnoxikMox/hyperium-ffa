package com.snoxmox.hyperium.ffa.listener;

import com.snoxmox.hyperium.ffa.user.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerJoinQuitListener(UserService userService) implements Listener {

    @EventHandler
    void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.userService.loadUser(player.getUniqueId());
    }

    @EventHandler
    void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.userService.saveUser(player.getUniqueId());
    }

}
