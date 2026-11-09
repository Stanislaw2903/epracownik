package com.eurosoftit.ebok.domain;

import com.eurosoftit.ebok.domain.enumeration.NotificationCanal;
import com.eurosoftit.ebok.domain.enumeration.NotificationCategory;
import com.eurosoftit.ebok.web.rest.group.UserGroupEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "description")
    private String description;

    @Column(name = "create_date")
    private ZonedDateTime createDate;

    @Column(name = "read_date")
    private ZonedDateTime readDate;

    @Column(name = "status")
    private Boolean status = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private NotificationCategory category;

    @ManyToOne
    @JsonIgnoreProperties(value = "notifications", allowSetters = true)
    private User createdBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "notification_recepient",
               joinColumns = @JoinColumn(name = "notification_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "recepient_id", referencedColumnName = "id"))
    private Set<User> recepients = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "canal")
    @Getter
    private NotificationCanal canal;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "notification_group",
        joinColumns = @JoinColumn(name = "notification_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    private Set<UserGroupEntity> groups = new HashSet<>();

    @Column(name = "send_mail")
    private Boolean sendMail = Boolean.FALSE;

    @Column(name = "send_sms")
    private Boolean sendSms = Boolean.FALSE;

    @Column(name = "send_notification")
    private Boolean sendNotification = Boolean.FALSE;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public Set<UserGroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(Set<UserGroupEntity> groups) {
        this.groups = groups;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public Notification subject(String subject) {
        this.subject = subject;
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public Notification description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public Notification createDate(ZonedDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public ZonedDateTime getReadDate() {
        return readDate;
    }

    public Notification readDate(ZonedDateTime readDate) {
        this.readDate = readDate;
        return this;
    }

    public void setReadDate(ZonedDateTime readDate) {
        this.readDate = readDate;
    }

    public Boolean isStatus() {
        return status;
    }

    public Notification status(Boolean status) {
        this.status = status;
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public NotificationCategory getCategory() {
        return category;
    }

    public Notification category(NotificationCategory category) {
        this.category = category;
        return this;
    }

    public void setCategory(NotificationCategory category) {
        this.category = category;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Notification createdBy(User user) {
        this.createdBy = user;
        return this;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    @JsonIgnore
    public User getRecepient() {
        return recepients != null && recepients.size() == 1 ? recepients.iterator().next() : null;
    }

    public Set<User> getRecepients() {
        return recepients;
    }

    public Boolean getStatus() {
        return status;
    }

    public Notification recepients(Set<User> users) {
        this.recepients = users;
        return this;
    }

    public Notification addRecepient(User user) {
        this.recepients.add(user);
        return this;
    }

    public Notification removeRecepient(User user) {
        this.recepients.remove(user);
        return this;
    }

    public void setRecepients(Set<User> users) {
        this.recepients = users;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return id != null && id.equals(((Notification) o).id);
    }

    public void setSendMail(Boolean sendMail) {
        this.sendMail = sendMail;
    }

    public Boolean getSendMail() {
        return sendMail;
    }

    public Boolean getSendSms() {
        return sendSms;
    }

    public void setSendSms(Boolean sendSms) {
        this.sendSms = sendSms;
    }

    public Boolean getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", subject='" + getSubject() + "'" +
            ", description='" + getDescription() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", readDate='" + getReadDate() + "'" +
            ", status='" + isStatus() + "'" +
            ", category='" + getCategory() + "'" +
            "}";
    }
}
