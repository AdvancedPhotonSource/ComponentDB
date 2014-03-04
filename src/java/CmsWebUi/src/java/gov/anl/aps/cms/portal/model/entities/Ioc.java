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
import javax.persistence.CascadeType;
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
@Table(name = "ioc")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ioc.findAll", query = "SELECT i FROM Ioc i"),
    @NamedQuery(name = "Ioc.findByIocId", query = "SELECT i FROM Ioc i WHERE i.iocId = :iocId"),
    @NamedQuery(name = "Ioc.findByIocNm", query = "SELECT i FROM Ioc i WHERE i.iocNm = :iocNm"),
    @NamedQuery(name = "Ioc.findBySystem", query = "SELECT i FROM Ioc i WHERE i.system = :system"),
    @NamedQuery(name = "Ioc.findByActive", query = "SELECT i FROM Ioc i WHERE i.active = :active"),
    @NamedQuery(name = "Ioc.findByModifiedDate", query = "SELECT i FROM Ioc i WHERE i.modifiedDate = :modifiedDate"),
    @NamedQuery(name = "Ioc.findByModifiedBy", query = "SELECT i FROM Ioc i WHERE i.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "Ioc.findByComponentId", query = "SELECT i FROM Ioc i WHERE i.componentId = :componentId")})
public class Ioc implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ioc_id")
    private Integer iocId;
    @Size(max = 40)
    @Column(name = "ioc_nm")
    private String iocNm;
    @Size(max = 20)
    @Column(name = "system")
    private String system;
    @Column(name = "active")
    private Boolean active;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Size(max = 10)
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "component_id")
    private Integer componentId;
    @OneToMany(mappedBy = "iocId")
    private List<AoiCrawler> aoiCrawlerList;
    @JoinColumn(name = "ioc_status_id", referencedColumnName = "ioc_status_id")
    @ManyToOne
    private IocStatus iocStatusId;
    @OneToMany(mappedBy = "iocId")
    private List<IocStcmdLine> iocStcmdLineList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iocId")
    private List<ApsIoc> apsIocList;

    public Ioc() {
    }

    public Ioc(Integer iocId) {
        this.iocId = iocId;
    }

    public Ioc(Integer iocId, Date modifiedDate) {
        this.iocId = iocId;
        this.modifiedDate = modifiedDate;
    }

    public Integer getIocId() {
        return iocId;
    }

    public void setIocId(Integer iocId) {
        this.iocId = iocId;
    }

    public String getIocNm() {
        return iocNm;
    }

    public void setIocNm(String iocNm) {
        this.iocNm = iocNm;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }

    @XmlTransient
    public List<AoiCrawler> getAoiCrawlerList() {
        return aoiCrawlerList;
    }

    public void setAoiCrawlerList(List<AoiCrawler> aoiCrawlerList) {
        this.aoiCrawlerList = aoiCrawlerList;
    }

    public IocStatus getIocStatusId() {
        return iocStatusId;
    }

    public void setIocStatusId(IocStatus iocStatusId) {
        this.iocStatusId = iocStatusId;
    }

    @XmlTransient
    public List<IocStcmdLine> getIocStcmdLineList() {
        return iocStcmdLineList;
    }

    public void setIocStcmdLineList(List<IocStcmdLine> iocStcmdLineList) {
        this.iocStcmdLineList = iocStcmdLineList;
    }

    @XmlTransient
    public List<ApsIoc> getApsIocList() {
        return apsIocList;
    }

    public void setApsIocList(List<ApsIoc> apsIocList) {
        this.apsIocList = apsIocList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iocId != null ? iocId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ioc)) {
            return false;
        }
        Ioc other = (Ioc) object;
        if ((this.iocId == null && other.iocId != null) || (this.iocId != null && !this.iocId.equals(other.iocId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Ioc[ iocId=" + iocId + " ]";
    }
    
}
