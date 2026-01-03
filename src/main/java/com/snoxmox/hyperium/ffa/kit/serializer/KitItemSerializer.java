package com.snoxmox.hyperium.ffa.kit.serializer;

import com.snoxmox.hyperium.ffa.kit.item.KitItem;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class KitItemSerializer implements ObjectSerializer<KitItem> {

    @Override
    public boolean supports(@NonNull final Class<?> type) {
        return false;
    }

    @Override
    public void serialize(@NonNull final KitItem object, @NonNull final SerializationData data, @NonNull final GenericsDeclaration generics) {
        data.add("slot", object.getSlot());
        data.add("item", object.getItem());
    }

    @Override
    public KitItem deserialize(@NonNull final DeserializationData data, @NonNull final GenericsDeclaration generics) {
        final int slot = data.get("slot", int.class);
        final ItemStack item = data.get("item", ItemStack.class);

        return new KitItem(slot, item);
    }
}