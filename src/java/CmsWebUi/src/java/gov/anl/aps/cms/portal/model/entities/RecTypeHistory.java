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
@Table(name = "rec_type_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecTypeHistory.findAll", query = "SELECT r FROM RecTypeHistory r"),
    @NamedQuery(name = "RecTypeHistory.findByRecTypeHistoryId", query = "SELECT r FROM RecTypeHistory r WHERE r.recTypeHistoryId = :recTypeHistoryId"),
    @NamedQuery(name = "RecTypeHistory.findByRecType", query = "SELECT r FROM RecTypeHistory r WHERE r.recType = :recType")})
public class RecTypeHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_type_history_id")
    private Integer recTypeHistoryId;
    @Size(max = 24)
    @Column(name = "rec_type")
    private String recType;
    @OneToMany(mappedBy = "recTypeHistoryId")
    private List<RecHistory> recHistoryList;
    @JoinColumn(name = "ioc_resource_history_id", referencedColumnName = "ioc_resource_history_id")
    @ManyToOne
    private IocResourceHistory iocResourceHistoryId;
    @JoinColumn(name = "ioc_boot_id", referencedColumnName = "ioc_boot_id")
    @ManyToOne
    private IocBoot iocBootId;
    @OneToMany(mappedBy = "recTypeHistoryId")
    private List<RecTypeDevSupHistory> recTypeDevSupHistoryList;
    @OneToMany(mappedBy = "recTypeHistoryId")
    private List<FldTypeHistory> fldTypeHistoryList;

    public RecTypeHistory() {
    }

    public RecTypeHistory(Integer recTypeHistoryId) {
        this.recTypeHistoryId = recTypeHistoryId;
    }

    public Integer getRecTypeHistoryId() {
        return recTypeHistoryId;
    }

    public void setRecTypeHistoryId(Integer recTypeHistoryId) {
        this.recTypeHistoryId = recTypeHistoryId;
    }

    public String getRecType() {
        return recType;
    }

    public void setRecType(String recType) {
        this.recType = recType;
    }

    @XmlTransient
    public List<RecHistory> getRecHistoryList() {
        return recHistoryList;
    }

    public void setRecHistoryList(List<RecHistory> recHistoryList) {
        this.recHistoryList = recHistoryList;
    }

    public IocResourceHistory getIocResourceHistoryId() {
        return iocResourceHistoryId;
    }

    public void setIocResourceHistoryId(IocResourceHistory iocResourceHistoryId) {
        this.iocResourceHistoryId = iocResourceHistoryId;
    }

    public IocBoot getIocBootId() {
        return iocBootId;
    }

    public void setIocBootId(IocBoot iocBootId) {
        this.iocBootId = iocBootId;
    }

    @XmlTransient
    public List<RecTypeDevSupHistory> getRecTypeDevSupHistoryList() {
        return recTypeDevSupHistoryList;
    }

    public void setRecTypeDevSupHistoryList(List<RecTypeDevSupHistory> recTypeDevSupHistoryList) {
        this.recTypeDevSupHistoryList = recTypeDevSupHistoryList;
    }

    @XmlTransient
    public List<FldTypeHistory> getFldTypeHistoryList() {
        return fldTypeHistoryList;
    }

    public void setFldTypeHistoryList(List<FldTypeHistory> fldTypeHistoryList) {
        this.fldTypeHistoryList = fldTypeHistoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recTypeHistoryId != null ? recTypeHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecTypeHistory)) {
            return false;
        }
        RecTypeHistory other = (RecTypeHistory) object;
        if ((this.recTypeHistoryId == null && other.recTypeHistoryId != null) || (this.recTypeHistoryId != null && !this.recTypeHistoryId.equals(other.recTypeHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RecTypeHistory[ recTypeHistoryId=" + recTypeHistoryId + " ]";
    }
    
}
