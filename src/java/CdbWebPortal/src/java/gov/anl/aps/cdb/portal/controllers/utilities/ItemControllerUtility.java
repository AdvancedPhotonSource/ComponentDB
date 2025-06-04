/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.constants.ListName;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeController;
import gov.anl.aps.cdb.portal.model.db.beans.AllowedPropertyMetadataValueFacade;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeMetadataFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyMetadataValue;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import gov.anl.aps.cdb.portal.model.db.entities.ListTbl;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataFieldInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
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
    PropertyTypeMetadataFacade propertyTypeMetadataFacade;
    AllowedPropertyMetadataValueFacade allowedPropertyMetadataValueFacade;
    RelationshipTypeFacade relationshipTypeFacade;

    Domain defaultDomain = null;

    public ItemControllerUtility() {
        domainFacade = DomainFacade.getInstance();
        itemFacade = getItemFacadeInstance();
        propertyTypeFacade = PropertyTypeFacade.getInstance();
        propertyTypeMetadataFacade = PropertyTypeMetadataFacade.getInstance();
        allowedPropertyMetadataValueFacade = AllowedPropertyMetadataValueFacade.getInstance();
        relationshipTypeFacade = RelationshipTypeFacade.getInstance();
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

    @Override
    public PropertyValue preparePropertyTypeValueAdd(ItemDomainEntity cdbDomainEntity,
            PropertyType propertyType, String propertyValueString, String tag) {
        EntityInfo entityInfo = cdbDomainEntity.getEntityInfo();
        UserInfo ownerUser = entityInfo.getOwnerUser();

        return preparePropertyTypeValueAdd(cdbDomainEntity, propertyType, propertyValueString, tag, ownerUser);
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

    public String generateUniqueElementNameForItem(Item item) {
        // I'm adding this to force the itemElementDisplayList to be rebuilt from the fullItemElementList
        // before generating the name, which is based on the number of elements.  Because the display list is cached
        // and not rebuilt as child items are added, the generated element names were not correct when adding 
        // multiple children to the same parent sequentially.  The generated name was always "E1".
        item.resetItemElementDisplayList();

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

        // initialize core metadata if subclass uses that facility
        item.initializeCoreMetadataPropertyValue();

        return item;
    }

    public final void prepareAddItemElement(ItemDomainEntity item, ItemElement itemElement) {
        List<ItemElement> itemElementList = item.getFullItemElementList();
        List<ItemElement> itemElementsDisplayList = item.getItemElementDisplayList();

        itemElementList.add(itemElement);
        itemElementsDisplayList.add(0, itemElement);
    }

    public void saveNewItemElement(ItemElement itemElement, UserInfo sessionUser) throws CdbException {
        ItemDomainEntity parentItem = (ItemDomainEntity) itemElement.getParentItem();
        ItemDomainEntity containedItem = (ItemDomainEntity) itemElement.getContainedItem();

        if (parentItem != null) {
            prepareAddItemElement(parentItem, itemElement);
        }

        if (containedItem != null && containedItem.getId() == null) {
            // New item, skip history, it will be done under update of current.                   
            prepareEntityInsert(containedItem, sessionUser, true);
        }

        update(parentItem, sessionUser);
    }

    public void migrateMetadataPropertyType(PropertyType propertyType, ItemMetadataPropertyInfo propInfo) {
        // iterate through metadata fields to identify missing information in property type
        boolean updated = false;
        for (ItemMetadataFieldInfo fieldInfo : propInfo.getFields()) {

            // add missing metadata fields to property type
            PropertyTypeMetadata ptm = propertyType.getPropertyTypeMetadataForKey(fieldInfo.getKey());
            if (ptm == null) {
                ptm = newPropertyTypeMetadataForField(fieldInfo, propertyType);
                propertyType.getPropertyTypeMetadataList().add(ptm);
                updated = true;

                // add missing allowed values    
            } else {
                if (fieldInfo.hasAllowedValues()) {
                    for (String allowedValueString : fieldInfo.getAllowedValues()) {
                        if (!ptm.hasAllowedPropertyMetadataValue(allowedValueString)) {
                            AllowedPropertyMetadataValue allowedValue
                                    = newAllowedPropertyMetadataValue(allowedValueString, ptm);
                            ptm.getAllowedPropertyMetadataValueList().add(allowedValue);
                            updated = true;
                        }
                    }
                }
            }
        }

        // iterate through property type metadata to identify obsolete information
        List<PropertyTypeMetadata> removePtmList = new ArrayList<>();
        for (PropertyTypeMetadata ptm : propertyType.getPropertyTypeMetadataList()) {
            String key = ptm.getMetadataKey();

            // remove metadata keys no longer defined in core metadata
            if (!propInfo.hasKey(key)) {
                removePtmList.add(ptm);
            } else {

                ItemMetadataFieldInfo fieldInfo = propInfo.getField(key);
                if (ptm.getIsHaveAllowedValues()) {

                    // remove allowed values no longer defined in core metadata
                    List<AllowedPropertyMetadataValue> removeApmvList = new ArrayList<>();
                    for (AllowedPropertyMetadataValue allowedValue : ptm.getAllowedPropertyMetadataValueList()) {
                        if (!fieldInfo.hasAllowedValue(allowedValue.getMetadataValue())) {
                            removeApmvList.add(allowedValue);
                        }
                    }
                    for (AllowedPropertyMetadataValue removeApmv : removeApmvList) {
                        ptm.getAllowedPropertyMetadataValueList().remove(removeApmv);
                        allowedPropertyMetadataValueFacade.remove(removeApmv);
                        updated = true;
                    }

                }
            }
        }
        for (PropertyTypeMetadata removePtm : removePtmList) {
            propertyType.getPropertyTypeMetadataList().remove(removePtm);
            propertyTypeMetadataFacade.remove(removePtm);
            updated = true;
        }

        if (updated) {
            PropertyTypeController propertyTypeController = PropertyTypeController.getInstance();
            propertyTypeController.setCurrent(propertyType);
            propertyTypeController.update();
        }
    }

    public void createOrMigrateCoreMetadataPropertyType(PropertyType propertyType) {

        // initialize property type if it is null
        if (propertyType == null) {
            propertyType = prepareCoreMetadataPropertyType();
        } else {
            migrateMetadataPropertyType(propertyType, createCoreMetadataPropertyInfo());
        }
    }

    public PropertyType prepareCoreMetadataPropertyType() {
        PropertyTypeControllerUtility propertyTypeControllerUtility = new PropertyTypeControllerUtility();
        PropertyType propertyType = propertyTypeControllerUtility.createEntityInstance(null);

        ItemMetadataPropertyInfo propInfo = createCoreMetadataPropertyInfo();

        propertyType.setIsInternal(true);
        propertyType.setName(propInfo.getPropertyName());
        propertyType.setDescription(propInfo.getDisplayName());

        List<Domain> allowedDomainList = new ArrayList<>();
        allowedDomainList.add(getDefaultDomain());
        propertyType.setAllowedDomainList(allowedDomainList);

        List<PropertyTypeMetadata> ptmList = new ArrayList<>();
        for (ItemMetadataFieldInfo fieldInfo : propInfo.getFields()) {
            PropertyTypeMetadata ptm = newPropertyTypeMetadataForField(fieldInfo, propertyType);
            ptmList.add(ptm);
        }
        propertyType.setPropertyTypeMetadataList(ptmList);

        try {
            propertyTypeControllerUtility.create(propertyType, null);
        } catch (CdbException ex) {
            logger.error(ex.getMessage());
            return null;
        }
        return propertyType;
    }

    public PropertyTypeMetadata newPropertyTypeMetadataForField(
            ItemMetadataFieldInfo field,
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

    public ItemMetadataPropertyInfo createCoreMetadataPropertyInfo() {
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
        if (defaultDomain == null) {
            defaultDomain = domainFacade.findByName(getDefaultDomainName());

            if (defaultDomain == null) {
                defaultDomain = new Domain();
                defaultDomain.setName(getDefaultDomainName());
            }
        }

        return defaultDomain;
    }

    /**
     * Provides common support for find by path operation for hierarchical
     * entities.
     */
    protected ItemDomainEntity findByPath_(
            String path, Function<ItemDomainEntity, ItemDomainEntity> parentGetterMethod) throws CdbException {

        if (path.charAt(0) != '/') {
            // first character expected to be forward slash
            throw new CdbException("invalid path format, first character expected to be forward slash");
        }

        // tokenize the path string, escaping any embedded delimiters
        String delim = "/";
        String esc = "\\";
        String regex = "(?<!" + Pattern.quote(esc) + ")" + Pattern.quote(delim);
        List<String> pathTokens = Arrays.asList(path.split(regex));

        if (pathTokens.isEmpty()) {
            return null;
        }

        // get item name and list of parent item names from path
        String itemName = pathTokens.get(pathTokens.size() - 1);
        List<String> pathParentNames = new ArrayList<>();
        if (pathTokens.size() > 1) {
            // here we skip the first element since it is expected to be empty string
            // as the first character is slash, and the last element which is the item name
            pathParentNames = pathTokens.subList(1, pathTokens.size() - 1);
            Collections.reverse(pathParentNames);
        }

        // retrieve list of candidate items matching name
        List<ItemDomainEntity> candidateItems = getItemFacadeInstance().findByDomainAndName(getDefaultDomainName(), itemName);

        // check path against parents for each candidate
        for (ItemDomainEntity candidateItem : candidateItems) {

            // create parent path list for candidate item and compare to specified path
            List<String> itemParentNames = new ArrayList<>();
            ItemDomainEntity candidateParent = parentGetterMethod.apply(candidateItem);
            while (candidateParent != null) {
                // replace occurrences of '/' with "\\/" to match syntax in specified path
                itemParentNames.add(candidateParent.getName().replace("/", "\\/"));
                candidateParent = parentGetterMethod.apply(candidateParent);
            }
            if (itemParentNames.equals(pathParentNames)) {
                // candidate item parent path matches specified path
                if (!candidateItem.getIsItemDeleted()) {
                    return candidateItem;
                }
            }
        }

        // no match
        return null;
    }

    protected String getItemCreatedFromTemplateRelationshipName() {
        return ItemElementRelationshipTypeNames.template.getValue();
    }

    public void addCreatedFromTemplateRelationshipToItem(ItemDomainEntity item, ItemDomainEntity templateToCreateNewItem) {
        RelationshipType templateRelationship
                = relationshipTypeFacade.findByName(getItemCreatedFromTemplateRelationshipName());

        // Create item element relationship between the template and the clone 
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setRelationshipType(templateRelationship);
        itemElementRelationship.setFirstItemElement(item.getSelfElement());
        itemElementRelationship.setSecondItemElement(templateToCreateNewItem.getSelfElement());

        item.setItemElementRelationshipList(new ArrayList<>());
        item.getItemElementRelationshipList().add(itemElementRelationship);
    }

    public ItemDomainEntity completeClone(ItemDomainEntity clonedItem, Integer cloningFromItemId, UserInfo user, boolean cloneProperties, boolean cloneSources, boolean cloneCreateItemElementPlaceholders) {
        ItemDomainEntity cloningFrom = findById(cloningFromItemId);

        if (cloneProperties) {
            clonedItem = cloneProperties(clonedItem, cloningFrom, user);
        }
        if (cloneSources) {
            clonedItem = cloneSources(clonedItem, cloningFrom);
        }
        if (cloneCreateItemElementPlaceholders) {
            clonedItem = defaultCloneCreateItemElementsForClone(clonedItem, cloningFrom, user);
        }

        cloneProperties = false;
        cloneSources = false;
        cloneCreateItemElementPlaceholders = false;

        return clonedItem;
    }

    /**
     * Default domain specific configuration for cloning elements when doing the
     * complete clone
     *
     * @param clonedItem
     * @param cloningFrom
     * @param user
     * @return
     */
    protected ItemDomainEntity defaultCloneCreateItemElementsForClone(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom, UserInfo user) {
        return cloneCreateItemElements(clonedItem, cloningFrom, user, false, false, false);
    }

    public ItemDomainEntity cloneProperties(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom, UserInfo enteredByUser) {
        List<PropertyValue> cloningFromPropertyValueList = cloningFrom.getPropertyValueList();

        if (cloningFromPropertyValueList != null) {
            List<PropertyValue> newItemPropertyValueList = new ArrayList<>();

            Date enteredOnDateTime = new Date();

            for (PropertyValue propertyValue : cloningFromPropertyValueList) {
                PropertyTypeHandlerInterface handler;
                handler = PropertyTypeHandlerFactory.getHandler(propertyValue);
                if (handler != null) {
                    if (handler.isPropertyCloneable(cloningFrom.getDomain()) == false) {
                        continue;
                    }
                }

                PropertyValue newPropertyValue = new PropertyValue();
                newPropertyValue.setPropertyType(propertyValue.getPropertyType());
                newPropertyValue.setValue(propertyValue.getValue());
                newPropertyValue.setDisplayValue(propertyValue.getDisplayValue());
                newPropertyValue.setTargetValue(propertyValue.getTargetValue());
                newPropertyValue.setTag(propertyValue.getTag());
                newPropertyValue.setUnits(propertyValue.getUnits());
                newPropertyValue.setDescription(propertyValue.getDescription());
                newPropertyValue.setIsDynamic(propertyValue.getIsDynamic());
                newPropertyValue.setEnteredOnDateTime(enteredOnDateTime);
                newPropertyValue.setEnteredByUser(enteredByUser);

                newItemPropertyValueList.add(newPropertyValue);
            }

            clonedItem.setPropertyValueList(newItemPropertyValueList);
        }

        return clonedItem;
    }

    public ItemDomainEntity cloneSources(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom) {
        List<ItemSource> cloningFromSourceList = cloningFrom.getItemSourceList();

        if (cloningFromSourceList != null) {
            List<ItemSource> newItemSourceList = new ArrayList<>();

            for (ItemSource itemSource : cloningFromSourceList) {
                ItemSource newItemSource = new ItemSource();

                newItemSource.setItem(clonedItem);
                newItemSource.setSource(itemSource.getSource());
                newItemSource.setPartNumber(itemSource.getPartNumber());
                newItemSource.setCost(itemSource.getCost());
                newItemSource.setDescription(itemSource.getDescription());
                newItemSource.setIsVendor(itemSource.getIsVendor());
                newItemSource.setIsManufacturer(itemSource.getIsManufacturer());
                newItemSource.setContactInfo(itemSource.getContactInfo());
                newItemSource.setUrl(itemSource.getUrl());

                newItemSourceList.add(newItemSource);
            }

            clonedItem.setItemSourceList(newItemSourceList);
        }

        return clonedItem;
    }

    public ItemDomainEntity cloneCreateItemElements(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom, UserInfo user, boolean addContained, boolean assignDerivedFromItemElement, boolean generateUniqueElementName) {
        List<ItemElement> cloningFromItemElementList = cloningFrom.getItemElementDisplayList();

        if (cloningFromItemElementList != null) {
            for (ItemElement itemElement : cloningFromItemElementList) {
                cloneCreateItemElement(itemElement, clonedItem, user, addContained, assignDerivedFromItemElement, generateUniqueElementName);
            }
        }

        return clonedItem;
    }

    public ItemElement cloneCreateItemElement(ItemElement itemElement, Item clonedItem, UserInfo user, boolean addContained, boolean assignDerivedFromItemElement, boolean generateUniqueElementName) {
        String newName = null;
        if (generateUniqueElementName) {
            clonedItem.resetItemElementVars();
            newName = generateUniqueElementNameForItem(clonedItem);
        }

        ItemElement newItemElement = new ItemElement();

        if (itemElement.getDerivedFromItemElement() != null) {
            newItemElement.init(clonedItem, itemElement.getDerivedFromItemElement(), user);
        } else {
            newItemElement.init(clonedItem, user);
        }

        if (addContained) {
            Item containedItem = itemElement.getContainedItem();
            if (containedItem != null) {
                containedItem.appendItemElementMemberList(newItemElement);
                newItemElement.setContainedItem(itemElement.getContainedItem());
            }
            Item containedItem2 = itemElement.getContainedItem2();
            if (containedItem2 != null) {
                containedItem2.appendItemElementMemberList2(newItemElement);
                newItemElement.setContainedItem2(itemElement.getContainedItem2());
            }
        }

        if (assignDerivedFromItemElement) {
            newItemElement.setDerivedFromItemElement(itemElement);
        }

        newItemElement.setName(itemElement.getName());
        newItemElement.setIsRequired(itemElement.getIsRequired());

        clonedItem.getFullItemElementList().add(newItemElement);

        newItemElement.setSortOrder(itemElement.getSortOrder());

        if (newName != null) {
            newItemElement.setName(newName);
        }

        return newItemElement;
    }

    public List<Item> getParentItemList(ItemDomainEntity itemEntity) {
        List<Item> parentItemList = itemEntity.getParentItemList();
        if (parentItemList == null) {
            parentItemList = getStandardParentItemList(itemEntity);
            itemEntity.setParentItemList(parentItemList);
        }

        return parentItemList;
    }
    
    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new ItemElementConstraintInformation(itemElement);
    }

    public static List<Item> getStandardParentItemList(Item itemEntity) {

        List<Item> itemList = new ArrayList<>();
        List<ItemElement> itemElementList = new ArrayList<>();

        if (itemEntity.getItemElementMemberList() != null) {
            itemElementList.addAll(itemEntity.getItemElementMemberList());
        }

        if (itemEntity.getItemElementMemberList2() != null) {
            itemElementList.addAll(itemEntity.getItemElementMemberList2());
        }

        // Remove currently being viewed item. 
        for (ItemElement itemElement : itemElementList) {
            Item memberItem = itemElement.getParentItem();
            memberItem.setMembershipItemElement(itemElement);

            if (itemList.contains(memberItem) == false) {
                itemList.add(memberItem);
            }
        }

        return itemList;
    }

}
