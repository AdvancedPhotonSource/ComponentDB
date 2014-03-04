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
@Table(name = "ioc_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IocStatus.findAll", query = "SELECT i FROM IocStatus i"),
    @NamedQuery(name = "IocStatus.findByIocStatusId", query = "SELECT i FROM IocStatus i WHERE i.iocStatusId = :iocStatusId"),
    @NamedQuery(name = "IocStatus.findByIocStatus", query = "SELECT i FROM IocStatus i WHERE i.iocStatus = :iocStatus")})
public class IocStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ioc_status_id")
    private Integer iocStatusId;
    @Size(max = 40)
    @Column(name = "ioc_status")
    private String iocStatus;
    @OneToMany(mappedBy = "iocStatusId")
    private List<Ioc> iocList;

    public IocStatus() {
    }

    public IocStatus(Integer iocStatusId) {
        this.iocStatusId = iocStatusId;
    }

    public Integer getIocStatusId() {
        return iocStatusId;
    }

    public void setIocStatusId(Integer iocStatusId) {
        this.iocStatusId = iocStatusId;
    }

    public String getIocStatus() {
        return iocStatus;
    }

    public void setIocStatus(String iocStatus) {
        this.iocStatus = iocStatus;
    }

    @XmlTransient
    public List<Ioc> getIocList() {
        return iocList;
    }

    public void setIocList(List<Ioc> iocList) {
        this.iocList = iocList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iocStatusId != null ? iocStatusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IocStatus)) {
            return false;
        }
        IocStatus other = (IocStatus) object;
        if ((this.iocStatusId == null && other.iocStatusId != null) || (this.iocStatusId != null && !this.iocStatusId.equals(other.iocStatusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.IocStatus[ iocStatusId=" + iocStatusId + " ]";
    }
    
}
