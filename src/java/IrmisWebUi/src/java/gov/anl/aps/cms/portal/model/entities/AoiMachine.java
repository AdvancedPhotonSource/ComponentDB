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
@Table(name = "aoi_machine")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiMachine.findAll", query = "SELECT a FROM AoiMachine a"),
    @NamedQuery(name = "AoiMachine.findByAoiMachineId", query = "SELECT a FROM AoiMachine a WHERE a.aoiMachineId = :aoiMachineId"),
    @NamedQuery(name = "AoiMachine.findByMachineId", query = "SELECT a FROM AoiMachine a WHERE a.machineId = :machineId")})
public class AoiMachine implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_machine_id")
    private Integer aoiMachineId;
    @Column(name = "machine_id")
    private Integer machineId;
    @JoinColumn(name = "aoi_id", referencedColumnName = "aoi_id")
    @ManyToOne
    private Aoi aoiId;

    public AoiMachine() {
    }

    public AoiMachine(Integer aoiMachineId) {
        this.aoiMachineId = aoiMachineId;
    }

    public Integer getAoiMachineId() {
        return aoiMachineId;
    }

    public void setAoiMachineId(Integer aoiMachineId) {
        this.aoiMachineId = aoiMachineId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public Aoi getAoiId() {
        return aoiId;
    }

    public void setAoiId(Aoi aoiId) {
        this.aoiId = aoiId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aoiMachineId != null ? aoiMachineId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiMachine)) {
            return false;
        }
        AoiMachine other = (AoiMachine) object;
        if ((this.aoiMachineId == null && other.aoiMachineId != null) || (this.aoiMachineId != null && !this.aoiMachineId.equals(other.aoiMachineId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiMachine[ aoiMachineId=" + aoiMachineId + " ]";
    }
    
}
