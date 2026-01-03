package com.snoxmox.hyperium.ffa.scoreboard;

import com.snoxmox.hyperium.ffa.clan.war.War;
import com.snoxmox.hyperium.ffa.configuration.ClanConfiguration;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class WarScoreboardView {

    private final ClanConfiguration config;
    private final War war;
    private final long startedAtMillis;

    private final Scoreboard scoreboard;
    private final Objective objective;

    private final List<String> entries;
    private final List<Team> teams;

    WarScoreboardView(final ClanConfiguration config, final War war, final long startedAtMillis) {
        this.config = config;
        this.war = war;
        this.startedAtMillis = startedAtMillis;

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective("war", "dummy", "war");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        final int size = Math.max(1, this.config.warScoreboard.lines.size());

        this.entries = IntStream.range(0, size)
                .mapToObj(i -> ChatColor.values()[i].toString())
                .toList();

        this.teams = IntStream.range(0, size)
                .mapToObj(i -> {
                    final Team t = this.scoreboard.registerNewTeam("w" + i);
                    t.addEntry(this.entries.get(i));
                    this.objective.getScore(this.entries.get(i)).setScore(size - i);
                    return t;
                })
                .toList();
    }

    Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    void update() {
        final Map<String, String> ph = new HashMap<>();

        ph.put("a", safe(this.war.getPrimaryClanA()));
        ph.put("b", safe(this.war.getPrimaryClanB()));

        ph.put("a_all", String.join(", ", this.war.getTeamA()));
        ph.put("b_all", String.join(", ", this.war.getTeamB()));

        ph.put("a_count", String.valueOf(this.war.getTeamA().size()));
        ph.put("b_count", String.valueOf(this.war.getTeamB().size()));

        ph.put("aPoints", String.valueOf(this.war.getPointsA()));
        ph.put("bPoints", String.valueOf(this.war.getPointsB()));
        ph.put("target", String.valueOf(this.war.getTargetPoints()));

        ph.put("since", WarScoreboardManager.formatSince(this.startedAtMillis));

        this.objective.setDisplayName(WarScoreboardManager.color(apply(this.config.warScoreboard.title, ph)));

        final List<String> lines = this.config.warScoreboard.lines;
        final int size = this.teams.size();

        for (int i = 0; i < size; i++) {
            final String raw = (i < lines.size()) ? lines.get(i) : "";
            final String colored = WarScoreboardManager.color(apply(raw, ph));

            final String trimmed = colored.length() > 128 ? colored.substring(0, 128) : colored;
            final String prefix = trimmed.length() > 64 ? trimmed.substring(0, 64) : trimmed;
            final String suffix = trimmed.length() > 64 ? trimmed.substring(64) : "";

            final Team t = this.teams.get(i);
            t.setPrefix(prefix);
            t.setSuffix(suffix);
        }
    }

    private static String apply(final String text, final Map<String, String> placeholders) {
        if (text == null) return "";
        String out = text;
        for (final var e : placeholders.entrySet()) {
            out = out.replace("{" + e.getKey() + "}", e.getValue());
        }
        return out;
    }

    War getWar() {
        return this.war;
    }

    private static String safe(final String s) {
        return (s == null) ? "" : s;
    }
}