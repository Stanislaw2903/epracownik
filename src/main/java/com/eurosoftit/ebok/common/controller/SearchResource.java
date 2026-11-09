package com.eurosoftit.ebok.common.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Serializable;

public interface SearchResource<K extends Serializable, E extends IEntity<K>, P, D extends DTO<K>> {

    SearchService<K, E, P> getSearchService();

    Service<K, E, D> getService();

    @GetMapping
    @ApiImplicitParams({
        @ApiImplicitParam(name = "sort", allowMultiple = true, dataTypeClass = String.class, paramType = "query",
            value = "Format: property,asc/desc"),
        @ApiImplicitParam(name = "page", dataTypeClass = Integer.class, paramType = "query"),
        @ApiImplicitParam(name = "size", dataTypeClass = Integer.class, paramType = "query")
    })
    default Page<D> search(P params, Pageable pageable) {
        return getService().getConverter().convert(getSearchService().search(params, pageable));
    }
}
