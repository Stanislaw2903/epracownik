package com.eurosoftit.ebok.service;

import com.eurosoftit.ebok.config.Constants;
import com.eurosoftit.ebok.domain.Authority;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.domain.specification.SpecificationReflectionBuilder;
import com.eurosoftit.ebok.repository.AuthorityRepository;
import com.eurosoftit.ebok.repository.UserRepository;
import com.eurosoftit.ebok.security.AuthoritiesConstants;
import com.eurosoftit.ebok.security.SecurityUtils;
import com.eurosoftit.ebok.service.dto.ApprovalType;
import com.eurosoftit.ebok.service.dto.AuthorizationToAcceptVacationType;
import com.eurosoftit.ebok.service.dto.UserDTO;
import com.eurosoftit.ebok.web.rest.UserProperties;
import com.eurosoftit.ebok.web.rest.group.UserGroupEntity;
import com.eurosoftit.ebok.web.rest.integration.SimpleUserDTO;
import com.eurosoftit.ebok.web.rest.integration.UserNameDTO;
import io.github.jhipster.security.RandomUtil;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final CacheManager cacheManager;

    @PersistenceContext
    private EntityManager entityManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(864000)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                user.setActivated(true);
                this.clearUserCaches(user);

                return userRepository.save(user);
            });
    }

    @Transactional
    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                user.setActivated(true);
                this.clearUserCaches(user);
                return userRepository.save(user);
            });
    }

    public User registerUser(UserDTO userDTO, String password) {
        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });
        userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }
        });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(normalize(userDTO.getFirstName()) + userDTO.getExternalId());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setExternalId(userDTO.getExternalId());
        newUser.setAuthorizationToAcceptVacation(userDTO.getAuthorizationToAcceptVacation());
        newUser.setApprovalType(userDTO.getApprovalType());
        newUser.setApprovalVerification(userDTO.getApprovalVerificationType());

        if (AuthorizationToAcceptVacationType.SPECIFIC_GROUP.equals(newUser.getAuthorizationToAcceptVacation())) {
            newUser.setRelatedUsers(toUserList(userDTO.getRelatedUsers()));
        }

        if (AuthorizationToAcceptVacationType.USER_GROUP.equals(newUser.getAuthorizationToAcceptVacation())) {
            newUser.setGroups(toGroupsList(userDTO.getGroups()));
        }

        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private List<User> toUserList(Set<Long> relatedUsers) {

        if (Objects.isNull(relatedUsers)) {
            return null;
        }

        return relatedUsers.stream().map(u -> entityManager.getReference(User.class, u)).collect(Collectors.toList());
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.getActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
    }

    private String normalize(String value) {
        String[] original = new String[]{"Ą", "ą", "Ć", "ć", "Ę", "ę", "Ł", "ł", "Ń", "ń", "Ó", "ó", "Ś", "ś", "Ź", "ź", "Ż", "ż"};
        String[] normalized = new String[]{"A", "a", "C", "c", "E", "e", "L", "l", "N", "n", "O", "o", "S", "s", "Z", "z", "Z", "z"};


        for (int i = 0; i < original.length; i++) {
            value = value.replace(original[i], normalized[i]);
        }

        return value.toLowerCase();
    }

    public User createUser(com.eurosoftit.ebok.web.rest.integration.UserDTO userDTO) {
        User user = new User();

        user.setLogin(normalize(userDTO.getFirstName()) + userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(StringUtils.trim(userDTO.getEmail().toLowerCase()));
        }

        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setType(userDTO.getType());
        user.setExternalId(userDTO.getId());
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(u -> user.setAuthorities(Sets.newHashSet(u)));
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    @Transactional
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setLogin(normalize(userDTO.getLogin().toLowerCase()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setExternalId(userDTO.getExternalId());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setAuthorizationToAcceptVacation(userDTO.getAuthorizationToAcceptVacation());
        user.setApprovalType(userDTO.getApprovalType());
        user.setApprovalVerification(userDTO.getApprovalVerificationType());

        if (AuthorizationToAcceptVacationType.SPECIFIC_GROUP.equals(user.getAuthorizationToAcceptVacation())) {
            user.setRelatedUsers(toUserList(userDTO.getRelatedUsers()));
        }

        if (AuthorizationToAcceptVacationType.USER_GROUP.equals(user.getAuthorizationToAcceptVacation())) {
            user.setGroups(toGroupsList(userDTO.getGroups()));
        }

        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        if (userDTO.getGroupId() != null) {
            user.setGroup(entityManager.getReference(UserGroupEntity.class, userDTO.getGroupId()));
        }

        if (userDTO.getType() == null) {
            user.setType("P");
        } else {
            user.setType(userDTO.getType());
        }
        user.setManualInsert(Boolean.TRUE);
        user = userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    @Transactional
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                this.clearUserCaches(user);
                user.setPhoneNumber(userDTO.getPhoneNumber());
                user.setLogin(normalize(userDTO.getLogin()));
                user.setExternalId(userDTO.getExternalId());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setAuthorizationToAcceptVacation(userDTO.getAuthorizationToAcceptVacation());
                user.setApprovalType(userDTO.getApprovalType());
                user.setApprovalVerification(userDTO.getApprovalVerificationType());

                if (AuthorizationToAcceptVacationType.SPECIFIC_GROUP.equals(user.getAuthorizationToAcceptVacation())) {
                    user.setRelatedUsers(toUserList(userDTO.getRelatedUsers()));
                } else {
                    user.getRelatedUsers().clear();
                }

                if (AuthorizationToAcceptVacationType.USER_GROUP.equals(user.getAuthorizationToAcceptVacation())) {
                    user.setGroups(toGroupsList(userDTO.getGroups()));
                } else {
                    user.getGroups().clear();
                }

                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO.getAuthorities().stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);

                if (userDTO.getGroupId() != null && (user.getGroup() == null || !userDTO.getGroupId().equals(user.getGroup().getId()))) {
                    user.setGroup(entityManager.getReference(UserGroupEntity.class, userDTO.getGroupId()));
                } else if (userDTO.getGroupId() == null) {
                    user.setGroup(null);
                }

                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    private Set<UserGroupEntity> toGroupsList(Set<Long> groups) {
        if (Objects.isNull(groups)) {
            return null;
        }

        return groups.stream().map(groupId -> entityManager.getReference(UserGroupEntity.class, groupId)).collect(Collectors.toSet());
    }

    @Transactional
    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            userRepository.delete(user);
            this.clearUserCaches(user);
            log.debug("Deleted User: {}", user);
        });
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    @Transactional
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl, String phoneNUmber) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setPhoneNumber(phoneNUmber);
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
            });
    }


    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(UserProperties properties, Pageable pageable) {
        if (Objects.nonNull(properties.getLogin())) {
            final Specification<User> process = SpecificationReflectionBuilder.process(properties);
            return userRepository.findAll(process, pageable).map(UserDTO::new);
        }

        Specification<User> process = SpecificationReflectionBuilder.process(properties);
        process = process.and(notEqual(Constants.ANONYMOUS_USER, "login"));
        return userRepository.findAll(process, pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllActivedManagedUsers(UserProperties properties, Pageable pageable) {
        properties.setActivated(true);
        if (Objects.nonNull(properties.getLogin())) {
            final Specification<User> process = SpecificationReflectionBuilder.process(properties);
            return userRepository.findAll(process, pageable).map(UserDTO::new);
        }

        Specification<User> process = SpecificationReflectionBuilder.process(properties);
        process = process.and(notEqual(Constants.ANONYMOUS_USER, "login"));
        return userRepository.findAll(process, pageable).map(UserDTO::new);
    }

    public static <T> Specification<T> notEqual(final String value, String field) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(value) ? null : criteriaBuilder.notEqual(eventRoot.get(field), value);
    }


    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesAndRelatedUsersByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesAndRelatedUsersByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(LocalDateTime.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }


    private void clearUserCaches(User user) {
        Optional.ofNullable(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).ifPresent(f -> f.evict(user.getLogin()));
        ;

        if (user.getEmail() != null) {
            Optional.ofNullable(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).ifPresent(f -> f.evict(user.getEmail()));
        }
    }

    public boolean userExists(String login) {
        return userRepository.userExists(login);
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.userExistsByEmail(email);
    }

    public Optional<List<User>> findAllAdministrators() {
        return userRepository.findByAuthority(AuthoritiesConstants.ADMIN);
    }

    public boolean userExistsLogin(String login) {
        return userRepository.userExistsByLogin(login);
    }

    @Transactional(readOnly = true)
    public List<SimpleUserDTO> findUserForRelation(Long userId) {

        return entityManager.unwrap(Session.class).createNativeQuery("select u.id,u.first_name, u.last_name,u.email from jhi_user u left join related_user ru on ru.related_user_id = u.id where u.id != :id " +
            " union select u.id,u.first_name, u.last_name,u.email from jhi_user u left join related_user ru on ru.related_user_id = u.id where ru.user_id = :id")
            .setParameter("id", userId).setResultTransformer(new ResultTransformer() {
                @Override
                public Object transformTuple(Object[] objects, String[] strings) {
                    return new SimpleUserDTO(((BigInteger) objects[0]).longValue(), (String) objects[1], (String) objects[2], (String) objects[3]);
                }

                @Override
                public List transformList(List list) {
                    return list;
                }
            }).getResultList();
    }

    public List<String> findUserPhoneForRelation(Long userId) {

        return entityManager.unwrap(Session.class).createNativeQuery(
            "select u.phone_number from jhi_user u join related_user ru on ru.user_id = u.id where ru.related_user_id = :id " +
                "and u.activated = 'true' ")
            .setParameter("id", userId).getResultList();
    }

    public List<String> findUserEmailsForRelation(Long userId) {
        return entityManager.unwrap(Session.class).createNativeQuery(
            "select u.email from jhi_user u join related_user ru on ru.user_id = u.id where ru.related_user_id = :id " +
                "and u.activated = 'true' ")
            .setParameter("id", userId).getResultList();
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findRelatedUsers(Long loggedUserId, Pageable pageable) {
        AuthorizationToAcceptVacationType authorizationToAcceptVacationType = userRepository.findAuthorizationToAcceptVacationById(loggedUserId);

        if (Objects.isNull(authorizationToAcceptVacationType)) {
            return Page.empty();
        } else if (AuthorizationToAcceptVacationType.NO_ONE.equals(authorizationToAcceptVacationType)) {
            return Page.empty();
        } else if (AuthorizationToAcceptVacationType.ALL.equals(authorizationToAcceptVacationType)) {
            return userRepository.findAllWithout(Constants.ANONYMOUS_USER, pageable).map(UserDTO::new);
        } else if (AuthorizationToAcceptVacationType.USER_GROUP.equals(authorizationToAcceptVacationType)) {
            List<Long> groups = userRepository.findGroups(loggedUserId);
            return userRepository.findByGroupIds(groups, pageable).map(UserDTO::new);
        }

        return userRepository.findRelatedUsers(loggedUserId, pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public List<UserNameDTO> findAllDTO() {
        return userRepository.findAllDTO();
    }

    public UserNameDTO findOneDTO(Long id) {
        return userRepository.findOneDTO(id);
    }

    public List<User> findAll(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public List<Long> findNotActivated() {
        return userRepository.findNotActivated();
    }

    public List<String> findPhoneNumbersByIds(List<Long> authorizationUsers) {
        return userRepository.findPhoneNumbersByIds(authorizationUsers);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findUsersWithPhone(Pageable pageable) {
        return userRepository.findUsersWithPhone(pageable).map(UserDTO::new);
    }

    public List<User> findByExternalIds(List<Long> externalIds) {
        return userRepository.findByExternalIds(externalIds);
    }

    public Collection<? extends String> findUserEmailsForRelationGroup(Long loggedUserGroupId) {
        if(loggedUserGroupId == null) {
            return new ArrayList<>();
        }
        return entityManager.unwrap(Session.class).createNativeQuery(
            "select u.email from jhi_user u join related_group ru on ru.user_id = u.id where ru.related_group_id = :id " +
                "and u.activated = 'true' ")
            .setParameter("id", loggedUserGroupId).getResultList();
    }

    public Collection<? extends String> findUserPhoneForRelationGroup(Long loggedUserGroupId) {
        if(loggedUserGroupId == null) {
            return new ArrayList<>();
        }
        return entityManager.unwrap(Session.class).createNativeQuery(
            "select u.phone_number from jhi_user u join related_group ru on ru.user_id = u.id where ru.related_group_id = :id " +
                "and u.activated = 'true' ")
            .setParameter("id", loggedUserGroupId).getResultList();
    }

    public String getUserApprovalVerificationRequirement(Long userId) {
        return userRepository.getUserApprovalVerificationRequirement(userId);
    }

    public ApprovalType getUserApprovalType(Long userId) {
        return userRepository.getUserApprovalType(userId);
    }

    public Collection<Long> getTwoLevelApproval(List<Long> userIds) {
        return userRepository.getTwoLevelApproval(userIds);
    }

    @Transactional
    public void resetPasswordResetProcessForExpired(List<Long> notActivated) {
        final List<User> all = findAll(notActivated);

        for (User user : all) {
            if(!user.getResetDate().isAfter(Instant.now().minusSeconds(864000))) {
                user.setResetDate(Instant.now());
                user.setResetKey(RandomUtil.generateResetKey());
            }
        }
    }
}
