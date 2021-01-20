/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.InventoryBillOfMaterialItemStates;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperInventory;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainInventorySettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainInventoryLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.beans.ConnectorFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCable;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemStatusUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import gov.anl.aps.cdb.portal.view.objects.InventoryBillOfMaterialItem;
import gov.anl.aps.cdb.portal.view.objects.InventoryItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.component.selectonelistbox.SelectOneListbox;
import org.primefaces.model.TreeNode;
import org.primefaces.model.DefaultTreeNode;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author djarosz
 */
@Named("itemDomainInventoryController")
@SessionScoped
public class ItemDomainInventoryController extends ItemDomainInventoryBaseController<ItemDomainInventoryControllerUtility, ItemDomainInventory, ItemDomainInventoryFacade, ItemDomainInventorySettings> {

    public static final String ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME = "Component Instance Status";

    private static final String DEFAULT_DOMAIN_NAME = ItemDomainName.inventory.getValue();
    private final String DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME = "Catalog";

    private static final Logger logger = LogManager.getLogger(ItemDomainInventoryController.class.getName());

    private List<PropertyValue> filteredPropertyValueList = null;

    private List<SelectItem> domainFilterOptions = null;

    //Variables used for creation of new inventory item.     
    private TreeNode currentItemBOMListTree = null;
    private TreeNode selectedItemBOMTreeNode = null;
    private boolean showOptionalPartsInBom = false;
    private Boolean currentItemBOMTreeHasOptionalItems = null;

    private Connector selectedConnectorOfCurrentItem = null;
    private Connector selectedConnectorOfSecondItem = null;
    // Connector that is on the same side as current. 
    private ItemConnector firstCableItemConnector = null;
    private ItemConnector secondCableItemConnector = null;
    private ItemDomainCable currentConnectionCable = null;
    private ListDataModel inventoryItemsWithRequiredConnector = null;
    private ItemDomainInventory selectedSecondItemWithRequiredConnection = null;

    private ItemDomainInventoryLazyDataModel itemDomainInventoryLazyDataModel = null;

    private List<ItemElementRelationship> relatedMAARCRelationshipsForCurrent = null;

    private boolean connectionEditRendered = false;

    @EJB
    private ItemElementRelationshipFacade itemElementRelationshipFacade;

    @EJB
    private RelationshipTypeFacade relationshipTypeFacade;

    @EJB
    private ItemDomainInventoryFacade itemDomainInventoryFacade;

    @EJB
    private ConnectorFacade connectorFacade;

    private static ItemDomainInventoryController apiInstance;

    public static synchronized ItemDomainInventoryController getApiInstance() {
        if (apiInstance == null) {
            apiInstance = new ItemDomainInventoryController();
            apiInstance.prepareApiInstance();
        }
        return apiInstance;
    }

    public boolean isInventory(Item item) {
        return item instanceof ItemDomainInventory;
    }

    @Override
    protected ItemDomainInventoryFacade getEntityDbFacade() {
        return itemDomainInventoryFacade;
    }

    @Override
    protected ItemDomainInventorySettings createNewSettingObject() {
        return new ItemDomainInventorySettings(this);
    }   

    @Override
    protected String generatePaddedUnitName(int itemNumber) {
        return ItemDomainInventory.generatePaddedUnitName(itemNumber);
    }

    @Override
    protected void loadEJBResourcesManually() {
        super.loadEJBResourcesManually();
        itemElementRelationshipFacade = ItemElementRelationshipFacade.getInstance();
        relationshipTypeFacade = RelationshipTypeFacade.getInstance();
        itemDomainInventoryFacade = ItemDomainInventoryFacade.getInstance();
        connectorFacade = ConnectorFacade.getInstance();
    }

