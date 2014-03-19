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
@Table(name = "rec")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rec.findAll", query = "SELECT r FROM Rec r"),
    @NamedQuery(name = "Rec.findByRecId", query = "SELECT r FROM Rec r WHERE r.recId = :recId"),
    @NamedQuery(name = "Rec.findByRecNm", query = "SELECT r FROM Rec r WHERE r.recNm = :recNm"),
    @NamedQuery(name = "Rec.findByRecCriticality", query = "SELECT r FROM Rec r WHERE r.recCriticality = :recCriticality")})
public class Rec implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_id")
    private Integer recId;
    @Size(max = 128)
    @Column(name = "rec_nm")
    private String recNm;
    @Basic(optional = false)
    @NotNull
    @Column(name = "rec_criticality")
    private int recCriticality;
    @OneToMany(mappedBy = "recId")
    private List<RecAlias> recAliasList;
    @OneToMany(mappedBy = "recId")
    private List<Fld> fldList;
    @JoinColumn(name = "rec_type_id", referencedColumnName = "rec_type_id")
    @ManyToOne
    private RecType recTypeId;
    @JoinColumn(name = "ioc_boot_id", referencedColumnName = "ioc_boot_id")
    @ManyToOne
    private IocBoot iocBootId;

    public Rec() {
    }

    public Rec(Integer recId) {
        this.recId = recId;
    }

    public Rec(Integer recId, int recCriticality) {
        this.recId = recId;
        this.recCriticality = recCriticality;
    }

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
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

    @XmlTransient
    public List<RecAlias> getRecAliasList() {
        return recAliasList;
    }

    public void setRecAliasList(List<RecAlias> recAliasList) {
        this.recAliasList = recAliasList;
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

    public IocBoot getIocBootId() {
        return iocBootId;
    }

    public void setIocBootId(IocBoot iocBootId) {
        this.iocBootId = iocBootId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recId != null ? recId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rec)) {
            return false;
        }
        Rec other = (Rec) object;
        if ((this.recId == null && other.recId != null) || (this.recId != null && !this.recId.equals(other.recId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Rec[ recId=" + recId + " ]";
    }
    
}
