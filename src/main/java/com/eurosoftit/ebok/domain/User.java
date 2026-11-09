package com.eurosoftit.ebok.domain;

import com.eurosoftit.ebok.common.controller.IEntity;
import com.eurosoftit.ebok.service.dto.ApprovalType;
import com.eurosoftit.ebok.service.dto.ApprovalVerificationType;
import com.eurosoftit.ebok.service.dto.AuthorizationToAcceptVacationType;
import com.eurosoftit.ebok.web.rest.group.UserGroupEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A user.
 */
@Setter
@Getter
@Entity
@Table(name = "jhi_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity implements Serializable, IEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    @Column(name = "password_hash", length = 60, nullable = false)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Size(min = 5, max = 254)
    @Column(length = 254, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    private boolean activated = false;

    @Size(min = 2, max = 10)
    @Column(name = "lang_key", length = 10)
    private String langKey = "pl";

    @Size(max = 256)
    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    @JsonIgnore
    private String resetKey;

    @Column(name = "reset_date")
    private Instant resetDate = null;

    private Long externalId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "related_user", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "related_user_id")})
    @JsonIgnore
    private List<User> relatedUsers = Lists.newArrayList();

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "related_group", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "related_group_id")})
    @JsonIgnore
    private Set<UserGroupEntity> groups = Sets.newHashSet();

    @Enumerated(EnumType.STRING)
    @Column(name = "authorization_to_accept_vacation")
    private AuthorizationToAcceptVacationType authorizationToAcceptVacation;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_verification")
    private ApprovalVerificationType approvalVerification;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_type")
    private ApprovalType approvalType;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "jhi_user_authority",
        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 20)
    private Set<Authority> authorities = new HashSet<>();

    @Column(updatable = false)
    private Boolean manualInsert = Boolean.FALSE;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "type")
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_GROUP_ID")
    private UserGroupEntity group;

    public ApprovalVerificationType getApprovalVerification() {
        return approvalVerification;
    }

    public void setApprovalVerification(ApprovalVerificationType approvalVerification) {
        this.approvalVerification = approvalVerification;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id != null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    // Lowercase the login before saving it in database
    public void setLogin(String login) {
        this.login = StringUtils.lowerCase(login, Locale.ENGLISH);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Instant getResetDate() {
        return resetDate;
    }

    public void setResetDate(Instant resetDate) {
        this.resetDate = resetDate;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "User{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", activated='" + activated + '\'' +
            ", langKey='" + langKey + '\'' +
            ", activationKey='" + activationKey + '\'' +
            "}";
    }

    public List<User> getRelatedUsers() {
        return relatedUsers;
    }

    public void setRelatedUsers(List<User> relatedUsers) {
        this.relatedUsers = relatedUsers;
    }

    public AuthorizationToAcceptVacationType getAuthorizationToAcceptVacation() {
        return authorizationToAcceptVacation;
    }

    public void setAuthorizationToAcceptVacation(AuthorizationToAcceptVacationType authorizationToAcceptVacation) {
        this.authorizationToAcceptVacation = authorizationToAcceptVacation;
    }

    public Boolean getManualInsert() {
        return manualInsert;
    }

    public void setManualInsert(Boolean manualInsert) {
        this.manualInsert = manualInsert;
    }

    public UserGroupEntity getGroup() {
        return group;
    }

    public void setGroup(UserGroupEntity group) {
        this.group = group;
    }

    public Set<UserGroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(Set<UserGroupEntity> groups) {
        this.groups = groups;
    }
}
