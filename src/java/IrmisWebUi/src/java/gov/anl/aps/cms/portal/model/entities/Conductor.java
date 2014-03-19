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
@Table(name = "conductor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Conductor.findAll", query = "SELECT c FROM Conductor c"),
    @NamedQuery(name = "Conductor.findByConductorId", query = "SELECT c FROM Conductor c WHERE c.conductorId = :conductorId"),
    @NamedQuery(name = "Conductor.findByMarkForDelete", query = "SELECT c FROM Conductor c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "Conductor.findByVersion", query = "SELECT c FROM Conductor c WHERE c.version = :version")})
public class Conductor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "conductor_id")
    private Integer conductorId;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "port_pin_b_id", referencedColumnName = "port_pin_id")
    @ManyToOne
    private PortPin portPinBId;
    @JoinColumn(name = "port_pin_a_id", referencedColumnName = "port_pin_id")
    @ManyToOne
    private PortPin portPinAId;
    @JoinColumn(name = "cable_id", referencedColumnName = "cable_id")
    @ManyToOne
    private Cable cableId;

    public Conductor() {
    }

    public Conductor(Integer conductorId) {
        this.conductorId = conductorId;
    }

    public Integer getConductorId() {
        return conductorId;
    }

    public void setConductorId(Integer conductorId) {
        this.conductorId = conductorId;
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

    public PortPin getPortPinBId() {
        return portPinBId;
    }

    public void setPortPinBId(PortPin portPinBId) {
        this.portPinBId = portPinBId;
    }

    public PortPin getPortPinAId() {
        return portPinAId;
    }

    public void setPortPinAId(PortPin portPinAId) {
        this.portPinAId = portPinAId;
    }

    public Cable getCableId() {
        return cableId;
    }

    public void setCableId(Cable cableId) {
        this.cableId = cableId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conductorId != null ? conductorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Conductor)) {
            return false;
        }
        Conductor other = (Conductor) object;
        if ((this.conductorId == null && other.conductorId != null) || (this.conductorId != null && !this.conductorId.equals(other.conductorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Conductor[ conductorId=" + conductorId + " ]";
    }
    
}
