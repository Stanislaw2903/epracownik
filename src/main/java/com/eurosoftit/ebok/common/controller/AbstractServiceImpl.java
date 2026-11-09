package com.eurosoftit.ebok.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class AbstractServiceImpl<entityT extends IEntity<ID>, ID extends Serializable> implements AbstractService<entityT, ID> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected abstract JpaRepository<entityT, ID> getRepo();

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public void clear() {
		entityManager.clear();
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public entityT save(final entityT entity) {
		return getRepo().saveAndFlush(entity);
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public List<entityT> save(final Collection<entityT> entities) {
		List<entityT> result = new ArrayList<entityT>();

		if (entities == null) {
			return result;
		}

			for (entityT entity : entities) {
				try {
					log.debug("save entity {} ", entity);
					result.add(save(entity));
					log.debug("save entity END, id = {} ", entity.getId());
				} catch (Throwable e) {
					log.error("error during save", e);
					throw e;
				}
			}


		return result;
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteById(final entityT entity) {
		getRepo().delete(entity);
		getRepo().flush();
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteById(final Collection<entityT> entities) {
		getRepo().deleteAll(entities);
		getRepo().flush();
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteById(final ID id) {
		getRepo().deleteById(id);
		getRepo().flush();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<entityT> findById(final ID id) {
		return getRepo().findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public long count() {
		return getRepo().count();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean exists(ID id) {
		return getRepo().existsById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<entityT> findAll(Pageable pageable) {
		return getRepo().findAll(pageable);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteAll() {
		getRepo().deleteAll();
		getRepo().flush();
	}

	@Override
	public List<entityT> findAll() {
		return getRepo().findAll();
	}

	@Override
	public entityT getReference(ID id) {
		return getRepo().getOne(id);
	}
}
