package com.snoxmox.hyperium.ffa.configuration;

import com.snoxmox.hyperium.ffa.notice.Notice;
import com.snoxmox.hyperium.ffa.notice.NotificationType;
import eu.okaeri.configs.OkaeriConfig;
import java.util.List;

public class ClanConfiguration extends OkaeriConfig {

    public int maxTagLength = 4;
    public int maxNameLength = 16;

    public int warJoinCooldownDays = 7;

    public String createPermission = "hyperium.clan.create";

    public String adminPermission = "hyperium.clan.admin";

    public Messages messages = new Messages();

    public static class Messages extends OkaeriConfig {

        public Notice help = new Notice("&7Komendy: &f/clan stworz, zapros, akceptuj, wyrzuc, opusc, pvp, war, waraccept", NotificationType.CHAT);
        public Notice unknownCommand = sub("&cNieznana komenda.");
        public Notice mustBePlayer = sub("&cTa komenda jest tylko dla gracza.");
        public Notice noPermission = sub("&cBrak uprawnień.");

        public Notice createOnlyAdmin = sub("&cNie możesz tworzyć klanu samodzielnie. &7Sprawdź wymagania i zgłoś się na Discord.");
        public Notice createUsage = sub("&cUżycie: &f/clan create <tag> <nazwa>");
        public Notice invalidTag = sub("&cNiepoprawny tag. &7Max: {max} znaki.");
        public Notice invalidName = sub("&cNiepoprawna nazwa. &7Max: {max} znaków.");
        public Notice clanExists = sub("&cKlan o tym tagu już istnieje.");
        public Notice clanCreated = sub("&aUtworzono klan &f{tag} &7({name}).");

        public Notice alreadyInClan = sub("&cJesteś już w klanie.");
        public Notice notInClan = sub("&cNie jesteś w klanie.");
        public Notice clanMissing = sub("&cBłąd: twój klan nie istnieje.");

        public Notice inviteUsage = sub("&cUżycie: &f/clan invite <nick>");
        public Notice playerOffline = sub("&cGracz offline.");
        public Notice targetAlreadyHasClan = sub("&cTen gracz ma już klan.");
        public Notice invited = sub("&aZaproszono &f{player}&a do klanu.");
        public Notice inviteReceived = sub("&aDostałeś zaproszenie do klanu &f{tag}&a. &7Użyj: &f/clan accept {tag}");

        public Notice acceptUsage = sub("&cUżycie: &f/clan accept <tag>");
        public Notice noInvite = sub("&cNie masz zaproszenia do tego klanu.");
        public Notice clanDoesNotExist = sub("&cTen klan nie istnieje.");
        public Notice joined = sub("&aDołączyłeś do klanu &f{tag}&a.");

        public Notice kickUsage = sub("&cUżycie: &f/clan kick <nick>");
        public Notice notInYourClan = sub("&cTen gracz nie jest w twoim klanie.");
        public Notice cannotKickOwner = sub("&cNie możesz wyrzucić ownera.");
        public Notice kicked = sub("&cWyrzucono &f{player}&c z klanu.");
        public Notice youWereKicked = sub("&cZostałeś wyrzucony z klanu.");

        public Notice ownerCannotLeave = sub("&cOwner nie może wyjść. &7(Zrób disband albo przekaż ownership)");
        public Notice left = sub("&eOpuściłeś klan.");

        public Notice pvpUsage = sub("&cUżycie: &f/clan pvp <on/off>");
        public Notice friendlyFireOn = sub("&aPvP w klanie: &fWŁĄCZONE");
        public Notice friendlyFireOff = sub("&aPvP w klanie: &fWYŁĄCZONE");

        public Notice warUsage = sub("&cUżycie: &f/clan war <tag> <punkty>");
        public Notice warPointsInvalid = sub("&cPunkty muszą być &f> 0&c.");
        public Notice warSelf = sub("&cNie możesz wypowiedzieć wojny sam sobie.");
        public Notice warClanNotExist = sub("&cTaki klan nie istnieje.");
        public Notice warAlready = sub("&cTen klan jest już w trakcie/podczas wojny lub ma pending.");
        public Notice warDeclared = sub("&eWypowiedziałeś wojnę klanowi &f{tag}&e do &f{points}&e punktów.");
        public Notice warIncoming = sub("&cTwój klan ma wypowiedzianą wojnę przez &f{tag}&c do &f{points}&c punktów. &7/clan waraccept {tag}");

        public Notice warAcceptUsage = sub("&cUżycie: &f/clan waraccept <tag>");
        public Notice warNoPending = sub("&cNie ma pending wojny z tym klanem.");
        public Notice warNotPending = sub("&cTa wojna nie jest w stanie PENDING.");
        public Notice warAccepted = sub("&aWojna rozpoczęta: &f{a}&a vs &f{b}&a. Cel: &f{points}");

        public Notice warCooldown = sub("&cTwoje kille nie liczą się do wojny. &7Musisz być w klanie co najmniej {days} dni.");
        public Notice warPoint = sub("&ePunkt dla klanu &f{tag}&e! Stan: &f{aPoints}&e:&f{bPoints}&e / &f{target}");
        public Notice warWin = sub("&aKlan &f{winner}&a wygrywa wojnę przeciwko &f{loser}&a! Wynik: &f{wPoints}&a:&f{lPoints}");

        public Notice clanDeleted = sub("&cTwoj clan został rozwiązany przez &f{admin}&c.");
        public Notice broadcastClanDeleted = new Notice("&8[&3ClanAdmin&8] &7Klan &b{tag} został rozwiązany przez &d{admin}", NotificationType.CHAT);
        public Notice youAreRemoveFromClan = new Notice("&8[&3ClanAdmin&8] &7Zostałeś wyrzucony z klanu przez administratora &b{admin}", NotificationType.CHAT);
        public Notice youAreAddToClan = new Notice("&8[&3ClanAdmin&8] &7Zostałeś dodany do klanu przez administratora &b{admin}", NotificationType.CHAT);

        public Notice warAllyJoined = new Notice("&aKlan {tag} dołączył do wojny po stronie {side}!", NotificationType.CHAT);
        public Notice warAllyInviteSent = new Notice("&aWysłano zaproszenie do wojny dla klanu {tag}.", NotificationType.CHAT);
        public Notice warAllyInviteReceived = new Notice("&eKlan {tag} prosi o wsparcie w wojnie! Wpisz /clan warjoin {tag}", NotificationType.CHAT);

        private static Notice sub(final String subtitle) {

            return new Notice("", NotificationType.TITLE, subtitle, 3, null, null);
        }
    }

    public WarScoreboard warScoreboard = new WarScoreboard();

    public static class WarScoreboard extends OkaeriConfig {

        public String title = "&c&lWOJNA KLANÓW";

        public List<String> lines = List.of(
                "&7Trwa od: &f{since}",
                " ",
                "&b{a} &7» &f{aPoints}&7/&f{target}",
                "&d{b} &7» &f{bPoints}&7/&f{target}",
                " ",
                "&7Cel: &f{target} &7pkt"
        );

        public int refreshSeconds = 1;
    }
}
