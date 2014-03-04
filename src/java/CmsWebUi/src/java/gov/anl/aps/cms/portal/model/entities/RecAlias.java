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
@Table(name = "rec_alias")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecAlias.findAll", query = "SELECT r FROM RecAlias r"),
    @NamedQuery(name = "RecAlias.findByRecAliasId", query = "SELECT r FROM RecAlias r WHERE r.recAliasId = :recAliasId"),
    @NamedQuery(name = "RecAlias.findByAliasNm", query = "SELECT r FROM RecAlias r WHERE r.aliasNm = :aliasNm"),
    @NamedQuery(name = "RecAlias.findByIocResourceId", query = "SELECT r FROM RecAlias r WHERE r.iocResourceId = :iocResourceId")})
public class RecAlias implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_alias_id")
    private Integer recAliasId;
    @Size(max = 128)
    @Column(name = "alias_nm")
    private String aliasNm;
    @Column(name = "ioc_resource_id")
    private Integer iocResourceId;
    @JoinColumn(name = "rec_id", referencedColumnName = "rec_id")
    @ManyToOne
    private Rec recId;

    public RecAlias() {
    }

    public RecAlias(Integer recAliasId) {
        this.recAliasId = recAliasId;
    }

    public Integer getRecAliasId() {
        return recAliasId;
    }

    public void setRecAliasId(Integer recAliasId) {
        this.recAliasId = recAliasId;
    }

    public String getAliasNm() {
        return aliasNm;
    }

    public void setAliasNm(String aliasNm) {
        this.aliasNm = aliasNm;
    }

    public Integer getIocResourceId() {
        return iocResourceId;
    }

    public void setIocResourceId(Integer iocResourceId) {
        this.iocResourceId = iocResourceId;
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
        hash += (recAliasId != null ? recAliasId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecAlias)) {
            return false;
        }
        RecAlias other = (RecAlias) object;
        if ((this.recAliasId == null && other.recAliasId != null) || (this.recAliasId != null && !this.recAliasId.equals(other.recAliasId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RecAlias[ recAliasId=" + recAliasId + " ]";
    }
    
}
