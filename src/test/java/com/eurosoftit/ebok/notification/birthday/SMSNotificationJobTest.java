package com.eurosoftit.ebok.notification.birthday;

import com.eurosoftit.ebok.EbokApp;
import com.eurosoftit.ebok.domain.UserView;
import com.eurosoftit.ebok.domain.UserViewRepository;
import com.iss.MessageResource;
import io.swagger.models.auth.In;
import org.apache.commons.compress.utils.Lists;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = EbokApp.class)
public class SMSNotificationJobTest {

    @Test
    public void test() throws IllegalAccessException {

        final NotificationInfoRepository notificationInfoRepository = Mockito.mock(NotificationInfoRepository.class);
        final MessageResource messageResource = Mockito.mock(MessageResource.class);
        final NotificationTemplateRepository notificationTemplateRepository = Mockito.mock(NotificationTemplateRepository.class);

        Mockito
            .when(notificationInfoRepository.existsByIdAndType(Mockito.any(), Mockito.any()))
            .thenReturn(false);

        Mockito
            .when(messageResource.sendSMS(Mockito.any()))
            .thenReturn(ResponseEntity.ok().build());

        NotificationTemplateEntity notificationTemplateEntity = new NotificationTemplateEntity();
        notificationTemplateEntity.setType(NotificationType.BIRTH_SMS);
        notificationTemplateEntity.setContent("Test {name}");

        Mockito
            .when(notificationTemplateRepository.findById(NotificationType.BIRTH_SMS))
            .thenReturn(Optional.of(notificationTemplateEntity));

        final List<UserView> userViews = prepareUsers();

        final UserViewRepository userRepository = Mockito.mock(UserViewRepository.class);
        Mockito
            .when(userRepository.findByBirthDate(Mockito.any(Integer.class), Mockito.any(Integer.class)))
            .thenReturn(userViews);

        NotificationBirthDayJob notificationBirthDayJob = new NotificationBirthDayJob(userRepository);

        setField("notificationInfoRepository", notificationInfoRepository, notificationBirthDayJob);
        setField("messageResource", messageResource, notificationBirthDayJob);
        setField("notificationTemplateRepository", notificationTemplateRepository, notificationBirthDayJob);

        notificationBirthDayJob.invoke();

        Mockito.verify(notificationInfoRepository, Mockito.times(1)).save(Mockito.any());
    }

    private void setField(String name, Object obj, NotificationBirthDayJob notificationBirthDayJob) throws IllegalAccessException {
        final Field field = ReflectionUtils.findField(NotificationBirthDayJob.class, name);
        field.setAccessible(true);
        field.set(notificationBirthDayJob, obj);
    }

    private List<UserView> prepareUsers() {
        List<UserView> userViews = Lists.newArrayList();
        UserView userView = new UserView();
        userView.setBirthDate(LocalDate.now());
        userView.setId(1L);
        userView.setPesel("88040400239");
        userView.setPhone("077880701");
        userViews.add(userView);

        return userViews;
    }
}
