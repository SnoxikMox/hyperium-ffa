package com.snoxmox.hyperium.ffa.scoreboard;

import com.snoxmox.hyperium.ffa.clan.war.War;
import com.snoxmox.hyperium.ffa.configuration.ClanConfiguration;
import com.snoxmox.hyperium.ffa.user.UserService;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class WarScoreboardManager {

    private final JavaPlugin plugin;
    private final ClanConfiguration config;
    private final UserService userService;

    private final Map<String, WarScoreboardView> active = new ConcurrentHashMap<>();
    private final Map<UUID, Scoreboard> previous = new ConcurrentHashMap<>();

    private int refreshTaskId = -1;

    public WarScoreboardManager(final JavaPlugin plugin, final ClanConfiguration config, final UserService userService) {
        this.plugin = plugin;
        this.config = config;
        this.userService = userService;
    }

    public void startWar(final War war) {
        final String key = warKey(war.getPrimaryClanA(), war.getPrimaryClanB());

        final WarScoreboardView view = this.active.computeIfAbsent(
                key,
                k -> new WarScoreboardView(this.config, war, System.currentTimeMillis())
        );

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!this.isInWarClan(p, war)) return;
            this.previous.putIfAbsent(p.getUniqueId(), p.getScoreboard());
            p.setScoreboard(view.getScoreboard());
        });

        this.ensureRefreshTask();
        view.update();
    }

    public void updateWar(final War war) {
        final String key = warKey(war.getPrimaryClanA(), war.getPrimaryClanB());
        final WarScoreboardView view = this.active.get(key);
        if (view == null) return;

        view.update();

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!this.isInWarClan(p, war)) return;

            this.previous.putIfAbsent(p.getUniqueId(), p.getScoreboard());
            if (p.getScoreboard() != view.getScoreboard()) {
                p.setScoreboard(view.getScoreboard());
            }
        });
    }

    public void handleJoin(final Player p) {
        if (this.active.isEmpty()) return;

        for (final WarScoreboardView view : this.active.values()) {
            final War war = view.getWar();
            if (!this.isInWarClan(p, war)) continue;

            this.previous.putIfAbsent(p.getUniqueId(), p.getScoreboard());
            p.setScoreboard(view.getScoreboard());
            view.update();
            return;
        }
    }

    public void handleQuit(final Player p) {
        this.previous.remove(p.getUniqueId());
    }

    public void stopWar(final War war) {
        final String key = warKey(war.getPrimaryClanA(), war.getPrimaryClanB());
        final WarScoreboardView view = this.active.remove(key);
        if (view == null) return;

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!this.isInWarClan(p, war)) return;
            final Scoreboard old = this.previous.remove(p.getUniqueId());
            p.setScoreboard(Objects.requireNonNullElseGet(old, () -> Bukkit.getScoreboardManager().getMainScoreboard()));
        });

        if (this.active.isEmpty()) {
            this.cancelRefreshTask();
        }
    }

    private void ensureRefreshTask() {
        if (this.refreshTaskId != -1) return;

        final int sec = Math.max(1, this.config.warScoreboard.refreshSeconds);
        this.refreshTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                this.plugin,
                () -> this.active.values().forEach(WarScoreboardView::update),
                20L,
                sec * 20L
        );
    }

    private void cancelRefreshTask() {
        if (this.refreshTaskId == -1) return;
        Bukkit.getScheduler().cancelTask(this.refreshTaskId);
        this.refreshTaskId = -1;
    }

    private boolean isInWarClan(final Player p, final War war) {
        final var u = this.userService.getUser(p.getUniqueId());
        if (!u.hasClan()) return false;

        return war.involves(u.getClanTag());
    }

    private static String warKey(final String a, final String b) {
        final String aa = (a == null) ? "" : a.toUpperCase();
        final String bb = (b == null) ? "" : b.toUpperCase();
        return (aa.compareTo(bb) <= 0) ? (aa + "|" + bb) : (bb + "|" + aa);
    }

    static String formatSince(final long startedAtMillis) {
        final long now = System.currentTimeMillis();
        final Duration d = Duration.ofMillis(Math.max(0, now - startedAtMillis));
        final long h = d.toHours();
        final long m = d.toMinutesPart();
        final long s = d.toSecondsPart();
        if (h > 0) return String.format("%dh %02dm %02ds", h, m, s);
        return String.format("%02dm %02ds", m, s);
    }

    static String color(final String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}