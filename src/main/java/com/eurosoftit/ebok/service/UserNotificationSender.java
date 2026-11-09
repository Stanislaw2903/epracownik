package com.eurosoftit.ebok.service;

import com.iss.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserNotificationSender {

    @Value("${jhipster.mail.lagInMillis:500}")
    private Long lagInMillis;

    @Value("${smsapi.url:''}")
    private String smsapiUrl;

    @Value("${smsapi.notification.type:SMS}")
    private NotificationType notificationType;

    @Autowired(required = false)
    private MessageResource messageResource;

    @Autowired
    private MailService mailService;

    public void send(List<String> toIdentifiers, String content, String subject) {
        try {
            if (!CollectionUtils.isEmpty(toIdentifiers)) {
                toIdentifiers = toIdentifiers.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
                for (String identifier : toIdentifiers) {
                    switch (notificationType) {
                        case MAIL:
                            if (!this.smsapiUrl.isEmpty() && this.messageResource != null) {
                                final MailRecipientDTO mailRecipient = new MailRecipientDTO();
                                mailRecipient.setIdentifier(identifier);

                                final MailMessageDTO smsMessage = new MailMessageDTO();
                                smsMessage.setSubject("e_Pracownik - " + subject);
                                smsMessage.setRecipients(Collections.singletonList(mailRecipient));
                                smsMessage.setContent(content);
                                this.messageResource.sendMail(smsMessage);
                                Thread.sleep(lagInMillis);
                            }
                            break;
                        case SMS:
                            if (!this.smsapiUrl.isEmpty() && this.messageResource != null) {
                                final SMSRecipientDTO smsRecipientDTO = new SMSRecipientDTO();
                                smsRecipientDTO.setIdentifier(identifier);

                                final SMSMessageDTO smsMessageDTO = new SMSMessageDTO();
                                smsMessageDTO.setRecipients(Collections.singletonList(smsRecipientDTO));
                                smsMessageDTO.setContent("e_Pracownik: " + content);
                                this.messageResource.sendSMS(smsMessageDTO);
                            }
                            break;
                        case LOCAL_MAIL:
                            log.error("Try to send email " + identifier);
                            this.mailService.sendEmail(identifier, "e_Pracownik - " + subject, "e_Pracownik: " + content, false, false);
                            Thread.sleep(lagInMillis);
                            break;
                    }
                }
            }
        } catch (Exception exception) {
            log.error("", exception);
            log.error("Error while sending - {} - {}", exception.getMessage());
        }
    }

    public void sendSms(List<String> toIdentifiers, String content) {
        try {
            if (!this.smsapiUrl.isEmpty() && this.messageResource != null) {
                if (!CollectionUtils.isEmpty(toIdentifiers)) {
                    toIdentifiers = toIdentifiers.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    for (String identifier : toIdentifiers) {
                        final SMSRecipientDTO smsRecipientDTO = new SMSRecipientDTO();
                        smsRecipientDTO.setIdentifier(identifier);

                        final SMSMessageDTO smsMessageDTO = new SMSMessageDTO();
                        smsMessageDTO.setRecipients(Collections.singletonList(smsRecipientDTO));
                        smsMessageDTO.setContent("e_Pracownik: " + content);
                        this.messageResource.sendSMS(smsMessageDTO);
                    }
                }
            }
        } catch (Exception exception) {
            log.error("", exception);
            log.error("Error while sending - {} - {}", notificationType, exception.getMessage());
        }
    }

    public void sendEmail(List<String> toIdentifiers, String content, String subject) {
        log.debug("Try send by Email init");
        try {
            boolean conditon = !this.smsapiUrl.isEmpty() && this.messageResource != null;
            log.debug("Try send by condition " + conditon);
            if (conditon) {
                log.debug("Try send by Email " + toIdentifiers);
                if (!CollectionUtils.isEmpty(toIdentifiers)) {
                    toIdentifiers = toIdentifiers.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    log.debug("Try send by Email " + toIdentifiers);
                    for (String identifier : toIdentifiers) {
                        log.debug("Try send by Email " + identifier);
                        switch (notificationType) {
                            case MAIL:
                                final MailRecipientDTO mailRecipient = new MailRecipientDTO();
                                mailRecipient.setIdentifier(identifier);

                                final MailMessageDTO smsMessage = new MailMessageDTO();
                                smsMessage.setSubject("e_Pracownik - " + subject);
                                smsMessage.setRecipients(Collections.singletonList(mailRecipient));
                                smsMessage.setContent(content);
                                this.messageResource.sendMail(smsMessage);
                                break;
                            default:
                                this.mailService.sendEmail(identifier, subject, content, false, containsHtml(content));
                                break;
                        }

                        Thread.sleep(lagInMillis);
                    }
                }
            }
        } catch (Exception exception) {
            log.error("", exception);
            log.error("Error while sending - {} - {}", notificationType, exception.getMessage());
        }
    }

    public boolean containsHtml(String text) {
        if (text == null) return false;
        return text.matches(".*<[^>]+>.*");
    }

    public enum NotificationType {
        MAIL, SMS, LOCAL_MAIL
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }
}
