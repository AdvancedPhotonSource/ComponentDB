/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
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
@Table(name = "item_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemType.findAll", query = "SELECT i FROM ItemType i"),
    @NamedQuery(name = "ItemType.findById", query = "SELECT i FROM ItemType i WHERE i.id = :id"),
    @NamedQuery(name = "ItemType.findByName", query = "SELECT i FROM ItemType i WHERE i.name = :name"),
    @NamedQuery(name = "ItemType.findByDescription", query = "SELECT i FROM ItemType i WHERE i.description = :description"),
    @NamedQuery(name = "ItemType.findByDomainName", query = "SELECT i FROM ItemType i WHERE i.domain.name = :domainName ORDER BY i.name ASC")})
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
    private List<Item> itemList;
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    @ManyToOne
    private Domain domain;
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

    public String getItemCategoryString() {
        if (itemCategoryString == null) {
            itemCategoryString = StringUtility.getStringifyCdbList(itemCategoryList);
        }
        return itemCategoryString;
    }

    public String getEditItemCategoryString() {
        itemCategoryString = getItemCategoryString();
        if (itemCategoryString.equals("-")) {
            return "Select Item " + getItemCategoryTitle();
        } else {
            return itemCategoryString;
        }
    }        

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);

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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemType)) {
            return false;
        }
        ItemType other = (ItemType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
