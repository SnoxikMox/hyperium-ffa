package com.snoxmox.hyperium.ffa.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.snoxmox.hyperium.ffa.arena.Arena;
import com.snoxmox.hyperium.ffa.database.repository.Identifiable;
import com.snoxmox.hyperium.ffa.helper.ItemHelper;
import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.item.KitItem;
import com.snoxmox.hyperium.ffa.user.stats.Statistic;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Identifiable<UUID> {

    private UUID uuid;
    private Map<String, List<KitItem>> customKits = new ConcurrentHashMap<>();
    private Statistic statistic;

    private String clanTag = null;
    private long clanJoinAtMillis = 0L;

    @JsonIgnore
    private transient Arena currentArena = null;

    @JsonIgnore
    private transient Arena lastArena = null;

    public User() {}

    public User(final UUID uuid) {
        this.uuid = uuid;
        this.statistic = new Statistic();

    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void saveCustomKit(final String kitName, final List<KitItem> newItems) {
        this.customKits.put(kitName, newItems);
    }

    public void equipKit(final Player player, final Kit kit) {
        player.getInventory().clear();

        final Collection<KitItem> itemsToEquip = this.hasCustomKit(kit.getName())
                ? this.customKits.get(kit.getName())
                : kit.getItems();

        itemsToEquip.forEach(kitItem -> {
            final ItemStack item = kitItem.getItem();
            final int slot = kitItem.getSlot();
            if (slot == 40) {
                player.getInventory().setItemInOffHand(item);
                return;
            }

            if (ItemHelper.setItemIfArmor(player, item)) {
                return;
            }

            if (slot >= 0 && slot < 36) {
                player.getInventory().setItem(slot, item);
            } else {
                player.getInventory().addItem(item);
            }
        });
    }

    public boolean hasCustomKit(final String name) {
        return this.customKits.containsKey(name);
    }

    public List<KitItem> getCustomKit(final String name) {
        return this.customKits.get(name);
    }

    public Arena getCurrentArena() {
        return this.currentArena;
    }

    public void setCurrentArena(final Arena currentArena) {
        this.currentArena = currentArena;
    }

    public Map<String, List<KitItem>> getCustomKits() {
        return this.customKits;
    }

    public Statistic getStatistic() {
        if (this.statistic == null) {
            this.statistic = new Statistic();
        }
        return this.statistic;
    }

    public String getClanTag() {
        return this.clanTag;
    }

    public void setClanTag(final String clanTag) {
        this.clanTag = clanTag;
    }

    public long getClanJoinAtMillis() {
        return this.clanJoinAtMillis;
    }

    public void setClanJoinAtMillis(final long clanJoinAtMillis) {
        this.clanJoinAtMillis = clanJoinAtMillis;
    }

    public Arena getLastArena() {
        return this.lastArena;
    }

    public void setLastArena(final Arena lastArena) {
        this.lastArena = lastArena;
    }

    @JsonIgnore
    public boolean hasClan() {
        return this.clanTag != null && !this.clanTag.isBlank();
    }

    @Override
    @JsonIgnore
    public UUID getId() {
        return this.uuid;
    }
}