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
@Table(name = "ioc_resource")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IocResource.findAll", query = "SELECT i FROM IocResource i"),
    @NamedQuery(name = "IocResource.findByIocResourceId", query = "SELECT i FROM IocResource i WHERE i.iocResourceId = :iocResourceId"),
    @NamedQuery(name = "IocResource.findByTextLine", query = "SELECT i FROM IocResource i WHERE i.textLine = :textLine"),
    @NamedQuery(name = "IocResource.findByLoadOrder", query = "SELECT i FROM IocResource i WHERE i.loadOrder = :loadOrder"),
    @NamedQuery(name = "IocResource.findByUnreachable", query = "SELECT i FROM IocResource i WHERE i.unreachable = :unreachable"),
    @NamedQuery(name = "IocResource.findBySubstStr", query = "SELECT i FROM IocResource i WHERE i.substStr = :substStr"),
    @NamedQuery(name = "IocResource.findByIocResourceTypeId", query = "SELECT i FROM IocResource i WHERE i.iocResourceTypeId = :iocResourceTypeId")})
public class IocResource implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ioc_resource_id")
    private Integer iocResourceId;
    @Size(max = 255)
    @Column(name = "text_line")
    private String textLine;
    @Column(name = "load_order")
    private Integer loadOrder;
    @Column(name = "unreachable")
    private Boolean unreachable;
    @Size(max = 255)
    @Column(name = "subst_str")
    private String substStr;
    @Column(name = "ioc_resource_type_id")
    private Integer iocResourceTypeId;
    @JoinColumn(name = "uri_id", referencedColumnName = "uri_id")
    @ManyToOne
    private Uri uriId;
    @JoinColumn(name = "ioc_boot_id", referencedColumnName = "ioc_boot_id")
    @ManyToOne
    private IocBoot iocBootId;
    @OneToMany(mappedBy = "iocResourceId")
    private List<Fld> fldList;
    @OneToMany(mappedBy = "iocResourceId")
    private List<RecType> recTypeList;

    public IocResource() {
    }

    public IocResource(Integer iocResourceId) {
        this.iocResourceId = iocResourceId;
    }

    public Integer getIocResourceId() {
        return iocResourceId;
    }

    public void setIocResourceId(Integer iocResourceId) {
        this.iocResourceId = iocResourceId;
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

    public Uri getUriId() {
        return uriId;
    }

    public void setUriId(Uri uriId) {
        this.uriId = uriId;
    }

    public IocBoot getIocBootId() {
        return iocBootId;
    }

    public void setIocBootId(IocBoot iocBootId) {
        this.iocBootId = iocBootId;
    }

    @XmlTransient
    public List<Fld> getFldList() {
        return fldList;
    }

    public void setFldList(List<Fld> fldList) {
        this.fldList = fldList;
    }

    @XmlTransient
    public List<RecType> getRecTypeList() {
        return recTypeList;
    }

    public void setRecTypeList(List<RecType> recTypeList) {
        this.recTypeList = recTypeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iocResourceId != null ? iocResourceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IocResource)) {
            return false;
        }
        IocResource other = (IocResource) object;
        if ((this.iocResourceId == null && other.iocResourceId != null) || (this.iocResourceId != null && !this.iocResourceId.equals(other.iocResourceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.IocResource[ iocResourceId=" + iocResourceId + " ]";
    }
    
}
