package com.snoxmox.hyperium.ffa.kit.serializer;

import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.item.KitItem;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class KitSerializer implements ObjectSerializer<Kit> {

    @Override
    public boolean supports(@NonNull final Class<?> type) {
        return false;
    }

    @Override
    public void serialize(@NonNull final Kit object, @NonNull final SerializationData data, @NonNull final GenericsDeclaration generics) {
        data.set("name", object.getName());
        data.set("displayItem", object.getDisplayItem());
        data.set("displayName", object.getDisplayName());
        data.set("build", object.isBuild());
        data.set("items", object.getItems());
    }

    @Override
    public Kit deserialize(@NonNull final DeserializationData data, @NonNull final GenericsDeclaration generics) {
        final String name = data.get("name", String.class);
        final String displayName = data.get("displayName", String.class);
        final boolean build = data.get("build", boolean.class);
        final ItemStack displayItem = data.get("displayItem", ItemStack.class);

        final List<KitItem> items = data.getAsList("items", KitItem.class);

        final Kit kit = new Kit(name);
        kit.setDisplayName(displayName);
        kit.setBuild(build);
        kit.setItems(items);
        kit.setDisplayItem(displayItem);

        return kit;
    }
}