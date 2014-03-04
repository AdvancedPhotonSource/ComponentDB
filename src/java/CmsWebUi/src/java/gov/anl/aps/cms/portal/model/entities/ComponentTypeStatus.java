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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_type_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentTypeStatus.findAll", query = "SELECT c FROM ComponentTypeStatus c"),
    @NamedQuery(name = "ComponentTypeStatus.findByComponentTypeStatusId", query = "SELECT c FROM ComponentTypeStatus c WHERE c.componentTypeStatusId = :componentTypeStatusId"),
    @NamedQuery(name = "ComponentTypeStatus.findBySpareQty", query = "SELECT c FROM ComponentTypeStatus c WHERE c.spareQty = :spareQty"),
    @NamedQuery(name = "ComponentTypeStatus.findByStockQty", query = "SELECT c FROM ComponentTypeStatus c WHERE c.stockQty = :stockQty"),
    @NamedQuery(name = "ComponentTypeStatus.findBySpareLoc", query = "SELECT c FROM ComponentTypeStatus c WHERE c.spareLoc = :spareLoc"),
    @NamedQuery(name = "ComponentTypeStatus.findByInstantiated", query = "SELECT c FROM ComponentTypeStatus c WHERE c.instantiated = :instantiated"),
    @NamedQuery(name = "ComponentTypeStatus.findByMarkForDelete", query = "SELECT c FROM ComponentTypeStatus c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentTypeStatus.findByVersion", query = "SELECT c FROM ComponentTypeStatus c WHERE c.version = :version"),
    @NamedQuery(name = "ComponentTypeStatus.findByNrtlStatus", query = "SELECT c FROM ComponentTypeStatus c WHERE c.nrtlStatus = :nrtlStatus"),
    @NamedQuery(name = "ComponentTypeStatus.findByNrtlAgency", query = "SELECT c FROM ComponentTypeStatus c WHERE c.nrtlAgency = :nrtlAgency")})
public class ComponentTypeStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_type_status_id")
    private Integer componentTypeStatusId;
    @Column(name = "spare_qty")
    private Integer spareQty;
    @Column(name = "stock_qty")
    private Integer stockQty;
    @Size(max = 100)
    @Column(name = "spare_loc")
    private String spareLoc;
    @Column(name = "instantiated")
    private Boolean instantiated;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @Size(max = 10)
    @Column(name = "nrtl_status")
    private String nrtlStatus;
    @Size(max = 60)
    @Column(name = "nrtl_agency")
    private String nrtlAgency;
    @JoinColumn(name = "component_type_id", referencedColumnName = "component_type_id")
    @ManyToOne
    private ComponentType componentTypeId;

    public ComponentTypeStatus() {
    }

    public ComponentTypeStatus(Integer componentTypeStatusId) {
        this.componentTypeStatusId = componentTypeStatusId;
    }

    public Integer getComponentTypeStatusId() {
        return componentTypeStatusId;
    }

    public void setComponentTypeStatusId(Integer componentTypeStatusId) {
        this.componentTypeStatusId = componentTypeStatusId;
    }

    public Integer getSpareQty() {
        return spareQty;
    }

    public void setSpareQty(Integer spareQty) {
        this.spareQty = spareQty;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public String getSpareLoc() {
        return spareLoc;
    }

    public void setSpareLoc(String spareLoc) {
        this.spareLoc = spareLoc;
    }

    public Boolean getInstantiated() {
        return instantiated;
    }

    public void setInstantiated(Boolean instantiated) {
        this.instantiated = instantiated;
    }

    public Boolean getMarkForDelete() {
        return markForDelete;
    }

    public void setMarkForDelete(Boolean markForDelete) {
        this.markForDelete = markForDelete;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getNrtlStatus() {
        return nrtlStatus;
    }

    public void setNrtlStatus(String nrtlStatus) {
        this.nrtlStatus = nrtlStatus;
    }

    public String getNrtlAgency() {
        return nrtlAgency;
    }

    public void setNrtlAgency(String nrtlAgency) {
        this.nrtlAgency = nrtlAgency;
    }

    public ComponentType getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(ComponentType componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentTypeStatusId != null ? componentTypeStatusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentTypeStatus)) {
            return false;
        }
        ComponentTypeStatus other = (ComponentTypeStatus) object;
        if ((this.componentTypeStatusId == null && other.componentTypeStatusId != null) || (this.componentTypeStatusId != null && !this.componentTypeStatusId.equals(other.componentTypeStatusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentTypeStatus[ componentTypeStatusId=" + componentTypeStatusId + " ]";
    }
    
}
