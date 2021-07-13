/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementHistory;
import gov.anl.aps.cdb.portal.view.objects.ItemHierarchyCache;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

/**
 * DB utility class for item elements.
 */
public class ItemElementUtility {

    private final static String ITEM_ELEMENT_NODE_TYPE = "itemElement";

    private final static String SELECTION_MENU_MODEL_ONCLICK_TEMPLATE = "#{%s.%s(itemGenericViewController.findById(%s))}";
    private final static String CLEAR_ITEM_ONCLICK_TEMPLATE = "#{%s.%s(%s)}";
    private final static String ACTIVE_LOCATION_MENU_ITEM_STYLE = "activeLocationMenuItem";
    private final static Integer MENU_MODEL_MAX_LENGTH = 25;
    private final static Integer MENU_MODEL_TEXT_WIDE_MIN = 20;
    private final static String MENU_MODEL_WIDE_STYLE_NAME = "wideMenuItem";

    private static final Logger logger = LogManager.getLogger(ItemElementUtility.class.getName());

    public static TreeNode createItemElementRoot(Item parentItem) throws CdbException {
        return createTreeRoot(parentItem, false);
    }

    public static TreeNode createItemRoot(Item parentItem) throws CdbException {
        return createTreeRoot(parentItem, true);
    }

    private static TreeNode createTreeRoot(Item parentItem, boolean useChildItem) throws CdbException {
        Object newTreeNodeDataObject;
        String nodeType;
        if (useChildItem) {
            newTreeNodeDataObject = parentItem;
            nodeType = parentItem.getDomain().getName().replace(" ", "");
        } else {
            newTreeNodeDataObject = parentItem.getSelfElement();
            nodeType = ITEM_ELEMENT_NODE_TYPE;
        }

        TreeNode treeRoot = new DefaultTreeNode(nodeType, newTreeNodeDataObject, null);
        if (parentItem == null) {
            throw new CdbException("Cannot create item element tree view: parent item is not set.");
        }

        // Use "tree branch" list to prevent circular trees
        // Whenever new item is encountered, it will be added to the tree branch list before populating
        // element node, and removed from the branch list after population is done
        // If an object is encountered twice in the tree branch, this itemates an error.
        List<Item> itemTreeBranch = new ArrayList<>();
        populateItemNode(treeRoot, parentItem, itemTreeBranch, useChildItem);
        return treeRoot;
    }

    private static void populateItemNode(TreeNode itemElementNode, Item item, List<Item> itemTreeBranch, boolean useChildItem) throws InvalidObjectState {
        List<ItemElement> itemElementList = item.getItemElementDisplayList();
        if (itemElementList == null) {
            return;
        }

        itemElementList.sort((ItemElement o1, ItemElement o2) -> {
            Float so1 = o1.getRelevantItemElementSortOrder();
            Float so2 = o2.getRelevantItemElementSortOrder();
            if (so1 == null) {
                so1 = Float.NEGATIVE_INFINITY;
            }
            if (so2 == null) {
                so2 = Float.NEGATIVE_INFINITY;
            }

            if (so1 > so2) {
                return 1;
            } else if (so1 < so2) {
                return -1;
            }

            return 0;
        });

        itemTreeBranch.add(item);
        for (ItemElement itemElement : itemElementList) {
            Item childItem = itemElement.getContainedItem();

            TreeNode childItemElementNode = null;

            if (useChildItem) {
                if (childItem != null) {
                    String nodeType = childItem.getDomain().getName().replace(" ", "");;
                    childItem.setHierarchyItemElement(itemElement);
                    childItemElementNode = new DefaultTreeNode(nodeType, childItem, itemElementNode);
                } else {
                    // no child item is linked to item element. 
                    continue;
                }
            }

            if (childItemElementNode == null) {
                childItemElementNode = new DefaultTreeNode(ITEM_ELEMENT_NODE_TYPE, itemElement, itemElementNode);
            }
            if (childItem != null) {
                if (itemTreeBranch.contains(childItem)) {
                    throw new InvalidObjectState("Cannot create item element tree view: circular child/parent relationship found with items "
                            + childItem.getName() + " and " + childItem.getName() + ".");
                }
                populateItemNode(childItemElementNode, childItem, itemTreeBranch, useChildItem);
            }

        }
        itemTreeBranch.remove(item);
    }

    public static List<ItemHierarchyCache> generateItemHierarchyCacheList(List<Item> itemList) {
        List<ItemHierarchyCache> itemHierarchyCaches = new ArrayList<>();
        for (Item item : itemList) {
            ItemHierarchyCache ihc = new ItemHierarchyCache(item);
            itemHierarchyCaches.add(ihc);
        }

        return itemHierarchyCaches;
    }

