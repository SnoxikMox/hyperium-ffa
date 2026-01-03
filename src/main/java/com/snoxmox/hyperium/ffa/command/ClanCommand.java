package com.snoxmox.hyperium.ffa.command;

import com.snoxmox.hyperium.ffa.clan.Clan;
import com.snoxmox.hyperium.ffa.clan.ClanService;
import com.snoxmox.hyperium.ffa.clan.war.War;
import com.snoxmox.hyperium.ffa.clan.war.WarService;
import com.snoxmox.hyperium.ffa.configuration.ClanConfiguration;
import com.snoxmox.hyperium.ffa.notice.Notice;
import com.snoxmox.hyperium.ffa.notice.NotificationType;
import com.snoxmox.hyperium.ffa.scoreboard.WarScoreboardManager;
import com.snoxmox.hyperium.ffa.user.User;
import com.snoxmox.hyperium.ffa.user.UserService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Command(name = "clan", aliases = {"klan", "klany"})
public record ClanCommand(ClanService clanService, WarService warService, UserService userService, ClanConfiguration config, WarScoreboardManager warScoreboardManager) {

    private static final Map<String, String> ALLY_INVITES = new ConcurrentHashMap<>();

    @Execute
    public void help(@Context final Player p) {
        this.config.messages.help.send(p);
    }

    @Execute(name = "stworz")
    public void create(@Context final Player p, @Arg("gracz") final Player target, @Arg("tag") final String tagRaw, @Arg("nazwa") final String name) {
        if (!p.hasPermission(this.config.createPermission) && !p.isOp()) {
            this.config.messages.createOnlyAdmin.send(p);
            return;
        }
        final User user = this.userService.getUser(target.getUniqueId());
        if (user.hasClan()) {
            this.config.messages.alreadyInClan.send(p);
            return;
        }

        final String tag = tagRaw.toUpperCase(Locale.ROOT);

        if (tag.length() > this.config.maxTagLength) {
            this.config.messages.invalidTag.send(p, "max", String.valueOf(this.config.maxTagLength));
            return;
        }
        if (name.length() > this.config.maxNameLength) {
            this.config.messages.invalidName.send(p, "max", String.valueOf(this.config.maxNameLength));
            return;
        }
        if (this.clanService.clanExists(tag)) {
            this.config.messages.clanExists.send(p);
            return;
        }

        this.clanService.createClan(tag, name, target.getUniqueId());
        user.setClanTag(tag);
        user.setClanJoinAtMillis(System.currentTimeMillis());

        this.config.messages.clanCreated.send(target, Map.of(
                "tag", tag,
                "name", name
        ));
        new Notice("&8[&3ClanAdmin&8] &7Stworzono clan graczowi &b" + target.getName() + " &7o tagu &d" + tagRaw, NotificationType.CHAT).send(p);
    }

    @Execute(name = "zapros")
    public void invite(@Context final Player p, @Arg("gracz") final Player target) {
        final User user = this.userService.getUser(p.getUniqueId());
        if (!user.hasClan()) {
            this.config.messages.notInClan.send(p);
            return;
        }

        final Optional<Clan> clanOpt = this.clanService.getClan(user.getClanTag());
        if (clanOpt.isEmpty()) {
            this.config.messages.clanMissing.send(p);
            return;
        }
        final Clan clan = clanOpt.get();

        if (!clan.getOwner().equals(p.getUniqueId()) && !p.hasPermission(this.config.adminPermission) && !p.isOp()) {
            this.config.messages.noPermission.send(p);
            return;
        }

        final User targetUser = this.userService.getUser(target.getUniqueId());
        if (targetUser.hasClan()) {
            this.config.messages.targetAlreadyHasClan.send(p);
            return;
        }

        this.clanService.invite(target.getUniqueId(), clan.getTag());

        this.config.messages.invited.send(p, "player", target.getName());
        this.config.messages.inviteReceived.send(target, "tag", clan.getTag());
    }

    @Execute(name = "akceptuj")
    public void accept(@Context final Player p, @Arg("klan") final Clan tagRaw) {
        final User user = this.userService.getUser(p.getUniqueId());
        if (user.hasClan()) {
            this.config.messages.alreadyInClan.send(p);
            return;
        }

        final String tag = tagRaw.getTag().toUpperCase(Locale.ROOT);
        if (!this.clanService.hasInvite(p.getUniqueId(), tag)) {
            this.config.messages.noInvite.send(p);
            return;
        }

        final Optional<Clan> clanOpt = this.clanService.getClan(tag);
        if (clanOpt.isEmpty()) {
            this.config.messages.clanDoesNotExist.send(p);
            this.clanService.consumeInvite(p.getUniqueId());
            return;
        }

        this.clanService.addMember(clanOpt.get(), p.getUniqueId());
        this.clanService.consumeInvite(p.getUniqueId());

        user.setClanTag(tag);
        user.setClanJoinAtMillis(System.currentTimeMillis());

        this.config.messages.joined.send(p, "tag", tag);
    }

