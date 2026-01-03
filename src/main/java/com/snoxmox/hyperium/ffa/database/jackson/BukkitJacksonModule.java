package com.snoxmox.hyperium.ffa.database.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BukkitJacksonModule extends SimpleModule {

    public BukkitJacksonModule() {

        this.addSerializer(Vector.class, new VectorSerializer());
        this.addDeserializer(Vector.class, new VectorDeserializer());

        this.addSerializer(Location.class, new LocationSerializer());
        this.addDeserializer(Location.class, new LocationDeserializer());

        this.addSerializer(ConfigurationSerializable.class, new ConfigurationSerializableSerializer());
        this.addDeserializer(ConfigurationSerializable.class, new ConfigurationSerializableDeserializer());

        this.addSerializer(ItemStack.class, new ConfigurationSerializableSerializer());
        this.addDeserializer(ItemStack.class, new ItemStackDeserializer());
    }


    private static class ConfigurationSerializableSerializer extends JsonSerializer<ConfigurationSerializable> {
        @Override
        public void serialize(final ConfigurationSerializable value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            final Map<String, Object> map = new LinkedHashMap<>();
            map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(value.getClass()));
            map.putAll(value.serialize());
            gen.writeObject(map);
        }
    }

    private static class ConfigurationSerializableDeserializer extends JsonDeserializer<ConfigurationSerializable> {
        @Override
        public ConfigurationSerializable deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {

            final ObjectMapper mapper = (ObjectMapper) p.getCodec();

            final Map<String, Object> map = mapper.readValue(p, new TypeReference<>() {
            });

            return ConfigurationSerialization.deserializeObject(map);
        }
    }

    private static class ItemStackDeserializer extends JsonDeserializer<ItemStack> {
        @Override
        public ItemStack deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {

            final ObjectMapper mapper = (ObjectMapper) p.getCodec();
            final Map<String, Object> map = mapper.readValue(p, new TypeReference<>() {
            });

            if (!map.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, "org.bukkit.inventory.ItemStack");
            }
            return (ItemStack) ConfigurationSerialization.deserializeObject(map);
        }
    }


    private static class VectorSerializer extends JsonSerializer<Vector> {
        @Override
        public void serialize(final Vector value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("x", value.getX());
            gen.writeNumberField("y", value.getY());
            gen.writeNumberField("z", value.getZ());
            gen.writeEndObject();
        }
    }

    private static class VectorDeserializer extends JsonDeserializer<Vector> {
        @Override
        public Vector deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonNode node = p.getCodec().readTree(p);
            final double x = node.get("x").asDouble();
            final double y = node.get("y").asDouble();
            final double z = node.get("z").asDouble();
            return new Vector(x, y, z);
        }
    }


    private static class LocationSerializer extends JsonSerializer<Location> {
        @Override
        public void serialize(final Location value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            if (value.getWorld() != null) {
                gen.writeStringField("world", value.getWorld().getName());
            }
            gen.writeNumberField("x", value.getX());
            gen.writeNumberField("y", value.getY());
            gen.writeNumberField("z", value.getZ());
            gen.writeNumberField("yaw", value.getYaw());
            gen.writeNumberField("pitch", value.getPitch());
            gen.writeEndObject();
        }
    }

    private static class LocationDeserializer extends JsonDeserializer<Location> {
        @Override
        public Location deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonNode node = p.getCodec().readTree(p);

            final String worldName = node.has("world") ? node.get("world").asText() : "world";
            final World world = Bukkit.getWorld(worldName);

            final double x = node.get("x").asDouble();
            final double y = node.get("y").asDouble();
            final double z = node.get("z").asDouble();
            final float yaw = (float) node.get("yaw").asDouble();
            final float pitch = (float) node.get("pitch").asDouble();

            return new Location(world, x, y, z, yaw, pitch);
        }
    }
}