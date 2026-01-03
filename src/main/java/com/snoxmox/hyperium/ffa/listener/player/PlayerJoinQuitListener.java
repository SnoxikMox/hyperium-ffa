package com.snoxmox.hyperium.ffa.listener.player;

import com.snoxmox.hyperium.ffa.clan.war.War;
import com.snoxmox.hyperium.ffa.clan.war.WarService;
import com.snoxmox.hyperium.ffa.scoreboard.WarScoreboardManager;
import com.snoxmox.hyperium.ffa.user.User;
import com.snoxmox.hyperium.ffa.user.UserService;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerJoinQuitListener(UserService userService) implements Listener {

    @EventHandler
    void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.userService.loadUser(player.getUniqueId()).join();
    }

    @EventHandler
    void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.userService.saveUserOnQuit(player.getUniqueId()).join();
    }

}
