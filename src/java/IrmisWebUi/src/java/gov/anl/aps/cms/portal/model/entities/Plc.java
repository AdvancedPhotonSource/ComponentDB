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
@Table(name = "plc")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Plc.findAll", query = "SELECT p FROM Plc p"),
    @NamedQuery(name = "Plc.findByPlcId", query = "SELECT p FROM Plc p WHERE p.plcId = :plcId"),
    @NamedQuery(name = "Plc.findByComponentId", query = "SELECT p FROM Plc p WHERE p.componentId = :componentId")})
public class Plc implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "plc_id")
    private Integer plcId;
    @Column(name = "component_id")
    private Integer componentId;
    @Lob
    @Size(max = 65535)
    @Column(name = "plc_description")
    private String plcDescription;
    @Lob
    @Size(max = 65535)
    @Column(name = "plc_version_pv_name")
    private String plcVersionPvName;

    public Plc() {
    }

    public Plc(Integer plcId) {
        this.plcId = plcId;
    }

    public Integer getPlcId() {
        return plcId;
    }

    public void setPlcId(Integer plcId) {
        this.plcId = plcId;
    }

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }

    public String getPlcDescription() {
        return plcDescription;
    }

    public void setPlcDescription(String plcDescription) {
        this.plcDescription = plcDescription;
    }

    public String getPlcVersionPvName() {
        return plcVersionPvName;
    }

    public void setPlcVersionPvName(String plcVersionPvName) {
        this.plcVersionPvName = plcVersionPvName;
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
        if (!(object instanceof Plc)) {
            return false;
        }
        Plc other = (Plc) object;
        if ((this.plcId == null && other.plcId != null) || (this.plcId != null && !this.plcId.equals(other.plcId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Plc[ plcId=" + plcId + " ]";
    }
    
}
