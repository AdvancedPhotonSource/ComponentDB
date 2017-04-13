/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import java.util.List;

/**
 * Interface specifies all necessary functions for item controllers. 
 * - Many functions are required by the item views. 
 */
public interface IItemController<ItemDomainEntity extends Item> {

    /**
     * Default domain of items managed by the controller.
     *
     * @return
     */
    public String getDefaultDomainName();
    
    /**
     * Does Item connectors section need to be displayed for the item in domain. 
     * 
     * @return 
     */
    public boolean getEntityDisplayItemConnectors(); 

    /**
     * Does item identifier 1 need to be displayed for the item in default
     * domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemIdentifier1();

    /**
     * Does item identifier 2 need to be displayed for the item in default
     * domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemIdentifier2();

    /**
     * Does item name need to be displayed for the item in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemName();

    /**
     * Does item type need to be displayed for the item in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemType();

    /**
     * Does item category need to be displayed for the item in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemCategory();

    /**
     * Does item of default domain need to display item it derived from.
     *
     * @return
     */
    public boolean getEntityDisplayDerivedFromItem();

    /**
     * Does item qrId need to be displayed for the item in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayQrId();

    /**
     * Does item gallery need to be displayed for the item in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemGallery();

    /**
     * Do the item logs need to be displayed for the item in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemLogs();

    /**
     * Do the item sources need to be displayed for the item in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemSources();

    /**
     * Do the item properties need to be displayed for the item in default
     * domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemProperties();

    /**
     * Do the item elements need to be displayed for the item in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemElements();

    /**
     * Do the items derived from the item in default domain need to be
     * displayed.
     *
     * @return
     */
    public boolean getEntityDisplayItemsDerivedFromItem();

    /**
     * Does item membership in other items as elements of item in default domain
     * need to be shown.
     *
     * @return
     */
    public boolean getEntityDisplayItemMemberships();
    
    /**
     * Does the item project need to be displayed for items in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemProject();

    /**
     * Do the item entity types need to be displayed for item in default domain.
     *
     * @return
     */
    public boolean getEntityDisplayItemEntityTypes();

    /**
     * What does item identifier 1 represent for items in default domain.
     *
     * @return
     */
    public String getItemIdentifier1Title();

    /**
     * What does item identifier 2 represent for items in default domain.
     *
     * @return
     */
    public String getItemIdentifier2Title();

    /**
     * What is the meaningful representation/title of items derived form item in
     * default domain. ex: inventory items for catalog item.
     *
     * @return
     */
    public String getItemsDerivedFromItemTitle();

    /**
     * What is the meaningful representation/title of item the item in default
     * domain derived from. ex: catalog item for inventory item.
     *
     * @return
     */
    public String getDerivedFromItemTitle();

    /**
     * What is the name of the css style for items in default domain.
     *
     * @return
     */
    public String getStyleName();
    
    /**
     * Get the default style of the list.
     * 
     * @return 
     */
    public String getListStyleName(); 
    
    /**
     * Style based on the current item in controller 
     * 
     * @return 
     */
    public String getCurrentItemStyleName();

    /**
     * Get method for domain name for which the default domain items have
     * derived from.
     *
     * @return
     */
    public String getDefaultDomainDerivedFromDomainName();

    /**
     * Get method for domain name for which the default domain items have
     * derived to.
     *
     * @return
     */
    public String getDefaultDomainDerivedToDomainName();
    
    /**
     * Get the title of the name to appear on the UI.
     * 
     * @return 
     */
    public String getNameTitle();

    /**
     * Get the title of the item type to appear on the UI.
     * 
     * @return 
     */
    public String getItemItemTypeTitle();

    /**
     * Get the title of the item category to appear on the UI.
     * 
     * @return 
     */
    public String getItemItemCategoryTitle();
    
    /**
     * Standard entity type name. 
     * 
     * @return 
     */
    public String getEntityTypeName();
    
    /**
     * Get standard list of item categories for the domain. 
     * 
     * @return 
     */
    public List<ItemCategory> getDomainItemCategoryList();
    
    /**
     * Get a list of item types based on state of current item. 
     * Example: selected categories. 
     * 
     * @return 
     */
    public List<ItemType> getAvailableItemTypesForCurrentItem();
    
    /**
     * True when item has primary image
     * 
     * @param item
     * @return 
     */
    public boolean itemHasPrimaryImage(Item item);

    /**
     * get primary image thumbnail for item.
     * 
     * @param item
     * @return 
     */
    public String getPrimaryImageThumbnailForItem(Item item);

    /**
     * Get primary image value for item. 
     * 
     * @param item
     * @return 
     */
    public String getPrimaryImageValueForItem(Item item);
    
    /**
     * Provides a display string for an item. 
     * 
     * @param item
     * @return 
     */
    public String getItemDisplayString(ItemDomainEntity item);
    
    /**
     * Class may be subscribed to ItemProjectController to execute code when item project changes. 
     */
    public void itemProjectChanged();
    
    /**
     * Get style of favorites icon for a given item.
     * 
     * @param item
     * @return 
     */
    public String getItemFavoritesIconStyle(Item item); 
    
    /**
     * Toggles an item as a favorite.
     * 
     * @param item 
     */
    public void toggleItemInFavoritesList(Item item); 
    
    /**
     * Get name of the item domain controller.
     * 
     * @return 
     */
    public String getDomainControllerName();
    
    /**
     * Throws an exception when item does not pass uniqueness check.
     * 
     * @param item
     * @throws CdbException 
     */
    public void checkItemUniqueness(Item item) throws CdbException;
    
    /**
     * Get item currently being viewed/edited by the user.
     * 
     * @param current 
     */
    public void setCurrent(ItemDomainEntity current);
    
    /**
     * Set item currently being viewed/edited by the user.
     * 
     * @return 
     */
    public ItemDomainEntity getCurrent();
    
    /**
     * determines if item project is required.
     * 
     * @return 
     */
    public Boolean isItemProjectRequired();
    
    /**
     * Throws exception when item project is not properly specified.
     * 
     * @param item
     * @throws CdbException 
     */
    public void checkItemProject(Item item) throws CdbException;
    
    /**
     * More readable version of entityTypeName.
     * 
     * @return 
     */
    public String getDisplayEntityTypeName();
    
    /**
     * Specifies if the user may edit the entity type.
     * 
     * @return 
     */
    public boolean isEntityTypeEditable();
    
    /**
     * The domain controller derived from the domain of current controller. 
     * 
     * Example: Catalog -> Inventory controller
     * 
     * @return 
     */
    public ItemController getDefaultDomainDerivedFromDomainController();

    /**
     * The domain controller to which derive the domain of current controller. 
     * 
     * Example: Catalog -> Inventory controller
     * 
     * @return 
     */
    public ItemController getDefaultDomainDerivedToDomainController();
    
    /**
     * Sets the derived from item attribute of current item in controller. 
     * 
     * @param derivedFromItem 
     */
    public void setCurrentDerivedFromItem(Item derivedFromItem);
    
    /**
     * Adds current item to the database.
     * 
     * @return 
     */
    public String create(); 
    
    /**
     * Default controller to perform selection of items. One could be specified
     * for the specific view page otherwise this one will be provided.
     *
     * @return
     */
    public ItemController getSelectionController();
    
    /**
     * Determines when the input for item type should be disabled on UI. 
     * 
     * @return 
     */
    public boolean isDisabledItemItemType();
    
    /**
     * Generates a display string based on current item type selection.
     * 
     * @return 
     */
    public String getCurrentItemItemTypeEditString();
    
}
