package com.sevenup.cpm.repository;

import com.sevenup.cpm.domain.GroupUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GroupUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {}
