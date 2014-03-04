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
@Table(name = "aoi_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiStatus.findAll", query = "SELECT a FROM AoiStatus a"),
    @NamedQuery(name = "AoiStatus.findByAoiStatusId", query = "SELECT a FROM AoiStatus a WHERE a.aoiStatusId = :aoiStatusId"),
    @NamedQuery(name = "AoiStatus.findByAoiStatus", query = "SELECT a FROM AoiStatus a WHERE a.aoiStatus = :aoiStatus")})
public class AoiStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_status_id")
    private Integer aoiStatusId;
    @Size(max = 40)
    @Column(name = "aoi_status")
    private String aoiStatus;
    @OneToMany(mappedBy = "aoiStatusId")
    private List<Aoi> aoiList;

    public AoiStatus() {
    }

    public AoiStatus(Integer aoiStatusId) {
        this.aoiStatusId = aoiStatusId;
    }

    public Integer getAoiStatusId() {
        return aoiStatusId;
    }

    public void setAoiStatusId(Integer aoiStatusId) {
        this.aoiStatusId = aoiStatusId;
    }

    public String getAoiStatus() {
        return aoiStatus;
    }

    public void setAoiStatus(String aoiStatus) {
        this.aoiStatus = aoiStatus;
    }

    @XmlTransient
    public List<Aoi> getAoiList() {
        return aoiList;
    }

    public void setAoiList(List<Aoi> aoiList) {
        this.aoiList = aoiList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aoiStatusId != null ? aoiStatusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiStatus)) {
            return false;
        }
        AoiStatus other = (AoiStatus) object;
        if ((this.aoiStatusId == null && other.aoiStatusId != null) || (this.aoiStatusId != null && !this.aoiStatusId.equals(other.aoiStatusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiStatus[ aoiStatusId=" + aoiStatusId + " ]";
    }
    
}
