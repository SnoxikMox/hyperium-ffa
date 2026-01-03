package com.snoxmox.hyperium.ffa.user;

import com.snoxmox.hyperium.ffa.database.Database;

import com.snoxmox.hyperium.ffa.database.repository.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {

    private final Map<UUID, User> userCache = new ConcurrentHashMap<>();
    private final Repository<UUID, User> repository;

    public UserService(final Database database) {
        this.repository = database.getUserRepository();
    }

    public CompletableFuture<User> loadUser(final UUID uuid) {
        final var cached = this.userCache.get(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        return this.repository.load(uuid).thenApply(loadedUser -> {
            final var user = (loadedUser != null) ? loadedUser : new User(uuid);
            this.userCache.put(uuid, user);
            return user;
        });
    }

    private final ConcurrentHashMap<UUID, CompletableFuture<Void>> saving = new ConcurrentHashMap<>();
    public CompletableFuture<Void> saveUserOnQuit(final UUID uuid) {
        return this.saving.compute(uuid, (id, current) -> {
            if (current != null && !current.isDone()) {
                return current;
            }
            return this.saveUser(uuid)
                    .whenComplete((_, _) -> this.saving.remove(id));
        });
    }

    public CompletableFuture<Void> saveUser(final UUID uuid) {
        final var user = this.userCache.remove(uuid);
        if (user == null) {
            return CompletableFuture.completedFuture(null);
        }

        return this.repository.save(user);
    }

    public CompletableFuture<Void> saveAll() {
        final var currentUsers = List.copyOf(this.userCache.values());
        return this.repository.saveAll(currentUsers);
    }

    public User getUser(final UUID uuid) {
        return Optional.ofNullable(this.userCache.get(uuid)).orElseGet(() -> this.loadUser(uuid).join());
    }

}