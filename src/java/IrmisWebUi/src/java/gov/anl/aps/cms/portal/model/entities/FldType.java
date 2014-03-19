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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "fld_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FldType.findAll", query = "SELECT f FROM FldType f"),
    @NamedQuery(name = "FldType.findByFldTypeId", query = "SELECT f FROM FldType f WHERE f.fldTypeId = :fldTypeId"),
    @NamedQuery(name = "FldType.findByFldType", query = "SELECT f FROM FldType f WHERE f.fldType = :fldType"),
    @NamedQuery(name = "FldType.findByDbdType", query = "SELECT f FROM FldType f WHERE f.dbdType = :dbdType"),
    @NamedQuery(name = "FldType.findByDefFldVal", query = "SELECT f FROM FldType f WHERE f.defFldVal = :defFldVal")})
public class FldType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "fld_type_id")
    private Integer fldTypeId;
    @Size(max = 24)
    @Column(name = "fld_type")
    private String fldType;
    @Size(max = 24)
    @Column(name = "dbd_type")
    private String dbdType;
    @Size(max = 128)
    @Column(name = "def_fld_val")
    private String defFldVal;
    @OneToMany(mappedBy = "fldTypeId")
    private List<Fld> fldList;
    @JoinColumn(name = "rec_type_id", referencedColumnName = "rec_type_id")
    @ManyToOne
    private RecType recTypeId;

    public FldType() {
    }

    public FldType(Integer fldTypeId) {
        this.fldTypeId = fldTypeId;
    }

    public Integer getFldTypeId() {
        return fldTypeId;
    }

    public void setFldTypeId(Integer fldTypeId) {
        this.fldTypeId = fldTypeId;
    }

    public String getFldType() {
        return fldType;
    }

    public void setFldType(String fldType) {
        this.fldType = fldType;
    }

    public String getDbdType() {
        return dbdType;
    }

    public void setDbdType(String dbdType) {
        this.dbdType = dbdType;
    }

    public String getDefFldVal() {
        return defFldVal;
    }

    public void setDefFldVal(String defFldVal) {
        this.defFldVal = defFldVal;
    }

    @XmlTransient
    public List<Fld> getFldList() {
        return fldList;
    }

    public void setFldList(List<Fld> fldList) {
        this.fldList = fldList;
    }

    public RecType getRecTypeId() {
        return recTypeId;
    }

    public void setRecTypeId(RecType recTypeId) {
        this.recTypeId = recTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fldTypeId != null ? fldTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FldType)) {
            return false;
        }
        FldType other = (FldType) object;
        if ((this.fldTypeId == null && other.fldTypeId != null) || (this.fldTypeId != null && !this.fldTypeId.equals(other.fldTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.FldType[ fldTypeId=" + fldTypeId + " ]";
    }
    
}
