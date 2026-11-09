package com.eurosoftit.ebok.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotificationSenderSubjectTest {

    @Test
    void addsPrefixOnce() {
        assertThat(UserNotificationSender.withSubjectPrefix("wniosek urlopowy")).isEqualTo("e_Pracownik - wniosek urlopowy");
    }

    @Test
    void keepsExistingPrefix() {
        assertThat(UserNotificationSender.withSubjectPrefix("e_Pracownik - powiadomienie")).isEqualTo("e_Pracownik - powiadomienie");
    }

    @Test
    void blankSubjectGivesAppName() {
        assertThat(UserNotificationSender.withSubjectPrefix(null)).isEqualTo("e_Pracownik");
        assertThat(UserNotificationSender.withSubjectPrefix(" ")).isEqualTo("e_Pracownik");
    }
}
