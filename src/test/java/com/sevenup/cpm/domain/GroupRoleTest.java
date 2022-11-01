package com.sevenup.cpm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sevenup.cpm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GroupRoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GroupRole.class);
        GroupRole groupRole1 = new GroupRole();
        groupRole1.setId(1L);
        GroupRole groupRole2 = new GroupRole();
        groupRole2.setId(groupRole1.getId());
        assertThat(groupRole1).isEqualTo(groupRole2);
        groupRole2.setId(2L);
        assertThat(groupRole1).isNotEqualTo(groupRole2);
        groupRole1.setId(null);
        assertThat(groupRole1).isNotEqualTo(groupRole2);
    }
}