    public static void prepareItemElementHistory(List<ItemElement> originalItemElementList,
            List<ItemElement> newItemElementList, EntityInfo entityInfo) {
        for (ItemElement itemElementValue : newItemElementList) {
            int index = -1;
            if (originalItemElementList != null) {
                index = originalItemElementList.indexOf(itemElementValue);
            }

            if (index >= 0) {
                // Original element was there.
                ItemElement originalItemElement = originalItemElementList.get(index);
                prepareItemElementHistory(originalItemElement, itemElementValue, entityInfo);
            } else {
                prepareItemElementHistory(null, itemElementValue, entityInfo);

                // New item elements may be created hierarchically
                Item containedItem = itemElementValue.getContainedItem();
                if (containedItem != null) {
                    List<ItemElement> fullItemElementList = containedItem.getFullItemElementList();
                    if (fullItemElementList != null) {
                        prepareItemElementHistory(null, fullItemElementList, entityInfo);
                    }
                }
            }
        }
    }

    public static void prepareItemElementHistory(ItemElement originalItemElement, ItemElement itemElementValue, EntityInfo entityInfo) {
        if (originalItemElement != null) {
            if (!itemElementValue.equalsByIdContainedItemsAndParentItemAndHousing(originalItemElement)) {
                // Property value was modified.
                logger.debug("Item element: " + itemElementValue + " was modified, storing history");

                // Save history
                List<ItemElementHistory> itemElementHistoryList = itemElementValue.getItemElementHistoryList();
                ItemElementHistory itemElementHistory = new ItemElementHistory(itemElementValue, entityInfo);
                itemElementHistoryList.add(itemElementHistory);
            }
        } else {
            // New Item Element
            logger.debug("Adding item element history for new item element record " + itemElementValue);
            ItemElementHistory itemElementHistory = new ItemElementHistory(itemElementValue, entityInfo);
            if (itemElementValue.getItemElementHistoryList() == null) {
                itemElementValue.setItemElementHistoryList(new ArrayList<>());
            }
            itemElementValue.getItemElementHistoryList().add(itemElementHistory);
        }
    }

    /**
     * Generates hierarchy of nodes in the form of MenuModel meant to be used as
     * a model in a tiered menu.Could be used in other menus.
     *
     * @param firstLevelItemList - List of all children cached in hierarchy.
     * @param baseNodeName - String that will be displayed on the initial
     * submenu.
     * @param selectionController - [Null accepted] Controller to select item
     * @param selectionMethod - [Null accepted] Method in the controller for
     * selection controller to be called for menuitem command.
     * @param activeItemList - [Null accepted] If provided a location selected
     * style will be applied to the location that lead to the lowest location.
     * @param nullOption - Allows for clearing the selection. if null is
     * provided item cannot be cleared.
     * @return
     */
    public static DefaultMenuModel generateItemSelectionMenuModel(List<ItemHierarchyCache> firstLevelItemList,
            String baseNodeName,
            String selectionController,
            String selectionMethod,
            List<Item> activeItemList,
            String nullOption,
            String updateTarget,
            String processTarget) {
        DefaultMenuModel generatedMenuModel = new DefaultMenuModel();

        if (firstLevelItemList != null) {
            DefaultSubMenu defaultSubMenu = DefaultSubMenu.builder()
                    .label(baseNodeName)
                    .build();
            generatedMenuModel.getElements().add(defaultSubMenu);

            // Add null item 
            if (nullOption != null) {
                DefaultMenuItem nullMenuItem = DefaultMenuItem.builder()
                        .value(nullOption)
                        .command(String.format(CLEAR_ITEM_ONCLICK_TEMPLATE, selectionController, selectionMethod, "null"))
                        .update(updateTarget)
                        .process(processTarget)
                        .build();
                defaultSubMenu.getElements().add(nullMenuItem);
            }

            generateItemSelectionMenuModel(defaultSubMenu, firstLevelItemList, selectionController, selectionMethod, activeItemList, updateTarget, processTarget);
        } else {
            DefaultMenuItem menuItem = DefaultMenuItem.builder()
                    .value(baseNodeName)
                    .disabled(true)
                    .build();
            generatedMenuModel.getElements().add(menuItem);
        }

        return generatedMenuModel;
    }

