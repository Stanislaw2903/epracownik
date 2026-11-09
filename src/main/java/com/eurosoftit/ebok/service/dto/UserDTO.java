package com.eurosoftit.ebok.service.dto;

import com.eurosoftit.ebok.domain.Authority;
import com.eurosoftit.ebok.domain.RelatedUserView;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.web.rest.group.UserGroupEntity;
import org.apache.commons.lang3.BooleanUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(max = 256)
    private String imageUrl;

    private boolean activated = false;

    @Size(min = 2, max = 10)
    private String langKey;

    private String createdBy;

    private LocalDateTime createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Long externalId;

    private Boolean manualInsert;

    private Set<String> authorities;

    private Set<Long> relatedUsers;

    private Set<Long> groups;

    private AuthorizationToAcceptVacationType authorizationToAcceptVacation;

    private String phoneNumber;

    private String type;

    private String groupName;

    private Long groupId;

    private ApprovalVerificationType approvalVerificationType;

    private ApprovalType approvalType;


    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.activated = user.getActivated();
        this.imageUrl = user.getImageUrl();
        this.langKey = user.getLangKey();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.externalId = user.getExternalId();
        this.manualInsert = BooleanUtils.isTrue(user.getManualInsert());
        this.authorizationToAcceptVacation = user.getAuthorizationToAcceptVacation();
        this.relatedUsers = user.getRelatedUsers().stream()
            .map(User::getId)
            .collect(Collectors.toSet());
        this.authorities = user.getAuthorities().stream()
            .map(Authority::getName)
            .collect(Collectors.toSet());
        this.phoneNumber = user.getPhoneNumber();
        this.type = user.getType();

        if(user.getGroups() != null) {
            this.groups = user.getGroups().stream()
                .map(UserGroupEntity::getId)
                .collect(Collectors.toSet());
        }

        Optional.ofNullable(user.getGroup())
            .map(group -> this.groupName = group.getName());

        Optional.ofNullable(user.getGroup())
            .map(group -> this.groupId = group.getId());

        this.approvalType = user.getApprovalType();
        this.approvalVerificationType = user.getApprovalVerification();
    }

    public UserDTO(RelatedUserView user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.activated = user.getActivated();
        this.imageUrl = user.getImageUrl();
        this.langKey = user.getLangKey();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.externalId = user.getExternalId();
        this.manualInsert = BooleanUtils.isTrue(user.getManualInsert());
        this.authorizationToAcceptVacation = user.getAuthorizationToAcceptVacation();
        this.approvalType = user.getApprovalType();
        this.approvalVerificationType = user.getApprovalVerificationType();
        this.relatedUsers = user.getRelatedUsers().stream()
            .map(User::getId)
            .collect(Collectors.toSet());
        this.authorities = user.getAuthorities().stream()
            .map(Authority::getName)
            .collect(Collectors.toSet());
        this.phoneNumber = user.getPhoneNumber();

        if(user.getGroups() != null) {
            this.groups = user.getGroups().stream()
                .map(UserGroupEntity::getId)
                .collect(Collectors.toSet());
        }

        this.type = user.getType();

        Optional.ofNullable(user.getGroup())
            .map(group -> this.groupName = group.getName());

        Optional.ofNullable(user.getGroup())
            .map(group -> this.groupId = group.getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public Set<Long> getRelatedUsers() {
        return relatedUsers;
    }

    public void setRelatedUsers(Set<Long> relatedUsers) {
        this.relatedUsers = relatedUsers;
    }

    public Set<Long> getGroups() {
        return groups;
    }

    public void setGroups(Set<Long> groups) {
        this.groups = groups;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getType() {
        return type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public ApprovalVerificationType getApprovalVerificationType() {
        return approvalVerificationType;
    }

    public void setApprovalVerificationType(ApprovalVerificationType approvalVerificationType) {
        this.approvalVerificationType = approvalVerificationType;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", externalId='" + externalId + '\'' +
            ", email='" + email + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", activated=" + activated +
            ", langKey='" + langKey + '\'' +
            ", createdBy=" + createdBy +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            ", authorities=" + authorities +
            "}";
    }
}
