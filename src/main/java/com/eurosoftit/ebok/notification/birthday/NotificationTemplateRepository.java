package com.eurosoftit.ebok.notification.birthday;

import org.springframework.data.jpa.repository.JpaRepository;

interface NotificationTemplateRepository extends JpaRepository<NotificationTemplateEntity, NotificationType> {
}
