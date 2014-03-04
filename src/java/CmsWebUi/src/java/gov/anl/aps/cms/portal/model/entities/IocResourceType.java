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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "ioc_resource_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IocResourceType.findAll", query = "SELECT i FROM IocResourceType i"),
    @NamedQuery(name = "IocResourceType.findByIocResourceTypeId", query = "SELECT i FROM IocResourceType i WHERE i.iocResourceTypeId = :iocResourceTypeId"),
    @NamedQuery(name = "IocResourceType.findByIocResourceType", query = "SELECT i FROM IocResourceType i WHERE i.iocResourceType = :iocResourceType")})
public class IocResourceType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ioc_resource_type_id")
    private Integer iocResourceTypeId;
    @Size(max = 40)
    @Column(name = "ioc_resource_type")
    private String iocResourceType;

    public IocResourceType() {
    }

    public IocResourceType(Integer iocResourceTypeId) {
        this.iocResourceTypeId = iocResourceTypeId;
    }

    public Integer getIocResourceTypeId() {
        return iocResourceTypeId;
    }

    public void setIocResourceTypeId(Integer iocResourceTypeId) {
        this.iocResourceTypeId = iocResourceTypeId;
    }

    public String getIocResourceType() {
        return iocResourceType;
    }

    public void setIocResourceType(String iocResourceType) {
        this.iocResourceType = iocResourceType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iocResourceTypeId != null ? iocResourceTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IocResourceType)) {
            return false;
        }
        IocResourceType other = (IocResourceType) object;
        if ((this.iocResourceTypeId == null && other.iocResourceTypeId != null) || (this.iocResourceTypeId != null && !this.iocResourceTypeId.equals(other.iocResourceTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.IocResourceType[ iocResourceTypeId=" + iocResourceTypeId + " ]";
    }
    
}
