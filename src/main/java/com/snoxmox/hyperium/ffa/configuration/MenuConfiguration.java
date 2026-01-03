package com.snoxmox.hyperium.ffa.configuration;

import com.snoxmox.hyperium.ffa.item.ConfigItem;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuConfiguration extends OkaeriConfig {

    @Comment("Konfiguracja menu wyboru zestawu (Selector)")
    public SelectorMenu selectorMenu = new SelectorMenu();

    @Comment("Konfiguracja menu edycji admina (Admin Editor)")
    public AdminEditorMenu adminEditorMenu = new AdminEditorMenu();

    @Comment("Konfiguracja menu edycji gracza (Player Editor)")
    public PlayerEditorMenu playerEditorMenu = new PlayerEditorMenu();

    @Comment("Przedmiot respawnu po śmierci")
    public ItemStack respawnItem = createRespawnItem();

    public static class SelectorMenu extends OkaeriConfig {
        public String title = "Wybierz zestaw do edycji";
        public int rows = 6;

        @Comment("Itemy dekoracyjne (tło)")
        public List<ConfigItem> background = List.of(createDecoration());

        @Comment("Przycisk poprzedniej strony")
        public ConfigItem previousPage = createButton(Material.ARROW, "<red>Poprzednia strona", 45);

        @Comment("Przycisk następnej strony")
        public ConfigItem nextPage = createButton(Material.ARROW, "<green>Następna strona", 53);

        @Comment("Wygląd ikony kitu w menu. Użyj {name} dla nazwy kitu.")
        public ConfigItem kitItemTemplate = createKitTemplate();
    }

    public static class AdminEditorMenu extends OkaeriConfig {
        public String title = "Edytor kitu: {kit}";
        public int rows = 6;
        public List<ConfigItem> background = List.of(createDecoration());

        public ConfigItem saveButton = createButton(Material.LIME_DYE, "<green>ZAPISZ ZMIANY", 53);
        public ConfigItem cancelButton = createButton(Material.RED_DYE, "<red>ANULUJ", 45);
    }

    public static class PlayerEditorMenu extends OkaeriConfig {
        public String title = "Edycja: {kit}";
        public int rows = 6;
        public List<ConfigItem> background = List.of(createDecoration());

        public ConfigItem saveButton = createButton(Material.LIME_DYE, "<green>ZAPISZ UKŁAD", 53);
        public ConfigItem resetButton = createButton(Material.YELLOW_DYE, "<yellow>PRZYWRÓĆ DOMYŚLNE", 49);
        public ConfigItem backButton = createButton(Material.RED_DYE, "<red>WRÓĆ / ANULUJ", 45);
    }

    private static ConfigItem createButton(final Material mat, final String name, final int slot) {
        final ConfigItem item = new ConfigItem();
        item.material = mat;
        item.name = name;
        item.slot = slot;
        return item;
    }

    private static ConfigItem createDecoration() {
        final ConfigItem item = new ConfigItem();
        item.material = Material.BLACK_STAINED_GLASS_PANE;
        item.name = " ";
        item.slots = Arrays.asList(46, 47, 48, 50, 51, 52);
        return item;
    }

    private static ConfigItem createKitTemplate() {
        final ConfigItem item = new ConfigItem();
        item.material = Material.IRON_SWORD;
        item.name = "<yellow>{name}";
        item.lore = Arrays.asList(
                "",
                "<gray>Kliknij, aby edytować układ!",
                "{status}"
        );
        return item;
    }
    private static ItemStack createRespawnItem() {
        final ItemStack item = new ItemStack(Material.FEATHER);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("§aRespawn"));
            meta.lore(Arrays.asList(
                    Component.text("§7Kliknij, aby się zrespawnować"),
                    Component.text("§7po śmierci w trybie FFA.")
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
}