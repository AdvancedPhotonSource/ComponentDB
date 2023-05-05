/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
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
@Table(name = "item_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemType.findAll", query = "SELECT i FROM ItemType i"),
    @NamedQuery(name = "ItemType.findById", query = "SELECT i FROM ItemType i WHERE i.id = :id"),
    @NamedQuery(name = "ItemType.findByName", query = "SELECT i FROM ItemType i WHERE i.name = :name"),
    @NamedQuery(name = "ItemType.findByDescription", query = "SELECT i FROM ItemType i WHERE i.description = :description"),
    @NamedQuery(name = "ItemType.findByDomainName", query = "SELECT i FROM ItemType i WHERE i.domain.name = :domainName ORDER BY i.sortOrder ASC"),
    @NamedQuery(name = "ItemType.findByNameAndDomainName", query = "SELECT i FROM ItemType i WHERE i.name = :name AND i.domain.name = :domainName ORDER BY i.name ASC")})
public class ItemType extends ItemTypeCategoryEntity implements Serializable {

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
    @ManyToMany(mappedBy = "itemTypeList")
    @JsonIgnore
    private List<Item> itemList;
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    @ManyToOne
    private Domain domain;
    @Column(name = "sort_order")
    private Float sortOrder;
    @JoinTable(name = "item_category_item_type", joinColumns = {
        @JoinColumn(name = "item_type_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_category_id", referencedColumnName = "id")})
    @ManyToMany
    @OrderBy("name ASC")
    private List<ItemCategory> itemCategoryList;

    private transient String itemCategoryString = null;

    public ItemType() {
    }

    public ItemType(Integer id) {
        this.id = id;
    }

    public ItemType(Integer id, String name) {
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
    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @XmlTransient
    @Override
    public Domain getDomain() {
        return domain;
    }
    
    public Float getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Float sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    @XmlTransient
    public List<ItemCategory> getItemCategoryList() {
        return itemCategoryList;
    }

    public void setItemCategoryList(List<ItemCategory> itemCategoryList) {
        itemCategoryString = null;
        this.itemCategoryList = itemCategoryList;
    }
    
    @JsonIgnore
    public String getItemCategoryString() {
        if (itemCategoryString == null) {
            itemCategoryString = StringUtility.getStringifyCdbList(itemCategoryList);
        }
        return itemCategoryString;
    }

    @JsonIgnore
    public String getEditItemCategoryString() {
        itemCategoryString = getItemCategoryString();
        if (itemCategoryString.equals("-")) {
            return "Select Item " + getItemCategoryTitle();
        } else {
            return itemCategoryString;
        }
    }        

    @Override
    public SearchResult createSearchResultInfo(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(this, id, name);

        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        if (domain != null) {
            searchResult.doesValueContainPattern("domain name", domain.getName(), searchPattern);
        }

        return searchResult;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (id != null) {
            hash += id.hashCode();
        } else {
            hash += getName().hashCode();
            hash += getDomain().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ItemType)) {
            return false;
        }
        
        ItemType other = (ItemType) object;
        
        // use domain and name to distinguish new items
        if ((this.id == null) && (other.id == null)) {
            
            if ((this.getDomain() != null) && (!this.getDomain().equals(other.getDomain()))) {
                // domains are different
                return false;
            }
            
            if ((this.getName() == null) && (other.getName() == null)) {
                // both names null
                return true;
            } else if (((this.getName() == null) && other.getName() != null) || (this.getName() != null && !this.getName().equals(other.getName()))) {
                // names are not equal
                return false;
            } else {
                // names are equal
                return true;
            }
            
        // check for existing items uses id
        } else if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            // at least one of the items exists and ids are not equal
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
