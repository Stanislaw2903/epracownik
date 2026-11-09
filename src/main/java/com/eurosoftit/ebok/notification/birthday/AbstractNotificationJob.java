package com.eurosoftit.ebok.notification.birthday;

import com.eurosoftit.ebok.domain.UserView;
import com.eurosoftit.ebok.service.UserNotificationSender;
import com.iss.SMSMessageDTO;
import com.iss.SMSRecipientDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractNotificationJob {

    @Autowired
    private NotificationInfoRepository notificationInfoRepository;
    @Autowired
    private UserNotificationSender messageResource;
    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    void process() {
        notificationTemplateRepository.findById(getNotificationType()).ifPresent(
            template ->
            {
                final List<UserView> users = getData();

                for (UserView user : users) {

                    if (!user.isActivated()) {
                        continue;
                    }

                    if (!notificationInfoRepository.existsByIdAndType(user.getId(), getNotificationType())) {

                        if (messageResource.getNotificationType() == UserNotificationSender.NotificationType.SMS) {
                            sendSMS(template, user);
                        } else {
                            sendEmail(template, user);
                        }
                    }
                }
            }
        );
    }

    private void sendEmail(NotificationTemplateEntity template, UserView user) {
        final String email = user.getEmail();
        if (StringUtils.isNotBlank(email)) {
            String content = template.getContent();
            try {
                messageResource.send(Arrays.asList(email), TemplateInterpreter.process(user, content), "e_Pracownik - powiadomienie");
                saveNotification(user, content, true);
            } catch (Exception e) {
                saveNotification(user, content, false);
            }
        }
    }

    private void sendSMS(NotificationTemplateEntity template, UserView user) {
        final SMSMessageDTO smsMessageDTO = prepareSms(user, template);

        try {
            if (messageResource != null) {
                final List<String> identifiers = smsMessageDTO.getRecipients().stream().map(SMSRecipientDTO::getIdentifier).collect(Collectors.toList());
                messageResource.send(identifiers, smsMessageDTO.getContent(), null);
                saveNotification(user, smsMessageDTO.getContent(), true);
            }
        } catch (Exception e) {
            saveNotification(user, smsMessageDTO.getContent(), false);
        }
    }

    protected abstract List<UserView> getData();

    protected abstract NotificationType getNotificationType();

    private SMSMessageDTO prepareSms(UserView user, NotificationTemplateEntity template) {
        final SMSMessageDTO smsMessageDTO = new SMSMessageDTO();
        final List<SMSRecipientDTO> smsRecipientDTOS = getSmsRecipientDTOS(user);
        smsMessageDTO.setRecipients(smsRecipientDTOS);

        String content = template.getContent();
        content = TemplateInterpreter.process(user, content);

        smsMessageDTO.setContent(content);

        return smsMessageDTO;
    }

    private void saveNotification(UserView user, String content, boolean sent) {
        NotificationInfoEntity notificationInfo = new NotificationInfoEntity();
        notificationInfo.setContent(content);
        notificationInfo.setCreationDate(LocalDateTime.now());
        notificationInfo.setExternalId(user.getId());
        notificationInfo.setType(getNotificationType());
        notificationInfo.setStructureType(StructureType.USER);
        notificationInfo.setSent(sent);
        notificationInfoRepository.save(notificationInfo);
    }

    private List<SMSRecipientDTO> getSmsRecipientDTOS(UserView user) {
        final List<SMSRecipientDTO> smsRecipientDTOS = new ArrayList<>();
        SMSRecipientDTO smsRecipientDTO = new SMSRecipientDTO();
        smsRecipientDTO.setIdentifier(user.getPhone());
        smsRecipientDTOS.add(smsRecipientDTO);
        return smsRecipientDTOS;
    }
}
