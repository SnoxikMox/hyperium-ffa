package com.snoxmox.hyperium.ffa.helper;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class ItemHelper {

    public static boolean isArmor(final ItemStack itemStack) {
        return itemStack.getType().getEquipmentSlot().isArmor();
    }

    public static boolean setItemIfArmor(final Player player, final ItemStack itemStack) {
        final EquipmentSlot slot = itemStack.getType().getEquipmentSlot();
        return switch (slot) {
            case HEAD, CHEST, LEGS, FEET -> {
                player.getInventory().setItem(slot, itemStack);
                yield true;
            }
            default -> false;
        };
    }


}
