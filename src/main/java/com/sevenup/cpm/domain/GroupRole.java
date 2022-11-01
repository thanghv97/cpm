package com.sevenup.cpm.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A GroupRole.
 */
@Entity
@Table(name = "group_role")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GroupRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "role_id")
    private Long roleId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GroupRole id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public GroupRole groupId(Long groupId) {
        this.setGroupId(groupId);
        return this;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public GroupRole roleId(Long roleId) {
        this.setRoleId(roleId);
        return this;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupRole)) {
            return false;
        }
        return id != null && id.equals(((GroupRole) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GroupRole{" +
            "id=" + getId() +
            ", groupId=" + getGroupId() +
            ", roleId=" + getRoleId() +
            "}";
    }
}
