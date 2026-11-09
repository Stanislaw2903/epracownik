package com.eurosoftit.ebok.common.controller;


import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

public interface ContentConverter<entityT extends IEntity<ID>, ID extends Serializable, T extends DTO<ID>> {
    entityT convert(T t);

    T convert(entityT entity);

    List<T> convert(List<entityT> entityList);

    Page<T> convert(Page<entityT> entityList);
}
