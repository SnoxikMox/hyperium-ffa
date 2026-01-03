package com.snoxmox.hyperium.ffa.command;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.arena.ArenaManager;
import com.snoxmox.hyperium.ffa.configuration.MessageConfiguration;
import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.user.UserService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "arena", aliases = {"arenas", "ffa"})
@Permission("hyperium.command.arena")
public record ArenaCommand(ArenaManager arenaManager, UserService userService, MessageConfiguration messageConfiguration) {


    @Execute(name = "create")
    public void createArena(@Context final Player player, @Arg("nazwa areny") final String name) {
        if (this.arenaManager.getArena(name) != null) {
            this.messageConfiguration.arenaAlreadyExists.send(player);
            return;
        }

        final Arena arena = new Arena(name);
        arena.setWorld(player.getWorld());
        arena.setSpawnLocation(player.getLocation());

        this.arenaManager.createArena(arena);
        this.messageConfiguration.arenaCreated.send(player, "name", name);
    }

    @Execute(name = "delete")
    public void deleteArena(@Context final Player player, @Arg final Arena arena) {
        this.arenaManager.removeArena(arena);
        this.messageConfiguration.arenaDeleted.send(player, "name", arena.getName());
    }

    @Execute(name = "setspawn")
    public void setSpawn(@Context final Player player, @Arg final Arena arena) {
        final Location location = player.getLocation();
        arena.setSpawnLocation(location);
        arena.setWorld(location.getWorld());
        this.messageConfiguration.arenaSetSpawn.send(player, "name", arena.getName());
    }

    @Execute(name = "list")
    public void listArenas(@Context final Player player) {
        final MessageConfiguration msg = this.messageConfiguration;
        msg.arenaListHeader.send(player);
        for (final Arena arena : this.arenaManager.getArenas()) {
            msg.arenaListEntry.send(player, "name", arena.getName());
        }
    }

    @Execute(name = "teleport")
    public void teleportPlayerToArena(@Context final CommandSender player, @Arg final Arena arena, @Arg final Player target) {
        if (arena.getSpawnLocation() == null) {
            this.messageConfiguration.arenaNoSpawn.send(player);
            return;
        }
        arena.userJoinArena(target, this.userService.getUser(target.getUniqueId()));
        target.teleport(arena.getSpawnLocation().getFirst());


    }

    @Execute(name = "teleport")
    public void teleport(@Context final Player player, @Arg final Arena arena) {
        if (arena.getSpawnLocation() == null) {
            this.messageConfiguration.arenaNoSpawn.send(player);
            return;
        }
        arena.userJoinArena(player, this.userService.getUser(player.getUniqueId()));
        player.teleport(arena.getSpawnLocation().getFirst());
        this.messageConfiguration.arenaTeleportedSelf.send(player, "name", arena.getName());
    }

    @Execute(name = "leave")
    public void leaveArena(@Context final Player player) {
        final Arena arena = this.userService.getUser(player.getUniqueId()).getCurrentArena();
        arena.userLeaveArena(player, this.userService.getUser(player.getUniqueId()));
        this.messageConfiguration.arenaLeft.send(player);
    }

    @Execute(name = "setkit")
    public void setKit(@Context final Player player, @Arg final Arena arena, @Arg final Kit kit) {
        arena.setKit(kit);
        this.messageConfiguration.arenaSetKit.send(player, "name", arena.getName());
    }

    @Execute(name = "setdisplayname")
    public void setDisplayName(@Context final Player player, @Arg final Arena arena, @Arg("wyswietlana nazwa") final List<String> displayName) {
        arena.setDisplayName(String.join(" ", displayName));
        this.messageConfiguration.arenaSetDisplayName.send(player, "name", arena.getName());
    }

    @Execute(name = "info")
    public void info(@Context final Player player, @Arg final Arena arena) {
        final MessageConfiguration msg = this.messageConfiguration;
        msg.arenaInfoName.send(player, "name", arena.getName());
        msg.arenaInfoWorld.send(player, "world", arena.getWorld().getName());
        msg.arenaInfoSpawn.send(player, Map.of(
                "x", String.valueOf(arena.getSpawnLocation().getFirst().getBlockX()),
                "y", String.valueOf(arena.getSpawnLocation().getFirst().getBlockY()),
                "z", String.valueOf(arena.getSpawnLocation().getFirst().getBlockZ())
        ));
        msg.arenaInfoKit.send(player, "kit", arena.getKit().getName());
        msg.arenaInfoPlayers.send(player, "count", String.valueOf(arena.getWorld().getPlayers().size()));
    }

}