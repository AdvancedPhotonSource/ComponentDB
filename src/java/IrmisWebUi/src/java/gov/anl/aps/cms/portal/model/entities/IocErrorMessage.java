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
@Table(name = "ioc_error_message")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IocErrorMessage.findAll", query = "SELECT i FROM IocErrorMessage i"),
    @NamedQuery(name = "IocErrorMessage.findByIocErrorNum", query = "SELECT i FROM IocErrorMessage i WHERE i.iocErrorNum = :iocErrorNum"),
    @NamedQuery(name = "IocErrorMessage.findByIocErrorMessage", query = "SELECT i FROM IocErrorMessage i WHERE i.iocErrorMessage = :iocErrorMessage")})
public class IocErrorMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ioc_error_num")
    private Integer iocErrorNum;
    @Size(max = 250)
    @Column(name = "ioc_error_message")
    private String iocErrorMessage;

    public IocErrorMessage() {
    }

    public IocErrorMessage(Integer iocErrorNum) {
        this.iocErrorNum = iocErrorNum;
    }

    public Integer getIocErrorNum() {
        return iocErrorNum;
    }

    public void setIocErrorNum(Integer iocErrorNum) {
        this.iocErrorNum = iocErrorNum;
    }

    public String getIocErrorMessage() {
        return iocErrorMessage;
    }

    public void setIocErrorMessage(String iocErrorMessage) {
        this.iocErrorMessage = iocErrorMessage;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iocErrorNum != null ? iocErrorNum.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IocErrorMessage)) {
            return false;
        }
        IocErrorMessage other = (IocErrorMessage) object;
        if ((this.iocErrorNum == null && other.iocErrorNum != null) || (this.iocErrorNum != null && !this.iocErrorNum.equals(other.iocErrorNum))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.IocErrorMessage[ iocErrorNum=" + iocErrorNum + " ]";
    }
    
}
