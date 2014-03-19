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
@Table(name = "ioc_resource_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IocResourceHistory.findAll", query = "SELECT i FROM IocResourceHistory i"),
    @NamedQuery(name = "IocResourceHistory.findByIocResourceHistoryId", query = "SELECT i FROM IocResourceHistory i WHERE i.iocResourceHistoryId = :iocResourceHistoryId"),
    @NamedQuery(name = "IocResourceHistory.findByIocBootId", query = "SELECT i FROM IocResourceHistory i WHERE i.iocBootId = :iocBootId"),
    @NamedQuery(name = "IocResourceHistory.findByTextLine", query = "SELECT i FROM IocResourceHistory i WHERE i.textLine = :textLine"),
    @NamedQuery(name = "IocResourceHistory.findByLoadOrder", query = "SELECT i FROM IocResourceHistory i WHERE i.loadOrder = :loadOrder"),
    @NamedQuery(name = "IocResourceHistory.findByUriHistoryId", query = "SELECT i FROM IocResourceHistory i WHERE i.uriHistoryId = :uriHistoryId"),
    @NamedQuery(name = "IocResourceHistory.findByUnreachable", query = "SELECT i FROM IocResourceHistory i WHERE i.unreachable = :unreachable"),
    @NamedQuery(name = "IocResourceHistory.findBySubstStr", query = "SELECT i FROM IocResourceHistory i WHERE i.substStr = :substStr"),
    @NamedQuery(name = "IocResourceHistory.findByIocResourceTypeId", query = "SELECT i FROM IocResourceHistory i WHERE i.iocResourceTypeId = :iocResourceTypeId")})
public class IocResourceHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ioc_resource_history_id")
    private Integer iocResourceHistoryId;
    @Column(name = "ioc_boot_id")
    private Integer iocBootId;
    @Size(max = 255)
    @Column(name = "text_line")
    private String textLine;
    @Column(name = "load_order")
    private Integer loadOrder;
    @Column(name = "uri_history_id")
    private Integer uriHistoryId;
    @Column(name = "unreachable")
    private Boolean unreachable;
    @Size(max = 255)
    @Column(name = "subst_str")
    private String substStr;
    @Column(name = "ioc_resource_type_id")
    private Integer iocResourceTypeId;
    @OneToMany(mappedBy = "iocResourceHistoryId")
    private List<RecTypeHistory> recTypeHistoryList;
    @OneToMany(mappedBy = "iocResourceHistoryId")
    private List<FldHistory> fldHistoryList;

    public IocResourceHistory() {
    }

    public IocResourceHistory(Integer iocResourceHistoryId) {
        this.iocResourceHistoryId = iocResourceHistoryId;
    }

    public Integer getIocResourceHistoryId() {
        return iocResourceHistoryId;
    }

    public void setIocResourceHistoryId(Integer iocResourceHistoryId) {
        this.iocResourceHistoryId = iocResourceHistoryId;
    }

    public Integer getIocBootId() {
        return iocBootId;
    }

    public void setIocBootId(Integer iocBootId) {
        this.iocBootId = iocBootId;
    }

    public String getTextLine() {
        return textLine;
    }

    public void setTextLine(String textLine) {
        this.textLine = textLine;
    }

    public Integer getLoadOrder() {
        return loadOrder;
    }

    public void setLoadOrder(Integer loadOrder) {
        this.loadOrder = loadOrder;
    }

    public Integer getUriHistoryId() {
        return uriHistoryId;
    }

    public void setUriHistoryId(Integer uriHistoryId) {
        this.uriHistoryId = uriHistoryId;
    }

    public Boolean getUnreachable() {
        return unreachable;
    }

    public void setUnreachable(Boolean unreachable) {
        this.unreachable = unreachable;
    }

    public String getSubstStr() {
        return substStr;
    }

    public void setSubstStr(String substStr) {
        this.substStr = substStr;
    }

    public Integer getIocResourceTypeId() {
        return iocResourceTypeId;
    }

    public void setIocResourceTypeId(Integer iocResourceTypeId) {
        this.iocResourceTypeId = iocResourceTypeId;
    }

    @XmlTransient
    public List<RecTypeHistory> getRecTypeHistoryList() {
        return recTypeHistoryList;
    }

    public void setRecTypeHistoryList(List<RecTypeHistory> recTypeHistoryList) {
        this.recTypeHistoryList = recTypeHistoryList;
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
        hash += (iocResourceHistoryId != null ? iocResourceHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IocResourceHistory)) {
            return false;
        }
        IocResourceHistory other = (IocResourceHistory) object;
        if ((this.iocResourceHistoryId == null && other.iocResourceHistoryId != null) || (this.iocResourceHistoryId != null && !this.iocResourceHistoryId.equals(other.iocResourceHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.IocResourceHistory[ iocResourceHistoryId=" + iocResourceHistoryId + " ]";
    }
    
}
