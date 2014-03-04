/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "role_name")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RoleName.findAll", query = "SELECT r FROM RoleName r"),
    @NamedQuery(name = "RoleName.findByRoleNameId", query = "SELECT r FROM RoleName r WHERE r.roleNameId = :roleNameId"),
    @NamedQuery(name = "RoleName.findByRoleName", query = "SELECT r FROM RoleName r WHERE r.roleName = :roleName")})
public class RoleName implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_name_id")
    private Integer roleNameId;
    @Size(max = 30)
    @Column(name = "role_name")
    private String roleName;
    @OneToMany(mappedBy = "roleNameId")
    private List<ComponentTypePerson> componentTypePersonList;

    public RoleName() {
    }

    public RoleName(Integer roleNameId) {
        this.roleNameId = roleNameId;
    }

    public Integer getRoleNameId() {
        return roleNameId;
    }

    public void setRoleNameId(Integer roleNameId) {
        this.roleNameId = roleNameId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @XmlTransient
    public List<ComponentTypePerson> getComponentTypePersonList() {
        return componentTypePersonList;
    }

    public void setComponentTypePersonList(List<ComponentTypePerson> componentTypePersonList) {
        this.componentTypePersonList = componentTypePersonList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roleNameId != null ? roleNameId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoleName)) {
            return false;
        }
        RoleName other = (RoleName) object;
        if ((this.roleNameId == null && other.roleNameId != null) || (this.roleNameId != null && !this.roleNameId.equals(other.roleNameId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RoleName[ roleNameId=" + roleNameId + " ]";
    }
    
}
