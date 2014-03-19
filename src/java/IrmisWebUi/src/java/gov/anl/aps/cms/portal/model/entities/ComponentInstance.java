/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
@Table(name = "component_instance")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentInstance.findAll", query = "SELECT c FROM ComponentInstance c"),
    @NamedQuery(name = "ComponentInstance.findByComponentInstanceId", query = "SELECT c FROM ComponentInstance c WHERE c.componentInstanceId = :componentInstanceId"),
    @NamedQuery(name = "ComponentInstance.findBySerialNumber", query = "SELECT c FROM ComponentInstance c WHERE c.serialNumber = :serialNumber"),
    @NamedQuery(name = "ComponentInstance.findByVersion", query = "SELECT c FROM ComponentInstance c WHERE c.version = :version")})
public class ComponentInstance implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_instance_id")
    private Integer componentInstanceId;
    @Size(max = 60)
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "version")
    private Integer version;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentInstanceId")
    private List<ComponentInstanceState> componentInstanceStateList;
    @JoinColumn(name = "component_type_id", referencedColumnName = "component_type_id")
    @ManyToOne(optional = false)
    private ComponentType componentTypeId;
    @JoinColumn(name = "component_id", referencedColumnName = "component_id")
    @ManyToOne
    private Component componentId;

    public ComponentInstance() {
    }

    public ComponentInstance(Integer componentInstanceId) {
        this.componentInstanceId = componentInstanceId;
    }

    public Integer getComponentInstanceId() {
        return componentInstanceId;
    }

    public void setComponentInstanceId(Integer componentInstanceId) {
        this.componentInstanceId = componentInstanceId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @XmlTransient
    public List<ComponentInstanceState> getComponentInstanceStateList() {
        return componentInstanceStateList;
    }

    public void setComponentInstanceStateList(List<ComponentInstanceState> componentInstanceStateList) {
        this.componentInstanceStateList = componentInstanceStateList;
    }

    public ComponentType getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(ComponentType componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentInstanceId != null ? componentInstanceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentInstance)) {
            return false;
        }
        ComponentInstance other = (ComponentInstance) object;
        if ((this.componentInstanceId == null && other.componentInstanceId != null) || (this.componentInstanceId != null && !this.componentInstanceId.equals(other.componentInstanceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentInstance[ componentInstanceId=" + componentInstanceId + " ]";
    }
    
}
