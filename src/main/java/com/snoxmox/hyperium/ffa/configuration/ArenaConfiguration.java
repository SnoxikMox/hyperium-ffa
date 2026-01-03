package com.snoxmox.hyperium.ffa.configuration;

import com.snoxmox.hyperium.ffa.arena.Arena;
import eu.okaeri.configs.OkaeriConfig;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

public class ArenaConfiguration extends OkaeriConfig {

    public Location globalSpawnLocation;

    private final List<Arena> arenas = new ArrayList<>();


    public void clearArenas() {
        this.arenas.clear();
    }

    public List<Arena> getArenas() {
        return this.arenas;
    }

}
