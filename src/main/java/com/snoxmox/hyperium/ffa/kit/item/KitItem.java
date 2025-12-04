package com.snoxmox.hyperium.ffa.kit.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import eu.okaeri.configs.OkaeriConfig;
import org.bukkit.inventory.ItemStack;

@JsonIgnoreProperties({
        "bindFile", "configurer", "declaration", "headers", "updater",
        "context", "internalState", "internalPath", "effectiveConfigurer",
        "subconfig", "bindFileName"
})public class KitItem extends OkaeriConfig {

    private int slot;
    private ItemStack item;

    public KitItem() {}

    public KitItem(final int slot, final ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(final int slot) {
        this.slot = slot;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(final ItemStack item) {
        this.item = item;
    }
}