    public static ItemDomainInventoryController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (ItemDomainInventoryController) findDomainController(DEFAULT_DOMAIN_NAME);
        } else {
            return getApiInstance();
        }
    }

    @Override
    protected ItemCreateWizardController getItemCreateWizardController() {
        return ItemCreateWizardDomainInventoryController.getInstance();
    }

    @Override
    public ItemMultiEditController getItemMultiEditController() {
        return ItemMultiEditDomainInventoryController.getInstance();
    }

    @Override
    public ItemEnforcedPropertiesController getItemEnforcedPropertiesController() {
        return ItemEnforcedPropertiesDomainInventoryController.getInstance();
    }

    private List<ItemElementRelationship> findItemCableConnectionRelationship(Item item) {
        // Support items that have not yet been saved to db.
        if (item.getSelfElement().getId() != null) {
            return itemElementRelationshipFacade
                    .findItemElementRelationshipListByNameAndItemElementId(ItemElementRelationshipTypeNames.itemCableConnection.getValue(),
                            item.getSelfElement().getId());
        }

        return null;
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
    protected void resetVariablesForCurrent() {
        super.resetVariablesForCurrent();
        relatedMAARCRelationshipsForCurrent = null;
    }

    @Override
    public List<ItemDomainInventory> getItemListWithProject(ItemProject itemProject) {
        String projectName = itemProject.getName();
        return itemDomainInventoryFacade.findByDomainAndProjectOrderByDerivedFromItem(getDefaultDomainName(), projectName);
    }

    @Override
    public List<EntityType> getFilterableEntityTypes() {
        return getDefaultDomainDerivedFromDomain().getAllowedEntityTypeList();
    }

    public List<ItemElementRelationship> getItemCableRelationshipList(Item inventoryItem) {
        if (inventoryItem.getItemCableConnectionsRelationshipList() == null) {
            List<ItemElementRelationship> cableRelationshipList;
            cableRelationshipList = findItemCableConnectionRelationship(inventoryItem);
            inventoryItem.setItemCableConnectionsRelationshipList(cableRelationshipList);
        }
        return inventoryItem.getItemCableConnectionsRelationshipList();
    }

    public boolean getDisplayItemCableRelationshipList() {
        if (getCurrent() != null) {
            List<ItemElementRelationship> itemCableRelationshipList = getItemCableRelationshipList(getCurrent());
            return itemCableRelationshipList != null && !itemCableRelationshipList.isEmpty();
        }
        return false;
    }

    public void createItemCableConnectionRelationshipForCurrent() {
        resetConnectorVairables();
        connectionEditRendered = true;
    }

    public void cancelCreateItemCableConnectionRelationshipForCurrent() {
        resetConnectorVairables();
    }

    public List<Connector> getAvailableConnectorListForCurrent() {
        Item inventoryItem = getCurrent();

        List<Connector> availableConnectors = inventoryItem.getItemAvaliableConnectorsList();
        if (availableConnectors == null) {
            availableConnectors = connectorFacade.getAvailableConnectorsForInventoryItem(inventoryItem, null, null);
            inventoryItem.setItemAvaliableConnectorsList(availableConnectors);
        }

        return availableConnectors;
    }

    public boolean isConnectionEditRendered() {
        return connectionEditRendered;
    }

    public boolean getItemHasAvaialbeConnectors() {
        return getAvailableConnectorListForCurrent().size() > 0;
    }

    public void handleConnectorSelectionEvent(AjaxBehaviorEvent event) {
        SelectOneListbox selectOneListBox = (SelectOneListbox) event.getSource();
        Connector connector = (Connector) selectOneListBox.getValue();
        selectedConnectorOfCurrentItem = connector;

        if (currentConnectionCable == null) {
            ItemDomainCableController itemDomainCableController = ItemDomainCableController.getInstance();
            currentConnectionCable = itemDomainCableController.createEntityInstance();

            // Item should have two connectors
            List<ItemConnector> cableConnectorList = currentConnectionCable.getItemConnectorList();
            if (cableConnectorList.size() == 2) {
                firstCableItemConnector = cableConnectorList.get(0);
                secondCableItemConnector = cableConnectorList.get(1);
            } else {
                // This should not happen. 
                SessionUtility.addErrorMessage("Error", "Cable was not created sucessfully.");
                currentConnectionCable = null;
            }
        }

        updateConnectorTypesForCurrentCable();

    }

    public void updateConnectorTypesForCurrentCable() {
        if (currentConnectionCable != null) {
            boolean isDirect = ItemDomainCableController.getIsDirectConnectionForItem(currentConnectionCable);
            ConnectorType connectorType = selectedConnectorOfCurrentItem.getConnectorType();
            boolean connectorGender = selectedConnectorOfCurrentItem.getIsMale();

            firstCableItemConnector.getConnector().setConnectorType(connectorType);
            secondCableItemConnector.getConnector().setConnectorType(connectorType);
            firstCableItemConnector.getConnector().setIsMale(!connectorGender);

            if (isDirect) {
                secondCableItemConnector.getConnector().setIsMale(connectorGender);
            } else {
                secondCableItemConnector.getConnector().setIsMale(!connectorGender);
            }

            loadAvailableInventoryItemListWithSecondItemConnector();
        }
    }

    public void loadAvailableInventoryItemListWithSecondItemConnector() {
        List<ItemDomainInventory> inventoryWithRequiredConnectorType;

        ConnectorType connectorType = secondCableItemConnector.getConnector().getConnectorType();
        Boolean requiredIsMale = !secondCableItemConnector.getConnector().getIsMale();

        inventoryWithRequiredConnectorType = itemDomainInventoryFacade.getInventoryItemsWithAvailableConnectorType(connectorType, requiredIsMale);
        inventoryWithRequiredConnectorType.remove(current);

        inventoryItemsWithRequiredConnector = new ListDataModel(inventoryWithRequiredConnectorType);
    }

    public List<Connector> loadAppropriateConnectorsForItemAndCurrentSelections(Item item) {
        if (item != null) {
            Boolean reqIsMale = !secondCableItemConnector.getConnector().getIsMale();
            ConnectorType reqConnectorType = secondCableItemConnector.getConnector().getConnectorType();

            return connectorFacade.getAvailableConnectorsForInventoryItem(item, reqConnectorType, reqIsMale);
        }
        return null;
    }

    public void saveConnectionInformation(String onSuccessCommand) {
        if (selectedConnectorOfCurrentItem == null) {
            SessionUtility.addErrorMessage("Could not create connection", "Please select a connector.");
            return;
        }

        if (selectedSecondItemWithRequiredConnection == null
                || selectedConnectorOfSecondItem == null) {
            SessionUtility.addErrorMessage("Could not create connection", "Please select a port on second inventory item.");
            return;
        }

        ItemConnector firstItemConnector = getItemConnectorToConnectTo(getCurrent(), selectedConnectorOfCurrentItem);
        ItemConnector secondItemConnector = getItemConnectorToConnectTo(selectedSecondItemWithRequiredConnection, selectedConnectorOfSecondItem);

        ItemElementRelationship currentToCableRelationship = createItemElementRelationshipToCableConnector(getCurrent(), currentConnectionCable, firstItemConnector, firstCableItemConnector);
        ItemElementRelationship secondToCableRelationship = createItemElementRelationshipToCableConnector(selectedSecondItemWithRequiredConnection, currentConnectionCable, secondItemConnector, secondCableItemConnector);

        // New cable item still needs a list for item element relationships. 
        ItemElement cableSelfElement = currentConnectionCable.getSelfElement();
        cableSelfElement.setItemElementRelationshipList1(new ArrayList<>());

        // Add the apporpriate item relationships to the model. 
        addItemElementRelationshipToItem(getCurrent(), currentToCableRelationship, false);
        addItemElementRelationshipToItem(selectedSecondItemWithRequiredConnection, secondToCableRelationship, false);
        addItemElementRelationshipToItem(currentConnectionCable, currentToCableRelationship, true);
        addItemElementRelationshipToItem(currentConnectionCable, secondToCableRelationship, true);

        this.update();

        // Prevent re-render of non-needed dialog content.
        resetConnectorVairables();

        SessionUtility.executeRemoteCommand(onSuccessCommand);

    }

    private void addItemElementRelationshipToItem(Item item, ItemElementRelationship ier, boolean secondItem) {
        ItemElement selfElement = item.getSelfElement();
        List<ItemElementRelationship> ierList;
        if (secondItem) {
            ierList = selfElement.getItemElementRelationshipList1();
        } else {
            ierList = selfElement.getItemElementRelationshipList();
        }
        ierList.add(ier);
    }

    private ItemElementRelationship createItemElementRelationshipToCableConnector(Item item,
            Item cableItem, ItemConnector itemItemConnector, ItemConnector cableItemConnector) {
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setFirstItemElement(item.getSelfElement());
        itemElementRelationship.setFirstItemConnector(itemItemConnector);
        itemElementRelationship.setSecondItemElement(cableItem.getSelfElement());
        itemElementRelationship.setSecondItemConnector(cableItemConnector);

        RelationshipType cableConnectionRelationshipType = getCableConnectionRelationshipType();
        itemElementRelationship.setRelationshipType(cableConnectionRelationshipType);

        return itemElementRelationship;
    }

    /**
     * get a valid item connector for a particular connector of an item.
     *
     * @param inventoryItem
     * @param connector
     * @return
     */
    private ItemConnector getItemConnectorToConnectTo(Item inventoryItem, Connector connector) {
        List<ItemConnector> itemConnectors = inventoryItem.getItemConnectorList();

        // Verify if an item already has a item connection. 
        for (ItemConnector itemConnector : itemConnectors) {
            if (itemConnector.getConnector().equals(connector)) {
                return itemConnector;
            }
        }

        // Create a new item connector
        // Verify that a connector could be created. 
        Item catalogItem = inventoryItem.getDerivedFromItem();
        List<ItemConnector> catalogItemConnectorList = catalogItem.getItemConnectorList();
        for (ItemConnector catalogItemConnector : catalogItemConnectorList) {
            if (catalogItemConnector.getConnector().equals(connector)) {
                ItemConnector itemConnector = new ItemConnector();
                itemConnector.setConnector(connector);
                itemConnector.setItem(inventoryItem);
                inventoryItem.getItemConnectorList().add(itemConnector);
                return itemConnector;
            }
        }

        return null;
    }

    public void disconnectPortConnection(ItemElementRelationship cableConnectionRelationship) {
        ItemConnector firstInventoryConnector = cableConnectionRelationship.getFirstItemConnector();

        if (firstInventoryConnector != null) {
            ItemConnectorController itemConnectorController = ItemConnectorController.getInstance();
            ItemDomainCable cableItem = (ItemDomainCable) itemConnectorController.getItemConnectedVia(firstInventoryConnector);
            ItemConnector secondInventoryConnector = itemConnectorController.getItemConnectorOfItemConnectedTo(firstInventoryConnector);

            if (cableItem != null) {
                // Cable item holds the connectors that both items are connected by. 
                ItemDomainCableController itemDomainCableController = ItemDomainCableController.getInstance();
                itemDomainCableController.destroyCableConnection(cableItem);
                reloadCurrent();
            }
            // Connectors contain no significant information unless they are connected. 
            itemConnectorController.destroy(firstInventoryConnector);
            itemConnectorController.destroy(secondInventoryConnector);
        }
    }

    public void disconnectSecondConnector() {
        selectedSecondItemWithRequiredConnection = null;
        selectedConnectorOfSecondItem = null;
    }

    public boolean selectedSecondConnectorDefined() {
        return selectedConnectorOfSecondItem != null;
    }

    public ListDataModel getInventoryItemsWithRequiredConnector() {
        return inventoryItemsWithRequiredConnector;
    }

    public Item getCurrentConnectionCable() {
        return currentConnectionCable;
    }

    public ItemConnector getFirstCableItemConnector() {
        return firstCableItemConnector;
    }

    public ItemConnector getSecondCableItemConnector() {
        return secondCableItemConnector;
    }

    public Connector getSelectedConnectorOfSecondItem() {
        return selectedConnectorOfSecondItem;
    }

    public void setSelectedConnectorOfSecondItem(Connector selectedConnectorOfSecondItem) {
        this.selectedConnectorOfSecondItem = selectedConnectorOfSecondItem;
    }

    public Item getSelectedSecondItemWithRequiredConnection() {
        return selectedSecondItemWithRequiredConnection;
    }

    public void setSelectedSecondItemWithRequiredConnection(ItemDomainInventory selectedInventoryItemWithRequiredConnection) {
        this.selectedConnectorOfSecondItem = null;
        this.selectedSecondItemWithRequiredConnection = selectedInventoryItemWithRequiredConnection;
    }

    private boolean isItemInAssemblyTree(Item currentItem, Item itemToLookFor) {
        return isItemInAssemblyTree(currentItem, itemToLookFor, true, true);
    }

    private boolean isItemInAssemblyTree(Item currentItem, Item itemToLookFor, boolean isNeedToMoveDown, boolean isNeedToMoveUp) {
        if (isNeedToMoveUp) {
            // Move up the assembly tree
            List<ItemElement> itemElementMembershipList = currentItem.getItemElementMemberList();
            if (itemElementMembershipList != null) {
                for (ItemElement itemElement : itemElementMembershipList) {
                    Item parentItem = itemElement.getParentItem();
                    if (parentItem != null) {
                        if (parentItem.equals(itemToLookFor)) {
                            return true;
                        }
                        if (isItemInAssemblyTree(parentItem, itemToLookFor, false, true)) {
                            return true;
                        }
                    }
                }
            }
        }
        if (isNeedToMoveDown) {
            // Move down the assembly tree
            List<ItemElement> itemElementList = currentItem.getItemElementDisplayList();
            if (itemElementList != null) {
                for (ItemElement itemElement : itemElementList) {
                    Item containedItem = itemElement.getContainedItem();
                    if (containedItem != null) {
                        if (containedItem.equals(itemToLookFor)) {
                            return true;
                        }
                        if (isItemInAssemblyTree(containedItem, itemToLookFor, true, false)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
    }

    @Override
    public DataModel getListDataModel() {
        return getItemDomainInventoryLazyDataModel();
    }

    @Override
    protected Boolean fetchFilterablePropertyValue(Integer propertyTypeId) {
        return true;
    }

    public ItemDomainInventoryLazyDataModel getItemDomainInventoryLazyDataModel() {
        if (itemDomainInventoryLazyDataModel == null) {
            itemDomainInventoryLazyDataModel = new ItemDomainInventoryLazyDataModel(itemDomainInventoryFacade, getDefaultDomain());
        }
        return itemDomainInventoryLazyDataModel;
    }

    public boolean isInventoryDomainItem(Item item) {
        return item.getDomain().getName().equals(getDefaultDomainName());
    }

    private RelationshipType getCableConnectionRelationshipType() {
        RelationshipType relationshipType = relationshipTypeFacade.findByName(ItemElementRelationshipTypeNames.itemCableConnection.getValue());
        if (relationshipType == null) {
            RelationshipTypeController controller = RelationshipTypeController.getInstance();
            String name = ItemElementRelationshipTypeNames.itemCableConnection.getValue();
            relationshipType = controller.createRelationshipTypeWithName(name);
        }
        return relationshipType;
    }

    public void createSaveFromDialog(String onSuccessCommand) {
        String result = create();
        if (result != null) {
            SessionUtility.executeRemoteCommand(onSuccessCommand);
        }
    }

    public void createCancelFromDialog() {
        if (getCurrent() != null) {
            ItemDomainCatalog catalogItem = getCurrent().getCatalogItem();
            if (catalogItem != null) {
                catalogItem.getDerivedFromItemList().remove(getCurrent());
            }
            setCurrent(null);
        }
    }

    @Override
    public void prepareAddItemDerivedFromItem(Item item) {
        super.prepareAddItemDerivedFromItem(item);
        prepareBillOfMaterialsForCurrentItem();
    }

    public void prepareBillOfMaterialsForCurrentItem() {
        getControllerUtility().prepareBillOfMaterialsForItem(getCurrent());
    }   

    private void resetConnectorVairables() {
        currentConnectionCable = null;
        firstCableItemConnector = null;
        secondCableItemConnector = null;

        inventoryItemsWithRequiredConnector = null;

        selectedSecondItemWithRequiredConnection = null;
        selectedConnectorOfSecondItem = null;
        selectedConnectorOfCurrentItem = null;

        connectionEditRendered = false;

        Item item = getCurrent();
        if (item != null) {
            item.setItemAvaliableConnectorsList(null);
        }
    }

    @Override
    public String prepareView(ItemDomainInventory item) {
        resetBOMSupportVariables();
        resetConnectorVairables();
        return super.prepareView(item); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String prepareEdit(ItemDomainInventory inventoryItem) {
        resetBOMSupportVariables();
        setCurrent(inventoryItem);
        return super.prepareEdit(inventoryItem);
    }

    public Boolean displayBOMEditButton() {
        if (current != null) {
            List<ItemElement> catalogItemElementDisplayList;
            if (current.getDerivedFromItem() != null) {
                catalogItemElementDisplayList = current.getDerivedFromItem().getItemElementDisplayList();
                return catalogItemElementDisplayList != null && catalogItemElementDisplayList.isEmpty() == false;
            }
        }

        return false;
    }

    public String saveEditBOMList() {
        return this.update();
    }

    public void prepareEditBOMForCurrent() {
        resetBOMSupportVariables();
        prepareBillOfMaterialsForCurrentItem();
    }

    @Override
    public void setCurrentDerivedFromItem(Item derivedFromItem) {
        if (getCurrent().getDerivedFromItem() == derivedFromItem) {
            //No need to set it is currently the same catalog item.
            return;
        }
        super.setCurrentDerivedFromItem(derivedFromItem);
        resetBOMSupportVariables();
    }

    public void resetBOMSupportVariables() {
        // All variables will be genererated when needed. 
        if (getCurrent() != null) {
            getCurrent().setInventoryDomainBillOfMaterialList(null);
            getCurrent().setContainedInBOM(null);
        }        
        currentItemBOMListTree = null;
        selectedItemBOMTreeNode = null;
        showOptionalPartsInBom = false;
        currentItemBOMTreeHasOptionalItems = null;

    }

    public TreeNode getCurrentItemBOMListTree() {
        if (currentItemBOMListTree == null) {
            currentItemBOMListTree = buildTreeNodeFromParentItem(getCurrent().getContainedInBOM());
        }

        return currentItemBOMListTree;
    }

    private TreeNode buildTreeNodeFromParentItem(InventoryBillOfMaterialItem startingBOM) {
        return buildTreeNodeFromParentItem(null, null, startingBOM);
    }

    private TreeNode buildTreeNodeFromParentItem(TreeNode root, TreeNode parent, InventoryBillOfMaterialItem nextBOM) {
        // The tree needs to be created. 
        if (root == null) {
            root = new DefaultTreeNode(null, null);
            parent = new DefaultTreeNode(nextBOM, root);
            parent.setExpanded(true);
            parent.setType(nextBOM.getState());
        }

        /*
        if (updateSelectedPointer) {
            if (parent.getData() == selectedItemBOMTreeNode.getData()) {
                selectedItemBOMTreeNode = parent; 
                updateSelectedPointer = false; 
            }
        }
         */
        if (nextBOM.getInventoryItem() != null) {
            List<InventoryBillOfMaterialItem> nextItemBOMList;
            nextItemBOMList = nextBOM.getInventoryItem().getInventoryDomainBillOfMaterialList();

            if (nextItemBOMList != null && nextItemBOMList.isEmpty() == false) {
                for (InventoryBillOfMaterialItem iBOM : nextItemBOMList) {
                    TreeNode newNode = new DefaultTreeNode(iBOM, parent);
                    newNode.setExpanded(true);
                    newNode.setType(iBOM.getState());
                    buildTreeNodeFromParentItem(root, newNode, iBOM);
                }
            }
        }

        return root;

    }

    public void addNewChildrenToCurrentSelection() {
        buildTreeNodeFromParentItem(currentItemBOMListTree,
                selectedItemBOMTreeNode,
                (InventoryBillOfMaterialItem) selectedItemBOMTreeNode.getData());
    }

    public TreeNode getSelectedItemBOMTreeNode() {
        return selectedItemBOMTreeNode;
    }

    public void setSelectedItemBOMTreeNode(TreeNode selectedItemBOMTreeNode) {
        if (selectedItemBOMTreeNode == null) {
            // Tree will set to null on every form update. 
            return;
        }

        // Clear selection in case same type of component item is in parts list.
        selectedObject = null;

        // Clear Filters
        settingObject.clearListFilters();
        filteredObjectList = null;

        this.selectedItemBOMTreeNode = selectedItemBOMTreeNode;

    }       

    public boolean isRenderBomOptionalUnspecified(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue());
        }
        return false;
    }

    public boolean isRenderBomPlaceholder(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.placeholder.getValue());
        }
        return false;
    }

    public boolean isRenderBomExisting(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.existingItem.getValue());
        }
        return false;
    }

    public boolean isRenderBomEdit(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.newItem.getValue());
        }
        return false;
    }

    public boolean isRenderItemBom(ItemDomainInventory item) {
        return item.getInventoryDomainBillOfMaterialList() != null
                && item.getInventoryDomainBillOfMaterialList().isEmpty() == false;
    }

    /**
     * Show full bill of materials based on top level item being added.
     *
     * @return
     */
    public boolean isRenderCurrentItemFullBOM() {
        if (getCurrent() == null) {
            return false;
        }
        return isRenderItemBom(getCurrent());
    }

    public void createOptionalBillOfMaterialsPart(InventoryBillOfMaterialItem bomItem) {
        bomItem.setState(InventoryBillOfMaterialItemStates.placeholder.getValue());
        currentItemBOMTreeHasOptionalItems = null;
    }

    public void removeOptionalBillOfMaterialsPart(InventoryBillOfMaterialItem bomItem) {
        // Removal could only happen for optional items 
        if (bomItem.isOptional()) {
            bomItem.setState(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue());
            if (bomItem.getInventoryItemElement() != null) {
                ItemElement inventoryItemElement = bomItem.getInventoryItemElement();
                current.getFullItemElementList().remove(inventoryItemElement);
                ItemElementController.getInstance().destroy(inventoryItemElement);
            }
            clearSelectedOptionalElementsIfNeeded();
            currentItemBOMTreeHasOptionalItems = null;
        }
    }

    public void toggleShowOptionalItems() {
        showOptionalPartsInBom = !showOptionalPartsInBom;
        clearSelectedOptionalElementsIfNeeded();
    }

    public Boolean getCurrentItemBOMTreeHasOptionalItems() {
        // TODO add support for optional elements in sub assamblies 
        if (current != null && currentItemBOMTreeHasOptionalItems == null) {
            currentItemBOMTreeHasOptionalItems = itemHasOptionalsInBOM(current);
            if (!currentItemBOMTreeHasOptionalItems) {                                
                InventoryBillOfMaterialItem containedInBOM = current.getContainedInBOM();
                List<ItemDomainInventory> newItemsToAdd = containedInBOM.getNewItemsToAdd();
                for (ItemDomainInventory item : newItemsToAdd) {
                    currentItemBOMTreeHasOptionalItems = itemHasOptionalsInBOM(item);
                    if (currentItemBOMTreeHasOptionalItems) {
                        return true;
                    }
                }
            }

        }
        return currentItemBOMTreeHasOptionalItems;
    }

    private boolean itemHasOptionalsInBOM(ItemDomainInventory item) {
        List<InventoryBillOfMaterialItem> iBomiList = item.getInventoryDomainBillOfMaterialList();
        for (InventoryBillOfMaterialItem iBomi : iBomiList) {
            if (iBomi.isOptional()) {
                if (iBomi.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearSelectedOptionalElementsIfNeeded() {
        if (showOptionalPartsInBom == false && selectedItemBOMTreeNode != null) {
            InventoryBillOfMaterialItem selectedBOM = (InventoryBillOfMaterialItem) selectedItemBOMTreeNode.getData();
            if (selectedBOM.isOptional()
                    && selectedBOM.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                selectedItemBOMTreeNode.setSelected(false);
                selectedItemBOMTreeNode = null;
            }
        }
    }

    public boolean isShowOptionalPartsInBom() {
        return showOptionalPartsInBom;
    }

    public boolean isBOMInventoryItemElementInDB(InventoryBillOfMaterialItem bomItem) {
        ItemElement inventoryItemElement = bomItem.getInventoryItemElement();
        if (inventoryItemElement != null) {
            Integer itemElementId = inventoryItemElement.getId();
            return itemElementId != null;
        }
        return false;
    }

    public void changeBillOfMaterialsState(InventoryBillOfMaterialItem bomItem, String previousState) {
        // Update type of selected tree node. 
        selectedItemBOMTreeNode.setType(bomItem.getState());

        if (!previousState.equals(bomItem.getState())) {
            if (previousState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {                
                // The current item will not be defined. it has no children.                 
                selectedItemBOMTreeNode.getChildren().clear();

                bomItem.setInventoryItem(null);

                ItemDomainCatalog catalogItem = bomItem.getCatalogItem();
                if (catalogItem.getFullItemElementList().size() > 1) {
                    // Assembly may have optionals
                    currentItemBOMTreeHasOptionalItems = null;
                }               
            } else if (InventoryBillOfMaterialItemStates.newItem.getValue().equals(bomItem.getState())) {
                ItemElement catalogItemElement = bomItem.getCatalogItemElement();
                ItemDomainCatalog catalogItem = (ItemDomainCatalog) catalogItemElement.getContainedItem();

                ItemDomainInventory newInventoryItem = createEntityInstance();

                newInventoryItem.setDerivedFromItem(catalogItem);
                InventoryBillOfMaterialItem.setBillOfMaterialsListForItem(newInventoryItem, bomItem);

                bomItem.setInventoryItem(newInventoryItem);

                // The tree needs to be updated.
                addNewChildrenToCurrentSelection();

                if (catalogItem.getFullItemElementList().size() > 1) {
                    // Assembly may have optionals
                    currentItemBOMTreeHasOptionalItems = null;
                }
            }
        }
    }      
    
    public boolean isApplyPermissionToAllNewPartsForCurrent() {
        return getControllerUtility().isApplyPermissionToAllNewPartsForItem(getCurrent());
    }

    @Override
    public String getItemElementContainedItemText(ItemElement instanceItemElement) {
        if (instanceItemElement.getContainedItem() == null) {
            if (instanceItemElement.getDerivedFromItemElement().getContainedItem() != null) {
                return "No instance of " + instanceItemElement.getDerivedFromItemElement().getContainedItem().getName() + " defined";
            } else {
                return "Catalog item: " + instanceItemElement.getDerivedFromItemElement().getParentItem().getName() + " has no defined item.";
            }
        }

        ItemDomainInventory containedItem = (ItemDomainInventory) instanceItemElement.getContainedItem();

        return getItemDisplayString(containedItem);
    }

    public boolean isCurrentHasPartsToDisplay() {
        if (getCurrent().getInventoryDomainBillOfMaterialList() != null) {
            return getCurrent().getInventoryDomainBillOfMaterialList().isEmpty() == false;
        }
        return false;
    }     

    public String getInventoryItemElementDisplayString(ItemElement itemElement) {
        if (itemElement != null) {
            if (itemElement.getContainedItem() != null) {
                ItemDomainInventory inventoryItem = (ItemDomainInventory) itemElement.getContainedItem();
                return getItemDisplayString(inventoryItem);
            }

            ItemDomainCatalog catalogItem = getCatalogItemForInventoryItemElement(itemElement);
            if (catalogItem != null) {
                return catalogItem.getName() + "- [ ]";
            } else {
                return "Undefined Part: " + itemElement.getDerivedFromItemElement().getName();
            }
        }
        return null;
    }

    public ItemDomainCatalog getCatalogItemForInventoryItemElement(ItemElement inventoryItemElement) {
        if (inventoryItemElement != null) {
            ItemElement derivedFromItemElement = inventoryItemElement.getDerivedFromItemElement();
            if (derivedFromItemElement.getContainedItem() != null) {
                return (ItemDomainCatalog) derivedFromItemElement.getContainedItem();
            }
        }
        return null;
    }   

    @Override
    public boolean isShowCloneCreateItemElementsPlaceholdersOption() {
        // Item elements should match the assembly. User has no control over that.
        return false;
    }

    @Override
    public String prepareCloneForItemToClone() {
        // Item elements should match the assembly. User has no control over that.
        cloneCreateItemElementPlaceholders = true;        

        return super.prepareCloneForItemToClone();
    }

    @Override
    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new InventoryItemElementConstraintInformation(itemElement);
    }

    @Override
    public String getPrimaryImageValueForItem(Item item) {
        String result = super.getPrimaryImageValueForItem(item);
        if (result.equals("")) {
            Item catalogItem = item.getDerivedFromItem();
            if (catalogItem != null) {
                return super.getPrimaryImageValueForItem(catalogItem);
            }
        }
        return result;
    }

    public boolean getIsCurrentItemHaveConnectors() {
        Item item = getCurrent();
        if (item != null) {
            List<ItemConnector> itemConnectorList = item.getItemConnectorList();
            if (itemConnectorList != null && !itemConnectorList.isEmpty()) {
                return true;
            }

            Item catalogItem = item.getDerivedFromItem();
            itemConnectorList = catalogItem.getItemConnectorList();

            if (itemConnectorList != null && !itemConnectorList.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        formatInfo.add(new ImportExportFormatInfo("Basic Inventory Format", ImportHelperInventory.class));
        
        String completionUrl = "/views/itemDomainInventory/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
    @Override
    public String getDefaultDomainName() {
        return DEFAULT_DOMAIN_NAME;
    }   

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    @Override
    protected ItemDomainInventoryControllerUtility createControllerUtilityInstance() {
        return new ItemDomainInventoryControllerUtility();
    }
}
