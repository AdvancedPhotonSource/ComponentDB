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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "rec_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecHistory.findAll", query = "SELECT r FROM RecHistory r"),
    @NamedQuery(name = "RecHistory.findByRecHistoryId", query = "SELECT r FROM RecHistory r WHERE r.recHistoryId = :recHistoryId"),
    @NamedQuery(name = "RecHistory.findByRecNm", query = "SELECT r FROM RecHistory r WHERE r.recNm = :recNm"),
    @NamedQuery(name = "RecHistory.findByRecCriticality", query = "SELECT r FROM RecHistory r WHERE r.recCriticality = :recCriticality")})
public class RecHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_history_id")
    private Integer recHistoryId;
    @Size(max = 128)
    @Column(name = "rec_nm")
    private String recNm;
    @Basic(optional = false)
    @NotNull
    @Column(name = "rec_criticality")
    private int recCriticality;
    @JoinColumn(name = "rec_type_history_id", referencedColumnName = "rec_type_history_id")
    @ManyToOne
    private RecTypeHistory recTypeHistoryId;
    @JoinColumn(name = "ioc_boot_id", referencedColumnName = "ioc_boot_id")
    @ManyToOne
    private IocBoot iocBootId;
    @OneToMany(mappedBy = "recHistoryId")
    private List<RecAliasHistory> recAliasHistoryList;
    @OneToMany(mappedBy = "recHistoryId")
    private List<FldHistory> fldHistoryList;

    public RecHistory() {
    }

    public RecHistory(Integer recHistoryId) {
        this.recHistoryId = recHistoryId;
    }

    public RecHistory(Integer recHistoryId, int recCriticality) {
        this.recHistoryId = recHistoryId;
        this.recCriticality = recCriticality;
    }

    public Integer getRecHistoryId() {
        return recHistoryId;
    }

    public void setRecHistoryId(Integer recHistoryId) {
        this.recHistoryId = recHistoryId;
    }

    public String getRecNm() {
        return recNm;
    }

    public void setRecNm(String recNm) {
        this.recNm = recNm;
    }

    public int getRecCriticality() {
        return recCriticality;
    }

    public void setRecCriticality(int recCriticality) {
        this.recCriticality = recCriticality;
    }

    public RecTypeHistory getRecTypeHistoryId() {
        return recTypeHistoryId;
    }

    public void setRecTypeHistoryId(RecTypeHistory recTypeHistoryId) {
        this.recTypeHistoryId = recTypeHistoryId;
    }

    public IocBoot getIocBootId() {
        return iocBootId;
    }

    public void setIocBootId(IocBoot iocBootId) {
        this.iocBootId = iocBootId;
    }

    @XmlTransient
    public List<RecAliasHistory> getRecAliasHistoryList() {
        return recAliasHistoryList;
    }

    public void setRecAliasHistoryList(List<RecAliasHistory> recAliasHistoryList) {
        this.recAliasHistoryList = recAliasHistoryList;
    }

    @XmlTransient
    public List<FldHistory> getFldHistoryList() {
        return fldHistoryList;
    }

    public void setFldHistoryList(List<FldHistory> fldHistoryList) {
        this.fldHistoryList = fldHistoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recHistoryId != null ? recHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecHistory)) {
            return false;
        }
        RecHistory other = (RecHistory) object;
        if ((this.recHistoryId == null && other.recHistoryId != null) || (this.recHistoryId != null && !this.recHistoryId.equals(other.recHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RecHistory[ recHistoryId=" + recHistoryId + " ]";
    }
    
}
