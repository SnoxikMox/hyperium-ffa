package com.snoxmox.hyperium.ffa.database.impl;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.mysql.MySQLConnectionBuilder;
import com.snoxmox.hyperium.ffa.configuration.DatabaseConfiguration;

import com.snoxmox.hyperium.ffa.database.repository.AbstractRepository;
import com.snoxmox.hyperium.ffa.database.repository.Identifiable;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import java.util.List;

public class MySQLRepository<K, V extends Identifiable<K>> extends AbstractRepository<K, V> {

    private final Connection connection;
    private final String tableName;
    private final String selectSql;
    private final String upsertSql;

    public MySQLRepository(final Class<V> type, final DatabaseConfiguration config, final String tableName) {
        super(type);
        this.tableName = tableName;

        final var jdbcUrl = "jdbc:mysql://%s:%s/%s?user=%s&password=%s".formatted(
                config.host, config.port, config.database, config.username, config.password
        );

        this.connection = MySQLConnectionBuilder.createConnectionPool(jdbcUrl);
        this.selectSql = "SELECT data FROM " + this.tableName + " WHERE id = ?";
        this.upsertSql = "INSERT INTO " + this.tableName + " (id, data) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE data = VALUES(data)";
    }

    @Override
    public void init() {
        this.connection.connect().thenAccept(c -> {
            final var sql = """
                CREATE TABLE IF NOT EXISTS %s (
                    id VARCHAR(64) PRIMARY KEY,
                    data JSON NOT NULL
                )
                """.formatted(this.tableName);

            c.sendPreparedStatement(sql);
        }).join();
    }

    @Override
    public CompletableFuture<V> load(final K id) {

        return this.connection.sendPreparedStatement(this.selectSql, List.of(id.toString()))
                .thenApply(result -> {
                    if (result.getRows().isEmpty()) {
                        return null;
                    }

                    try {
                        final var json = result.getRows().getFirst().getString("data");
                        return this.mapper.readValue(json, this.type);
                    } catch (final Exception e) {
                        throw new RuntimeException("Failed to deserialize JSON for id: " + id, e);
                    }
                })
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> save(final V value) {
        try {
            final var json = this.mapper.writeValueAsString(value);
            final var idString = value.getId().toString();

            return this.connection.sendPreparedStatement(this.upsertSql, Arrays.asList(idString, json))
                    .thenAccept(r -> {})
                    .toCompletableFuture();

        } catch (final Exception e) {
            return CompletableFuture.failedFuture(e);
        }
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
        this.connection.disconnect();
    }
}