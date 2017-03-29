/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ManyToOne;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "domain_id", discriminatorType = DiscriminatorType.INTEGER)
@NamedQueries({
    @NamedQuery(name = "Item.findAll",
            query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findAllWithName",
            query = "SELECT i FROM Item i WHERE i.name != NULL"),
    @NamedQuery(name = "Item.findById",
            query = "SELECT i FROM Item i WHERE i.id = :id"),
    @NamedQuery(name = "Item.findByDerivedFromItemId",
            query = "SELECT i FROM Item i WHERE i.derivedFromItem.id = :id"),
    @NamedQuery(name = "Item.findByName",
            query = "SELECT i FROM Item i WHERE i.name = :name"),
    @NamedQuery(name = "Item.findByItemIdentifier1",
            query = "SELECT i FROM Item i WHERE i.itemIdentifier1 = :itemIdentifier1"),
    @NamedQuery(name = "Item.findByItemIdentifier2",
            query = "SELECT i FROM Item i WHERE i.itemIdentifier2 = :itemIdentifier2"),
    @NamedQuery(name = "Item.findByQrId",
            query = "SELECT i FROM Item i WHERE i.qrId = :qrId"),
    @NamedQuery(name = "Item.findByDomainName",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName"),
    @NamedQuery(name = "Item.findByDomainNameAndProject",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.itemProjectList ipl WHERE i.domain.name = :domainName and ipl.name = :projectName"),
    @NamedQuery(name = "Item.findByDomainNameWithNoParents",
            query = "SELECT i FROM Item i WHERE i.itemElementMemberList IS EMPTY and i.domain.name = :domainName"),
    @NamedQuery(name = "Item.findByDomainNameOrderByQrId",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName ORDER BY i.qrId DESC"),
    @NamedQuery(name = "Item.findByDomainNameAndProjectOrderByQrId",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.itemProjectList ipl WHERE i.domain.name = :domainName and ipl.name = :projectName ORDER BY i.qrId DESC"),
    @NamedQuery(name = "Item.findByDomainNameAndEntityType",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName"),
    @NamedQuery(name = "Item.findByDomainNameOderByQrId",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName ORDER BY i.qrId DESC"),
    @NamedQuery(name = "Item.findByDomainAndDerivedEntityTypeOrderByQrId",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.derivedFromItem.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName ORDER BY i.qrId DESC"),
    @NamedQuery(name = "Item.findItemsWithPropertyType",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel JOIN fiel.propertyValueList pvl WHERE i.domain.name = :domainName AND fiel.name is NULL and fiel.derivedFromItemElement is NULL AND pvl.propertyType.id = :propertyTypeId "),
    @NamedQuery(name = "Item.findItemsWithPropertyTypeAndProject",
            query = "Select DISTINCT(i) FROM Item i JOIN i.itemProjectList ipl JOIN i.fullItemElementList fiel JOIN fiel.propertyValueList pvl WHERE i.domain.name = :domainName AND fiel.name is NULL and fiel.derivedFromItemElement is NULL AND pvl.propertyType.id = :propertyTypeId AND ipl.name = :projectName"),
    @NamedQuery(name = "Item.findItemsOwnedByUserId",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel "
            + "WHERE i.domain.name = :domainName "
            + "AND fiel.name is NULL "
            + "AND fiel.derivedFromItemElement is NULL "
            + "AND fiel.entityInfo.ownerUser.id = :ownerUserId"),
    @NamedQuery(name = "Item.findItemsOwnedByUserGroupId",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel "
            + "WHERE i.domain.name = :domainName "
            + "AND fiel.name is NULL "
            + "AND fiel.derivedFromItemElement is NULL "
            + "AND fiel.entityInfo.ownerUserGroup.id = :ownerUserGroupId"),
    @NamedQuery(name = "Item.findItemsInList",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel "
            + "WHERE i.domain.name = :domainName "
            + "AND fiel.derivedFromItemElement is NULL "
            + "AND fiel.name is NULL "
            + "AND fiel.listList = :list "),
    @NamedQuery(name = "Item.findItemsOwnedByUserGroupIdOrInList",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel LEFT JOIN fiel.listList ieList "
            + "WHERE  fiel.name is NULL "
            + "AND fiel.derivedFromItemElement is NULL "
            + "AND (fiel.entityInfo.ownerUserGroup.id = :ownerUserGroupId "
            + "OR ieList = :list)"
            + "AND i.domain.name = :domainName"),
    @NamedQuery(name = "Item.findItemsOwnedByUserIdOrInList",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel LEFT JOIN fiel.listList ieList "
            + "WHERE  fiel.name is NULL "
            + "AND fiel.derivedFromItemElement is NULL "
            + "AND (fiel.entityInfo.ownerUser.id = :ownerUserId "
            + "OR ieList = :list)"
            + "AND i.domain.name = :domainName"),})
public class Item extends CdbDomainEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @Size(max = 64)
    private String name;
    @Size(max = 32)
    @Column(name = "item_identifier1")
    private String itemIdentifier1;
    @Size(max = 32)
    @Column(name = "item_identifier2")
    private String itemIdentifier2;
    @Column(name = "qr_id")
    private Integer qrId;
    @JoinTable(name = "item_entity_type", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "entity_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<EntityType> entityTypeList;
    @JoinTable(name = "item_item_category", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_category_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ItemCategory> itemCategoryList;
    @JoinTable(name = "item_item_type", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ItemType> itemTypeList;
    @JoinTable(name = "item_item_project", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_project_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ItemProject> itemProjectList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentItem")
    private List<ItemElement> fullItemElementList;
    @OneToMany(mappedBy = "containedItem")
    private List<ItemElement> itemElementMemberList;
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Domain domain;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "derivedFromItem")
    private List<Item> derivedFromItemList;
    @JoinColumn(name = "derived_from_item_id", referencedColumnName = "id")
    @ManyToOne
    private Item derivedFromItem;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<ItemConnector> itemConnectorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<ItemSource> itemSourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<ItemResource> itemResourceList;

    // Item element representing self 
    private transient ItemElement selfItemElement = null;
    
    // Descriptors in string format
    private transient String itemTypeString = null;
    private transient String itemCategoryString = null;
    private transient String itemSourceString = null;
    private transient String itemProjectString = null;
    private transient String qrIdDisplay = null;
       
    private transient String entityTypeString = null;

    private transient String primaryImageValue = null;   

    private transient boolean isCloned = false;

    // List of item elements that should be shown to user. 
    private transient List<ItemElement> itemElementDisplayList;  

    // Description that is list friendly (shortened if needed). 
    private transient String listDisplayDescription = null;               
    
    private transient ItemController itemDomainController = null; 
    
    private transient TreeNode assemblyRootTreeNode = null;

    public Item() {
    }

    public void init() {
        ItemElement selfElement = new ItemElement();
        selfElement.init(this);
        this.fullItemElementList = new ArrayList<>();
        this.fullItemElementList.add(selfElement);

        name = "";
        itemIdentifier1 = "";
        itemIdentifier2 = "";
    }

    public void init(Domain domain) {
        init();

        this.domain = domain;
    }
    
    // Override in sub domains 
    public Item createInstance() {
        return null; 
    }
              
    @Override
    public Item clone() throws CloneNotSupportedException {
        Item clonedItem = createInstance();
        clonedItem.isCloned = true;

        clonedItem.setDomain(this.getDomain());        
        clonedItem.entityTypeList = this.getEntityTypeList();
        clonedItem.setItemCategoryList(this.getItemCategoryList());
        clonedItem.setItemTypeList(this.getItemTypeList());
        clonedItem.setDerivedFromItem(this.getDerivedFromItem());
        clonedItem.setItemProjectList(this.getItemProjectList());

        clonedItem.setId(null);
        clonedItem.setName("(Cloned) " + this.getName());
        clonedItem.setItemIdentifier1(this.getItemIdentifier1());
        clonedItem.setItemIdentifier2(this.getItemIdentifier2());

        ItemElement newSelfElement = new ItemElement();
        ItemElement oldSelfElement = this.getSelfElement();

        newSelfElement.init(clonedItem);
        newSelfElement.setDescription(oldSelfElement.getDescription());

        clonedItem.setFullItemElementList(new ArrayList<>());
        clonedItem.resetItemElementDisplayList();
        clonedItem.resetSelfElement();
        clonedItem.fullItemElementList.add(newSelfElement);

        clonedItem.setItemSourceList(null);
        clonedItem.setQrId(null);        
        clonedItem.setPropertyValueList(null);
        clonedItem.setLogList(null);
        clonedItem.setDerivedFromItemList(null);
        clonedItem.setItemElementMemberList(null);

        clonedItem = getItemDomainController().completeClone(clonedItem, this.getId());

        return clonedItem;
    }

    public ItemController getItemDomainController() {
        if (itemDomainController == null) {
            if (domain != null) {
                String domainName = domain.getName();
                itemDomainController = ItemController.findDomainController(domainName);
            }
        }

        return itemDomainController;
    }

    public Item(Integer id) {
        this.id = id;
    }

    public Item(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void resetAttributesToNullIfEmpty() {
        if (itemIdentifier1 != null && itemIdentifier1.isEmpty()) {
            itemIdentifier1 = null;
        }
        if (itemIdentifier2 != null && itemIdentifier2.isEmpty()) {
            itemIdentifier2 = null;
        }

    }

    @Override
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

    public String getItemIdentifier1() {
        return itemIdentifier1;
    }

    public void setItemIdentifier1(String itemIdentifier1) {
        this.itemIdentifier1 = itemIdentifier1;
    }

    public String getItemIdentifier2() {
        return itemIdentifier2;
    }

    public void setItemIdentifier2(String itemIdentifier2) {
        this.itemIdentifier2 = itemIdentifier2;
    }

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    public static String formatQrIdDisplay(Integer qrId) {
        String qrIdDisplay = null;
        if (qrId != null) {
            qrIdDisplay = String.format("%09d", qrId);
            qrIdDisplay = qrIdDisplay.substring(0, 3) + " " + qrIdDisplay.substring(3, 6) + " " + qrIdDisplay.substring(6, 9);
        }
        return qrIdDisplay;
    }

    public String getQrIdDisplay() {
        if (qrId != null && qrIdDisplay == null) {
            qrIdDisplay = formatQrIdDisplay(qrId);
        }
        if (qrIdDisplay == null) {
            qrIdDisplay = "-";
        }
        return qrIdDisplay;
    }

    public String getDescription() {
        return getSelfElement().getDescription();
    }

    public void setDescription(String description) {
        this.getSelfElement().setDescription(description);
    }

    /**
     * Function is used to limit the length of description for list column.
     *
     * @return shortened description (if needed)
     */
    public String getListDisplayDescription() {
        if (listDisplayDescription == null) {
            listDisplayDescription = getDescription();
            if (listDisplayDescription != null) {
                String[] descriptionWords = listDisplayDescription.split(" ");

                listDisplayDescription = "";
                for (String descriptionWord : descriptionWords) {
                    if (descriptionWord.length() > 30) {
                        descriptionWord = " [...] ";
                    } else if (descriptionWord.length() < 30 && descriptionWord.length() > 20) {
                        descriptionWord = descriptionWord.substring(0, 20) + "...]";
                    }

                    listDisplayDescription += descriptionWord + " ";
                }

                if (listDisplayDescription.length() > 120) {
                    listDisplayDescription = listDisplayDescription.substring(0, 90);
                    listDisplayDescription += "...";
                }
            }
        }

        return listDisplayDescription;
    }

    @Override
    public List<Log> getLogList() {
        return getSelfElement().getLogList();
    }

    public void setLogList(List<Log> logList) {
        getSelfElement().setLogList(logList);
    }

    @XmlTransient
    public List<EntityType> getEntityTypeList() {
        return entityTypeList;
    }

    public List<EntityType> getEntityTypeDisplayList() {
        if ((entityTypeList == null || entityTypeList.isEmpty()) && derivedFromItem != null) {
            return derivedFromItem.entityTypeList;
        }
        return entityTypeList;
    }

    public String getEntityTypeString() {
        if (entityTypeString == null) {
            entityTypeString = "";
            List<EntityType> entityTypeDisplayList = getEntityTypeDisplayList();
            if (entityTypeDisplayList != null) {
                for (EntityType entityType : entityTypeDisplayList) {
                    if (entityTypeString.length() > 0) {
                        entityTypeString += " ";
                    }
                    entityTypeString += entityType.getName();
                }
            }
            entityTypeString = entityTypeString.replaceAll(" ", " | ");
        }

        return entityTypeString;
    }

    public String getEditEntityTypeString() {
        String entityTypeString = getEntityTypeString();

        if (entityTypeString.isEmpty()) {
            return "Select Entity Type";
        } else {
            return entityTypeString;
        }
    }

    public void setEntityTypeList(List<EntityType> entityTypeList) throws CdbException {
        if (domain != null) {
            List<EntityType> allowedEntityTypeList = domain.getAllowedEntityTypeList();
            for (EntityType entityType : entityTypeList) {
                if (allowedEntityTypeList.contains(entityType) == false) {
                    throw new CdbException(entityType.getName() + " is not in the domain hanlder allowed list for the item: " + toString());
                }
            }

        } else {
            throw new CdbException("Entity Type cannot be set: no domain has been defined for the item " + toString());
        }

        entityTypeString = null;
        this.entityTypeList = entityTypeList;
    }

    @XmlTransient
    public List<ItemCategory> getItemCategoryList() {
        return itemCategoryList;
    }

    public String getItemCategoryString() {
        if (itemCategoryString == null) {
            itemCategoryString = StringUtility.getStringifyCdbList(itemCategoryList);
        }

        return itemCategoryString;
    }

    public String getEditItemCategoryString(String itemCategoryTitle) {
        String itemCategoryString = getItemCategoryString();

        if (itemCategoryString.equals("-")) {
            return "Select " + itemCategoryTitle;
        }

        return itemCategoryString;
    }

    public void setItemCategoryList(List<ItemCategory> itemCategoryList) {
        this.itemCategoryString = null;
        this.itemCategoryList = itemCategoryList;
    }

    @XmlTransient
    public List<ItemType> getItemTypeList() {
        return itemTypeList;
    }

    public void setItemTypeList(List<ItemType> itemTypeList) {
        this.itemTypeString = null;
        this.itemTypeList = itemTypeList;
    }

    @XmlTransient
    public List<ItemProject> getItemProjectList() {
        return itemProjectList;
    }

    public void setItemProjectList(List<ItemProject> itemProjectList) {
        this.itemProjectString = null;
        this.itemProjectList = itemProjectList;
    }

    public String getItemProjectString() {
        if (itemProjectString == null) {
            itemProjectString = StringUtility.getStringifyCdbList(itemProjectList);
        }

        return itemProjectString;
    }

    public String getItemTypeString() {
        if (itemTypeString == null) {
            itemTypeString = StringUtility.getStringifyCdbList(itemTypeList);
        }

        return itemTypeString;
    }

    public String getEditItemTypeString(String itemTypeTitle) {
        String itemTypeString = getItemTypeString();

        if (itemTypeString.equals("-")) {
            return "Select " + itemTypeTitle;
        }

        return itemTypeString;
    }

    public String getEditItemProjectString() {
        String itemProjectString = getItemProjectString();

        if (itemProjectString.equals("-")) {
            return "Select Project";
        }

        return itemProjectString;
    }

    @XmlTransient
    public List<ItemElement> getFullItemElementList() {
        return fullItemElementList;
    }

    public void setFullItemElementList(List<ItemElement> fullItemElementList) {
        this.fullItemElementList = fullItemElementList;
    }

    public List<ItemElement> getItemElementDisplayList() {
        if (itemElementDisplayList == null) {
            itemElementDisplayList = new ArrayList<>(fullItemElementList);

            for (ItemElement itemElement : itemElementDisplayList) {
                if (itemElement.getName() == null) {
                    itemElementDisplayList.remove(itemElement);
                    break;
                }
            }
        }
        return itemElementDisplayList;
    }

    public void resetItemElementDisplayList() {
        itemElementDisplayList = null;
    }

    public void resetSelfElement() {
        selfItemElement = null;
    }

    public void updateDynamicProperties(UserInfo enteredByUser, Date enteredOnDateTime) {
        if (isCloned) {
            // Only update properties for non-cloned instances
            return;
        }
        List<PropertyValue> itemPropertyList = getPropertyValueList();
        if (itemPropertyList == null) {
            itemPropertyList = new ArrayList<>();
        }
        List<PropertyValue> parentItemPropertyList = derivedFromItem.getPropertyValueList();
        for (PropertyValue propertyValue : parentItemPropertyList) {
            if (propertyValue.getIsDynamic()) {
                PropertyValue propertyValue2 = propertyValue.copyAndSetUserInfoAndDate(enteredByUser, enteredOnDateTime);
                itemPropertyList.add(propertyValue2);
            }
        }
        setPropertyValueList(itemPropertyList);
    }

    public void setItemElementList(List<ItemElement> itemElementDisplayList) {
        this.itemElementDisplayList = itemElementDisplayList;
    }

    @XmlTransient
    public List<ItemElement> getItemElementMemberList() {
        return itemElementMemberList;
    }

    public void setItemElementMemberList(List<ItemElement> itemElementMemberList) {
        this.itemElementMemberList = itemElementMemberList;
    }

    @XmlTransient
    public List<Item> getDerivedFromItemList() {
        return derivedFromItemList;
    }

    public void setDerivedFromItemList(List<Item> derivedFromItemList) {
        this.derivedFromItemList = derivedFromItemList;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        itemDomainController = null;
        this.domain = domain;
    }

    public Item getDerivedFromItem() {
        return derivedFromItem;
    }

    public void setDerivedFromItem(Item derivedFromItem) {
        this.derivedFromItem = derivedFromItem;
    }

    @Override
    public EntityInfo getEntityInfo() {
        return getSelfElement().getEntityInfo();
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.getSelfElement().setEntityInfo(entityInfo);
    }

    @XmlTransient
    public List<ItemConnector> getItemConnectorList() {
        return itemConnectorList;
    }

    public void setItemConnectorList(List<ItemConnector> itemConnectorList) {
        this.itemConnectorList = itemConnectorList;
    }

    @XmlTransient
    public List<ItemSource> getItemSourceList() {
        return itemSourceList;
    }

    public void setItemSourceList(List<ItemSource> itemSourceList) {
        this.itemSourceList = itemSourceList;
    }

    public String getItemSourceString() {
        if (itemSourceString == null) {
            itemSourceString = "";
            itemSourceList.stream().forEach((itemSource) -> {
                itemSourceString += " " + itemSource.getSource().getName();
            });
        }

        return itemSourceString;
    }



    @XmlTransient
    public List<ItemResource> getItemResourceList() {
        return itemResourceList;
    }

    public void setItemResourceList(List<ItemResource> itemResourceList) {
        this.itemResourceList = itemResourceList;
    }

    public ItemElement getSelfElement() {
        if (selfItemElement == null) {
            for (ItemElement ie : this.fullItemElementList) {
                if (ie.getName() == null && ie.getDerivedFromItemElement() == null) {
                    selfItemElement = ie;
                    break;
                }
            }
        }

        return selfItemElement;
    }

    public boolean isItemElementDisplayListEmpty() {
        return getItemElementDisplayList().isEmpty();
    }

    public String getPrimaryImageValue() {
        return primaryImageValue;
    }

    public void setPrimaryImageValue(String primaryImageValue) {
        this.primaryImageValue = primaryImageValue;
    }

    public List<PropertyValue> getPropertyValueInternalList() {
        if (propertyValueInternalList == null) {
            loadPropertyValueLists();
        }
        return propertyValueInternalList;
    }

    @Override
    public List<PropertyValue> getPropertyValueList() {
        return this.getSelfElement().getPropertyValueList();
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.getSelfElement().setPropertyValueList(propertyValueList);
    }

    @Override
    public List<PropertyValue> getPropertyValueDisplayList() {
        if (propertyValueDisplayList == null) {
            loadPropertyValueLists();
        }
        return propertyValueDisplayList;
    }

    private void loadPropertyValueLists() {
        if (getPropertyValueList() != null) {
            propertyValueDisplayList = new ArrayList<>(getPropertyValueList());
            propertyValueInternalList = getInternalPropertyValues();
            propertyValueDisplayList.removeAll(propertyValueInternalList);
        } else {
            propertyValueDisplayList = new ArrayList<>();
        }
    }

    private List<PropertyValue> getInternalPropertyValues() {
        List<PropertyValue> internalPropertyValues = new ArrayList<>();
        for (PropertyValue propertyValue : getPropertyValueList()) {
            if (propertyValue.getPropertyType().getIsInternal()) {
                internalPropertyValues.add(propertyValue);
            }
        }
        return internalPropertyValues;
    }

    @Override
    public void setImagePropertyList(List<PropertyValue> imageList) {
        this.getSelfElement().setImagePropertyList(imageList);
    }

    @Override
    public List<PropertyValue> getImagePropertyList() {
        return this.getSelfElement().getImagePropertyList();
    }

    public CdbDomainEntity.PropertyValueInformation getPropertyValueInformation(int propertyTypeId) {
        return this.getSelfElement().getPropertyValueInformation(propertyTypeId);
    }

    @Override
    public String getPropertyValue1() {
        return getSelfElement().getPropertyValue1();
    }

    @Override
    public void setPropertyValue1(String propertyValue1) {
        getSelfElement().setPropertyValueByIndex(1, propertyValue1);
    }

    @Override
    public String getPropertyValue2() {
        return getSelfElement().getPropertyValue2();
    }

    @Override
    public void setPropertyValue2(String propertyValue2) {
        getSelfElement().setPropertyValueByIndex(2, propertyValue2);
    }

    @Override
    public String getPropertyValue3() {
        return getSelfElement().getPropertyValue3();
    }

    @Override
    public void setPropertyValue3(String propertyValue3) {
        getSelfElement().setPropertyValueByIndex(3, propertyValue3);
    }

    @Override
    public String getPropertyValue4() {
        return getSelfElement().getPropertyValue4();
    }

    @Override
    public void setPropertyValue4(String propertyValue4) {
        getSelfElement().setPropertyValueByIndex(4, propertyValue4);
    }

    @Override
    public String getPropertyValue5() {
        return getSelfElement().getPropertyValue5();
    }

    @Override
    public void setPropertyValue5(String propertyValue5) {
        getSelfElement().setPropertyValueByIndex(5, propertyValue5);
    }

    public boolean isIsCloned() {
        return isCloned;
    }

    public void setIsCloned(boolean isCloned) {
        this.isCloned = isCloned;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult;

        if (name != null) {
            searchResult = new SearchResult(id, name);
            searchResult.doesValueContainPattern("name", name, searchPattern);
        } else if (derivedFromItem != null && derivedFromItem.getName() != null) {
            String title = "Derived from: " + derivedFromItem.getName();
            if (qrId != null) {
                title += " (QRID: " + getQrIdDisplay() + ")";
            }
            searchResult = new SearchResult(id, title);
        } else {
            searchResult = new SearchResult(id, "Item");
        }

        if (derivedFromItem != null) {
            searchResult.doesValueContainPattern("derived from name", derivedFromItem.getName(), searchPattern);
        }

        searchResult.doesValueContainPattern("created by", getEntityInfo().getCreatedByUser().getUsername(), searchPattern);
        searchResult.doesValueContainPattern("last modified by", getEntityInfo().getLastModifiedByUser().getUsername(), searchPattern);
        searchResult.doesValueContainPattern("owned by", getEntityInfo().getOwnerUser().getUsername(), searchPattern);
        searchResult.doesValueContainPattern("description", getDescription(), searchPattern);
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
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;

        if (other == this) {
            return true;
        }

        if (other.getId() != null) {
            if (other.getId().equals(id)) {
                return true;
            }
        }

        return (Objects.equals(other.getItemIdentifier1(), itemIdentifier1)
                && Objects.equals(other.getItemIdentifier2(), itemIdentifier2)
                && Objects.equals(other.getDerivedFromItem(), derivedFromItem)
                && Objects.equals(other.getDomain(), domain)
                && Objects.equals(other.getName(), name));
    }

    @Override
    public String toString() {
        if (getName() != null && getName().isEmpty() == false) {
            if (derivedFromItem != null) {
                return derivedFromItem.toString() + " - " + getName();
            }
            return getName();
        } else if (getDerivedFromItem() != null && getDerivedFromItem().getName() != null) {
            return "derived from " + getDerivedFromItem().getName();
        } else if (getId() != null) {
            return getId().toString();
        } else {
            return "New Item";
        }
    }
    
    public TreeNode getAssemblyRootTreeNode() throws CdbException {
        if (assemblyRootTreeNode == null) {
            if (getItemElementDisplayList().size() > 0) {
                assemblyRootTreeNode = ItemElementUtility.createItemRoot(this);

            }
        }
        return assemblyRootTreeNode;
    }      

}
