/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Cacheable(true)
@Table(name = "entity_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EntityType.findAll", query = "SELECT e FROM EntityType e"),
    @NamedQuery(name = "EntityType.findById", query = "SELECT e FROM EntityType e WHERE e.id = :id"),
    @NamedQuery(name = "EntityType.findByName", query = "SELECT e FROM EntityType e WHERE e.name = :name"),
    @NamedQuery(name = "EntityType.findByDescription", query = "SELECT e FROM EntityType e WHERE e.description = :description")})
public class EntityType extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "allowed_child_entity_type", joinColumns = {
        @JoinColumn(name = "parent_entity_type_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "child_entity_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<EntityType> allowedEntityTypeList;
    @ManyToMany(mappedBy = "allowedEntityTypeList")
    private List<EntityType> entityTypeList1;
    @ManyToMany(mappedBy = "entityTypeList")
    private List<Item> itemList;
    @ManyToMany(mappedBy = "entityTypeList")
    private List<PropertyType> propertyTypeList;

    public EntityType() {
    }

    public EntityType(Integer id) {
        this.id = id;
    }

    public EntityType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<EntityType> getAllowedEntityTypeList() {
        return allowedEntityTypeList;
    }

    public void setAllowedEntityTypeList(List<EntityType> allowedEntityTypeList) {
        this.allowedEntityTypeList = allowedEntityTypeList;
    }

    @XmlTransient
    public List<EntityType> getEntityTypeList1() {
        return entityTypeList1;
    }

    public void setEntityTypeList1(List<EntityType> entityTypeList1) {
        this.entityTypeList1 = entityTypeList1;
    }

    @XmlTransient
    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @XmlTransient
    public List<PropertyType> getPropertyTypeList() {
        return propertyTypeList;
    }

    public void setPropertyTypeList(List<PropertyType> propertyTypeList) {
        this.propertyTypeList = propertyTypeList;
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
        if (!(object instanceof EntityType)) {
            return false;
        }
        EntityType other = (EntityType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getName();
    }
    
}
