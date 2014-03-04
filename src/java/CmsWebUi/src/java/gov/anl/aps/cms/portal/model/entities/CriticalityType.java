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
@Table(name = "criticality_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CriticalityType.findAll", query = "SELECT c FROM CriticalityType c"),
    @NamedQuery(name = "CriticalityType.findByCriticalityId", query = "SELECT c FROM CriticalityType c WHERE c.criticalityId = :criticalityId"),
    @NamedQuery(name = "CriticalityType.findByCriticalityLevel", query = "SELECT c FROM CriticalityType c WHERE c.criticalityLevel = :criticalityLevel"),
    @NamedQuery(name = "CriticalityType.findByCriticalityClassification", query = "SELECT c FROM CriticalityType c WHERE c.criticalityClassification = :criticalityClassification")})
public class CriticalityType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "criticality_id")
    private Integer criticalityId;
    @Column(name = "criticality_level")
    private Integer criticalityLevel;
    @Size(max = 255)
    @Column(name = "criticality_classification")
    private String criticalityClassification;
    @OneToMany(mappedBy = "criticalityId")
    private List<AoiCriticality> aoiCriticalityList;

    public CriticalityType() {
    }

    public CriticalityType(Integer criticalityId) {
        this.criticalityId = criticalityId;
    }

    public Integer getCriticalityId() {
        return criticalityId;
    }

    public void setCriticalityId(Integer criticalityId) {
        this.criticalityId = criticalityId;
    }

    public Integer getCriticalityLevel() {
        return criticalityLevel;
    }

    public void setCriticalityLevel(Integer criticalityLevel) {
        this.criticalityLevel = criticalityLevel;
    }

    public String getCriticalityClassification() {
        return criticalityClassification;
    }

    public void setCriticalityClassification(String criticalityClassification) {
        this.criticalityClassification = criticalityClassification;
    }

    @XmlTransient
    public List<AoiCriticality> getAoiCriticalityList() {
        return aoiCriticalityList;
    }

    public void setAoiCriticalityList(List<AoiCriticality> aoiCriticalityList) {
        this.aoiCriticalityList = aoiCriticalityList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (criticalityId != null ? criticalityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CriticalityType)) {
            return false;
        }
        CriticalityType other = (CriticalityType) object;
        if ((this.criticalityId == null && other.criticalityId != null) || (this.criticalityId != null && !this.criticalityId.equals(other.criticalityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.CriticalityType[ criticalityId=" + criticalityId + " ]";
    }
    
}
