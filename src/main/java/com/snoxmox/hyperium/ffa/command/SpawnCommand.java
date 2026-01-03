package com.snoxmox.hyperium.ffa.command;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.configuration.ArenaConfiguration;
import com.snoxmox.hyperium.ffa.user.User;
import com.snoxmox.hyperium.ffa.user.UserService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

@Command(name = "spawn")
public record SpawnCommand(ArenaConfiguration arenaConfiguration, UserService userService) {

    @Execute()
    public void spawn(@Context final Player player) {
        final User user = this.userService.getUser(player.getUniqueId());
        final Arena arena = user.getCurrentArena();

        if (arena != null) {
            arena.userLeaveArena(player, user);
        }
        player.teleport(this.arenaConfiguration.globalSpawnLocation);
    }

    @Execute(name = "set")
    @Permission("hyperium.command.spawn.set")
    public void setSpawn(@Context final Player player) {
        this.arenaConfiguration.globalSpawnLocation = player.getLocation();
        player.sendMessage("Spawn  set");
    }

}
