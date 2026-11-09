package com.eurosoftit.ebok.service;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.JoinType;

import com.eurosoftit.ebok.web.rest.group.UserGroupEntity_;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.eurosoftit.ebok.domain.Notification;
import com.eurosoftit.ebok.domain.*; // for static metamodels
import com.eurosoftit.ebok.repository.NotificationRepository;
import com.eurosoftit.ebok.service.dto.NotificationCriteria;

/**
 * Service for executing complex queries for {@link Notification} entities in the database.
 * The main input is a {@link NotificationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Notification} or a {@link Page} of {@link Notification} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificationQueryService extends QueryService<Notification> {

    private final Logger log = LoggerFactory.getLogger(NotificationQueryService.class);

    private final NotificationRepository notificationRepository;

    public NotificationQueryService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Return a {@link List} of {@link Notification} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Notification> findByCriteria(NotificationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Notification> specification = createSpecification(criteria);
        return notificationRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Notification} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Notification> findByCriteria(NotificationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Notification> specification = createSpecification(criteria);
        return notificationRepository.findAll(specification, page);
    }

    /**
     * Return {@link Notification} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Optional <Notification> findOneByCriteria(NotificationCriteria criteria) {
        log.debug("find by criteria : {}, page: {}", criteria);
        final Specification<Notification> specification = createSpecification(criteria);
        return notificationRepository.findOne(specification);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Notification> specification = createSpecification(criteria);
        return notificationRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Notification> createSpecification(NotificationCriteria criteria) {
        Specification<Notification> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Notification_.id));
            }
            if (criteria.getSubject() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSubject(), Notification_.subject));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Notification_.description));
            }
            if (criteria.getCreateDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreateDate(), Notification_.createDate));
            }
            if (criteria.getReadDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReadDate(), Notification_.readDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Notification_.status));
            }
            if (criteria.getSendNotification() != null) {
                specification = specification.and(buildSpecification(criteria.getSendNotification(), Notification_.sendNotification));
            }
            if (criteria.getCategory() != null) {
                specification = specification.and(buildSpecification(criteria.getCategory(), Notification_.category));
            }
            if (criteria.getCreatedById() != null) {
                specification = specification.and(buildSpecification(criteria.getCreatedById(),
                    root -> root.join(Notification_.createdBy, JoinType.LEFT).get(User_.id)));
            }


            if (criteria.getRecepientId() != null || criteria.getCanal() != null || criteria.getGroupId() != null) {

                Specification recipients = buildSpecification(criteria.getRecepientId(),
                    root -> root.join(Notification_.recepients, JoinType.LEFT).get(User_.id));
                Specification canal = buildSpecification(criteria.getCanal(), Notification_.canal).and(Specification.not(buildSpecification(criteria.getNotCreatedById(),
                    root -> root.join(Notification_.createdBy, JoinType.LEFT).get(User_.id))));
                Specification groups = criteria.getGroupId() == null ? null : buildSpecification(criteria.getGroupId(),
                    root -> root.join(Notification_.groups, JoinType.LEFT).get(UserGroupEntity_.id)).and(Specification.not(buildSpecification(criteria.getNotCreatedById(),
                    root -> root.join(Notification_.createdBy, JoinType.LEFT).get(User_.id))));

                specification =  specification.and(or(recipients, canal, groups));
            }

        }
        return specification;
    }

    private Specification or(Specification... specifications) {
        if(ArrayUtils.isEmpty(specifications)) {
           return null;
        }

        Specification temp = null;
        for (Specification specification : Arrays.stream(specifications).filter(Objects::nonNull).collect(Collectors.toList())) {
            if(temp == null) {
                temp = specification;
            }else {
                temp = temp.or(specification);
            }
        }

        return temp;
    }
}
