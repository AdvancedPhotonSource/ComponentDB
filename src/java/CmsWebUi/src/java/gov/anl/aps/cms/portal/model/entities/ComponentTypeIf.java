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
@Table(name = "component_type_if")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentTypeIf.findAll", query = "SELECT c FROM ComponentTypeIf c"),
    @NamedQuery(name = "ComponentTypeIf.findByComponentTypeIfId", query = "SELECT c FROM ComponentTypeIf c WHERE c.componentTypeIfId = :componentTypeIfId"),
    @NamedQuery(name = "ComponentTypeIf.findByRequired", query = "SELECT c FROM ComponentTypeIf c WHERE c.required = :required"),
    @NamedQuery(name = "ComponentTypeIf.findByPresented", query = "SELECT c FROM ComponentTypeIf c WHERE c.presented = :presented"),
    @NamedQuery(name = "ComponentTypeIf.findByMaxChildren", query = "SELECT c FROM ComponentTypeIf c WHERE c.maxChildren = :maxChildren"),
    @NamedQuery(name = "ComponentTypeIf.findByMarkForDelete", query = "SELECT c FROM ComponentTypeIf c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentTypeIf.findByVersion", query = "SELECT c FROM ComponentTypeIf c WHERE c.version = :version")})
public class ComponentTypeIf implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_type_if_id")
    private Integer componentTypeIfId;
    @Column(name = "required")
    private Boolean required;
    @Column(name = "presented")
    private Boolean presented;
    @Column(name = "max_children")
    private Integer maxChildren;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "component_type_if_type_id", referencedColumnName = "component_type_if_type_id")
    @ManyToOne
    private ComponentTypeIfType componentTypeIfTypeId;
    @JoinColumn(name = "component_rel_type_id", referencedColumnName = "component_rel_type_id")
    @ManyToOne
    private ComponentRelType componentRelTypeId;
    @JoinColumn(name = "component_type_id", referencedColumnName = "component_type_id")
    @ManyToOne
    private ComponentType componentTypeId;

    public ComponentTypeIf() {
    }

    public ComponentTypeIf(Integer componentTypeIfId) {
        this.componentTypeIfId = componentTypeIfId;
    }

    public Integer getComponentTypeIfId() {
        return componentTypeIfId;
    }

    public void setComponentTypeIfId(Integer componentTypeIfId) {
        this.componentTypeIfId = componentTypeIfId;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getPresented() {
        return presented;
    }

    public void setPresented(Boolean presented) {
        this.presented = presented;
    }

    public Integer getMaxChildren() {
        return maxChildren;
    }

    public void setMaxChildren(Integer maxChildren) {
        this.maxChildren = maxChildren;
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

    public ComponentTypeIfType getComponentTypeIfTypeId() {
        return componentTypeIfTypeId;
    }

    public void setComponentTypeIfTypeId(ComponentTypeIfType componentTypeIfTypeId) {
        this.componentTypeIfTypeId = componentTypeIfTypeId;
    }

    public ComponentRelType getComponentRelTypeId() {
        return componentRelTypeId;
    }

    public void setComponentRelTypeId(ComponentRelType componentRelTypeId) {
        this.componentRelTypeId = componentRelTypeId;
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
        hash += (componentTypeIfId != null ? componentTypeIfId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentTypeIf)) {
            return false;
        }
        ComponentTypeIf other = (ComponentTypeIf) object;
        if ((this.componentTypeIfId == null && other.componentTypeIfId != null) || (this.componentTypeIfId != null && !this.componentTypeIfId.equals(other.componentTypeIfId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentTypeIf[ componentTypeIfId=" + componentTypeIfId + " ]";
    }
    
}
