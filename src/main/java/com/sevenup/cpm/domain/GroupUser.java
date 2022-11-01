package com.sevenup.cpm.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A GroupUser.
 */
@Entity
@Table(name = "group_user")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GroupUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GroupUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public GroupUser groupId(Long groupId) {
        this.setGroupId(groupId);
        return this;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public GroupUser userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupUser)) {
            return false;
        }
        return id != null && id.equals(((GroupUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GroupUser{" +
            "id=" + getId() +
            ", groupId=" + getGroupId() +
            ", userId=" + getUserId() +
            "}";
    }
}
