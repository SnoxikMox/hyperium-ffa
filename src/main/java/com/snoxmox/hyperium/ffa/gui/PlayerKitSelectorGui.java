package com.snoxmox.hyperium.ffa.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.snoxmox.hyperium.ffa.configuration.MenuConfiguration;
import com.snoxmox.hyperium.ffa.configuration.MessageConfiguration;
import com.snoxmox.hyperium.ffa.item.ConfigItem;
import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.KitManager;
import com.snoxmox.hyperium.ffa.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerKitSelectorGui {

    private final KitManager kitManager;
    private final User user;
    private final MessageConfiguration messageConfiguration;
    private final MenuConfiguration menuConfiguration;

    public PlayerKitSelectorGui(final KitManager kitManager, final User user, final MessageConfiguration messageConfiguration, final MenuConfiguration menuConfiguration) {
        this.kitManager = kitManager;
        this.user = user;
        this.messageConfiguration = messageConfiguration;
        this.menuConfiguration = menuConfiguration;
    }

    public void open(final Player player) {
        final MenuConfiguration.SelectorMenu config = this.menuConfiguration.selectorMenu;
        final ChestGui gui = new ChestGui(config.rows, config.title);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        final StaticPane backgroundPane = new StaticPane(0, 0, 9, config.rows);
        for (final ConfigItem bgItem : config.background) {
            for (final int slot : bgItem.slots) {
                if (slot >= 0 && slot < config.rows * 9) {
                    backgroundPane.addItem(new GuiItem(bgItem.toItemStack()), slot % 9, slot / 9);
                }
            }
        }
        gui.addPane(backgroundPane);

        final PaginatedPane pages = new PaginatedPane(0, 0, 9, config.rows - 1);
        final List<GuiItem> items = new ArrayList<>();
        final MiniMessage mm = MiniMessage.miniMessage();

        for (final Kit kit : this.kitManager.getKits()) {
            final ConfigItem template = config.kitItemTemplate;
            final ItemStack icon = kit.getDisplayItem() != null ? kit.getDisplayItem().clone() : template.toItemStack();
            if (kit.getDisplayItem() == null) {
                icon.setType(template.material);
            }

            final ItemMeta meta = icon.getItemMeta();
            if (template.name != null) {
                final String displayName = template.name.replace("{name}", kit.getDisplayName());
                meta.displayName(mm.deserialize(displayName));
            }

            final List<Component> lore = new ArrayList<>();
            final String statusLine = this.user.hasCustomKit(kit.getName())
                    ? "<green>Posiadasz własny układ."
                    : "<dark_gray>Używasz domyślnego układu.";

            for (final String line : template.lore) {
                final String replaced = line
                        .replace("{name}", kit.getDisplayName())
                        .replace("{status}", statusLine);
                lore.add(mm.deserialize(replaced));
            }
            meta.lore(lore);
            icon.setItemMeta(meta);

            items.add(new GuiItem(icon, _ -> {
                new PlayerKitEditorGui(this.user, this.kitManager, kit.getName(), this.messageConfiguration, this.menuConfiguration).open(player);
            }));
        }

        pages.populateWithGuiItems(items);
        gui.addPane(pages);

        final StaticPane navigation = new StaticPane(0, 0, 9, config.rows);
        final ConfigItem prevConfig = config.previousPage;
        if (prevConfig.slot >= 0) {
            navigation.addItem(new GuiItem(prevConfig.toItemStack(), _ -> {
                if (pages.getPage() > 0) {
                    pages.setPage(pages.getPage() - 1);
                    gui.update();
                }
            }), prevConfig.slot % 9, prevConfig.slot / 9);
        }

        final ConfigItem nextConfig = config.nextPage;
        if (nextConfig.slot >= 0) {
            navigation.addItem(new GuiItem(nextConfig.toItemStack(), _ -> {
                if (pages.getPage() < pages.getPages() - 1) {
                    pages.setPage(pages.getPage() + 1);
                    gui.update();
                }
            }), nextConfig.slot % 9, nextConfig.slot / 9);
        }

        gui.addPane(navigation);
        gui.show(player);
    }
}