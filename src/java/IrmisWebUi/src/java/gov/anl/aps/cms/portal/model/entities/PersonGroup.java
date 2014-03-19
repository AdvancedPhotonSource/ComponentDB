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
@Table(name = "person_group")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersonGroup.findAll", query = "SELECT p FROM PersonGroup p"),
    @NamedQuery(name = "PersonGroup.findByPersonGroupId", query = "SELECT p FROM PersonGroup p WHERE p.personGroupId = :personGroupId")})
public class PersonGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "person_group_id")
    private Integer personGroupId;
    @JoinColumn(name = "group_name_id", referencedColumnName = "group_name_id")
    @ManyToOne
    private GroupName groupNameId;
    @JoinColumn(name = "person_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person personId;

    public PersonGroup() {
    }

    public PersonGroup(Integer personGroupId) {
        this.personGroupId = personGroupId;
    }

    public Integer getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(Integer personGroupId) {
        this.personGroupId = personGroupId;
    }

    public GroupName getGroupNameId() {
        return groupNameId;
    }

    public void setGroupNameId(GroupName groupNameId) {
        this.groupNameId = groupNameId;
    }

    public Person getPersonId() {
        return personId;
    }

    public void setPersonId(Person personId) {
        this.personId = personId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (personGroupId != null ? personGroupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersonGroup)) {
            return false;
        }
        PersonGroup other = (PersonGroup) object;
        if ((this.personGroupId == null && other.personGroupId != null) || (this.personGroupId != null && !this.personGroupId.equals(other.personGroupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.PersonGroup[ personGroupId=" + personGroupId + " ]";
    }
    
}
