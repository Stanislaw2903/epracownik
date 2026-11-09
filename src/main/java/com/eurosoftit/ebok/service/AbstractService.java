package com.eurosoftit.ebok.service;

import com.eurosoftit.ebok.common.controller.IEntity;
import com.eurosoftit.ebok.domain.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class AbstractService<T extends IEntity<ID>, ID extends Serializable, P> {

    public abstract JpaRepository<T, ID> getRepository();

    public T save(T entity) {
        return getRepository().saveAndFlush(entity);
    }

    public void save(List<T> entity) {
        entity.stream().forEach(getRepository()::saveAndFlush);
    }

    public void delete(T entity) {
        getRepository().delete(entity);
    }

    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }


    public void delete(List<T> entity) {
        entity.stream().forEach(getRepository()::delete);
    }

    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    public Optional<T> findOne(ID value) {
        return getRepository().findById(value);
    }
}
