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
import javax.persistence.Lob;
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
@Table(name = "plc_old")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PlcOld.findAll", query = "SELECT p FROM PlcOld p"),
    @NamedQuery(name = "PlcOld.findByPlcId", query = "SELECT p FROM PlcOld p WHERE p.plcId = :plcId"),
    @NamedQuery(name = "PlcOld.findByPlcName", query = "SELECT p FROM PlcOld p WHERE p.plcName = :plcName")})
public class PlcOld implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "plc_id")
    private Integer plcId;
    @Size(max = 60)
    @Column(name = "plc_name")
    private String plcName;
    @Lob
    @Size(max = 65535)
    @Column(name = "plc_description")
    private String plcDescription;

    public PlcOld() {
    }

    public PlcOld(Integer plcId) {
        this.plcId = plcId;
    }

    public Integer getPlcId() {
        return plcId;
    }

    public void setPlcId(Integer plcId) {
        this.plcId = plcId;
    }

    public String getPlcName() {
        return plcName;
    }

    public void setPlcName(String plcName) {
        this.plcName = plcName;
    }

    public String getPlcDescription() {
        return plcDescription;
    }

    public void setPlcDescription(String plcDescription) {
        this.plcDescription = plcDescription;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (plcId != null ? plcId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlcOld)) {
            return false;
        }
        PlcOld other = (PlcOld) object;
        if ((this.plcId == null && other.plcId != null) || (this.plcId != null && !this.plcId.equals(other.plcId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.PlcOld[ plcId=" + plcId + " ]";
    }
    
}
