package com.snoxmox.hyperium.ffa.configuration;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.item.ConfigItem;
import com.snoxmox.hyperium.ffa.kit.Kit;
import eu.okaeri.configs.OkaeriConfig;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class KitConfiguration extends OkaeriConfig {

    public ItemStack respawnItem;

    private final List<Kit> kits = new ArrayList<>();

    public List<Kit> getKits() {
        return this.kits;
    }

    public void clearKits() {
        this.kits.clear();
    }


    public void createRespawnItem() {
        final ConfigItem configItem = new ConfigItem();
        configItem.name = "&cRespawn";
        configItem.material = Material.FEATHER;
        this.respawnItem = configItem.toItemStack();
        this.save();
    }

}
