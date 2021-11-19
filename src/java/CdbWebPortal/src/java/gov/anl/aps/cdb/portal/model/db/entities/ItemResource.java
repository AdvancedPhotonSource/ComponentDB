/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
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
 * @author djarosz
 */
@Entity
@Cacheable(true)
@Table(name = "item_resource")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemResource.findAll", query = "SELECT i FROM ItemResource i"),
    @NamedQuery(name = "ItemResource.findById", query = "SELECT i FROM ItemResource i WHERE i.id = :id"),
    @NamedQuery(name = "ItemResource.findByValue", query = "SELECT i FROM ItemResource i WHERE i.value = :value"),
    @NamedQuery(name = "ItemResource.findByUnits", query = "SELECT i FROM ItemResource i WHERE i.units = :units"),
    @NamedQuery(name = "ItemResource.findByDescription", query = "SELECT i FROM ItemResource i WHERE i.description = :description"),
    @NamedQuery(name = "ItemResource.findByIsProvided", query = "SELECT i FROM ItemResource i WHERE i.isProvided = :isProvided"),
    @NamedQuery(name = "ItemResource.findByIsUsedRequired", query = "SELECT i FROM ItemResource i WHERE i.isUsedRequired = :isUsedRequired"),
    @NamedQuery(name = "ItemResource.findByIsUsedOptional", query = "SELECT i FROM ItemResource i WHERE i.isUsedOptional = :isUsedOptional")})
public class ItemResource implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String value;
    @Size(max = 64)
    private String units;
    @Size(max = 256)
    private String description;
    @Column(name = "is_provided")
    private Boolean isProvided;
    @Column(name = "is_used_required")
    private Boolean isUsedRequired;
    @Column(name = "is_used_optional")
    private Boolean isUsedOptional;
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Item item;
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ResourceType resourceType;
    @JoinColumn(name = "item_connector_id", referencedColumnName = "id")
    @ManyToOne
    private ItemConnector itemConnector;

    public ItemResource() {
    }

    public ItemResource(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsProvided() {
        return isProvided;
    }

    public void setIsProvided(Boolean isProvided) {
        this.isProvided = isProvided;
    }

    public Boolean getIsUsedRequired() {
        return isUsedRequired;
    }

    public void setIsUsedRequired(Boolean isUsedRequired) {
        this.isUsedRequired = isUsedRequired;
    }

    public Boolean getIsUsedOptional() {
        return isUsedOptional;
    }

    public void setIsUsedOptional(Boolean isUsedOptional) {
        this.isUsedOptional = isUsedOptional;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ItemConnector getItemConnector() {
        return itemConnector;
    }

    public void setItemConnector(ItemConnector itemConnector) {
        this.itemConnector = itemConnector;
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
        if (!(object instanceof ItemResource)) {
            return false;
        }
        ItemResource other = (ItemResource) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.ItemResource[ id=" + id + " ]";
    }
    
}
