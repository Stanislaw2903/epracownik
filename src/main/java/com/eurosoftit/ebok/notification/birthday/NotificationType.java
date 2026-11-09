package com.eurosoftit.ebok.notification.birthday;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.eurosoftit.ebok.notification.birthday.NotificationSource.SMS;

@AllArgsConstructor
public enum NotificationType {
    BIRTH_SMS(SMS);

    @Getter
    private NotificationSource source;
}
