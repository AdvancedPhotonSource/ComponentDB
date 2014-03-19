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
@Table(name = "component_port_template")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentPortTemplate.findAll", query = "SELECT c FROM ComponentPortTemplate c"),
    @NamedQuery(name = "ComponentPortTemplate.findByComponentPortTemplateId", query = "SELECT c FROM ComponentPortTemplate c WHERE c.componentPortTemplateId = :componentPortTemplateId"),
    @NamedQuery(name = "ComponentPortTemplate.findByComponentPortName", query = "SELECT c FROM ComponentPortTemplate c WHERE c.componentPortName = :componentPortName"),
    @NamedQuery(name = "ComponentPortTemplate.findByComponentPortOrder", query = "SELECT c FROM ComponentPortTemplate c WHERE c.componentPortOrder = :componentPortOrder"),
    @NamedQuery(name = "ComponentPortTemplate.findByMarkForDelete", query = "SELECT c FROM ComponentPortTemplate c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentPortTemplate.findByVersion", query = "SELECT c FROM ComponentPortTemplate c WHERE c.version = :version")})
public class ComponentPortTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_port_template_id")
    private Integer componentPortTemplateId;
    @Size(max = 40)
    @Column(name = "component_port_name")
    private String componentPortName;
    @Column(name = "component_port_order")
    private Integer componentPortOrder;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @OneToMany(mappedBy = "componentPortTemplateId")
    private List<PortPinTemplate> portPinTemplateList;
    @JoinColumn(name = "component_port_type_id", referencedColumnName = "component_port_type_id")
    @ManyToOne
    private ComponentPortType componentPortTypeId;
    @JoinColumn(name = "component_type_id", referencedColumnName = "component_type_id")
    @ManyToOne
    private ComponentType componentTypeId;

    public ComponentPortTemplate() {
    }

    public ComponentPortTemplate(Integer componentPortTemplateId) {
        this.componentPortTemplateId = componentPortTemplateId;
    }

    public Integer getComponentPortTemplateId() {
        return componentPortTemplateId;
    }

    public void setComponentPortTemplateId(Integer componentPortTemplateId) {
        this.componentPortTemplateId = componentPortTemplateId;
    }

    public String getComponentPortName() {
        return componentPortName;
    }

    public void setComponentPortName(String componentPortName) {
        this.componentPortName = componentPortName;
    }

    public Integer getComponentPortOrder() {
        return componentPortOrder;
    }

    public void setComponentPortOrder(Integer componentPortOrder) {
        this.componentPortOrder = componentPortOrder;
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
    public List<PortPinTemplate> getPortPinTemplateList() {
        return portPinTemplateList;
    }

    public void setPortPinTemplateList(List<PortPinTemplate> portPinTemplateList) {
        this.portPinTemplateList = portPinTemplateList;
    }

    public ComponentPortType getComponentPortTypeId() {
        return componentPortTypeId;
    }

    public void setComponentPortTypeId(ComponentPortType componentPortTypeId) {
        this.componentPortTypeId = componentPortTypeId;
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
        hash += (componentPortTemplateId != null ? componentPortTemplateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentPortTemplate)) {
            return false;
        }
        ComponentPortTemplate other = (ComponentPortTemplate) object;
        if ((this.componentPortTemplateId == null && other.componentPortTemplateId != null) || (this.componentPortTemplateId != null && !this.componentPortTemplateId.equals(other.componentPortTemplateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentPortTemplate[ componentPortTemplateId=" + componentPortTemplateId + " ]";
    }
    
}
