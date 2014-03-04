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
@Table(name = "group_name")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GroupName.findAll", query = "SELECT g FROM GroupName g"),
    @NamedQuery(name = "GroupName.findByGroupNameId", query = "SELECT g FROM GroupName g WHERE g.groupNameId = :groupNameId"),
    @NamedQuery(name = "GroupName.findByGroupName", query = "SELECT g FROM GroupName g WHERE g.groupName = :groupName")})
public class GroupName implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "group_name_id")
    private Integer groupNameId;
    @Size(max = 100)
    @Column(name = "group_name")
    private String groupName;
    @OneToMany(mappedBy = "aoiCustomerGroupId")
    private List<Aoi> aoiList;
    @OneToMany(mappedBy = "groupNameId")
    private List<PersonGroup> personGroupList;
    @OneToMany(mappedBy = "groupNameId")
    private List<ApsComponent> apsComponentList;

    public GroupName() {
    }

    public GroupName(Integer groupNameId) {
        this.groupNameId = groupNameId;
    }

    public Integer getGroupNameId() {
        return groupNameId;
    }

    public void setGroupNameId(Integer groupNameId) {
        this.groupNameId = groupNameId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @XmlTransient
    public List<Aoi> getAoiList() {
        return aoiList;
    }

    public void setAoiList(List<Aoi> aoiList) {
        this.aoiList = aoiList;
    }

    @XmlTransient
    public List<PersonGroup> getPersonGroupList() {
        return personGroupList;
    }

    public void setPersonGroupList(List<PersonGroup> personGroupList) {
        this.personGroupList = personGroupList;
    }

    @XmlTransient
    public List<ApsComponent> getApsComponentList() {
        return apsComponentList;
    }

    public void setApsComponentList(List<ApsComponent> apsComponentList) {
        this.apsComponentList = apsComponentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupNameId != null ? groupNameId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupName)) {
            return false;
        }
        GroupName other = (GroupName) object;
        if ((this.groupNameId == null && other.groupNameId != null) || (this.groupNameId != null && !this.groupNameId.equals(other.groupNameId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.GroupName[ groupNameId=" + groupNameId + " ]";
    }
    
}
