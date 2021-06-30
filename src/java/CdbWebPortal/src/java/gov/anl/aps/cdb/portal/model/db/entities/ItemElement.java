/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
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
import javax.persistence.FetchType;
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
            query = "SELECT i FROM ItemElement i WHERE i.sortOrder = :sortOrder"),})
@JsonIgnoreProperties(value = {
    "itemCanHaveInventoryItem",
    "catalogDisplayString",
    "inventoryDisplayString",
    "machineDesignDisplayString",
    "constraintInformation",
    "catalogItem",
    "inventoryItem",
    "machineDesignItem",
    "temporaryIsRequiredValue",
    "customizableSortOrder",
    "childItemElementListTreeTableRootNode",
    "importParentCatalogItem",
    "importParentItem",
    "importPartDescription",
    "importPartName",
    "importPartRequired",
    "importPartCatalogItemName",
    "importPartCatalogItem",
    "importChildItem",
    "importChildItemName",
    "itemElementRelationshipList",
    "itemElementRelationshipList1",
    "itemElementRelationshipList2",
    "itemElementRelationshipHistoryList",
    "itemElementRelationshipHistoryList1",
    "itemElementRelationshipHistoryList2",
    "mdConnector",
    "mdTypeContainedItem",
    "parentItem",
    "containedItem",
    "containedItem2",
    "markedForDeletion"
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
    private boolean isRequired;
    @Column(name = "is_housed")
    private boolean isHoused = true;
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
    @ManyToMany(cascade = CascadeType.ALL)
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
    @JoinColumn(name = "contained_item_id1", referencedColumnName = "id")
    @ManyToOne()
    private Item containedItem1;
    @JoinColumn(name = "contained_item_id2", referencedColumnName = "id")
    @ManyToOne()
    private Item containedItem2;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private EntityInfo entityInfo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstItemElement")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondItemElement")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList1;
    @OneToMany(mappedBy = "linkItemElement")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList2;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstItemElement")
    private List<ItemElementRelationship> itemElementRelationshipList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondItemElement")
    private List<ItemElementRelationship> itemElementRelationshipList1;
    @OneToMany(mappedBy = "linkItemElement")
    private List<ItemElementRelationship> itemElementRelationshipList2;
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "itemElement")
    @OrderBy("enteredOnDateTime DESC")
    private List<ItemElementHistory> itemElementHistoryList;

    private static transient Integer sortByPropertyTypeId = null;
    private transient TreeNode childItemElementListTreeTableRootNode = null;
    private transient ItemElementConstraintInformation constraintInformation;
    private transient boolean markedForDeletion = false;

    // <editor-fold defaultstate="collapsed" desc="Import Variables">
    private transient Item importParentItem;
    private transient String importChildItemName;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Machine Design Element Variables"> 
    private transient Item catalogItem;
    private transient Item inventoryItem;
    private transient ItemDomainMachineDesign machineDesignItem;
    private transient String catalogDisplayString;
    private transient String inventoryDisplayString;
    private transient String machineDesignDisplayString;
    private transient boolean loadedCatalogInventoryMachineDesignItem = false;
    private transient boolean itemCanHaveInventoryItem = false;
    private transient String rowStyle;
    private transient ItemConnector mdConnector;
    // </editor-fold>

    // Helper variable used to ensure proper procedure is executed if the attribute changes. 
    private transient Boolean temporaryIsRequiredValue = null;

    public ItemElement() {
    }

    public void init(Item parentItem, ItemElement derivedFromItemElement, UserInfo userInfo) {
        init(parentItem, derivedFromItemElement, null, userInfo, userInfo.getUserGroupList().get(0));
    }

    public void init(Item parentItem, ItemElement derivedFromItemElement, EntityInfo entityInfo) {
        init(parentItem, derivedFromItemElement, entityInfo, null, null);
    }

    public void init(
            Item parentItem,
            ItemElement derivedFromItemElement,
            EntityInfo entityInfo,
            UserInfo ownerUser,
            UserGroup ownerGroup) {

        if (entityInfo == null) {
            entityInfo = EntityInfoUtility.createEntityInfo(ownerUser, ownerUser, ownerGroup);
        }
        this.setEntityInfo(entityInfo);
        this.setParentItem(parentItem);
        this.setDerivedFromItemElement(derivedFromItemElement);
    }

    public void init(Item parentItem, EntityInfo entityInfo) {
        init(parentItem, null, entityInfo);
    }

    public void init(Item parentItem, UserInfo userInfo) {
        EntityInfo ei = EntityInfoUtility.createEntityInfo(userInfo); 
        init(parentItem, ei);
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

    public Float getRelevantItemElementSortOrder() {
        if (derivedFromItemElement != null && parentItem != null) {
            String inventoryDomain = ItemDomainName.inventory.getValue();
            if (parentItem.getDomain().getName().equals(inventoryDomain)) {
                return derivedFromItemElement.getSortOrder();
            }
        }
        return getSortOrder();
    }

    public Object getCustomizableSortOrder() {
        if (sortByPropertyTypeId == null) {
            return getRelevantItemElementSortOrder();
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

    public boolean getIsHoused() {
        return isHoused;
    }

    public void setIsHoused(boolean isHoused) {
        this.isHoused = isHoused;
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
    public List<ItemElementHistory> getItemElementHistoryList() {
        return itemElementHistoryList;
    }

    public void setItemElementHistoryList(List<ItemElementHistory> itemElementHistoryList) {
        this.itemElementHistoryList = itemElementHistoryList;
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

    public Integer getParentItemId() {
        if (parentItem != null) {
            return parentItem.getId();
        }
        return null;
    }

    @XmlTransient
    public Item getParentItem() {
        return parentItem;
    }

    public void setParentItem(Item parentItem) {
        this.parentItem = parentItem;
    }

    public Integer getContainedItem1Id() {
        if (containedItem1 != null) {
            return containedItem1.getId();
        }
        return null;
    }

    @XmlTransient
    public Item getContainedItem() {
        return containedItem1;
    }

    public void setContainedItem(Item containedItem) {
        resetCatalogInventoryMachineDesingItems();
        this.containedItem1 = containedItem;
    }

    public Integer getContainedItem2Id() {
        if (containedItem2 != null) {
            return containedItem2.getId();
        }
        return null;
    }

    @XmlTransient
    public Item getContainedItem2() {
        return containedItem2;
    }

    public void setContainedItem2(Item containedItem2) {
        this.containedItem2 = containedItem2;
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

    public void setMarkedForDeletion(boolean markForDeletion) {
        this.markedForDeletion = markForDeletion;
    }

    @XmlTransient
    public boolean isMarkedForDeletion() {
        return this.markedForDeletion;
    }

    // <editor-fold defaultstate="collapsed" desc="Machine Design Logic">  
    private void resetCatalogInventoryMachineDesingItems() {
        loadedCatalogInventoryMachineDesignItem = false;
        itemCanHaveInventoryItem = false;
        catalogItem = null;
        inventoryItem = null;
        machineDesignItem = null;
        catalogDisplayString = null;
        inventoryDisplayString = null;
        machineDesignDisplayString = null;
    }

    private void loadCatalogInventoryMachineDesignItems() {
        if (!loadedCatalogInventoryMachineDesignItem) {
            if (containedItem1 != null) {
                if (containedItem1 instanceof ItemDomainMachineDesign) {
                    ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) containedItem1;
                    Item assignedItem = mdItem.getAssignedItem();
                    if (assignedItem != null) {
                        Domain domain = assignedItem.getDomain();
                        switch (domain.getId()) {
                            case ItemDomainName.CATALOG_ID:
                                catalogItem = assignedItem;
                                machineDesignDisplayString = "N/A";
                                catalogDisplayString = catalogItem.toString();
                                itemCanHaveInventoryItem = true;
                                break;
                            case ItemDomainName.INVENTORY_ID:
                                inventoryItem = assignedItem;
                                catalogItem = assignedItem.getDerivedFromItem();
                                machineDesignDisplayString = "N/A";
                                catalogDisplayString = catalogItem.toString();
                                inventoryDisplayString = inventoryItem.getName();
                                itemCanHaveInventoryItem = true;
                                break;
                            case ItemDomainName.MACHINE_DESIGN_ID:
                                machineDesignItem = (ItemDomainMachineDesign) containedItem1;
                                machineDesignDisplayString = machineDesignItem.toString();
                                catalogDisplayString = "N/A";
                                inventoryDisplayString = "N/A";
                                itemCanHaveInventoryItem = false;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            loadedCatalogInventoryMachineDesignItem = true;
        }
    }

    public boolean getItemCanHaveInventoryItem() {
        return itemCanHaveInventoryItem;
    }

    public Item getCatalogItem() {
        loadCatalogInventoryMachineDesignItems();
        return catalogItem;
    }

    public Item getInventoryItem() {
        loadCatalogInventoryMachineDesignItems();
        return inventoryItem;
    }

    public void setInventoryItem(Item inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public ItemDomainMachineDesign getMachineDesignItem() {
        loadCatalogInventoryMachineDesignItems();
        return machineDesignItem;
    }

    public Item getMdTypeContainedItem() {
        if (getContainedItem() instanceof ItemDomainMachineDesign) {
            return getContainedItem();
        }
        return null;
    }

    public String getCatalogDisplayString() {
        loadCatalogInventoryMachineDesignItems();
        return catalogDisplayString;
    }

    public String getInventoryDisplayString() {
        loadCatalogInventoryMachineDesignItems();
        return inventoryDisplayString;
    }

    public String getMachineDesignDisplayString() {
        loadCatalogInventoryMachineDesignItems();
        return machineDesignDisplayString;
    }

    public String getRowStyle() {
        return rowStyle;
    }

    public void setRowStyle(String rowStyle) {
        this.rowStyle = rowStyle;
    }

    public ItemConnector getMdConnector() {
        return mdConnector;
    }

    public void setMdConnector(ItemConnector mdConnector) {
        this.mdConnector = mdConnector;
    }

    // </editor-fold>
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

        searchResult = new SearchResult(this, id, identifier);

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
        if (other.getId() != null) {
            return (other.getId().equals(id));
        }

        // special case for import
        if ((this.importParentItem != null)
                && (other.importParentItem != null)) {
            if ((this.importParentItem == other.importParentItem)
                    && (this.getName().equals(other.getName()))) {
                return true;
            } else {
                return false;
            }
        }

        // Verify other metadata 
        if (ObjectUtility.equals(this.getName(), other.getName())
                && ObjectUtility.equals(this.getParentItem(), other.getParentItem())
                && ObjectUtility.equals(this.getContainedItem(), other.getContainedItem())
                && ObjectUtility.equals(this.getContainedItem2(), other.getContainedItem2())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean equalsByIdContainedItemsAndParentItemAndHousing(ItemElement other) {
        if (this.equals(other)) {
            return nullSafeComparison(this.containedItem1, other.containedItem1)
                    && nullSafeComparison(this.containedItem2, other.containedItem2)
                    && nullSafeComparison(this.parentItem, other.parentItem)
                    && nullSafeComparison(this.isHoused, other.isHoused);
        }
        return false;
    }

    private boolean nullSafeComparison(Object object1, Object object2) {
        if (object1 == null && object2 == null) {
            return true;
        } else if (object1 == null || object2 == null) {
            return false;
        } else {
            return object1.equals(object2);
        }
    }

    @Override
    public String toString() {
        if (name != null && name.isEmpty() == false) {
            return name;
        }

        return "gov.anl.aps.cdb.portal.model.db.entities.ItemElement[ id=" + id + " ]";
    }

    // <editor-fold defaultstate="collapsed" desc="import functionality">
    public void setImportParentItem(Item parentItem) {
        setImportParentItem(parentItem, null, null, null);
    }

    public Item getImportParentItem() {
        return getParentItem();
    }

    public void setImportParentItem(Item parentItem, Float sortOrder, UserInfo user, UserGroup group) {

        if (parentItem != null) {
            setParentItem(parentItem);
            parentItem.getFullItemElementList().add(this);
            parentItem.getItemElementDisplayList().add(0, this);

            if (sortOrder == null) {
                int elementSize = parentItem.getItemElementDisplayList().size();
                float sOrder = elementSize;
                this.setSortOrder(sOrder);
            } else {
                this.setSortOrder(sortOrder);
            }

            EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
            if (user != null) {
                entityInfo.setOwnerUser(user);
            }
            if (group != null) {
                entityInfo.setOwnerUserGroup(group);
            }
            this.setEntityInfo(entityInfo);
        }

        importParentItem = parentItem;
    }

    public String getImportPartName() {
        return getName();
    }

    public void setImportPartName(String partName) {
        setName(partName);
    }

    public String getImportPartDescription() {
        return getDescription();
    }

    public void setImportPartDescription(String partDescription) {
        this.setDescription(partDescription);
    }

    public Boolean getImportPartRequired() {
        return this.getIsRequired();
    }

    public void setImportPartRequired(Boolean partRequired) {
        this.setIsRequired(partRequired);
    }

    public String getImportChildItemName() {
        return importChildItemName;
    }

    public void setImportChildItemName(String partCatalogItemName) {
        this.importChildItemName = partCatalogItemName;
    }

    public ItemDomainCatalog getImportChildItem() {
        return (ItemDomainCatalog) this.getContainedItem();
    }

    public void setImportChildItem(Item childItem) {
        this.setContainedItem(childItem);
        if (childItem.getItemElementMemberList() == null) {
            childItem.setItemElementMemberList(new ArrayList<>());
        }
        childItem.getItemElementMemberList().add(this);
    }
    // </editor-fold>
}
