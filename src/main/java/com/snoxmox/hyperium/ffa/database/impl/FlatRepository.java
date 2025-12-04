package com.snoxmox.hyperium.ffa.database.impl;

import com.fasterxml.jackson.databind.SerializationFeature;

import com.snoxmox.hyperium.ffa.database.repository.AbstractRepository;
import com.snoxmox.hyperium.ffa.database.repository.Identifiable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlatRepository<K, V extends Identifiable<K>> extends AbstractRepository<K, V> {

    private final Path directory;
    private final ExecutorService executor;

    public FlatRepository(final Class<V> type, final String folderPath) {
        super(type);
        this.directory = Path.of(folderPath);
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void init() {
        try {
            if (Files.notExists(this.directory)) {
                Files.createDirectories(this.directory);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Could not create directory: " + this.directory, e);
        }
    }

    @Override
    public CompletableFuture<V> load(final K id) {
        return CompletableFuture.supplyAsync(() -> {
            final var path = this.directory.resolve(id.toString() + ".json");

            if (Files.notExists(path)) {
                return null;
            }

            try {
                return this.mapper.readValue(path.toFile(), this.type);
            } catch (final IOException e) {
                throw new RuntimeException("Failed to load data for id: " + id, e);
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> save(final V value) {
        return CompletableFuture.runAsync(() -> {
            final var path = this.directory.resolve(value.getId().toString() + ".json");
            try {
                this.mapper.writeValue(path.toFile(), value);
            } catch (final IOException e) {
                throw new RuntimeException("Failed to save data for id: " + value.getId(), e);
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> saveAll(final List<V> values) {
        if (values.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        final var futures = values.stream()
                .map(this::save)
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }


    @Override
    public void close() {
        this.executor.shutdown();
    }
}