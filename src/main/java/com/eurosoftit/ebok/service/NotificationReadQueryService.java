package com.eurosoftit.ebok.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.eurosoftit.ebok.domain.NotificationRead;
import com.eurosoftit.ebok.domain.*; // for static metamodels
import com.eurosoftit.ebok.repository.NotificationReadRepository;
import com.eurosoftit.ebok.service.dto.NotificationReadCriteria;

/**
 * Service for executing complex queries for {@link NotificationRead} entities in the database.
 * The main input is a {@link NotificationReadCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link NotificationRead} or a {@link Page} of {@link NotificationRead} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificationReadQueryService extends QueryService<NotificationRead> {

    private final Logger log = LoggerFactory.getLogger(NotificationReadQueryService.class);

    private final NotificationReadRepository notificationReadRepository;

    public NotificationReadQueryService(NotificationReadRepository notificationReadRepository) {
        this.notificationReadRepository = notificationReadRepository;
    }

    /**
     * Return a {@link List} of {@link NotificationRead} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<NotificationRead> findByCriteria(NotificationReadCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<NotificationRead> specification = createSpecification(criteria);
        return notificationReadRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link NotificationRead} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationRead> findByCriteria(NotificationReadCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<NotificationRead> specification = createSpecification(criteria);
        return notificationReadRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificationReadCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<NotificationRead> specification = createSpecification(criteria);
        return notificationReadRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificationReadCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<NotificationRead> createSpecification(NotificationReadCriteria criteria) {
        Specification<NotificationRead> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), NotificationRead_.id));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), NotificationRead_.date));
            }
            if (criteria.getNotify() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNotify(), NotificationRead_.notify));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(NotificationRead_.users, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
