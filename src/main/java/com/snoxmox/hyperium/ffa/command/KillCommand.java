package com.snoxmox.hyperium.ffa.command;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.configuration.ArenaConfiguration;
import com.snoxmox.hyperium.ffa.user.User;
import com.snoxmox.hyperium.ffa.user.UserService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

@Command(name = "kill")
public record KillCommand(ArenaConfiguration arenaConfiguration, UserService userService) {

    @Execute()
    public void spawn(@Context final Player player) {
        final User user = this.userService.getUser(player.getUniqueId());
        final Arena arena = user.getCurrentArena();

        if (arena != null) {
            arena.userLeaveArena(player, user);
        }
        player.teleport(this.arenaConfiguration.globalSpawnLocation);
    }

    @Execute
    public void spawnPlayer(@Context final Player player, @Arg final Player target) {
        final User user = this.userService.getUser(target.getUniqueId());
        final Arena arena = user.getCurrentArena();

        if (arena != null) {
            arena.userLeaveArena(target, user);
        }
        target.teleport(this.arenaConfiguration.globalSpawnLocation);
    }

}
