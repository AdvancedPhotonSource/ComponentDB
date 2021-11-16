/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author djarosz
 */
@Entity
@Cacheable(true)
@Table(name = "user_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserRole.findAll", query = "SELECT u FROM UserRole u"),
    @NamedQuery(name = "UserRole.findByUserId", query = "SELECT u FROM UserRole u WHERE u.userRolePK.userId = :userId"),
    @NamedQuery(name = "UserRole.findByRoleTypeId", query = "SELECT u FROM UserRole u WHERE u.userRolePK.roleTypeId = :roleTypeId"),
    @NamedQuery(name = "UserRole.findByUserGroupId", query = "SELECT u FROM UserRole u WHERE u.userRolePK.userGroupId = :userGroupId")})
public class UserRole extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserRolePK userRolePK;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private UserInfo userInfo;
    @JoinColumn(name = "role_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private RoleType roleType;
    @JoinColumn(name = "user_group_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private UserGroup userGroup;

    public UserRole() {
    }

    public void init(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserRole(UserRolePK userRolePK) {
        this.userRolePK = userRolePK;
    }

    public UserRole(int userId, int roleTypeId, int userGroupId) {
        this.userRolePK = new UserRolePK(userId, roleTypeId, userGroupId);
    }

    @JsonIgnore
    public UserRolePK getUserRolePK() {
        return userRolePK;
    }

    public void setUserRolePK(UserRolePK userRolePK) {
        this.userRolePK = userRolePK;
    }

    @JsonIgnore
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    @Override
    @JsonIgnore
    public Object getId() {
        return userRolePK; 
    }
    
    public static UserRolePK createPrimaryKeyObject(UserRole userRole) throws CdbException {
        if (userRole.getUserInfo() == null ) {
            throw new CdbException("User info not specified for a user role object.");
        }
        
        if (userRole.getRoleType() == null ) {
            throw new CdbException("Role type not specified for a user role object.");
        }
        
        if (userRole.getUserGroup() == null ) {
            throw new CdbException("User Group not specified for a user role object.");
        }
        
        return new UserRolePK(userRole.getUserInfo().getId()
                , userRole.getRoleType().getId()
                , userRole.getUserGroup().getId());                
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userRolePK != null ? userRolePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserRole)) {
            return false;
        }
        UserRole other = (UserRole) object;
        if ((this.userRolePK == null && other.userRolePK != null) || (this.userRolePK != null && !this.userRolePK.equals(other.userRolePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.UserRole[ userRolePK=" + userRolePK + " ]";
    }

}
