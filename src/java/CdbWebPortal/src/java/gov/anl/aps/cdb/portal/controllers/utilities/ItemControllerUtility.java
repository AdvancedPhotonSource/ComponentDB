/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.constants.ListName;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyMetadataValue;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ListTbl;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.view.objects.ItemCoreMetadataFieldInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemCoreMetadataPropertyInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 * @param <ItemDomainEntity>
 * @param <ItemDomainEntityFacade>
 */
public abstract class ItemControllerUtility<ItemDomainEntity extends Item, ItemDomainEntityFacade extends ItemFacadeBase<ItemDomainEntity>>
        extends CdbDomainEntityControllerUtility<ItemDomainEntity, ItemDomainEntityFacade> {

    DomainFacade domainFacade;
    ItemDomainEntityFacade itemFacade;
    PropertyTypeFacade propertyTypeFacade;

    public ItemControllerUtility() {
        domainFacade = DomainFacade.getInstance();
        itemFacade = getItemFacadeInstance();
        propertyTypeFacade = PropertyTypeFacade.getInstance(); 
    }

    protected abstract ItemDomainEntityFacade getItemFacadeInstance();

    @Override
    protected ItemDomainEntityFacade getEntityDbFacade() {
        return itemFacade;
    }

    private static final Logger logger = LogManager.getLogger(ItemControllerUtility.class.getName());

    @Override
    public void prepareEntityInsert(ItemDomainEntity item, UserInfo userInfo) throws CdbException {
        prepareEntityInsert(item, userInfo, false);
    }

    public void prepareEntityInsert(ItemDomainEntity item, UserInfo userInfo, boolean skipHistory) throws CdbException {
        super.prepareEntityInsert(item, userInfo);

        checkItem(item);
        performPrepareEntityInsertUpdate(item, userInfo);

        if (!skipHistory) {
            List<ItemElement> newElementList = item.getFullItemElementList();
            logger.debug("Adding innitial element history for " + item);
            EntityInfo entityInfo = item.getEntityInfo();
            ItemElementUtility.prepareItemElementHistory(null, newElementList, entityInfo);

            List<ItemElement> itemElementMemberList = item.getItemElementMemberList();
            if (itemElementMemberList != null) {
                // Reverse hierarchy inserted, parent specified during insert. 
                logger.debug("Adding innitial element member history for " + item);
                ItemElementUtility.prepareItemElementHistory(null, itemElementMemberList, entityInfo);
            }
        }
    }

    @Override
    public void prepareEntityUpdate(ItemDomainEntity item, UserInfo updatedByUser) throws CdbException {
        checkItem(item);
        performPrepareEntityInsertUpdate(item, updatedByUser);
        item.resetAttributesToNullIfEmpty();
        EntityInfo entityInfo = item.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo, updatedByUser);

        // Prepare history 
        Item originalItem = findById(item.getId());
        // Full item element list contains self element as well as all the elements 
        List<ItemElement> originalElementList = originalItem.getFullItemElementList();
        List<ItemElement> newElementList = item.getFullItemElementList();
        logger.debug("Verifying elements for item " + item);
        ItemElementUtility.prepareItemElementHistory(originalElementList, newElementList, entityInfo);
        //Verify reverse hierarchy updates
        List<ItemElement> originalItemElementMemberList = originalItem.getItemElementMemberList();
        List<ItemElement> newitemElementMemberList = item.getItemElementMemberList();
        ItemElementUtility.prepareItemElementHistory(originalItemElementMemberList, newitemElementMemberList, entityInfo);

        // Compare properties with what is in the db
        List<PropertyValue> originalPropertyValueList = originalItem.getPropertyValueList();
        List<PropertyValue> newPropertyValueList = item.getPropertyValueList();
        logger.debug("Verifying properties for item " + item);
        PropertyValueUtility.preparePropertyValueHistory(originalPropertyValueList, newPropertyValueList, entityInfo);
        item.clearPropertyValueCache();

        List<Item> derivedFromItemList = item.getDerivedFromItemList();
        if (derivedFromItemList != null) {
            for (Item derivedItem : derivedFromItemList) {
                derivedItem.resetAttributesToNullIfEmpty();
                ItemControllerUtility derivedItemController = derivedItem.getItemControllerUtility();
                derivedItemController.checkItem(derivedItem);
            }
        }

        logger.debug("Updating item " + item.getId()
                + " (user: " + entityInfo.getLastModifiedByUser().getUsername() + ")");

    }

    @Override
    protected void prepareEntityDestroy(ItemDomainEntity item, UserInfo userInfo) throws CdbException {
        super.prepareEntityDestroy(item, userInfo);
        List<ItemElement> memberList = item.getItemElementMemberList();
        if (memberList != null && memberList.isEmpty() == false) {
            for (ItemElement member : memberList) {
//                System.out.println("parent: " + member.getParentItem().getName());
//                System.out.println("child: " + member.getContainedItem().getName());
//                System.out.println("element: " + member.getName());
                if (!member.isMarkedForDeletion()) {
                    throw new CdbException("Item " + item.getName() + " is part of an assembly.");
                }
            }
        }
    }

    public void checkItemElement(ItemElement itemElement) throws CdbException {
        ItemDomainEntity parentItem = (ItemDomainEntity) itemElement.getParentItem();
        checkItemElementsForItem(parentItem);
    }

    public ItemElement createItemElement(ItemDomainEntity item, UserInfo userInfo) {
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo(userInfo);
        return createItemElement(item, entityInfo);
    }

    protected ItemElement createItemElement(ItemDomainEntity item, EntityInfo entityInfo) {
        ItemElement itemElement = new ItemElement();
        itemElement.setEntityInfo(entityInfo);
        itemElement.setParentItem(item);

        String elementName = generateUniqueElementNameForItem(item);

        itemElement.setName(elementName);

        return itemElement;
    }

    public String generateUniqueElementNameForItem(ItemDomainEntity item) {
        List<ItemElement> itemElementsDisplayList = item.getItemElementDisplayList();
        int elementNumber = itemElementsDisplayList.size() + 1;
        String elementNameSuffix = "E";
        String elementName = null;

        boolean unique = false;
        while (elementName == null) {
            String test = elementNameSuffix + elementNumber;
            if (itemElementsDisplayList.size() > 0) {
                for (ItemElement ittrItemElement : itemElementsDisplayList) {
                    if (ittrItemElement.getName().equalsIgnoreCase(test)) {
                        elementNumber++;
                        unique = false;
                        break;
                    } else {
                        unique = true;
                    }
                }
            } else {
                unique = true;
            }
            if (unique) {
                elementName = test;
            }
        }

        return elementName;
    }

    public void performPrepareEntityInsertUpdate(Item item, UserInfo userInfo) throws InvalidRequest {
        if (item instanceof LocatableItem) {
            LocatableItem locatableItem = (LocatableItem) item;
            LocatableItemControllerUtility locatableControllerUtility;
            locatableControllerUtility = new LocatableItemControllerUtility();
            locatableControllerUtility.updateItemLocation(locatableItem, userInfo);
        }
        addDynamicPropertiesToItem(item, userInfo);
    }

    public void addDynamicPropertiesToItem(Item item, UserInfo createdByUser) {
        if (item.getId() == null) {
            Item itemDerivedFromItem = item.getDerivedFromItem();
            if (itemDerivedFromItem != null) {
                Date createdOnDateTime = new Date();
                item.updateDynamicProperties(createdByUser, createdOnDateTime);
            }
        }
    }

    public ItemElement finalizeItemElementRequiredStatusChanged(ItemElement itemElement, UserInfo userinfo) throws CdbException {
        return itemElement;
    }

    public void checkItem(ItemDomainEntity item) throws CdbException {
        checkItem(item, false);
    }

    public void checkItem(ItemDomainEntity item, boolean skipProjects) throws CdbException {
        Domain itemDomain = item.getDomain();

        // Verify no qr id is specified when it is not allowed for the domain.
        if (isEntityHasQrId(item) == false) {
            if (item.getQrId() != null) {
                throw new CdbException("QR Id cannot be specified for " + itemDomainToString(item));
            }
        }

        if (itemDomain == null) {
            throw new CdbException("No domain has been specified for " + itemDomainToString(item));
        }

        if (skipProjects == false) {
            if (isItemProjectRequired()) {
                checkItemProject(item);
            }
        }

        checkItemUniqueness(item);
        checkItemElementsForItem(item);
    }

    public void checkItemUniqueness(ItemDomainEntity item) throws CdbException {
        String name = item.getName();
        Integer qrId = item.getQrId();

        if (isEntityHasName()) {
            if (name != null && name.isEmpty()) {
                throw new CdbException("No " + getNameTitle() + " has been specified for " + itemDomainToString(item));
            }
        }

        if (isEntityHasQrId()) {
            if (qrId != null) {
                Item existingItem = getEntityDbFacade().findByQrId(qrId);
                if (existingItem != null) {
                    if (!Objects.equals(existingItem.getId(), item.getId())) {
                        throw new ObjectAlreadyExists("Item " + existingItem.toString() + " already exists with qrId " + existingItem.getQrIdDisplay() + ".");
                    }
                }
            }
        }

        if (verifyItemNameCombinationUniqueness(item) == false) {
            String additionalInfo = "Please update some of the following:  ";

            if (isEntityHasName()) {
                additionalInfo += "Name, ";
            }
            if (isEntityHasItemIdentifier1()) {
                additionalInfo += getItemIdentifier1Title() + ", ";
            }
            if (isEntityHasItemIdentifier2()) {
                additionalInfo += getItemIdentifier2Title() + ", ";
            }

            //Remove last comma. 
            additionalInfo = additionalInfo.substring(0, additionalInfo.length() - 2);

            throw new ObjectAlreadyExists("Item " + itemDomainToString(item) + " has nonunique attributes. " + additionalInfo);
        }

    }

    protected boolean verifyItemNameCombinationUniqueness(Item item) {
        String name = item.getName();
        String itemIdentifier1 = item.getItemIdentifier1();
        String itemIdentifier2 = item.getItemIdentifier2();
        Item derivedFromItem = item.getDerivedFromItem();
        Domain itemDomain = item.getDomain();

        Item existingItem = getEntityDbFacade().findByUniqueAttributes(derivedFromItem, itemDomain, name, itemIdentifier1, itemIdentifier2);

        // The same item will have all the same attributes if it wasn't changed.  
        if (existingItem != null) {
            if (Objects.equals(item.getId(), existingItem.getId()) == false) {
                return false;
            }
        }
        return true;
    }

    public void checkItemElementsForItem(ItemDomainEntity item) throws CdbException {
        item.resetItemElementDisplayList();
        List<String> elementNames = new ArrayList<>();
        for (ItemElement itemElement : item.getItemElementDisplayList()) {
            if (itemElement.getName() == null || itemElement.getName().isEmpty()) {
                throw new CdbException("Item element name cannot be empty.");
            }
            String itemElementName = itemElement.getName();
            if (elementNames.contains(itemElementName)) {
                throw new CdbException("Element names must be unique within their assembly. '" + itemElementName + "' is repeated.");
            }

            elementNames.add(itemElement.getName());
        }
        // Throws exception if a tree cannot be generated due to circular reference. 
        ItemElementUtility.createItemElementRoot(item);
    }

    public void checkItemProject(Item item) throws CdbException {
        if (item.getItemProjectList() == null || item.getItemProjectList().isEmpty()) {
            throw new CdbException("Project for item " + itemDomainToString(item) + " must be specified.");
        }
    }

    public ItemConnector prepareAddItemConnector(Item item, UserInfo sessionUser) {
        ItemConnectorControllerUtility itemConnectorController = new ItemConnectorControllerUtility();
        ItemConnector itemConnector = itemConnectorController.createEntityInstance(sessionUser);
        itemConnector.setItem(item);
        itemConnector = prepareItemConnectorForDomain(itemConnector);
        item.getItemConnectorList().add(itemConnector);
        return itemConnector;
    }

    protected final ItemConnector prepareItemConnectorForDomain(ItemConnector itemConnector) {
        return itemConnector;
    }

    protected abstract ItemDomainEntity instenciateNewItemDomainEntity();

    @Override
    public ItemDomainEntity createEntityInstance(UserInfo sessionUser) {
        ItemDomainEntity item = instenciateNewItemDomainEntity();
        EntityInfo ei = EntityInfoUtility.createEntityInfo(sessionUser);

        Domain domain = getDefaultDomain();
        if (domain != null) {           
            item.init(domain, ei);            
        } else {            
            item.init(ei);
        }

        try {
            initializeCoreMetadataPropertyValue(item);
        } catch (CdbException ex) {
            logger.error(ex);
        }

        return item;
    }

    protected void initializeCoreMetadataPropertyValue(ItemDomainEntity item) throws CdbException {
        if (item.getCoreMetadataPropertyInfo() != null) {
            item.setPropertyValueList(new ArrayList<>());
            prepareCoreMetadataPropertyValue(item);
        }
    }

    public PropertyValue prepareCoreMetadataPropertyValue(ItemDomainEntity item) throws CdbException {
        // Add cable internal property type
        PropertyType propertyType = propertyTypeFacade.findByName(item.getCoreMetadataPropertyInfo().getPropertyName());

        if (propertyType == null) {
            propertyType = prepareCoreMetadataPropertyType();
        }

        return preparePropertyTypeValueAdd(item, propertyType, propertyType.getDefaultValue(), null);
    }

    public PropertyType prepareCoreMetadataPropertyType() throws CdbException {
        PropertyTypeControllerUtility propertyTypeControllerUtility = new PropertyTypeControllerUtility();
        PropertyType propertyType = propertyTypeControllerUtility.createEntityInstance(null);

        ItemCoreMetadataPropertyInfo propInfo = createCoreMetadataPropertyInfo();

        propertyType.setIsInternal(true);
        propertyType.setName(propInfo.getPropertyName());
        propertyType.setDescription(propInfo.getDisplayName());

        List<Domain> allowedDomainList = new ArrayList<>();
        allowedDomainList.add(getDefaultDomain());
        propertyType.setAllowedDomainList(allowedDomainList);

        List<PropertyTypeMetadata> ptmList = new ArrayList<>();
        for (ItemCoreMetadataFieldInfo fieldInfo : propInfo.getFields()) {
            PropertyTypeMetadata ptm = newPropertyTypeMetadataForField(fieldInfo, propertyType);
            ptmList.add(ptm);
        }
        propertyType.setPropertyTypeMetadataList(ptmList);

        propertyTypeControllerUtility.create(propertyType, null);
        return propertyType;
    }

    public PropertyTypeMetadata newPropertyTypeMetadataForField(
            ItemCoreMetadataFieldInfo field,
            PropertyType propertyType) {

        PropertyTypeMetadata ptm = new PropertyTypeMetadata();
        ptm.setMetadataKey(field.getKey());
        ptm.setDescription(field.getDescription());
        List<AllowedPropertyMetadataValue> allowedValueList = new ArrayList<>();
        for (String allowedValueString : field.getAllowedValues()) {
            AllowedPropertyMetadataValue allowedValue
                    = newAllowedPropertyMetadataValue(allowedValueString, ptm);
            allowedValueList.add(allowedValue);
        }
        ptm.setAllowedPropertyMetadataValueList(allowedValueList);
        ptm.setPropertyType(propertyType);

        return ptm;
    }

    public AllowedPropertyMetadataValue newAllowedPropertyMetadataValue(
            String value,
            PropertyTypeMetadata ptm) {

        AllowedPropertyMetadataValue allowedValue = new AllowedPropertyMetadataValue();
        allowedValue.setMetadataValue(value);
        allowedValue.setPropertyTypeMetadata(ptm);
        return allowedValue;
    }

    public ItemCoreMetadataPropertyInfo createCoreMetadataPropertyInfo() {
        // do nothing by default, subclasses with core metadata to override
        return null;
    }

    public List<ItemDomainEntity> getFavoriteItems(SettingEntity settingEntity) {
        ItemDomainEntityFacade itemFacade = getEntityDbFacade();
        String domainName = getDefaultDomainName();

        List<ItemDomainEntity> itemList = null;
        itemList = itemFacade.getItemListContainedInListWithoutEntityType(domainName, getFavoritesList(settingEntity));

        return itemList;
    }

    public ListTbl getFavoritesList(SettingEntity settingEntity) {
        List<ListTbl> itemElementLists = settingEntity.getItemElementLists();
        if (itemElementLists != null) {
            for (ListTbl list : itemElementLists) {
                String favoriteListName = ListName.favorite.getValue();
                if (list.getName().equals(favoriteListName)) {
                    return list;
                }
            }
        }

        // List does not exist
        return null;
    }

    public Boolean isItemExistInDb(Item item) {
        Item dbItem = null;
        if (item.getId() != null) {
            dbItem = findById(item.getId());
        }

        return dbItem != null;
    }
    
    @Override
    public List<ItemDomainEntity> getAllEntities() {
        return getItemList();
    }
        
    public List<ItemDomainEntity> getItemList() {
        return getEntityDbFacade().findByDomain(getDefaultDomainName());
    }

    protected String itemDomainToString(Item item) {
        return item.toString();
    }

    // TODO remove this upon creation of machine template controller. 
    public boolean isEntityHasQrId(Item item) {
        return isEntityHasQrId();
    }

    public String getNameTitle() {
        return "Name";
    }

    public abstract boolean isEntityHasQrId();

    public abstract boolean isEntityHasName();

    public abstract boolean isEntityHasProject();

    public abstract String getDefaultDomainName();

    public final boolean isItemProjectRequired() {
        return isEntityHasProject();
    }

    public boolean isEntityHasItemIdentifier1() {
        return getItemIdentifier1Title() != null;
    }

    public boolean isEntityHasItemIdentifier2() {
        return getItemIdentifier2Title() != null;
    }

    public String getItemIdentifier1Title() {
        if (getDefaultDomain() != null) {
            return getDefaultDomain().getItemIdentifier1Label();
        }
        return null;
    }

    public String getItemIdentifier2Title() {
        if (getDefaultDomain() != null) {
            return getDefaultDomain().getItemIdentifier2Label();
        }
        return null;
    }

    public abstract String getDerivedFromItemTitle();

    public Domain getDefaultDomain() {
        Domain defaultControllerDomain;
        defaultControllerDomain = domainFacade.findByName(getDefaultDomainName());

        if (defaultControllerDomain == null) {
            defaultControllerDomain = new Domain();
            defaultControllerDomain.setName(getDefaultDomainName());
        }

        return defaultControllerDomain;
    }

}
