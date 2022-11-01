package com.sevenup.cpm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sevenup.cpm.IntegrationTest;
import com.sevenup.cpm.domain.GroupUser;
import com.sevenup.cpm.repository.GroupUserRepository;
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
 * Integration tests for the {@link GroupUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GroupUserResourceIT {

    private static final Long DEFAULT_GROUP_ID = 1L;
    private static final Long UPDATED_GROUP_ID = 2L;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String ENTITY_API_URL = "/api/group-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GroupUserRepository groupUserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGroupUserMockMvc;

    private GroupUser groupUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GroupUser createEntity(EntityManager em) {
        GroupUser groupUser = new GroupUser().groupId(DEFAULT_GROUP_ID).userId(DEFAULT_USER_ID);
        return groupUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GroupUser createUpdatedEntity(EntityManager em) {
        GroupUser groupUser = new GroupUser().groupId(UPDATED_GROUP_ID).userId(UPDATED_USER_ID);
        return groupUser;
    }

    @BeforeEach
    public void initTest() {
        groupUser = createEntity(em);
    }

    @Test
    @Transactional
    void createGroupUser() throws Exception {
        int databaseSizeBeforeCreate = groupUserRepository.findAll().size();
        // Create the GroupUser
        restGroupUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(groupUser)))
            .andExpect(status().isCreated());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeCreate + 1);
        GroupUser testGroupUser = groupUserList.get(groupUserList.size() - 1);
        assertThat(testGroupUser.getGroupId()).isEqualTo(DEFAULT_GROUP_ID);
        assertThat(testGroupUser.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void createGroupUserWithExistingId() throws Exception {
        // Create the GroupUser with an existing ID
        groupUser.setId(1L);

        int databaseSizeBeforeCreate = groupUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroupUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(groupUser)))
            .andExpect(status().isBadRequest());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGroupUsers() throws Exception {
        // Initialize the database
        groupUserRepository.saveAndFlush(groupUser);

        // Get all the groupUserList
        restGroupUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].groupId").value(hasItem(DEFAULT_GROUP_ID.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));
    }

    @Test
    @Transactional
    void getGroupUser() throws Exception {
        // Initialize the database
        groupUserRepository.saveAndFlush(groupUser);

        // Get the groupUser
        restGroupUserMockMvc
            .perform(get(ENTITY_API_URL_ID, groupUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(groupUser.getId().intValue()))
            .andExpect(jsonPath("$.groupId").value(DEFAULT_GROUP_ID.intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingGroupUser() throws Exception {
        // Get the groupUser
        restGroupUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGroupUser() throws Exception {
        // Initialize the database
        groupUserRepository.saveAndFlush(groupUser);

        int databaseSizeBeforeUpdate = groupUserRepository.findAll().size();

        // Update the groupUser
        GroupUser updatedGroupUser = groupUserRepository.findById(groupUser.getId()).get();
        // Disconnect from session so that the updates on updatedGroupUser are not directly saved in db
        em.detach(updatedGroupUser);
        updatedGroupUser.groupId(UPDATED_GROUP_ID).userId(UPDATED_USER_ID);

        restGroupUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGroupUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGroupUser))
            )
            .andExpect(status().isOk());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeUpdate);
        GroupUser testGroupUser = groupUserList.get(groupUserList.size() - 1);
        assertThat(testGroupUser.getGroupId()).isEqualTo(UPDATED_GROUP_ID);
        assertThat(testGroupUser.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void putNonExistingGroupUser() throws Exception {
        int databaseSizeBeforeUpdate = groupUserRepository.findAll().size();
        groupUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, groupUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(groupUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGroupUser() throws Exception {
        int databaseSizeBeforeUpdate = groupUserRepository.findAll().size();
        groupUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(groupUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGroupUser() throws Exception {
        int databaseSizeBeforeUpdate = groupUserRepository.findAll().size();
        groupUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(groupUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGroupUserWithPatch() throws Exception {
        // Initialize the database
        groupUserRepository.saveAndFlush(groupUser);

        int databaseSizeBeforeUpdate = groupUserRepository.findAll().size();

        // Update the groupUser using partial update
        GroupUser partialUpdatedGroupUser = new GroupUser();
        partialUpdatedGroupUser.setId(groupUser.getId());

        partialUpdatedGroupUser.userId(UPDATED_USER_ID);

        restGroupUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroupUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGroupUser))
            )
            .andExpect(status().isOk());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeUpdate);
        GroupUser testGroupUser = groupUserList.get(groupUserList.size() - 1);
        assertThat(testGroupUser.getGroupId()).isEqualTo(DEFAULT_GROUP_ID);
        assertThat(testGroupUser.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void fullUpdateGroupUserWithPatch() throws Exception {
        // Initialize the database
        groupUserRepository.saveAndFlush(groupUser);

        int databaseSizeBeforeUpdate = groupUserRepository.findAll().size();

        // Update the groupUser using partial update
        GroupUser partialUpdatedGroupUser = new GroupUser();
        partialUpdatedGroupUser.setId(groupUser.getId());

        partialUpdatedGroupUser.groupId(UPDATED_GROUP_ID).userId(UPDATED_USER_ID);

        restGroupUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroupUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGroupUser))
            )
            .andExpect(status().isOk());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeUpdate);
        GroupUser testGroupUser = groupUserList.get(groupUserList.size() - 1);
        assertThat(testGroupUser.getGroupId()).isEqualTo(UPDATED_GROUP_ID);
        assertThat(testGroupUser.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void patchNonExistingGroupUser() throws Exception {
        int databaseSizeBeforeUpdate = groupUserRepository.findAll().size();
        groupUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, groupUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(groupUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGroupUser() throws Exception {
        int databaseSizeBeforeUpdate = groupUserRepository.findAll().size();
        groupUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(groupUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGroupUser() throws Exception {
        int databaseSizeBeforeUpdate = groupUserRepository.findAll().size();
        groupUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupUserMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(groupUser))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GroupUser in the database
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGroupUser() throws Exception {
        // Initialize the database
        groupUserRepository.saveAndFlush(groupUser);

        int databaseSizeBeforeDelete = groupUserRepository.findAll().size();

        // Delete the groupUser
        restGroupUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, groupUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GroupUser> groupUserList = groupUserRepository.findAll();
        assertThat(groupUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
