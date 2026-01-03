package com.snoxmox.hyperium.ffa.configuration;

import eu.okaeri.configs.OkaeriConfig;
import com.snoxmox.hyperium.ffa.notice.Notice;
import com.snoxmox.hyperium.ffa.notice.NotificationType;

public class MessageConfiguration extends OkaeriConfig {

    public Notice killBroadcast = new Notice(
            "&7Gracz &c{victim}&c &8[&4{victim_health}❤&8]&7 został zabity na arenie {arena} z kitem {kit} przez gracza &a{killer}&a &8[&4{killer_health}❤&8]",
            NotificationType.CHAT
    );

    public Notice arenaAlreadyExists = new Notice("&cArena o tej nazwie już istnieje!", NotificationType.CHAT);
    public Notice arenaCreated = new Notice("&aStworzono arenę: {name}", NotificationType.CHAT);
    public Notice arenaDeleted = new Notice("&aUsunięto arenę: {name}", NotificationType.CHAT);
    public Notice arenaSetSpawn = new Notice("&aUstawiono spawn dla areny: {name}", NotificationType.CHAT);
    public Notice arenaListHeader = new Notice("&6Lista aren:", NotificationType.CHAT);
    public Notice arenaListEntry = new Notice("&e- {name}", NotificationType.CHAT);
    public Notice arenaNoSpawn = new Notice("&cTa arena nie ma ustawionego spawnu!", NotificationType.CHAT);
    public Notice arenaTeleportedSelf = new Notice("&aPrzeteleportowano na arenę: {name}", NotificationType.CHAT);
    public Notice arenaLeft = new Notice("&aOpusciles arene", NotificationType.CHAT);
    public Notice arenaSetKit = new Notice("&aUstawiono kit dla areny: {name}", NotificationType.CHAT);
    public Notice arenaSetDisplayName = new Notice("&aUstawiono wyświetlaną nazwę dla areny: {name}", NotificationType.CHAT);
    public Notice arenaInfoName = new Notice("&6Nazwa: &e{name}", NotificationType.CHAT);
    public Notice arenaInfoWorld = new Notice("&6World: &e{world}", NotificationType.CHAT);
    public Notice arenaInfoSpawn = new Notice("&6Spawn: &e{x}, {y}, {z}", NotificationType.CHAT);
    public Notice arenaInfoKit = new Notice("&6Kit: &e{kit}", NotificationType.CHAT);
    public Notice arenaInfoPlayers = new Notice("&6Ilość graczy: &e{count}", NotificationType.CHAT);

    public Notice kitAlreadyExists = new Notice("&cKit o tej nazwie już istnieje!", NotificationType.CHAT);
    public Notice kitCreated = new Notice("&aStworzono kit: {name}", NotificationType.CHAT);
    public Notice kitDeleted = new Notice("&aUsunięto kit: {name}", NotificationType.CHAT);
    public Notice kitListHeader = new Notice("&6Lista kitów:", NotificationType.CHAT);
    public Notice kitListEntry = new Notice("&e- {name} ({display})", NotificationType.CHAT);
    public Notice kitSetIconNoItem = new Notice("&cMusisz trzymać przedmiot w ręce!", NotificationType.CHAT);
    public Notice kitIconSet = new Notice("&aUstawiono ikonę kitu: {name}", NotificationType.CHAT);
    public Notice kitSetDisplayName = new Notice("&aUstawiono wyświetlaną nazwę kitu na: {name}", NotificationType.CHAT);
    public Notice kitSetBuild = new Notice("&aUstawiono tryb budowania dla kitu {name} na: {build}", NotificationType.CHAT);
    public Notice kitInfoHeader = new Notice("&6Informacje o kicie:", NotificationType.CHAT);
    public Notice kitInfoId = new Notice("&eID: {id}", NotificationType.CHAT);
    public Notice kitInfoDisplayName = new Notice("&eWyświetlana nazwa: {display}", NotificationType.CHAT);
    public Notice kitInfoItems = new Notice("&eIlość przedmiotów: {count}", NotificationType.CHAT);
    public Notice kitInfoBuild = new Notice("&eMożliwość budowania: {build}", NotificationType.CHAT);
    public Notice kitInfoIconPresent = new Notice("&eIkona: {material}", NotificationType.CHAT);
    public Notice kitInfoIconAbsent = new Notice("&cIkona: BRAK", NotificationType.CHAT);

    public Notice adminKitEditCancelled = new Notice("&cAnulowano edycję kitu.", NotificationType.CHAT);
    public Notice adminKitSaved = new Notice("&aPomyślnie zapisano zawartość kitu!", NotificationType.CHAT);

    public Notice playerKitDefaultRestored = new Notice("&ePrzywrócono domyślny układ.", NotificationType.CHAT);
    public Notice playerKitLayoutSaved = new Notice("&aZapisano Twój układ kitu!", NotificationType.CHAT);


}