package com.eurosoftit.ebok.web.rest.graphic;

import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.web.rest.group.UserGroupEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class GraphicResourceFilterTest {

    @Test
    void noFiltersKeepsEveryone() {
        assertThat(GraphicResource.matchesFilters(user("Nowak", 1L), null, null)).isTrue();
        assertThat(GraphicResource.matchesFilters(user("Nowak", null), Collections.emptyList(), " ")).isTrue();
    }

    @Test
    void groupFilterKeepsOnlyUsersFromSelectedGroups() {
        assertThat(GraphicResource.matchesFilters(user("Nowak", 1L), Arrays.asList(1L, 2L), null)).isTrue();
        assertThat(GraphicResource.matchesFilters(user("Nowak", 3L), Arrays.asList(1L, 2L), null)).isFalse();
        assertThat(GraphicResource.matchesFilters(user("Nowak", null), Arrays.asList(1L, 2L), null)).isFalse();
    }

    @Test
    void lastNameFilterIsCaseInsensitiveContains() {
        assertThat(GraphicResource.matchesFilters(user("Kałużny", 1L), null, "ałuż")).isTrue();
        assertThat(GraphicResource.matchesFilters(user("Kałużny", 1L), null, " KAŁ ")).isTrue();
        assertThat(GraphicResource.matchesFilters(user("Nowak", 1L), null, "kał")).isFalse();
    }

    @Test
    void groupAndLastNameMustBothMatch() {
        assertThat(GraphicResource.matchesFilters(user("Nowak", 1L), Collections.singletonList(1L), "now")).isTrue();
        assertThat(GraphicResource.matchesFilters(user("Nowak", 2L), Collections.singletonList(1L), "now")).isFalse();
    }

    private static User user(String lastName, Long groupId) {
        User user = new User();
        user.setLastName(lastName);
        if (groupId != null) {
            UserGroupEntity group = new UserGroupEntity();
            group.setId(groupId);
            user.setGroup(group);
        }
        return user;
    }
}
