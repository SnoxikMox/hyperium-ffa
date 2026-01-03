package com.snoxmox.hyperium.ffa;

import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.arena.ArenaManager;
import com.snoxmox.hyperium.ffa.clan.Clan;
import com.snoxmox.hyperium.ffa.clan.ClanService;
import com.snoxmox.hyperium.ffa.clan.war.WarService;
import com.snoxmox.hyperium.ffa.command.ArenaCommand;
import com.snoxmox.hyperium.ffa.command.ClanAdminCommand;
import com.snoxmox.hyperium.ffa.command.ClanCommand;
import com.snoxmox.hyperium.ffa.command.KillCommand;
import com.snoxmox.hyperium.ffa.command.KitCommand;
import com.snoxmox.hyperium.ffa.command.SpawnCommand;
import com.snoxmox.hyperium.ffa.command.resolver.ArenaArgumentResolver;
import com.snoxmox.hyperium.ffa.command.resolver.ClanResolver;
import com.snoxmox.hyperium.ffa.command.resolver.KitArgumentResolver;
import com.snoxmox.hyperium.ffa.configuration.ArenaConfiguration;
import com.snoxmox.hyperium.ffa.configuration.ClanConfiguration;
import com.snoxmox.hyperium.ffa.configuration.DatabaseConfiguration;
import com.snoxmox.hyperium.ffa.configuration.KitConfiguration;
import com.snoxmox.hyperium.ffa.configuration.MenuConfiguration;
import com.snoxmox.hyperium.ffa.configuration.MessageConfiguration;
import com.snoxmox.hyperium.ffa.database.Database;
import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.KitManager;
import com.snoxmox.hyperium.ffa.kit.serializer.KitItemSerializer;
import com.snoxmox.hyperium.ffa.kit.serializer.KitSerializer;
import com.snoxmox.hyperium.ffa.listener.arena.ArenaJoinQuitListener;
import com.snoxmox.hyperium.ffa.listener.arena.PlayerDeathListener;
import com.snoxmox.hyperium.ffa.listener.clan.ClanCombatListener;
import com.snoxmox.hyperium.ffa.listener.player.PlayerJoinQuitListener;
import com.snoxmox.hyperium.ffa.listener.world.PlayerWorldChangeListener;
import com.snoxmox.hyperium.ffa.scoreboard.WarScoreboardManager;
import com.snoxmox.hyperium.ffa.user.UserService;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import java.io.File;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    private DatabaseConfiguration databaseConfiguration;
    private Database database;

    private UserService userService;

    private ArenaConfiguration arenaConfiguration;
    private ArenaManager arenaManager;

    private KitConfiguration kitConfiguration;
    private KitManager kitManager;

    private MessageConfiguration messageConfiguration;

    private ClanService clanService;
    private WarService warService;
    private ClanConfiguration clanConfiguration;
    private MenuConfiguration menuConfiguration;
    private WarScoreboardManager warScoreboardManager;

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        instance = this;

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

        this.userService = new UserService(this.database);

        this.clanConfiguration = ConfigManager.create(ClanConfiguration.class, it -> {
            it.configure(opt -> {
                opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                opt.bindFile(new File(this.getDataFolder(), "clan_configuration.yml"));
                opt.removeOrphans(true);
            });
            it.saveDefaults();
            it.load(true);
        });
        this.clanService = new ClanService(this.database);
        this.warService = new WarService();

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
                opt.serdes(registry -> {
                    registry.register(new KitSerializer());
                    registry.register(new KitItemSerializer());
                });
                opt.bindFile(new File(this.getDataFolder(), "kit_configuration.yml"));
                opt.removeOrphans(true);
            });
            it.saveDefaults();
            it.load(true);
        });
        this.kitConfiguration.createRespawnItem();
        this.kitManager = new KitManager(this.kitConfiguration);

        this.messageConfiguration = ConfigManager.create(MessageConfiguration.class, it -> {
            it.configure(opt -> {
                opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                opt.bindFile(new File(this.getDataFolder(), "message_configuration.yml"));
                opt.removeOrphans(true);
            });
            it.saveDefaults();
            it.load(true);
        });

        this.menuConfiguration = ConfigManager.create(MenuConfiguration.class, it -> {
            it.configure(opt -> {
                opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                opt.bindFile(new File(this.getDataFolder(), "menu_configuration.yml"));
                opt.removeOrphans(true);
            });
        });

        this.warScoreboardManager = new WarScoreboardManager(this, this.clanConfiguration, this.userService);

        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("hyperium")
                )
                .argument(Arena.class, new ArenaArgumentResolver(this.arenaManager))
                .argument(Kit.class, new KitArgumentResolver(this.kitManager))
                .argument(Clan.class, new ClanResolver(this.clanService))
                .commands(
                        new ArenaCommand(this.arenaManager, this.userService, this.messageConfiguration),
                        new KitCommand(this.userService, this.kitManager, this.messageConfiguration, this.menuConfiguration),
                        new ClanCommand(this.clanService, this.warService, this.userService, this.clanConfiguration, this.warScoreboardManager),
                        new ClanAdminCommand(this.clanService, this.warService, this.userService, this.clanConfiguration),
                        new SpawnCommand(this.arenaConfiguration, this.userService),
                        new KillCommand(this.arenaConfiguration, this.userService)
                )
                .build();

        List.of(
                        new PlayerJoinQuitListener(this.userService),
                        new PlayerWorldChangeListener(this.arenaManager),
                        new ArenaJoinQuitListener(this.userService),
                        new PlayerDeathListener(this.userService, this.messageConfiguration, this, this.arenaConfiguration, this.menuConfiguration),
                        new ClanCombatListener(this.clanService, this.warService, this.userService, this.clanConfiguration, this.warScoreboardManager)
                )
                .forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {

        this.arenaConfiguration.getArenas().addAll(this.arenaManager.getArenas());
        this.arenaConfiguration.save();
        this.arenaManager.getArenas().clear();

        this.kitConfiguration.getKits().addAll(this.kitManager.getKits());
        this.kitConfiguration.save();
        this.kitManager.getKits().clear();

        this.userService.saveAll().join();

        this.database.close();

    }

    public static Main getInstance() {
        return instance;
    }

    public MessageConfiguration getMessageConfiguration() {
        return this.messageConfiguration;
    }

}