    /**
     * Recursive method generates a menu model for locations based on given
     * location root tree node.
     *
     * @param submenu - SubMenu to which child tree nodes will be converted to.
     * @param locationTreeNode - Location TreeNode branch
     * @param setLocationController - [Null accepted] Controller to update item
     * location.
     * @param setLocationMethod - [Null accepted] Method in the location
     * controller to be called for menuitem command.
     * @param activeItemList - Apply location selected style to menu items in
     * the list.
     */
    private static void generateItemSelectionMenuModel(DefaultSubMenu submenu,
            List<ItemHierarchyCache> itemList,
            String setLocationController,
            String setLocationMethod,
            List<Item> activeItemList,
            String updateTarget,
            String processTarget) {

        int size = itemList.size();
        int setIndex = 0;
        DefaultSubMenu currentSubmenu = submenu;

        for (int i = 0; i < size; i++) {
            ItemHierarchyCache itemCache = itemList.get(i);

            Item item = itemCache.getParentItem();

            if (size > MENU_MODEL_MAX_LENGTH && i == setIndex * MENU_MODEL_MAX_LENGTH) {
                setIndex++;
                int nextSize = (MENU_MODEL_MAX_LENGTH * setIndex) - 1;
                if (nextSize > size) {
                    nextSize = size - 1;
                }
                currentSubmenu = DefaultSubMenu.builder().label(i + "..." + nextSize).build();
                submenu.getElements().add(currentSubmenu);
            }

            boolean applyLocationActiveStyle = false;
            if (activeItemList != null) {
                if (activeItemList.contains(item)) {
                    applyLocationActiveStyle = true;
                }
            }

            List<ItemHierarchyCache> itemChildren = itemCache.getChildrenItem();
            boolean createSubmenu = (itemChildren != null && itemChildren.size() > 0);

            if (createSubmenu) {
                DefaultSubMenu childSubmenu = DefaultSubMenu.builder()
                        .label(item.getName())
                        .build();

                if (applyLocationActiveStyle) {
                    childSubmenu.setStyleClass(ACTIVE_LOCATION_MENU_ITEM_STYLE);
                    if (activeItemList.indexOf(item) != activeItemList.size() - 1) {
                        // Still more items in hierarchy no need to highlight two reps of same item. 
                        applyLocationActiveStyle = false;
                    }
                }

                if (childSubmenu.getLabel().length() > MENU_MODEL_TEXT_WIDE_MIN) {
                    currentSubmenu.setStyleClass(MENU_MODEL_WIDE_STYLE_NAME);
                }

                currentSubmenu.getElements().add(childSubmenu);
                addMenuItemToSubmenu(childSubmenu, item,
                        setLocationController, setLocationMethod,
                        applyLocationActiveStyle, updateTarget, processTarget);

                generateItemSelectionMenuModel(childSubmenu, itemChildren, setLocationController, setLocationMethod, activeItemList, updateTarget, processTarget);
            } else {
                addMenuItemToSubmenu(currentSubmenu, item,
                        setLocationController, setLocationMethod,
                        applyLocationActiveStyle, updateTarget, processTarget);
            }

        }
    }

    /**
     * Create a MenuItem for the location provided and insert into the SubMenu
     * provided. Apply additional necessary attributes based on the input
     * parameters.
     *
     * @param currentSubmenu - Submenu to add menu item to
     * @param item
     * @param setItemController - [Null accepted] Controller to update item
     * location.
     * @param setItemMethod - [Null accepted] Method in the location controller
     * to be called for menuitem command.
     * @param applyActiveLocationStyle - Apply location selected style to menu
     * items in the list.
     */
    private static void addMenuItemToSubmenu(DefaultSubMenu currentSubmenu,
            Item item,
            String setItemController,
            String setItemMethod,
            boolean applyActiveLocationStyle,
            String updateTarget,
            String processTarget) {
        String label = item.getName();
        DefaultMenuItem menuItem = DefaultMenuItem.builder()
                .value(label)
                .build();

        if (applyActiveLocationStyle) {
            menuItem.setStyleClass(ACTIVE_LOCATION_MENU_ITEM_STYLE);
        }

        if (setItemController != null) {
            String onClick = String.format(SELECTION_MENU_MODEL_ONCLICK_TEMPLATE, setItemController, setItemMethod, item.getId());
            menuItem.setCommand(onClick);
            menuItem.setUpdate(updateTarget);
            menuItem.setProcess(processTarget);
        }

        if (label.length() > MENU_MODEL_TEXT_WIDE_MIN) {
            currentSubmenu.setStyleClass(MENU_MODEL_WIDE_STYLE_NAME);
        }

        currentSubmenu.getElements().add(menuItem);
    }

}
