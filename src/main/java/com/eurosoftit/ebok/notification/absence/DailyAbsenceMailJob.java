package com.eurosoftit.ebok.notification.absence;

import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.service.MailService;
import com.eurosoftit.ebok.service.UserNotificationSender;
import com.eurosoftit.ebok.web.rest.vacation.Vacation;
import com.eurosoftit.ebok.web.rest.vacation.VacationRepository;
import com.eurosoftit.ebok.web.rest.vacation.VacationStatus;
import com.iss.MailMessageDTO;
import com.iss.MailRecipientDTO;
import com.iss.MessageResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.text.Collator;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty("app.job.absence-mail.enabled")
public class DailyAbsenceMailJob {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final String TEMPLATE = "mail/dailyAbsenceEmail";

    @Autowired
    private VacationRepository vacationRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserNotificationSender userNotificationSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired(required = false)
    private MessageResource messageResource;

    @Value("${app.job.absence-mail.recipients}")
    private String recipients;
    @Value("${app.job.absence-mail.admin-contact}")
    private String adminContact;
    @Value("${smsapi.url:}")
    private String smsapiUrl;

    @Scheduled(cron = "${app.job.absence-mail.cron}")
    @Transactional(readOnly = true)
    public void invoke() {
        LocalDate today = LocalDate.now();
        ZonedDateTime dayStart = today.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime dayEnd = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1);

        List<String> absent = lines(vacationRepository.findAllCoveringDay(dayStart, dayEnd,
            Collections.singletonList(VacationStatus.ACCEPTED)), today, true);
        List<String> pending = lines(vacationRepository.findAllCoveringDay(dayStart, dayEnd,
            Arrays.asList(VacationStatus.WAITING, VacationStatus.ACCEPTED_INITIALLY)), today, false);

        if (absent.isEmpty() && pending.isEmpty()) {
            log.debug("Daily absence mail: no absences for {}, nothing sent", today);
            return;
        }

        List<String> to = recipients();
        if (to.isEmpty()) {
            log.warn("Daily absence mail: app.job.absence-mail.recipients is empty");
            return;
        }

        Context context = new Context(new Locale("pl", "PL"));
        context.setVariable("date", today.format(DATE));
        context.setVariable("absent", absent);
        context.setVariable("pending", pending);
        context.setVariable("adminContact", StringUtils.trimToNull(adminContact));
        String content = templateEngine.process(TEMPLATE, context);
        String subject = "[e_Pracownik] Lista osób nieobecnych dzisiaj: " + today.format(DATE);

        send(to, subject, content);
    }

    private List<String> lines(List<Vacation> vacations, LocalDate today, boolean absentList) {
        Collator collator = Collator.getInstance(new Locale("pl", "PL"));
        return vacations.stream()
            .filter(v -> coversDay(v, today))
            .sorted(Comparator
                .comparing((Vacation v) -> v.getCreatedBy().getLastName(), Comparator.nullsLast(collator::compare))
                .thenComparing(v -> v.getCreatedBy().getFirstName(), Comparator.nullsLast(collator::compare)))
            .map(v -> line(v, today, absentList))
            .collect(Collectors.toList());
    }

    static boolean coversDay(Vacation vacation, LocalDate day) {
        if (vacation.getDateFrom() == null || vacation.getDateTo() == null) {
            return false;
        }
        return !local(vacation.getDateFrom()).toLocalDate().isAfter(day)
            && !local(vacation.getDateTo()).toLocalDate().isBefore(day);
    }

    static String line(Vacation vacation, LocalDate today, boolean absentList) {
        User user = vacation.getCreatedBy();
        StringBuilder line = new StringBuilder()
            .append(StringUtils.defaultString(user.getLastName())).append(' ')
            .append(StringUtils.defaultString(user.getFirstName()));
        if (user.getGroup() != null && StringUtils.isNotBlank(user.getGroup().getName())) {
            line.append(" - ").append(user.getGroup().getName());
        }
        if (!absentList) {
            return line.toString();
        }
        if (Boolean.FALSE.equals(vacation.getFullDay())) {
            line.append(" (").append(local(vacation.getDateFrom()).format(TIME))
                .append('–').append(local(vacation.getDateTo()).format(TIME)).append(')');
        } else if (local(vacation.getDateTo()).toLocalDate().isAfter(today)) {
            line.append(" (do ").append(local(vacation.getDateTo()).format(DATE)).append(')');
        }
        return line.toString();
    }

    private static ZonedDateTime local(ZonedDateTime date) {
        return date.withZoneSameInstant(ZoneId.systemDefault());
    }

    private List<String> recipients() {
        return Arrays.stream(StringUtils.defaultString(recipients).split("[,;]"))
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    private void send(List<String> to, String subject, String content) {
        if (userNotificationSender.getNotificationType() == UserNotificationSender.NotificationType.LOCAL_MAIL) {
            sendLocal(to, subject, content);
        } else if (StringUtils.isNotBlank(smsapiUrl) && messageResource != null) {
            sendViaApi(to, subject, content);
        } else {
            log.warn("Daily absence mail not sent: neither LOCAL_MAIL nor smsapi.url configured");
        }
    }

    private void sendLocal(List<String> to, String subject, String content) {
        to.forEach(address -> mailService.sendEmail(address, subject, content, false, true));
        log.info("Daily absence mail sent locally to {}", to);
    }

    private void sendViaApi(List<String> to, String subject, String content) {
        for (String address : to) {
            MailRecipientDTO recipient = new MailRecipientDTO();
            recipient.setIdentifier(address);
            MailMessageDTO message = new MailMessageDTO();
            message.setSubject(subject);
            message.setRecipients(Collections.singletonList(recipient));
            message.setContent(content);
            try {
                messageResource.sendMail(message);
            } catch (Exception e) {
                log.error("Daily absence mail could not be sent to {}", address, e);
            }
        }
        log.info("Daily absence mail sent via API to {}", to);
    }
}
