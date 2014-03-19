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
@Table(name = "component_rel_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentRelType.findAll", query = "SELECT c FROM ComponentRelType c"),
    @NamedQuery(name = "ComponentRelType.findByComponentRelTypeId", query = "SELECT c FROM ComponentRelType c WHERE c.componentRelTypeId = :componentRelTypeId"),
    @NamedQuery(name = "ComponentRelType.findByRelName", query = "SELECT c FROM ComponentRelType c WHERE c.relName = :relName")})
public class ComponentRelType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "component_rel_type_id")
    private Integer componentRelTypeId;
    @Size(max = 30)
    @Column(name = "rel_name")
    private String relName;
    @OneToMany(mappedBy = "componentRelTypeId")
    private List<ComponentTypeIfType> componentTypeIfTypeList;
    @OneToMany(mappedBy = "componentRelTypeId")
    private List<ComponentTypeIf> componentTypeIfList;
    @OneToMany(mappedBy = "componentRelTypeId")
    private List<ComponentRel> componentRelList;

    public ComponentRelType() {
    }

    public ComponentRelType(Integer componentRelTypeId) {
        this.componentRelTypeId = componentRelTypeId;
    }

    public Integer getComponentRelTypeId() {
        return componentRelTypeId;
    }

    public void setComponentRelTypeId(Integer componentRelTypeId) {
        this.componentRelTypeId = componentRelTypeId;
    }

    public String getRelName() {
        return relName;
    }

    public void setRelName(String relName) {
        this.relName = relName;
    }

    @XmlTransient
    public List<ComponentTypeIfType> getComponentTypeIfTypeList() {
        return componentTypeIfTypeList;
    }

    public void setComponentTypeIfTypeList(List<ComponentTypeIfType> componentTypeIfTypeList) {
        this.componentTypeIfTypeList = componentTypeIfTypeList;
    }

    @XmlTransient
    public List<ComponentTypeIf> getComponentTypeIfList() {
        return componentTypeIfList;
    }

    public void setComponentTypeIfList(List<ComponentTypeIf> componentTypeIfList) {
        this.componentTypeIfList = componentTypeIfList;
    }

    @XmlTransient
    public List<ComponentRel> getComponentRelList() {
        return componentRelList;
    }

    public void setComponentRelList(List<ComponentRel> componentRelList) {
        this.componentRelList = componentRelList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentRelTypeId != null ? componentRelTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentRelType)) {
            return false;
        }
        ComponentRelType other = (ComponentRelType) object;
        if ((this.componentRelTypeId == null && other.componentRelTypeId != null) || (this.componentRelTypeId != null && !this.componentRelTypeId.equals(other.componentRelTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentRelType[ componentRelTypeId=" + componentRelTypeId + " ]";
    }
    
}
