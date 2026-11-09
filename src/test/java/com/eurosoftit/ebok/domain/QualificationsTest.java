package com.eurosoftit.ebok.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.eurosoftit.ebok.web.rest.TestUtil;

public class QualificationsTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Qualifications.class);
        Qualifications qualifications1 = new Qualifications();
        qualifications1.setId(1L);
        Qualifications qualifications2 = new Qualifications();
        qualifications2.setId(qualifications1.getId());
        assertThat(qualifications1).isEqualTo(qualifications2);
        qualifications2.setId(2L);
        assertThat(qualifications1).isNotEqualTo(qualifications2);
        qualifications1.setId(null);
        assertThat(qualifications1).isNotEqualTo(qualifications2);
    }
}
