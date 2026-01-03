package com.snoxmox.hyperium.ffa.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.snoxmox.hyperium.ffa.configuration.MenuConfiguration;
import com.snoxmox.hyperium.ffa.configuration.MessageConfiguration;
import com.snoxmox.hyperium.ffa.item.ConfigItem;
import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.item.KitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AdminKitEditorGui {

    private final Kit kit;
    private final MessageConfiguration messageConfiguration;
    private final MenuConfiguration menuConfiguration;

    private static final int OFFHAND_SLOT_ID = 40;
    private static final String OFFHAND_PLACEHOLDER_NAME = "§6§lDruga Ręka";

    public AdminKitEditorGui(final Kit kit, final MessageConfiguration messageConfiguration, final MenuConfiguration menuConfiguration) {
        this.kit = kit;
        this.messageConfiguration = messageConfiguration;
        this.menuConfiguration = menuConfiguration;
    }

    public void open(final Player player) {
        final MenuConfiguration.AdminEditorMenu config = this.menuConfiguration.adminEditorMenu;
        final String title = config.title.replace("{kit}", this.kit.getName());
        ChestGui gui = new ChestGui(config.rows, title);

        gui.setOnGlobalClick(event -> {
            if (event.isShiftClick()) {
                event.setCancelled(true);
            }
        });

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

        ItemStack startOffhandItem = null;

        for (final KitItem kitItem : this.kit.getItems()) {
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

        final ConfigItem cancelConfig = config.cancelButton;
        if (cancelConfig.slot >= 0) {
            controlPane.addItem(new GuiItem(cancelConfig.toItemStack(), _ -> {
                this.messageConfiguration.adminKitEditCancelled.send(player);
                player.closeInventory();
            }), cancelConfig.slot % 9, cancelConfig.slot / 9);
        }

        final ConfigItem saveConfig = config.saveButton;
        if (saveConfig.slot >= 0) {
            controlPane.addItem(new GuiItem(saveConfig.toItemStack(), _ -> {
                this.saveKitContent(gui, guiOffHandSlot);
                this.messageConfiguration.adminKitSaved.send(player);
                player.closeInventory();
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
            meta.setLore(List.of("§7Połóż tutaj przedmiot,", "§7aby zapisać go w drugiej ręce."));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private boolean isOffhandPlaceholder(final ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getType() == Material.SHIELD &&
                OFFHAND_PLACEHOLDER_NAME.equals(item.getItemMeta().getDisplayName());
    }

    private void saveKitContent(final ChestGui gui, final int guiOffHandSlot) {
        final Inventory inventory = gui.getInventory();
        final List<KitItem> newItems = new ArrayList<>();
        final MenuConfiguration.AdminEditorMenu config = this.menuConfiguration.adminEditorMenu;

        final List<Integer> excludedSlots = new ArrayList<>();
        if (config.saveButton.slot >= 0) excludedSlots.add(config.saveButton.slot);
        if (config.cancelButton.slot >= 0) excludedSlots.add(config.cancelButton.slot);
        for (final ConfigItem bg : config.background) {
            excludedSlots.addAll(bg.slots);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (excludedSlots.contains(i)) continue;

            final ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR && !this.isOffhandPlaceholder(item)) {
                if (i == guiOffHandSlot) {
                    newItems.add(new KitItem(OFFHAND_SLOT_ID, item.clone()));
                } else {
                    newItems.add(new KitItem(i, item.clone()));
                }
            }
        }
        this.kit.setItems(newItems);
    }
}