package com.snoxmox.hyperium.ffa.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.snoxmox.hyperium.ffa.configuration.MenuConfiguration;
import com.snoxmox.hyperium.ffa.configuration.MessageConfiguration;
import com.snoxmox.hyperium.ffa.helper.ItemHelper;
import com.snoxmox.hyperium.ffa.item.ConfigItem;
import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.KitManager;
import com.snoxmox.hyperium.ffa.kit.item.KitItem;
import com.snoxmox.hyperium.ffa.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerKitEditorGui {

    private final User user;
    private final KitManager kitManager;
    private final String kitName;
    private final MessageConfiguration messageConfiguration;
    private final MenuConfiguration menuConfiguration;

    private static final int OFFHAND_SLOT_ID = 40;
    private static final String OFFHAND_PLACEHOLDER_NAME = "§6§lDruga Ręka";

    public PlayerKitEditorGui(final User user, final KitManager kitManager, final String kitName,
                              final MessageConfiguration messageConfiguration, final MenuConfiguration menuConfiguration) {
        this.user = user;
        this.kitManager = kitManager;
        this.kitName = kitName;
        this.messageConfiguration = messageConfiguration;
        this.menuConfiguration = menuConfiguration;
    }

    public void open(final Player player) {
        final MenuConfiguration.PlayerEditorMenu config = this.menuConfiguration.playerEditorMenu;
        final String title = config.title.replace("{kit}", this.kitName);
        final ChestGui gui = new ChestGui(config.rows, title);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        final int guiOffHandSlot = (config.rows * 9) - 5;
        final int offHandX = guiOffHandSlot % 9;
        final int offHandY = guiOffHandSlot / 9;

        final StaticPane bgPane = new StaticPane(0, 0, 9, config.rows);
        for (final ConfigItem bgItem : config.background) {
            for (final int slot : bgItem.slots) {
                if (slot >= 0 && slot < config.rows * 9) {
                    bgPane.addItem(new GuiItem(bgItem.toItemStack(), event -> event.setCancelled(true)), slot % 9, slot / 9);
                }
            }
        }
        gui.addPane(bgPane);

        final StaticPane contentPane = new StaticPane(0, 0, 9, config.rows);
        contentPane.setOnClick(event -> event.setCancelled(false));

        final List<KitItem> itemsToLoad;
        if (this.user.hasCustomKit(this.kitName)) {
            itemsToLoad = this.user.getCustomKit(this.kitName);
        } else {
            final Kit defaultKit = this.kitManager.getKit(this.kitName);
            itemsToLoad = defaultKit != null ? defaultKit.getItems() : new ArrayList<>();
        }

        ItemStack startOffhandItem = null;

        for (final KitItem kitItem : itemsToLoad) {
            if (ItemHelper.isArmor(kitItem.getItem())) {
                continue;
            }

            final int slot = kitItem.getSlot();

            if (slot == OFFHAND_SLOT_ID) {
                startOffhandItem = kitItem.getItem().clone();
            } else if (slot >= 0 && slot < config.rows * 9) {
                contentPane.addItem(new GuiItem(kitItem.getItem()), slot % 9, slot / 9);
            }
        }

        this.refreshOffhandSlot(gui, contentPane, offHandX, offHandY, startOffhandItem, false);

        gui.addPane(contentPane);

        final StaticPane controlPane = new StaticPane(0, 0, 9, config.rows);

        final ConfigItem backConfig = config.backButton;
        if (backConfig.slot >= 0) {
            controlPane.addItem(new GuiItem(backConfig.toItemStack(), _ -> {
                new PlayerKitSelectorGui(this.kitManager, this.user, this.messageConfiguration, this.menuConfiguration).open(player);
            }), backConfig.slot % 9, backConfig.slot / 9);
        }

        final ConfigItem resetConfig = config.resetButton;
        if (resetConfig.slot >= 0) {
            controlPane.addItem(new GuiItem(resetConfig.toItemStack(), _ -> {
                final Kit defaultKit = this.kitManager.getKit(this.kitName);
                if (defaultKit != null) {
                    this.user.saveCustomKit(this.kitName, defaultKit.getItems());
                    this.messageConfiguration.playerKitDefaultRestored.send(player);
                    this.open(player);
                }
            }), resetConfig.slot % 9, resetConfig.slot / 9);
        }

        final ConfigItem saveConfig = config.saveButton;
        if (saveConfig.slot >= 0) {
            controlPane.addItem(new GuiItem(saveConfig.toItemStack(), _ -> {
                this.saveLayout(gui, guiOffHandSlot);
                this.messageConfiguration.playerKitLayoutSaved.send(player);
                new PlayerKitSelectorGui(this.kitManager, this.user, this.messageConfiguration, this.menuConfiguration).open(player);
            }), saveConfig.slot % 9, saveConfig.slot / 9);
        }

        gui.addPane(controlPane);
        gui.show(player);
    }

    private void refreshOffhandSlot(final ChestGui gui, final StaticPane pane, final int x, final int y, final ItemStack currentItem, final boolean updateRealInventory) {
        pane.removeItem(x, y);

        final boolean isEmpty = (currentItem == null || currentItem.getType() == Material.AIR);
        final ItemStack displayItem = isEmpty ? this.createOffhandPlaceholder() : currentItem;

        final GuiItem guiItem = new GuiItem(displayItem, event -> {
            event.setCancelled(true);

            final ItemStack cursor = event.getCursor();
            final boolean cursorEmpty = (cursor == null || cursor.getType() == Material.AIR);

            if (isEmpty) {
                if (!cursorEmpty) {
                    final ItemStack toPlace = cursor.clone();
                    event.getView().setCursor(null);
                    this.refreshOffhandSlot(gui, pane, x, y, toPlace, true);
                }
            } else {
                if (cursorEmpty) {
                    event.getView().setCursor(currentItem.clone());
                    this.refreshOffhandSlot(gui, pane, x, y, null, true);
                } else {
                    final ItemStack toPlace = cursor.clone();
                    event.getView().setCursor(currentItem.clone());
                    this.refreshOffhandSlot(gui, pane, x, y, toPlace, true);
                }
            }
        });

        pane.addItem(guiItem, x, y);

        if (updateRealInventory && gui.getInventory() != null) {
            final int absoluteSlot = (y * 9) + x;
            gui.getInventory().setItem(absoluteSlot, displayItem);
        }
    }

    private ItemStack createOffhandPlaceholder() {
        final ItemStack stack = new ItemStack(Material.SHIELD);
        final ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(OFFHAND_PLACEHOLDER_NAME);
            meta.setLore(List.of("§7To miejsce na przedmiot,", "§7który chcesz mieć w lewej ręce."));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private boolean isOffhandPlaceholder(final ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getType() == Material.SHIELD &&
                OFFHAND_PLACEHOLDER_NAME.equals(item.getItemMeta().getDisplayName());
    }

    private void saveLayout(final ChestGui gui, final int guiOffHandSlot) {
        final Inventory inv = gui.getInventory();
        final List<KitItem> newItems = new ArrayList<>();
        final MenuConfiguration.PlayerEditorMenu config = this.menuConfiguration.playerEditorMenu;

        final List<Integer> excludedSlots = new ArrayList<>();
        if (config.saveButton.slot >= 0) excludedSlots.add(config.saveButton.slot);
        if (config.backButton.slot >= 0) excludedSlots.add(config.backButton.slot);
        if (config.resetButton.slot >= 0) excludedSlots.add(config.resetButton.slot);
        for (final ConfigItem bg : config.background) {
            excludedSlots.addAll(bg.slots);
        }

        final List<KitItem> sourceItems;
        if (this.user.hasCustomKit(this.kitName)) {
            sourceItems = this.user.getCustomKit(this.kitName);
        } else {
            final Kit defaultKit = this.kitManager.getKit(this.kitName);
            sourceItems = defaultKit != null ? defaultKit.getItems() : new ArrayList<>();
        }

        for (final KitItem sourceItem : sourceItems) {
            if (ItemHelper.isArmor(sourceItem.getItem())) {
                if (sourceItem.getSlot() != OFFHAND_SLOT_ID) {
                    newItems.add(sourceItem);
                }
            }
        }

        for (int i = 0; i < inv.getSize(); i++) {
            if (excludedSlots.contains(i) && i != guiOffHandSlot) continue;

            final ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR && !this.isOffhandPlaceholder(item)) {
                if (i == guiOffHandSlot) {
                    newItems.add(new KitItem(OFFHAND_SLOT_ID, item.clone()));
                } else {
                    newItems.add(new KitItem(i, item.clone()));
                }
            }
        }
        this.user.saveCustomKit(this.kitName, newItems);
    }
}