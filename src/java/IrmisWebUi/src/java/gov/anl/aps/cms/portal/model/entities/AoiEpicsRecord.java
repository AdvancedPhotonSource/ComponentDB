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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "aoi_epics_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiEpicsRecord.findAll", query = "SELECT a FROM AoiEpicsRecord a"),
    @NamedQuery(name = "AoiEpicsRecord.findByAoiEpicsRecordId", query = "SELECT a FROM AoiEpicsRecord a WHERE a.aoiEpicsRecordId = :aoiEpicsRecordId"),
    @NamedQuery(name = "AoiEpicsRecord.findByRecNm", query = "SELECT a FROM AoiEpicsRecord a WHERE a.recNm = :recNm")})
public class AoiEpicsRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_epics_record_id")
    private Integer aoiEpicsRecordId;
    @Size(max = 128)
    @Column(name = "rec_nm")
    private String recNm;
    @JoinColumn(name = "aoi_ioc_stcmd_line_id", referencedColumnName = "aoi_ioc_stcmd_line_id")
    @ManyToOne
    private AoiIocStcmdLine aoiIocStcmdLineId;

    public AoiEpicsRecord() {
    }

    public AoiEpicsRecord(Integer aoiEpicsRecordId) {
        this.aoiEpicsRecordId = aoiEpicsRecordId;
    }

    public Integer getAoiEpicsRecordId() {
        return aoiEpicsRecordId;
    }

    public void setAoiEpicsRecordId(Integer aoiEpicsRecordId) {
        this.aoiEpicsRecordId = aoiEpicsRecordId;
    }

    public String getRecNm() {
        return recNm;
    }

    public void setRecNm(String recNm) {
        this.recNm = recNm;
    }

    public AoiIocStcmdLine getAoiIocStcmdLineId() {
        return aoiIocStcmdLineId;
    }

    public void setAoiIocStcmdLineId(AoiIocStcmdLine aoiIocStcmdLineId) {
        this.aoiIocStcmdLineId = aoiIocStcmdLineId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aoiEpicsRecordId != null ? aoiEpicsRecordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiEpicsRecord)) {
            return false;
        }
        AoiEpicsRecord other = (AoiEpicsRecord) object;
        if ((this.aoiEpicsRecordId == null && other.aoiEpicsRecordId != null) || (this.aoiEpicsRecordId != null && !this.aoiEpicsRecordId.equals(other.aoiEpicsRecordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiEpicsRecord[ aoiEpicsRecordId=" + aoiEpicsRecordId + " ]";
    }
    
}
