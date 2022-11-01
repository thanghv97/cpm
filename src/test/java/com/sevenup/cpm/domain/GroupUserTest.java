package com.sevenup.cpm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sevenup.cpm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GroupUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GroupUser.class);
        GroupUser groupUser1 = new GroupUser();
        groupUser1.setId(1L);
        GroupUser groupUser2 = new GroupUser();
        groupUser2.setId(groupUser1.getId());
        assertThat(groupUser1).isEqualTo(groupUser2);
        groupUser2.setId(2L);
        assertThat(groupUser1).isNotEqualTo(groupUser2);
        groupUser1.setId(null);
        assertThat(groupUser1).isNotEqualTo(groupUser2);
    }
}
