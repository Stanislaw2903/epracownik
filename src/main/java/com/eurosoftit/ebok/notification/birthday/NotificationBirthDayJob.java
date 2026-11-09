package com.eurosoftit.ebok.notification.birthday;

import com.eurosoftit.ebok.domain.UserView;
import com.eurosoftit.ebok.domain.UserViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty("app.job.birth.enabled")
class NotificationBirthDayJob extends AbstractNotificationJob {

    private final static NotificationType NOTIFICATION_TYPE = NotificationType.BIRTH_SMS;
    private final UserViewRepository userRepository;

    @Scheduled(cron = "${app.job.birth.value}")
    void invoke() {
        process();
    }

    @Override
    protected List<UserView> getData() {
        final LocalDate now = LocalDate.now();
        final int dayOfMonth = now.getDayOfMonth();
        final int month = now.getMonthValue();
        return userRepository.findByBirthDate(dayOfMonth, month);
    }

    @Override
    protected NotificationType getNotificationType() {
        return NOTIFICATION_TYPE;
    }
}
