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
@Table(name = "aoi_ioc_stcmd_line")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiIocStcmdLine.findAll", query = "SELECT a FROM AoiIocStcmdLine a"),
    @NamedQuery(name = "AoiIocStcmdLine.findByAoiIocStcmdLineId", query = "SELECT a FROM AoiIocStcmdLine a WHERE a.aoiIocStcmdLineId = :aoiIocStcmdLineId"),
    @NamedQuery(name = "AoiIocStcmdLine.findByAoiId", query = "SELECT a FROM AoiIocStcmdLine a WHERE a.aoiId = :aoiId"),
    @NamedQuery(name = "AoiIocStcmdLine.findByIocStcmdLineId", query = "SELECT a FROM AoiIocStcmdLine a WHERE a.iocStcmdLineId = :iocStcmdLineId"),
    @NamedQuery(name = "AoiIocStcmdLine.findByPvFilter", query = "SELECT a FROM AoiIocStcmdLine a WHERE a.pvFilter = :pvFilter")})
public class AoiIocStcmdLine implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_ioc_stcmd_line_id")
    private Integer aoiIocStcmdLineId;
    @Column(name = "aoi_id")
    private Integer aoiId;
    @Column(name = "ioc_stcmd_line_id")
    private Integer iocStcmdLineId;
    @Size(max = 128)
    @Column(name = "pv_filter")
    private String pvFilter;
    @OneToMany(mappedBy = "aoiIocStcmdLineId")
    private List<AoiEpicsRecord> aoiEpicsRecordList;

    public AoiIocStcmdLine() {
    }

    public AoiIocStcmdLine(Integer aoiIocStcmdLineId) {
        this.aoiIocStcmdLineId = aoiIocStcmdLineId;
    }

    public Integer getAoiIocStcmdLineId() {
        return aoiIocStcmdLineId;
    }

    public void setAoiIocStcmdLineId(Integer aoiIocStcmdLineId) {
        this.aoiIocStcmdLineId = aoiIocStcmdLineId;
    }

    public Integer getAoiId() {
        return aoiId;
    }

    public void setAoiId(Integer aoiId) {
        this.aoiId = aoiId;
    }

    public Integer getIocStcmdLineId() {
        return iocStcmdLineId;
    }

    public void setIocStcmdLineId(Integer iocStcmdLineId) {
        this.iocStcmdLineId = iocStcmdLineId;
    }

    public String getPvFilter() {
        return pvFilter;
    }

    public void setPvFilter(String pvFilter) {
        this.pvFilter = pvFilter;
    }

    @XmlTransient
    public List<AoiEpicsRecord> getAoiEpicsRecordList() {
        return aoiEpicsRecordList;
    }

    public void setAoiEpicsRecordList(List<AoiEpicsRecord> aoiEpicsRecordList) {
        this.aoiEpicsRecordList = aoiEpicsRecordList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aoiIocStcmdLineId != null ? aoiIocStcmdLineId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiIocStcmdLine)) {
            return false;
        }
        AoiIocStcmdLine other = (AoiIocStcmdLine) object;
        if ((this.aoiIocStcmdLineId == null && other.aoiIocStcmdLineId != null) || (this.aoiIocStcmdLineId != null && !this.aoiIocStcmdLineId.equals(other.aoiIocStcmdLineId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiIocStcmdLine[ aoiIocStcmdLineId=" + aoiIocStcmdLineId + " ]";
    }
    
}
