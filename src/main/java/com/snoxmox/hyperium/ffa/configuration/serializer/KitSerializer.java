package com.snoxmox.hyperium.ffa.configuration.serializer;

import com.snoxmox.hyperium.ffa.kit.Kit;
import com.snoxmox.hyperium.ffa.kit.KitManager;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

public record KitSerializer(KitManager kitManager) implements ObjectSerializer<Kit> {
    @Override
    public boolean supports(@NotNull final Class<?> type) {
        return Kit.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull final Kit object, @NotNull final SerializationData data, @NotNull final GenericsDeclaration generics) {

    }

    @Override
    public Kit deserialize(@NotNull final DeserializationData data, @NotNull final GenericsDeclaration generics) {
        return null;
    }
}
