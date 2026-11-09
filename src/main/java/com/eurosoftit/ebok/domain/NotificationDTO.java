package com.eurosoftit.ebok.domain;

import com.eurosoftit.ebok.domain.enumeration.NotificationCanal;
import com.eurosoftit.ebok.domain.enumeration.NotificationCategory;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class NotificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String subject;
    private String description;
    private ZonedDateTime createDate;
    private ZonedDateTime readDate;
    private Boolean status;
    private NotificationCanal canal;
    private NotificationCategory category;
    private User createdBy;
    private User recepients;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public ZonedDateTime getReadDate() {
        return readDate;
    }

    public void setReadDate(ZonedDateTime readDate) {
        this.readDate = readDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public NotificationCategory getCategory() {
        return category;
    }

    public void setCategory(NotificationCategory category) {
        this.category = category;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getRecepients() {
        return recepients;
    }

    public void setRecepients(User recepients) {
        this.recepients = recepients;
    }

    public NotificationCanal getCanal() {
        return canal;
    }

    public void setCanal(NotificationCanal canal) {
        this.canal = canal;
    }
}
