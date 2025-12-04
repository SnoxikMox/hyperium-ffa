package com.snoxmox.hyperium.ffa.database.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface Repository<K, V extends Identifiable<K>> {

    void init();

    CompletableFuture<V> load(K id);

    CompletableFuture<Void> save(V value);

    CompletableFuture<Void> saveAll(List<V> values);

    void close();
}