    @Execute(name = "wyrzuc")
    public void kick(@Context final Player p, @Arg("gracz") final Player target) {
        final User user = this.userService.getUser(p.getUniqueId());
        if (!user.hasClan()) {
            this.config.messages.notInClan.send(p);
            return;
        }

        final Optional<Clan> clanOpt = this.clanService.getClan(user.getClanTag());
        if (clanOpt.isEmpty()) return;
        final Clan clan = clanOpt.get();

        if (!clan.getOwner().equals(p.getUniqueId()) && !p.hasPermission(this.config.adminPermission) && !p.isOp()) {
            this.config.messages.noPermission.send(p);
            return;
        }

        final User targetUser = this.userService.getUser(target.getUniqueId());
        if (!targetUser.hasClan() || !targetUser.getClanTag().equalsIgnoreCase(clan.getTag())) {
            this.config.messages.notInYourClan.send(p);
            return;
        }

        if (target.getUniqueId().equals(clan.getOwner())) {
            this.config.messages.cannotKickOwner.send(p);
            return;
        }

        this.clanService.removeMember(clan, target.getUniqueId());
        targetUser.setClanTag(null);
        targetUser.setClanJoinAtMillis(0L);

        this.config.messages.kicked.send(p, "player", target.getName());
        this.config.messages.youWereKicked.send(target);
    }

    @Execute(name = "opusc")
    public void leave(@Context final Player p) {
        final User user = this.userService.getUser(p.getUniqueId());
        if (!user.hasClan()) {
            this.config.messages.notInClan.send(p);
            return;
        }

        final Optional<Clan> clanOpt = this.clanService.getClan(user.getClanTag());
        if (clanOpt.isEmpty()) return;
        final Clan clan = clanOpt.get();

        if (p.getUniqueId().equals(clan.getOwner())) {
            this.config.messages.ownerCannotLeave.send(p);
            return;
        }

        this.clanService.removeMember(clan, p.getUniqueId());
        user.setClanTag(null);
        user.setClanJoinAtMillis(0L);

        this.config.messages.left.send(p);
    }

    @Execute(name = "pvp")
    public void pvp(@Context final Player p) {
        final User user = this.userService.getUser(p.getUniqueId());
        if (!user.hasClan()) {
            this.config.messages.notInClan.send(p);
            return;
        }

        final Optional<Clan> clanOpt = this.clanService.getClan(user.getClanTag());
        if (clanOpt.isEmpty()) return;
        final Clan clan = clanOpt.get();

        if (!clan.getOwner().equals(p.getUniqueId()) && !p.hasPermission(this.config.adminPermission) && !p.isOp()) {
            this.config.messages.noPermission.send(p);
            return;
        }
        clan.setFriendlyFire(!clan.isFriendlyFire());

        if (clan.isFriendlyFire()) {
            this.config.messages.friendlyFireOn.send(p);
        } else {
            this.config.messages.friendlyFireOff.send(p);
        }
    }

    @Execute(name = "war")
    public void war(@Context final Player p, @Arg("klan") final Clan enemy, @Arg("punkty") final int points) {
        final User user = this.userService.getUser(p.getUniqueId());
        if (!user.hasClan()) {
            this.config.messages.notInClan.send(p);
            return;
        }

        final Optional<Clan> clanOpt = this.clanService.getClan(user.getClanTag());
        if (clanOpt.isEmpty()) return;
        final Clan clan = clanOpt.get();

        if (!clan.getOwner().equals(p.getUniqueId()) && !p.hasPermission(this.config.adminPermission) && !p.isOp()) {
            this.config.messages.noPermission.send(p);
            return;
        }

        if (points <= 0) {
            this.config.messages.warPointsInvalid.send(p);
            return;
        }

        final String enemyTag = enemy.getTag().toUpperCase(Locale.ROOT);
        if (enemyTag.equalsIgnoreCase(clan.getTag())) {
            this.config.messages.warSelf.send(p);
            return;
        }

        if (this.warService.hasAnyWar(clan.getTag()) || this.warService.hasAnyWar(enemyTag)) {
            this.config.messages.warAlready.send(p);
            return;
        }

        this.warService.declareWar(clan.getTag(), enemyTag, points);

        this.config.messages.warDeclared.send(p, Map.of(
                "tag", enemyTag,
                "points", String.valueOf(points)
        ));

        Bukkit.getOnlinePlayers().forEach(pl -> {
            final User u = this.userService.getUser(pl.getUniqueId());
            if (u.hasClan() && u.getClanTag().equalsIgnoreCase(enemyTag)) {
                this.config.messages.warIncoming.send(pl, Map.of(
                        "tag", clan.getTag(),
                        "points", String.valueOf(points)
                ));
            }
        });
    }

