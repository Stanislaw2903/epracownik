package com.eurosoftit.ebok.common.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public interface IEntity<ID extends Serializable> extends Serializable {

    ID getId();

    @JsonIgnore
    boolean isNew();
}
