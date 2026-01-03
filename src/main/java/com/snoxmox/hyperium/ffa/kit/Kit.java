package com.snoxmox.hyperium.ffa.kit;

import com.snoxmox.hyperium.ffa.kit.item.KitItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class Kit implements Serializable {

    private String name;
    private String displayName;
    private ItemStack displayItem;
    private List<KitItem> items;
    private boolean build = true;

    public Kit() {
    }

    public Kit(final String name) {
        this.name = name;
        this.displayName = name;
        this.items = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public List<KitItem> getItems() {
        return this.items;
    }

    public void setDisplayItem(final ItemStack item) {
        this.displayItem = item;
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public void setItems(final List<KitItem> items) {
        this.items = items;
    }

    public boolean isBuild() {
        return this.build;
    }

    public void setBuild(final boolean build) {
        this.build = build;
    }


}
