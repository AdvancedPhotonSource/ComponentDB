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
@Table(name = "fld")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fld.findAll", query = "SELECT f FROM Fld f"),
    @NamedQuery(name = "Fld.findByFldId", query = "SELECT f FROM Fld f WHERE f.fldId = :fldId"),
    @NamedQuery(name = "Fld.findByFldVal", query = "SELECT f FROM Fld f WHERE f.fldVal = :fldVal")})
public class Fld implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "fld_id")
    private Integer fldId;
    @Size(max = 128)
    @Column(name = "fld_val")
    private String fldVal;
    @JoinColumn(name = "ioc_resource_id", referencedColumnName = "ioc_resource_id")
    @ManyToOne
    private IocResource iocResourceId;
    @JoinColumn(name = "fld_type_id", referencedColumnName = "fld_type_id")
    @ManyToOne
    private FldType fldTypeId;
    @JoinColumn(name = "rec_id", referencedColumnName = "rec_id")
    @ManyToOne
    private Rec recId;

    public Fld() {
    }

    public Fld(Integer fldId) {
        this.fldId = fldId;
    }

    public Integer getFldId() {
        return fldId;
    }

    public void setFldId(Integer fldId) {
        this.fldId = fldId;
    }

    public String getFldVal() {
        return fldVal;
    }

    public void setFldVal(String fldVal) {
        this.fldVal = fldVal;
    }

    public IocResource getIocResourceId() {
        return iocResourceId;
    }

    public void setIocResourceId(IocResource iocResourceId) {
        this.iocResourceId = iocResourceId;
    }

    public FldType getFldTypeId() {
        return fldTypeId;
    }

    public void setFldTypeId(FldType fldTypeId) {
        this.fldTypeId = fldTypeId;
    }

    public Rec getRecId() {
        return recId;
    }

    public void setRecId(Rec recId) {
        this.recId = recId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fldId != null ? fldId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Fld)) {
            return false;
        }
        Fld other = (Fld) object;
        if ((this.fldId == null && other.fldId != null) || (this.fldId != null && !this.fldId.equals(other.fldId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Fld[ fldId=" + fldId + " ]";
    }
    
}
