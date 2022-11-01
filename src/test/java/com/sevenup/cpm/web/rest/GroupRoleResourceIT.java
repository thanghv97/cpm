package com.sevenup.cpm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sevenup.cpm.IntegrationTest;
import com.sevenup.cpm.domain.GroupRole;
import com.sevenup.cpm.repository.GroupRoleRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GroupRoleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GroupRoleResourceIT {

    private static final Long DEFAULT_GROUP_ID = 1L;
    private static final Long UPDATED_GROUP_ID = 2L;

    private static final Long DEFAULT_ROLE_ID = 1L;
    private static final Long UPDATED_ROLE_ID = 2L;

    private static final String ENTITY_API_URL = "/api/group-roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GroupRoleRepository groupRoleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGroupRoleMockMvc;

    private GroupRole groupRole;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GroupRole createEntity(EntityManager em) {
        GroupRole groupRole = new GroupRole().groupId(DEFAULT_GROUP_ID).roleId(DEFAULT_ROLE_ID);
        return groupRole;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GroupRole createUpdatedEntity(EntityManager em) {
        GroupRole groupRole = new GroupRole().groupId(UPDATED_GROUP_ID).roleId(UPDATED_ROLE_ID);
        return groupRole;
    }

    @BeforeEach
    public void initTest() {
        groupRole = createEntity(em);
    }

    @Test
    @Transactional
    void createGroupRole() throws Exception {
        int databaseSizeBeforeCreate = groupRoleRepository.findAll().size();
        // Create the GroupRole
        restGroupRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(groupRole)))
            .andExpect(status().isCreated());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeCreate + 1);
        GroupRole testGroupRole = groupRoleList.get(groupRoleList.size() - 1);
        assertThat(testGroupRole.getGroupId()).isEqualTo(DEFAULT_GROUP_ID);
        assertThat(testGroupRole.getRoleId()).isEqualTo(DEFAULT_ROLE_ID);
    }

    @Test
    @Transactional
    void createGroupRoleWithExistingId() throws Exception {
        // Create the GroupRole with an existing ID
        groupRole.setId(1L);

        int databaseSizeBeforeCreate = groupRoleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroupRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(groupRole)))
            .andExpect(status().isBadRequest());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGroupRoles() throws Exception {
        // Initialize the database
        groupRoleRepository.saveAndFlush(groupRole);

        // Get all the groupRoleList
        restGroupRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].groupId").value(hasItem(DEFAULT_GROUP_ID.intValue())))
            .andExpect(jsonPath("$.[*].roleId").value(hasItem(DEFAULT_ROLE_ID.intValue())));
    }

    @Test
    @Transactional
    void getGroupRole() throws Exception {
        // Initialize the database
        groupRoleRepository.saveAndFlush(groupRole);

        // Get the groupRole
        restGroupRoleMockMvc
            .perform(get(ENTITY_API_URL_ID, groupRole.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(groupRole.getId().intValue()))
            .andExpect(jsonPath("$.groupId").value(DEFAULT_GROUP_ID.intValue()))
            .andExpect(jsonPath("$.roleId").value(DEFAULT_ROLE_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingGroupRole() throws Exception {
        // Get the groupRole
        restGroupRoleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGroupRole() throws Exception {
        // Initialize the database
        groupRoleRepository.saveAndFlush(groupRole);

        int databaseSizeBeforeUpdate = groupRoleRepository.findAll().size();

        // Update the groupRole
        GroupRole updatedGroupRole = groupRoleRepository.findById(groupRole.getId()).get();
        // Disconnect from session so that the updates on updatedGroupRole are not directly saved in db
        em.detach(updatedGroupRole);
        updatedGroupRole.groupId(UPDATED_GROUP_ID).roleId(UPDATED_ROLE_ID);

        restGroupRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGroupRole.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGroupRole))
            )
            .andExpect(status().isOk());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeUpdate);
        GroupRole testGroupRole = groupRoleList.get(groupRoleList.size() - 1);
        assertThat(testGroupRole.getGroupId()).isEqualTo(UPDATED_GROUP_ID);
        assertThat(testGroupRole.getRoleId()).isEqualTo(UPDATED_ROLE_ID);
    }

    @Test
    @Transactional
    void putNonExistingGroupRole() throws Exception {
        int databaseSizeBeforeUpdate = groupRoleRepository.findAll().size();
        groupRole.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, groupRole.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(groupRole))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGroupRole() throws Exception {
        int databaseSizeBeforeUpdate = groupRoleRepository.findAll().size();
        groupRole.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(groupRole))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGroupRole() throws Exception {
        int databaseSizeBeforeUpdate = groupRoleRepository.findAll().size();
        groupRole.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupRoleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(groupRole)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGroupRoleWithPatch() throws Exception {
        // Initialize the database
        groupRoleRepository.saveAndFlush(groupRole);

        int databaseSizeBeforeUpdate = groupRoleRepository.findAll().size();

        // Update the groupRole using partial update
        GroupRole partialUpdatedGroupRole = new GroupRole();
        partialUpdatedGroupRole.setId(groupRole.getId());

        partialUpdatedGroupRole.groupId(UPDATED_GROUP_ID).roleId(UPDATED_ROLE_ID);

        restGroupRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroupRole.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGroupRole))
            )
            .andExpect(status().isOk());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeUpdate);
        GroupRole testGroupRole = groupRoleList.get(groupRoleList.size() - 1);
        assertThat(testGroupRole.getGroupId()).isEqualTo(UPDATED_GROUP_ID);
        assertThat(testGroupRole.getRoleId()).isEqualTo(UPDATED_ROLE_ID);
    }

    @Test
    @Transactional
    void fullUpdateGroupRoleWithPatch() throws Exception {
        // Initialize the database
        groupRoleRepository.saveAndFlush(groupRole);

        int databaseSizeBeforeUpdate = groupRoleRepository.findAll().size();

        // Update the groupRole using partial update
        GroupRole partialUpdatedGroupRole = new GroupRole();
        partialUpdatedGroupRole.setId(groupRole.getId());

        partialUpdatedGroupRole.groupId(UPDATED_GROUP_ID).roleId(UPDATED_ROLE_ID);

        restGroupRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroupRole.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGroupRole))
            )
            .andExpect(status().isOk());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeUpdate);
        GroupRole testGroupRole = groupRoleList.get(groupRoleList.size() - 1);
        assertThat(testGroupRole.getGroupId()).isEqualTo(UPDATED_GROUP_ID);
        assertThat(testGroupRole.getRoleId()).isEqualTo(UPDATED_ROLE_ID);
    }

    @Test
    @Transactional
    void patchNonExistingGroupRole() throws Exception {
        int databaseSizeBeforeUpdate = groupRoleRepository.findAll().size();
        groupRole.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, groupRole.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(groupRole))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGroupRole() throws Exception {
        int databaseSizeBeforeUpdate = groupRoleRepository.findAll().size();
        groupRole.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(groupRole))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGroupRole() throws Exception {
        int databaseSizeBeforeUpdate = groupRoleRepository.findAll().size();
        groupRole.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupRoleMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(groupRole))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GroupRole in the database
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGroupRole() throws Exception {
        // Initialize the database
        groupRoleRepository.saveAndFlush(groupRole);

        int databaseSizeBeforeDelete = groupRoleRepository.findAll().size();

        // Delete the groupRole
        restGroupRoleMockMvc
            .perform(delete(ENTITY_API_URL_ID, groupRole.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GroupRole> groupRoleList = groupRoleRepository.findAll();
        assertThat(groupRoleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
