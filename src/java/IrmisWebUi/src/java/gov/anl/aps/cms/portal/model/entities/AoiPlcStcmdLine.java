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
@Table(name = "aoi_plc_stcmd_line")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiPlcStcmdLine.findAll", query = "SELECT a FROM AoiPlcStcmdLine a"),
    @NamedQuery(name = "AoiPlcStcmdLine.findByAoiPlcStcmdLineId", query = "SELECT a FROM AoiPlcStcmdLine a WHERE a.aoiPlcStcmdLineId = :aoiPlcStcmdLineId"),
    @NamedQuery(name = "AoiPlcStcmdLine.findByPlcId", query = "SELECT a FROM AoiPlcStcmdLine a WHERE a.plcId = :plcId"),
    @NamedQuery(name = "AoiPlcStcmdLine.findByIocStcmdLineId", query = "SELECT a FROM AoiPlcStcmdLine a WHERE a.iocStcmdLineId = :iocStcmdLineId")})
public class AoiPlcStcmdLine implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_plc_stcmd_line_id")
    private Integer aoiPlcStcmdLineId;
    @Column(name = "plc_id")
    private Integer plcId;
    @Column(name = "ioc_stcmd_line_id")
    private Integer iocStcmdLineId;
    @JoinColumn(name = "aoi_id", referencedColumnName = "aoi_id")
    @ManyToOne
    private Aoi aoiId;

    public AoiPlcStcmdLine() {
    }

    public AoiPlcStcmdLine(Integer aoiPlcStcmdLineId) {
        this.aoiPlcStcmdLineId = aoiPlcStcmdLineId;
    }

    public Integer getAoiPlcStcmdLineId() {
        return aoiPlcStcmdLineId;
    }

    public void setAoiPlcStcmdLineId(Integer aoiPlcStcmdLineId) {
        this.aoiPlcStcmdLineId = aoiPlcStcmdLineId;
    }

    public Integer getPlcId() {
        return plcId;
    }

    public void setPlcId(Integer plcId) {
        this.plcId = plcId;
    }

    public Integer getIocStcmdLineId() {
        return iocStcmdLineId;
    }

    public void setIocStcmdLineId(Integer iocStcmdLineId) {
        this.iocStcmdLineId = iocStcmdLineId;
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
        hash += (aoiPlcStcmdLineId != null ? aoiPlcStcmdLineId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiPlcStcmdLine)) {
            return false;
        }
        AoiPlcStcmdLine other = (AoiPlcStcmdLine) object;
        if ((this.aoiPlcStcmdLineId == null && other.aoiPlcStcmdLineId != null) || (this.aoiPlcStcmdLineId != null && !this.aoiPlcStcmdLineId.equals(other.aoiPlcStcmdLineId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiPlcStcmdLine[ aoiPlcStcmdLineId=" + aoiPlcStcmdLineId + " ]";
    }
    
}
