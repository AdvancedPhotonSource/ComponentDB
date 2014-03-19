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
@Table(name = "port_pin")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PortPin.findAll", query = "SELECT p FROM PortPin p"),
    @NamedQuery(name = "PortPin.findByPortPinId", query = "SELECT p FROM PortPin p WHERE p.portPinId = :portPinId"),
    @NamedQuery(name = "PortPin.findByPortPinUsage", query = "SELECT p FROM PortPin p WHERE p.portPinUsage = :portPinUsage"),
    @NamedQuery(name = "PortPin.findBySignalName", query = "SELECT p FROM PortPin p WHERE p.signalName = :signalName"),
    @NamedQuery(name = "PortPin.findByMarkForDelete", query = "SELECT p FROM PortPin p WHERE p.markForDelete = :markForDelete"),
    @NamedQuery(name = "PortPin.findByVersion", query = "SELECT p FROM PortPin p WHERE p.version = :version")})
public class PortPin implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "port_pin_id")
    private Integer portPinId;
    @Size(max = 60)
    @Column(name = "port_pin_usage")
    private String portPinUsage;
    @Size(max = 60)
    @Column(name = "signal_name")
    private String signalName;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "port_pin_designator_id", referencedColumnName = "port_pin_designator_id")
    @ManyToOne
    private PortPinDesignator portPinDesignatorId;
    @JoinColumn(name = "component_port_id", referencedColumnName = "component_port_id")
    @ManyToOne
    private ComponentPort componentPortId;
    @JoinColumn(name = "port_pin_type_id", referencedColumnName = "port_pin_type_id")
    @ManyToOne
    private PortPinType portPinTypeId;
    @OneToMany(mappedBy = "portPinBId")
    private List<Conductor> conductorList;
    @OneToMany(mappedBy = "portPinAId")
    private List<Conductor> conductorList1;

    public PortPin() {
    }

    public PortPin(Integer portPinId) {
        this.portPinId = portPinId;
    }

    public Integer getPortPinId() {
        return portPinId;
    }

    public void setPortPinId(Integer portPinId) {
        this.portPinId = portPinId;
    }

    public String getPortPinUsage() {
        return portPinUsage;
    }

    public void setPortPinUsage(String portPinUsage) {
        this.portPinUsage = portPinUsage;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
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

    public PortPinDesignator getPortPinDesignatorId() {
        return portPinDesignatorId;
    }

    public void setPortPinDesignatorId(PortPinDesignator portPinDesignatorId) {
        this.portPinDesignatorId = portPinDesignatorId;
    }

    public ComponentPort getComponentPortId() {
        return componentPortId;
    }

    public void setComponentPortId(ComponentPort componentPortId) {
        this.componentPortId = componentPortId;
    }

    public PortPinType getPortPinTypeId() {
        return portPinTypeId;
    }

    public void setPortPinTypeId(PortPinType portPinTypeId) {
        this.portPinTypeId = portPinTypeId;
    }

    @XmlTransient
    public List<Conductor> getConductorList() {
        return conductorList;
    }

    public void setConductorList(List<Conductor> conductorList) {
        this.conductorList = conductorList;
    }

    @XmlTransient
    public List<Conductor> getConductorList1() {
        return conductorList1;
    }

    public void setConductorList1(List<Conductor> conductorList1) {
        this.conductorList1 = conductorList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (portPinId != null ? portPinId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PortPin)) {
            return false;
        }
        PortPin other = (PortPin) object;
        if ((this.portPinId == null && other.portPinId != null) || (this.portPinId != null && !this.portPinId.equals(other.portPinId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.PortPin[ portPinId=" + portPinId + " ]";
    }
    
}
