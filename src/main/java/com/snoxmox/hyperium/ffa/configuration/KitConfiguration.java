package com.snoxmox.hyperium.ffa.configuration;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.kit.Kit;
import eu.okaeri.configs.OkaeriConfig;
import java.util.ArrayList;
import java.util.List;

public class KitConfiguration extends OkaeriConfig {

    private final List<Kit> kits = new ArrayList<>();

    public List<Kit> getKits() {
        return this.kits;
    }

    public void clearKits() {
        this.kits.clear();
    }


}
