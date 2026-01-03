package com.snoxmox.hyperium.ffa.arena;

import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.user.User;
import eu.okaeri.configs.OkaeriConfig;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Arena extends OkaeriConfig {

    private final String name;
    private String displayName;
    private World world;
    private Kit kit;
    private List<Location> spawnLocation = new ArrayList<>();


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

    public List<Location> getSpawnLocation() {
        return this.spawnLocation;
    }

    public void setSpawnLocation(final Location spawnLocation) {
        this.spawnLocation.add(spawnLocation);
    }

    public void userJoinArena(final Player player, final User user) {
        player.teleport(this.spawnLocation.get(RandomUtils.nextInt(0, this.spawnLocation.size())));
        player.getInventory().clear();
        user.setCurrentArena(this);
        user.equipKit(player, this.getKit());
    }

    public void userLeaveArena(final Player player, final User user) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        user.setCurrentArena(null);
    }
}
