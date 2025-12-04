package com.snoxmox.hyperium.ffa.database;

import com.snoxmox.hyperium.ffa.configuration.DatabaseConfiguration;
import com.snoxmox.hyperium.ffa.database.impl.FlatRepository;
import com.snoxmox.hyperium.ffa.database.impl.MySQLRepository;
import com.snoxmox.hyperium.ffa.database.repository.Repository;

import com.snoxmox.hyperium.ffa.user.User;
import java.util.UUID;
import org.bukkit.plugin.Plugin;

public class Database {

    private final Plugin plugin;
    private final DatabaseConfiguration configuration;
    private Repository<UUID, User> userRepository;

    public Database(final Plugin plugin, final DatabaseConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }

    public void initialize() {
        this.userRepository = switch (this.configuration.type) {
            case DatabaseConfiguration.DatabaseType.MYSQL ->
                    new MySQLRepository<>(User.class, this.configuration, "users");
            case DatabaseConfiguration.DatabaseType.FLAT ->
                    new FlatRepository<>(User.class,  this.plugin.getDataPath() + "/users");
        };
        this.userRepository.init();
    }

    public void close() {
        if (this.userRepository != null) {
            this.userRepository.close();
        }
    }

    public Repository<UUID, User> getUserRepository() {
        return this.userRepository;
    }
}