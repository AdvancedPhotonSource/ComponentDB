/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item_element")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemElement.findAll",
            query = "SELECT i FROM ItemElement i"),
    @NamedQuery(name = "ItemElement.findById",
            query = "SELECT i FROM ItemElement i WHERE i.id = :id"),
    @NamedQuery(name = "ItemElement.findByName",
            query = "SELECT i FROM ItemElement i WHERE i.name = :name"),
    @NamedQuery(name = "ItemElement.findByIsRequired",
            query = "SELECT i FROM ItemElement i WHERE i.isRequired = :isRequired"),
    @NamedQuery(name = "ItemElement.findByDescription",
            query = "SELECT i FROM ItemElement i WHERE i.description = :description"),
    @NamedQuery(name = "ItemElement.findBySortOrder",
            query = "SELECT i FROM ItemElement i WHERE i.sortOrder = :sortOrder"),
})
public class ItemElement extends CdbDomainEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String name;
    @Column(name = "is_required")
    private Boolean isRequired;
    @Size(max = 256)
    private String description;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "sort_order")
    private Float sortOrder;
    @JoinTable(name = "item_element_list", joinColumns = {
        @JoinColumn(name = "item_element_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "list_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ListTbl> listList;
    @JoinTable(name = "item_element_property", joinColumns = {
        @JoinColumn(name = "item_element_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PropertyValue> propertyValueList;
    @JoinTable(name = "item_element_log", joinColumns = {
        @JoinColumn(name = "item_element_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @ManyToMany
    @OrderBy("enteredOnDateTime DESC")
    private List<Log> logList;
    @JoinColumn(name = "parent_item_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Item parentItem;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "derivedFromItemElement")
    private List<ItemElement> derivedFromItemElementList;
    @JoinColumn(name = "derived_from_item_element_id", referencedColumnName = "id")
    @ManyToOne
    private ItemElement derivedFromItemElement;
    @JoinColumn(name = "contained_item_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private Item containedItem;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private EntityInfo entityInfo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstItemElement")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondItemElement")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList1;
    @OneToMany(mappedBy = "linkItemElement")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList2;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstItemElement")
    private List<ItemElementRelationship> itemElementRelationshipList;
    @OneToMany(mappedBy = "secondItemElement")
    private List<ItemElementRelationship> itemElementRelationshipList1;
    @OneToMany(mappedBy = "linkItemElement")
    private List<ItemElementRelationship> itemElementRelationshipList2;

    private static transient Integer sortByPropertyTypeId = null;
    private transient TreeNode childItemElementListTreeTableRootNode = null;
    private transient ItemElementConstraintInformation constraintInformation; 
    
    // Helper variable used to ensure proper procedure is executed if the attribute changes. 
    private transient Boolean temporaryIsRequiredValue = null; 

    public ItemElement() {
    }

    public void init(Item parentItem, ItemElement derivedFromItemElement) {
        EntityInfo newEntityInfo = EntityInfoUtility.createEntityInfo();
        this.setEntityInfo(newEntityInfo);
        this.setParentItem(parentItem);
        this.setDerivedFromItemElement(derivedFromItemElement);

    }
    
    public void init(Item parentItem) {
        init(parentItem, null);
    }

    public ItemElement(Item parentItem) {
        this.parentItem = parentItem;
    }

    public ItemElement(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getCustomizableSortOrder() {
        if (sortByPropertyTypeId == null) {
            return getSortOrder();
        } else {
            return getPropertyValue(sortByPropertyTypeId);
        }
    }

    public static void setSortByPropertyTypeId(Integer sortByPropertyTypeId) {
        ItemElement.sortByPropertyTypeId = sortByPropertyTypeId;
    }

    public String getName() {
        if (name == null || name.isEmpty()) {
            if (derivedFromItemElement != null) {
                return derivedFromItemElement.getName(); 
            }
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsRequired() {        
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    } 

    public Boolean getTemporaryIsRequiredValue() {
        if (temporaryIsRequiredValue == null) {
            temporaryIsRequiredValue = isRequired; 
        }
        return temporaryIsRequiredValue;
    }

    public void setTemporaryIsRequiredValue(Boolean temporaryIsRequiredValue) {
        this.temporaryIsRequiredValue = temporaryIsRequiredValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Float sortOrder) {
        this.sortOrder = sortOrder;
    }

    @XmlTransient
    public List<gov.anl.aps.cdb.portal.model.db.entities.ListTbl> getListList() {
        return listList;
    }

    public void setListList(List<ListTbl> listList) {
        this.listList = listList;
    }
    
    @Override
    public void addPropertyValueToPropertyValueList(PropertyValue propertyValue) {
        propertyValue.addItemElementToItemElementList(this);
        if (propertyValueList == null) {
            propertyValueList = new ArrayList<>();
        }
        propertyValueList.add(0, propertyValue);
    }

    @Override
    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @Override
    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    @XmlTransient
    public List<ItemElement> getDerivedFromItemElementList() {
        return derivedFromItemElementList;
    }

    public void setDerivedFromItemElementList(List<ItemElement> derivedFromItemElementList) {
        this.derivedFromItemElementList = derivedFromItemElementList;
    }

    @XmlTransient
    public ItemElement getDerivedFromItemElement() {
        return derivedFromItemElement;
    }

    public void setDerivedFromItemElement(ItemElement derivedFromItemElement) {
        this.derivedFromItemElement = derivedFromItemElement;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public Item getParentItem() {
        return parentItem;
    }

    public void setParentItem(Item parentItem) {
        this.parentItem = parentItem;
    }

    @XmlTransient
    public Item getContainedItem() {
        return containedItem;
    }

    public void setContainedItem(Item containedItem) {
        this.containedItem = containedItem;
    }

    @Override
    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    @XmlTransient
    public List<ItemElementRelationshipHistory> getItemElementRelationshipHistoryList() {
        return itemElementRelationshipHistoryList;
    }

    public void setItemElementRelationshipHistoryList(List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList) {
        this.itemElementRelationshipHistoryList = itemElementRelationshipHistoryList;
    }

    @XmlTransient
    public List<ItemElementRelationshipHistory> getItemElementRelationshipHistoryList1() {
        return itemElementRelationshipHistoryList1;
    }

    public void setItemElementRelationshipHistoryList1(List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList1) {
        this.itemElementRelationshipHistoryList1 = itemElementRelationshipHistoryList1;
    }

    @XmlTransient
    public List<ItemElementRelationshipHistory> getItemElementRelationshipHistoryList2() {
        return itemElementRelationshipHistoryList2;
    }

    public void setItemElementRelationshipHistoryList2(List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList2) {
        this.itemElementRelationshipHistoryList2 = itemElementRelationshipHistoryList2;
    }

    @XmlTransient
    public List<ItemElementRelationship> getItemElementRelationshipList() {
        return itemElementRelationshipList;
    }

    public void setItemElementRelationshipList(List<ItemElementRelationship> itemElementRelationshipList) {
        this.itemElementRelationshipList = itemElementRelationshipList;
    }

    @XmlTransient
    public List<ItemElementRelationship> getItemElementRelationshipList1() {
        return itemElementRelationshipList1;
    }

    public void setItemElementRelationshipList1(List<ItemElementRelationship> itemElementRelationshipList1) {
        this.itemElementRelationshipList1 = itemElementRelationshipList1;
    }

    @XmlTransient
    public List<ItemElementRelationship> getItemElementRelationshipList2() {
        return itemElementRelationshipList2;
    }

    public void setItemElementRelationshipList2(List<ItemElementRelationship> itemElementRelationshipList2) {
        this.itemElementRelationshipList2 = itemElementRelationshipList2;
    }

    public TreeNode getChildItemElementListTreeTableRootNode() {
        return childItemElementListTreeTableRootNode;
    }

    public void setChildItemElementListTreeTableRootNode(TreeNode childItemElementListTreeTableRootNode) {
        this.childItemElementListTreeTableRootNode = childItemElementListTreeTableRootNode;
    }

    public ItemElementConstraintInformation getConstraintInformation() {
        return constraintInformation;
    }

    public void setConstraintInformation(ItemElementConstraintInformation constraintInformation) {
        this.constraintInformation = constraintInformation;
    }        
    
    @Override
    public SearchResult search(Pattern searchPattern) {
        
        SearchResult searchResult;
        
        String identifier = ""; 
        
        if (name != null) {
            identifier = name;            
        } else if (derivedFromItemElement != null && derivedFromItemElement.getName() != null) {
            identifier = "Derived from: " + derivedFromItemElement.getName();            
        } else if (parentItem != null && parentItem.getName() != null) { 
            identifier = "Child of: " + parentItem.getName(); 
        }
        
        searchResult = new SearchResult(id, identifier);
        
        searchResult.doesValueContainPattern("name", name, searchPattern); 
        searchResult.doesValueContainPattern("description", description, searchPattern);
        
        if (derivedFromItemElement != null) {
            searchResult.doesValueContainPattern("derived from", derivedFromItemElement.getName(), searchPattern);
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
        if (!(object instanceof ItemElement)) {
            return false;
        }
        ItemElement other = (ItemElement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (name != null && name.isEmpty() == false) {
            return name; 
        }
        
        return "gov.anl.aps.cdb.portal.model.db.entities.ItemElement[ id=" + id + " ]";
    }   

}
