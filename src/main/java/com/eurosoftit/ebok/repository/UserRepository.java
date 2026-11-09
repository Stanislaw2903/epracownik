package com.eurosoftit.ebok.repository;

import com.eurosoftit.ebok.domain.RelatedUserView;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.service.dto.ApprovalType;
import com.eurosoftit.ebok.service.dto.AuthorizationToAcceptVacationType;
import com.eurosoftit.ebok.web.rest.integration.UserNameDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(LocalDateTime dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByLogin(String login);

    @Query("select u.id from User u where u.login = :login")
    Optional<Long> findIdByLogin(@Param("login") String login);

    @Query("select g.id from User u left join u.group g where u.login = :login")
    Long findGroupIdByLogin(@Param("login") String login);

    @Query("select u.externalId from User u where u.login = :login")
    Optional<Long> findExternalIdByLogin(@Param("login") String login);

    @EntityGraph(attributePaths = {"authorities", "relatedUsers", "group", "groups"})
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<User> findOneWithAuthoritiesAndRelatedUsersByLogin(String login);

    @EntityGraph(attributePaths = {"authorities", "relatedUsers", "group", "groups"})
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesAndRelatedUsersByEmailIgnoreCase(String email);

    Page<User> findAllByLoginNot(Pageable pageable, String login);

    @Query("select u from User u where (u.login || u.firstName || u.lastName ) LIKE CONCAT('%',:login,'%')")
    Page<User> findAllByLoginLike(Pageable pageable, @Param("login") String login);

    @Query("select u from User u where (u.login || u.firstName || u.lastName ) LIKE CONCAT('%',:login,'%') and u.activated = true")
    Page<User> findAllActivatedByLoginLike(Pageable pageable, @Param("login") String login);

    @Query("select u from User u where (u.login || u.firstName || u.lastName ) NOT LIKE CONCAT('%',:login,'%') and u.activated = true")
    Page<User> findAllActivatedByNotLoginLike(Pageable pageable, @Param("login") String login);

    @Query("SELECT (case when count(u.id) > 0 then 'true' else 'false' end) FROM User u WHERE u.login = :login")
    boolean userExists(@Param("login") String login);

    @Query("SELECT (case when count(u.id) > 0 then 'true' else 'false' end) FROM User u WHERE u.email = :email")
    boolean userExistsByEmail(@Param("email") String email);

    @Query("Select u from User u join u.authorities a where a.name = :authority")
    Optional<List<User>> findByAuthority(@Param("authority") String authority);

    @Query("SELECT (case when count(u.id) > 0 then 'true' else 'false' end) FROM User u WHERE u.login = :login")
    boolean userExistsByLogin(@Param("login") String login);

    @Query("SELECT (case when count(u.id) > 0 then 'true' else 'false' end) FROM User u WHERE u.externalId = :externalId")
    boolean userExistsByExternalId(@Param("externalId") Long externalId);

    @Query("SELECT (case when count(u.id) > 0 then 'true' else 'false' end) FROM User u WHERE u.externalId = :externalId and u.id != :userId")
    boolean userExistsByExternalId(@Param("externalId") Long externalId, @Param("userId") Long userId);

    @Query("Select ru.id from User u join u.relatedUsers ru where u.id = :id")
    List<Long> findRelatedUserIdsByUserId(@Param("id") Long loggedUserId);

    @Query("Select ru from User u join u.relatedUsers ru where u.id = :id")
    List<User> findRelatedUserByUserId(@Param("id") Long loggedUserId);

    @Query("Select ru.id from User u join u.relatedUsers ru where u.id = :id")
    Page<Long> findRelatedUserIdsByUserId(@Param("id") Long loggedUserId, Pageable pageable);

    @Query("Select u.authorizationToAcceptVacation from User u where u.id = :id")
    AuthorizationToAcceptVacationType findAuthorizationToAcceptVacationType(@Param("id") Long loggedUserId);

    @Query("Select u from RelatedUserView u where u.relatedToUserId = :id")
    Page<RelatedUserView> findRelatedUsers(@Param("id") Long loggedUserId, Pageable pageable);

    @Query("Select u.id from User u")
    Page<Long> findAllIds(Pageable pageable);

    @Query("select new com.eurosoftit.ebok.web.rest.integration.UserNameDTO(u.id, u.login, u.firstName, u.lastName) from User u")
    List<UserNameDTO> findAllDTO();

    @Query("select new com.eurosoftit.ebok.web.rest.integration.UserNameDTO(u.id, u.login, u.firstName, u.lastName) from User u where u.id = :id")
    UserNameDTO findOneDTO(@Param("id") Long id);

    @Query("Select u.id from User u where u.activated = false")
    List<Long> findNotActivated();

    @Query("select u.authorizationToAcceptVacation FROM User u where u.id = :id")
    AuthorizationToAcceptVacationType findAuthorizationToAcceptVacationById(@Param("id") Long loggedUserId);

    @Query("SELECT u FROM User u WHERE u.login not in (:notLogin)")
    Page<User> findAllWithout(@Param("notLogin") String notLogin, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.login not in (:notLogin)")
    List<User> findAllWithout(@Param("notLogin") String notLogin);

    @Query("SELECT u.phoneNumber FROM User u WHERE u.id in (:authorizationUsers) and u.phoneNumber is not null")
    List<String> findPhoneNumbersByIds(@Param("authorizationUsers") List<Long> authorizationUsers);

    @Query("SELECT u FROM User u WHERE u.phoneNumber is not null and u.activated = true")
    Page<User> findUsersWithPhone(Pageable pageable);

    @Query("SELECT u FROM User u join u.group g WHERE g.id in (:groupIds)")
    Set<User> findByGroupIds(@Param("groupIds") List<Long> ids);

    @Query("SELECT g.id FROM User u join u.groups g WHERE u.id = :userId")
    List<Long> findGroups(@Param("userId") Long userId);

    @Query(value = "SELECT u.id FROM jhi_user u", nativeQuery = true)
    List<BigInteger> findNativeAllIds();

    @Query(value = "SELECT u.id FROM jhi_user u where u.login not in (:notLogin)", nativeQuery = true)
    List<BigInteger> findNativeAllWithout(@Param("notLogin") String notLogin);

    @Query(value = "SELECT ru.related_user_id FROM jhi_user u join related_user ru ON ru.user_id = u.id" +
        " where u.id = :id", nativeQuery = true)
    List<BigInteger> findNativeRelatedUserIdsByUserId(@Param("id") Long loggedUserId);

    @Query(value = "SELECT distinct u.id FROM jhi_user u where u.USER_GROUP_ID in (:groupIds)", nativeQuery = true)
    List<BigInteger> findUserByGroupsViewByGroupIds(@Param("groupIds") List<Long> ids);

    @Query("SELECT u FROM User u join u.group g WHERE g.id in (:groupIds)")
    Page<User> findByGroupIds(@Param("groupIds") List<Long> ids, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.externalId in (:externalIds)")
    List<User> findByExternalIds(@Param("externalIds") List<Long> externalIds);

    @Query("SELECT u.approvalVerification FROM User u WHERE u.id = :userId")
    String getUserApprovalVerificationRequirement(@Param("userId") Long userId);

    @Query("SELECT u.approvalType FROM User u WHERE u.id = :userId")
    ApprovalType getUserApprovalType(@Param("userId") Long userId);

    @Query("SELECT u.id FROM User u WHERE u.id in(:userIds) and u.approvalVerification = 'TWO_LEVEL' or u.approvalVerification is null")
    List<Long> getTwoLevelApproval(@Param("userIds") List<Long> userIds);

    @Query("SELECT (case when count(u.id) > 0 then 'true' else 'false' end) FROM User u join u.authorities a WHERE a.name = :role and u.id = :userId")
    boolean hasRole(@Param("role") String role, @Param("userId") Long userId);
}
