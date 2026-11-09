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
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.eurosoftit.ebok.domain.NotificationRead} entity. This class is used
 * in {@link com.eurosoftit.ebok.web.rest.NotificationReadResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notification-reads?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class NotificationReadCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter date;

    private IntegerFilter notify;

    private LongFilter userId;

    public NotificationReadCriteria() {
    }

    public NotificationReadCriteria(NotificationReadCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.notify = other.notify == null ? null : other.notify.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public NotificationReadCriteria copy() {
        return new NotificationReadCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ZonedDateTimeFilter getDate() {
        return date;
    }

    public void setDate(ZonedDateTimeFilter date) {
        this.date = date;
    }

    public IntegerFilter getNotify() {
        return notify;
    }

    public void setNotify(IntegerFilter notify) {
        this.notify = notify;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NotificationReadCriteria that = (NotificationReadCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(notify, that.notify) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        date,
        notify,
        userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationReadCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (notify != null ? "notify=" + notify + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
