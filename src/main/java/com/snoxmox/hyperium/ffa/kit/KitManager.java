package com.snoxmox.hyperium.ffa.kit;

import com.snoxmox.hyperium.ffa.configuration.KitConfiguration;
import java.util.ArrayList;
import java.util.List;

public class KitManager {

    private final List<Kit> kits = new ArrayList<>();

    public KitManager(final KitConfiguration kitConfiguration) {
        this.kits.addAll(kitConfiguration.getKits());
        kitConfiguration.clearKits();
    }

    public Kit getKit(final String name) {
        return this.kits.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void addKit(final Kit kit) {
        this.kits.add(kit);
    }

    public void removeKit(final Kit kit) {
        this.kits.remove(kit);
    }

    public List<Kit> getKits() {
        return this.kits;
    }



}
