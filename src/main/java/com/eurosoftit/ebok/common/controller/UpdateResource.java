package com.eurosoftit.ebok.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Objects;

public interface UpdateResource<K extends Serializable, E extends IEntity<K>, D extends DTO<K>> {

    Service<K, E, D> getService();

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    default ResponseEntity<?> update(@RequestBody @Valid E entity) {

        try {
            if (Objects.isNull(entity.getId())) {
                return new ResponseEntity<>("{\"message\" : \"Id cannot be empty\"}", HttpStatus.CONFLICT);
            } else if (!getService().exists(entity.getId())) {
                return new ResponseEntity<>("{\"message\" : \"Object already exist\"}", HttpStatus.CONFLICT);
            }

            getService().save(entity);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
