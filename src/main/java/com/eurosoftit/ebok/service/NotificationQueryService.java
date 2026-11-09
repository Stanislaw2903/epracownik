package com.eurosoftit.ebok.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import io.github.jhipster.service.filter.LongFilter;

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
import org.springframework.util.CollectionUtils;

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

    @Transactional(readOnly = true)
    public Page<Notification> findByCriteria(NotificationCriteria criteria, Specification<Notification> scope, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        return notificationRepository.findAll(createSpecification(criteria).and(scope), page);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(NotificationCriteria criteria, Specification<Notification> scope) {
        log.debug("count by criteria : {}", criteria);
        return notificationRepository.count(createSpecification(criteria).and(scope));
    }

    @Transactional(readOnly = true)
    public boolean isInScope(Long id, Specification<Notification> scope) {
        Specification<Notification> byId = (root, query, builder) -> builder.equal(root.get(Notification_.id), id);
        return notificationRepository.count(byId.and(scope)) > 0;
    }

    public static Specification<Notification> involving(Collection<Long> userIds, Collection<Long> groupIds) {
        return (root, query, builder) -> {
            List<Predicate> matches = new ArrayList<>();
            if (!CollectionUtils.isEmpty(userIds)) {
                Subquery<Long> sent = query.subquery(Long.class);
                Root<Notification> sentRoot = sent.from(Notification.class);
                sent.select(sentRoot.get(Notification_.id))
                    .where(sentRoot.join(Notification_.createdBy).get(User_.id).in(userIds));
                matches.add(root.get(Notification_.id).in(sent));

                Subquery<Long> received = query.subquery(Long.class);
                Root<Notification> receivedRoot = received.from(Notification.class);
                received.select(receivedRoot.get(Notification_.id))
                    .where(receivedRoot.join(Notification_.recepients).get(User_.id).in(userIds));
                matches.add(root.get(Notification_.id).in(received));
            }
            if (!CollectionUtils.isEmpty(groupIds)) {
                Subquery<Long> toGroup = query.subquery(Long.class);
                Root<Notification> groupRoot = toGroup.from(Notification.class);
                toGroup.select(groupRoot.get(Notification_.id))
                    .where(groupRoot.join(Notification_.groups).get(UserGroupEntity_.id).in(groupIds));
                matches.add(root.get(Notification_.id).in(toGroup));
            }
            return matches.isEmpty() ? builder.disjunction() : builder.or(matches.toArray(new Predicate[0]));
        };
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

                Specification<Notification> recipients = criteria.getRecepientId() == null ? null : recipientIn(criteria.getRecepientId());
                Specification<Notification> canal = criteria.getCanal() == null ? null
                    : notCreatedBy(buildSpecification(criteria.getCanal(), Notification_.canal), criteria);
                Specification<Notification> groups = criteria.getGroupId() == null ? null : notCreatedBy(buildSpecification(criteria.getGroupId(),
                    root -> root.join(Notification_.groups, JoinType.LEFT).get(UserGroupEntity_.id)), criteria);

                specification = specification.and(or(recipients, canal, groups));
            }

        }
        return specification;
    }

    private Specification<Notification> notCreatedBy(Specification<Notification> specification, NotificationCriteria criteria) {
        if (criteria.getNotCreatedById() == null) {
            return specification;
        }
        return specification.and(Specification.not(buildSpecification(criteria.getNotCreatedById(),
            root -> root.join(Notification_.createdBy, JoinType.LEFT).get(User_.id))));
    }

    static List<Long> filterIds(LongFilter filter) {
        if (filter.getIn() != null && !filter.getIn().isEmpty()) {
            return filter.getIn();
        }
        return filter.getEquals() == null ? Collections.emptyList() : Collections.singletonList(filter.getEquals());
    }

    private Specification<Notification> recipientIn(LongFilter filter) {
        List<Long> ids = filterIds(filter);
        return (root, query, builder) -> {
            if (ids.isEmpty()) {
                return builder.disjunction();
            }
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Notification> notification = subquery.from(Notification.class);
            subquery.select(notification.get(Notification_.id))
                .where(notification.join(Notification_.recepients).get(User_.id).in(ids));
            return root.get(Notification_.id).in(subquery);
        };
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
