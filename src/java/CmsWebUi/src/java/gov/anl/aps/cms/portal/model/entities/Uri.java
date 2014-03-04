/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "uri")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Uri.findAll", query = "SELECT u FROM Uri u"),
    @NamedQuery(name = "Uri.findByUriId", query = "SELECT u FROM Uri u WHERE u.uriId = :uriId"),
    @NamedQuery(name = "Uri.findByUri", query = "SELECT u FROM Uri u WHERE u.uri = :uri"),
    @NamedQuery(name = "Uri.findByUriModifiedDate", query = "SELECT u FROM Uri u WHERE u.uriModifiedDate = :uriModifiedDate"),
    @NamedQuery(name = "Uri.findByModifiedDate", query = "SELECT u FROM Uri u WHERE u.modifiedDate = :modifiedDate"),
    @NamedQuery(name = "Uri.findByModifiedBy", query = "SELECT u FROM Uri u WHERE u.modifiedBy = :modifiedBy")})
public class Uri implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "uri_id")
    private Integer uriId;
    @Size(max = 255)
    @Column(name = "uri")
    private String uri;
    @Basic(optional = false)
    @NotNull
    @Column(name = "uri_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uriModifiedDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Size(max = 10)
    @Column(name = "modified_by")
    private String modifiedBy;
    @OneToMany(mappedBy = "uriId")
    private List<IocResource> iocResourceList;
    @OneToMany(mappedBy = "uriId")
    private List<ComponentTypeDocument> componentTypeDocumentList;

    public Uri() {
    }

    public Uri(Integer uriId) {
        this.uriId = uriId;
    }

    public Uri(Integer uriId, Date uriModifiedDate, Date modifiedDate) {
        this.uriId = uriId;
        this.uriModifiedDate = uriModifiedDate;
        this.modifiedDate = modifiedDate;
    }

    public Integer getUriId() {
        return uriId;
    }

    public void setUriId(Integer uriId) {
        this.uriId = uriId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getUriModifiedDate() {
        return uriModifiedDate;
    }

    public void setUriModifiedDate(Date uriModifiedDate) {
        this.uriModifiedDate = uriModifiedDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @XmlTransient
    public List<IocResource> getIocResourceList() {
        return iocResourceList;
    }

    public void setIocResourceList(List<IocResource> iocResourceList) {
        this.iocResourceList = iocResourceList;
    }

    @XmlTransient
    public List<ComponentTypeDocument> getComponentTypeDocumentList() {
        return componentTypeDocumentList;
    }

    public void setComponentTypeDocumentList(List<ComponentTypeDocument> componentTypeDocumentList) {
        this.componentTypeDocumentList = componentTypeDocumentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uriId != null ? uriId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Uri)) {
            return false;
        }
        Uri other = (Uri) object;
        if ((this.uriId == null && other.uriId != null) || (this.uriId != null && !this.uriId.equals(other.uriId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Uri[ uriId=" + uriId + " ]";
    }
    
}
