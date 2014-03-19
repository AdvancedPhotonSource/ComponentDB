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
@Table(name = "aoi_crawler")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiCrawler.findAll", query = "SELECT a FROM AoiCrawler a"),
    @NamedQuery(name = "AoiCrawler.findByAoiCrawlerId", query = "SELECT a FROM AoiCrawler a WHERE a.aoiCrawlerId = :aoiCrawlerId")})
public class AoiCrawler implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_crawler_id")
    private Integer aoiCrawlerId;
    @JoinColumn(name = "ioc_boot_id", referencedColumnName = "ioc_boot_id")
    @ManyToOne
    private IocBoot iocBootId;
    @JoinColumn(name = "ioc_id", referencedColumnName = "ioc_id")
    @ManyToOne
    private Ioc iocId;

    public AoiCrawler() {
    }

    public AoiCrawler(Integer aoiCrawlerId) {
        this.aoiCrawlerId = aoiCrawlerId;
    }

    public Integer getAoiCrawlerId() {
        return aoiCrawlerId;
    }

    public void setAoiCrawlerId(Integer aoiCrawlerId) {
        this.aoiCrawlerId = aoiCrawlerId;
    }

    public IocBoot getIocBootId() {
        return iocBootId;
    }

    public void setIocBootId(IocBoot iocBootId) {
        this.iocBootId = iocBootId;
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
        hash += (aoiCrawlerId != null ? aoiCrawlerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiCrawler)) {
            return false;
        }
        AoiCrawler other = (AoiCrawler) object;
        if ((this.aoiCrawlerId == null && other.aoiCrawlerId != null) || (this.aoiCrawlerId != null && !this.aoiCrawlerId.equals(other.aoiCrawlerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiCrawler[ aoiCrawlerId=" + aoiCrawlerId + " ]";
    }
    
}
