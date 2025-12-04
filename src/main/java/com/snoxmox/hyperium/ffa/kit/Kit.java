package com.snoxmox.hyperium.ffa.kit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.snoxmox.hyperium.ffa.kit.item.KitItem;
import eu.okaeri.configs.OkaeriConfig;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({
        "bindFile", "configurer", "declaration", "headers", "updater",
        "context", "internalState", "internalPath", "effectiveConfigurer",
        "subconfig", "bindFileName"
})public class Kit extends OkaeriConfig {

    private String name;
    private String displayName;
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
