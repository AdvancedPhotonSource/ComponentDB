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
@Table(name = "aoi_techsys")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiTechsys.findAll", query = "SELECT a FROM AoiTechsys a"),
    @NamedQuery(name = "AoiTechsys.findByAoiTechsystemId", query = "SELECT a FROM AoiTechsys a WHERE a.aoiTechsystemId = :aoiTechsystemId"),
    @NamedQuery(name = "AoiTechsys.findByTechnicalSystemId", query = "SELECT a FROM AoiTechsys a WHERE a.technicalSystemId = :technicalSystemId")})
public class AoiTechsys implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_techsystem_id")
    private Integer aoiTechsystemId;
    @Column(name = "technical_system_id")
    private Integer technicalSystemId;
    @JoinColumn(name = "aoi_id", referencedColumnName = "aoi_id")
    @ManyToOne
    private Aoi aoiId;

    public AoiTechsys() {
    }

    public AoiTechsys(Integer aoiTechsystemId) {
        this.aoiTechsystemId = aoiTechsystemId;
    }

    public Integer getAoiTechsystemId() {
        return aoiTechsystemId;
    }

    public void setAoiTechsystemId(Integer aoiTechsystemId) {
        this.aoiTechsystemId = aoiTechsystemId;
    }

    public Integer getTechnicalSystemId() {
        return technicalSystemId;
    }

    public void setTechnicalSystemId(Integer technicalSystemId) {
        this.technicalSystemId = technicalSystemId;
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
        hash += (aoiTechsystemId != null ? aoiTechsystemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiTechsys)) {
            return false;
        }
        AoiTechsys other = (AoiTechsys) object;
        if ((this.aoiTechsystemId == null && other.aoiTechsystemId != null) || (this.aoiTechsystemId != null && !this.aoiTechsystemId.equals(other.aoiTechsystemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiTechsys[ aoiTechsystemId=" + aoiTechsystemId + " ]";
    }
    
}
