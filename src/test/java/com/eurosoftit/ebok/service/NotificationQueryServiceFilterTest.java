package com.eurosoftit.ebok.service;

import io.github.jhipster.service.filter.LongFilter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationQueryServiceFilterTest {

    @Test
    void inTakesPrecedence() {
        LongFilter filter = new LongFilter();
        filter.setIn(Arrays.asList(3L, 5L));
        filter.setEquals(7L);
        assertThat(NotificationQueryService.filterIds(filter)).containsExactly(3L, 5L);
    }

    @Test
    void equalsWhenNoIn() {
        LongFilter filter = new LongFilter();
        filter.setEquals(7L);
        assertThat(NotificationQueryService.filterIds(filter)).containsExactly(7L);
    }

    @Test
    void emptyFilterGivesNoIds() {
        assertThat(NotificationQueryService.filterIds(new LongFilter())).isEmpty();
    }
}
