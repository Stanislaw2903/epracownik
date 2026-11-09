package com.eurosoftit.ebok.common.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;
import java.util.Objects;

public interface DeleteResource<K extends Serializable, E extends IEntity<K>, D extends DTO<K>> {

    Service<K, E, D> getService();


    @DeleteMapping(value = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    default ResponseEntity<?> delete(@PathVariable("id") K id) {

        try {
            if (Objects.isNull(id)) {
                return new ResponseEntity<>("{\"message\" : \"Id cannot be empty\"}", HttpStatus.CONFLICT);
            } else if (!getService().exists(id)) {
                return new ResponseEntity<>("{\"message\" : \"Object already exist\"}", HttpStatus.CONFLICT);
            }

            getService().delete(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
