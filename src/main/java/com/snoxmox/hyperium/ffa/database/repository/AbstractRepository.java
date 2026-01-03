package com.snoxmox.hyperium.ffa.database.repository;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.snoxmox.hyperium.ffa.database.serializer.ItemStackModule;

public abstract class AbstractRepository<K, V extends Identifiable<K>> implements Repository<K, V> {

    protected final ObjectMapper mapper;
    protected final Class<V> type;

    public AbstractRepository(final Class<V> type) {
        this.type = type;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        this.mapper.registerModule(new ItemStackModule());
    }
}