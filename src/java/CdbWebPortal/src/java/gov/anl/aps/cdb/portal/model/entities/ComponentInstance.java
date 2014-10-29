/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "component_instance")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentInstance.findAll", query = "SELECT c FROM ComponentInstance c"),
    @NamedQuery(name = "ComponentInstance.findById", query = "SELECT c FROM ComponentInstance c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentInstance.findByQuantity", query = "SELECT c FROM ComponentInstance c WHERE c.quantity = :quantity"),
    @NamedQuery(name = "ComponentInstance.findByLocationDetails", query = "SELECT c FROM ComponentInstance c WHERE c.locationDetails = :locationDetails"),
    @NamedQuery(name = "ComponentInstance.findByDescription", query = "SELECT c FROM ComponentInstance c WHERE c.description = :description")})
public class ComponentInstance extends CloneableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    private Integer quantity;
    @Size(max = 256)
    @Column(name = "location_details")
    private String locationDetails;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "component_instance_log", joinColumns = {
        @JoinColumn(name = "component_instance_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Log> logList;
    @ManyToMany(mappedBy = "componentInstanceList")
    private List<DesignElement> designElementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentInstance")
    private List<ComponentInstanceProperty> componentInstancePropertyList;
    @OneToMany(mappedBy = "locationId")
    private List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentInstanceId")
    private List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList1;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EntityInfo entityInfoId;
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @ManyToOne
    private Location locationId;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component componentId;

    public ComponentInstance() {
    }

    public ComponentInstance(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList() {
        return designElementList;
    }

    public void setDesignElementList(List<DesignElement> designElementList) {
        this.designElementList = designElementList;
    }

    @XmlTransient
    public List<ComponentInstanceProperty> getComponentInstancePropertyList() {
        return componentInstancePropertyList;
    }

    public void setComponentInstancePropertyList(List<ComponentInstanceProperty> componentInstancePropertyList) {
        this.componentInstancePropertyList = componentInstancePropertyList;
    }

    @XmlTransient
    public List<ComponentInstanceLocationHistory> getComponentInstanceLocationHistoryList() {
        return componentInstanceLocationHistoryList;
    }

    public void setComponentInstanceLocationHistoryList(List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList) {
        this.componentInstanceLocationHistoryList = componentInstanceLocationHistoryList;
    }

    @XmlTransient
    public List<ComponentInstanceLocationHistory> getComponentInstanceLocationHistoryList1() {
        return componentInstanceLocationHistoryList1;
    }

    public void setComponentInstanceLocationHistoryList1(List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList1) {
        this.componentInstanceLocationHistoryList1 = componentInstanceLocationHistoryList1;
    }

    public EntityInfo getEntityInfoId() {
        return entityInfoId;
    }

    public void setEntityInfoId(EntityInfo entityInfoId) {
        this.entityInfoId = entityInfoId;
    }

    public Location getLocationId() {
        return locationId;
    }

    public void setLocationId(Location locationId) {
        this.locationId = locationId;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentInstance)) {
            return false;
        }
        ComponentInstance other = (ComponentInstance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.ComponentInstance[ id=" + id + " ]";
    }
    
}
