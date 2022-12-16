/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbRole;
import gov.anl.aps.cdb.portal.controllers.extensions.CableWizard;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.constants.PortalStyles;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignBaseControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperMachineAssignTemplate;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperMachineHierarchy;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperMachineItemUpdate;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperMachineTemplateInstantiation;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignBaseTreeNode;
import gov.anl.aps.cdb.portal.model.ItemGenericLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementHistory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.comparator.ItemElementSortOrderComparator;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorListObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import gov.anl.aps.cdb.portal.controllers.extensions.CableWizardClient;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidWarningInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.WarningInfo;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import static gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode.CONNECTOR_NODE_TYPE;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author djarosz
 * @param <ItemDomainMachineTreeNode>
 */
public abstract class ItemDomainMachineDesignBaseController<MachineTreeNode extends ItemDomainMachineDesignBaseTreeNode, controllerUtility extends ItemDomainMachineDesignBaseControllerUtility>
        extends ItemController<controllerUtility, ItemDomainMachineDesign, ItemDomainMachineDesignFacade, ItemDomainMachineDesignSettings, ItemGenericLazyDataModel>
        implements CableWizardClient {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignBaseController.class.getName());

    private final static String URL_PARAM_DETAIL_MODE = "detail";
    private final static String URL_PARAM_DETAIL_MODE_SWITCHVAL = "switch";

    private final static String cableWizardRedirectSuccess
            = "/views/itemDomainMachineDesign/list?faces-redirect=true";

    private final static String pluginItemMachineDesignSectionsName = "itemMachineDesignDetailsViewSections";

    private TreeNode searchResultsTreeNode;

    // <editor-fold defaultstate="collapsed" desc="Favorites toggle variables">
    private boolean favoritesShown = false;
    private MachineTreeNode favoriteMachineDesignTreeRootTreeNode;

    // </editor-fold>       
    // <editor-fold defaultstate="collapsed" desc="Dual list view configuration variables ">
    protected MachineTreeNode selectedItemInListTreeTable = null;
    protected MachineTreeNode lastExpandedNode = null;

    private MachineTreeNode currentMachineDesignListRootTreeNode = null;
    private MachineTreeNode machineDesignTreeRootTreeNode = null;
    private MachineTreeNode machineDesignTemplateRootTreeNode = null;
    private boolean currentViewIsTemplate = false;

    private boolean displayListConfigurationView = false;
    private boolean displayListViewItemDetailsView = false;
    private boolean displayAddMDPlaceholderListConfigurationPanel = true;
    private boolean displayAddMDFromTemplateConfigurationPanel = true;
    private boolean displayAddMDMoveExistingConfigurationPanel = true;
    private boolean displayAddCatalogItemListConfigurationPanel = true;
    private boolean displayAssignCatalogItemListConfigurationPanel = true;
    private boolean displayAssignInventoryItemListConfigurationPanel = true;
    private boolean displayUpdateInstalledInventoryStateDialogContents = true;
    private boolean displayAttachTemplateToMachine = true;
    private boolean displayMachineDesignReorderOverlayPanel = true;

    private List<ItemDomainCatalog> catalogItemsDraggedAsChildren = null;
    private TreeNode newCatalogItemsInMachineDesignModel = null;
    
    private List<ItemElement> elementsAvaiableForNodeRepresentation; 
    private ItemElement selectedElementForNodeRepresentation; 
    private Boolean matchElementNamesForTemplateInstances; 

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Machine Design drag and drop variables">
    private static final String JS_SOURCE_MD_ID_PASSED_KEY = "sourceId";
    private static final String JS_DESTINATION_MD_ID_PASSED_KEY = "destinationId";
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="Delete support variables">
    private Boolean moveToTrashAllowed;
    private Boolean moveToTrashHasWarnings;
    private TreeNode moveToTrashNode = new DefaultTreeNode();
    private String moveToTrashDisplayName = null;
    private String moveToTrashMessage = null;
    private List<ItemDomainMachineDesign> moveToTrashItemsToUpdate = null;
    private List<ItemElement> moveToTrashElementsToDelete = null;
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

        UserInfo sessionUser = SessionUtility.getUser();
        try {
            getControllerUtility().performMachineMoveWithUpdate(parent, child, sessionUser);
            resetListDataModel();
            resetSelectDataModel();
        } catch (CdbException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            return;
        }

        child = findById(sourceId);
        expandToSpecificMachineDesignItem(child);
    }

    // </editor-fold>   
    // <editor-fold defaultstate="collapsed" desc="Undocumented Fold">
    private String mdSearchString;
    private List<MachineTreeNode> searchResultsList;
    private boolean searchCollapsed;

    protected ItemElement currentHierarchyItemElement;

    @EJB
    ItemDomainMachineDesignFacade itemDomainMachineDesignFacade;

    public boolean isItemInventory(Item item) {
        return item instanceof ItemDomainInventory;
    }

    public boolean isItemCatalog(Item item) {
        return item instanceof ItemDomainCatalog;
    }

    public boolean isItemMachineDesign(Item item) {
        return isItemMachineDesignStatic(item);
    }

    public static boolean isItemMachineDesignStatic(Item item) {
        return item instanceof ItemDomainMachineDesign;
    }

    protected boolean isMachineDesignAndEntityType(Item item, EntityTypeName entityTypeName) {
        if (item instanceof ItemDomainMachineDesign) {
            return ((ItemDomainMachineDesign) item).isItemEntityType(entityTypeName.getValue());
        }

        return false;
    }

    public boolean isItemMachineDesignAndPower(Item item) {
        return isMachineDesignAndEntityType(item, EntityTypeName.power);
    }

    public boolean isItemMachineDesignAndControl(Item item) {
        return isMachineDesignAndEntityType(item, EntityTypeName.control);
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
        } else if (isItemMachineDesignAndControl(item)) {
            return PortalStyles.machineDesignControlIcon.getValue();
        } else if (isItemMachineDesignAndPower(item)) {
            return PortalStyles.machineDesignPowerIcon.getValue();
        } else {
            return item.getDomain().getDomainRepIcon();
        }
    }

    public boolean isCollapsedRelatedMAARCItemsForCurrent() {
        return getRelatedMAARCRelationshipsForCurrent().size() < 1;
    }

    public List<ItemElementRelationship> getRelatedMAARCRelationshipsForCurrent() {
        ItemDomainMachineDesign current = getCurrent();
        List<ItemElementRelationship> relatedMAARCRelationshipsForCurrent = current.getRelatedMAARCRelationshipsForCurrent();
        if (relatedMAARCRelationshipsForCurrent == null) {
            relatedMAARCRelationshipsForCurrent = ItemDomainMAARCController.getRelatedMAARCRelationshipsForItem(getCurrent());
            current.setRelatedMAARCRelationshipsForCurrent(relatedMAARCRelationshipsForCurrent);
        }

        return relatedMAARCRelationshipsForCurrent;
    }

    @Override
    public ItemGenericLazyDataModel createItemLazyDataModel() {
        return new ItemGenericLazyDataModel(getEntityDbFacade(), getDefaultDomain(), settingObject);
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
        currentMachineDesignListRootTreeNode = null;
        machineDesignTemplateRootTreeNode = null;
        machineDesignTreeRootTreeNode = null;
        favoriteMachineDesignTreeRootTreeNode = null;
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="Dual list view configuration implementation ">   
    public MachineTreeNode getCurrentMachineDesignListRootTreeNode() {
        if (currentMachineDesignListRootTreeNode == null) {
            if (currentViewIsTemplate) {
                currentMachineDesignListRootTreeNode = getMachineDesignTemplateRootTreeNode();
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

    public MachineTreeNode getMachineDesignTreeRootTreeNode() {
        if (machineDesignTreeRootTreeNode == null) {
            machineDesignTreeRootTreeNode = loadMachineDesignRootTreeNode();
        }
        return machineDesignTreeRootTreeNode;
    }

    public MachineTreeNode getMachineDesignTemplateRootTreeNode() {
        if (machineDesignTemplateRootTreeNode == null) {
            List<ItemDomainMachineDesign> itemsWithoutParents;
            itemsWithoutParents = getEntityDbFacade().findByDomainNameWithNoParentsAndWithEntityType(getDefaultDomainName(), EntityTypeName.template.getValue());
            for (int i = itemsWithoutParents.size() - 1; i >= 0; i--) {
                ItemDomainMachineDesign item = itemsWithoutParents.get(i);
                if (item.getIsItemDeleted()) {
                    itemsWithoutParents.remove(i);
                }
            }
            machineDesignTemplateRootTreeNode = loadMachineDesignRootTreeNode(itemsWithoutParents);
        }
        return machineDesignTemplateRootTreeNode;
    }

    public void loadMachineDesignFilters() {
        MachineTreeNode node = getCurrentMachineDesignListRootTreeNode();
        node.getConfig().setLoadAllChildren(true);
    }

    public boolean isFiltersLoaded() {
        MachineTreeNode node = getCurrentMachineDesignListRootTreeNode();
        return node.getConfig().isLoadAllChildren();
    }

    /**
     * Override this function as default tree parents for the list of derived
     * machine entity.
     *
     * @return parent list of machine items.
     */
    public List<ItemDomainMachineDesign> getDefaultTopLevelMachineList() {
        return getEntityDbFacade().findByDomainNameWithNoParentsAndEntityType(getDefaultDomainName());
    }

    public MachineTreeNode loadMachineDesignRootTreeNode() {
        List<ItemDomainMachineDesign> defaultTopLevelMachineList = getDefaultTopLevelMachineList();
        return loadMachineDesignRootTreeNode(defaultTopLevelMachineList);
    }

    public abstract MachineTreeNode loadMachineDesignRootTreeNode(List<ItemDomainMachineDesign> itemsWithoutParents);

    public abstract MachineTreeNode createMachineTreeNodeInstance();

    public void searchMachineDesign() {
        Pattern searchPattern = Pattern.compile(Pattern.quote(mdSearchString), Pattern.CASE_INSENSITIVE);

        MachineTreeNode mdRoot = getCurrentMachineDesignListRootTreeNode();

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

    public void searchMachineDesign(MachineTreeNode parentNode,
            Pattern searchPattern, List<MachineTreeNode> results) {
        Object data = parentNode.getData();
        parentNode.setExpanded(false);
        if (data != null) {
            ItemElement ie = (ItemElement) data;
            Item parentItem = ie.getContainedItem();
            if (parentItem != null) {
                SearchResult search = parentItem.createSearchResultInfo(searchPattern);
                if (search.getObjectAttributeMatchMap().size() > 0) {
                    results.add(parentNode);
                    ie.setRowStyle(SearchResult.SEARCH_RESULT_ROW_STYLE);
                } else {
                    ie.setRowStyle(null);
                }
            }
        }
        List<MachineTreeNode> machineChildren = parentNode.getTreeNodeItemChildren();
        for (MachineTreeNode node : machineChildren) {
            MachineTreeNode machineNode = (MachineTreeNode) node;
            searchMachineDesign(machineNode, searchPattern, results);
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

            MachineTreeNode result = searchResultsList.get(indx);
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
        MachineTreeNode selectedItemInListTreeTable = getSelectedItemInListTreeTable();
        if (selectedItemInListTreeTable != null) {
            boolean expanded = !selectedItemInListTreeTable.isExpanded();
            try {
                selectedItemInListTreeTable.expandAllChildren(expanded);
            } catch (CdbException ex) {
                selectedItemInListTreeTable.setExpanded(true);
                SessionUtility.addWarningMessage("Warning", ex.getErrorMessage());
            }
        } else {
            SessionUtility.addInfoMessage("No tree node is selected", "Select a tree node and try again.");
        }
    }

    public MachineTreeNode getSelectedItemInListTreeTable() {
        return selectedItemInListTreeTable;
    }

    public void setSelectedItemInListTreeTable(MachineTreeNode selectedItemInListTreeTable) {
        this.selectedItemInListTreeTable = selectedItemInListTreeTable;
    }

    private void selectItemInTreeTable(MachineTreeNode newSelection) {
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

    public String switchToFullViewForCurrent() {
        ItemDomainMachineDesign current = getCurrent();

        String mode = URL_PARAM_DETAIL_MODE + "=" + URL_PARAM_DETAIL_MODE_SWITCHVAL;

        return "view?id=" + current.getId() + "&" + mode + "&faces-redirect=true";
    }

    /**
     * Return entity view page with query parameters of id.
     *
     * @return URL to view page in the entity folder with id query paramter.
     */
    public String listViewForCurrentEntity() {
        ItemDomainMachineDesign current = getCurrent();
        return "listView?id=" + current.getId() + "&mode=detail&faces-redirect=true";
    }
    
    /**
     * Return entity view page with query parameters of id.
     *
     * @return URL to view page in the entity folder with id query paramter.
     */
    public String listForCurrentEntity() {
        ItemDomainMachineDesign current = getCurrent();
        return "list?id=" + current.getId() + "&faces-redirect=true";
    }

    public String updateDetailsForCurrentSelectedTreeNode() {
        if (selectedItemInListTreeTable != null) {
            Object data = selectedItemInListTreeTable.getData();
            if (data instanceof ItemElement) {
                ItemConnector mdConnector = ((ItemElement) data).getMdConnector();
                if (mdConnector != null) {
                    return failUpdateDetailsForCurrentSelectedTreeNode("Connector Selected", "Connector is not a machine design item, only machines can be loaded.");
                } else {
                    Item containedItem = ((ItemElement) data).getContainedItem();
                    if (containedItem != null) {
                        if (!(containedItem instanceof ItemDomainMachineDesign)) {
                            String type = containedItem.getDomain().getName();
                            return failUpdateDetailsForCurrentSelectedTreeNode("Error", "Only machines can be loaded. " + type + " was selected.");
                        }
                    } else {
                        // No contained item and no connector 
                        return failUpdateDetailsForCurrentSelectedTreeNode("Error", "Unknown selection made.");
                    }
                }
            } else {
                return failUpdateDetailsForCurrentSelectedTreeNode("Error", "Unknown selection made.");
            }
        }

        return showDetailsForCurrentSelectedTreeNode();
    }

    private String failUpdateDetailsForCurrentSelectedTreeNode(String summary, String message) {
        SessionUtility.addErrorMessage(summary, message);
        SessionUtility.executeRemoteCommand("PF('loadingDialog').hide()");
        return null;
    }

    public String showDetailsForCurrentSelectedTreeNode() {
        updateCurrentUsingSelectedItemInTreeTable();

        ItemDomainMachineDesign item = getCurrent();

        if (item != null) {
            return listViewForCurrentEntity();
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
        displayUpdateInstalledInventoryStateDialogContents = false;
        displayAttachTemplateToMachine = false;
        displayMachineDesignReorderOverlayPanel = false;
        catalogItemsDraggedAsChildren = null;
        newCatalogItemsInMachineDesignModel = null;
        currentMachineDesignListRootTreeNode = null;
    }

    public void prepareAddPlaceholder() {
        UserInfo user = SessionUtility.getUser();
        ItemDomainMachineDesign parentMachine = getItemFromSelectedItemInTreeTable();
        ItemElement machinePlaceholder = null;
        try {
            machinePlaceholder = getControllerUtility().prepareMachinePlaceholder(parentMachine, user);
        } catch (CdbException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            return;
        }

        prepareAddNewMachineDesignListConfiguration(machinePlaceholder);
        displayAddMDPlaceholderListConfigurationPanel = true;
    }

    public void prepareAssignTemplate() {
        prepareAddNewMachineDesignListConfiguration();
        templateToCreateNewItem = null;
        displayAttachTemplateToMachine = true;
    }

    public void prepareAddMdFromPlaceholder() {
        prepareAddNewMachineDesignListConfiguration();
        displayAddMDFromTemplateConfigurationPanel = true;

        ItemDomainMachineDesign current = getCurrent();
        current.setCurrentEditItemElementSaveButtonEnabled(true);
    }

    public void prepareAddMdFromCatalog() {
        prepareAddNewMachineDesignListConfiguration();
        displayAddCatalogItemListConfigurationPanel = true;
    }

    public void prepareAddMoveExistingMd() {
        prepareAddNewMachineDesignListConfiguration();
        displayAddMDMoveExistingConfigurationPanel = true;
    }

    public boolean isDisplayDualViewBlockUI() {
        if (displayListConfigurationView) {
            if (!displayListViewItemDetailsView) {
                return true;
            }
        }

        return false;
    }

    public boolean isDisplayFollowInstructionOnRightOnBlockUI() {
        return displayAddMDMoveExistingConfigurationPanel
                || displayAddMDFromTemplateConfigurationPanel
                || displayAddMDPlaceholderListConfigurationPanel
                || displayAssignCatalogItemListConfigurationPanel
                || displayAssignInventoryItemListConfigurationPanel
                || displayAttachTemplateToMachine;
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

    public boolean isDisplayUpdateInstalledInventoryStateDialogContents() {
        return displayUpdateInstalledInventoryStateDialogContents;
    }

    public boolean isDisplayAttachTemplateToMachine() {
        return displayAttachTemplateToMachine;
    }

    public boolean isDisplayMachineDesignReorderOverlayPanel() {
        return displayMachineDesignReorderOverlayPanel;
    }
    
    protected ItemDomainMachineDesign getParentOfSelectedItemInHierarchy(MachineTreeNode machineTreeNode) {
        ItemDomainMachineDesignBaseTreeNode parent = machineTreeNode.getParent();
        if (parent != null) {
            ItemElement element = parent.getElement();
            if (element != null) {
                return (ItemDomainMachineDesign) element.getContainedItem();                 
            }
        }
        
        return null;
    }
    
    protected ItemDomainMachineDesign getParentOfSelectedItemInHierarchy() {
        MachineTreeNode selectedTreeNode = getSelectedItemInListTreeTable();
        return getParentOfSelectedItemInHierarchy(selectedTreeNode); 
    }

    protected void updateCurrentUsingSelectedItemInTreeTable() {
        setCurrent(getItemFromSelectedItemInTreeTable());
        setCurrentHierarchyItemElement(getItemElementFromSelectedItemInTreeTable());
    }

    private ItemElement getItemElementFromSelectedItemInTreeTable() {
        if (selectedItemInListTreeTable != null) {
            ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();
            return element;
        }
        return null;
    }

    private ItemDomainMachineDesign getItemFromSelectedItemInTreeTable() {
        ItemElement itemElement = getItemElementFromSelectedItemInTreeTable();
        Item item = null;
        if (itemElement != null) {
            if (selectedItemInListTreeTable.getType() == CONNECTOR_NODE_TYPE) {
                ItemConnector connector = itemElement.getMdConnector();
                item = connector.getItem();
            } else {
                item = itemElement.getContainedItem();
            }
        }

        if (item instanceof ItemDomainMachineDesign) {
            return (ItemDomainMachineDesign) item;
        } else {
            return null;
        }
    }

    public void unlinkContainedItem2ToDerivedFromItem() {
        ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();

        ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) element.getContainedItem();

        Item assignedItem = mdItem.getAssignedItem();
        Item derivedFromItem = assignedItem.getDerivedFromItem();
        controllerUtility utility = getControllerUtility();
        UserInfo user = SessionUtility.getUser();

        try {
            utility.updateAssignedItem(mdItem, derivedFromItem, user);
        } catch (CdbException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }

        setCurrent(mdItem);
        update();

        resetListDataModel();
        expandToSpecificTreeNode(selectedItemInListTreeTable);
    }

    public void unlinkContainedItem2FromSelectedItem() {
        ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();

        unlinkAssignedItemFromMachineElement(element);
    }

    public void unlinkAssignedItemFromMachineElement(ItemElement element) {
        ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) element.getContainedItem();

        controllerUtility utility = getControllerUtility();
        UserInfo user = SessionUtility.getUser();

        try {
            utility.updateAssignedItem(mdItem, null, user);
        } catch (CdbException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            return;
        }

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

        selectedItemInListTreeTable = (MachineTreeNode) selectedItemInListTreeTable.getParent();

        resetListDataModel();

        expandToSpecificTreeNode(selectedItemInListTreeTable);
        if (detachedDomainId == ItemDomainName.MACHINE_DESIGN_ID) {
            List<MachineTreeNode> machineChildren = getCurrentMachineDesignListRootTreeNode().getTreeNodeItemChildren();
            for (MachineTreeNode node : machineChildren) {
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

    @Deprecated
    /**
     * Templates are only created fully and only previously partially created md
     * from templates will utilize this.
     */
    public void prepareFullfilPlaceholder() {
        // Element with template to be fullfilled
        ItemElement templateElement = (ItemElement) selectedItemInListTreeTable.getData();

        // Select Parent where the template will be created 
        selectedItemInListTreeTable = (MachineTreeNode) selectedItemInListTreeTable.getParent();

        // Execute standard add template function 
        prepareAddMdFromPlaceholder();

        // Remove the template element                 
        getCurrent().removeItemElement(templateElement);

        // Select current template 
        templateToCreateNewItem = (ItemDomainMachineDesign) templateElement.getContainedItem();
        generateTemplateForElementMachineDesignNameVars();
    }

    public void prepareUpdateInventoryInstallState() {
        updateCurrentUsingSelectedItemInTreeTable();

        ItemDomainMachineDesign current = getCurrent();
        boolean isHoused = current.isIsHoused();
        current.setInventoryIsInstalled(isHoused);

        displayUpdateInstalledInventoryStateDialogContents = true;
    }

    public void updateInventoryInstallState() {
        boolean inventoryIsInstalled = isInventoryIsInstalled();
        ItemDomainMachineDesign current = getCurrent();
        current.setIsHoused(inventoryIsInstalled);

        update();

        resetListConfigurationVariables();
        expandToSelectedTreeNodeAndSelect();
    }

    public void prepareAssignInventoryMachineDesignListConfiguration() {
        updateCurrentUsingSelectedItemInTreeTable();
        setCurrentEditItemElement((ItemElement) selectedItemInListTreeTable.getData());
        ItemElement currentEditItemElement = getCurrentEditItemElement();
        setCatalogForElement(currentEditItemElement.getCatalogItem());

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
        prepareAddNewMachineDesignListConfiguration(null);
    }

    public void prepareAddNewMachineDesignListConfiguration(ItemElement element) {
        updateCurrentUsingSelectedItemInTreeTable();

        displayListConfigurationView = true;

        if (element == null) {
            prepareCreateSingleItemElementSimpleDialog();
        } else {
            setCurrentEditItemElement(element);
        }
    }

    public void completeAddNewMachineDesignListConfiguration() {
        resetListConfigurationVariables();
    }

    public String saveTreeListMachineDesignItem() {
        ItemElement currentEditItemElement = getCurrentEditItemElement();
        ItemElement ref = currentEditItemElement;
        saveCreateSingleItemElementSimpleDialog();

        ItemDomainMachineDesign current = getCurrent();
        for (ItemElement element : current.getItemElementDisplayList()) {
            if (element.getName().equals(ref.getName())) {
                ref = element;
                break;
            }
        }

        cloneNewTemplateElementForItemsDerivedFromItem(ref);

        ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) ref.getContainedItem();
        setCurrent(containedItem);
        reloadCurrent();
        setCurrentFlash();

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
            UserInfo user = SessionUtility.getUser();
            for (int i = 0; i < derivedItems.size(); i++) {
                Item item = derivedItems.get(i);
                ItemElement newItemElement = getControllerUtility().cloneCreateItemElement(newTemplateElement, item, user, true, true, true);
                try {
                    ItemDomainMachineDesign current = getCurrent();
                    ItemDomainMachineDesign origCurrent = current;
                    performUpdateOperations((ItemDomainMachineDesign) item);
                    // Update pointer to latest version of the item. 
                    derivedItems.set(i, current);
                    setCurrent(origCurrent);
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

        MachineTreeNode machineDesignTreeRootTreeNode = getCurrentMachineDesignListRootTreeNode();

        if (selectedItemInListTreeTable != null) {
            selectedItemInListTreeTable.setSelected(false);
            selectedItemInListTreeTable = null;
        }

        ItemDomainMachineDesignBaseTreeNode selectedNode = expandToSpecificMachineDesignItem(machineDesignTreeRootTreeNode, item);
        selectedItemInListTreeTable = (MachineTreeNode) selectedNode;
    }

    public static TreeNode expandToItemOrPort(
            ItemDomainMachineDesignTreeNode tree, ItemDomainMachineDesign item, ItemConnector port) {
        TreeNode selection = null;
        TreeNode selectedNode = ItemDomainMachineDesignController.expandToSpecificMachineDesignItem(
                tree,
                (ItemDomainMachineDesign) item);
        if ((selectedNode != null) && (port != null)) {
            selectedNode.setSelected(false);
            selectedNode.setExpanded(true);
            List<TreeNode> children = selectedNode.getChildren();
            for (TreeNode child : children) {
                if (child.getType().equals("Connector")) {
                    ItemConnector connectorChild
                            = ((ItemElement) (child.getData())).getMdConnector();
                    if (connectorChild.equals(port)) {
                        child.setSelected(true);
                        selection = child;
                        break;
                    }
                }
            }
        } else {
            selection = selectedNode;
        }
        return selection;
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
    public static ItemDomainMachineDesignBaseTreeNode expandToSpecificMachineDesignItem(
            ItemDomainMachineDesignBaseTreeNode machineDesignTreeRootTreeNode,
            ItemDomainMachineDesign item) {

        Stack<ItemDomainMachineDesign> machineDesingItemStack = new Stack<>();

        machineDesingItemStack.push(item);

        updateParentStackForMachineDesignByElements(item, machineDesingItemStack);

        return expandToSpecificTreeNodeFromStack(machineDesingItemStack, machineDesignTreeRootTreeNode);
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
    protected ItemDomainMachineDesignBaseTreeNode expandToSpecificMachineDesignItemByRelationship(
            ItemElementRelationshipTypeNames relationshipConstant,
            ItemDomainMachineDesignBaseTreeNode machineDesignTreeRootTreeNode,
            ItemDomainMachineDesign item) {

        Stack<ItemDomainMachineDesign> machineDesingItemStack = new Stack<>();

        machineDesingItemStack.push(item);

        // Find relationship parents
        int relationshipId = relationshipConstant.getDbId();
        List<ItemDomainMachineDesign> parentRelationshipItems = new ArrayList<>();

        while (parentRelationshipItems != null) {
            parentRelationshipItems = itemDomainMachineDesignFacade.fetchRelationshipParentItems(item.getId(), relationshipId);

            if (parentRelationshipItems.isEmpty()) {
                parentRelationshipItems = null;
            } else {
                item = parentRelationshipItems.get(0);
                machineDesingItemStack.push(item);
            }
        }

        updateParentStackForMachineDesignByElements(item, machineDesingItemStack);

        return expandToSpecificTreeNodeFromStack(machineDesingItemStack, machineDesignTreeRootTreeNode);
    }

    private static void updateParentStackForMachineDesignByElements(ItemDomainMachineDesign item, Stack<ItemDomainMachineDesign> machineDesignItemStack) {
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
                machineDesignItemStack.push(parentItem);
                parentItemList = getParentItemList(parentItem);
            } else {
                parentItemList = null;
            }
        }
    }

    private static ItemDomainMachineDesignBaseTreeNode expandToSpecificTreeNodeFromStack(
            Stack<ItemDomainMachineDesign> machineDesingItemStack,
            ItemDomainMachineDesignBaseTreeNode machineDesignTreeRootTreeNode) {
        List<ItemDomainMachineDesignBaseTreeNode> children = machineDesignTreeRootTreeNode.getTreeNodeItemChildren();

        ItemDomainMachineDesignBaseTreeNode result = null;

        while (children != null && machineDesingItemStack.size() > 0) {
            ItemDomainMachineDesign pop = machineDesingItemStack.pop();

            for (ItemDomainMachineDesignBaseTreeNode treeNode : children) {
                ItemElement data = (ItemElement) treeNode.getData();
                Item containedItem = data.getContainedItem();
                if (isItemMachineDesignStatic(containedItem)) {
                    if (containedItem.equals(pop)) {
                        if (machineDesingItemStack.isEmpty()) {
                            result = treeNode;
                            treeNode.setSelected(true);
                            children = null;
                            break;
                        } else {
                            treeNode.setExpanded(true);
                            children = treeNode.getTreeNodeItemChildren();
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private void expandToSpecificTreeNodeAndSelect(MachineTreeNode treeNode) {
        expandToSpecificTreeNode(treeNode);
        selectedItemInListTreeTable = lastExpandedNode;
        lastExpandedNode.setSelected(true);
    }

    protected void expandToSelectedTreeNodeAndSelect() {
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
        List<MachineTreeNode> machineChildren = lastExpandedNode.getTreeNodeItemChildren();
        for (MachineTreeNode ittrTreeNode : machineChildren) {
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

        MachineTreeNode currentMachineDesignListRootTreeNode = getCurrentMachineDesignListRootTreeNode();
        currentMachineDesignListRootTreeNode.clearFilterResults();

        List<TreeNode> currentTopLevels = currentMachineDesignListRootTreeNode.getChildren();

        for (TreeNode node : currentTopLevels) {
            ItemElement data = (ItemElement) node.getData();
            ItemElement mockItemElement = new ItemElement();
            mockItemElement.setName("MOCK element");
            mockItemElement.setContainedItem(data.getContainedItem());
            mockItemElement.setParentItem(mockTopLevelMachineDesign);
            mockItemElement.setSortOrder(data.getSortOrder());

            mockTopLevelMachineDesign.getFullItemElementList().add(mockItemElement);
        }

        mockTopLevelMachineDesign.getFullItemElementList().sort(new ItemElementSortOrderComparator());

        setCurrent(mockTopLevelMachineDesign);

        displayMachineDesignReorderOverlayPanel = true;
    }

    public void prepareReorderMachineDesignElements() {
        updateCurrentUsingSelectedItemInTreeTable();

        displayMachineDesignReorderOverlayPanel = true;
    }

    public String revertReorderMachineDesignElement() {
        Boolean hasElementReorderChangesForCurrent = getHasElementReorderChangesForCurrent();
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
        setCurrentEditItemElement((ItemElement) selectedItemInListTreeTable.getData());

        displayListConfigurationView = true;
        displayAssignCatalogItemListConfigurationPanel = true;
    }

    public String completeAssignCatalogListConfiguration() {
        ItemElement currentEditItemElement = getCurrentEditItemElement();
        Item catalogForElement = getCatalogForElement();
        if (currentEditItemElement != null
                && catalogForElement != null) {

            ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) currentEditItemElement.getContainedItem();
            UserInfo user = SessionUtility.getUser();
            controllerUtility utility = getControllerUtility();

            try {
                utility.updateAssignedItem(mdItem, catalogForElement, user);
            } catch (CdbException ex) {
                LOGGER.error(ex.getMessage());
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return null;
            }

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

            ItemDomainMachineDesign selectedItem = getItemFromSelectedItemInTreeTable();
            controllerUtility utility = getControllerUtility();
            UserInfo user = SessionUtility.getUser();
            ItemDomainMachineDesign newMachineDesign;
            try {
                newMachineDesign = utility.createEntityInstanceBasedOnParent(selectedItem, user);
            } catch (CdbException ex) {
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                LOGGER.error(ex);
                return null;
            }
            newMachineDesign.setName(item.getMachineDesignPlaceholderName());
            idmList.add(newMachineDesign);
        }

        // Verify valid machine desings 
        for (ItemDomainMachineDesign md : idmList) {
            try {
                getControllerUtility().checkItem(md);
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

            UserInfo user = SessionUtility.getUser();
            controllerUtility utility = getControllerUtility();
            ItemDomainMachineDesign current = getCurrent();

            ItemElement newItemElement = getControllerUtility().createItemElement(current, user);

            newItemElement.setContainedItem(mdItem);

            try {
                utility.updateAssignedItem(mdItem, catalogItem, user);
            } catch (CdbException ex) {
                LOGGER.error(ex.getMessage());
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return null;
            }

            prepareAddItemElement(current, newItemElement);

            newlyAddedItemElementList.add(newItemElement);
        }

        update();

        // Add item elements to items created from the current template 
        if (currentViewIsTemplate) {
            // Remap to the added elements 
            List<ItemElement> dbMappedElements = new ArrayList<>();
            ItemDomainMachineDesign current = getCurrent();

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
        }
        List<TreeNode> children = newCatalogItemsInMachineDesignModel.getChildren();
        TreeNode topItem = children.get(0);
        TreeNode newCatalogNode = new DefaultTreeNode(catalogItem);
        topItem.getChildren().add(newCatalogNode);

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

    public TreeNode getSearchResults(String searchString, boolean caseInsensitive) {
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
                    List<TreeNode> children = currentRoot.getChildren();
                    for (TreeNode node : children) {
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

    public String viewCable() {
        ItemElement element = getItemElementFromSelectedItemInTreeTable();
        ItemDomainCableDesign cable = (ItemDomainCableDesign) element.getContainedItem();
        if (cable != null) {
            ItemDomainCableDesignController controller = ItemDomainCableDesignController.getInstance();
            controller.setCurrent(cable);
            return "/views/itemDomainCableDesign/view?id=" + cable.getId() + "faces-redirect=true";
        } else {
            return "";
        }
    }

    public boolean disableAddCableForSelection() {
        ItemDomainMachineDesign selectedItem = getItemFromSelectedItemInTreeTable();
        boolean writeable = LoginController.getInstance().isEntityWriteable(selectedItem.getEntityInfo());
        if (!writeable) {
            return true;
        }

        if (selectedItemInListTreeTable.getType() == CONNECTOR_NODE_TYPE) {
            ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();
            ItemConnector connector = element.getMdConnector();
            return connector.isConnected();
        } else {
            return false;
        }
    }

    public String prepareWizardCable() {

        updateCurrentUsingSelectedItemInTreeTable();
        setCurrentEditItemElement((ItemElement) selectedItemInListTreeTable.getData());
        ItemDomainMachineDesign endpointItem = getItemFromSelectedItemInTreeTable();

        CableWizard cableWizard = CableWizard.getInstance();
        cableWizard.registerClient(this, cableWizardRedirectSuccess);
//        cableWizard.setSelectionEndpoint1(selectedItemInListTreeTable);
        cableWizard.setEndpoint1(endpointItem);
        if (selectedItemInListTreeTable.getType() == CONNECTOR_NODE_TYPE) {
            ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();
            ItemConnector connector = element.getMdConnector();
            cableWizard.setEnd1Port(connector);
        }
        cableWizard.expandEndpoint1TreeAndSelectNode();

        return "/views/itemDomainCableDesign/create?faces-redirect=true";
    }

    public void cleanupCableWizard() {
        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }

    public List<MachineDesignConnectorListObject> getMdConnectorListForCurrent() {
        ItemDomainMachineDesign current = getCurrent();
        List<MachineDesignConnectorListObject> mdConnectorList = current.getMdConnectorList();
        if (mdConnectorList == null) {
            //Generate connector list
            ItemDomainMachineDesign item = getCurrent();
            current.setMdConnectorList(getMdConnectorListForItem(item));
            mdConnectorList = current.getMdConnectorList();
        }
        return mdConnectorList;
    }

    public List<MachineDesignConnectorListObject> getMdConnectorListForItem(ItemDomainMachineDesign item) {
        return MachineDesignConnectorListObject.createMachineDesignConnectorList(item);
    }

    public boolean getDisplayMdConnectorList() {
        return getMdConnectorListForCurrent().size() > 0;
    }

    public List<ItemElementHistory> getCombinedItemElementHistory(ItemElement ie) {
        List<ItemElementHistory> itemElementHistories = new ArrayList<>();
        if (ie == null) {
            return itemElementHistories;
        }
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
            boolean is_installed = true;
            Item currentParentItem = null;
            if (aih != null) {
                currentAssignedItem = aih.getContainedItem2();
                is_installed = aih.getIsHoused();
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
            ieh.setIsHoused(is_installed);

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
    public boolean isCollapseContentsOfInventoryItem() {
        ItemDomainMachineDesign current = getCurrent();
        return current.getDerivedFromItemList().size() == 0;
    }

    public boolean isInventory(ItemDomainMachineDesign item) {
        if (item == null) {
            return false;
        }
        String inventoryetn = EntityTypeName.inventory.getValue();
        return item.isItemEntityType(inventoryetn);
    }

    protected ItemDomainMachineDesign getMachineFromNodeSelectEvent(NodeSelectEvent nodeSelection) {
        TreeNode treeNode = nodeSelection.getTreeNode();
        treeNode.setSelected(false);

        ItemElement element = (ItemElement) treeNode.getData();
        Item parentItem = element.getContainedItem();

        return (ItemDomainMachineDesign) parentItem;
    }

    public void templateToCreateNewItemSelected(NodeSelectEvent nodeSelection) {
        ItemDomainMachineDesign machineFromNodeSelectEvent = getMachineFromNodeSelectEvent(nodeSelection);

        if (machineFromNodeSelectEvent.getRepresentsCatalogElement() != null) {
            // Depends on parent machine to function. 
            SessionUtility.addWarningMessage("Invalid Selection", "Promoted machine element relies on parent for its assignment. Please use parent to continue.", true);
            return;
        }

        templateToCreateNewItem = machineFromNodeSelectEvent;
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
    public MachineTreeNode getFavoriteMachineDesignTreeRootTreeNode() {
        if (favoriteMachineDesignTreeRootTreeNode == null) {
            List<ItemDomainMachineDesign> favoriteItems = getFavoriteItems();

            if (favoriteItems == null) {
                favoriteMachineDesignTreeRootTreeNode = createMachineTreeNodeInstance();
            } else {

                List<ItemDomainMachineDesign> parentFavorites = new ArrayList<>();

                for (ItemDomainMachineDesign item : favoriteItems) {

                    ItemDomainMachineDesign parentMachineDesign = item.getParentMachineDesign();
                    while (parentMachineDesign != null) {
                        ItemDomainMachineDesign ittrParent = parentMachineDesign.getParentMachineDesign();
                        if (ittrParent == null) {
                            item = parentMachineDesign;
                        }
                        parentMachineDesign = ittrParent;
                    }

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

                favoriteMachineDesignTreeRootTreeNode = loadMachineDesignRootTreeNode(parentFavorites);
            }
        }

        return favoriteMachineDesignTreeRootTreeNode;
    }

    public boolean isFavoritesShown() {
        return favoritesShown;
    }

    public void setFavoritesShown(boolean favoritesShown) {
        if (this.favoritesShown == false && favoritesShown == true) {
            // Change from not show to shown. 
            favoriteMachineDesignTreeRootTreeNode = null;
        }
        this.favoritesShown = favoritesShown;
    }
    // </editor-fold>      

    public String getPluginItemMachineDesignSectionsName() {
        return pluginItemMachineDesignSectionsName;
    }

    // <editor-fold defaultstate="collapsed" desc="Element creation implementation ">   
    // <editor-fold defaultstate="collapsed" desc="Functionality">
    public void newMachineDesignElementContainedItemValueChanged() {
        ItemElement currentEditItemElement = getCurrentEditItemElement();
        String name = currentEditItemElement.getContainedItem().getName();
        boolean currentEditItemElementSaveButtonEnabled = false;
        if (!name.equals("")) {
            currentEditItemElementSaveButtonEnabled = true;
        }
        ItemDomainMachineDesign current = getCurrent();
        current.setCurrentEditItemElementSaveButtonEnabled(currentEditItemElementSaveButtonEnabled);

    }

    public void updateInstalledInventoryItem() {
        boolean updateNecessary = false;
        ItemElement currentEditItemElement = getCurrentEditItemElement();
        Item inventoryForElement = getInventoryForElement();
        boolean inventoryIsInstalled = isInventoryIsInstalled();
        ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) currentEditItemElement.getContainedItem();
        Item assignedItem = mdItem.getAssignedItem();

        controllerUtility utility = getControllerUtility();
        UserInfo user = SessionUtility.getUser();

        if (inventoryForElement != null) {
            if (assignedItem.equals(inventoryForElement)) {
                SessionUtility.addInfoMessage("No update", "Inventory selected is same as before");
            } else {
                updateNecessary = true;
                try {
                    utility.updateAssignedItem(mdItem, inventoryForElement, user, inventoryIsInstalled);
                } catch (CdbException ex) {
                    LOGGER.error(ex.getMessage());
                    SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                    return;
                }
            }
        } else if (assignedItem.getDomain().getId() == ItemDomainName.INVENTORY_ID) {
            // Item is unselected, select catalog item
            updateNecessary = true;
            try {
                utility.updateAssignedItem(mdItem, assignedItem.getDerivedFromItem(), user);
            } catch (CdbException ex) {
                LOGGER.error(ex.getMessage());
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return;
            }
        } else {
            SessionUtility.addInfoMessage("No update", "Inventory item not selected");
        }

        if (updateNecessary) {
            setCurrent(mdItem);
            update();
        }
    }

    public DataModel getInstalledInventorySelectionForCurrentElement() {
        ItemDomainMachineDesign current = getCurrent();
        if (current == null) {
            return null;
        }
        DataModel installedInventorySelectionForCurrentElement = current.getInstalledInventorySelectionForCurrentElement();
        if (installedInventorySelectionForCurrentElement == null) {
            Item catalogForElement = getCatalogForElement();
            if (catalogForElement != null) {
                List<Item> derivedFromItemList = catalogForElement.getDerivedFromItemList();
                installedInventorySelectionForCurrentElement = new ListDataModel(derivedFromItemList);
            }
            current.setInstalledInventorySelectionForCurrentElement(installedInventorySelectionForCurrentElement);
        }
        return installedInventorySelectionForCurrentElement;
    }

    public DataModel getMachineDesignTemplatesSelectionList() {
        ItemDomainMachineDesign current = getCurrent();
        DataModel machineDesignTemplatesSelectionList = current.getMachineDesignTemplatesSelectionList();
        if (machineDesignTemplatesSelectionList == null) {
            List<ItemDomainMachineDesign> machineDesignTemplates = itemDomainMachineDesignFacade.getMachineDesignTemplates();
            machineDesignTemplatesSelectionList = new ListDataModel(machineDesignTemplates);
            current.setMachineDesignTemplatesSelectionList(machineDesignTemplatesSelectionList);
        }
        return machineDesignTemplatesSelectionList;
    }

    public DataModel getTopLevelMachineDesignSelectionList() {
        ItemDomainMachineDesign current = getCurrent();

        if (current != null) {
            DataModel topLevelMachineDesignSelectionList = current.getTopLevelMachineDesignSelectionList();
            if (topLevelMachineDesignSelectionList == null) {
                List<ItemDomainMachineDesign> itemsWithoutParents = getItemsWithoutParents();

                removeTopLevelParentOfItemFromList(current, itemsWithoutParents);
                removeEntityTypesFromList(itemsWithoutParents, !isCurrentViewIsTemplate());

                topLevelMachineDesignSelectionList = new ListDataModel(itemsWithoutParents);
                current.setTopLevelMachineDesignSelectionList(topLevelMachineDesignSelectionList);
            }
            return topLevelMachineDesignSelectionList;
        } else {
            return null;
        }
    }

    protected void removeTopLevelParentOfItemFromList(Item item, List<ItemDomainMachineDesign> topLevelItems) {
        List<ItemElement> itemElementMemberList = item.getItemElementMemberList();

        if (itemElementMemberList != null) {
            if (itemElementMemberList.size() == 0) {
                // current item has no parents
                topLevelItems.remove(item);
            } else {
                // Be definition machine design item should only have one parent
                Item parentItem = null;

                while (itemElementMemberList.size() != 0) {
                    ItemElement parentElement = itemElementMemberList.get(0);
                    parentItem = parentElement.getParentItem();

                    itemElementMemberList = parentItem.getItemElementMemberList();
                }

                topLevelItems.remove(parentItem);
            }
        }
    }

    private void removeEntityTypesFromList(List<ItemDomainMachineDesign> itemList, boolean removeTemplate) {

        String templateEntityName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityName);

        String deletedEntityName = EntityTypeName.deleted.getValue();
        EntityType deletedEntityType = entityTypeFacade.findByName(deletedEntityName);

        String inventoryEntityName = EntityTypeName.inventory.getValue();
        EntityType inventoryEntityType = entityTypeFacade.findByName(inventoryEntityName);

        int index = 0;
        while (index < itemList.size()) {

            Item item = itemList.get(index);

            // remove template items or regular items depending on removeTemplate flag
            // remove all deleted items
            // remove all machine inventory
            if (((item.getEntityTypeList().contains(templateEntityType)) && (removeTemplate))
                    || ((!item.getEntityTypeList().contains(templateEntityType)) && (!removeTemplate))
                    || (item.getEntityTypeList().contains(deletedEntityType))
                    || (item.getEntityTypeList().contains(inventoryEntityType))) {

                itemList.remove(index);

            } else {
                index++;
            }
        }
    }

    public void verifyMoveExistingMachineDesignSelected() {
        ItemElement currentEditItemElement = getCurrentEditItemElement();
        if (currentEditItemElement.getContainedItem() != null) {
            ItemDomainMachineDesign current = getCurrent();
            current.setCurrentEditItemElementSaveButtonEnabled(true);
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
            controllerUtility controllerUtility = getControllerUtility();
            List<KeyValueObject> machineDesignNameList = getMachineDesignNameList();
            controllerUtility.setMachineDesginIdentifiersFromTemplateItem(createdFromTemplate, item, machineDesignNameList);
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

        controllerUtility utility = getControllerUtility();
        List<KeyValueObject> nameList = utility.generateTemplateVarsForSelectedMdCreatedFromTemplate(selectedItem);

        setMachineDesignNameList(nameList);
    }

    public void generateTemplateForElementMachineDesignNameVars() {
        if (templateToCreateNewItem != null) {

            controllerUtility utility = getControllerUtility();
            List<KeyValueObject> nameList = utility.generateMachineDesignTemplateNameListRecursivelly(templateToCreateNewItem);
            setMachineDesignNameList(nameList);

            generateMachineDesignName();
        }
    }

    public void generateMachineDesignName() {
        controllerUtility controllerUtility = getControllerUtility();
        List<KeyValueObject> machineDesignNameList = getMachineDesignNameList();
        String generatedName = controllerUtility.generateMachineDesignNameForTemplateItem(templateToCreateNewItem.getName(), machineDesignNameList);
        setMachineDesignName(generatedName);
    }

    @Override
    public void beforeValidateItemElement() throws CloneNotSupportedException, CdbException {
        super.beforeValidateItemElement();

        ItemElement currentEditItemElement = getCurrentEditItemElement();

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

        List<ItemElement> itemElementMemberList = containedItem.getItemElementMemberList();
        if (itemElementMemberList == null) {
            containedItem.setItemElementMemberList(new ArrayList<>());
            itemElementMemberList = containedItem.getItemElementMemberList();
        }

        if (itemElementMemberList.contains(currentEditItemElement) == false) {
            containedItem.getItemElementMemberList().add(currentEditItemElement);
        }

        getControllerUtility().checkItem(containedItem);

    }

    public void assignTemplateToSelectedItem() {
        if (templateToCreateNewItem == null) {
            SessionUtility.addWarningMessage("No Template Selected", "Please select template and try again.");
            return;
        }
        TreeNode selectedItemInListTreeTable = getSelectedItemInListTreeTable();
        MachineTreeNode machineNode = (MachineTreeNode) selectedItemInListTreeTable;

        controllerUtility utility = getControllerUtility();
        UserInfo user = SessionUtility.getUser();
        ItemElement element = machineNode.getElement();
        ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) element.getContainedItem();
        List<KeyValueObject> machineDesignNameList = getMachineDesignNameList();

        ValidInfo assignValidInfo = utility.assignTemplateToItem(
                containedItem,
                templateToCreateNewItem,
                user,
                machineDesignNameList);

        if (!assignValidInfo.isValid()) {
            SessionUtility.addErrorMessage("Error", assignValidInfo.getValidString());
            return;
        }

        updateCurrentUsingSelectedItemInTreeTable();
        update();

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }

    private void createMachineDesignFromTemplateForEditItemElement() throws CdbException, CloneNotSupportedException {
        controllerUtility utility = getControllerUtility();
        ItemElement currentEditItemElement = getCurrentEditItemElement();
        UserInfo ownerUser = SessionUtility.getUser();
        UserGroup ownerGroup = ownerUser.getUserGroupList().get(0);
        List<KeyValueObject> machineDesignNameList = getMachineDesignNameList();

        utility.createMachineDesignFromTemplateHierachically(currentEditItemElement, templateToCreateNewItem, ownerUser, ownerGroup, machineDesignNameList);

        // No longer needed. Skip the standard template relationship process. 
        templateToCreateNewItem = null;
    }

    @Override
    public void failedValidateItemElement() {
        super.failedValidateItemElement();
        ItemElement currentEditItemElement = getCurrentEditItemElement();
        Item originalForElement = getOriginalForElement();
        currentEditItemElement.setContainedItem(originalForElement);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Accessors">   
    public String getMachineDesignName() {
        ItemDomainMachineDesign current = getCurrent();
        String machineDesignName = current.getMachineDesignName();
        return machineDesignName;
    }

    protected void setMachineDesignName(String name) {
        ItemDomainMachineDesign current = getCurrent();
        current.setMachineDesignName(name);
    }

    public List<KeyValueObject> getMachineDesignNameList() {
        ItemDomainMachineDesign current = getCurrentForCurrentData();
        List<KeyValueObject> machineDesignNameList = current.getMachineDesignNameList();
        return machineDesignNameList;
    }

    public void setMachineDesignNameList(List<KeyValueObject> list) {
        ItemDomainMachineDesign current = getCurrentForCurrentData();
        current.setMachineDesignNameList(list);
    }

    public Item getInventoryForElement() {
        ItemDomainMachineDesign current = getCurrent();
        return current.getInventoryForElement();
    }

    public void setInventoryForElement(Item inventoryForElement) {
        ItemDomainMachineDesign current = getCurrent();
        current.setInventoryForElement(inventoryForElement);
    }

    public boolean isInventoryIsInstalled() {
        ItemDomainMachineDesign current = getCurrent();
        return current.isInventoryIsInstalled();
    }

    public Item getCatalogForElement() {
        ItemDomainMachineDesign current = getCurrent();
        return current.getCatalogForElement();
    }

    public void setCatalogForElement(Item catalogForElement) {
        ItemDomainMachineDesign current = getCurrent();
        current.setCatalogForElement(catalogForElement);
    }

    private Item getOriginalForElement() {
        ItemDomainMachineDesign current = getCurrent();
        return current.getOriginalForElement();
    }

    public boolean isDisplayCreateMachineDesignFromTemplateContent() {
        ItemDomainMachineDesign current = getCurrent();
        return current.isDisplayCreateMachineDesignFromTemplateContent();
    }

    protected ItemDomainMachineDesign getCurrentForCurrentData() {
        ItemDomainMachineDesign current = getCurrent();
        if (current == null) {
            setCurrent(new ItemDomainMachineDesign());
            current = getCurrent();
        }
        return current;
    }

    // </editor-fold>    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Base class overrides">                   
    @Override
    public String getItemListPageTitle() {
        return "Machine: Housing Hierarchy";
    }

    @Override
    public String getItemTemplateListPageTitle() {
        return "Machine Element Templates";
    }

    @Override
    public boolean getEntityHasSortableElements() {
        return true;
    }

    public boolean getMachineHasHousingColumn() {
        return false;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    private void resetListViewVariables() {
        currentViewIsTemplate = false;
    }

    protected boolean resetFiltersOnPreRenderList() {
        return false;
    }

    @Override
    public void processPreRenderList() {
        processPreRenderList(false);
    }

    public void processPreRenderList(boolean currentViewIsTemplate) {
        super.processPreRenderList();
        resetListViewVariables();

        resetListConfigurationVariables();

        this.currentViewIsTemplate = currentViewIsTemplate;

        ItemDomainMachineDesign currentLoaded = null;

        String paramValue = SessionUtility.getRequestParameterValue("id");
        if (paramValue != null) {
            Integer idParam = Integer.parseInt(paramValue);
            currentLoaded = itemDomainMachineDesignFacade.findById(idParam);
            if (currentLoaded == null) {
                SessionUtility.addErrorMessage("Error", "Machine design with id " + idParam + " couldn't be found.");
            }
        } else {
            currentLoaded = getCurrentFlash();
            if (currentLoaded != null) {
                setCurrent(currentLoaded);
            }
        }

        if (currentLoaded != null && currentLoaded.getId() != null) {
            boolean itemMachineDesignAndTemplate = isItemMachineDesignAndTemplate(currentLoaded);     
            // list.xhtml does not have required templateList functionality. 
            if (itemMachineDesignAndTemplate && !currentViewIsTemplate) { 
                Integer id = currentLoaded.getId();
                SessionUtility.navigateTo("/views/" + getEntityViewsDirectory() + "/templateList" + ".xhtml?id=" + id + "&faces-redirect=true");                            
                return; 
            }            
        }

        if (resetFiltersOnPreRenderList()) {
            MachineTreeNode rootTreeNode = getCurrentMachineDesignListRootTreeNode();
            rootTreeNode.clearFilterResults();
        }

        if (currentLoaded != null && currentLoaded.getId() != null) {
            expandToSpecificMachineDesignItem(currentLoaded);
        }

        String mode = SessionUtility.getRequestParameterValue("mode");
        if (mode != null && mode.equals(URL_PARAM_DETAIL_MODE)) {
            displayListConfigurationView = true;
            displayListViewItemDetailsView = true;
        }
    }

    @Override
    public void processPreRenderTemplateList() {
        processPreRenderList(true);
    }

    @Override
    public void processViewRequestParams() {
        super.processViewRequestParams();

        String mode = SessionUtility.getRequestParameterValue(URL_PARAM_DETAIL_MODE);

        if (mode == null || !mode.equals(URL_PARAM_DETAIL_MODE_SWITCHVAL)) {
            // Check if last page was machine design listView if yes then redirect to list view. 
            String referrerViewId = SessionUtility.getReferrerViewId();
            String entityViewsDirectory = getDomainPath();
            String listViewPath = entityViewsDirectory + "/listView";
            if (referrerViewId != null && referrerViewId.startsWith(listViewPath)) {
                // Retain listView. 
                String listViewForCurrentEntity = listViewForCurrentEntity();
                try {
                    String redirect = entityViewsDirectory + "/" + listViewForCurrentEntity;
                    SessionUtility.redirectTo(redirect);
                } catch (IOException ex) {
                    SessionUtility.addErrorMessage("Error", ex.getMessage());
                    LOGGER.error(ex);
                }
            }
        }

        ItemDomainMachineDesign current = getCurrent();
        if (current != null) {
            currentViewIsTemplate = isItemMachineDesignAndTemplate(current);
        }
    }

    /**
     * Process list view request parameters.
     *
     * If request is not valid, user will be redirected to appropriate error
     * page.
     */
    public void processListViewRequestParams() {
        Integer idParam = null;
        String idValue = SessionUtility.getRequestParameterValue("id");
        if (idValue != null) {
            idParam = Integer.parseInt(idValue);
            ItemDomainMachineDesign item = findById(idParam);
            if (item != null) {
                setCurrent(item);

                // Cannot only show favorites when specific node is selected by id.
                favoritesShown = false;

                processPreRenderList();
                prepareEntityView(item);
                String redirect = "/list";

                if (loadViewModeUrlParameter()) {
                    return;
                }

                if (currentViewIsTemplate) {
                    redirect = "/templateList";
                }

                SessionUtility.navigateTo("/views/" + getEntityViewsDirectory() + redirect + ".xhtml?id=" + item.getId() + "&faces-redirect=true");
            }
        }
        
        // Current not loaded. Try flash. 
        if (getCurrent() == null) {
            ItemDomainMachineDesign currentFlash = getCurrentFlash();
            setCurrent(currentFlash);
        }

        processPreRender();
    }

    protected boolean loadViewModeUrlParameter() {
        String viewMode = SessionUtility.getRequestParameterValue("mode");
        if (viewMode != null) {
            if (viewMode.equals(URL_PARAM_DETAIL_MODE)) {
                displayListConfigurationView = true;
                displayListViewItemDetailsView = true;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void completeEntityUpdate(ItemDomainMachineDesign entity) {
        super.completeEntityUpdate(entity);

        if (displayListViewItemDetailsView) {
            expandToSpecificMachineDesignItem(entity);
        }
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
                if (isItemMachineDesignStatic(containedItem)) {
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
        return (currentViewIsTemplate == false);
    }

    public boolean isCurrentViewIsTemplate() {
        return currentViewIsTemplate;
    }

    public String currentDualViewList() {
        setCurrentFlash();

        if (currentViewIsTemplate) {
            return templateList();
        }

        return list();
    }

    public String getDetailsPageHeader() {
        String header = getDisplayEntityTypeName();
        if (isCurrentItemTemplate()) {
            header += " Template";
        }
        header += " Details";

        return header;
    }

    public ItemElement getCurrentHierarchyItemElementForItem(ItemDomainMachineDesign item) {
        List<ItemElement> itemElementMemberList = item.getItemElementMemberList();
        ItemElement hierarchyItemElement = item.getCurrentHierarchyItemElement();

        if (hierarchyItemElement == null) {
            Integer id = currentHierarchyItemElement.getId();

            if (id == null) {
                ItemElement ie = new ItemElement();
                ie.setContainedItem(item);

                item.setCurrentHierarchyItemElement(ie);
            } else {
                for (ItemElement ie : itemElementMemberList) {
                    Integer ieId = ie.getId();
                    if (ieId.equals(id)) {
                        item.setCurrentHierarchyItemElement(ie);
                    }
                }
            }
        }

        return item.getCurrentHierarchyItemElement();
    }

    private void setCurrentHierarchyItemElement(ItemElement currentHierarchyItemElement) {
        this.currentHierarchyItemElement = currentHierarchyItemElement;
    }

    @Override
    protected ItemDomainMachineDesignFacade getEntityDbFacade() {
        return itemDomainMachineDesignFacade;
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
    public boolean getEntityDisplayItemConnectors() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId(ItemDomainMachineDesign item) {
        return !item.getIsItemTemplate();
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
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }

    @Override
    public boolean getEntityDisplayTemplates() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDeletedItems() {
        return true;
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
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
    protected DomainImportExportInfo initializeDomainImportInfo() {

        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();

        formatInfo.add(new ImportExportFormatInfo(
                "Machine Hierarchy Creation Format", ImportHelperMachineHierarchy.class
        ));
        formatInfo.add(new ImportExportFormatInfo(
                "Machine Template Instantiation Format", ImportHelperMachineTemplateInstantiation.class
        ));
        formatInfo.add(new ImportExportFormatInfo(
                "Machine Element Update/Compare/Delete Format", ImportHelperMachineItemUpdate.class
        ));
        formatInfo.add(new ImportExportFormatInfo(
                "Machine Element Assign Template Format", ImportHelperMachineAssignTemplate.class
        ));

        String completionUrl = "/views/itemDomainMachineDesign/list?faces-redirect=true";

        return new DomainImportExportInfo(formatInfo, completionUrl);
    }

    @Override
    public boolean getEntityDisplayExportButton() {
        return true;
    }

    @Override
    protected DomainImportExportInfo initializeDomainExportInfo() {

        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();

        formatInfo.add(new ImportExportFormatInfo(
                "Machine Element Update/Compare/Delete Format", ImportHelperMachineItemUpdate.class));
        formatInfo.add(new ImportExportFormatInfo(
                "Machine Hierarchy Transfer Format", ImportHelperMachineHierarchy.class));

        String completionUrl = "/views/itemDomainMachineDesign/list?faces-redirect=true";

        return new DomainImportExportInfo(formatInfo, completionUrl);
    }

    private static List<ItemDomainMachineDesign> createListForTreeNodeHierarchy(
            ItemDomainMachineDesignBaseTreeNode node,
            boolean enforceMaxLevels,
            Integer maxLevels,
            int currentLevel) {

        List<ItemDomainMachineDesign> itemList = new ArrayList<>();

        if (enforceMaxLevels && (currentLevel >= maxLevels)) {
            return itemList;
        }

        currentLevel = currentLevel + 1;

        // walk tree node hierarchy to create list
        node.setExpanded(true);
        List<ItemDomainMachineDesignBaseTreeNode> machineChildren = node.getTreeNodeItemChildren();
        for (ItemDomainMachineDesignBaseTreeNode childNode : machineChildren) {
            ItemElement dataElem = childNode.getElement();
            if (dataElem != null) {
                Item item = dataElem.getContainedItem();
                if (item instanceof ItemDomainMachineDesign) {
                    itemList.add((ItemDomainMachineDesign) item);
                    itemList.addAll(createListForTreeNodeHierarchy(
                            childNode, enforceMaxLevels, maxLevels, currentLevel));
                }
            }
        }

        return itemList;
    }

    public static List<ItemDomainMachineDesign> createListForTreeNodeHierarchy(
            ItemDomainMachineDesignBaseTreeNode rootNode,
            boolean enforceMaxLevels,
            Integer maxLevels) {

        return createListForTreeNodeHierarchy(rootNode, enforceMaxLevels, maxLevels, 0);
    }

    // </editor-fold>       
    // <editor-fold defaultstate="collapsed" desc="Delete support">   
    private void addChildrenForItemToHierarchyNode(
            ItemDomainMachineDesign item,
            TreeNode itemNode) {

        itemNode.setExpanded(true);

        List<ItemElement> childElements = item.getItemElementDisplayList();
        List<ItemDomainMachineDesign> childItems = childElements.stream()
                .map((child) -> (ItemDomainMachineDesign) child.getContainedItem())
                .collect(Collectors.toList());

        for (ItemDomainMachineDesign childItem : childItems) {
            //TreeNode childNode = new DefaultTreeNode(nodeType, childItem.getName(), itemNode);
            TreeNode childNode = new DefaultTreeNode(childItem, itemNode);
            childNode.setExpanded(true);
            // itemNode.getChildren().add(childNode);
            addChildrenForItemToHierarchyNode(childItem, childNode);
        }
    }

    protected void prepareItemHierarchyTree(
            TreeNode rootNode,
            ItemDomainMachineDesign rootItem) {

        if (rootItem != null) {
            rootNode.setExpanded(true);
            //TreeNode childNode = new DefaultTreeNode(nodeType, rootItem.getName(), rootNode);
            TreeNode childNode = new DefaultTreeNode(rootItem, rootNode);
            // rootNode.getChildren().add(childNode);
            addChildrenForItemToHierarchyNode(rootItem, childNode);
        }
    }

    public void collectItemsFromHierarchy(
            ItemDomainMachineDesign parentItem,
            List<ItemDomainMachineDesign> collectedItems,
            List<ItemElement> collectedElements,
            boolean isRootItem,
            boolean rootRelationshipOnly) {

        boolean isValid = true;
        String validString = "";

        List<ItemElement> displayList = parentItem.getItemElementDisplayList();
        for (ItemElement ie : displayList) {
            Item childItem = ie.getContainedItem();
            if (childItem instanceof ItemDomainMachineDesign) {
                // depth first ordering is important here, otherwise there are merge errors for deleted items
                collectItemsFromHierarchy((ItemDomainMachineDesign) childItem, collectedItems, collectedElements, false, rootRelationshipOnly);
                collectedItems.add((ItemDomainMachineDesign) childItem);
                if (!rootRelationshipOnly) {
                    collectedElements.add(ie);
                }
            }
        }

        if (isRootItem) {
            collectedItems.add(parentItem);

            // mark ItemElement for relationship from parent to its container for deletion
            List<ItemElement> memberList = parentItem.getItemElementMemberList();
            if (memberList.size() == 1) {
                ItemElement containerRelElement = memberList.get(0);
                collectedElements.add(containerRelElement);
            }
        }
    }

    public Boolean getMoveToTrashAllowed() {
        return moveToTrashAllowed;
    }

    public Boolean getMoveToTrashHasWarnings() {
        return moveToTrashHasWarnings;
    }

    public String getMoveToTrashDisplayName() {
        return moveToTrashDisplayName;
    }

    public String getMoveToTrashMessage() {
        return moveToTrashMessage;
    }

    public TreeNode getMoveToTrashNode() {
        return moveToTrashNode;
    }

    public ValidWarningInfo validateItemsMovingToTrash(List<ItemDomainMachineDesign> itemsMovingToTrash) {

        boolean isValid = true;
        boolean isWarning = false;

        CdbRole sessionRole = (CdbRole) SessionUtility.getRole();
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();

        for (ItemDomainMachineDesign itemToCheck : itemsMovingToTrash) {

            String validStr = "";
            String warningStr = "";

            // don't allow move to trash for template with instances
            List<Item> templateInstances = itemToCheck.getItemsCreatedFromThisTemplateItem();
            if ((templateInstances != null) && (templateInstances.size() > 0)) {
                isValid = false;
                validStr = validStr + "Item is a template with instances. ";
            }

            // don't allow move to trash for item in multiple assemblies
            List<ItemElement> childMemberList = itemToCheck.getItemElementMemberList();
            if (childMemberList.size() > 1) {
                // this code assumes that a child machine design item has only one 'membership'
                isValid = false;
                validStr = validStr + "Item is a child of multiple parent items or templates (e.g., it might be an unfulfilled template placeholder).";
            }

            // check for various relationships
            List<ItemElementRelationship> relationshipList = itemToCheck.getFullRelationshipList();
            boolean hasCableRelationship = false;
            boolean hasMaarcRelationship = false;
            boolean hasOtherRelationship = false;
            for (ItemElementRelationship relationship : relationshipList) {
                RelationshipType relType = relationship.getRelationshipType();
                if (relType == null) {
                    // shouldn't happen, but....
                    continue;
                } else if (relType.equals(RelationshipTypeFacade.getRelationshipTypeLocation())) {
                    // ignore location relationships
                    continue;
                } else if (relType.equals(RelationshipTypeFacade.getRelationshipTypeTemplate())) {
                    // ignore template relationships, handling explicitly already
                    continue;
                } else if (relType.equals(RelationshipTypeFacade.getRelationshipTypeCable())) {
                    hasCableRelationship = true;
                } else if (relType.equals(RelationshipTypeFacade.getRelationshipTypeMaarc())) {
                    hasMaarcRelationship = true;
                } else {
                    isValid = false;
                    String relTypeName = relType.getName();
                    if (relTypeName != null) {
                        validStr = validStr + "Item has " + relTypeName + " relationship. ";
                    } else {
                        hasOtherRelationship = true;
                    }
                }
            }
            if (hasCableRelationship) {
                // don't allow move to trash for cable endpoints
                isValid = false;
                validStr = validStr + "Item is an endpoint for one or more cables. ";
            }
            if (hasMaarcRelationship) {
                // don't allow move to trash for MAARC relationships
                isValid = false;
                validStr = validStr + "Item has one or more MAARC items. ";
            }
            if (hasOtherRelationship) {
                // don't allow move to trash for other relationships (generic check for now, add specific handling as encountered)
                validStr = validStr + "Item has one or more relationships of unspecified type. ";
            }

            // check permissions for current user
            if (sessionRole != CdbRole.ADMIN) {
                if (!AuthorizationUtility.isEntityWriteableByUser(itemToCheck, sessionUser)) {
                    isValid = false;
                    validStr = validStr + "Current user does not have permission to delete item. ";
                }
            }

            // check if need to warn about property values
            List<PropertyValue> itemProperties = itemToCheck.getPropertyValueList();
            if (itemProperties != null && !itemProperties.isEmpty()) {
                isWarning = true;
                warningStr = warningStr
                        + "Item has property values and/or traveler templates/instances, including: (";
                for (PropertyValue propValue : itemProperties) {
                    PropertyType propType = propValue.getPropertyType();
                    if (propType != null && propType.getName() != null) {
                        warningStr = warningStr
                                + propValue.getPropertyType().getName() + ", ";
                    }
                    warningStr = warningStr.substring(0, warningStr.length() - 2) + "). ";
                }
            }

            // check if need to warn about log entries
            List<Log> itemLogs = itemToCheck.getLogList();
            if (itemLogs != null && !itemLogs.isEmpty()) {
                isWarning = true;
                warningStr = warningStr + "Item has log entries. ";
            }

            if (!validStr.isEmpty()) {
                isValid = false;
                itemToCheck.setMoveToTrashErrorMsg(validStr);
            }
            if (!warningStr.isEmpty()) {
                isWarning = true;
                itemToCheck.setMoveToTrashWarningMsg(warningStr);
            }
        }

        String rootValidStr = "";
        if (!isValid) {
            rootValidStr = "Item children have move to trash validation issues";
        }
        String rootWarningStr = "";
        if (isWarning) {
            rootWarningStr = "Item children have move to trash warnings";
        }
        ValidInfo validInfo = new ValidInfo(isValid, rootValidStr);
        WarningInfo warningInfo = new WarningInfo(isWarning, rootWarningStr);
        return new ValidWarningInfo(validInfo, warningInfo);
    }

    /**
     * Prepares dialog for move to trash operation.
     */
    public void prepareMoveToTrash() {

        updateCurrentUsingSelectedItemInTreeTable();

        ItemDomainMachineDesign itemToDelete = findById(getCurrent().getId());
        if (itemToDelete == null) {
            return;
        }

        moveToTrashNode = null;
        moveToTrashDisplayName = itemToDelete.getName();
        moveToTrashMessage = "";
        moveToTrashHasWarnings = false;
        moveToTrashAllowed = true;

        // collect list of items to delete, for use here in applying restrictions
        // and in moveToTrash for executing the operation
        moveToTrashItemsToUpdate = new ArrayList<>();
        moveToTrashElementsToDelete = new ArrayList<>();
        collectItemsFromHierarchy(itemToDelete, moveToTrashItemsToUpdate, moveToTrashElementsToDelete, true, true);

        // check each item moving to trash for restriction violations
        ValidWarningInfo info = validateItemsMovingToTrash(moveToTrashItemsToUpdate);
        moveToTrashAllowed = info.isValid();
        moveToTrashHasWarnings = info.isWarning();

        // build tree node hierarchy for dialog
        if (moveToTrashItemsToUpdate.size() > 1 || !moveToTrashAllowed || moveToTrashHasWarnings) {
            moveToTrashNode = new DefaultTreeNode();
            prepareItemHierarchyTree(moveToTrashNode, itemToDelete);
        }

        if (!moveToTrashAllowed) {
            moveToTrashMessage = "Unable to move '"
                    + itemToDelete.getName()
                    + "' to trash because there are problems with one or more items in hierarchy. "
                    + "Items with problems are shown in red in the table below. "
                    + "Consult item detail view for additional information. "
                    + "Click 'No' to cancel. ";
            if (moveToTrashHasWarnings) {
                moveToTrashMessage = moveToTrashMessage
                        + "WARNING: there are warnings for one or more items. "
                        + "Items with warnings can be moved to trash.";
            }
        } else {
            String itemDescription = "'" + itemToDelete.getName() + "'";
            if (!itemToDelete.getItemElementDisplayList().isEmpty()) {
                itemDescription = itemDescription
                        + " and its children (hierarchy shown in table below) ";
            }
            moveToTrashMessage = "Click 'Yes' to move "
                    + itemDescription
                    + " to trash. Click 'No' to cancel. "
                    + "NOTE: items restored from trash will appear as top-level items "
                    + "and not within their original container. ";
            if (moveToTrashHasWarnings) {
                moveToTrashMessage = moveToTrashMessage
                        + "WARNING: there are warnings for one or more items. "
                        + "Items with warnings are shown in green in the table below. "
                        + "Items with warnings can be moved to trash.";
            }
        }
    }

    public ValidInfo handleMoveToTrash(
            List<ItemDomainMachineDesign> itemsMovingToTrash,
            List<ItemElement> relationshipElements) {

        boolean isValid = true;
        String validStr = "";

        // mark all items as deleted entity type (moves them to "trash")
        for (ItemDomainMachineDesign item : itemsMovingToTrash) {
            performMoveToTrashOperationsForItem(item);
        }

        // remove relationship for root item to its parent and 
        // add container item to list of items to update
        if (relationshipElements.size() > 1) {
            // should be 0 for a top-level item or 1 for internal node
            isValid = false;
            validStr = "Item has unexpected relationships to other items";

        } else if (relationshipElements.size() == 1) {
            ItemElement ie = relationshipElements.get(0);
            Item childItem = ie.getContainedItem();
            Item ieParentItem = ie.getParentItem();
            ieParentItem.removeItemElement(ie);
            childItem.getItemElementMemberList().remove(ie);
            ie.setMarkedForDeletion(true);
        }

        return new ValidInfo(isValid, validStr);
    }

    /**
     * Executes move to trash operation initiated by 'Yes' button on dialog.
     */
    public void moveToTrash() {

        ItemDomainMachineDesign rootItemToDelete = findById(getCurrent().getId());
        if (rootItemToDelete == null) {
            return;
        }

        ItemDomainMachineDesign current = getCurrent();
        ItemDomainMachineDesign parentMachineDesign = current.getParentMachineDesign();

        // mark items moving to trash as deleted and remove relationship between root item and its parent
        ValidInfo moveValidInfo = handleMoveToTrash(moveToTrashItemsToUpdate, moveToTrashElementsToDelete);
        if (!moveValidInfo.isValid()) {
            SessionUtility.addErrorMessage("Error", moveValidInfo.getValidString());
            return;
        }

        // need to update parentItem if removing relationship
        if (moveToTrashElementsToDelete.size() == 1) {
            ItemElement ie = moveToTrashElementsToDelete.get(0);
            Item ieParentItem = ie.getParentItem();
            moveToTrashItemsToUpdate.add((ItemDomainMachineDesign) ieParentItem);
        }

        try {
            updateList(moveToTrashItemsToUpdate);
        } catch (CdbException ex) {
            // handled adequately by thrower
        }

        moveToTrashNode = null;
        moveToTrashMessage = null;
        moveToTrashItemsToUpdate = null;
        moveToTrashElementsToDelete = null;
        moveToTrashHasWarnings = false;

        ItemDomainMachineDesignDeletedItemsController.getInstance().resetListDataModel();
        ItemDomainMachineDesignDeletedItemsController.getInstance().resetSelectDataModel();

        if (parentMachineDesign != null) {
            expandToSpecificMachineDesignItem(parentMachineDesign);
        }
    }

    protected void performMoveToTrashOperationsForItem(ItemDomainMachineDesign item) {
        item.setIsDeleted();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Update permission for branch">   
    public EntityInfo getEntityInfoBranchUpdate() {
        ItemDomainMachineDesign current = getCurrent();
        return current.getEntityInfoBranchUpdate();
    }

    public void prepareUpdatePermissionForBranch() {
        updateCurrentUsingSelectedItemInTreeTable();

        ItemDomainMachineDesign current = getCurrent();

        EntityInfo entityInfo = current.getEntityInfo();
        EntityInfo mockEi = new EntityInfo();

        mockEi.setOwnerUser(entityInfo.getOwnerUser());
        mockEi.setOwnerUserGroup(entityInfo.getOwnerUserGroup());
        mockEi.setIsGroupWriteable(entityInfo.getIsGroupWriteable());

        current.setEntityInfoBranchUpdate(mockEi);
    }

    public void updatePermissionForBranch() {
        Boolean isAdmin = LoginController.getInstance().isLoggedInAsAdmin();

        if (!isAdmin) {
            SessionUtility.addErrorMessage("Insufficient privileges", "Only admins can update permission for a branch.");
            return;
        }

        moveToTrashItemsToUpdate = new ArrayList<>();
        ItemDomainMachineDesign selectedItem = getCurrent();

        collectItemsFromHierarchy(selectedItem, moveToTrashItemsToUpdate, new ArrayList<ItemElement>(), true, true);

        EntityInfo mockEi = selectedItem.getEntityInfoBranchUpdate();

        for (Item item : moveToTrashItemsToUpdate) {
            EntityInfo entityInfo = item.getEntityInfo();
            entityInfo.setOwnerUser(mockEi.getOwnerUser());
            entityInfo.setOwnerUserGroup(mockEi.getOwnerUserGroup());
            entityInfo.setIsGroupWriteable(mockEi.getIsGroupWriteable());
        }

        try {
            updateList(moveToTrashItemsToUpdate);
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            LOGGER.error(ex);
        } catch (RuntimeException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }
        expandToSelectedTreeNodeAndSelect();
    }
    
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Update Node Representation">   

    public List<ItemElement> getElementsAvaiableForNodeRepresentation() {
        return elementsAvaiableForNodeRepresentation;
    }

    public ItemElement getSelectedElementForNodeRepresentation() {
        return selectedElementForNodeRepresentation;
    }

    public void setSelectedElementForNodeRepresentation(ItemElement selectedElementForNodeRepresentation) {
        this.selectedElementForNodeRepresentation = selectedElementForNodeRepresentation;
    }

    public Boolean getMatchElementNamesForTemplateInstances() {
        return matchElementNamesForTemplateInstances;
    }

    public void setMatchElementNamesForTemplateInstances(Boolean matchElementNamesForTemplateInstances) {
        this.matchElementNamesForTemplateInstances = matchElementNamesForTemplateInstances;
    }
    
    public void prepareMachineUnlinkRepresentingAssemblyElement() {
        updateCurrentUsingSelectedItemInTreeTable();                
    }
    
    public String unlinkRepresentingAssemblyElementForCurrentMachine() {
        ItemDomainMachineDesign current = getCurrent();
        
        try {             
            this.controllerUtility.updateRepresentingAssemblyElementForMachine(current, null, false);
            update(); 
        } catch (CdbException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
        
        // Reset list data model to show exact state of the object after funciton if it failed or not.
        resetListDataModel();
        return listForCurrentEntity(); 
    }
    
    
    public void prepareMachineAssignRepresentingAssemblyElement() {
        updateCurrentUsingSelectedItemInTreeTable();

        ItemDomainMachineDesign current = getCurrent();
        ItemDomainMachineDesign parentMachineDesign = current.getParentMachineDesign();
        
        elementsAvaiableForNodeRepresentation = null; 
        selectedElementForNodeRepresentation = null;
        matchElementNamesForTemplateInstances = false; 
        
        if (current.getRepresentsCatalogElement() != null) {
            ItemElement catElement = current.getRepresentsCatalogElement(); 
            SessionUtility.addErrorMessage("Already assigned", "This element is representing catalog element " + catElement.getName());
            return; 
        }
        
        if (current.getAssignedItem() != null) {
            Item assignedItem = current.getAssignedItem();
            SessionUtility.addErrorMessage("Already assigned", "Please unassign assigned item before proceeding: " + assignedItem);
            return; 
        }                                                       
        
        if (parentMachineDesign != null) {
            Item assignedItem = parentMachineDesign.getAssignedItem();
            
            if (assignedItem != null && assignedItem.getItemElementDisplayList().size() != 0) {                
                elementsAvaiableForNodeRepresentation = controllerUtility.fetchElementsAvaiableForNodeRepresentation(current); 
            } else {
                SessionUtility.addErrorMessage("No Assembly", "Parent must have assembly assigned.");
                return; 
            }
        } else {
            SessionUtility.addErrorMessage("No Parent", "Cannot fetch a parent assembly for assigning to assembly element.`");
            return; 
        }        
        
        if (elementsAvaiableForNodeRepresentation != null && elementsAvaiableForNodeRepresentation.size() > 0) {
            
        } else {
            SessionUtility.addErrorMessage("No Avaiable Elements", "All elements of parent's assembly have already been assigned to machine elements for representation.");
            elementsAvaiableForNodeRepresentation = null; 
            return; 
        }
    }
    
    public String assignRepresentingAssemblyElementForCurrentMachine() {
        if (selectedElementForNodeRepresentation == null) {
            SessionUtility.addErrorMessage("No Element Selected.", "Please select element and try again.");
            return null; 
        } 
        
        ItemDomainMachineDesign current = getCurrent();        
        
        try {             
            this.controllerUtility.updateRepresentingAssemblyElementForMachine(current, selectedElementForNodeRepresentation, matchElementNamesForTemplateInstances);
            update(); 
        } catch (CdbException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
        
        // Reset list data model to show exact state of the object after funciton if it failed or not.
        resetListDataModel();
        return listForCurrentEntity(); 
    }

    // </editor-fold>
}
