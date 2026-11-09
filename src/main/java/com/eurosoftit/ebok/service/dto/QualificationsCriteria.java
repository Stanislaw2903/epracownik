package com.eurosoftit.ebok.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link com.eurosoftit.ebok.domain.Qualifications} entity. This class is used
 * in {@link com.eurosoftit.ebok.web.rest.QualificationsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /qualifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class QualificationsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LocalDateFilter startDate;

    private LocalDateFilter expireDate;

    private StringFilter comments;

    private BooleanFilter subsidized;

    public QualificationsCriteria() {
    }

    public QualificationsCriteria(QualificationsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.expireDate = other.expireDate == null ? null : other.expireDate.copy();
        this.comments = other.comments == null ? null : other.comments.copy();
        this.subsidized = other.subsidized == null ? null : other.subsidized.copy();
    }

    @Override
    public QualificationsCriteria copy() {
        return new QualificationsCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LocalDateFilter getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateFilter startDate) {
        this.startDate = startDate;
    }

    public LocalDateFilter getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateFilter expireDate) {
        this.expireDate = expireDate;
    }

    public StringFilter getComments() {
        return comments;
    }

    public void setComments(StringFilter comments) {
        this.comments = comments;
    }

    public BooleanFilter getSubsidized() {
        return subsidized;
    }

    public void setSubsidized(BooleanFilter subsidized) {
        this.subsidized = subsidized;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final QualificationsCriteria that = (QualificationsCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(expireDate, that.expireDate) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(subsidized, that.subsidized);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        startDate,
        expireDate,
        comments,
        subsidized
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QualificationsCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (startDate != null ? "startDate=" + startDate + ", " : "") +
                (expireDate != null ? "expireDate=" + expireDate + ", " : "") +
                (comments != null ? "comments=" + comments + ", " : "") +
                (subsidized != null ? "subsidized=" + subsidized + ", " : "") +
            "}";
    }

}
