package com.snoxmox.hyperium.ffa.kit.item;

import java.io.Serializable;
import org.bukkit.inventory.ItemStack;

public class KitItem implements Serializable {

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
