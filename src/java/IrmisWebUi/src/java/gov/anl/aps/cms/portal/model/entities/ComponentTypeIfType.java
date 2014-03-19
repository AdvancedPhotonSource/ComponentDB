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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_type_if_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentTypeIfType.findAll", query = "SELECT c FROM ComponentTypeIfType c"),
    @NamedQuery(name = "ComponentTypeIfType.findByComponentTypeIfTypeId", query = "SELECT c FROM ComponentTypeIfType c WHERE c.componentTypeIfTypeId = :componentTypeIfTypeId"),
    @NamedQuery(name = "ComponentTypeIfType.findByIfType", query = "SELECT c FROM ComponentTypeIfType c WHERE c.ifType = :ifType"),
    @NamedQuery(name = "ComponentTypeIfType.findByMarkForDelete", query = "SELECT c FROM ComponentTypeIfType c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentTypeIfType.findByVersion", query = "SELECT c FROM ComponentTypeIfType c WHERE c.version = :version")})
public class ComponentTypeIfType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_type_if_type_id")
    private Integer componentTypeIfTypeId;
    @Size(max = 100)
    @Column(name = "if_type")
    private String ifType;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "component_rel_type_id", referencedColumnName = "component_rel_type_id")
    @ManyToOne
    private ComponentRelType componentRelTypeId;
    @OneToMany(mappedBy = "componentTypeIfTypeId")
    private List<ComponentTypeIf> componentTypeIfList;

    public ComponentTypeIfType() {
    }

    public ComponentTypeIfType(Integer componentTypeIfTypeId) {
        this.componentTypeIfTypeId = componentTypeIfTypeId;
    }

    public Integer getComponentTypeIfTypeId() {
        return componentTypeIfTypeId;
    }

    public void setComponentTypeIfTypeId(Integer componentTypeIfTypeId) {
        this.componentTypeIfTypeId = componentTypeIfTypeId;
    }

    public String getIfType() {
        return ifType;
    }

    public void setIfType(String ifType) {
        this.ifType = ifType;
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

    public ComponentRelType getComponentRelTypeId() {
        return componentRelTypeId;
    }

    public void setComponentRelTypeId(ComponentRelType componentRelTypeId) {
        this.componentRelTypeId = componentRelTypeId;
    }

    @XmlTransient
    public List<ComponentTypeIf> getComponentTypeIfList() {
        return componentTypeIfList;
    }

    public void setComponentTypeIfList(List<ComponentTypeIf> componentTypeIfList) {
        this.componentTypeIfList = componentTypeIfList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentTypeIfTypeId != null ? componentTypeIfTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentTypeIfType)) {
            return false;
        }
        ComponentTypeIfType other = (ComponentTypeIfType) object;
        if ((this.componentTypeIfTypeId == null && other.componentTypeIfTypeId != null) || (this.componentTypeIfTypeId != null && !this.componentTypeIfTypeId.equals(other.componentTypeIfTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentTypeIfType[ componentTypeIfTypeId=" + componentTypeIfTypeId + " ]";
    }
    
}
