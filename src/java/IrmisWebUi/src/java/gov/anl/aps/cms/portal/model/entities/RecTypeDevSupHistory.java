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
@Table(name = "rec_type_dev_sup_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecTypeDevSupHistory.findAll", query = "SELECT r FROM RecTypeDevSupHistory r"),
    @NamedQuery(name = "RecTypeDevSupHistory.findByRecTypeDevSupHistoryId", query = "SELECT r FROM RecTypeDevSupHistory r WHERE r.recTypeDevSupHistoryId = :recTypeDevSupHistoryId"),
    @NamedQuery(name = "RecTypeDevSupHistory.findByDtypStr", query = "SELECT r FROM RecTypeDevSupHistory r WHERE r.dtypStr = :dtypStr"),
    @NamedQuery(name = "RecTypeDevSupHistory.findByDevSupDset", query = "SELECT r FROM RecTypeDevSupHistory r WHERE r.devSupDset = :devSupDset"),
    @NamedQuery(name = "RecTypeDevSupHistory.findByDevSupIoType", query = "SELECT r FROM RecTypeDevSupHistory r WHERE r.devSupIoType = :devSupIoType")})
public class RecTypeDevSupHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_type_dev_sup_history_id")
    private Integer recTypeDevSupHistoryId;
    @Size(max = 50)
    @Column(name = "dtyp_str")
    private String dtypStr;
    @Size(max = 50)
    @Column(name = "dev_sup_dset")
    private String devSupDset;
    @Size(max = 50)
    @Column(name = "dev_sup_io_type")
    private String devSupIoType;
    @JoinColumn(name = "rec_type_history_id", referencedColumnName = "rec_type_history_id")
    @ManyToOne
    private RecTypeHistory recTypeHistoryId;

    public RecTypeDevSupHistory() {
    }

    public RecTypeDevSupHistory(Integer recTypeDevSupHistoryId) {
        this.recTypeDevSupHistoryId = recTypeDevSupHistoryId;
    }

    public Integer getRecTypeDevSupHistoryId() {
        return recTypeDevSupHistoryId;
    }

    public void setRecTypeDevSupHistoryId(Integer recTypeDevSupHistoryId) {
        this.recTypeDevSupHistoryId = recTypeDevSupHistoryId;
    }

    public String getDtypStr() {
        return dtypStr;
    }

    public void setDtypStr(String dtypStr) {
        this.dtypStr = dtypStr;
    }

    public String getDevSupDset() {
        return devSupDset;
    }

    public void setDevSupDset(String devSupDset) {
        this.devSupDset = devSupDset;
    }

    public String getDevSupIoType() {
        return devSupIoType;
    }

    public void setDevSupIoType(String devSupIoType) {
        this.devSupIoType = devSupIoType;
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
        hash += (recTypeDevSupHistoryId != null ? recTypeDevSupHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecTypeDevSupHistory)) {
            return false;
        }
        RecTypeDevSupHistory other = (RecTypeDevSupHistory) object;
        if ((this.recTypeDevSupHistoryId == null && other.recTypeDevSupHistoryId != null) || (this.recTypeDevSupHistoryId != null && !this.recTypeDevSupHistoryId.equals(other.recTypeDevSupHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RecTypeDevSupHistory[ recTypeDevSupHistoryId=" + recTypeDevSupHistoryId + " ]";
    }
    
}
