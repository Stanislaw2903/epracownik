package com.eurosoftit.ebok.repository;

import com.eurosoftit.ebok.domain.NotificationRead;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the NotificationRead entity.
 */
@Repository
public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long>, JpaSpecificationExecutor<NotificationRead> {

    @Query(value = "select distinct notificationRead from NotificationRead notificationRead left join fetch notificationRead.users",
        countQuery = "select count(distinct notificationRead) from NotificationRead notificationRead")
    Page<NotificationRead> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct notificationRead from NotificationRead notificationRead left join fetch notificationRead.users")
    List<NotificationRead> findAllWithEagerRelationships();

    @Query("select distinct notificationRead from NotificationRead notificationRead left join fetch notificationRead.users u where notify =:id")
    List<NotificationRead> findAllByNotificationIdWithEagerRelationships(@Param("id") Long id);

    @Query("select notificationRead from NotificationRead notificationRead left join fetch notificationRead.users where notificationRead.id =:id")
    Optional<NotificationRead> findOneWithEagerRelationships(@Param("id") Long id);
}
