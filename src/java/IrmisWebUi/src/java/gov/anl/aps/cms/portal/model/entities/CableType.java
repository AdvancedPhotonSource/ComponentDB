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
@Table(name = "cable_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CableType.findAll", query = "SELECT c FROM CableType c"),
    @NamedQuery(name = "CableType.findByCableTypeId", query = "SELECT c FROM CableType c WHERE c.cableTypeId = :cableTypeId"),
    @NamedQuery(name = "CableType.findByCableType", query = "SELECT c FROM CableType c WHERE c.cableType = :cableType"),
    @NamedQuery(name = "CableType.findByCableTypeDescription", query = "SELECT c FROM CableType c WHERE c.cableTypeDescription = :cableTypeDescription"),
    @NamedQuery(name = "CableType.findByCableDiameter", query = "SELECT c FROM CableType c WHERE c.cableDiameter = :cableDiameter")})
public class CableType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cable_type_id")
    private Integer cableTypeId;
    @Size(max = 50)
    @Column(name = "cable_type")
    private String cableType;
    @Size(max = 255)
    @Column(name = "cable_type_description")
    private String cableTypeDescription;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cable_diameter")
    private Float cableDiameter;

    public CableType() {
    }

    public CableType(Integer cableTypeId) {
        this.cableTypeId = cableTypeId;
    }

    public Integer getCableTypeId() {
        return cableTypeId;
    }

    public void setCableTypeId(Integer cableTypeId) {
        this.cableTypeId = cableTypeId;
    }

    public String getCableType() {
        return cableType;
    }

    public void setCableType(String cableType) {
        this.cableType = cableType;
    }

    public String getCableTypeDescription() {
        return cableTypeDescription;
    }

    public void setCableTypeDescription(String cableTypeDescription) {
        this.cableTypeDescription = cableTypeDescription;
    }

    public Float getCableDiameter() {
        return cableDiameter;
    }

    public void setCableDiameter(Float cableDiameter) {
        this.cableDiameter = cableDiameter;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cableTypeId != null ? cableTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CableType)) {
            return false;
        }
        CableType other = (CableType) object;
        if ((this.cableTypeId == null && other.cableTypeId != null) || (this.cableTypeId != null && !this.cableTypeId.equals(other.cableTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.CableType[ cableTypeId=" + cableTypeId + " ]";
    }
    
}
