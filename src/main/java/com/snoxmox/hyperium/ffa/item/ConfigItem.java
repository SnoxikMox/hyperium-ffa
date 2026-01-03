package com.snoxmox.hyperium.ffa.item;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigItem extends OkaeriConfig {

    public Material material = Material.STONE;
    public String name = "Nazwa przedmiotu";
    public List<String> lore = Collections.emptyList();
    public int slot = -1;
    public List<Integer> slots = new ArrayList<>();
    public boolean glow = false;

    public ItemStack toItemStack() {
        final ItemStack item = new ItemStack(this.material);
        final ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            final MiniMessage mm = MiniMessage.miniMessage();
            if (this.name != null) {
                meta.displayName(mm.deserialize(this.name));
            }
            if (this.lore != null && !this.lore.isEmpty()) {
                final List<Component> loreComponents = new ArrayList<>();
                for (final String line : this.lore) {
                    loreComponents.add(mm.deserialize(line));
                }
                meta.lore(loreComponents);
            }
            if (this.glow) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}