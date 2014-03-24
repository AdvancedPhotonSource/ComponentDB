/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "user_user_group")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserUserGroup.findAll", query = "SELECT u FROM UserUserGroup u"),
    @NamedQuery(name = "UserUserGroup.findById", query = "SELECT u FROM UserUserGroup u WHERE u.id = :id"),
    @NamedQuery(name = "UserUserGroup.findByUserIdAndUserGroupId", query = "SELECT u FROM UserUserGroup u WHERE u.user.id = :userId AND u.userGroup.id = :userGroupId"),
    @NamedQuery(name = "UserUserGroup.findByUserIdAndUserGroupName", query = "SELECT u FROM UserUserGroup u JOIN u.userGroup ug WHERE u.user.id = :userId AND u.userGroup.name = :userGroupName"),
    @NamedQuery(name = "UserUserGroup.findByUserUsernameAndUserGroupName", query = "SELECT u FROM UserUserGroup u JOIN u.user uu,u.userGroup ug WHERE uu.username = :userUsername AND ug.name = :userGroupName")})


public class UserUserGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "user_group_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserGroup userGroup;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user;

    public UserUserGroup() {
    }

    public UserUserGroup(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserUserGroup)) {
            return false;
        }
        UserUserGroup other = (UserUserGroup) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.UserUserGroup[ id=" + id + " ]";
    }
    
}
