package com.eurosoftit.ebok.service.dto;

import java.io.Serializable;
import java.util.Objects;

import com.eurosoftit.ebok.domain.enumeration.NotificationCanal;
import io.github.jhipster.service.Criteria;
import com.eurosoftit.ebok.domain.enumeration.NotificationCategory;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.eurosoftit.ebok.domain.Notification} entity. This class is used
 * in {@link com.eurosoftit.ebok.web.rest.NotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class NotificationCriteria implements Serializable, Criteria {
    public void onlySendNotification() {
        BooleanFilter booleanFilter = new BooleanFilter();
        booleanFilter.setEquals(true);
        this.sendNotification = booleanFilter;
    }

    /**
     * Class for filtering notificationCategory
     */
    public static class notificationCategoryFilter extends Filter<NotificationCategory> {

        public notificationCategoryFilter() {
        }

        public notificationCategoryFilter(notificationCategoryFilter filter) {
            super(filter);
        }

        @Override
        public notificationCategoryFilter copy() {
            return new notificationCategoryFilter(this);
        }

    }

    public static class notificationCanalFilter extends Filter<NotificationCanal> {

        public notificationCanalFilter() {
        }

        public notificationCanalFilter(notificationCanalFilter filter) {
            super(filter);
        }

        @Override
        public notificationCanalFilter copy() {
            return new notificationCanalFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter subject;

    private StringFilter description;

    private ZonedDateTimeFilter createDate;

    private ZonedDateTimeFilter readDate;

    private BooleanFilter status;

    private notificationCategoryFilter category;

    private notificationCanalFilter canal;

    private LongFilter createdById;
    private LongFilter notCreatedById;

    private LongFilter recepientId;
    private LongFilter groupId;

    private BooleanFilter sendNotification;

    public NotificationCriteria() {
    }

    public NotificationCriteria(NotificationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.subject = other.subject == null ? null : other.subject.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.createDate = other.createDate == null ? null : other.createDate.copy();
        this.readDate = other.readDate == null ? null : other.readDate.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.sendNotification = other.sendNotification == null ? null : other.sendNotification.copy();
        this.category = other.category == null ? null : other.category.copy();
        this.canal = other.canal == null ? null : other.canal.copy();
        this.createdById = other.createdById == null ? null : other.createdById.copy();
        this.notCreatedById = other.notCreatedById == null ? null : other.notCreatedById.copy();
        this.recepientId = other.recepientId == null ? null : other.recepientId.copy();
        this.groupId = other.groupId == null ? null : other.groupId.copy();
    }

    @Override
    public NotificationCriteria copy() {
        return new NotificationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSubject() {
        return subject;
    }

    public void setSubject(StringFilter subject) {
        this.subject = subject;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public ZonedDateTimeFilter getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTimeFilter createDate) {
        this.createDate = createDate;
    }

    public ZonedDateTimeFilter getReadDate() {
        return readDate;
    }

    public void setReadDate(ZonedDateTimeFilter readDate) {
        this.readDate = readDate;
    }

    public BooleanFilter getStatus() {
        return status;
    }

    public void setStatus(BooleanFilter status) {
        this.status = status;
    }

    public notificationCategoryFilter getCategory() {
        return category;
    }

    public void setCategory(notificationCategoryFilter category) {
        this.category = category;
    }

    public notificationCanalFilter getCanal() {
        return canal;
    }

    public void setCanal(notificationCanalFilter canal) {
        this.canal = canal;
    }

    public LongFilter getCreatedById() {
        return createdById;
    }

    public void setCreatedById(LongFilter createdById) {
        this.createdById = createdById;
    }

    public LongFilter getRecepientId() {
        return recepientId;
    }

    public void setRecepientId(LongFilter recepientId) {
        this.recepientId = recepientId;
    }

    public void setGroupId(LongFilter groupId) {
        this.groupId = groupId;
    }

    public LongFilter getGroupId() {
        return groupId;
    }

    public BooleanFilter getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(BooleanFilter sendNotification) {
        this.sendNotification = sendNotification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NotificationCriteria that = (NotificationCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(description, that.description) &&
            Objects.equals(createDate, that.createDate) &&
            Objects.equals(readDate, that.readDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(sendNotification, that.sendNotification) &&
            Objects.equals(category, that.category) &&
            Objects.equals(canal, that.canal) &&
            Objects.equals(groupId, that.groupId) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(recepientId, that.recepientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        subject,
        description,
        createDate,
        readDate,
        status,
        sendNotification,
        category,
        canal,
        createdById,
        recepientId,
        groupId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (subject != null ? "subject=" + subject + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (createDate != null ? "createDate=" + createDate + ", " : "") +
                (readDate != null ? "readDate=" + readDate + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (sendNotification != null ? "sendNotification=" + sendNotification + ", " : "") +
                (category != null ? "category=" + category + ", " : "") +
                (canal != null ? "canal=" + canal + ", " : "") +
                (createdById != null ? "createdById=" + createdById + ", " : "") +
                (recepientId != null ? "recepientId=" + recepientId + ", " : "") +
                (groupId != null ? "groupId=" + groupId + ", " : "") +
            "}";
    }

    public void setNotCreatedById(LongFilter notCreatedById) {
        this.notCreatedById = notCreatedById;
    }

    public LongFilter getNotCreatedById() {
        return notCreatedById;
    }
}
