/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.utilities.EntityTypeControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    @NamedQuery(name = "Item.findByDomainNameAndName",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName AND i.name = :name"),
    @NamedQuery(name = "Item.findByDomainNameAndNameExcludeEntityType",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName AND i.name = :name AND (i.id not in (SELECT DISTINCT(i.id) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :excludeEntityTypeName))"),
    @NamedQuery(name = "Item.findByDomainNameAndEntityTypeAndNameExcludeEntityType",
            query = "SELECT DISTINCT(i) FROM Item i WHERE i.name = :name AND i.domain.name = :domainName AND (i.id in (SELECT DISTINCT(i.id) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName)) AND (i.id not in (SELECT DISTINCT(i.id) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :excludeEntityTypeName))"),
    @NamedQuery(name = "Item.findByItemIdentifier1",
            query = "SELECT i FROM Item i WHERE i.itemIdentifier1 = :itemIdentifier1"),
    @NamedQuery(name = "Item.findByItemIdentifier2",
            query = "SELECT i FROM Item i WHERE i.itemIdentifier2 = :itemIdentifier2"),
    @NamedQuery(name = "Item.findByQrId",
            query = "SELECT i FROM Item i WHERE i.qrId = :qrId"),
    @NamedQuery(name = "Item.findByDomainName",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName ORDER BY i.name ASC"),
    @NamedQuery(name = "Item.findByDomainNameAndProject",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.itemProjectList ipl WHERE i.domain.name = :domainName and ipl.name = :projectName ORDER BY i.name ASC"),
    @NamedQuery(name = "Item.findByDomainNameAndProjectExcludeEntityType",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.itemProjectList ipl LEFT JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and ipl.name = :projectName AND (etl.name != :entityTypeName or etl.name is null) ORDER BY i.name ASC"),
    @NamedQuery(name = "Item.findByDomainNameWithNoParents",
            query = "SELECT i FROM Item i WHERE i.itemElementMemberList IS EMPTY and i.domain.name = :domainName"),
    @NamedQuery(name = "Item.findByDomainNameWithNoParentsAndEntityType",
            query = "SELECT i FROM Item i WHERE i.itemElementMemberList IS EMPTY and i.domain.name = :domainName and i.entityTypeList is EMPTY"),
    @NamedQuery(name = "Item.findByDomainNameWithNoParentsAndWithEntityType",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.itemElementMemberList IS EMPTY AND i.domain.name = :domainName and etl.name = :entityTypeName"),
    @NamedQuery(name = "Item.findByDomainNameOrderByQrId",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName ORDER BY i.qrId DESC"),
    @NamedQuery(name = "Item.findByDomainNameOrderByDerivedFromItem",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName ORDER BY i.derivedFromItem DESC"),
    @NamedQuery(name = "Item.findByDomainNameOrderByDerivedFromItemAndItemName",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName ORDER BY i.derivedFromItem ASC, i.name ASC"),
    @NamedQuery(name = "Item.findByDomainNameAndProjectOrderByQrId",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.itemProjectList ipl WHERE i.domain.name = :domainName and ipl.name = :projectName ORDER BY i.qrId DESC"),
    @NamedQuery(name = "Item.findByDomainNameAndProjectOrderByDerivedFromItem",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.itemProjectList ipl WHERE i.domain.name = :domainName and ipl.name = :projectName ORDER BY i.derivedFromItem DESC"),
    @NamedQuery(name = "Item.findByDomainNameAndEntityType",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName"),
    @NamedQuery(name = "Item.findByDomainNameAndEntityTypeAndTopLevel",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName and i.itemElementMemberList IS EMPTY AND i.itemElementMemberList2 IS EMPTY"),
    @NamedQuery(name = "Item.findByDomainNameAndEntityTypeAndTopLevelOrderByDerivedFromItem",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName and i.itemElementMemberList IS EMPTY AND i.itemElementMemberList2 IS EMPTY ORDER BY i.derivedFromItem.id DESC"),
    @NamedQuery(name = "Item.findByDomainNameAndEntityTypeAndTopLevelExcludeEntityTypeOrderByDerivedFromItem",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName and (i.id not in (SELECT DISTINCT(i.id) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :excludeEntityTypeName)) and i.itemElementMemberList IS EMPTY AND i.itemElementMemberList2 IS EMPTY ORDER BY i.derivedFromItem.id DESC"),
    @NamedQuery(name = "Item.findByDomainNameAndExcludeEntityType",
            query = "SELECT DISTINCT(i) FROM Item i LEFT JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and (etl.name != :entityTypeName or etl.name is null) ORDER BY i.name ASC"),
    @NamedQuery(name = "Item.findByDomainNameOderByQrId",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName ORDER BY i.qrId DESC"),
    @NamedQuery(name = "Item.findByDomainAndDerivedEntityTypeOrderByQrId",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.derivedFromItem.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName ORDER BY i.qrId DESC"),
    @NamedQuery(name = "Item.findItemsWithPropertyType",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel JOIN fiel.propertyValueList pvl WHERE i.domain.name = :domainName AND fiel.name is NULL and fiel.derivedFromItemElement is NULL AND pvl.propertyType.id = :propertyTypeId ORDER BY i.name ASC"),
    @NamedQuery(name = "Item.findItemsWithPropertyTypeExcludeEntityType",
            query = "Select DISTINCT(i) FROM Item i LEFT JOIN i.entityTypeList etl JOIN i.fullItemElementList fiel JOIN fiel.propertyValueList pvl WHERE i.domain.name = :domainName AND fiel.name is NULL and fiel.derivedFromItemElement is NULL AND pvl.propertyType.id = :propertyTypeId AND (etl.name != :entityTypeName or etl.name is null) ORDER BY i.name ASC"),
    @NamedQuery(name = "Item.findItemsWithPropertyTypeAndProject",
            query = "Select DISTINCT(i) FROM Item i JOIN i.itemProjectList ipl JOIN i.fullItemElementList fiel JOIN fiel.propertyValueList pvl WHERE i.domain.name = :domainName AND fiel.name is NULL and fiel.derivedFromItemElement is NULL AND pvl.propertyType.id = :propertyTypeId AND ipl.name = :projectName ORDER BY i.name ASC"),
    @NamedQuery(name = "Item.findItemsWithPropertyTypeAndProjectExcludeEntityType",
            query = "Select DISTINCT(i) FROM Item i JOIN i.itemProjectList ipl LEFT JOIN i.entityTypeList etl JOIN i.fullItemElementList fiel JOIN fiel.propertyValueList pvl WHERE i.domain.name = :domainName AND fiel.name is NULL and fiel.derivedFromItemElement is NULL AND pvl.propertyType.id = :propertyTypeId AND ipl.name = :projectName and (etl.name != :entityTypeName or etl.name is null) ORDER BY i.name ASC"),
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
    @NamedQuery(name = "Item.findItemsInListExcludeEntityType",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel LEFT JOIN i.entityTypeList etl "
            + "WHERE i.domain.name = :domainName "
            + "AND (etl.name != :entityTypeName or etl.name is null) "
            + "AND fiel.derivedFromItemElement is NULL "
            + "AND fiel.name is NULL "
            + "AND fiel.listList = :list "),
    @NamedQuery(name = "Item.findItemsInListWithoutEntityType",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel "
            + "WHERE i.domain.name = :domainName "
            + "AND i.entityTypeList IS EMPTY "
            + "AND fiel.derivedFromItemElement is NULL "
            + "AND fiel.name is NULL "
            + "AND fiel.listList = :list "),
    @NamedQuery(name = "Item.findItemsInListWithEntityType",
            query = "Select DISTINCT(i) FROM Item i JOIN i.fullItemElementList fiel JOIN i.entityTypeList etl "
            + "WHERE i.domain.name = :domainName "
            + "AND etl.name = :entityTypeName "
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
            + "AND i.domain.name = :domainName")
})
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
            name = "item.itemWithWritePermissionsForUser",
            procedureName = "items_with_write_permission_for_user",
            resultClasses = Item.class,
            parameters = {
                @StoredProcedureParameter(
                        name = "user_id",
                        mode = ParameterMode.IN,
                        type = Integer.class
                ),
                @StoredProcedureParameter(
                        name = "domain_id",
                        mode = ParameterMode.IN,
                        type = Integer.class
                )
            }
    ),})

@JsonIgnoreProperties(value = {
    // Transient
    "assemblyRootTreeNode",
    "itemTypeString",
    "itemCategoryString",
    "itemSourceString",
    "itemProjectString",
    "qrIdDisplay",
    "qrIdFilter",
    "itemCableConnectionsRelationshipList",
    "editEntityTypeString",
    "editItemProjectString",
    "logList",
    "itemElementDisplayList",
    "itemElementDisplayListEmpty",
    "itemElementRelationshipList",
    "itemElementRelationshipList1",
    "itemElementRelationshipList2",
    "fullRelationshipList", 
    "itemElementMemberList",
    "itemElementMemberList2",
    "historyMemberList",
    "historyMemberList2",
    "hierarchyItemElement",
    "fullItemElementList",
    "derivedFromItemList",
    "entityTypeString",
    "entityTypeDisplayList",
    "listDisplayDescription",
    "primaryImageValue",
    "availableItemTypes",
    "lastKnownItemCategoryList",
    "isItemTemplate",
    "selfElement",
    "descriptionFromAPI",
    "coreMetadataPropertyValue",
    "coreMetadataPropertyInfo",
    "coreMetadataPropertyType", 
    "PropertyValueList",
    "templateInfoLoaded",
    "createdFromTemplate",
    "itemsCreatedFromThisTemplateItem"
})
@Schema(name = "Item",
        subTypes
        = {
            ItemDomainCatalog.class,
            ItemDomainInventory.class,
            ItemDomainMachineDesign.class,
            ItemDomainCableCatalog.class,
            ItemDomainCableInventory.class,
            ItemDomainCableDesign.class,
            ItemDomainMAARC.class,
            ItemDomainLocation.class
        }
)
public class Item extends CdbDomainEntity implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(Item.class.getName());
    
    public static final String ATTRIBUTE_DOMAIN_NAME = "domainName";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_QR_ID = "qrId";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @Size(max = 128)
    private String name;
    @Size(max = 128)
    @Column(name = "item_identifier1")
    private String itemIdentifier1;
    @Size(max = 128)
    @Column(name = "item_identifier2")
    private String itemIdentifier2;
    @Column(name = "qr_id")
    @Min(0)
    private Integer qrId;
    @JoinTable(name = "item_entity_type", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "entity_type_id", referencedColumnName = "id")})
    @ManyToMany
    @JsonProperty("entityTypeList")
    private List<EntityType> entityTypeList;
    @JoinTable(name = "item_item_category", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_category_id", referencedColumnName = "id")})
    @ManyToMany
    @JsonProperty("itemCategoryList")
    private List<ItemCategory> itemCategoryList;
    @JoinTable(name = "item_item_type", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_type_id", referencedColumnName = "id")})
    @ManyToMany
    @JsonProperty("itemTypeList")
    private List<ItemType> itemTypeList;
    @JoinTable(name = "item_item_project", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_project_id", referencedColumnName = "id")})
    @ManyToMany
    @JsonProperty("itemProjectList")
    private List<ItemProject> itemProjectList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentItem", orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ItemElement> fullItemElementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "containedItem1")
    private List<ItemElement> itemElementMemberList;
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "containedItem2")
    private List<ItemElement> itemElementMemberList2;
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
    @OneToMany(mappedBy = "containedItem1")
    private List<ItemElementHistory> historyMemberList;
    @OneToMany(mappedBy = "containedItem2")
    private List<ItemElementHistory> historyMemberList2;

    // Item element representing self 
    private transient ItemElement selfItemElement = null;

    // Descriptors in string format
    private transient String itemTypeString = null;
    private transient String itemCategoryString = null;
    private transient String itemSourceString = null;
    private transient String itemProjectString = null;
    private transient String qrIdDisplay = null;
    private transient String qrIdFilter = null;

    private transient String entityTypeString = null;

    private transient String primaryImageValue = null;

    private transient boolean isCloned = false;

    // List of item elements that should be shown to user. 
    private transient List<ItemElement> itemElementDisplayList;

    // Description that is list friendly (shortened if needed). 
    private transient String listDisplayDescription = null;

    private transient ItemController itemDomainController = null;

    private transient TreeNode assemblyRootTreeNode = null;

    private transient List<ItemType> availableItemTypes = null;
    private transient List<ItemCategory> lastKnownItemCategoryList = null;

    private transient Boolean isItemTemplate = null;
    private transient Boolean templateInfoLoaded = false;
    private transient Item createdFromTemplate = null;
    private transient List<Item> itemsCreatedFromThisTemplateItem = null;

    private transient Boolean isItemDeleted = null;
    
    private transient Boolean isItemInventory = null;
    
    // Item element from which it was added to in the hierarchy. 
    private transient ItemElement hierarchyItemElement = null;

    // API generation variables    
    private transient String descriptionFromAPI;

    protected transient ItemMetadataPropertyInfo coreMetadataPropertyInfo = null;
    protected transient PropertyType coreMetadataPropertyType = null;    
    protected transient PropertyValue coreMetadataPropertyValue = null;
    
    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">
    protected transient ItemElement currentEditItemElement = null;
    protected transient Boolean currentEditItemElementSaveButtonEnabled = false;
    protected transient ItemSource currentEditItemSource = null;
    protected transient Boolean hasElementReorderChangesForCurrent = false;
    // </editor-fold>

    public Item() {
    }
    
    public void init(EntityInfo entityInfo) {
        ItemElement selfElement = new ItemElement();        
        selfElement.init(this, entityInfo);
        
        this.fullItemElementList = new ArrayList<>();
        this.fullItemElementList.add(selfElement);

        name = "";
        itemIdentifier1 = "";
        itemIdentifier2 = "";
    }

    public void init(Domain domain, EntityInfo ei) {
        init(ei);

        this.domain = domain;
    }   

    // Override in sub domains 
    public Item createInstance() {
        return null;
    }

    @Override
    public Item clone(UserInfo userInfo) throws CloneNotSupportedException {
        UserGroup firstGroup = userInfo.getUserGroupList().get(0); 
        return clone(userInfo, firstGroup);
    }

    public Item clone(UserInfo ownerUser, UserGroup ownerGroup) throws CloneNotSupportedException {
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

        newSelfElement.init(clonedItem, null, null, ownerUser, ownerGroup);
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
    
    @JsonIgnore      
    public ItemControllerUtility getItemControllerUtility() {
        return null; 
    }
    

    @JsonIgnore
    public ItemController getItemDomainController() {
        if (itemDomainController == null) {
            itemDomainController = ItemController.findDomainControllerForItem(this);
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
        this.qrIdDisplay = null;
        this.qrIdFilter = null;
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

    public String getQrIdFilter() {
        if (qrIdFilter == null) {
            String dispQr = getQrIdDisplay();
            qrIdFilter = dispQr + " " + qrId + " " + dispQr.replace(" ", "");
        }
        return qrIdFilter;
    }
    
    @JsonIgnore
    public Map<String,String> getAttributeMap() {
        Map<String, String> attributeMap = new HashMap<>();
        
        Domain domain = getDomain();
        attributeMap.put(ATTRIBUTE_DOMAIN_NAME, domain.getName());
        
        attributeMap.put(ATTRIBUTE_NAME, getName());
        
        if (domain.getItemIdentifier1Label() != null) {
            attributeMap.put(domain.getItemIdentifier1Label(), getItemIdentifier1());
        }
        
        if (domain.getItemIdentifier2Label() != null) {
            attributeMap.put(domain.getItemIdentifier2Label(), getItemIdentifier2());
        }
        
        if (getQrId() != null) {
            attributeMap.put(ATTRIBUTE_QR_ID, String.valueOf(getQrId()));
        }
        
        return attributeMap;
    }

    @Size(max = 256)
    public String getDescription() {
        return getSelfElement().getDescription();
    }

    public void setDescription(String description) {
        this.getSelfElement().setDescription(description);
    }

    public String getDescriptionFromAPI() {
        return descriptionFromAPI;
    }

    @JsonSetter("description")
    public void setDescriptionFromApi(String descriptionTmp) {
        this.descriptionFromAPI = descriptionTmp;
    }

    public List<ItemElementRelationship> getItemElementRelationshipList() {
        return getSelfElement().getItemElementRelationshipList();
    }

    public void setItemElementRelationshipList(List<ItemElementRelationship> itemElementRelationshipList) {
        getSelfElement().setItemElementRelationshipList(itemElementRelationshipList);
    }

    public List<ItemElementRelationship> getItemElementRelationshipList1() {
        return getSelfElement().getItemElementRelationshipList1();
    }

    public void setItemElementRelationshipList1(List<ItemElementRelationship> itemElementRelationshipList1) {
        getSelfElement().setItemElementRelationshipList1(itemElementRelationshipList1);
    }

    public List<ItemElementRelationship> getItemElementRelationshipList2() {
        return getSelfElement().getItemElementRelationshipList2();
    }

    public void setItemElementRelationshipList2(List<ItemElementRelationship> itemElementRelationshipList2) {
        getSelfElement().setItemElementRelationshipList2(itemElementRelationshipList2);
    }
    
    /**
     * Returns merged list of itemElementRelationshipList, itemElementRelationshipList1, itemElementRelationshipList2.
     */
    public List<ItemElementRelationship> getFullRelationshipList() {
        
        List<ItemElementRelationship> fullList = new ArrayList<>();
        
        List<ItemElementRelationship> ierList = getItemElementRelationshipList();
        if (ierList != null) {
            fullList.addAll(ierList);
        }
        
        List<ItemElementRelationship> ierList1 = getItemElementRelationshipList1();
        if (ierList1 != null) {
            fullList.addAll(ierList1);
        }
        
        List<ItemElementRelationship> ierList2 = getItemElementRelationshipList2();
        if (ierList2 != null) {
            fullList.addAll(ierList2);
        }
        
        return fullList;
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
                for (int i = 0; i < entityTypeDisplayList.size(); i++) {
                    EntityType entityType = entityTypeDisplayList.get(i);
                    boolean lastIdx = i == entityTypeDisplayList.size() - 1;
                    if (entityTypeString.length() > 0 && lastIdx) {
                        entityTypeString += " ";
                    }
                    entityTypeString += entityType.getName();
                }
            }
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
    
    private EntityType findEntityTypeByName(String name) {
        EntityTypeControllerUtility ecu = new EntityTypeControllerUtility(); 
        return ecu.findByName(name); 
    }
    
    public void addEntityType(String entityTypeName) throws CdbException {        
        EntityType entityType = findEntityTypeByName(entityTypeName); 
        
        // entity type already set for this entity
        if (entityTypeList.contains(entityType)) {
            return;
        }

        // check to see that entity type is valid for entity's domain
        if (domain != null) {
            List<EntityType> allowedEntityTypeList = domain.getAllowedEntityTypeList();
            if (allowedEntityTypeList.contains(entityType) == false) {
                throw new CdbException(entityType.getName() + " is not in the domain hanlder allowed list for the item: " + toString());
            }

        } else {
            throw new CdbException("Entity Type cannot be set: no domain has been defined for the item " + toString());
        }

        // add entity type to entity type list
        List<EntityType> entityTypeList = getEntityTypeList();
        if ( entityTypeList == null) {
            entityTypeList = new ArrayList<>();
        }
        entityTypeList.add(entityType);

    }
    
    public void removeEntityType(String entityTypeName) {
        EntityType entityType = findEntityTypeByName(entityTypeName); 
        
        if (entityType == null) {
            return;
        }
        
        if (!entityTypeList.contains(entityType)) {
            return;
        }
        
        getEntityTypeList().remove(entityType);
    }

    @JsonSetter("entityTypeList")
    public void setEntityTypeListFromApi(List<EntityType> entityTypeList) {
        this.entityTypeList = entityTypeList;
    }

    @XmlTransient    
    public List<ItemCategory> getItemCategoryList() {
        return itemCategoryList;
    }
    
    @JsonIgnore
    public List<String> getItemCategoryNameList() {
        List<String> result = new ArrayList<>();
        for (ItemCategory category : getItemCategoryList()) {
            result.add(category.getName());
        }
        return result;
    }

    @JsonIgnore
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

    public void setItemCategoryListImport(List<ItemCategory> itemCategoryList) {
        if (itemCategoryList != null) {
            this.itemCategoryString = null;
            this.itemCategoryList = itemCategoryList;
        } else if (this.itemCategoryList != null) {
            // if the new list value is null, but the old value is not null, then clear the list
            this.itemCategoryList.clear();
        }
    }

    @XmlTransient
    public List<ItemType> getItemTypeList() {
        return itemTypeList;
    }
    
    @JsonIgnore
    public List<String> getItemTypeNameList() {
        List<String> result = new ArrayList<>();
        for (ItemType type : getItemTypeList()) {
            result.add(type.getName());
        }
        return result;
    }
   
    public void setItemTypeList(List<ItemType> itemTypeList) {
        this.itemTypeString = null;
        this.itemTypeList = itemTypeList;
    }
        
    @JsonIgnore
    public void setItemType(ItemType itemType) {
        List<ItemType> itList = new ArrayList<>();
        itList.add(itemType);
        setItemTypeList(itList);
    }

    @XmlTransient    
    public List<ItemProject> getItemProjectList() {
        return itemProjectList;
    }
    
    @JsonIgnore    
    public List<String> getItemProjectNameList() {
        List<String> projectNames = new ArrayList<>();
        for (ItemProject project : getItemProjectList()) {
            projectNames.add(project.getName());
        }
        return projectNames;
    }
    
    public void setItemProjectList(List<ItemProject> itemProjectList) {
        this.itemProjectString = null;
        this.itemProjectList = itemProjectList;
    }
    
    @JsonIgnore
    public void setProject(ItemProject project) {
        if (project != null) {
            List<ItemProject> projectList = null;
            if (this.getItemProjectList() == null) {
                projectList = new ArrayList<>();
                this.setItemProjectList(projectList);
            } else {
                projectList = getItemProjectList();
            }
            projectList.add(project);
        }
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

    public void removeItemElement(ItemElement itemElement) {
        fullItemElementList.remove(itemElement);
        resetItemElementVars();
    }

    public void resetItemElementVars() {
        resetItemElementDisplayList();
        resetSelfElement();
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
    
    @JsonIgnore
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
    public List<ItemElement> getItemElementMemberList2() {
        return itemElementMemberList2;
    }

    public void setItemElementMemberList2(List<ItemElement> itemElementMemberList2) {
        this.itemElementMemberList2 = itemElementMemberList2;
    }

    @XmlTransient
    public List<ItemElementHistory> getHistoryMemberList() {
        return historyMemberList;
    }

    @XmlTransient
    public List<ItemElementHistory> getHistoryMemberList2() {
        return historyMemberList2;
    }

    @XmlTransient
    public List<Item> getDerivedFromItemList() {
        return derivedFromItemList;
    }

    public void setDerivedFromItemList(List<Item> derivedFromItemList) {
        this.derivedFromItemList = derivedFromItemList;
    }
    
    public Integer getDomainId() {
        return domain.getId(); 
    }

//    TODO v3.13.0 update app to utilize the domainID 
//    @JsonIgnore
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
    @JsonIgnore
    public EntityInfo getEntityInfo() {
        return getSelfElement().getEntityInfo();
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.getSelfElement().setEntityInfo(entityInfo);
    }
    
    @JsonIgnore
    public String getOwnerDisplayName() {
        return this.getEntityInfo().getOwnerUserDisplayName();
    }
    
    @JsonIgnore
    public String getOwnerUsername() {
        return this.getEntityInfo().getOwnerUsername();
    }
    
    @JsonIgnore
    public UserInfo getOwnerUser() {
        return this.getEntityInfo().getOwnerUser();
    }
    
    public void setOwnerUser(UserInfo ownerUser) {
        this.getEntityInfo().setOwnerUser(ownerUser);
    }
    
    @JsonIgnore
    public String getOwnerUserGroupName() {
        return this.getEntityInfo().getOwnerGroupDisplayName();
    }
    
    @JsonIgnore
    public UserGroup getOwnerUserGroup() {
        return this.getEntityInfo().getOwnerUserGroup();
    }
    
    public void setOwnerUserGroup(UserGroup ownerUserGroupId) {
        this.getEntityInfo().setOwnerUserGroup(ownerUserGroupId);
    }

    @XmlTransient
    @JsonIgnore
    public List<ItemConnector> getItemConnectorList() {
        return itemConnectorList;
    }
    
    @JsonIgnore
    public List<ItemConnector> getItemConnectorListSorted() {
        
        Collections.sort(itemConnectorList, (c1, c2) -> {
            
            String c1End = "";
            String c2End = "";
            if ((c1.getConnector() != null) && (c1.getConnector().getCableEndDesignation() != null)) {
                c1End = c1.getConnector().getCableEndDesignation();
            }
            if ((c2.getConnector() != null) && (c2.getConnector().getCableEndDesignation() != null)) {
                c2End = c2.getConnector().getCableEndDesignation();
            }
            if (!c1End.equals(c2End)) {
                return c1End.compareTo(c2End);
            } else {
                String c1Name = "";
                String c2Name = "";
                if ((c1.getConnector() != null) && (c1.getConnector().getName() != null)) {
                    c1Name = c1.getConnector().getName();
                }
                if ((c2.getConnector() != null) && (c2.getConnector().getName() != null)) {
                    c2Name = c2.getConnector().getName();
                }
                return c1Name.compareTo(c2Name);
            }
        });
        return itemConnectorList;
    }

    public void setItemConnectorList(List<ItemConnector> itemConnectorList) {
        this.itemConnectorList = itemConnectorList;
    }
    
    public ItemConnector getConnectorNamed(String connectorName) {
        List<ItemConnector> connectorList = getItemConnectorList();
        if (connectorList == null) {
            return null;
        }
        for (ItemConnector itemConnector : connectorList) {
            Connector connector = itemConnector.getConnector();
            if (connector != null) {
                String name = connector.getName();
                if (name.equals(connectorName)) {
                    return itemConnector;
                }
            }
        }
        return null;
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

    public List<ItemType> getAvailableItemTypes() {
        return availableItemTypes;
    }

    public void setAvailableItemTypes(List<ItemType> availableItemTypes) {
        this.availableItemTypes = availableItemTypes;
    }

    public List<ItemCategory> getLastKnownItemCategoryList() {
        return lastKnownItemCategoryList;
    }

    public void setLastKnownItemCategoryList(List<ItemCategory> lastKnownItemCategoryList) {
        this.lastKnownItemCategoryList = lastKnownItemCategoryList;
    }

    @XmlTransient
    @JsonIgnore
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
    public void addPropertyValueToPropertyValueList(PropertyValue propertyValue) {
        getSelfElement().addPropertyValueToPropertyValueList(propertyValue);
    }

    @Override
    @JsonIgnore
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

    public Item getCreatedFromTemplate() {
        if (!templateInfoLoaded) {
            if (!getIsItemTemplate()) {

                String machineDesignTemplateRelationshipTypeName = ItemElementRelationshipTypeNames.template.getValue();
                if (getItemElementRelationshipList() != null) {
                    for (ItemElementRelationship ier : getItemElementRelationshipList()) {
                        if (ier.getRelationshipType().getName().equals(machineDesignTemplateRelationshipTypeName)) {
                            createdFromTemplate = ier.getSecondItemElement().getParentItem();
                        }
                    }
                }

                templateInfoLoaded = true;
            }
        }
        return createdFromTemplate;
    }

    public List<Item> getItemsCreatedFromThisTemplateItem() {
        if (!templateInfoLoaded) {
            if (getIsItemTemplate()) {

                String machineDesignTemplateRelationshipTypeName = ItemElementRelationshipTypeNames.template.getValue();
                itemsCreatedFromThisTemplateItem = new ArrayList<>();
                if (getItemElementRelationshipList1() != null) {
                    for (ItemElementRelationship ier : getItemElementRelationshipList1()) {
                        if (ier.getRelationshipType().getName().equals(machineDesignTemplateRelationshipTypeName)) {
                            Item parentItem = ier.getFirstItemElement().getParentItem();
                            itemsCreatedFromThisTemplateItem.add(parentItem);
                        }
                    }
                }
                templateInfoLoaded = true;
            }
        }
        return itemsCreatedFromThisTemplateItem;
    }

    public ItemElement getHierarchyItemElement() {
        return hierarchyItemElement;
    }

    public void setHierarchyItemElement(ItemElement hierarchyItemElement) {
        this.hierarchyItemElement = hierarchyItemElement;
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

    @JsonIgnore
    public boolean isIsCloned() {
        return isCloned;
    }

    public void setIsCloned(boolean isCloned) {
        this.isCloned = isCloned;
    }

    public String getPrimaryImageForItem() {
        PropertyValue primaryImagePropertyValueForItem = ItemController.getPrimaryImagePropertyValueForItem(this);
        if (primaryImagePropertyValueForItem != null) {
            return primaryImagePropertyValueForItem.getValue();
        }
        return null;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult;

        if (name != null) {
            searchResult = new SearchResult(this, id, name);
            searchResult.doesValueContainPattern("name", name, searchPattern);
            searchResult.doesValueContainPattern("item identifier 1", itemIdentifier1, searchPattern); 
            searchResult.doesValueContainPattern("item identifier 2", itemIdentifier2, searchPattern); 
        } else if (derivedFromItem != null && derivedFromItem.getName() != null) {
            String title = "Derived from: " + derivedFromItem.getName();
            if (qrId != null) {
                title += " (QRID: " + getQrIdDisplay() + ")";
            }
            searchResult = new SearchResult(this, id, title);
        } else {
            searchResult = new SearchResult(this, id, "Item");
        }

        if (derivedFromItem != null) {
            searchResult.doesValueContainPattern("derived from name", derivedFromItem.getName(), searchPattern);
        }
        searchResult.doesValueContainPattern("QrId", getQrIdFilter(), searchPattern); 
        searchResult.doesValueContainPattern("created by", getEntityInfo().getCreatedByUser().getUsername(), searchPattern);
        searchResult.doesValueContainPattern("last modified by", getEntityInfo().getLastModifiedByUser().getUsername(), searchPattern);
        if (getEntityInfo().getOwnerUser() != null) {
            searchResult.doesValueContainPattern("owned by", getEntityInfo().getOwnerUser().getUsername(), searchPattern);
        }
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
            return (other.getId().equals(id));
        }

        return (Objects.equals(other.getItemIdentifier1(), itemIdentifier1)
                && Objects.equals(other.getItemIdentifier2(), itemIdentifier2)
                && Objects.equals(other.getDerivedFromItem(), derivedFromItem)
                && Objects.equals(other.getDomain(), domain)
                && Objects.equals(other.getName(), name));
    }

    public Boolean getIsItemTemplate() {
        if (isItemTemplate == null) {
            isItemTemplate = isItemTemplate(this);
        }
        return isItemTemplate;
    }

    public boolean isItemEntityType(String entityTypeName) {
        return isItemEntityType(this, entityTypeName);
    }

    public static boolean isItemTemplate(Item item) {
        return isItemEntityType(item, EntityTypeName.template.getValue());
    }

    // TODO for future performance, use id instead
    public static boolean isItemEntityType(Item item, String entityTypeName) {
        if (item != null) {
            List<EntityType> entityTypeList = item.getEntityTypeList();
            if (entityTypeList != null) {
                for (EntityType entityType : entityTypeList) {
                    if (entityType.getName().equals(entityTypeName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @JsonIgnore
    @Override
    public Boolean getIsItemDeleted() {
        if (isItemDeleted == null) {
            isItemDeleted = isItemDeleted(this);
        }
        return isItemDeleted;
    }

    public static boolean isItemDeleted(Item item) {
        return isItemEntityType(item, EntityTypeName.deleted.getValue());
    }

    @JsonIgnore
    public Boolean getIsItemInventory() {
        if (isItemInventory == null) {
            isItemInventory = isItemInventory(this);
        }
        return isItemInventory;
    }
    
    public static boolean isItemInventory(Item item) {
        return isItemEntityType(item, EntityTypeName.inventory.getValue());
    } 

    @Override
    public String getSystemLogString() {
        String result = toString();
        if (getId() != null) {
            result += " [Item Id: " + getId() + "]"; 
        }
        return result; 
    }

    @Override
    public String toString() {
        if (getName() != null && getName().isEmpty() == false) {
            if (derivedFromItem != null) {
                return derivedFromItem.toString() + " - [" + getName() + "]";
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

    public void initializeCoreMetadataPropertyValue() {
        if (getCoreMetadataPropertyInfo() != null) {
            if (getPropertyValueList() == null) {
                setPropertyValueList(new ArrayList<>());
            }
            prepareCoreMetadataPropertyValue();
        }
    }

    public PropertyValue prepareCoreMetadataPropertyValue() {
        PropertyType propertyType = getCoreMetadataPropertyType();
        return getItemControllerUtility().preparePropertyTypeValueAdd(
                this, propertyType, propertyType.getDefaultValue(), null);
    }

    public PropertyValue getCoreMetadataPropertyValue() {

        ItemMetadataPropertyInfo info = getCoreMetadataPropertyInfo();

        if (info != null) {
            if (coreMetadataPropertyValue == null) {
                List<PropertyValue> propertyValueList = getPropertyValueList();
                for (PropertyValue propertyValue : propertyValueList) {
                    if (propertyValue.getPropertyType().getName().equals(info.getPropertyName())) {
                        coreMetadataPropertyValue = propertyValue;
                    }
                }
            }
            return coreMetadataPropertyValue;
        }

        return null;
    }

    protected void validateCoreMetadataKey(String key) throws CdbException {
        ItemMetadataPropertyInfo info = getCoreMetadataPropertyInfo();
        if (!info.hasKey(key)) {
            throw new CdbException("Invalid metadata key used to get/set core metadata field value: " + key);
        }
    }

    protected void setCoreMetadataPropertyFieldValue(String key, String value) throws CdbException {

        validateCoreMetadataKey(key);

        PropertyValue propertyValue = getCoreMetadataPropertyValue();

        if (propertyValue == null) {
            propertyValue = prepareCoreMetadataPropertyValue();
        }
        
        if (value == null) {
            value = ""; // this is the default value in prepare value add
        }
        
        propertyValue.setPropertyMetadataValue(key, value);
    }

    protected String getCoreMetadataPropertyFieldValue(String key) throws CdbException {

        validateCoreMetadataKey(key);

        PropertyValue propertyValue = getCoreMetadataPropertyValue();
        if (propertyValue != null) {
            return propertyValue.getPropertyMetadataValueForKey(key);
        } else {
            return "";
        }
    }

    public ItemMetadataPropertyInfo getCoreMetadataPropertyInfo() {
        if (coreMetadataPropertyInfo == null) {
            coreMetadataPropertyInfo = getItemControllerUtility().createCoreMetadataPropertyInfo();
        }
        return coreMetadataPropertyInfo;
    }
    
    public PropertyType getCoreMetadataPropertyType() {
        if (coreMetadataPropertyType == null) {
            coreMetadataPropertyType =
                    PropertyTypeFacade.getInstance().findByName(
                            getCoreMetadataPropertyInfo().getPropertyName());
            if (coreMetadataPropertyType == null) {
                coreMetadataPropertyType = getItemControllerUtility().prepareCoreMetadataPropertyType();
            }
        }
        return coreMetadataPropertyType;
    }
    
    protected CdbEntity getEntityById(String id) {

        if (id != null && !id.isEmpty()) {
            Integer intId = 0;
            try {
                intId = Integer.valueOf(id);
            } catch (NumberFormatException ex) {
                LOGGER.error("getEntityById() number format exception on id: " + id);
            }
            if (intId > 0) {
                ItemFacade instance = ItemFacade.getInstance();
                return instance.findById(intId);
            }
        }

        LOGGER.error("getEntityById() invalid reference id: " + id);
        return null;
    }
    
    public Float getMaxSortOrder() {
        Float maxSortOrder = 0f;
        List<ItemElement> ieList = getFullItemElementList();
        for (ItemElement ie : ieList) {
            Float ieSortOrder = ie.getSortOrder();
            if (ieSortOrder != null) {
                if (ieSortOrder > maxSortOrder) {
                    maxSortOrder = ieSortOrder;
                }
            }
        }
        return maxSortOrder;
    }

    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">
    @JsonIgnore
    public ItemElement getCurrentEditItemElement() {
        return currentEditItemElement;
    }

    public void setCurrentEditItemElement(ItemElement currentEditItemElement) {
        this.currentEditItemElement = currentEditItemElement;
    }

    @JsonIgnore
    public Boolean getCurrentEditItemElementSaveButtonEnabled() {
        return currentEditItemElementSaveButtonEnabled;
    }

    public void setCurrentEditItemElementSaveButtonEnabled(Boolean currentEditItemElementSaveButtonEnabled) {
        this.currentEditItemElementSaveButtonEnabled = currentEditItemElementSaveButtonEnabled;
    }

    @JsonIgnore
    public ItemSource getCurrentEditItemSource() {
        return currentEditItemSource;
    }

    public void setCurrentEditItemSource(ItemSource currentEditItemSource) {
        this.currentEditItemSource = currentEditItemSource;
    }

    @JsonIgnore
    public Boolean getHasElementReorderChangesForCurrent() {
        return hasElementReorderChangesForCurrent;
    }

    public void setHasElementReorderChangesForCurrent(Boolean hasElementReorderChangesForCurrent) {
        this.hasElementReorderChangesForCurrent = hasElementReorderChangesForCurrent;
    }
    // </editor-fold>
}
