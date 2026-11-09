package com.eurosoftit.ebok.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.eurosoftit.ebok.web.rest.TestUtil;

public class NotificationReadTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationRead.class);
        NotificationRead notificationRead1 = new NotificationRead();
        notificationRead1.setId(1L);
        NotificationRead notificationRead2 = new NotificationRead();
        notificationRead2.setId(notificationRead1.getId());
        assertThat(notificationRead1).isEqualTo(notificationRead2);
        notificationRead2.setId(2L);
        assertThat(notificationRead1).isNotEqualTo(notificationRead2);
        notificationRead1.setId(null);
        assertThat(notificationRead1).isNotEqualTo(notificationRead2);
    }
}
