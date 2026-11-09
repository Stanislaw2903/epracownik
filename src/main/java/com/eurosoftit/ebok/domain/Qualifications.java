package com.eurosoftit.ebok.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Qualifications.
 */
@Entity
@Table(name = "qualifications")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Qualifications implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    @Column(name = "comments")
    private String comments;

    @Column(name = "subsidized")
    private Boolean subsidized;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Qualifications name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Qualifications startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public Qualifications expireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getComments() {
        return comments;
    }

    public Qualifications comments(String comments) {
        this.comments = comments;
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean isSubsidized() {
        return subsidized;
    }

    public Qualifications subsidized(Boolean subsidized) {
        this.subsidized = subsidized;
        return this;
    }

    public void setSubsidized(Boolean subsidized) {
        this.subsidized = subsidized;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Qualifications)) {
            return false;
        }
        return id != null && id.equals(((Qualifications) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Qualifications{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", expireDate='" + getExpireDate() + "'" +
            ", comments='" + getComments() + "'" +
            ", subsidized='" + isSubsidized() + "'" +
            "}";
    }
}
