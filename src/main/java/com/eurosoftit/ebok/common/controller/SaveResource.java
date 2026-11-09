package com.eurosoftit.ebok.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Objects;

public interface SaveResource<K extends Serializable, E extends IEntity<K>, D extends DTO<K>> {

    Service<K, E, D> getService();

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    default ResponseEntity<?> create(@RequestBody @Valid E entity) {

        try {
            if (Objects.nonNull(entity.getId()) && getService().exists(entity.getId())) {
                return new ResponseEntity<>("{\"message\" : \"Object already exist\"}", HttpStatus.CONFLICT);
            }

            getService().save(entity);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
