package com.eurosoftit.ebok.web.rest.integration;

import com.eurosoftit.ebok.domain.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserCreatorResendTest {

    @Test
    void inactiveWithEmailCanReceive() {
        assertThat(UserCreator.resendProblem(user(false, "jan@firma.pl"))).isNull();
    }

    @Test
    void activeIsSkipped() {
        assertThat(UserCreator.resendProblem(user(true, "jan@firma.pl"))).isEqualTo("alreadyActivated");
    }

    @Test
    void missingEmailIsSkipped() {
        assertThat(UserCreator.resendProblem(user(false, " "))).isEqualTo("emailIsEmpty");
    }

    private static User user(boolean activated, String email) {
        User user = new User();
        user.setActivated(activated);
        user.setEmail(email);
        return user;
    }
}
