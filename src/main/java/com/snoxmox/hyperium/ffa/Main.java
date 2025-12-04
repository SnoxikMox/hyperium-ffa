package com.snoxmox.hyperium.ffa;

import com.snoxmox.hyperium.ffa.arena.ArenaManager;
import com.snoxmox.hyperium.ffa.configuration.ArenaConfiguration;
import com.snoxmox.hyperium.ffa.configuration.DatabaseConfiguration;
import com.snoxmox.hyperium.ffa.configuration.KitConfiguration;
import com.snoxmox.hyperium.ffa.database.Database;
import com.snoxmox.hyperium.ffa.kit.KitManager;
import com.snoxmox.hyperium.ffa.listener.PlayerJoinQuitListener;
import com.snoxmox.hyperium.ffa.user.UserService;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import java.io.File;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private DatabaseConfiguration databaseConfiguration;
    private Database database;

    private UserService userService;

    private ArenaConfiguration arenaConfiguration;
    private ArenaManager arenaManager;

    private KitConfiguration kitConfiguration;
    private KitManager kitManager;

    @Override
    public void onLoad() {
        //database
        this.databaseConfiguration = ConfigManager.create(DatabaseConfiguration.class, it -> {
            it.configure(opt -> {
                opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                opt.bindFile(new File(this.getDataFolder(), "database_configuration.yml"));
                opt.removeOrphans(true);
            });
            it.saveDefaults();
            it.load(true);
        });
        this.database = new Database(this, this.databaseConfiguration);
        this.database.initialize();
        //user
        this.userService = new UserService(this.database);

        //ladowanie aren
        this.arenaConfiguration = ConfigManager.create(ArenaConfiguration.class, it -> {
            it.configure(opt -> {
                opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                opt.bindFile(new File(this.getDataFolder(), "arena_configuration.yml"));
                opt.removeOrphans(true);
            });
            it.saveDefaults();
            it.load(true);
        });
        this.arenaManager = new ArenaManager(this.arenaConfiguration);

        this.kitConfiguration = ConfigManager.create(KitConfiguration.class, it -> {
            it.configure(opt -> {
                opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                opt.bindFile(new File(this.getDataFolder(), "kit_configuration.yml"));
                opt.removeOrphans(true);
            });
            it.saveDefaults();
            it.load(true);
        });
        this.kitManager = new KitManager(this.kitConfiguration);
    }

    @Override
    public void onEnable() {
        //Register listeners
        List.of(
                new PlayerJoinQuitListener(this.userService)
                )

                .forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        //save arenas
        this.arenaConfiguration.getArenas().addAll(this.arenaManager.getArenas());
        this.arenaConfiguration.save();
        this.arenaManager.getArenas().clear();
        //save kits
        this.kitConfiguration.getKits().addAll(this.kitManager.getKits());
        this.kitConfiguration.save();
        this.kitManager.getKits().clear();
        //user
        this.userService.saveAll().join();
        //database
        this.database.close();

    }

}
