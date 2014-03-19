/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "ioc_stcmd_line")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IocStcmdLine.findAll", query = "SELECT i FROM IocStcmdLine i"),
    @NamedQuery(name = "IocStcmdLine.findByIocStcmdLineId", query = "SELECT i FROM IocStcmdLine i WHERE i.iocStcmdLineId = :iocStcmdLineId"),
    @NamedQuery(name = "IocStcmdLine.findByIocStcmdLine", query = "SELECT i FROM IocStcmdLine i WHERE i.iocStcmdLine = :iocStcmdLine"),
    @NamedQuery(name = "IocStcmdLine.findByTableModifiedDate", query = "SELECT i FROM IocStcmdLine i WHERE i.tableModifiedDate = :tableModifiedDate"),
    @NamedQuery(name = "IocStcmdLine.findByTableModifiedBy", query = "SELECT i FROM IocStcmdLine i WHERE i.tableModifiedBy = :tableModifiedBy"),
    @NamedQuery(name = "IocStcmdLine.findByIocStcmdLineNumber", query = "SELECT i FROM IocStcmdLine i WHERE i.iocStcmdLineNumber = :iocStcmdLineNumber"),
    @NamedQuery(name = "IocStcmdLine.findByIncludeLineNumber", query = "SELECT i FROM IocStcmdLine i WHERE i.includeLineNumber = :includeLineNumber")})
public class IocStcmdLine implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ioc_stcmd_line_id")
    private Integer iocStcmdLineId;
    @Size(max = 255)
    @Column(name = "ioc_stcmd_line")
    private String iocStcmdLine;
    @Basic(optional = false)
    @NotNull
    @Column(name = "table_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tableModifiedDate;
    @Size(max = 20)
    @Column(name = "table_modified_by")
    private String tableModifiedBy;
    @Column(name = "ioc_stcmd_line_number")
    private Integer iocStcmdLineNumber;
    @Column(name = "include_line_number")
    private Integer includeLineNumber;
    @JoinColumn(name = "ioc_id", referencedColumnName = "ioc_id")
    @ManyToOne
    private Ioc iocId;

    public IocStcmdLine() {
    }

    public IocStcmdLine(Integer iocStcmdLineId) {
        this.iocStcmdLineId = iocStcmdLineId;
    }

    public IocStcmdLine(Integer iocStcmdLineId, Date tableModifiedDate) {
        this.iocStcmdLineId = iocStcmdLineId;
        this.tableModifiedDate = tableModifiedDate;
    }

    public Integer getIocStcmdLineId() {
        return iocStcmdLineId;
    }

    public void setIocStcmdLineId(Integer iocStcmdLineId) {
        this.iocStcmdLineId = iocStcmdLineId;
    }

    public String getIocStcmdLine() {
        return iocStcmdLine;
    }

    public void setIocStcmdLine(String iocStcmdLine) {
        this.iocStcmdLine = iocStcmdLine;
    }

    public Date getTableModifiedDate() {
        return tableModifiedDate;
    }

    public void setTableModifiedDate(Date tableModifiedDate) {
        this.tableModifiedDate = tableModifiedDate;
    }

    public String getTableModifiedBy() {
        return tableModifiedBy;
    }

    public void setTableModifiedBy(String tableModifiedBy) {
        this.tableModifiedBy = tableModifiedBy;
    }

    public Integer getIocStcmdLineNumber() {
        return iocStcmdLineNumber;
    }

    public void setIocStcmdLineNumber(Integer iocStcmdLineNumber) {
        this.iocStcmdLineNumber = iocStcmdLineNumber;
    }

    public Integer getIncludeLineNumber() {
        return includeLineNumber;
    }

    public void setIncludeLineNumber(Integer includeLineNumber) {
        this.includeLineNumber = includeLineNumber;
    }

    public Ioc getIocId() {
        return iocId;
    }

    public void setIocId(Ioc iocId) {
        this.iocId = iocId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iocStcmdLineId != null ? iocStcmdLineId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IocStcmdLine)) {
            return false;
        }
        IocStcmdLine other = (IocStcmdLine) object;
        if ((this.iocStcmdLineId == null && other.iocStcmdLineId != null) || (this.iocStcmdLineId != null && !this.iocStcmdLineId.equals(other.iocStcmdLineId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.IocStcmdLine[ iocStcmdLineId=" + iocStcmdLineId + " ]";
    }
    
}
