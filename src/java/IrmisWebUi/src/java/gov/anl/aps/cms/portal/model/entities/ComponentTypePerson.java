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
@Table(name = "component_type_person")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentTypePerson.findAll", query = "SELECT c FROM ComponentTypePerson c"),
    @NamedQuery(name = "ComponentTypePerson.findByComponentTypePersonId", query = "SELECT c FROM ComponentTypePerson c WHERE c.componentTypePersonId = :componentTypePersonId"),
    @NamedQuery(name = "ComponentTypePerson.findByMarkForDelete", query = "SELECT c FROM ComponentTypePerson c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentTypePerson.findByVersion", query = "SELECT c FROM ComponentTypePerson c WHERE c.version = :version")})
public class ComponentTypePerson implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_type_person_id")
    private Integer componentTypePersonId;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "role_name_id", referencedColumnName = "role_name_id")
    @ManyToOne
    private RoleName roleNameId;
    @JoinColumn(name = "person_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person personId;
    @JoinColumn(name = "component_type_id", referencedColumnName = "component_type_id")
    @ManyToOne
    private ComponentType componentTypeId;

    public ComponentTypePerson() {
    }

    public ComponentTypePerson(Integer componentTypePersonId) {
        this.componentTypePersonId = componentTypePersonId;
    }

    public Integer getComponentTypePersonId() {
        return componentTypePersonId;
    }

    public void setComponentTypePersonId(Integer componentTypePersonId) {
        this.componentTypePersonId = componentTypePersonId;
    }

    public Boolean getMarkForDelete() {
        return markForDelete;
    }

    public void setMarkForDelete(Boolean markForDelete) {
        this.markForDelete = markForDelete;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public RoleName getRoleNameId() {
        return roleNameId;
    }

    public void setRoleNameId(RoleName roleNameId) {
        this.roleNameId = roleNameId;
    }

    public Person getPersonId() {
        return personId;
    }

    public void setPersonId(Person personId) {
        this.personId = personId;
    }

    public ComponentType getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(ComponentType componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentTypePersonId != null ? componentTypePersonId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentTypePerson)) {
            return false;
        }
        ComponentTypePerson other = (ComponentTypePerson) object;
        if ((this.componentTypePersonId == null && other.componentTypePersonId != null) || (this.componentTypePersonId != null && !this.componentTypePersonId.equals(other.componentTypePersonId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentTypePerson[ componentTypePersonId=" + componentTypePersonId + " ]";
    }
    
}
