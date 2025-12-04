package com.snoxmox.hyperium.ffa.arena;

import com.snoxmox.hyperium.ffa.kit.Kit;
import eu.okaeri.configs.OkaeriConfig;
import org.bukkit.World;

public class Arena extends OkaeriConfig {

    private final String name;
    private String displayName;
    private World world;
    private Kit kit;


    public Arena(final String name) {
        this.name = name;
    }

    public Kit getKit() {
        return this.kit;
    }

    public void setKit(final Kit kit) {
        this.kit = kit;
    }

    public World getWorld() {
        return this.world;
    }

    public void setWorld(final World world) {
        this.world = world;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return this.name;
    }
}
