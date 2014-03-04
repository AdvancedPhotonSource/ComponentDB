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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "ioc_error")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IocError.findAll", query = "SELECT i FROM IocError i"),
    @NamedQuery(name = "IocError.findByIocErrorId", query = "SELECT i FROM IocError i WHERE i.iocErrorId = :iocErrorId"),
    @NamedQuery(name = "IocError.findByIocErrorNum", query = "SELECT i FROM IocError i WHERE i.iocErrorNum = :iocErrorNum")})
public class IocError implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ioc_error_id")
    private Integer iocErrorId;
    @Column(name = "ioc_error_num")
    private Integer iocErrorNum;
    @JoinColumn(name = "ioc_boot_id", referencedColumnName = "ioc_boot_id")
    @ManyToOne
    private IocBoot iocBootId;

    public IocError() {
    }

    public IocError(Integer iocErrorId) {
        this.iocErrorId = iocErrorId;
    }

    public Integer getIocErrorId() {
        return iocErrorId;
    }

    public void setIocErrorId(Integer iocErrorId) {
        this.iocErrorId = iocErrorId;
    }

    public Integer getIocErrorNum() {
        return iocErrorNum;
    }

    public void setIocErrorNum(Integer iocErrorNum) {
        this.iocErrorNum = iocErrorNum;
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
        hash += (iocErrorId != null ? iocErrorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IocError)) {
            return false;
        }
        IocError other = (IocError) object;
        if ((this.iocErrorId == null && other.iocErrorId != null) || (this.iocErrorId != null && !this.iocErrorId.equals(other.iocErrorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.IocError[ iocErrorId=" + iocErrorId + " ]";
    }
    
}
