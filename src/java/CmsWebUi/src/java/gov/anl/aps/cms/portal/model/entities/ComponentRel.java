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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_rel")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentRel.findAll", query = "SELECT c FROM ComponentRel c"),
    @NamedQuery(name = "ComponentRel.findByComponentRelId", query = "SELECT c FROM ComponentRel c WHERE c.componentRelId = :componentRelId"),
    @NamedQuery(name = "ComponentRel.findByLogicalOrder", query = "SELECT c FROM ComponentRel c WHERE c.logicalOrder = :logicalOrder"),
    @NamedQuery(name = "ComponentRel.findByLogicalDesc", query = "SELECT c FROM ComponentRel c WHERE c.logicalDesc = :logicalDesc"),
    @NamedQuery(name = "ComponentRel.findByMarkForDelete", query = "SELECT c FROM ComponentRel c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentRel.findByVersion", query = "SELECT c FROM ComponentRel c WHERE c.version = :version"),
    @NamedQuery(name = "ComponentRel.findByVerifiedPersonId", query = "SELECT c FROM ComponentRel c WHERE c.verifiedPersonId = :verifiedPersonId")})
public class ComponentRel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_rel_id")
    private Integer componentRelId;
    @Column(name = "logical_order")
    private Integer logicalOrder;
    @Size(max = 60)
    @Column(name = "logical_desc")
    private String logicalDesc;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @Column(name = "verified_person_id")
    private Integer verifiedPersonId;
    @JoinColumn(name = "component_rel_type_id", referencedColumnName = "component_rel_type_id")
    @ManyToOne
    private ComponentRelType componentRelTypeId;
    @JoinColumn(name = "child_component_id", referencedColumnName = "component_id")
    @ManyToOne
    private Component childComponentId;
    @JoinColumn(name = "parent_component_id", referencedColumnName = "component_id")
    @ManyToOne
    private Component parentComponentId;

    public ComponentRel() {
    }

    public ComponentRel(Integer componentRelId) {
        this.componentRelId = componentRelId;
    }

    public Integer getComponentRelId() {
        return componentRelId;
    }

    public void setComponentRelId(Integer componentRelId) {
        this.componentRelId = componentRelId;
    }

    public Integer getLogicalOrder() {
        return logicalOrder;
    }

    public void setLogicalOrder(Integer logicalOrder) {
        this.logicalOrder = logicalOrder;
    }

    public String getLogicalDesc() {
        return logicalDesc;
    }

    public void setLogicalDesc(String logicalDesc) {
        this.logicalDesc = logicalDesc;
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

    public Integer getVerifiedPersonId() {
        return verifiedPersonId;
    }

    public void setVerifiedPersonId(Integer verifiedPersonId) {
        this.verifiedPersonId = verifiedPersonId;
    }

    public ComponentRelType getComponentRelTypeId() {
        return componentRelTypeId;
    }

    public void setComponentRelTypeId(ComponentRelType componentRelTypeId) {
        this.componentRelTypeId = componentRelTypeId;
    }

    public Component getChildComponentId() {
        return childComponentId;
    }

    public void setChildComponentId(Component childComponentId) {
        this.childComponentId = childComponentId;
    }

    public Component getParentComponentId() {
        return parentComponentId;
    }

    public void setParentComponentId(Component parentComponentId) {
        this.parentComponentId = parentComponentId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentRelId != null ? componentRelId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentRel)) {
            return false;
        }
        ComponentRel other = (ComponentRel) object;
        if ((this.componentRelId == null && other.componentRelId != null) || (this.componentRelId != null && !this.componentRelId.equals(other.componentRelId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentRel[ componentRelId=" + componentRelId + " ]";
    }
    
}
