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

import com.eurosoftit.ebok.domain.Qualifications;
import com.eurosoftit.ebok.domain.*; // for static metamodels
import com.eurosoftit.ebok.repository.QualificationsRepository;
import com.eurosoftit.ebok.service.dto.QualificationsCriteria;

/**
 * Service for executing complex queries for {@link Qualifications} entities in the database.
 * The main input is a {@link QualificationsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Qualifications} or a {@link Page} of {@link Qualifications} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QualificationsQueryService extends QueryService<Qualifications> {

    private final Logger log = LoggerFactory.getLogger(QualificationsQueryService.class);

    private final QualificationsRepository qualificationsRepository;

    public QualificationsQueryService(QualificationsRepository qualificationsRepository) {
        this.qualificationsRepository = qualificationsRepository;
    }

    /**
     * Return a {@link List} of {@link Qualifications} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Qualifications> findByCriteria(QualificationsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Qualifications> specification = createSpecification(criteria);
        return qualificationsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Qualifications} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Qualifications> findByCriteria(QualificationsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Qualifications> specification = createSpecification(criteria);
        return qualificationsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QualificationsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Qualifications> specification = createSpecification(criteria);
        return qualificationsRepository.count(specification);
    }

    /**
     * Function to convert {@link QualificationsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Qualifications> createSpecification(QualificationsCriteria criteria) {
        Specification<Qualifications> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Qualifications_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Qualifications_.name));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), Qualifications_.startDate));
            }
            if (criteria.getExpireDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExpireDate(), Qualifications_.expireDate));
            }
            if (criteria.getComments() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComments(), Qualifications_.comments));
            }
            if (criteria.getSubsidized() != null) {
                specification = specification.and(buildSpecification(criteria.getSubsidized(), Qualifications_.subsidized));
            }
        }
        return specification;
    }
}
