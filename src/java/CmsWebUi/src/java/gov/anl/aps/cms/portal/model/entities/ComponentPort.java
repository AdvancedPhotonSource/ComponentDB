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
@Table(name = "component_port")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentPort.findAll", query = "SELECT c FROM ComponentPort c"),
    @NamedQuery(name = "ComponentPort.findByComponentPortId", query = "SELECT c FROM ComponentPort c WHERE c.componentPortId = :componentPortId"),
    @NamedQuery(name = "ComponentPort.findByComponentPortName", query = "SELECT c FROM ComponentPort c WHERE c.componentPortName = :componentPortName"),
    @NamedQuery(name = "ComponentPort.findByComponentPortOrder", query = "SELECT c FROM ComponentPort c WHERE c.componentPortOrder = :componentPortOrder"),
    @NamedQuery(name = "ComponentPort.findByMarkForDelete", query = "SELECT c FROM ComponentPort c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentPort.findByVersion", query = "SELECT c FROM ComponentPort c WHERE c.version = :version")})
public class ComponentPort implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_port_id")
    private Integer componentPortId;
    @Size(max = 40)
    @Column(name = "component_port_name")
    private String componentPortName;
    @Column(name = "component_port_order")
    private Integer componentPortOrder;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @OneToMany(mappedBy = "componentPortId")
    private List<PortPin> portPinList;
    @JoinColumn(name = "component_id", referencedColumnName = "component_id")
    @ManyToOne
    private Component componentId;
    @JoinColumn(name = "component_port_type_id", referencedColumnName = "component_port_type_id")
    @ManyToOne
    private ComponentPortType componentPortTypeId;
    @OneToMany(mappedBy = "componentPortBId")
    private List<Cable> cableList;
    @OneToMany(mappedBy = "componentPortAId")
    private List<Cable> cableList1;

    public ComponentPort() {
    }

    public ComponentPort(Integer componentPortId) {
        this.componentPortId = componentPortId;
    }

    public Integer getComponentPortId() {
        return componentPortId;
    }

    public void setComponentPortId(Integer componentPortId) {
        this.componentPortId = componentPortId;
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
    public List<PortPin> getPortPinList() {
        return portPinList;
    }

    public void setPortPinList(List<PortPin> portPinList) {
        this.portPinList = portPinList;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
    }

    public ComponentPortType getComponentPortTypeId() {
        return componentPortTypeId;
    }

    public void setComponentPortTypeId(ComponentPortType componentPortTypeId) {
        this.componentPortTypeId = componentPortTypeId;
    }

    @XmlTransient
    public List<Cable> getCableList() {
        return cableList;
    }

    public void setCableList(List<Cable> cableList) {
        this.cableList = cableList;
    }

    @XmlTransient
    public List<Cable> getCableList1() {
        return cableList1;
    }

    public void setCableList1(List<Cable> cableList1) {
        this.cableList1 = cableList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentPortId != null ? componentPortId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentPort)) {
            return false;
        }
        ComponentPort other = (ComponentPort) object;
        if ((this.componentPortId == null && other.componentPortId != null) || (this.componentPortId != null && !this.componentPortId.equals(other.componentPortId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentPort[ componentPortId=" + componentPortId + " ]";
    }
    
}
