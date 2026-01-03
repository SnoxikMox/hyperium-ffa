package com.snoxmox.hyperium.ffa.clan;

import com.snoxmox.hyperium.ffa.database.Database;
import com.snoxmox.hyperium.ffa.database.repository.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ClanService {

    private final Map<String, Clan> clanCache = new ConcurrentHashMap<>();
    private final Map<UUID, String> memberIndex = new ConcurrentHashMap<>();

    private final Map<UUID, String> invites = new ConcurrentHashMap<>();

    private final Repository<String, Clan> repository;

    public ClanService(final Database database) {
        this.repository = database.getClanRepository();
    }

    public CompletableFuture<Clan> loadClan(final String tag) {
        final String normalized = this.normalize(tag);
        if (normalized == null) {
            return CompletableFuture.completedFuture(null);
        }

        final var cached = this.clanCache.get(normalized);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return this.repository.load(normalized).thenApply(loaded -> {
            if (loaded == null) {
                return null;
            }

            final String loadedTag = this.normalize(loaded.getTag());
            final String key = (loadedTag != null) ? loadedTag : normalized;

            this.clanCache.put(key, loaded);
            this.indexClanMembers(loaded);

            return loaded;
        });
    }

    public CompletableFuture<Void> saveClan(final String tag) {
        final String normalized = this.normalize(tag);
        if (normalized == null) {
            return CompletableFuture.completedFuture(null);
        }

        final var clan = this.clanCache.get(normalized);
        if (clan == null) {
            return CompletableFuture.completedFuture(null);
        }

        return this.repository.save(clan);
    }

    public CompletableFuture<Void> saveAll() {
        return this.repository.saveAll(List.copyOf(this.clanCache.values()));
    }


    public Optional<Clan> getClan(final String tag) {
        final String normalized = this.normalize(tag);
        if (normalized == null) return Optional.empty();

        final var cached = this.clanCache.get(normalized);
        if (cached != null) {
            return Optional.of(cached);
        }

        return Optional.ofNullable(this.loadClan(normalized).join());
    }

    public Optional<Clan> getClanByMember(final UUID uuid) {
        final var tag = this.memberIndex.get(uuid);
        return this.getClan(tag);
    }


    public boolean clanExists(final String tag) {
        final String normalized = this.normalize(tag);
        if (normalized == null) return false;

        if (this.clanCache.containsKey(normalized)) return true;

        final Clan loaded = this.loadClan(normalized).join();
        return loaded != null;
    }

    public Clan createClan(String tag, final String name, final UUID owner) {
        tag = this.normalize(tag);
        if (tag == null) {
            throw new IllegalArgumentException("Clan tag is null");
        }
        if (this.clanExists(tag)) {
            throw new IllegalStateException("Clan already exists: " + tag);
        }

        final var clan = new Clan(tag, name, owner);

        this.clanCache.put(tag, clan);
        this.indexClanMembers(clan);

        this.repository.save(clan);

        return clan;
    }

    public void deleteClan(final String tag) {
        final String normalized = this.normalize(tag);
        if (normalized == null) return;

        final var removed = this.clanCache.remove(normalized);
        if (removed != null) {
            for (final UUID member : removed.getMembers()) {
                this.memberIndex.remove(member);
            }
            if (removed.getOwner() != null) {
                this.memberIndex.remove(removed.getOwner());
            }
        }
    }


    public void invite(final UUID target, final String clanTag) {
        final String normalized = this.normalize(clanTag);
        if (normalized == null) return;
        this.invites.put(target, normalized);
    }

    public boolean hasInvite(final UUID target, final String clanTag) {
        final String normalized = this.normalize(clanTag);
        if (normalized == null) return false;

        final var v = this.invites.get(target);
        return v != null && v.equalsIgnoreCase(normalized);
    }

    public void consumeInvite(final UUID target) {
        this.invites.remove(target);
    }

    public boolean addMember(final Clan clan, final UUID uuid) {
        if (clan == null || uuid == null) return false;
        if (clan.isMember(uuid)) return false;

        clan.getMembers().add(uuid);
        this.memberIndex.put(uuid, this.normalize(clan.getTag()));

        this.repository.save(clan);
        return true;
    }

    public boolean removeMember(final Clan clan, final UUID uuid) {
        if (clan == null || uuid == null) return false;
        if (!clan.isMember(uuid)) return false;

        clan.getMembers().remove(uuid);
        this.memberIndex.remove(uuid);

        this.repository.save(clan);

        return true;
    }

    public Map<String, Clan> getClansByTag() {
        return Collections.unmodifiableMap(this.clanCache);
    }

    private void indexClanMembers(final Clan clan) {
        final String tag = this.normalize(clan.getTag());
        if (tag == null) return;
        if (clan.getOwner() != null) {
            this.memberIndex.put(clan.getOwner(), tag);
        }
        for (final UUID member : clan.getMembers()) {
            this.memberIndex.put(member, tag);
        }
    }

    private String normalize(final String tag) {
        if (tag == null) return null;
        final String t = tag.trim();
        if (t.isEmpty()) return null;
        return t.toUpperCase(Locale.ROOT);
    }
}