    @Execute(name = "waraccept")
    public void waraccept(@Context final Player p, @Arg("klan") final Clan enemy) {
        final User user = this.userService.getUser(p.getUniqueId());
        if (!user.hasClan()) {
            this.config.messages.notInClan.send(p);
            return;
        }

        final Optional<Clan> clanOpt = this.clanService.getClan(user.getClanTag());
        if (clanOpt.isEmpty()) return;
        final Clan clan = clanOpt.get();

        if (!clan.getOwner().equals(p.getUniqueId()) && !p.hasPermission(this.config.adminPermission) && !p.isOp()) {
            this.config.messages.noPermission.send(p);
            return;
        }

        final String enemyTag = enemy.getTag().toUpperCase(Locale.ROOT);
        final Optional<War> warOpt = this.warService.getWarBetween(clan.getTag(), enemyTag);
        if (warOpt.isEmpty()) {
            this.config.messages.warNoPending.send(p);
            return;
        }

        final War war = warOpt.get();
        if (war.getState() != War.State.PENDING) {
            this.config.messages.warNotPending.send(p);
            return;
        }

        this.warService.acceptWar(war);

        final String teamANames = String.join(", ", war.getTeamA());
        final String teamBNames = String.join(", ", war.getTeamB());

        final Map<String, String> placeholders = Map.of(
                "a", teamANames,
                "b", teamBNames,
                "points", String.valueOf(war.getTargetPoints())
        );
        this.warScoreboardManager.startWar(war);
        Bukkit.getOnlinePlayers().forEach(pl ->
                this.config.messages.warAccepted.send(pl, placeholders)
        );
    }

    @Execute(name = "warinvite")
    public void warInvite(@Context final Player p, @Arg("klan") final Clan ally) {
        final User user = this.userService.getUser(p.getUniqueId());
        if (!user.hasClan()) {
            this.config.messages.notInClan.send(p);
            return;
        }
        final String myTag = user.getClanTag();
        final Clan myClan = this.clanService.getClan(myTag).orElseThrow();

        if (!myClan.getOwner().equals(p.getUniqueId()) && !p.hasPermission(this.config.adminPermission) && !p.isOp()) {
            this.config.messages.noPermission.send(p);
            return;
        }

        final Optional<War> warOpt = this.warService.getWar(myTag);
        if (warOpt.isEmpty() || warOpt.get().getState() != War.State.ACTIVE) {
            this.config.messages.warNoPending.send(p);
            return;
        }

        final String allyTag = ally.getTag().toUpperCase(Locale.ROOT);
        if (allyTag.equalsIgnoreCase(myTag)) {
            new Notice("&cNie możesz zaprosić samego siebie.", NotificationType.CHAT).send(p);
            return;
        }

        if (this.warService.hasAnyWar(allyTag)) {
            this.config.messages.warAlready.send(p);
            return;
        }

        ALLY_INVITES.put(allyTag, myTag);

        this.config.messages.warAllyInviteSent.send(p, "tag", allyTag);

        Bukkit.getOnlinePlayers().forEach(pl -> {
            final User u = this.userService.getUser(pl.getUniqueId());
            if (u.hasClan() && u.getClanTag().equalsIgnoreCase(allyTag)) {
                this.config.messages.warAllyInviteReceived.send(pl, "tag", myTag);
            }
        });
    }

    @Execute(name = "warjoin")
    public void warJoin(@Context final Player p, @Arg("klan") final Clan inviterClan) {
        final User user = this.userService.getUser(p.getUniqueId());
        if (!user.hasClan()) {
            this.config.messages.notInClan.send(p);
            return;
        }
        final String myTag = user.getClanTag();
        final Clan myClan = this.clanService.getClan(myTag).orElseThrow();

        if (!myClan.getOwner().equals(p.getUniqueId()) && !p.hasPermission(this.config.adminPermission) && !p.isOp()) {
            this.config.messages.noPermission.send(p);
            return;
        }

        if (this.warService.hasAnyWar(myTag)) {
            this.config.messages.warAlready.send(p);
            return;
        }

        final String inviterTag = inviterClan.getTag().toUpperCase(Locale.ROOT);

        final String actualInviter = ALLY_INVITES.get(myTag);
        if (actualInviter == null || !actualInviter.equalsIgnoreCase(inviterTag)) {
            this.config.messages.noInvite.send(p);
            return;
        }

        final Optional<War> warOpt = this.warService.getWar(inviterTag);
        if (warOpt.isEmpty() || warOpt.get().getState() != War.State.ACTIVE) {
            this.config.messages.warNotPending.send(p);
            ALLY_INVITES.remove(myTag);
            return;
        }
        final War war = warOpt.get();

        final boolean isSideA = war.isSideA(inviterTag);

        this.warService.joinWar(war, myTag, isSideA);
        ALLY_INVITES.remove(myTag);

        final String sideName = isSideA ? war.getPrimaryClanA() : war.getPrimaryClanB();
        final Map<String, String> info = Map.of(
                "tag", myTag,
                "side", sideName
        );

        Bukkit.getOnlinePlayers().forEach(pl -> {
            final User u = this.userService.getUser(pl.getUniqueId());
            if (u.hasClan() && war.involves(u.getClanTag())) {
                this.config.messages.warAllyJoined.send(pl, info);
            }
        });
    }
}