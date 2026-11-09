package com.eurosoftit.ebok.notification.birthday;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATION_INFO")
@Data
public class NotificationInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    private Long externalId;

    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String content;

    @Enumerated(EnumType.STRING)
    private StructureType structureType;

    private Boolean sent;
}
