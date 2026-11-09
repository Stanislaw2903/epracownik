package com.eurosoftit.ebok.notification.birthday;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "NOTIFICATION_TEMPLATE")
@Data
public class NotificationTemplateEntity {

    @Id
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String content;
}
