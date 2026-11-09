package com.eurosoftit.ebok.common.controller;

import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface Service<K extends Serializable, E extends IEntity<K>, D extends DTO<K>> {

    JpaRepository<E, K> getRepository();

    ContentConverter<E, K, D> getConverter();

    default Optional<E> findById(K key) {
        return getRepository().findById(key);
    }

    default void delete(K key) {
        getRepository().deleteById(key);
    }

    default E save(E entity) {
        return getRepository().save(entity);
    }

    default boolean exists(K key) {
        return getRepository().existsById(key);
    }

    default List<E> findAll() {
        return getRepository().findAll();
    }

}

