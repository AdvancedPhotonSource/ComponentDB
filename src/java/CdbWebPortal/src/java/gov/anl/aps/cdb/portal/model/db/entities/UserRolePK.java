/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author djarosz
 */
@Embeddable
public class UserRolePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_type_id")
    private int roleTypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_group_id")
    private int userGroupId;

    public UserRolePK() {
    }

    public UserRolePK(int userId, int roleTypeId, int userGroupId) {
        this.userId = userId;
        this.roleTypeId = roleTypeId;
        this.userGroupId = userGroupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(int roleTypeId) {
        this.roleTypeId = roleTypeId;
    }

    public int getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(int userGroupId) {
        this.userGroupId = userGroupId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) userId;
        hash += (int) roleTypeId;
        hash += (int) userGroupId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserRolePK)) {
            return false;
        }
        UserRolePK other = (UserRolePK) object;
        if (this.userId != other.userId) {
            return false;
        }
        if (this.roleTypeId != other.roleTypeId) {
            return false;
        }
        if (this.userGroupId != other.userGroupId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.UserRolePK[ userId=" + userId + ", roleTypeId=" + roleTypeId + ", userGroupId=" + userGroupId + " ]";
    }
    
}
