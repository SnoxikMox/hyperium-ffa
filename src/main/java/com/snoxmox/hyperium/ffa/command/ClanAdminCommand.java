package com.snoxmox.hyperium.ffa.command;

import com.snoxmox.hyperium.ffa.clan.Clan;
import com.snoxmox.hyperium.ffa.clan.ClanService;
import com.snoxmox.hyperium.ffa.clan.war.WarService;
import com.snoxmox.hyperium.ffa.configuration.ClanConfiguration;
import com.snoxmox.hyperium.ffa.notice.Notice;
import com.snoxmox.hyperium.ffa.notice.NotificationType;
import com.snoxmox.hyperium.ffa.user.User;
import com.snoxmox.hyperium.ffa.user.UserService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "clanadmin", aliases = {"klanadmin", "klanyadmin"})
@Permission("hyperium.command.clanadmin")
public record ClanAdminCommand(ClanService clanService, WarService warService, UserService userService, ClanConfiguration clanConfiguration) {

    @Execute(name = "bypassjoin")
    public void bypassJoinTime(@Context final Player player, @Arg final Player target) {
        this.userService.getUser(target.getUniqueId()).setClanJoinAtMillis(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
        new Notice("&8[&3ClanAdmin&8] &7Usunięto blokade czasową graczowi &b" + target.getName(), NotificationType.CHAT).send(player);
    }

    @Execute(name = "remove")
    public void removeClan(@Context final CommandSender sender, @Arg final Clan clan) {
        this.clanService.deleteClan(clan.getTag());
        clan.getMembers().forEach(uuid -> {
            final User user = this.userService.getUser(uuid);
            user.setClanTag(null);
            user.setClanJoinAtMillis(0L);
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                this.clanConfiguration.messages.clanDeleted.send(player, "admin", sender.getName());
            }
        });
        new Notice("&8[&3ClanAdmin&8] &7Usunięto klan &b" + clan.getTag(), NotificationType.CHAT).send(sender);
        Bukkit.getOnlinePlayers().forEach(player -> this.clanConfiguration.messages.broadcastClanDeleted.send(player, Map.of(
                "admin", sender.getName(),
                "tag", clan.getTag()
        )));
    }

    @Execute(name = "removemember")
    public void removeMember(@Context final CommandSender sender, @Arg final Clan clan, @Arg final OfflinePlayer target) {
        final User targetUser = this.userService.getUser(target.getUniqueId());
        if (!targetUser.hasClan()) {
            new Notice("&8[&3ClanAdmin&8] &7Gracz &b" + target.getName() + "&7 nie jest w klanie.", NotificationType.CHAT).send(sender);
            return;
        }
        if (!targetUser.getClanTag().equalsIgnoreCase(clan.getTag())) {
            new Notice("&8[&3ClanAdmin&8] &7Gracz &b" + target.getName() + "&7 nie jest w klanie.", NotificationType.CHAT).send(sender);
            return;
        }
        targetUser.setClanTag(null);
        targetUser.setClanJoinAtMillis(0L);
        this.clanService.removeMember(clan, target.getUniqueId());
        new Notice("&8[&3ClanAdmin&8] &7Usunięto gracza &b" + target.getName() + "&7 z klanu &b" + clan.getTag(), NotificationType.CHAT).send(sender);
        if (target.isOnline())
            this.clanConfiguration.messages.youAreRemoveFromClan.send((Player) target, "admin", sender.getName());
    }


    @Execute(name = "addmember")
    public void addMember(@Context final CommandSender sender, @Arg final Clan clan, @Arg final OfflinePlayer target) {
        this.clanService.addMember(clan, target.getUniqueId());
        new Notice("&8[&3ClanAdmin&8] &7Dodano gracza &b" + target.getName() + "&7 do klanu &b" + clan.getTag(), NotificationType.CHAT).send(sender);
        final User targetUser = this.userService.getUser(target.getUniqueId());
        targetUser.setClanTag(clan.getTag());
        targetUser.setClanJoinAtMillis(System.currentTimeMillis());
        if (target.isOnline())
            this.clanConfiguration.messages.youAreAddToClan.send((Player) target, "admin", sender.getName());
    }

}
