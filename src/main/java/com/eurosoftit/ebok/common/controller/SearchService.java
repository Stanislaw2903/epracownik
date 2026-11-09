package com.eurosoftit.ebok.common.controller;

import com.eurosoftit.ebok.domain.specification.SpecificationReflectionBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

public interface SearchService<K extends Serializable, E extends IEntity<K>, P> {

    JpaSpecificationExecutor<E> getSearchRepository();

    default Specification<E> getSpecification(P params) {
        return SpecificationReflectionBuilder.<K, E, P>process(params);
    }

    default Page<E> search(P params, Pageable pageable) {
        return getSearchRepository().findAll(getSpecification(params), pageable);

    }
}
