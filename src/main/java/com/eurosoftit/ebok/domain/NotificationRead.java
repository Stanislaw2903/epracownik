package com.eurosoftit.ebok.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A NotificationRead.
 */
@Entity
@Table(name = "notification_read")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NotificationRead implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "date")
    private ZonedDateTime date;

    @Column(name = "notify")
    private Integer notify;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "notification_read_user",
               joinColumns = @JoinColumn(name = "notification_read_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public NotificationRead date(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Integer getNotify() {
        return notify;
    }

    public NotificationRead notify(Integer notify) {
        this.notify = notify;
        return this;
    }

    public void setNotify(Integer notify) {
        this.notify = notify;
    }

    public Set<User> getUsers() {
        return users;
    }

    public NotificationRead users(Set<User> users) {
        this.users = users;
        return this;
    }

    public NotificationRead addUser(User user) {
        this.users.add(user);
        return this;
    }

    public NotificationRead removeUser(User user) {
        this.users.remove(user);
        return this;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationRead)) {
            return false;
        }
        return id != null && id.equals(((NotificationRead) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationRead{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", notify=" + getNotify() +
            "}";
    }
}
