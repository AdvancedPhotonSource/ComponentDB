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
@Table(name = "rec_alias_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecAliasHistory.findAll", query = "SELECT r FROM RecAliasHistory r"),
    @NamedQuery(name = "RecAliasHistory.findByRecAliasHistoryId", query = "SELECT r FROM RecAliasHistory r WHERE r.recAliasHistoryId = :recAliasHistoryId"),
    @NamedQuery(name = "RecAliasHistory.findByAliasNm", query = "SELECT r FROM RecAliasHistory r WHERE r.aliasNm = :aliasNm"),
    @NamedQuery(name = "RecAliasHistory.findByIocResourceHistoryId", query = "SELECT r FROM RecAliasHistory r WHERE r.iocResourceHistoryId = :iocResourceHistoryId")})
public class RecAliasHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_alias_history_id")
    private Integer recAliasHistoryId;
    @Size(max = 128)
    @Column(name = "alias_nm")
    private String aliasNm;
    @Column(name = "ioc_resource_history_id")
    private Integer iocResourceHistoryId;
    @JoinColumn(name = "rec_history_id", referencedColumnName = "rec_history_id")
    @ManyToOne
    private RecHistory recHistoryId;

    public RecAliasHistory() {
    }

    public RecAliasHistory(Integer recAliasHistoryId) {
        this.recAliasHistoryId = recAliasHistoryId;
    }

    public Integer getRecAliasHistoryId() {
        return recAliasHistoryId;
    }

    public void setRecAliasHistoryId(Integer recAliasHistoryId) {
        this.recAliasHistoryId = recAliasHistoryId;
    }

    public String getAliasNm() {
        return aliasNm;
    }

    public void setAliasNm(String aliasNm) {
        this.aliasNm = aliasNm;
    }

    public Integer getIocResourceHistoryId() {
        return iocResourceHistoryId;
    }

    public void setIocResourceHistoryId(Integer iocResourceHistoryId) {
        this.iocResourceHistoryId = iocResourceHistoryId;
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
        hash += (recAliasHistoryId != null ? recAliasHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecAliasHistory)) {
            return false;
        }
        RecAliasHistory other = (RecAliasHistory) object;
        if ((this.recAliasHistoryId == null && other.recAliasHistoryId != null) || (this.recAliasHistoryId != null && !this.recAliasHistoryId.equals(other.recAliasHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RecAliasHistory[ recAliasHistoryId=" + recAliasHistoryId + " ]";
    }
    
}
