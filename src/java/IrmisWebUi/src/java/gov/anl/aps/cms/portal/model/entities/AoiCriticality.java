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
@Table(name = "aoi_criticality")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiCriticality.findAll", query = "SELECT a FROM AoiCriticality a"),
    @NamedQuery(name = "AoiCriticality.findByAoiCriticalityId", query = "SELECT a FROM AoiCriticality a WHERE a.aoiCriticalityId = :aoiCriticalityId")})
public class AoiCriticality implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_criticality_id")
    private Integer aoiCriticalityId;
    @JoinColumn(name = "criticality_id", referencedColumnName = "criticality_id")
    @ManyToOne
    private CriticalityType criticalityId;
    @JoinColumn(name = "aoi_id", referencedColumnName = "aoi_id")
    @ManyToOne
    private Aoi aoiId;

    public AoiCriticality() {
    }

    public AoiCriticality(Integer aoiCriticalityId) {
        this.aoiCriticalityId = aoiCriticalityId;
    }

    public Integer getAoiCriticalityId() {
        return aoiCriticalityId;
    }

    public void setAoiCriticalityId(Integer aoiCriticalityId) {
        this.aoiCriticalityId = aoiCriticalityId;
    }

    public CriticalityType getCriticalityId() {
        return criticalityId;
    }

    public void setCriticalityId(CriticalityType criticalityId) {
        this.criticalityId = criticalityId;
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
        hash += (aoiCriticalityId != null ? aoiCriticalityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiCriticality)) {
            return false;
        }
        AoiCriticality other = (AoiCriticality) object;
        if ((this.aoiCriticalityId == null && other.aoiCriticalityId != null) || (this.aoiCriticalityId != null && !this.aoiCriticalityId.equals(other.aoiCriticalityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiCriticality[ aoiCriticalityId=" + aoiCriticalityId + " ]";
    }
    
}
