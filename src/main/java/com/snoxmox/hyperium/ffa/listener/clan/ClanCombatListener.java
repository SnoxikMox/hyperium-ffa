package com.snoxmox.hyperium.ffa.listener.clan;

import com.snoxmox.hyperium.ffa.clan.ClanService;
import com.snoxmox.hyperium.ffa.clan.war.War;
import com.snoxmox.hyperium.ffa.clan.war.WarService;
import com.snoxmox.hyperium.ffa.configuration.ClanConfiguration;
import com.snoxmox.hyperium.ffa.scoreboard.WarScoreboardManager;
import com.snoxmox.hyperium.ffa.user.User;
import com.snoxmox.hyperium.ffa.user.UserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public record ClanCombatListener(ClanService clanService, WarService warService, UserService userService, ClanConfiguration config, WarScoreboardManager warScoreboardManager) implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        this.warScoreboardManager.handleJoin(e.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        this.warScoreboardManager.handleQuit(e.getPlayer());
    }

    @EventHandler
    public void onKick(final PlayerKickEvent e) {
        this.warScoreboardManager.handleQuit(e.getPlayer());
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
        final Player victim = e.getEntity();
        final Player killer = victim.getKiller();
        if (killer == null) return;

        final User killerUser = this.userService.getUser(killer.getUniqueId());
        final User victimUser = this.userService.getUser(victim.getUniqueId());

        if (!killerUser.hasClan() || !victimUser.hasClan()) return;
        final String killerClan = killerUser.getClanTag();
        final String victimClan = victimUser.getClanTag();

        final Optional<War> warOpt = this.warService.getWar(killerClan);
        if (warOpt.isEmpty()) return;
        final War war = warOpt.get();
        if (!war.isConflict(killerClan, victimClan)) return;

        if (war.getState() != War.State.ACTIVE) return;

        final long now = System.currentTimeMillis();
        final long minMillis = TimeUnit.DAYS.toMillis(this.config.warJoinCooldownDays);
        if (killerUser.getClanJoinAtMillis() - now < minMillis) {
            this.config.messages.warCooldown.send(killer, "days", String.valueOf(this.config.warJoinCooldownDays));
            return;
        }
        final boolean isKillerSideA = war.isSideA(killerClan);
        war.addPointToSide(isKillerSideA);

        final int aP = war.getPointsA();
        final int bP = war.getPointsB();

        final Map<String, String> pointPlaceholders = Map.of(
                "tag", killerClan,
                "aPoints", String.valueOf(aP),
                "bPoints", String.valueOf(bP),
                "target", String.valueOf(war.getTargetPoints())
        );
        this.warScoreboardManager.updateWar(war);

        killer.getServer().getOnlinePlayers().forEach(p -> {
            final User u = this.userService.getUser(p.getUniqueId());
            if (!u.hasClan()) return;
            if (war.involves(u.getClanTag())) {
                this.config.messages.warPoint.send(p, pointPlaceholders);
            }
        });

        if (war.isWinner(isKillerSideA)) {
            final String winnerSide = isKillerSideA ? String.join(", ", war.getTeamA()) : String.join(", ", war.getTeamB());
            final String loserSide = isKillerSideA ? String.join(", ", war.getTeamB()) : String.join(", ", war.getTeamA());

            final int wPoints = isKillerSideA ? war.getPointsA() : war.getPointsB();
            final int lPoints = isKillerSideA ? war.getPointsB() : war.getPointsA();

            final Map<String, String> winPlaceholders = Map.of(
                    "winner", winnerSide,
                    "loser", loserSide,
                    "wPoints", String.valueOf(wPoints),
                    "lPoints", String.valueOf(lPoints)
            );

            Bukkit.getOnlinePlayers().forEach(p ->
                    this.config.messages.warWin.send(p, winPlaceholders)
            );
            this.warScoreboardManager.stopWar(war);
            this.warService.finishWar(war);
        }
    }
}