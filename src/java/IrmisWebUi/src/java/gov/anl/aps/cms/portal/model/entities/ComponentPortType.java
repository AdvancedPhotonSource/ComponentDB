/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_port_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentPortType.findAll", query = "SELECT c FROM ComponentPortType c"),
    @NamedQuery(name = "ComponentPortType.findByComponentPortTypeId", query = "SELECT c FROM ComponentPortType c WHERE c.componentPortTypeId = :componentPortTypeId"),
    @NamedQuery(name = "ComponentPortType.findByComponentPortType", query = "SELECT c FROM ComponentPortType c WHERE c.componentPortType = :componentPortType"),
    @NamedQuery(name = "ComponentPortType.findByComponentPortGroup", query = "SELECT c FROM ComponentPortType c WHERE c.componentPortGroup = :componentPortGroup"),
    @NamedQuery(name = "ComponentPortType.findByComponentPortPinCount", query = "SELECT c FROM ComponentPortType c WHERE c.componentPortPinCount = :componentPortPinCount"),
    @NamedQuery(name = "ComponentPortType.findByModifiedDate", query = "SELECT c FROM ComponentPortType c WHERE c.modifiedDate = :modifiedDate"),
    @NamedQuery(name = "ComponentPortType.findByModifiedBy", query = "SELECT c FROM ComponentPortType c WHERE c.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "ComponentPortType.findByMarkForDelete", query = "SELECT c FROM ComponentPortType c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentPortType.findByVersion", query = "SELECT c FROM ComponentPortType c WHERE c.version = :version")})
public class ComponentPortType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_port_type_id")
    private Integer componentPortTypeId;
    @Size(max = 60)
    @Column(name = "component_port_type")
    private String componentPortType;
    @Size(max = 60)
    @Column(name = "component_port_group")
    private String componentPortGroup;
    @Column(name = "component_port_pin_count")
    private Integer componentPortPinCount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Size(max = 10)
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @OneToMany(mappedBy = "componentPortTypeId")
    private List<PortPinDesignator> portPinDesignatorList;
    @OneToMany(mappedBy = "componentPortTypeId")
    private List<ComponentPort> componentPortList;
    @OneToMany(mappedBy = "componentPortTypeId")
    private List<ComponentPortTemplate> componentPortTemplateList;

    public ComponentPortType() {
    }

    public ComponentPortType(Integer componentPortTypeId) {
        this.componentPortTypeId = componentPortTypeId;
    }

    public ComponentPortType(Integer componentPortTypeId, Date modifiedDate) {
        this.componentPortTypeId = componentPortTypeId;
        this.modifiedDate = modifiedDate;
    }

    public Integer getComponentPortTypeId() {
        return componentPortTypeId;
    }

    public void setComponentPortTypeId(Integer componentPortTypeId) {
        this.componentPortTypeId = componentPortTypeId;
    }

    public String getComponentPortType() {
        return componentPortType;
    }

    public void setComponentPortType(String componentPortType) {
        this.componentPortType = componentPortType;
    }

    public String getComponentPortGroup() {
        return componentPortGroup;
    }

    public void setComponentPortGroup(String componentPortGroup) {
        this.componentPortGroup = componentPortGroup;
    }

    public Integer getComponentPortPinCount() {
        return componentPortPinCount;
    }

    public void setComponentPortPinCount(Integer componentPortPinCount) {
        this.componentPortPinCount = componentPortPinCount;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    @XmlTransient
    public List<PortPinDesignator> getPortPinDesignatorList() {
        return portPinDesignatorList;
    }

    public void setPortPinDesignatorList(List<PortPinDesignator> portPinDesignatorList) {
        this.portPinDesignatorList = portPinDesignatorList;
    }

    @XmlTransient
    public List<ComponentPort> getComponentPortList() {
        return componentPortList;
    }

    public void setComponentPortList(List<ComponentPort> componentPortList) {
        this.componentPortList = componentPortList;
    }

    @XmlTransient
    public List<ComponentPortTemplate> getComponentPortTemplateList() {
        return componentPortTemplateList;
    }

    public void setComponentPortTemplateList(List<ComponentPortTemplate> componentPortTemplateList) {
        this.componentPortTemplateList = componentPortTemplateList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentPortTypeId != null ? componentPortTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentPortType)) {
            return false;
        }
        ComponentPortType other = (ComponentPortType) object;
        if ((this.componentPortTypeId == null && other.componentPortTypeId != null) || (this.componentPortTypeId != null && !this.componentPortTypeId.equals(other.componentPortTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentPortType[ componentPortTypeId=" + componentPortTypeId + " ]";
    }
    
}
