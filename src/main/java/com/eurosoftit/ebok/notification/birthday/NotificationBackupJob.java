package com.eurosoftit.ebok.notification.birthday;

import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.repository.UserRepository;
import com.eurosoftit.ebok.service.UserNotificationSender;
import com.iss.SMSMessageDTO;
import com.iss.SMSRecipientDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty("app.job.birth.enabled")
class NotificationBackupJob {
    private final NotificationInfoRepository notificationInfoRepository;
    private final UserRepository userRepository;

    @Autowired
    private UserNotificationSender messageResource;

    @Scheduled(cron = "${app.job.birth.backup}")
    void process() {
        final List<NotificationInfoEntity> notSent = notificationInfoRepository.findNotSent();

        for (NotificationInfoEntity notificationInfo : notSent) {
            try {
                final Long externalId = notificationInfo.getExternalId();
                userRepository.findById(externalId).ifPresent(
                    user -> {
                        if (user.getActivated()) {
                            UserNotificationSender.NotificationType notificationType = messageResource.getNotificationType();
                            if (notificationType == UserNotificationSender.NotificationType.SMS) {
                                if (StringUtils.isNotBlank(user.getPhoneNumber())) {
                                    messageResource.send(Arrays.asList(user.getPhoneNumber()), notificationInfo.getContent(), "e_Pracownik - powiadomienie");
                                }
                            } else {
                                if (StringUtils.isNotBlank(user.getEmail())) {
                                    messageResource.send(Arrays.asList(user.getEmail()), notificationInfo.getContent(), "e_Pracownik - powiadomienie");
                                }
                            }
                        }
                    }
                );
                notificationInfo.setSent(true);
                notificationInfoRepository.save(notificationInfo);
            } catch (Exception e) {
                log.error("Error while sending sms. {}", e.getMessage());
            }
        }
    }

    private SMSMessageDTO prepareSms(User user, String content) {
        final SMSMessageDTO smsMessageDTO = new SMSMessageDTO();
        final List<SMSRecipientDTO> smsRecipientDTOS = getSmsRecipientDTOS(user);
        smsMessageDTO.setRecipients(smsRecipientDTOS);
        smsMessageDTO.setContent(content);
        return smsMessageDTO;
    }

    private List<SMSRecipientDTO> getSmsRecipientDTOS(User user) {
        final List<SMSRecipientDTO> smsRecipientDTOS = new ArrayList<>();
        SMSRecipientDTO smsRecipientDTO = new SMSRecipientDTO();
        smsRecipientDTO.setIdentifier(user.getPhoneNumber());
        smsRecipientDTOS.add(smsRecipientDTO);
        return smsRecipientDTOS;
    }
}
