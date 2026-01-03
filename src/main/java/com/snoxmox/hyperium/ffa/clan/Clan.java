package com.snoxmox.hyperium.ffa.clan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.snoxmox.hyperium.ffa.database.repository.Identifiable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Clan implements Identifiable<String> {
    private String tag;
    private String name;

    private UUID owner;
    private final Set<UUID> members = new HashSet<>();

    private boolean friendlyFire = false;

    public Clan() {}

    public Clan(final String tag, final String name, final UUID owner) {
        this.tag = tag;
        this.name = name;
        this.owner = owner;
        this.members.add(owner);
    }

    public String getTag() { return this.tag; }
    public String getName() { return this.name; }
    public UUID getOwner() { return this.owner; }
    public Set<UUID> getMembers() { return this.members; }

    public boolean isFriendlyFire() { return this.friendlyFire; }
    public void setFriendlyFire(final boolean friendlyFire) { this.friendlyFire = friendlyFire; }

    public boolean isMember(final UUID uuid) {
        return this.members.contains(uuid);
    }

    @Override
    @JsonIgnore
    public String getId() {
        return this.tag;
    }
}
