package com.eurosoftit.ebok.notification.birthday;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface NotificationInfoRepository extends JpaRepository<NotificationInfoEntity, Long> {
    @Query("select case when count(1) > 1 then 'true' else 'false' end " +
        "FROM NotificationInfoEntity nie where nie.externalId = :id and nie.type = :notificationType")
    boolean existsByIdAndType(@Param("id") Long id, @Param("notificationType") NotificationType notificationType);

    @Query("select n from NotificationInfoEntity n where n.sent = false")
    List<NotificationInfoEntity> findNotSent();
}
