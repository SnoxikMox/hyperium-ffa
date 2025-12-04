package com.snoxmox.hyperium.ffa.configuration;

import com.snoxmox.hyperium.ffa.arena.Arena;
import eu.okaeri.configs.OkaeriConfig;
import java.util.ArrayList;
import java.util.List;

public class ArenaConfiguration extends OkaeriConfig {

    private final List<Arena> arenas = new ArrayList<>();


    public void clearArenas() {
        this.arenas.clear();
    }

    public List<Arena> getArenas() {
        return this.arenas;
    }

}
