package com.snoxmox.hyperium.ffa.arena;

import com.snoxmox.hyperium.ffa.configuration.ArenaConfiguration;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;

public class ArenaManager {

    private final List<Arena> arenas = new ArrayList<>();

    public ArenaManager(final ArenaConfiguration arenaConfiguration) {
        this.arenas.addAll(arenaConfiguration.getArenas());
        arenaConfiguration.clearArenas();
    }

    public Arena getArena(final String name) {
        return this.arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Arena getArena(final World world) {
        return this.arenas.stream().filter(arena -> arena.getWorld().equals(world)).findFirst().orElse(null);
    }

    public void createArena(final Arena arena) {
        this.arenas.add(arena);
    }

    public void removeArena(final Arena arena) {
        this.arenas.remove(arena);
    }

    public List<Arena> getArenas() {
        return this.arenas;
    }
}
