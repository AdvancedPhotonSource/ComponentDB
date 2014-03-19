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
@Table(name = "fld_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FldHistory.findAll", query = "SELECT f FROM FldHistory f"),
    @NamedQuery(name = "FldHistory.findByFldHistoryId", query = "SELECT f FROM FldHistory f WHERE f.fldHistoryId = :fldHistoryId"),
    @NamedQuery(name = "FldHistory.findByFldVal", query = "SELECT f FROM FldHistory f WHERE f.fldVal = :fldVal")})
public class FldHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "fld_history_id")
    private Integer fldHistoryId;
    @Size(max = 128)
    @Column(name = "fld_val")
    private String fldVal;
    @JoinColumn(name = "ioc_resource_history_id", referencedColumnName = "ioc_resource_history_id")
    @ManyToOne
    private IocResourceHistory iocResourceHistoryId;
    @JoinColumn(name = "fld_type_history_id", referencedColumnName = "fld_type_history_id")
    @ManyToOne
    private FldTypeHistory fldTypeHistoryId;
    @JoinColumn(name = "rec_history_id", referencedColumnName = "rec_history_id")
    @ManyToOne
    private RecHistory recHistoryId;

    public FldHistory() {
    }

    public FldHistory(Integer fldHistoryId) {
        this.fldHistoryId = fldHistoryId;
    }

    public Integer getFldHistoryId() {
        return fldHistoryId;
    }

    public void setFldHistoryId(Integer fldHistoryId) {
        this.fldHistoryId = fldHistoryId;
    }

    public String getFldVal() {
        return fldVal;
    }

    public void setFldVal(String fldVal) {
        this.fldVal = fldVal;
    }

    public IocResourceHistory getIocResourceHistoryId() {
        return iocResourceHistoryId;
    }

    public void setIocResourceHistoryId(IocResourceHistory iocResourceHistoryId) {
        this.iocResourceHistoryId = iocResourceHistoryId;
    }

    public FldTypeHistory getFldTypeHistoryId() {
        return fldTypeHistoryId;
    }

    public void setFldTypeHistoryId(FldTypeHistory fldTypeHistoryId) {
        this.fldTypeHistoryId = fldTypeHistoryId;
    }

    public RecHistory getRecHistoryId() {
        return recHistoryId;
    }

    public void setRecHistoryId(RecHistory recHistoryId) {
        this.recHistoryId = recHistoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fldHistoryId != null ? fldHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FldHistory)) {
            return false;
        }
        FldHistory other = (FldHistory) object;
        if ((this.fldHistoryId == null && other.fldHistoryId != null) || (this.fldHistoryId != null && !this.fldHistoryId.equals(other.fldHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.FldHistory[ fldHistoryId=" + fldHistoryId + " ]";
    }
    
}
