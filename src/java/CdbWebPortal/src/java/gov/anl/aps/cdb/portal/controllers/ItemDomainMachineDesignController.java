/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.extensions.CableWizard;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.PortalStyles;
import gov.anl.aps.cdb.portal.controllers.extensions.BundleWizard;
import gov.anl.aps.cdb.portal.controllers.extensions.CircuitWizard;
import gov.anl.aps.cdb.portal.controllers.extensions.ImportHelperMachineDesign;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementHistory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorCableMapperItem;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorListObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainMachineDesignController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignController
        extends ItemController<ItemDomainMachineDesign, ItemDomainMachineDesignFacade, ItemDomainMachineDesignSettings>
        implements ItemDomainCableDesignWizardClient {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignController.class.getName());

    public final static String controllerNamed = "itemDomainMachineDesignController";
    private final static String cableWizardRedirectSuccess
            = "/views/itemDomainMachineDesign/list?faces-redirect=true";

    private List<ItemElementRelationship> relatedMAARCRelationshipsForCurrent = null;

    private MachineDesignConnectorCableMapperItem mdccmi;
    private List<MachineDesignConnectorListObject> mdConnectorList;

    private TreeNode searchResultsTreeNode;

    private static ItemDomainMachineDesignController apiInstance;

    // <editor-fold defaultstate="collapsed" desc="Favorites toggle variables">
    private boolean favoritesShown = false;
    private TreeNode favoriteMachineDesignTreeRootTreeNode;

    private boolean cablesShown = false;
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="Element edit variables ">
    private Item inventoryForElement = null;
    private Item catalogForElement = null;
    private Item originalForElement = null;
    protected DataModel installedInventorySelectionForCurrentElement;
    protected DataModel machineDesignTemplatesSelectionList;
    private DataModel topLevelMachineDesignSelectionList;
    private List<KeyValueObject> machineDesignNameList = null;
    private String machineDesignName = null;
    private boolean displayCreateMachineDesignFromTemplateContent = false;
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="Dual list view configuration variables ">
    private TreeNode selectedItemInListTreeTable = null;
    private TreeNode lastExpandedNode = null;

    private TreeNode currentMachineDesignListRootTreeNode = null;
    private TreeNode machineDesignTreeRootTreeNode = null;
    private TreeNode machineDesignTemplateRootTreeNode = null;
    private boolean currentViewIsTemplate = false;

    private ItemDomainMachineDesign newMdInventoryItem = null;
    private TreeNode subAssemblyRootTreeNode = null;
    private boolean currentViewIsSubAssembly = false;

    private boolean displayListConfigurationView = false;
    private boolean displayListViewItemDetailsView = false;
    private boolean displayAddMDPlaceholderListConfigurationPanel = true;
    private boolean displayAddMDFromTemplateConfigurationPanel = true;
    private boolean displayAddMDMoveExistingConfigurationPanel = true;
    private boolean displayAddCatalogItemListConfigurationPanel = true;
    private boolean displayAssignCatalogItemListConfigurationPanel = true;
    private boolean displayAssignInventoryItemListConfigurationPanel = true;
    private boolean displayMachineDesignReorderOverlayPanel = true;
    private boolean displayAddCablePanel = true;
    private boolean displayAddCableCircuitPanel = true;
    private boolean displayAddCableBundlePanel = true;

    private List<ItemDomainCatalog> catalogItemsDraggedAsChildren = null;
    private TreeNode newCatalogItemsInMachineDesignModel = null;

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Machine Design drag and drop variables">
    private static final String JS_SOURCE_MD_ID_PASSED_KEY = "sourceId";
    private static final String JS_DESTINATION_MD_ID_PASSED_KEY = "destinationId";
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="Machine Design drag and drop implementation">
    public void onDropFromJS() {
        LoginController loginController = LoginController.getInstance();
        if (loginController.isLoggedIn() == false) {
            SessionUtility.addInfoMessage("Cannot complete move.", "Please login and try again.");
            return;
        }

        String sourceIdStr = SessionUtility.getRequestParameterValue(JS_SOURCE_MD_ID_PASSED_KEY);
        String destinationIdStr = SessionUtility.getRequestParameterValue(JS_DESTINATION_MD_ID_PASSED_KEY);
        int sourceId = Integer.parseInt(sourceIdStr);
        int destId = Integer.parseInt(destinationIdStr);

        ItemDomainMachineDesign parent = findById(destId);
        ItemDomainMachineDesign child = findById(sourceId);

        // Permission check
        if (loginController.isEntityWriteable(parent.getEntityInfo()) == false) {
            SessionUtility.addErrorMessage("Insufficient privilages", "The user doesn't have permissions to item: " + parent.toString());
            return;
        }
        if (loginController.isEntityWriteable(child.getEntityInfo()) == false) {
            SessionUtility.addErrorMessage("Insufficient privilages", "The user doesn't have permissions to item: " + child.toString());
            return;
        }

        // Continue to reassignment of parent.
        setCurrent(parent);
        ItemElement currentItemElement = child.getCurrentItemElement();
        if (currentItemElement.getId() != null) {
            String uniqueName = generateUniqueElementNameForItem(parent);
            currentItemElement.setName(uniqueName);
            currentItemElement.setParentItem(parent);
        } else {
            // Dragging in top level
            currentItemElement = createItemElement(parent);
            currentItemElement.setContainedItem(child);
        }

        prepareAddItemElement(parent, currentItemElement);

        update();

        child = findById(sourceId);
        expandToSpecificMachineDesignItem(child);
    }

    // </editor-fold>   
    // <editor-fold defaultstate="collapsed" desc="Undocumented Fold">
    private String mdSearchString;
    private List<TreeNode> searchResultsList;
    private boolean searchCollapsed;

    @EJB
    ItemDomainMachineDesignFacade itemDomainMachineDesignFacade;

    public static ItemDomainMachineDesignController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (ItemDomainMachineDesignController) SessionUtility.findBean(controllerNamed);
        } else {
            return getApiInstance();
        }
    }

    public static synchronized ItemDomainMachineDesignController getApiInstance() {
        if (apiInstance == null) {
            apiInstance = new ItemDomainMachineDesignController();
            apiInstance.prepareApiInstance();
        }
        return apiInstance;
    }

    @Override
    protected void loadEJBResourcesManually() {
        super.loadEJBResourcesManually();
        itemDomainMachineDesignFacade = ItemDomainMachineDesignFacade.getInstance();
    }

    public boolean isItemInventory(Item item) {
        return item instanceof ItemDomainInventory;
    }

    public boolean isItemCatalog(Item item) {
        return item instanceof ItemDomainCatalog;
    }

    public static boolean isItemMachineDesign(Item item) {
        return item instanceof ItemDomainMachineDesign;
    }

    public boolean isItemMachineDesignAndTemplate(Item item) {
        if (item instanceof ItemDomainMachineDesign) {
            return ((ItemDomainMachineDesign) item).getIsItemTemplate();
        }

        return false;
    }

    public String getMdNodeRepIcon(ItemElement ie) {
        Item containedItem = ie.getContainedItem();
        if (containedItem != null) {
            return getItemRepIcon(containedItem);
        }
        Item parentItem = ie.getParentItem();
        if (parentItem != null) {
            return getItemRepIcon(parentItem);
        }
        ItemConnector conn = ie.getMdConnector();
        if (conn != null) {
            return PortalStyles.itemConnectorIcon.getValue();
        }

        return "";
    }

    public String getItemRepIcon(Item item) {
        if (isItemMachineDesignAndTemplate(item)) {
            return PortalStyles.machineDesingTemplateIcon.getValue();
        } else {
            return item.getDomain().getDomainRepIcon();
        }
    }

    public boolean isCollapsedRelatedMAARCItemsForCurrent() {
        return getRelatedMAARCRelationshipsForCurrent().size() < 1;
    }

    public List<ItemElementRelationship> getRelatedMAARCRelationshipsForCurrent() {
        if (relatedMAARCRelationshipsForCurrent == null) {
            relatedMAARCRelationshipsForCurrent = ItemDomainMAARCController.getRelatedMAARCRelationshipsForItem(getCurrent());
        }

        return relatedMAARCRelationshipsForCurrent;
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
        currentMachineDesignListRootTreeNode = null;
        machineDesignTemplateRootTreeNode = null;
        machineDesignTreeRootTreeNode = null;
        subAssemblyRootTreeNode = null;
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="Dual list view configuration implementation ">
    private void setTreeNodeTypeMachineDesignTreeList(TreeNode treeNode) {
        Object data = treeNode.getData();
        ItemElement ie = (ItemElement) data;
        Item item = ie.getContainedItem();
        ItemDomainMachineDesign mdItem = null;
        if (item instanceof ItemDomainMachineDesign) {
            mdItem = (ItemDomainMachineDesign) item;
        }
        String domain = item.getDomain().getName();
        int itemDomainId = item.getDomain().getId();
        String defaultDomainAssignment = domain.replace(" ", "");
        if (isItemMachineDesignAndTemplate(item)) {
            defaultDomainAssignment += "Template";
        }

        if (treeNode.getParent().getData() != null) {
            Object parentData = treeNode.getParent().getData();
            ItemElement parentIe = (ItemElement) parentData;
            Item parentItem = parentIe.getContainedItem();
            int parentDomainId = parentItem.getDomain().getId();

            if (parentDomainId == ItemDomainName.CATALOG_ID
                    || parentDomainId == ItemDomainName.INVENTORY_ID) {
                // Sub item of a catalog or an inventory 
                defaultDomainAssignment += "Member";
            } else if (parentDomainId == ItemDomainName.MACHINE_DESIGN_ID) {
                boolean isMDMember = false;
                if (isItemMachineDesignAndTemplate(item)) {
                    if (isItemMachineDesignAndTemplate(parentItem)) {
                        // parent is template -- default name is correct
                        defaultDomainAssignment += "Member";
                        isMDMember = true;
                    } else {
                        // parent is machine desing 
                        defaultDomainAssignment += "Placeholder";
                    }
                } else if (itemDomainId == ItemDomainName.MACHINE_DESIGN_ID) {
                    // machine design sub item of a machine design 
                    defaultDomainAssignment += "Member";
                    isMDMember = true;
                } else if (itemDomainId == ItemDomainName.CATALOG_ID) {
                    // catalog sub item of a machine design 
                    if (isItemMachineDesignAndTemplate(parentItem)) {
                        // catalog sub item of a machine design template
                        defaultDomainAssignment += "TemplateMember";
                    }
                }
            }
        }
        if (mdItem != null) {
            // Verify if contains an item in secondary contained item
            Item assignedItem = mdItem.getAssignedItem();
            if (assignedItem != null) {
                String ci2Domain = assignedItem.getDomain().getName();
                ci2Domain = ci2Domain.replace(" ", "");
                defaultDomainAssignment += ci2Domain;
            }
        }
        treeNode.setType(defaultDomainAssignment);
    }

    public TreeNode getCurrentMachineDesignListRootTreeNode() {
        if (currentMachineDesignListRootTreeNode == null) {
            if (currentViewIsTemplate) {
                currentMachineDesignListRootTreeNode = getMachineDesignTemplateRootTreeNode();
            } else if (currentViewIsSubAssembly) {
                currentMachineDesignListRootTreeNode = getMachineDesignFixtureRootTreeNode();
            } else {
                if (favoritesShown) {
                    currentMachineDesignListRootTreeNode = getFavoriteMachineDesignTreeRootTreeNode();
                } else {
                    currentMachineDesignListRootTreeNode = getMachineDesignTreeRootTreeNode();
                }
            }
        }
        return currentMachineDesignListRootTreeNode;
    }

    public TreeNode getMachineDesignTreeRootTreeNode() {
        if (machineDesignTreeRootTreeNode == null) {
            machineDesignTreeRootTreeNode = loadMachineDesignRootTreeNode(false);
        }
        return machineDesignTreeRootTreeNode;
    }

    public TreeNode getMachineDesignTemplateRootTreeNode() {
        if (machineDesignTemplateRootTreeNode == null) {
            machineDesignTemplateRootTreeNode = loadMachineDesignRootTreeNode(true);
        }
        return machineDesignTemplateRootTreeNode;
    }

    public TreeNode getMachineDesignFixtureRootTreeNode() {
        if (subAssemblyRootTreeNode == null) {
            subAssemblyRootTreeNode = new DefaultTreeNode();

            ItemDomainMachineDesign current = getCurrent();

            ItemDomainMachineDesign parentMachineDesign = current;
            while (parentMachineDesign.getParentMachineDesign() != null) {
                parentMachineDesign = parentMachineDesign.getParentMachineDesign();
            }

            // get latest version
            parentMachineDesign = findById(parentMachineDesign.getId());

            expandTreeChildren(parentMachineDesign, subAssemblyRootTreeNode);
            subAssemblyRootTreeNode.getChildren().get(0).setExpanded(true);

        }
        return subAssemblyRootTreeNode;
    }

    public TreeNode loadMachineDesignRootTreeNode(Boolean isTemplate) {
        TreeNode rootTreeNode = new DefaultTreeNode();
        List<ItemDomainMachineDesign> itemsWithoutParents
                = getItemsWithoutParents();

        for (Item item : itemsWithoutParents) {
            boolean skip = false;
            if (item.getEntityTypeList().isEmpty() == false) {
                skip = true;
                if (isTemplate) {
                    skip = !(item.getIsItemTemplate() == isTemplate);
                }
            } else {
                skip = isTemplate;
            }

            if (skip == false) {
                expandTreeChildren(item, rootTreeNode);
            }
        }

        return rootTreeNode;
    }

    private void expandTreeChildren(Item item, TreeNode rootTreeNode) {
        ItemElement element = new ItemElement();
        ItemElement selfElement = item.getSelfElement();
        Float sortOrder = selfElement.getSortOrder();
        element.setContainedItem(item);
        element.setSortOrder(sortOrder);
        TreeNode parent = new DefaultTreeNode(element);
        rootTreeNode.getChildren().add(parent);
        parent.setParent(rootTreeNode);
        setTreeNodeTypeMachineDesignTreeList(parent);
        expandTreeChildren(parent);
    }

    private void expandTreeChildren(TreeNode treeNode) {
        Object data = treeNode.getData();
        ItemElement ie = (ItemElement) data;
        Item item = ie.getContainedItem();

        // TODO do not check for cables when in template view 
        boolean parentIsTemplate = isItemMachineDesignAndTemplate(item);

        if (item != null) {
            List<ItemElement> itemElementList = null;
            if (item instanceof ItemDomainMachineDesign) {
                ItemDomainMachineDesign idm = (ItemDomainMachineDesign) item;
                itemElementList = idm.getCombinedItemElementList(ie);
            } else {
                itemElementList = item.getItemElementDisplayList();
            }
            for (ItemElement itemElement : itemElementList) {
                TreeNode newTreeNode = new DefaultTreeNode(itemElement);
                Item containedItem = itemElement.getContainedItem();

                if (cablesShown) {
                    if (containedItem instanceof ItemDomainMachineDesign) {
                        List<MachineDesignConnectorListObject> connList = getMdConnectorListForItem((ItemDomainMachineDesign) containedItem);

                        for (MachineDesignConnectorListObject connObj : connList) {
                            TreeNode parentNode = newTreeNode;

                            if (connObj.getItemConnector() != null) {
                                ItemConnector itemConnector = connObj.getItemConnector();
                                ItemElement mockIE = new ItemElement();
                                mockIE.setMdConnector(itemConnector);
                                TreeNode connectorNode = new DefaultTreeNode(mockIE);
                                connectorNode.setType("Connector");

                                parentNode.getChildren().add(connectorNode);
                                parentNode = connectorNode;
                            }

                            if (connObj.getCableItem() != null) {
                                ItemDomainCableDesign cableItem = connObj.getCableItem();
                                ItemElement mockIE = new ItemElement();
                                mockIE.setContainedItem(cableItem);

                                TreeNode cableNode = new DefaultTreeNode(mockIE);
                                cableNode.setType(ItemDomainName.cableDesign.getValue());
                                parentNode.getChildren().add(cableNode);
                            }
                        }
                    }
                }

                treeNode.getChildren().add(newTreeNode);
                newTreeNode.setParent(treeNode);

                if (containedItem != null) {
                    setTreeNodeTypeMachineDesignTreeList(newTreeNode);
                    boolean skipExpansion = false;
                    if (!parentIsTemplate) {
                        if (isItemMachineDesignAndTemplate(containedItem)) {
                            // Template within a non-template (Placeholder)
                            skipExpansion = true;
                        }
                    }
                    if (!skipExpansion) {
                        expandTreeChildren(newTreeNode);
                    }
                } else {
                    newTreeNode.setType("Blank");
                }

            }
        }
    }

    public void searchMachineDesign() {
        Pattern searchPattern = Pattern.compile(Pattern.quote(mdSearchString), Pattern.CASE_INSENSITIVE);

        TreeNode mdRoot = getCurrentMachineDesignListRootTreeNode();

        searchResultsList = new ArrayList();

        searchMachineDesign(mdRoot, searchPattern, searchResultsList);

        if (searchResultsList.size() > 0) {
            for (TreeNode node : searchResultsList) {
                TreeNode parent = node.getParent();
                while (parent != null) {
                    parent.setExpanded(true);
                    parent = parent.getParent();
                }
            }

            selectItemInTreeTable(searchResultsList.get(0));
        }
        searchCollapsed = true;
    }

    private void searchMachineDesign(TreeNode parentNode, Pattern searchPattern, List<TreeNode> results) {
        Object data = parentNode.getData();
        parentNode.setExpanded(false);
        if (data != null) {
            ItemElement ie = (ItemElement) data;
            Item parentItem = ie.getContainedItem();
            if (parentItem != null) {
                SearchResult search = parentItem.search(searchPattern);
                if (search.getObjectAttributeMatchMap().size() > 0) {
                    results.add(parentNode);
                    ie.setRowStyle(SearchResult.SEARCH_RESULT_ROW_STYLE);
                } else {
                    ie.setRowStyle(null);
                }
            }
        }

        for (TreeNode node : parentNode.getChildren()) {
            searchMachineDesign(node, searchPattern, results);
        }
    }

    public void selectNextResult() {
        if (searchResultsList != null && searchResultsList.size() > 0) {
            TreeNode selectedItemInListTreeTable = getSelectedItemInListTreeTable();
            int indx = 0;
            if (selectedItemInListTreeTable != null) {
                for (int i = 0; i < searchResultsList.size(); i++) {
                    TreeNode node = searchResultsList.get(i);
                    if (node.equals(selectedItemInListTreeTable)) {
                        indx = i + 1;
                        break;
                    }
                }

                // Last index
                if (indx == searchResultsList.size() - 1) {
                    indx = 0;
                }
            }

            TreeNode result = searchResultsList.get(indx);
            selectItemInTreeTable(result);
        }
    }

    public String getMdSearchString() {
        return mdSearchString;
    }

    public void setMdSearchString(String mdSearchString) {
        this.mdSearchString = mdSearchString;
    }

    public boolean isSearchCollapsed() {
        return searchCollapsed;
    }

    public void setSearchCollapsed(boolean searchCollapsed) {
        this.searchCollapsed = searchCollapsed;
    }

    public void expandSelectedTreeNode() {
        TreeNode selectedItemInListTreeTable = getSelectedItemInListTreeTable();
        if (selectedItemInListTreeTable != null) {
            boolean expanded = !selectedItemInListTreeTable.isExpanded();
            expandAllChildren(selectedItemInListTreeTable, expanded);
        } else {
            SessionUtility.addInfoMessage("No tree node is selected", "Select a tree node and try again.");
        }
    }

    private void expandAllChildren(TreeNode treeNode, boolean expanded) {
        treeNode.setExpanded(expanded);

        List<TreeNode> children = treeNode.getChildren();
        if (children != null) {
            for (TreeNode child : children) {
                expandAllChildren(child, expanded);
            }
        }
    }

    public TreeNode getSelectedItemInListTreeTable() {
        return selectedItemInListTreeTable;
    }

    public void setSelectedItemInListTreeTable(TreeNode selectedItemInListTreeTable) {
        this.selectedItemInListTreeTable = selectedItemInListTreeTable;
    }

    private void selectItemInTreeTable(TreeNode newSelection) {
        TreeNode selectedItemInListTreeTable = getSelectedItemInListTreeTable();
        if (selectedItemInListTreeTable != null) {
            selectedItemInListTreeTable.setSelected(false);
        }

        newSelection.setSelected(true);
        setSelectedItemInListTreeTable(newSelection);
    }

    public boolean isSelectedItemInListReorderable() {
        if (isSelectedItemInListTreeViewWriteable()) {
            ItemDomainMachineDesign selectedItem = getItemFromSelectedItemInTreeTable();
            List<ItemElement> itemElementDisplayList = selectedItem.getItemElementDisplayList();
            return itemElementDisplayList.size() > 1;
        }
        return false;
    }

    public boolean isSelectedItemInListTreeViewWriteable() {
        if (selectedItemInListTreeTable != null) {
            ItemDomainMachineDesign selectedItem = getItemFromSelectedItemInTreeTable();
            if (selectedItem != null) {
                LoginController instance = LoginController.getInstance();
                return instance.isEntityWriteable(selectedItem.getEntityInfo());
            }
        }
        return false;
    }

    public boolean isParentOfSelectedItemInListTreeViewWriteable() {
        if (selectedItemInListTreeTable != null) {
            ItemElement itemElement = (ItemElement) selectedItemInListTreeTable.getData();
            Item parentItem = itemElement.getParentItem();
            if (parentItem != null) {
                LoginController instance = LoginController.getInstance();
                return instance.isEntityWriteable(parentItem.getEntityInfo());
            } else {
                // Top level
                return isSelectedItemInListTreeViewWriteable();
            }
        }
        return false;
    }

    public String showDetailsForCurrentSelectedTreeNode() {
        ItemDomainMachineDesign item = getItemFromSelectedItemInTreeTable();

        if (item != null) {
            setCurrent(item);
            return viewForCurrentEntity() + "&mode=detail";
        }

        SessionUtility.addErrorMessage("Error", "Cannot load details for a non machine design.");
        return null;
    }

    public void resetListConfigurationVariables() {
        searchCollapsed = true;
        displayListConfigurationView = false;
        displayListViewItemDetailsView = false;
        displayAddMDPlaceholderListConfigurationPanel = false;
        displayAddMDFromTemplateConfigurationPanel = false;
        displayAddMDMoveExistingConfigurationPanel = false;
        displayAddCatalogItemListConfigurationPanel = false;
        displayAssignCatalogItemListConfigurationPanel = false;
        displayAssignInventoryItemListConfigurationPanel = false;
        displayMachineDesignReorderOverlayPanel = false;
        displayAddCablePanel = false;
        displayAddCableCircuitPanel = false;
        displayAddCableBundlePanel = false;
        catalogItemsDraggedAsChildren = null;
        newCatalogItemsInMachineDesignModel = null;
        currentMachineDesignListRootTreeNode = null;
    }

    @Override
    public ItemDomainMachineDesign createEntityInstance() {
        ItemDomainMachineDesign item = super.createEntityInstance();
        return item;
    }

    public ItemDomainMachineDesign createEntityInstanceForDualTreeView() {
        ItemDomainMachineDesign newInstance = createEntityInstance();

        Item selectedItem = getItemFromSelectedItemInTreeTable();
        newInstance.setItemProjectList(selectedItem.getItemProjectList());

        if (currentViewIsTemplate) {
            try {
                List<EntityType> entityTypeList = new ArrayList<>();
                EntityType templateEntity = entityTypeFacade.findByName(EntityTypeName.template.getValue());
                entityTypeList.add(templateEntity);
                newInstance.setEntityTypeList(entityTypeList);
            } catch (CdbException ex) {
                LOGGER.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return null;
            }
        } else if (currentViewIsSubAssembly) {
            assignInventoryAttributes(newInstance);
        }

        return newInstance;
    }

    public void prepareAddPlaceholder() {
        prepareAddNewMachineDesignListConfiguration();

        displayAddMDPlaceholderListConfigurationPanel = true;
        ItemDomainMachineDesign newItem = createEntityInstanceForDualTreeView();

        currentEditItemElement.setContainedItem(newItem);
    }

    public void prepareAddMdFromPlaceholder() {
        prepareAddNewMachineDesignListConfiguration();
        displayAddMDFromTemplateConfigurationPanel = true;
        currentEditItemElementSaveButtonEnabled = true;
    }

    public void prepareAddMdFromCatalog() {
        prepareAddNewMachineDesignListConfiguration();
        displayAddCatalogItemListConfigurationPanel = true;
    }

    public void prepareAddMoveExistingMd() {
        prepareAddNewMachineDesignListConfiguration();
        displayAddMDMoveExistingConfigurationPanel = true;
    }

    public boolean isDisplayFollowInstructionOnRightOnBlockUI() {
        return displayAddMDMoveExistingConfigurationPanel
                || displayAddMDFromTemplateConfigurationPanel
                || displayAddMDPlaceholderListConfigurationPanel
                || displayAssignCatalogItemListConfigurationPanel
                || displayAssignInventoryItemListConfigurationPanel;
    }

    public boolean isDisplayListConfigurationView() {
        return displayListConfigurationView;
    }

    public boolean isDisplayListViewItemDetailsView() {
        return displayListViewItemDetailsView;
    }

    public boolean isDisplayAddMDPlaceholderListConfigurationPanel() {
        return displayAddMDPlaceholderListConfigurationPanel;
    }

    public boolean isDisplayAddMDFromTemplateConfigurationPanel() {
        return displayAddMDFromTemplateConfigurationPanel;
    }

    public boolean isDisplayAddMDMoveExistingConfigurationPanel() {
        return displayAddMDMoveExistingConfigurationPanel;
    }

    public boolean isDisplayAddCatalogItemListConfigurationPanel() {
        return displayAddCatalogItemListConfigurationPanel;
    }

    public boolean isDisplayAssignCatalogItemListConfigurationPanel() {
        return displayAssignCatalogItemListConfigurationPanel;
    }

    public boolean isDisplayAssignInventoryItemListConfigurationPanel() {
        return displayAssignInventoryItemListConfigurationPanel;
    }

    public boolean isDisplayMachineDesignReorderOverlayPanel() {
        return displayMachineDesignReorderOverlayPanel;
    }

    public boolean isDisplayAddCablePanel() {
        return displayAddCablePanel;
    }

    public boolean isDisplayAddCableCircuitPanel() {
        return displayAddCableCircuitPanel;
    }

    public boolean isDisplayAddCableBundlePanel() {
        return displayAddCableBundlePanel;
    }

    private void updateCurrentUsingSelectedItemInTreeTable() {
        setCurrent(getItemFromSelectedItemInTreeTable());
    }

    private ItemDomainMachineDesign getItemFromSelectedItemInTreeTable() {
        if (selectedItemInListTreeTable != null) {
            ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();
            Item item = element.getContainedItem();

            if (item instanceof ItemDomainMachineDesign) {
                return (ItemDomainMachineDesign) item;
            }
        }

        return null;
    }

    public void unlinkContainedItem2ToDerivedFromItem() {
        ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();

        ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) element.getContainedItem();

        Item assignedItem = mdItem.getAssignedItem();
        Item derivedFromItem = assignedItem.getDerivedFromItem();

        mdItem.setAssignedItem(derivedFromItem);

        setCurrent(mdItem);
        update();

        resetListDataModel();
        expandToSpecificTreeNode(selectedItemInListTreeTable);
    }

    public void unlinkContainedItem2FromSelectedItem() {
        ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();

        ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) element.getContainedItem();
        Item originalContainedItem = mdItem.getAssignedItem();

        mdItem.setAssignedItem(null);

        updateTemplateReferenceElementContainedItem2(element, originalContainedItem, null);

        setCurrent(mdItem);
        update();

        resetListDataModel();
        expandToSpecificTreeNode(selectedItemInListTreeTable);
    }

    public void detachSelectedItemFromHierarchyInDualView() {
        ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();

        Item containedItem = element.getContainedItem();
        Integer detachedDomainId = containedItem.getDomain().getId();

        ItemElementController instance = ItemElementController.getInstance();

        if (currentViewIsTemplate) {
            List<ItemElement> derivedFromItemElementList = element.getDerivedFromItemElementList();
            boolean needsUpdate = false;
            for (ItemElement itemElement : derivedFromItemElementList) {
                if (!containedItem.equals(itemElement.getContainedItem())) {
                    // fullfilled Element
                    itemElement.setDerivedFromItemElement(null);
                    needsUpdate = true;

                    try {
                        instance.performUpdateOperations(itemElement);
                    } catch (Exception ex) {
                        LOGGER.error(ex);
                        SessionUtility.addErrorMessage("Error", ex.getMessage());
                    }
                }
            }
            if (needsUpdate) {
                element = instance.findById(element.getId());
            }
        }

        instance.destroy(element);

        selectedItemInListTreeTable = selectedItemInListTreeTable.getParent();

        resetListDataModel();

        expandToSpecificTreeNode(selectedItemInListTreeTable);
        if (detachedDomainId == ItemDomainName.MACHINE_DESIGN_ID) {
            for (TreeNode node : getCurrentMachineDesignListRootTreeNode().getChildren()) {
                ItemElement ie = (ItemElement) node.getData();
                Item ci = ie.getContainedItem();
                if (containedItem.equals(ci)) {
                    node.setSelected(true);
                    selectedItemInListTreeTable = node;
                    break;
                }
            }
        }
    }

    public void deleteSelectedMachineDesignItemFromDualView() {
        updateCurrentUsingSelectedItemInTreeTable();

        destroy();
    }

    @Deprecated
    /**
     * Templates are only created fully and only previously partially created md
     * from templates will utilize this.
     */
    public void prepareFullfilPlaceholder() {
        // Element with template to be fullfilled
        ItemElement templateElement = (ItemElement) selectedItemInListTreeTable.getData();

        // Select Parent where the template will be created 
        selectedItemInListTreeTable = selectedItemInListTreeTable.getParent();

        // Execute standard add template function 
        prepareAddMdFromPlaceholder();

        // Remove the template element                 
        getCurrent().removeItemElement(templateElement);

        // Select current template 
        templateToCreateNewItem = (ItemDomainMachineDesign) templateElement.getContainedItem();
        generateTemplateForElementMachineDesignNameVars();
    }

    public void prepareAssignInventoryMachineDesignListConfiguration() {
        currentEditItemElement = (ItemElement) selectedItemInListTreeTable.getData();
        catalogForElement = currentEditItemElement.getCatalogItem();

        prepareUpdateInstalledInventoryItem();

        displayAssignInventoryItemListConfigurationPanel = true;
        displayListConfigurationView = true;
    }

    public void assignInventoryMachineDesignListConfiguration() {
        updateInstalledInventoryItem();

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }

    private void updateItemElement(ItemElement itemElement) {
        ItemElementController itemElementController = ItemElementController.getInstance();
        itemElementController.setCurrent(itemElement);
        itemElementController.update();
    }

    public void unassignInventoryMachineDesignListConfiguration() {
        ItemElement itemElement = (ItemElement) selectedItemInListTreeTable.getData();

        Item containedItem = itemElement.getContainedItem();

        if (containedItem instanceof ItemDomainInventory) {
            itemElement.setContainedItem(containedItem.getDerivedFromItem());

            updateItemElement(itemElement);
        }

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();

    }

    public void prepareAddNewMachineDesignListConfiguration() {
        updateCurrentUsingSelectedItemInTreeTable();

        displayListConfigurationView = true;

        prepareCreateSingleItemElementSimpleDialog();
    }

    public void completeAddNewMachineDesignListConfiguration() {
        resetListConfigurationVariables();
    }

    public String saveTreeListMachineDesignItem() {
        ItemElement ref = currentEditItemElement;
        saveCreateSingleItemElementSimpleDialog();

        for (ItemElement element : current.getItemElementDisplayList()) {
            if (element.getName().equals(ref.getName())) {
                ref = element;
                break;
            }
        }

        cloneNewTemplateElementForItemsDerivedFromItem(ref);

        expandToSelectedTreeNodeAndSelectChildItemElement(ref);

        return currentDualViewList();
    }

    private void cloneNewTemplateElementForItemsDerivedFromItem(ItemElement newTemplateElement) {
        if (currentViewIsTemplate) {
            Item parentItem = newTemplateElement.getParentItem();
            List<Item> items = getItemsCreatedFromTemplateItem(parentItem);
            cloneNewTemplateElementForItemsDerivedFromItem(newTemplateElement, items);
        }
    }

    private void cloneNewTemplateElementForItemsDerivedFromItem(ItemElement newTemplateElement, List<Item> derivedItems) {
        if (currentViewIsTemplate) {
            for (int i = 0; i < derivedItems.size(); i++) {
                Item item = derivedItems.get(i);
                String uniqueName = generateUniqueElementNameForItem((ItemDomainMachineDesign) item);
                ItemElement newItemElement = cloneCreateItemElement(newTemplateElement, item, true, true);
                newItemElement.setName(uniqueName);
                try {
                    ItemDomainMachineDesign origCurrent = current;
                    performUpdateOperations((ItemDomainMachineDesign) item);
                    // Update pointer to latest version of the item. 
                    derivedItems.set(i, current);
                    current = origCurrent;
                } catch (CdbException ex) {
                    SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                    LOGGER.error(ex);
                } catch (Exception ex) {
                    SessionUtility.addErrorMessage("Error", ex.getMessage());
                    LOGGER.error(ex);
                }
            }
        }
    }

    private void expandToSpecificMachineDesignItem(ItemDomainMachineDesign item) {

        TreeNode machineDesignTreeRootTreeNode = getCurrentMachineDesignListRootTreeNode();

        if (selectedItemInListTreeTable != null) {
            selectedItemInListTreeTable.setSelected(false);
            selectedItemInListTreeTable = null;
        }

        TreeNode selectedNode = expandToSpecificMachineDesignItem(machineDesignTreeRootTreeNode, item);
        selectedItemInListTreeTable = selectedNode;
    }

    /**
     * Expands the parent nodes in the supplied tree above the specified machine
     * design item. Returns the TreeNode for the specified item, so that the
     * caller can call select to highlight it if appropriate.
     *
     * @param machineDesignTreeRootTreeNode Root node of machine design
     * hierarchy.
     * @param item Child node to expand the nodes above.
     * @return
     */
    public static TreeNode expandToSpecificMachineDesignItem(
            TreeNode machineDesignTreeRootTreeNode,
            ItemDomainMachineDesign item) {

        Stack<ItemDomainMachineDesign> machineDesingItemStack = new Stack<>();

        machineDesingItemStack.push(item);

        List<Item> parentItemList = getParentItemList(item);

        while (parentItemList != null) {
            ItemDomainMachineDesign parentItem = null;
            for (Item ittrItem : parentItemList) {
                if (ittrItem instanceof ItemDomainMachineDesign) {
                    // Machine design items should only have one parent
                    parentItem = (ItemDomainMachineDesign) parentItemList.get(0);
                    break;
                }
            }

            if (parentItem != null) {
                machineDesingItemStack.push(parentItem);
                parentItemList = getParentItemList(parentItem);
            } else {
                parentItemList = null;
            }
        }

        List<TreeNode> children = machineDesignTreeRootTreeNode.getChildren();

        TreeNode result = null;

        while (children != null && machineDesingItemStack.size() > 0) {
            ItemDomainMachineDesign pop = machineDesingItemStack.pop();

            for (TreeNode treeNode : children) {
                ItemElement data = (ItemElement) treeNode.getData();
                Item containedItem = data.getContainedItem();
                if (isItemMachineDesign(containedItem)) {
                    if (containedItem.equals(pop)) {
                        if (machineDesingItemStack.isEmpty()) {
                            result = treeNode;
                            treeNode.setSelected(true);
                            children = null;
                            break;
                        } else {
                            treeNode.setExpanded(true);
                            children = treeNode.getChildren();
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private void expandToSelectedTreeNodeAndSelectChildItemElement(ItemElement element) {
        if (selectedItemInListTreeTable != null) {
            selectedItemInListTreeTable.setSelected(false);
        }

        expandToSpecificTreeNode(selectedItemInListTreeTable);

        for (TreeNode treeNode : lastExpandedNode.getChildren()) {
            if (treeNode.getData().equals(element)) {
                selectedItemInListTreeTable = treeNode;
                treeNode.setSelected(true);
                break;
            }
        }
    }

    private void expandToSpecificTreeNodeAndSelect(TreeNode treeNode) {
        expandToSpecificTreeNode(treeNode);
        selectedItemInListTreeTable = lastExpandedNode;
        lastExpandedNode.setSelected(true);
    }

    private void expandToSelectedTreeNodeAndSelect() {
        expandToSpecificTreeNodeAndSelect(selectedItemInListTreeTable);
    }

    private void expandToSpecificTreeNode(TreeNode treeNode) {
        lastExpandedNode = null;

        if (treeNode.getParent() != null) {
            // No need to get top most parent. 
            if (treeNode.getParent().getParent() != null) {
                expandToSpecificTreeNode(treeNode.getParent());
            }
        }
        if (lastExpandedNode == null) {
            lastExpandedNode = getCurrentMachineDesignListRootTreeNode();
        }

        ItemElement itemElement = (ItemElement) treeNode.getData();
        Item item = itemElement.getContainedItem();

        for (TreeNode ittrTreeNode : lastExpandedNode.getChildren()) {
            ItemElement element = (ItemElement) ittrTreeNode.getData();
            Item ittrItem = element.getContainedItem();
            if (item.equals(ittrItem)) {
                ittrTreeNode.setExpanded(true);
                lastExpandedNode = ittrTreeNode;
                break;
            }
        }
    }

    public void prepareAddNewCatalogListConfiguration() {
        updateCurrentUsingSelectedItemInTreeTable();

        displayListConfigurationView = true;
        displayAddCatalogItemListConfigurationPanel = true;
    }

    public void prepareReorderTopLevelMachineDesignElements() {
        ItemDomainMachineDesign mockTopLevelMachineDesign = new ItemDomainMachineDesign();
        mockTopLevelMachineDesign.setFullItemElementList(new ArrayList<>());
        mockTopLevelMachineDesign.setDomain(getDefaultDomain());

        TreeNode currentMachineDesignListRootTreeNode = getCurrentMachineDesignListRootTreeNode();

        List<TreeNode> currentTopLevels = currentMachineDesignListRootTreeNode.getChildren();

        for (TreeNode node : currentTopLevels) {
            ItemElement data = (ItemElement) node.getData();
            ItemElement mockItemElement = new ItemElement();
            mockItemElement.setName("MOCK element");
            mockItemElement.setContainedItem(data.getContainedItem());
            mockItemElement.setParentItem(mockTopLevelMachineDesign);

            mockTopLevelMachineDesign.getFullItemElementList().add(mockItemElement);
        }

        mockTopLevelMachineDesign.getFullItemElementList().sort(new Comparator<ItemElement>() {
            @Override
            public int compare(ItemElement o1, ItemElement o2) {
                Float sortOrder = o1.getSortOrder();
                Float sortOrder1 = o2.getSortOrder();

                if (ObjectUtility.equals(o1, o2)) {
                    return 0;
                }

                if (sortOrder == null && sortOrder1 != null) {
                    return -1;
                }

                if (sortOrder != null && sortOrder1 == null) {
                    return 1;
                }

                if (sortOrder > sortOrder1) {
                    return 1;
                }
                return -1;
            }
        });

        setCurrent(mockTopLevelMachineDesign);

        displayMachineDesignReorderOverlayPanel = true;
    }

    public void prepareReorderMachineDesignElements() {
        updateCurrentUsingSelectedItemInTreeTable();

        displayMachineDesignReorderOverlayPanel = true;
    }

    public String revertReorderMachineDesignElement() {
        if (hasElementReorderChangesForCurrent) {
            resetListConfigurationVariables();
            resetListDataModel();
            expandToSelectedTreeNodeAndSelect();
        }
        return currentDualViewList();
    }

    public String saveReorderMachineDesignElement() {
        ItemDomainMachineDesign current = getCurrent();
        if (current.getId() == null) {
            // not in DB. Simulated list. Set sort order on self element. Perform multi-item save
            List<ItemDomainMachineDesign> itemsToUpdate = new ArrayList<>();
            List<ItemElement> itemElements = current.getItemElementDisplayList();
            for (ItemElement itemElement : itemElements) {
                Float sortOrder = itemElement.getSortOrder();

                ItemDomainMachineDesign sortedItem = (ItemDomainMachineDesign) itemElement.getContainedItem();
                ItemElement selfElement = sortedItem.getSelfElement();
                selfElement.setSortOrder(sortOrder);

                itemsToUpdate.add(sortedItem);
            }

            try {
                updateList(itemsToUpdate);
            } catch (CdbException ex) {
                LOGGER.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }

            return currentDualViewList();
        }

        if (isItemMachineDesignAndTemplate(getCurrent())) {
            ItemDomainMachineDesign template = getCurrent();
            for (ItemElement ie : template.getItemElementDisplayList()) {
                for (ItemElement derivedIe : ie.getDerivedFromItemElementList()) {
                    derivedIe.setSortOrder(ie.getSortOrder());
                }
            }
        }
        update();
        expandToSelectedTreeNodeAndSelect();
        return currentDualViewList();
    }

    public void prepareAssignCatalogListConfiguration() {
        updateCurrentUsingSelectedItemInTreeTable();
        currentEditItemElement = (ItemElement) selectedItemInListTreeTable.getData();

        displayListConfigurationView = true;
        displayAssignCatalogItemListConfigurationPanel = true;
    }

    public String completeAssignCatalogListConfiguration() {
        if (currentEditItemElement != null
                && catalogForElement != null) {

            ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) currentEditItemElement.getContainedItem();

            Item assignedItem = mdItem.getAssignedItem();
            mdItem.setAssignedItem(catalogForElement);

            updateTemplateReferenceElementContainedItem2(currentEditItemElement, assignedItem, catalogForElement);

            setCurrent(mdItem);
            update();

            resetListConfigurationVariables();
            resetListDataModel();
            expandToSelectedTreeNodeAndSelect();
            return currentDualViewList();
        } else {
            SessionUtility.addErrorMessage("Error", "Please select catalog item and try again.");
        }
        return null;
    }

    /**
     * Updates contained item on elements created from template if update is
     * valid.
     *
     * @param currentElement
     * @param newAssignedItem
     */
    private void updateTemplateReferenceElementContainedItem2(ItemElement currentElement,
            Item originalContainedItem,
            Item newAssignedItem) {

        Item mdItem = currentElement.getContainedItem();

        if (currentViewIsTemplate && isItemMachineDesignAndTemplate(mdItem)) {
            List<ItemDomainMachineDesign> itemsCreatedFromThisTemplateItem = (List<ItemDomainMachineDesign>) (List<?>) mdItem.getItemsCreatedFromThisTemplateItem();
            List<ItemDomainMachineDesign> itemsToUpdate = new ArrayList<>();

            for (ItemDomainMachineDesign item : itemsCreatedFromThisTemplateItem) {
                Item assignedItem = item.getAssignedItem();

                // Verify if in sync with template
                if (ObjectUtility.equals(originalContainedItem, assignedItem)) {
                    item.setAssignedItem(newAssignedItem);
                    itemsToUpdate.add(item);
                }
            }

            try {
                updateList(itemsToUpdate);
            } catch (CdbException ex) {
                LOGGER.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
    }

    public String completeAddNewCatalogListConfiguration() {
        // Verify non empty names 
        for (ItemDomainCatalog item : catalogItemsDraggedAsChildren) {
            String mdName = item.getMachineDesignPlaceholderName();
            if (mdName == null || mdName.equals("")) {
                SessionUtility.addErrorMessage("Error", "Please provide machine design name for all items. '" + item.getName() + "' is missing machine design name.");
                return null;
            }
        }

        // Verify uniqueness
        List<ItemDomainMachineDesign> idmList = new ArrayList<>();
        for (ItemDomainCatalog item : catalogItemsDraggedAsChildren) {
            String name = item.getMachineDesignPlaceholderName();
            for (ItemDomainCatalog innerItem : catalogItemsDraggedAsChildren) {
                if (innerItem.getId() != item.getId()) {
                    if (name.equals(innerItem.getMachineDesignPlaceholderName())) {
                        SessionUtility.addErrorMessage("Error",
                                "Ensure all machine designs are unique. '"
                                + name + "' shows up twice.");
                        return null;
                    }
                }
            }

            ItemDomainMachineDesign newMachineDesign = createEntityInstanceForDualTreeView();
            newMachineDesign.setName(item.getMachineDesignPlaceholderName());
            idmList.add(newMachineDesign);
        }

        // Verify valid machine desings 
        for (ItemDomainMachineDesign md : idmList) {
            try {
                checkItem(md);
            } catch (CdbException ex) {
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                LOGGER.error(ex);
                return null;
            }
        }

        // Add new items
        List<ItemElement> newlyAddedItemElementList = new ArrayList<>();
        for (int i = 0; i < catalogItemsDraggedAsChildren.size(); i++) {
            ItemDomainMachineDesign mdItem = idmList.get(i);
            Item catalogItem = catalogItemsDraggedAsChildren.get(i);

            ItemElement newItemElement = createItemElement(current);

            newItemElement.setContainedItem(mdItem);
            mdItem.setAssignedItem(catalogItem);

            prepareAddItemElement(current, newItemElement);

            newlyAddedItemElementList.add(newItemElement);
        }

        update();

        // Add item elements to items created from the current template 
        if (currentViewIsTemplate) {
            // Remap to the added elements 
            List<ItemElement> dbMappedElements = new ArrayList<>();
            for (ItemElement ie : current.getItemElementDisplayList()) {
                for (int i = 0; i < newlyAddedItemElementList.size(); i++) {
                    ItemElement unmappedIE = newlyAddedItemElementList.get(i);
                    if (ie.getName().equals(unmappedIE.getName())) {
                        dbMappedElements.add(ie);
                        newlyAddedItemElementList.remove(i);
                        break;
                    }
                }
            }

            List<Item> items = getItemsCreatedFromTemplateItem(getCurrent());

            for (ItemElement ie : dbMappedElements) {
                cloneNewTemplateElementForItemsDerivedFromItem(ie, items);
            }
        }

        expandToSelectedTreeNodeAndSelect();

        return currentDualViewList();
    }

    public void onItemDrop(DragDropEvent ddEvent) {
        ItemDomainCatalog catalogItem = ((ItemDomainCatalog) ddEvent.getData());

        if (catalogItemsDraggedAsChildren == null) {
            catalogItemsDraggedAsChildren = new ArrayList<>();
        }

        if (newCatalogItemsInMachineDesignModel == null) {
            newCatalogItemsInMachineDesignModel = new DefaultTreeNode();
            TreeNode parent = new DefaultTreeNode(getCurrent());
            newCatalogItemsInMachineDesignModel.getChildren().add(parent);
            parent.setExpanded(true);
            lastExpandedNode = parent;
        }
        TreeNode newCatalogNode = new DefaultTreeNode(catalogItem);
        lastExpandedNode.getChildren().add(newCatalogNode);

        catalogItemsDraggedAsChildren.add(catalogItem);
    }

    public List<ItemDomainCatalog> getCatalogItemsDraggedAsChildren() {
        return catalogItemsDraggedAsChildren;
    }

    public TreeNode getNewCatalogItemsInMachineDesignModel() {
        return newCatalogItemsInMachineDesignModel;
    }

    @Override
    public void resetSearchVariables() {
        super.resetSearchVariables();
        searchResultsTreeNode = null;
    }

    public synchronized TreeNode getSearchResults(String searchString, boolean caseInsensitive) {
        this.performEntitySearch(searchString, caseInsensitive);
        return getHierarchicalSearchResults();
    }

    public TreeNode getHierarchicalSearchResults() {
        if (searchResultsTreeNode != null) {
            return searchResultsTreeNode;
        }
        LinkedList<SearchResult> searchResultList = getSearchResultList();
        TreeNode rootTreeNode = new DefaultTreeNode();
        if (searchResultList != null) {
            for (SearchResult result : searchResultList) {
                result.setRowStyle(SearchResult.SEARCH_RESULT_ROW_STYLE);

                ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) result.getCdbEntity();

                ItemDomainMachineDesign parent = mdItem.getParentMachineDesign();

                TreeNode resultNode = new DefaultTreeNode(result);

                List<ItemDomainMachineDesign> parents = new ArrayList<>();

                while (parent != null) {
                    parents.add(parent);
                    parent = parent.getParentMachineDesign();
                }

                TreeNode currentRoot = rootTreeNode;

                // Combine common parents 
                parentSearch:
                for (int i = parents.size() - 1; i >= 0; i--) {
                    ItemDomainMachineDesign currentParent = parents.get(i);

                    for (TreeNode node : currentRoot.getChildren()) {
                        Object data = node.getData();
                        SearchResult searchResult = (SearchResult) data;
                        CdbEntity cdbEntity = searchResult.getCdbEntity();
                        ItemDomainMachineDesign itemResult = (ItemDomainMachineDesign) cdbEntity;

                        if (itemResult.equals(currentParent)) {
                            currentRoot = node;
                            continue parentSearch;
                        }
                    }

                    // Need to create parentNode
                    SearchResult parentResult = new SearchResult(currentParent, currentParent.getId(), currentParent.getName());
                    parentResult.addAttributeMatch("Reason", "Parent of Result");

                    TreeNode newRoot = new DefaultTreeNode(parentResult);
                    newRoot.setExpanded(true);
                    currentRoot.getChildren().add(newRoot);
                    currentRoot = newRoot;
                }

                currentRoot.getChildren().add(resultNode);

                List<ItemElement> childElements = mdItem.getItemElementDisplayList();

                for (ItemElement childElement : childElements) {
                    Item mdChild = childElement.getContainedItem();
                    SearchResult childResult = new SearchResult(mdChild, mdChild.getId(), mdChild.getName());
                    childResult.addAttributeMatch("Reason", "Child of result");

                    TreeNode resultChildNode = new DefaultTreeNode(childResult);
                    resultNode.getChildren().add(resultChildNode);
                }
            }
        }
        searchResultsTreeNode = rootTreeNode;
        return searchResultsTreeNode;
    }

    private void syncMachineDesignConnectors(ItemDomainMachineDesign item) {
        List<ItemConnector> itemConnectorList = item.getItemConnectorList();
        List<ItemConnector> connectorsFromAssignedCatalogItem = getConnectorsFromAssignedCatalogItem(item);

        if (connectorsFromAssignedCatalogItem == null) {
            return;
        }

        if (itemConnectorList.size() == 0) {
            // Sync all connectors into machine design
            for (ItemConnector cconnector : connectorsFromAssignedCatalogItem) {
                ItemConnector mdConnector = cloneConnectorForMachineDesign(cconnector, item);

                itemConnectorList.add(mdConnector);
            }
        } else {
            // Verify if any new connections were created on the catalog             
            if (connectorsFromAssignedCatalogItem != null) {

                catConnFor:
                for (ItemConnector catalogItemConn : connectorsFromAssignedCatalogItem) {
                    for (ItemConnector mdItemConn : itemConnectorList) {
                        Connector mdConnector = mdItemConn.getConnector();
                        Connector catConnector = catalogItemConn.getConnector();

                        if (mdConnector.equals(catConnector)) {
                            continue catConnFor;
                        }
                    }
                    ItemConnector mdConnector = cloneConnectorForMachineDesign(catalogItemConn, item);
                    itemConnectorList.add(mdConnector);
                }
            }
        }
    }

    private ItemConnector cloneConnectorForMachineDesign(ItemConnector catalogConnector, ItemDomainMachineDesign mdItem) {
        ItemConnector mdConnector = new ItemConnector();

        mdConnector.setConnector(catalogConnector.getConnector());
        mdConnector.setItem(mdItem);

        return mdConnector;
    }

    public void prepareCableMappingDialog() {
        ItemDomainMachineDesign item = getItemFromSelectedItemInTreeTable();
        // Refresh item from DB
        item = findById(item.getId());

        setCurrent(item);
        prepareCableMappingDialogForCurrent();
    }

    public void prepareCableMappingDialogForCurrent() {
        mdccmi = new MachineDesignConnectorCableMapperItem(getMdConnectorListForCurrent());
    }

    public void saveCableMappingDialog() {
        update();
    }

    public MachineDesignConnectorCableMapperItem getMachineDesignConnectorCableMapperItem() {
        return mdccmi;
    }

    public void resetMachineDesignConnectorCableMapperItem() {
        mdccmi = null;
    }

    public void prepareWizardCable() {
        updateCurrentUsingSelectedItemInTreeTable();
        currentEditItemElement = (ItemElement) selectedItemInListTreeTable.getData();

        CableWizard cableWizard = CableWizard.getInstance();
        cableWizard.registerClient(this, cableWizardRedirectSuccess);
        cableWizard.setSelectionEndpoint1(selectedItemInListTreeTable);

        displayListConfigurationView = true;
        displayAddCablePanel = true;
    }

    public void prepareWizardCircuit() {
        updateCurrentUsingSelectedItemInTreeTable();
        currentEditItemElement = (ItemElement) selectedItemInListTreeTable.getData();

        // create model for wizard
        CircuitWizard circuitWizard = CircuitWizard.getInstance();
        circuitWizard.registerClient(this, cableWizardRedirectSuccess);
        circuitWizard.setSelectionEndpoint1(selectedItemInListTreeTable);

        displayListConfigurationView = true;
        displayAddCableCircuitPanel = true;
    }

    public void prepareWizardBundle() {
        updateCurrentUsingSelectedItemInTreeTable();
        currentEditItemElement = (ItemElement) selectedItemInListTreeTable.getData();

        // create model for wizard
        BundleWizard bundleWizard = BundleWizard.getInstance();
        bundleWizard.registerClient(this, cableWizardRedirectSuccess);
        bundleWizard.setSelectionEndpoint1(selectedItemInListTreeTable);

        displayListConfigurationView = true;
        displayAddCableBundlePanel = true;
    }

    public void cleanupCableWizard() {
        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }

    private static List<ItemConnector> getConnectorsFromAssignedCatalogItem(ItemDomainMachineDesign item) {
        ItemElement ie = item.getCurrentItemElement();
        Item catalogItem = ie.getCatalogItem();
        if (catalogItem != null) {
            return catalogItem.getItemConnectorList();
        }
        return null;
    }

    public List<MachineDesignConnectorListObject> getMdConnectorListForCurrent() {
        if (mdConnectorList == null) {
            //Generate connector list
            ItemDomainMachineDesign item = getCurrent();
            mdConnectorList = getMdConnectorListForItem(item);
        }
        return mdConnectorList;
    }

    public List<MachineDesignConnectorListObject> getMdConnectorListForItem(ItemDomainMachineDesign item) {
        syncMachineDesignConnectors(item);
        return MachineDesignConnectorListObject.createMachineDesignConnectorList(item);
    }

    public boolean getDisplayMdConnectorList() {
        return getMdConnectorListForCurrent().size() > 0;
    }

    public List<ItemElementHistory> getCombinedItemElementHistory(ItemElement ie) {
        List<ItemElementHistory> itemElementHistories = new ArrayList<>();
        Item mdItem = ie.getContainedItem();
        ItemElement selfElement = mdItem.getSelfElement();

        List<ItemElementHistory> assignedItemHistory = selfElement.getItemElementHistoryList();
        List<ItemElementHistory> parentItemHistory = ie.getItemElementHistoryList();

        int currentAssignedItemInx = incrementValidIndxForHistory(-1, assignedItemHistory);
        int currentParentItemInx = incrementValidIndxForHistory(-1, parentItemHistory);

        int size = assignedItemHistory.size();
        if (parentItemHistory != null) {
            size = size + parentItemHistory.size();
        }

        Date lastParentDate = null;

        for (int i = 0; i < size; i++) {
            ItemElementHistory aih = null;
            if (currentAssignedItemInx != -1) {
                aih = assignedItemHistory.get(currentAssignedItemInx);
            }
            
            ItemElementHistory pih = null;
            if (currentParentItemInx != -1) {
                pih = parentItemHistory.get(currentParentItemInx);
            }

            // default 1 is to use the assigned item history. Parent item history is optional.
            int result = 1;
            if (currentAssignedItemInx != -1 && currentParentItemInx != -1) {
                // Both histories must be valid
                result = aih.getEnteredOnDateTime().compareTo(pih.getEnteredOnDateTime());
            } else if (currentParentItemInx != -1) {
                // parent item must be valid
                result = -1; 
            }

            ItemElementHistory ieh = new ItemElementHistory();

            Item currentAssignedItem = null; 
            Item currentParentItem = null;
            if (aih != null) {                
                currentAssignedItem = aih.getContainedItem2();
            }
            if (pih != null) {
                currentParentItem = pih.getParentItem();
            }

            if (result == 0) {
                ieh.setEnteredOnDateTime(aih.getEnteredOnDateTime());
                ieh.setEnteredByUser(aih.getEnteredByUser());

                currentAssignedItemInx = incrementValidIndxForHistory(currentAssignedItemInx, assignedItemHistory);
                currentParentItemInx = incrementValidIndxForHistory(currentParentItemInx, parentItemHistory);

                lastParentDate = pih.getEnteredOnDateTime();

                i++;
            } else if (result > 0) {
                ieh.setEnteredOnDateTime(aih.getEnteredOnDateTime());
                ieh.setEnteredByUser(aih.getEnteredByUser());

                currentAssignedItemInx = incrementValidIndxForHistory(currentAssignedItemInx, assignedItemHistory);                
            } else {
                ieh.setEnteredOnDateTime(pih.getEnteredOnDateTime());
                ieh.setEnteredByUser(pih.getEnteredByUser());
                
                lastParentDate = pih.getEnteredOnDateTime();
                
                currentParentItemInx = incrementValidIndxForHistory(currentParentItemInx, parentItemHistory);                
            }

            if (lastParentDate == null && pih != null) {
                lastParentDate = pih.getEnteredOnDateTime();
            }

            // Add parent only if it has happened. 
            if (lastParentDate != null && ieh.getEnteredOnDateTime().compareTo(lastParentDate) >= 0) {
                ieh.setParentItem(currentParentItem);
            } 
            ieh.setContainedItem2(currentAssignedItem);

            itemElementHistories.add(ieh);
        }

        return itemElementHistories;
    }

    private int incrementValidIndxForHistory(int currIndx, List<ItemElementHistory> ieh) {
        currIndx++;

        if (ieh == null || ieh.size() == currIndx) {
            return -1; 
        }
        return currIndx;
    }

    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc="Undocumented Fold">
    public boolean verifyValidTemplateName(String templateName, boolean printMessage) {
        boolean validTitle = false;
        if (templateName.contains("{")) {
            int openBraceIndex = templateName.indexOf("{");
            int closeBraceIndex = templateName.indexOf("}");
            if (openBraceIndex < closeBraceIndex) {
                validTitle = true;
            }
        }
        if (!validTitle && printMessage) {
            SessionUtility.addWarningMessage(
                    "Template names require parameters",
                    "Place parements within {} in template name. Example: 'templateName {paramName}'");

        }

        return validTitle;
    }

    public void prepareCreateInventoryFromCurrentTemplate() {
        newMdInventoryItem = null;

        try {
            newMdInventoryItem = createItemFromTemplate(current);
            createMachineDesignFromTemplateHierachically(newMdInventoryItem);
        } catch (CdbException | CloneNotSupportedException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            return;
        }

        List<Item> inventoryForCurrentTemplate = current.getDerivedFromItemList();
        int unitNum = inventoryForCurrentTemplate.size() + 1;
        newMdInventoryItem.setName("Unit: " + unitNum);

        assignInventoryAttributes(newMdInventoryItem, current);
    }

    private void assignInventoryAttributes(ItemDomainMachineDesign newInventory, ItemDomainMachineDesign templateItem) {
        newInventory.setDerivedFromItem(templateItem);
        assignInventoryAttributes(newInventory);
    }

    private void assignInventoryAttributes(ItemDomainMachineDesign newInventory) {
        String inventoryetn = EntityTypeName.inventory.getValue();
        EntityType inventoryet = entityTypeFacade.findByName(inventoryetn);
        if (newInventory.getEntityTypeList() == null) {
            try {
                newInventory.setEntityTypeList(new ArrayList());
            } catch (CdbException ex) {
                LOGGER.error(ex);
            }
        }
        newInventory.getEntityTypeList().add(inventoryet);
    }

    public void createNewMdInventoryItem() {
        ItemDomainMachineDesign currentItem;
        currentItem = getCurrent();
        setCurrent(newMdInventoryItem);
        create();

        currentItem = findById(currentItem.getId());
        setCurrent(currentItem);
    }

    public boolean isCollapseContentsOfInventoryItem() {
        return current.getDerivedFromItemList().size() == 0;
    }

    public boolean isRenderInventorySection() {
        return current.getItemElementMemberList().size() == 0
                && current.getItemElementMemberList2().size() == 0;
    }

    public boolean isInventory(ItemDomainMachineDesign item) {
        if (item == null) {
            return false;
        }
        String inventoryetn = EntityTypeName.inventory.getValue();
        return item.isItemEntityType(inventoryetn);
    }

    public ItemDomainMachineDesign getNewMdInventoryItem() {
        return newMdInventoryItem;
    }

    public void templateToCreateNewItemSelected(NodeSelectEvent nodeSelection) {
        TreeNode treeNode = nodeSelection.getTreeNode();

        ItemElement element = (ItemElement) treeNode.getData();
        Item parentItem = element.getContainedItem();

        templateToCreateNewItem = (ItemDomainMachineDesign) parentItem;
    }

    @Override
    public String prepareCreateTemplate() {
        String createRedirect = super.prepareCreate();

        ItemDomainMachineDesign current = getCurrent();
        String templateEntityTypeName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityTypeName);
        try {
            current.setEntityTypeList(new ArrayList<>());
        } catch (CdbException ex) {
            LOGGER.error(ex);
        }
        current.getEntityTypeList().add(templateEntityType);

        return createRedirect;

    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="Favorites toggle impl">
    public TreeNode getFavoriteMachineDesignTreeRootTreeNode() {

        List<ItemDomainMachineDesign> favoriteItems = getFavoriteItems();
        favoriteMachineDesignTreeRootTreeNode = new DefaultTreeNode();

        if (favoriteItems == null) {
            return favoriteMachineDesignTreeRootTreeNode;
        }

        List<Item> parentFavorites = new ArrayList<>();

        for (ItemDomainMachineDesign item : favoriteItems) {

            ItemDomainMachineDesign parentMachineDesign = item.getParentMachineDesign();
            boolean parentFound = parentMachineDesign != null;
            while (parentMachineDesign != null) {
                ItemDomainMachineDesign ittrParent = parentMachineDesign.getParentMachineDesign();
                if (ittrParent == null) {
                    item = parentMachineDesign;
                }
                parentMachineDesign = ittrParent;
            }
            if (parentFound) {
                // Ensure mutliple top levels aren't added. 
                if (parentFavorites.contains(item)) {
                    continue;
                } else {
                    parentFavorites.add(item);
                }

                // Ensure multiple top levels aren't added when a child of a favorite is also a favorite. 
                if (favoriteItems.contains(item)) {
                    continue;
                }
            }

            ItemElement element = new ItemElement();
            element.setContainedItem(item);

            TreeNode parent = new DefaultTreeNode(element);
            favoriteMachineDesignTreeRootTreeNode.getChildren().add(parent);
            parent.setParent(favoriteMachineDesignTreeRootTreeNode);
            setTreeNodeTypeMachineDesignTreeList(parent);
            expandTreeChildren(parent);
        }

        return favoriteMachineDesignTreeRootTreeNode;
    }

    public boolean isFavoritesShown() {
        return favoritesShown;
    }

    public void setFavoritesShown(boolean favoritesShown) {
        this.favoritesShown = favoritesShown;
    }
    // </editor-fold>   

    public boolean isCablesShown() {
        return cablesShown;
    }

    public void setCablesShown(boolean cablesShown) {
        if (cablesShown != this.cablesShown) {
            this.resetListDataModel();
        }
        this.cablesShown = cablesShown;
    }

    // <editor-fold defaultstate="collapsed" desc="Element creation implementation ">   
    // <editor-fold defaultstate="collapsed" desc="Functionality">
    public void newMachineDesignElementContainedItemValueChanged() {
        String name = currentEditItemElement.getContainedItem().getName();
        if (!name.equals("")) {
            currentEditItemElementSaveButtonEnabled = true;
        } else {
            currentEditItemElementSaveButtonEnabled = false;
        }

    }

    public void updateInstalledInventoryItem() {
        boolean updateNecessary = false;
        ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) currentEditItemElement.getContainedItem();
        Item assignedItem = mdItem.getAssignedItem();

        if (inventoryForElement != null) {
            if (assignedItem.equals(inventoryForElement)) {
                SessionUtility.addInfoMessage("No update", "Inventory selected is same as before");
            } else if (verifyValidUnusedInventoryItem(inventoryForElement)) {
                updateNecessary = true;
                mdItem.setAssignedItem(inventoryForElement);
            }
        } else if (assignedItem.getDomain().getId() == ItemDomainName.INVENTORY_ID) {
            // Item is unselected, select catalog item
            updateNecessary = true;
            mdItem.setAssignedItem(assignedItem.getDerivedFromItem());
        } else {
            SessionUtility.addInfoMessage("No update", "Inventory item not selected");
        }

        if (updateNecessary) {
            setCurrent(mdItem);
            update();
        }

        resetItemElementEditVariables();
    }

    private boolean verifyValidUnusedInventoryItem(Item inventoryItem) {
        for (ItemElement itemElement : inventoryItem.getItemElementMemberList2()) {
            Item item = itemElement.getParentItem();
            if (item instanceof ItemDomainMachineDesign) {
                SessionUtility.addWarningMessage("Inventory item used",
                        "Inventory item cannot be saved, used in: " + item.toString());
                return false;
            }
        }

        return true;

    }

    public void prepareUpdateInstalledInventoryItem() {
        resetItemElementEditVariables();
        catalogForElement = currentEditItemElement.getCatalogItem();
    }

    public DataModel getInstalledInventorySelectionForCurrentElement() {
        if (installedInventorySelectionForCurrentElement == null) {
            if (catalogForElement != null) {
                List<Item> derivedFromItemList = catalogForElement.getDerivedFromItemList();
                installedInventorySelectionForCurrentElement = new ListDataModel(derivedFromItemList);
            }

        }
        return installedInventorySelectionForCurrentElement;
    }

    public DataModel getMachineDesignTemplatesSelectionList() {
        if (machineDesignTemplatesSelectionList == null) {
            List<ItemDomainMachineDesign> machineDesignTemplates = itemDomainMachineDesignFacade.getMachineDesignTemplates();
            machineDesignTemplatesSelectionList = new ListDataModel(machineDesignTemplates);
        }
        return machineDesignTemplatesSelectionList;
    }

    public DataModel getTopLevelMachineDesignSelectionList() {
        if (topLevelMachineDesignSelectionList == null) {
            List<ItemDomainMachineDesign> itemsWithoutParents = getItemsWithoutParents();
            List<ItemElement> itemElementMemberList = current.getItemElementMemberList();

            if (itemElementMemberList != null) {
                if (itemElementMemberList.size() == 0) {
                    // current item has no parents
                    itemsWithoutParents.remove(current);
                } else {
                    // Be definition machine design item should only have one parent
                    Item parentItem = null;

                    while (itemElementMemberList.size() != 0) {
                        ItemElement parentElement = itemElementMemberList.get(0);
                        parentItem = parentElement.getParentItem();

                        itemElementMemberList = parentItem.getItemElementMemberList();
                    }

                    itemsWithoutParents.remove(parentItem);
                }
            }

            removeTemplatesFromList(itemsWithoutParents, !isCurrentViewIsTemplate());

            topLevelMachineDesignSelectionList = new ListDataModel(itemsWithoutParents);
        }
        return topLevelMachineDesignSelectionList;
    }

    private void removeTemplatesFromList(List<ItemDomainMachineDesign> itemList, boolean removeTemplate) {
        String templateEntityName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityName);

        int index = 0;
        while (index < itemList.size()) {
            Item item = itemList.get(index);
            if (item.getEntityTypeList().contains(templateEntityType)) {
                if (removeTemplate) {
                    itemList.remove(index);
                } else {
                    index++;
                }
            } else {
                if (removeTemplate) {
                    index++;
                } else {
                    itemList.remove(index);
                }
            }
        }
    }

    private void removeMachineDesignFromList(List<ItemDomainMachineDesign> itemList) {
        String templateEntityName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityName);

        int index = 0;
        while (index < itemList.size()) {
            Item item = itemList.get(index);
            // Does not contain template entity type
            if (!item.getEntityTypeList().contains(templateEntityType)) {
                itemList.remove(index);
            } else {
                index++;
            }
        }
    }

    public void resetItemElementEditVariables() {
        currentEditItemElementSaveButtonEnabled = false;
        displayCreateMachineDesignFromTemplateContent = false;

        installedInventorySelectionForCurrentElement = null;
        inventoryForElement = null;
        catalogForElement = null;
        inventoryForElement = null;
        templateToCreateNewItem = null;
        machineDesignTemplatesSelectionList = null;
        topLevelMachineDesignSelectionList = null;
        machineDesignNameList = null;
        machineDesignName = null;
    }

    @Override
    public void prepareCreateSingleItemElementSimpleDialog() {
        super.prepareCreateSingleItemElementSimpleDialog();
        resetItemElementEditVariables();

        int elementSize = current.getItemElementDisplayList().size();
        float sortOrder = elementSize;
        currentEditItemElement.setSortOrder(sortOrder);
    }

    public void verifyMoveExistingMachineDesignSelected() {
        if (currentEditItemElement.getContainedItem() != null) {
            currentEditItemElementSaveButtonEnabled = true;
        }
    }

    public void reassignTemplateVarsForSelectedMdCreatedFromTemplate() {
        ItemDomainMachineDesign itemFromSelectedItemInTreeTable = getItemFromSelectedItemInTreeTable();

        List<ItemDomainMachineDesign> itemsToUpdate = new ArrayList<>();
        reassignTemplateVarsForSelectedMdCreatedFromTemplateRecursivelly(itemFromSelectedItemInTreeTable, itemsToUpdate);

        try {
            updateList(itemsToUpdate);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }

        expandToSpecificMachineDesignItem(itemFromSelectedItemInTreeTable);
    }

    private void reassignTemplateVarsForSelectedMdCreatedFromTemplateRecursivelly(ItemDomainMachineDesign item, List<ItemDomainMachineDesign> itemsToUpdate) {
        ItemDomainMachineDesign createdFromTemplate = (ItemDomainMachineDesign) item.getCreatedFromTemplate();

        if (createdFromTemplate != null) {
            setMachineDesginIdentifiersFromTemplateItem(createdFromTemplate, item);
            itemsToUpdate.add(item);
        }

        List<ItemElement> itemElementDisplayList = item.getItemElementDisplayList();

        for (ItemElement itemElement : itemElementDisplayList) {
            ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) itemElement.getContainedItem();
            reassignTemplateVarsForSelectedMdCreatedFromTemplateRecursivelly(containedItem, itemsToUpdate);
        }
    }

    public void generateTemplateVarsForSelectedMdCreatedFromTemplate() {
        ItemDomainMachineDesign selectedItem = getItemFromSelectedItemInTreeTable();

        machineDesignNameList = new ArrayList<>();

        generateTemplateVarsForSelectedMdCreatedFromTemplateRecursivelly(selectedItem);

    }

    private void generateTemplateVarsForSelectedMdCreatedFromTemplateRecursivelly(ItemDomainMachineDesign itemDomainMachineDesign) {
        ItemDomainMachineDesign createdFromTemplate = (ItemDomainMachineDesign) itemDomainMachineDesign.getCreatedFromTemplate();

        if (createdFromTemplate != null) {
            generateMachineDesignTemplateNameVars(createdFromTemplate);
        }

        List<ItemElement> itemElementDisplayList = itemDomainMachineDesign.getItemElementDisplayList();

        for (ItemElement itemElement : itemElementDisplayList) {
            ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) itemElement.getContainedItem();
            generateTemplateVarsForSelectedMdCreatedFromTemplateRecursivelly(containedItem);
        }
    }

    public void generateTemplateForElementMachineDesignNameVars() {
        if (templateToCreateNewItem != null) {

            machineDesignNameList = new ArrayList<>();

            generateMachineDesignTemplateNameVarsRecursivelly(templateToCreateNewItem);

            generateMachineDesignName();
        }
    }

    public void generateMachineDesignTemplateNameVarsRecursivelly(ItemDomainMachineDesign template) {
        generateMachineDesignTemplateNameVars(template);

        for (ItemElement ie : template.getItemElementDisplayList()) {
            ItemDomainMachineDesign machineDesignTemplate = (ItemDomainMachineDesign) ie.getContainedItem();
            if (machineDesignTemplate != null) {
                generateMachineDesignTemplateNameVarsRecursivelly(machineDesignTemplate);
            }
        }
    }

    private void generateMachineDesignTemplateNameVars(ItemDomainMachineDesign template) {
        String name = template.getName();
        String alternateName = template.getItemIdentifier1();
        appendMachineDesignNameList(name);
        appendMachineDesignNameList(alternateName);
    }

    private void appendMachineDesignNameList(String templateIdentifier) {
        if (templateIdentifier == null) {
            return;
        }
        int firstVar = templateIdentifier.indexOf('{');
        int secondVar;

        while (firstVar != -1) {
            templateIdentifier = templateIdentifier.substring(firstVar);
            secondVar = templateIdentifier.indexOf('}');

            String key = templateIdentifier.substring(1, secondVar);

            KeyValueObject keyValue = new KeyValueObject(key);

            if (machineDesignNameList.contains(keyValue) == false) {
                machineDesignNameList.add(keyValue);
            }

            templateIdentifier = templateIdentifier.substring(secondVar + 1);

            firstVar = templateIdentifier.indexOf('{');
        }
    }

    public void generateMachineDesignName() {
        machineDesignName = generateMachineDesignNameForTemplateItem(templateToCreateNewItem.getName());
    }

    private void setMachineDesginIdentifiersFromTemplateItem(ItemDomainMachineDesign templateItem, ItemDomainMachineDesign mdItem) {
        String machineDesignName = generateMachineDesignNameForTemplateItem(templateItem.getName());
        mdItem.setName(machineDesignName);
        String alternateName = generateMachineDesignNameForTemplateItem(templateItem.getItemIdentifier1());
        mdItem.setItemIdentifier1(alternateName);
    }

    public String generateMachineDesignNameForTemplateItem(String templateIdentifier) {
        if (templateIdentifier == null) {
            return templateIdentifier;
        }
        if (machineDesignNameList != null) {
            for (KeyValueObject kv : machineDesignNameList) {
                if (kv.getValue() != null && !kv.getValue().equals("")) {
                    String originalText = "{" + kv.getKey() + "}";
                    templateIdentifier = templateIdentifier.replace(originalText, kv.getValue());
                }
            }
        }

        return templateIdentifier;
    }

    @Override
    public void beforeValidateItemElement() throws CloneNotSupportedException, CdbException {
        super.beforeValidateItemElement();

        if (displayAddMDFromTemplateConfigurationPanel) {
            if (currentViewIsTemplate == false) {
                createMachineDesignFromTemplateForEditItemElement();
            } else {
                // Template link in multiple places        
                currentEditItemElement.setContainedItem(templateToCreateNewItem);

                // Add from top level only 
                if (templateToCreateNewItem.getParentMachineDesign() == null) {
                    throw new CdbException("Top level machine design templates will be moved into selected machine design. Use add top machine design.");
                }
            }
        }

        ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) currentEditItemElement.getContainedItem();

        if (containedItem.getDerivedFromItemList() != null && containedItem.getDerivedFromItemList().size() > 0) {
            throw new CdbException("Machine design: '" + containedItem.getName() + "' must stay top level. It has inventory items.");
        }

        List<ItemElement> itemElementMemberList = containedItem.getItemElementMemberList();
        if (itemElementMemberList == null) {
            containedItem.setItemElementMemberList(new ArrayList<>());
            itemElementMemberList = containedItem.getItemElementMemberList();
        }

        if (itemElementMemberList.contains(currentEditItemElement) == false) {
            containedItem.getItemElementMemberList().add(currentEditItemElement);
        }

        checkItem(containedItem);

    }

    private void createMachineDesignFromTemplateForEditItemElement() throws CdbException, CloneNotSupportedException {
        createMachineDesignFromTemplate(currentEditItemElement, templateToCreateNewItem);

        createMachineDesignFromTemplateHierachically(currentEditItemElement);
    }

    public void createMachineDesignFromTemplateHierachically(ItemElement itemElement) throws CdbException, CloneNotSupportedException {
        Item containedItem = itemElement.getContainedItem();
        ItemDomainMachineDesign subTemplate = (ItemDomainMachineDesign) containedItem;
        createMachineDesignFromTemplateHierachically(subTemplate);
    }

    private void createMachineDesignFromTemplateHierachically(ItemDomainMachineDesign subTemplate) throws CdbException, CloneNotSupportedException {
        List<ItemElement> itemElementDisplayList = subTemplate.getItemElementDisplayList();
        for (ItemElement ie : itemElementDisplayList) {
            Item containedItem2 = ie.getContainedItem();
            ItemDomainMachineDesign templateItem = (ItemDomainMachineDesign) containedItem2;

            createMachineDesignFromTemplate(ie, templateItem);
            createMachineDesignFromTemplateHierachically(ie);
        }

    }

    public ItemDomainMachineDesign createMachineDesignFromTemplate(ItemElement itemElement, ItemDomainMachineDesign templateItem) throws CdbException, CloneNotSupportedException {
        cloneProperties = true;
        cloneCreateItemElementPlaceholders = false;

        // TODO: once update template selection to tree table use the selected item element. 
        ItemElement currentItemElement = templateItem.getCurrentItemElement();
        if (currentItemElement.getId() != null) {
            itemElement.setDerivedFromItemElement(currentItemElement);
        }

        ItemDomainMachineDesign createItemFromTemplate = createItemFromTemplate(templateItem);

        Item assignedItem = templateItem.getAssignedItem();
        createItemFromTemplate.setAssignedItem(assignedItem);

        if (newMdInventoryItem != null || currentViewIsSubAssembly) {
            // New inventory creation mode 
            assignInventoryAttributes(createItemFromTemplate, templateItem);
        }

        itemElement.setContainedItem(createItemFromTemplate);

        // No longer needed. Skip the standard template relationship process. 
        templateToCreateNewItem = null;

        return createItemFromTemplate;
    }

    private ItemDomainMachineDesign createItemFromTemplate(ItemDomainMachineDesign templateItem) throws CdbException, CloneNotSupportedException {
        ItemDomainMachineDesign clone = (ItemDomainMachineDesign) templateItem.clone();
        cloneCreateItemElements(clone, templateItem, true, true);
        setMachineDesginIdentifiersFromTemplateItem(templateItem, clone);

        // ensure uniqueness of template creation.
        String viewUUID = clone.getViewUUID();
        clone.setItemIdentifier2(viewUUID);

        addCreatedFromTemplateRelationshipToItem(clone, templateItem);

        clone.setEntityTypeList(new ArrayList<>());

        return clone;
    }

    @Override
    public void failedValidateItemElement() {
        super.failedValidateItemElement();
        currentEditItemElement.setContainedItem(originalForElement);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Accessors">   
    public String getMachineDesignName() {
        return machineDesignName;
    }

    public List<KeyValueObject> getMachineDesignNameList() {
        return machineDesignNameList;
    }

    public void setMachineDesignNameList(List<KeyValueObject> list) {
        this.machineDesignNameList = list;
    }

    public Item getInventoryForElement() {
        return inventoryForElement;
    }

    public void setInventoryForElement(Item inventoryForElement) {
        this.inventoryForElement = inventoryForElement;
    }

    public Item getCatalogForElement() {
        return catalogForElement;
    }

    public void setCatalogForElement(Item catalogForElement) {
        this.catalogForElement = catalogForElement;
    }

    public boolean isDisplayCreateMachineDesignFromTemplateContent() {
        return displayCreateMachineDesignFromTemplateContent;
    }

    // </editor-fold>    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Base class overrides">                   
    @Override
    public String getItemListPageTitle() {
        return "Machine Design - Housing";
    }

    public String getSubassemblyPageTitle() {
        String title = "Machine Subassembly: ";
        if (getCurrent() != null) {
            ItemDomainMachineDesign current = getCurrent();

            while (current.getParentMachineDesign() != null) {
                current = current.getParentMachineDesign();
            }

            title += current;
        }

        return title;
    }

    @Override
    public boolean getEntityHasSortableElements() {
        return true;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    private void resetListViewVariables() {
        currentViewIsTemplate = false;
        currentViewIsSubAssembly = false;
    }

    @Override
    public void processPreRenderList() {
        super.processPreRenderList();
        resetListViewVariables();

        resetListConfigurationVariables();

        String paramValue = SessionUtility.getRequestParameterValue("id");
        if (paramValue != null) {
            Integer idParam = Integer.parseInt(paramValue);
            ItemDomainMachineDesign item = itemDomainMachineDesignFacade.findById(idParam);
            if (item != null) {
                currentViewIsTemplate = isItemMachineDesignAndTemplate(item);
                expandToSpecificMachineDesignItem(item);
            } else {
                SessionUtility.addErrorMessage("Error", "Machine design with id " + idParam + " couldn't be found.");
            }
        }
    }

    @Override
    public void processPreRenderTemplateList() {
        super.processPreRenderTemplateList();

        currentViewIsTemplate = true;
    }

    public void processSubassemblyViewRequestParams() {
        resetListViewVariables();
        resetListConfigurationVariables();

        currentViewIsSubAssembly = true;

        String itemId = SessionUtility.getRequestParameterValue("id");
        ItemDomainMachineDesign entity = getCurrent();
        if (itemId != null) {
            entity = findById(Integer.valueOf(itemId));
            if (isInventory(entity)) {
                subAssemblyRootTreeNode = null;
            }
        }

        if (isInventory(entity)) {
            setCurrent(entity);
        } else {
            prepareEntityView(entity);
        }

        processPreRender();
    }

    @Override
    protected void prepareEntityView(ItemDomainMachineDesign entity) {
        super.prepareEntityView(entity);

        // Cannot only show favorites when specific node is selected by id.
        favoritesShown = false;

        processPreRenderList();

        String redirect = "/list";

        if (isInventory(entity)) {
            redirect = "/subAssembly";
            currentViewIsSubAssembly = true;
        }

        String viewMode = SessionUtility.getRequestParameterValue("mode");
        if (viewMode != null) {
            if (viewMode.equals("detail")) {
                displayListConfigurationView = true;
                displayListViewItemDetailsView = true;
                return;
            }
        }

        if (currentViewIsTemplate) {
            redirect = "/templateList";
        }

        SessionUtility.navigateTo("/views/" + getEntityViewsDirectory() + redirect + ".xhtml?id=" + entity.getId() + "&faces-redirect=true");
    }

    @Override
    protected void completeEntityUpdate(ItemDomainMachineDesign entity) {
        super.completeEntityUpdate(entity);

        if (displayListViewItemDetailsView) {
            expandToSpecificMachineDesignItem(entity);
        }
    }

    @Override
    protected void checkItem(ItemDomainMachineDesign item) throws CdbException {
        super.checkItem(item);

        if (item.getIsItemTemplate()) {
            List<ItemElement> itemElementMemberList = item.getItemElementMemberList();
            if (itemElementMemberList == null || itemElementMemberList.isEmpty()) {
                // Item is not a child of another item. 
                if (!verifyValidTemplateName(item.getName(), false)) {
                    throw new CdbException("Place parements within {} in template name. Example: 'templateName {paramName}'");
                }
            }
        }
    }

    @Override
    protected boolean verifyItemNameCombinationUniqueness(Item item) {
        boolean unique = super.verifyItemNameCombinationUniqueness(item);

        // Ensure all machine designs are unique
        if (!unique) {
            String viewUUID = item.getViewUUID();
            item.setItemIdentifier2(viewUUID);
            unique = true;
        }

        return unique;
    }

    @Override
    protected void resetVariablesForCurrent() {
        super.resetVariablesForCurrent();

        relatedMAARCRelationshipsForCurrent = null;
        mdccmi = null;
        mdConnectorList = null;
        newMdInventoryItem = null;

        resetItemElementEditVariables();
    }

    public String getPrimaryImageThumbnailForMachineDesignItem(ItemElement itemElement) {
        String value = getPrimaryImageValueForMachineDesignItem(itemElement);
        if (!value.isEmpty()) {
            return PropertyValueController.getThumbnailImagePathByValue(value);
        }
        return value;
    }

    public Boolean isMachineDesignItemHasPrimaryImage(ItemElement itemElement) {
        return !getPrimaryImageValueForMachineDesignItem(itemElement).isEmpty();
    }

    public String getPrimaryImageValueForMachineDesignItem(ItemElement itemElement) {
        String value = "";
        if (itemElement != null) {
            Item containedItem = itemElement.getContainedItem();

            if (containedItem != null) {
                if (isItemMachineDesign(containedItem)) {
                    value = super.getPrimaryImageValueForItem(containedItem);
                } else {
                    ItemController itemItemController = getItemItemController(containedItem);
                    value = itemItemController.getPrimaryImageValueForItem(containedItem);
                }
            }

            if (value.isEmpty()) {
                if (containedItem instanceof ItemDomainMachineDesign) {
                    Item assignedItem = ((ItemDomainMachineDesign) containedItem).getAssignedItem();
                    if (assignedItem != null) {
                        ItemController itemItemController = getItemItemController(assignedItem);
                        value = itemItemController.getPrimaryImageValueForItem(assignedItem);
                    }
                }
            }
        }
        return value;
    }

    public boolean isSelectedItemInTreeTableCreatedFromTemplate() {
        ItemDomainMachineDesign item = getItemFromSelectedItemInTreeTable();
        if (item != null) {
            return item.getCreatedFromTemplate() != null;
        }
        return false;
    }

    public boolean isCurrentViewIsStandard() {
        return (currentViewIsSubAssembly == false && currentViewIsTemplate == false);
    }

    public boolean isCurrentViewIsSubAssembly() {
        return currentViewIsSubAssembly;
    }

    public boolean isCurrentViewIsTemplate() {
        return currentViewIsTemplate;
    }

    public String currentDualViewList() {
        if (currentViewIsTemplate) {
            return templateList();
        } else if (currentViewIsSubAssembly) {
            return subAssembly();
        }

        return list();
    }

    public String subAssembly() {
        return "subAssembly.xhtml?faces-redirect=true";
    }

    public String getDetailsPageHeader() {
        String header = getDisplayEntityTypeName();
        if (isCurrentItemTemplate()) {
            header += " Template";
        }
        header += " Details";

        return header;
    }

    @Override
    protected ItemDomainMachineDesign instenciateNewItemDomainEntity() {
        return new ItemDomainMachineDesign();
    }

    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this);
    }

    @Override
    protected ItemDomainMachineDesignFacade getEntityDbFacade() {
        return itemDomainMachineDesignFacade;
    }

    @Override
    public String getEntityTypeName() {
        return "itemMachineDesign";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Machine Design Item";
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.machineDesign.getValue();
    }

    public boolean getRenderItemElementList() {
        if (getEntityDisplayItemElements()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return isCurrentViewIsTemplate() == false;
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }

    @Override
    public boolean getEntityDisplayTemplates() {
        return true;
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        return "machineDesign";
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    protected ImportHelperBase createImportHelperInstance() throws CdbException {
        return new ImportHelperMachineDesign();
    }

    // </editor-fold>       
}
