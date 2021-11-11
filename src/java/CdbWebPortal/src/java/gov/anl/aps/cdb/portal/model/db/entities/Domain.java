/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.PortalStyles;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@Table(name = "domain")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Domain.findAll", query = "SELECT d FROM Domain d"),
    @NamedQuery(name = "Domain.findById", query = "SELECT d FROM Domain d WHERE d.id = :id"),
    @NamedQuery(name = "Domain.findByName", query = "SELECT d FROM Domain d WHERE d.name = :name"),
    @NamedQuery(name = "Domain.findByDescription", query = "SELECT d FROM Domain d WHERE d.description = :description")})
public class Domain extends CdbEntity implements Serializable {

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
    @Size(max = 32)
    @Column(name = "item_identifier1_label")
    private String itemIdentifier1Label;    
    @Size(max = 32)
    @Column(name = "item_identifier2_label")
    private String itemIdentifier2Label;
    @Size(max = 32)
    @Column(name = "item_type_label")
    private String itemTypeLabel;
    @Size(max = 32)
    @Column(name = "item_category_label")
    private String itemCategoryLabel;    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "domain")
    private List<Item> itemList;    
    @ManyToMany(mappedBy = "allowedDomainList")
    private List<PropertyType> propertyTypeList;    
    @OneToMany(mappedBy = "domain")
    @OrderBy("name ASC")
    private List<ItemType> itemTypeList;
    @OneToMany(mappedBy = "domain")
    @OrderBy("name ASC")
    private List<ItemCategory> itemCategoryList;
    @JoinTable(name = "allowed_entity_type_domain", joinColumns = {
        @JoinColumn(name = "domain_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "entity_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<EntityType> allowedEntityTypeList;     
    
    private transient String domainRepIcon = null; 

    public Domain() {
    }

    public Domain(Integer id) {
        this.id = id;
    }

    public Domain(Integer id, String name) {
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

    public String getItemIdentifier1Label() {
        return itemIdentifier1Label;
    }

    public String getItemIdentifier2Label() {
        return itemIdentifier2Label;
    }

    public String getItemTypeLabel() {
        return itemTypeLabel;
    }

    public void setItemTypeLabel(String itemTypeLabel) {
        this.itemTypeLabel = itemTypeLabel;
    }

    public String getItemCategoryLabel() {
        return itemCategoryLabel;
    }

    public void setItemCategoryLabel(String itemCategoryLabel) {
        this.itemCategoryLabel = itemCategoryLabel;
    }

    @XmlTransient
    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @XmlTransient
    public List<ItemType> getItemTypeList() {
        return itemTypeList;
    }

    public void setItemTypeList(List<ItemType> itemTypeList) {
        this.itemTypeList = itemTypeList;
    }

    @XmlTransient
    public List<PropertyType> getPropertyTypeList() {
        return propertyTypeList;
    }

    public void setPropertyTypeList(List<PropertyType> propertyTypeList) {
        this.propertyTypeList = propertyTypeList;
    }

    @XmlTransient
    public List<ItemCategory> getItemCategoryList() {
        return itemCategoryList;
    }

    public void setItemCategoryList(List<ItemCategory> itemCategoryList) {
        this.itemCategoryList = itemCategoryList;
    }

    @XmlTransient
    public List<EntityType> getAllowedEntityTypeList() {
        return allowedEntityTypeList;
    }

    public void setAllowedEntityTypeList(List<EntityType> allowedEntityTypeList) {
        this.allowedEntityTypeList = allowedEntityTypeList;
    }

    public String getDomainRepIcon() {
        if (domainRepIcon == null) {
            switch(name){
                case "Catalog":
                    domainRepIcon = PortalStyles.catalogIcon.getValue();
                    break;
                case "Machine Design":
                    domainRepIcon = PortalStyles.machineDesignIcon.getValue();
                    break;
                case "Inventory":
                    domainRepIcon = PortalStyles.inventoryIcon.getValue();
                    break;
                case "Maarc":
                    domainRepIcon = PortalStyles.maarcIcon.getValue();
                    break;
                case "Cable Design":
                    domainRepIcon = PortalStyles.cableDesignIcon.getValue();
                    break;
                default:
                    domainRepIcon = ""; 
                    break;
            }
        }
        return domainRepIcon;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Domain)) {
            return false;
        }
        Domain other = (Domain) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if (this.id != null && other.id != null && this.id == other.id) {
            return true; 
        }
        return false;
    }

    @Override
    public String toString() {
        return name; 
    }

}
