package com.sevenup.cpm.web.rest;

import com.sevenup.cpm.domain.GroupUser;
import com.sevenup.cpm.repository.GroupUserRepository;
import com.sevenup.cpm.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.sevenup.cpm.domain.GroupUser}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GroupUserResource {

    private final Logger log = LoggerFactory.getLogger(GroupUserResource.class);

    private static final String ENTITY_NAME = "groupUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GroupUserRepository groupUserRepository;

    public GroupUserResource(GroupUserRepository groupUserRepository) {
        this.groupUserRepository = groupUserRepository;
    }

    /**
     * {@code POST  /group-users} : Create a new groupUser.
     *
     * @param groupUser the groupUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new groupUser, or with status {@code 400 (Bad Request)} if the groupUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/group-users")
    public ResponseEntity<GroupUser> createGroupUser(@RequestBody GroupUser groupUser) throws URISyntaxException {
        log.debug("REST request to save GroupUser : {}", groupUser);
        if (groupUser.getId() != null) {
            throw new BadRequestAlertException("A new groupUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GroupUser result = groupUserRepository.save(groupUser);
        return ResponseEntity
            .created(new URI("/api/group-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /group-users/:id} : Updates an existing groupUser.
     *
     * @param id the id of the groupUser to save.
     * @param groupUser the groupUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groupUser,
     * or with status {@code 400 (Bad Request)} if the groupUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the groupUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/group-users/{id}")
    public ResponseEntity<GroupUser> updateGroupUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GroupUser groupUser
    ) throws URISyntaxException {
        log.debug("REST request to update GroupUser : {}, {}", id, groupUser);
        if (groupUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groupUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groupUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GroupUser result = groupUserRepository.save(groupUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, groupUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /group-users/:id} : Partial updates given fields of an existing groupUser, field will ignore if it is null
     *
     * @param id the id of the groupUser to save.
     * @param groupUser the groupUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groupUser,
     * or with status {@code 400 (Bad Request)} if the groupUser is not valid,
     * or with status {@code 404 (Not Found)} if the groupUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the groupUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/group-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GroupUser> partialUpdateGroupUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GroupUser groupUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update GroupUser partially : {}, {}", id, groupUser);
        if (groupUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groupUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groupUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GroupUser> result = groupUserRepository
            .findById(groupUser.getId())
            .map(existingGroupUser -> {
                if (groupUser.getGroupId() != null) {
                    existingGroupUser.setGroupId(groupUser.getGroupId());
                }
                if (groupUser.getUserId() != null) {
                    existingGroupUser.setUserId(groupUser.getUserId());
                }

                return existingGroupUser;
            })
            .map(groupUserRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, groupUser.getId().toString())
        );
    }

    /**
     * {@code GET  /group-users} : get all the groupUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of groupUsers in body.
     */
    @GetMapping("/group-users")
    public List<GroupUser> getAllGroupUsers() {
        log.debug("REST request to get all GroupUsers");
        return groupUserRepository.findAll();
    }

    /**
     * {@code GET  /group-users/:id} : get the "id" groupUser.
     *
     * @param id the id of the groupUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the groupUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/group-users/{id}")
    public ResponseEntity<GroupUser> getGroupUser(@PathVariable Long id) {
        log.debug("REST request to get GroupUser : {}", id);
        Optional<GroupUser> groupUser = groupUserRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(groupUser);
    }

    /**
     * {@code DELETE  /group-users/:id} : delete the "id" groupUser.
     *
     * @param id the id of the groupUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/group-users/{id}")
    public ResponseEntity<Void> deleteGroupUser(@PathVariable Long id) {
        log.debug("REST request to delete GroupUser : {}", id);
        groupUserRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
