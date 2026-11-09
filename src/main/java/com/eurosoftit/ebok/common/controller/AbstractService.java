package com.eurosoftit.ebok.common.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AbstractService<entityT extends IEntity<ID>, ID extends Serializable> {

	entityT save(entityT entity);

	List<entityT> save(Collection<entityT> entities);

	void deleteById(entityT entity);

	void deleteById(Collection<entityT> entities);

	void deleteById(ID id);

	Optional<entityT> findById(ID id);

	long count();

	boolean exists(ID id);

	Page<entityT> findAll(Pageable pageable);

	void deleteAll();

	void clear();

	entityT getReference(ID id);

	EntityManager getEntityManager();

	List<entityT> findAll();
}
