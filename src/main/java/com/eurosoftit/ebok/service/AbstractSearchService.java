package com.eurosoftit.ebok.service;

import com.eurosoftit.ebok.common.controller.IEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

import static com.eurosoftit.ebok.domain.specification.SpecificationReflectionBuilder.process;

public abstract class AbstractSearchService<T extends IEntity<ID>, ID extends Serializable, P> extends AbstractService<T, ID, P> {

    public abstract JpaSpecificationExecutor<T> getSearchRepository();

    public Specification<T> getSpecification(P vacationProperties) {
        return process(vacationProperties);
    }

    public Page<T> findAll(P vacationProperties, Pageable pageable) {
        return getSearchRepository().findAll(getSpecification(vacationProperties), pageable);
    }
}
