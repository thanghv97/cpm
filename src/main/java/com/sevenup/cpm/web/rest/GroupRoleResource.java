package com.sevenup.cpm.web.rest;

import com.sevenup.cpm.domain.GroupRole;
import com.sevenup.cpm.repository.GroupRoleRepository;
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
 * REST controller for managing {@link com.sevenup.cpm.domain.GroupRole}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GroupRoleResource {

    private final Logger log = LoggerFactory.getLogger(GroupRoleResource.class);

    private static final String ENTITY_NAME = "groupRole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GroupRoleRepository groupRoleRepository;

    public GroupRoleResource(GroupRoleRepository groupRoleRepository) {
        this.groupRoleRepository = groupRoleRepository;
    }

    /**
     * {@code POST  /group-roles} : Create a new groupRole.
     *
     * @param groupRole the groupRole to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new groupRole, or with status {@code 400 (Bad Request)} if the groupRole has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/group-roles")
    public ResponseEntity<GroupRole> createGroupRole(@RequestBody GroupRole groupRole) throws URISyntaxException {
        log.debug("REST request to save GroupRole : {}", groupRole);
        if (groupRole.getId() != null) {
            throw new BadRequestAlertException("A new groupRole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GroupRole result = groupRoleRepository.save(groupRole);
        return ResponseEntity
            .created(new URI("/api/group-roles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /group-roles/:id} : Updates an existing groupRole.
     *
     * @param id the id of the groupRole to save.
     * @param groupRole the groupRole to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groupRole,
     * or with status {@code 400 (Bad Request)} if the groupRole is not valid,
     * or with status {@code 500 (Internal Server Error)} if the groupRole couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/group-roles/{id}")
    public ResponseEntity<GroupRole> updateGroupRole(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GroupRole groupRole
    ) throws URISyntaxException {
        log.debug("REST request to update GroupRole : {}, {}", id, groupRole);
        if (groupRole.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groupRole.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groupRoleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GroupRole result = groupRoleRepository.save(groupRole);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, groupRole.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /group-roles/:id} : Partial updates given fields of an existing groupRole, field will ignore if it is null
     *
     * @param id the id of the groupRole to save.
     * @param groupRole the groupRole to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groupRole,
     * or with status {@code 400 (Bad Request)} if the groupRole is not valid,
     * or with status {@code 404 (Not Found)} if the groupRole is not found,
     * or with status {@code 500 (Internal Server Error)} if the groupRole couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/group-roles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GroupRole> partialUpdateGroupRole(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GroupRole groupRole
    ) throws URISyntaxException {
        log.debug("REST request to partial update GroupRole partially : {}, {}", id, groupRole);
        if (groupRole.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groupRole.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groupRoleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GroupRole> result = groupRoleRepository
            .findById(groupRole.getId())
            .map(existingGroupRole -> {
                if (groupRole.getGroupId() != null) {
                    existingGroupRole.setGroupId(groupRole.getGroupId());
                }
                if (groupRole.getRoleId() != null) {
                    existingGroupRole.setRoleId(groupRole.getRoleId());
                }

                return existingGroupRole;
            })
            .map(groupRoleRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, groupRole.getId().toString())
        );
    }

    /**
     * {@code GET  /group-roles} : get all the groupRoles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of groupRoles in body.
     */
    @GetMapping("/group-roles")
    public List<GroupRole> getAllGroupRoles() {
        log.debug("REST request to get all GroupRoles");
        return groupRoleRepository.findAll();
    }

    /**
     * {@code GET  /group-roles/:id} : get the "id" groupRole.
     *
     * @param id the id of the groupRole to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the groupRole, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/group-roles/{id}")
    public ResponseEntity<GroupRole> getGroupRole(@PathVariable Long id) {
        log.debug("REST request to get GroupRole : {}", id);
        Optional<GroupRole> groupRole = groupRoleRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(groupRole);
    }

    /**
     * {@code DELETE  /group-roles/:id} : delete the "id" groupRole.
     *
     * @param id the id of the groupRole to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/group-roles/{id}")
    public ResponseEntity<Void> deleteGroupRole(@PathVariable Long id) {
        log.debug("REST request to delete GroupRole : {}", id);
        groupRoleRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
