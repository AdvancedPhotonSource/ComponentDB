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
@Table(name = "fld_type_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FldTypeHistory.findAll", query = "SELECT f FROM FldTypeHistory f"),
    @NamedQuery(name = "FldTypeHistory.findByFldTypeHistoryId", query = "SELECT f FROM FldTypeHistory f WHERE f.fldTypeHistoryId = :fldTypeHistoryId"),
    @NamedQuery(name = "FldTypeHistory.findByFldType", query = "SELECT f FROM FldTypeHistory f WHERE f.fldType = :fldType"),
    @NamedQuery(name = "FldTypeHistory.findByDbdType", query = "SELECT f FROM FldTypeHistory f WHERE f.dbdType = :dbdType"),
    @NamedQuery(name = "FldTypeHistory.findByDefFldVal", query = "SELECT f FROM FldTypeHistory f WHERE f.defFldVal = :defFldVal")})
public class FldTypeHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "fld_type_history_id")
    private Integer fldTypeHistoryId;
    @Size(max = 24)
    @Column(name = "fld_type")
    private String fldType;
    @Size(max = 24)
    @Column(name = "dbd_type")
    private String dbdType;
    @Size(max = 128)
    @Column(name = "def_fld_val")
    private String defFldVal;
    @OneToMany(mappedBy = "fldTypeHistoryId")
    private List<FldHistory> fldHistoryList;
    @JoinColumn(name = "rec_type_history_id", referencedColumnName = "rec_type_history_id")
    @ManyToOne
    private RecTypeHistory recTypeHistoryId;

    public FldTypeHistory() {
    }

    public FldTypeHistory(Integer fldTypeHistoryId) {
        this.fldTypeHistoryId = fldTypeHistoryId;
    }

    public Integer getFldTypeHistoryId() {
        return fldTypeHistoryId;
    }

    public void setFldTypeHistoryId(Integer fldTypeHistoryId) {
        this.fldTypeHistoryId = fldTypeHistoryId;
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
    public List<FldHistory> getFldHistoryList() {
        return fldHistoryList;
    }

    public void setFldHistoryList(List<FldHistory> fldHistoryList) {
        this.fldHistoryList = fldHistoryList;
    }

    public RecTypeHistory getRecTypeHistoryId() {
        return recTypeHistoryId;
    }

    public void setRecTypeHistoryId(RecTypeHistory recTypeHistoryId) {
        this.recTypeHistoryId = recTypeHistoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fldTypeHistoryId != null ? fldTypeHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FldTypeHistory)) {
            return false;
        }
        FldTypeHistory other = (FldTypeHistory) object;
        if ((this.fldTypeHistoryId == null && other.fldTypeHistoryId != null) || (this.fldTypeHistoryId != null && !this.fldTypeHistoryId.equals(other.fldTypeHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.FldTypeHistory[ fldTypeHistoryId=" + fldTypeHistoryId + " ]";
    }
    
}
