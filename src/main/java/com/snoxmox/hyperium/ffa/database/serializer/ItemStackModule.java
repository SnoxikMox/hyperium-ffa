package com.snoxmox.hyperium.ffa.database.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemStackModule extends SimpleModule {

    public ItemStackModule() {
        this.addSerializer(ItemStack.class, new ItemStackSerializer());
        this.addDeserializer(ItemStack.class, new ItemStackDeserializer());
    }

    /**
     * Serializer: ItemStack -> Map -> JSON
     */
    private static class ItemStackSerializer extends JsonSerializer<ItemStack> {
        @Override
        public void serialize(final ItemStack item, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            if (item == null) {
                gen.writeNull();
                return;
            }

            // Zamieniamy ItemStack na Mapę używając API Bukkita
            final Map<String, Object> serialized = item.serialize();

            // Rekurencyjnie serializujemy mapę (żeby obsłużyć ItemMeta w środku)
            gen.writeStartObject();
            for (final Map.Entry<String, Object> entry : serialized.entrySet()) {
                gen.writeFieldName(entry.getKey());
                this.serializeObject(entry.getValue(), gen);
            }
            gen.writeEndObject();
        }

        private void serializeObject(final Object value, final JsonGenerator gen) throws IOException {
            if (value instanceof final ConfigurationSerializable serializable) {
                // Jeśli obiekt to np. ItemMeta, też go zamieniamy na mapę
                final Map<String, Object> map = serializable.serialize();
                gen.writeStartObject();
                for (final Map.Entry<String, Object> entry : map.entrySet()) {
                    gen.writeFieldName(entry.getKey());
                    this.serializeObject(entry.getValue(), gen);
                }
                gen.writeEndObject();
            } else if (value instanceof final Map<?, ?> map) {
                gen.writeStartObject();
                for (final Map.Entry<?, ?> entry : map.entrySet()) {
                    gen.writeFieldName(entry.getKey().toString());
                    this.serializeObject(entry.getValue(), gen);
                }
                gen.writeEndObject();
            } else if (value instanceof final Iterable<?> iterable) {
                gen.writeStartArray();
                for (final Object o : iterable) {
                    this.serializeObject(o, gen);
                }
                gen.writeEndArray();
            } else {
                // Proste typy (String, Integer, Boolean)
                gen.writeObject(value);
            }
        }
    }

    /**
     * Deserializer: JSON -> Map -> ItemStack
     */
    private static class ItemStackDeserializer extends JsonDeserializer<ItemStack> {
        @Override
        public ItemStack deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonNode node = p.getCodec().readTree(p);

            // Jackson zamienia JSON na JsonNode, my musimy to zmienić na Map<String, Object>
            // jaką rozumie Bukkit (musi mieć odpowiednie typy liczb!)
            final Map<String, Object> map = (Map<String, Object>) this.toJavaType(node);

            if (map == null) return null;

            try {
                // Magia Bukkita - odtwarza item ze wszystkimi detalami
                return ItemStack.deserialize(map);
            } catch (final Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Object toJavaType(final JsonNode node) {
            if (node.isObject()) {
                final Map<String, Object> map = new LinkedHashMap<>();
                node.fields().forEachRemaining(entry ->
                        map.put(entry.getKey(), this.toJavaType(entry.getValue()))
                );
                // Bardzo ważne: Bukkit wymaga specyficznego klucza dla serializacji
                if (map.containsKey("==")) {
                    // Tu nic nie musimy robić, ItemStack.deserialize to obsłuży
                }
                return map;
            } else if (node.isArray()) {
                final var list = new java.util.ArrayList<>();
                for (final JsonNode element : node) {
                    list.add(this.toJavaType(element));
                }
                return list;
            } else if (node.isInt()) {
                return node.asInt();
            } else if (node.isDouble()) {
                return node.asDouble();
            } else if (node.isBoolean()) {
                return node.asBoolean();
            } else if (node.isLong()) {
                return node.asLong(); // Bukkit czasem woli Int, ale Long jest bezpieczny
            } else if (node.isTextual()) {
                return node.asText();
            }
            return null;
        }
    }
}