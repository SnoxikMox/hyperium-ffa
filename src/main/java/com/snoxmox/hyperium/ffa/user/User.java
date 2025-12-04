package com.snoxmox.hyperium.ffa.user;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.database.repository.Identifiable;
import com.snoxmox.hyperium.ffa.kit.Kit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User implements Identifiable<UUID> {

    private UUID uuid;
    private Map<String, Kit> customKits = new ConcurrentHashMap<>();
    private Arena currentArena = null;

    public User() {}

    public User(final UUID uuid) {
        this.uuid = uuid;
        this.customKits.put("testowy", new Kit("testowy"));
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void addCustomKit(final Kit kit) {
        this.customKits.put(kit.getName(), kit);
    }

    public boolean hasCustomKit(final String name) {
        return this.customKits.containsKey(name);
    }

    public Kit getCustomKit(final String name) {
        return this.customKits.get(name);
    }

    public Arena getCurrentArena() {
        return this.currentArena;
    }

    public void setCurrentArena(final Arena currentArena) {
        this.currentArena = currentArena;
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }
}
