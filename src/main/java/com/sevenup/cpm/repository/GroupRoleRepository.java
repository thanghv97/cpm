package com.sevenup.cpm.repository;

import com.sevenup.cpm.domain.GroupRole;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GroupRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GroupRoleRepository extends JpaRepository<GroupRole, Long> {}
