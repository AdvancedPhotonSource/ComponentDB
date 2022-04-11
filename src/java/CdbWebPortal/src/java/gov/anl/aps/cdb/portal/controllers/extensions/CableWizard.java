/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import static gov.anl.aps.cdb.portal.model.db.entities.CdbEntity.VALUE_CABLE_END_1;
import static gov.anl.aps.cdb.portal.model.db.entities.CdbEntity.VALUE_CABLE_END_2;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;

/**
 * This class is intended to be used by another controller class, e.g.,
 * ItemDomainMachineDesignController, and is utilized when adding a new cable
 * design item.
 *
 * @author cmcchesney
 */
@Named(CableWizard.CONTROLLER_NAMED)
@SessionScoped
public class CableWizard implements Serializable {

    private static final String defaultRedirectSuccess = "/views/itemDomainCableDesign/view?faces-redirect=true";
    public static final String cableTypeUnspecified = "unspecified";
    public static final String cableTypeCatalog = "catalog";
    private static final String tabEndpoint = "EndpointTab";
    private static final String tabBasics = "CableBasicsTab";
    private static final String tabType = "CableTypeTab";
    private static final String tabDetails = "CableDetailsTab";
    private static final String tabReview = "CableReviewTab";

    public static final String CONTROLLER_NAMED = "cableWizard";
    
    protected CableWizardClient client;
    protected ItemDomainMachineDesignTreeNode machineDesignTreeEndpoint1 = null;
    protected ItemDomainMachineDesignTreeNode machineDesignTreeEndpoint2 = null;
    protected String inputValueName = "";
    protected List<ItemProject> selectionProjectList = new ArrayList<>();
    protected List<ItemCategory> selectionTechnicalSystemList = new ArrayList<>();
    protected TreeNode selectionEndpoint1 = null;
    protected Item itemEnd1 = null;
    protected TreeNode selectionEndpoint2 = null;
    protected Item itemEnd2 = null;
    protected List<Item> members = new ArrayList<>();
    protected Boolean disableButtonNext = true;
    protected Boolean disableButtonSave = true;
    protected Boolean disableButtonCancel = false;
    protected String currentTab = tabEndpoint;
    protected String redirectSuccess = "";
    protected List<ItemCategory> availableTechnicalSystems = null;
    private ItemDomainCableDesign cableItem = null;
    
    private String selectionCableType = null;
    private Item selectionCableCatalogItem = null;
    
    protected ItemConnector portEnd1 = null;
    protected ItemConnector connectorEnd1 = null;
    protected ItemConnector portEnd2 = null;
    protected ItemConnector connectorEnd2 = null;

    private String selectedConnectorNameEnd1 = null;
    private String selectedConnectorNameEnd2 = null;
    
    private Map<String, ItemConnector> connectorMap = new HashMap<>();
    
    public static CableWizard getInstance() {
        return (CableWizard) SessionUtility.findBean(CableWizard.CONTROLLER_NAMED);
    } 
    
    public void registerClient(CableWizardClient client, String redirectSuccess) {
        this.client = client;
        this.redirectSuccess = redirectSuccess;
        reset();
    }

    /**
     * Returns the root node of the machine design tree that is used to populate
     * the wizard's endpoint selection tab.
     */
    public ItemDomainMachineDesignTreeNode getMachineDesignTreeEndpoint1() {
        if (machineDesignTreeEndpoint1 == null) {
            ItemDomainMachineDesignController controller = ItemDomainMachineDesignController.getInstance();
            machineDesignTreeEndpoint1 = controller.loadMachineDesignRootTreeNode();
            machineDesignTreeEndpoint1.getConfig().setShowConnectorsOnly(true);
        }
        return machineDesignTreeEndpoint1;
    }

    /**
     * @link ItemDomainCableDesignWizard#getMachineDesignTreeEndpoint1
     */
    public void setMachineDesignTreeEndpoint1(ItemDomainMachineDesignTreeNode machineDesignTree) {
        this.machineDesignTreeEndpoint1 = machineDesignTree;
    }

    /**
     * Returns the root node of the machine design tree that is used to populate
     * the wizard's endpoint selection tab.
     */
    public ItemDomainMachineDesignTreeNode getMachineDesignTreeEndpoint2() {
        if (machineDesignTreeEndpoint2 == null) {
            ItemDomainMachineDesignController controller = ItemDomainMachineDesignController.getInstance();
            machineDesignTreeEndpoint2 = controller.loadMachineDesignRootTreeNode();
            machineDesignTreeEndpoint2.getConfig().setShowConnectorsOnly(true);
        }
        return machineDesignTreeEndpoint2;
    }

    /**
     * @link ItemDomainCableDesignWizard#getMachineDesignTreeEndpoint2
     */
    public void setMachineDesignTreeEndpoint2(ItemDomainMachineDesignTreeNode machineDesignTree) {
        this.machineDesignTreeEndpoint2 = machineDesignTree;
    }

    /**
     * Returns the cable name. This is the model for the name input on the
     * "basics" tab.
     */
    public String getInputValueName() {
        return inputValueName;
    }

    /**
     * @link ItemDomainCableDesignWizard#getName
     */
    public void setInputValueName(String inputValueName) {
        this.inputValueName = inputValueName;
    }

    /**
     * Returns the selected list of projects for the cable.
     */
    public List<ItemProject> getSelectionProjectList() {
        return selectionProjectList;
    }

    /**
     * @link ItemDomainCableDesignWizard#getSelectionProjectList
     */
    public void setSelectionProjectList(List<ItemProject> projectList) {
        this.selectionProjectList = projectList;
    }

    /**
     * Returns project list as String for display.
     */
    public String getProjectsString() {
        return StringUtility.getStringifyCdbList(selectionProjectList);
    }

    public List<ItemCategory> getSelectionTechnicalSystemList() {
        return selectionTechnicalSystemList;
    }

    public void setSelectionTechnicalSystemList(List<ItemCategory> selectionTechnicalSystemList) {
        this.selectionTechnicalSystemList = selectionTechnicalSystemList;
    }

    /**
     * Returns technical system list as String for display.
     */
    public String getTechnicalSystemsString() {
        return StringUtility.getStringifyCdbList(selectionTechnicalSystemList);
    }

    public List<ItemCategory> getAvailableTechnicalSystems() {
        if (availableTechnicalSystems == null) {
            availableTechnicalSystems = ItemCategoryController.getInstance().getItemTypeCategoryEntityListByDomainName(getDomain().getName());
        }
        return availableTechnicalSystems;
    }

    /**
     * Returns the cable's first endpoint, intended to be set by the client
     * after creating an instance of this class.
     */
    public TreeNode getSelectionEndpoint1() {
        return selectionEndpoint1;
    }

    /**
     * @link ItemDomainCableDesignWizard#getEndpoint1
     */
    public void setSelectionEndpoint1(TreeNode selectionEndpoint1) {
        this.selectionEndpoint1 = selectionEndpoint1;
    }
    
    public void setEndpoint1(Item itemEnd1) {
        this.itemEnd1 = itemEnd1;
    }
    
    public Item getEndpoint1() {
        return itemEnd1;
    }

    /**
     * Returns selectionEndpoint1 as a string.
     * @link ItemDomainCableDesignWizard#getSelectionEndpoint1
     */
    public String getEndpoint1String() {
        if (itemEnd1 == null) {
            return new String();
        } else {
            return itemEnd1.toString();
        }
    }
    
    /**
     * Returns the cable's second endpoint. This is set by the wizard's endpoint
     * selection tab, and is the selection model for the machine design tree
     * table.
     */
    public TreeNode getSelectionEndpoint2() {
        return selectionEndpoint2;
    }

    public Item getEndpoint2() {
        return itemEnd2;
    }

    /**
     * Returns endpoint2 as a string.
     * @link ItemDomainCableDesignWizard#getSelectionEndpoint2
     */
    public String getEndpoint2String() {
        if (itemEnd2 == null) {
            return new String();
        } else {
            return itemEnd2.toString();
        }
    }

    /**
     * @link ItemDomainCableDesignWizard#getSelectedEndpoint
     */
    public void setSelectionEndpoint2(TreeNode selectionEndpoint2) {
        this.selectionEndpoint2 = selectionEndpoint2;
    }

    /**
     * Returns value model for list of member cables.
     */
    public List<Item> getMembers() {
        return members;
    }

    /**
     * Returns number of member cables.
     */
    public int getMemberCount() {
        return members.size();
    }

    protected void addMembers(List<Item> newMembers) {

        for (Item item : newMembers) {
            if (!members.contains(item)) {
                members.add(item);
            }
        }
        setEnablementForCurrentTab();
    }

    public void removeMember(Item member) {
        if (members.contains(member)) {
            members.remove(member);
            setEnablementForCurrentTab();
        }
    }

    /**
     * Returns true if the next button should be disabled.
     */
    public Boolean getDisableButtonNext() {
        return disableButtonNext;
    }

    /**
     * @link ItemDomainCableDesignWizard#getDisableButtonNext
     */
    public void setDisableButtonNext(Boolean disableButtonNext) {
        this.disableButtonNext = disableButtonNext;
    }

    /**
     * Returns true if the save button should be disabled.
     */
    public Boolean getDisableButtonSave() {
        return disableButtonSave;
    }

    /**
     * @link ItemDomainCableDesignWizard#getDisableButtonSave
     */
    public void setDisableButtonSave(Boolean disableButtonSave) {
        this.disableButtonSave = disableButtonSave;
    }

    /**
     * Returns true if the cancel button should be disabled.
     */
    public Boolean getDisableButtonCancel() {
        return disableButtonCancel;
    }

    /**
     * @link ItemDomainCableDesignWizard#getDisableButtonCancel
     */
    public void setDisableButtonCancel(Boolean disableButtonCancel) {
        this.disableButtonCancel = disableButtonCancel;
    }

    protected String getRedirectSuccess() {
        return redirectSuccess;
    }

    protected void setRedirectSuccess(String redirectSuccess) {
        this.redirectSuccess = redirectSuccess;
    }

    /**
     * Handles select events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    /**
     * Handles select event on the p:selectCheckboxMenu for selecting project.
     */
    public void selectListenerProjectList() {
        setEnablementForCurrentTab();
    }

    /**
     * Handles unselect event on the p:selectCheckboxMenu for selecting project.
     */
    public void unselectListenerProjectList() {
        setEnablementForCurrentTab();
    }

    /**
     * Handles select event on the p:selectCheckboxMenu for selecting project.
     */
    public void selectListenerTechnicalSystemList() {
        setEnablementForCurrentTab();
    }

    /**
     * Handles keyup events for the inputValueName inputText component.
     */
    public void keyupListenerName() {
        setEnablementForCurrentTab();
    }

    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    public void setEnablementForCurrentTab() {
        setEnablement(currentTab);
    }
    
    protected void handleTabStep(String currStep, String nextStep) {
        ItemDomainCableDesignController controller = ItemDomainCableDesignController.getInstance();

        if (currStep.endsWith("CableBasicsTab") && nextStep.endsWith("CableTypeTab")) {
            cableItem = controller.createCableCommon(
                    inputValueName,
                    selectionProjectList,
                    selectionTechnicalSystemList);
        } else if (currStep.endsWith("CableDetailsTab") && nextStep.endsWith("CableConnectorTab")) {
            cableItem.setCatalogItem(selectionCableCatalogItem);
        }
    }

    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    protected void setEnablement(String tab) {

        // default
        disableButtonCancel = false;
        disableButtonSave = true;
        disableButtonNext = true;

        if (tab.endsWith("EndpointTab")) {
            disableButtonCancel = false;
            disableButtonSave = true;
            if ((itemEnd1 == null) || (itemEnd2 == null)) {
                disableButtonNext = true;
            } else {
                disableButtonNext = false;
            }
        } else if (tab.endsWith("CableBasicsTab")) {
            disableButtonCancel = false;
            disableButtonSave = true;
            if ((inputValueName.isEmpty()) || (selectionProjectList.isEmpty()) || (selectionTechnicalSystemList.isEmpty())) {
                disableButtonNext = true;
            } else {
                disableButtonNext = false;
            }
        } else if (tab.endsWith("CableTypeTab")) {
            disableButtonCancel = false;
            disableButtonSave = true;
            if (selectionCableType == null) {
                disableButtonNext = true;
            } else {
                disableButtonNext = false;
            }
        } else if (tab.endsWith("CableDetailsTab")) {
            disableButtonCancel = false;
            disableButtonSave = true;
            switch (selectionCableType) {
                case cableTypeUnspecified:
                    disableButtonNext = false;
                    break;
                case cableTypeCatalog:
                    if (selectionCableCatalogItem != null) {
                        disableButtonNext = false;
                    } else {
                        disableButtonNext = true;
                    }
                    break;
                default:
                    disableButtonNext = true;
            }
        } else if (tab.endsWith("CableConnectorTab")) {
            disableButtonCancel = false;
            disableButtonSave = true;
            disableButtonNext = false;
        } else if (tab.endsWith("CableReviewTab")) {
            disableButtonCancel = false;
            disableButtonSave = false;
            disableButtonNext = true;
        }
    }

    /**
     * Returns custom tab navigation based on wizard state.  Subclass should
     * override for custom behavior.
     */
    public String nextTab(String currStep, String nextStep) {
        
        String nextTab = nextStep;
        
        // skip details tab for unspecified cable type
        if ((nextStep.endsWith(tabDetails)) && (isTypeUnspecified())) {
            if (currStep.endsWith(tabType)) {
                nextTab = CONTROLLER_NAMED+tabReview;
            } else if (currStep.endsWith(tabReview)) {
                nextTab = CONTROLLER_NAMED+tabType;
            }
        }
        
        return nextTab;
    }

    /**
     * Handles FlowEvents generated by the wizard component. Determines next tab
     * based on current tab, defaults to visiting all tabs but implements
     * special cases. Skips cableDetailsTab when the type is "unspecified" since
     * that tab is empty.
     */
    public String onFlowProcess(FlowEvent event) {

        String nextStep = event.getNewStep();
        String currStep = event.getOldStep();

        nextStep = nextTab(currStep, nextStep);
        
        if (!nextStep.equals(currStep)) {
            handleTabStep(currStep, nextStep);
        }

        setEnablement(nextStep);

        currentTab = nextStep;

        return nextStep;
    }

    /**
     * Resets models for wizard components.
     */
    public void reset() {
        currentTab = "EndpointTab";
        machineDesignTreeEndpoint1 = null;
        machineDesignTreeEndpoint2 = null;
        inputValueName = "";
        selectionEndpoint1 = null;
        selectionEndpoint2 = null;
        itemEnd1 = null;
        itemEnd2 = null;
        selectionProjectList = null;
        selectionTechnicalSystemList = null;
        members.clear();
        selectionCableType = null;
        selectionCableCatalogItem = null;
        cableItem = null;
        portEnd1 = null;
        connectorEnd1 = null;
        portEnd2 = null;
        connectorEnd2 = null;
        selectedConnectorNameEnd1 = null;
        selectedConnectorNameEnd2 = null;            
        connectorMap.clear();
    }

    protected void cleanupClient() {
        if (client != null) {
            client.cleanupCableWizard();
        }
        client = null;
        redirectSuccess = "";
    }

    /**
     * Implements the cancel operation, invoked by the wizard's "Cancel"
     * navigation button.
     */
    public String cancel() {
        cleanupClient();
        this.reset();
        return "list";
    }

    protected Domain getDomain() {
        return ItemDomainCableDesignController.getInstance().getDefaultDomain();
    }

    /**
     * Returns whether to specify cable type (e.g., unspecified or catalog). 
     * This is the model for radio buttons on the wizard's cable type tab.
     */
    public String getSelectionCableType() {
        return selectionCableType;
    }

    /**
     * @link ItemDomainCableDesignWizard#getCableType
     */
    public void setSelectionCableType(String selectionCableType) {
        this.selectionCableType = selectionCableType;
    }
    
    /**
     * Returns true if the cable type is unspecified.
     */
    public Boolean isTypeUnspecified() {
        if (selectionCableType == null) {
            return false;
        } else {
            return selectionCableType.equals(cableTypeUnspecified);
        }
    }

    /**
     * Returns true if the cable type is catalog.
     */
    public Boolean isTypeCatalog() {
        if (selectionCableType == null) {
            return false;
        } else {
            return selectionCableType.equals(cableTypeCatalog);
        }
    }

    /**
     * Returns the selection model for the cable catalog data table.
     */
    public Item getSelectionCableCatalogItem() {
        return selectionCableCatalogItem;
    }

    /**
     * @link ItemDomainCableDesignWizard#getSelectedCableCatalogItem
     */
    public void setSelectionCableCatalogItem(Item selectionCableCatalogItem) {
        this.selectionCableCatalogItem = selectionCableCatalogItem;
    }


    /**
     * Handles click events for the selectionCableType selectOneRadio component.
     */
    public void clickListenerCableType() {
        setEnablementForCurrentTab();
    }

    /**
     * Handles selection events for the cable catalog item datatable component.
     */
    public void selectListenerCableCatalogItem() {
        setEnablementForCurrentTab();
    }
    
    public void setEnd1Port(ItemConnector port) {
        this.portEnd1 = port;
    }
    
    public String getEnd1PortString() {
        if (portEnd1 == null) {
            return "";
        } else {
            return portEnd1.getConnector().getName();
        }
    }
    
    public ItemConnector getEnd1Connector() {
        return connectorEnd1;
    }
    
    public void setEnd1Connector(ItemConnector connector) {
        connectorEnd1 = connector;
    }
    
    public String getEnd1ConnectorString() {
        if (connectorEnd1 == null) {
            return "";
        } else {
            return connectorEnd1.getConnector().getName();
        }
    }

    public String getEnd2PortString() {
        if (portEnd2 == null) {
            return "";
        } else {
            return portEnd2.getConnector().getName();
        }
    }
    
    public ItemConnector getEnd2Connector() {
        return connectorEnd2;
    }
    
    public void setEnd2Connector(ItemConnector connector) {
        connectorEnd2 = connector;
    }
    
    public String getEnd2ConnectorString() {
        if (connectorEnd2 == null) {
            return "";
        } else {
            return connectorEnd2.getConnector().getName();
        }
    }

    public void selectListenerEndpoint1(NodeSelectEvent event) {
        if (selectionEndpoint1 != null) {
            ItemElement ieEnd1 = (ItemElement) selectionEndpoint1.getData();
            Item containedItemEnd1 = ieEnd1.getContainedItem();
            if (containedItemEnd1 instanceof ItemDomainMachineDesign) {
                itemEnd1 = containedItemEnd1;
            } else {
                portEnd1 = ieEnd1.getMdConnector();
                if (portEnd1 != null) {
                    if (portEnd1.isConnected()) {
                        SessionUtility.addErrorMessage(
                                "Invalid Connection",
                                "Selected port is already in use.");
                        itemEnd1 = null;
                        portEnd1 = null;
                    } else {
                        itemEnd1 = portEnd1.getItem();
                    }
                }
            }
        } else {
            itemEnd1 = null;
            portEnd1 = null;
        }
        setEnablementForCurrentTab();
    }

    /**
     * Handles unselect events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void unselectListenerEndpoint1(NodeUnselectEvent event) {
        itemEnd1 = null;
        portEnd1 = null;
        setEnablementForCurrentTab();
    }
    
    /**
     * Handles select events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void selectListenerEndpoint2(NodeSelectEvent event) {
        if (selectionEndpoint2 != null) {
            ItemElement ieEnd2 = (ItemElement) selectionEndpoint2.getData();
            Item containedItemEnd2 = ieEnd2.getContainedItem();
            if (containedItemEnd2 instanceof ItemDomainMachineDesign) {
                itemEnd2 = containedItemEnd2;
            } else {
                portEnd2 = ieEnd2.getMdConnector();
                if (portEnd2 != null) {
                    if (portEnd2.isConnected()) {
                        SessionUtility.addErrorMessage(
                                "Invalid Connection",
                                "Selected port is already in use.");
                        itemEnd2 = null;
                        portEnd2 = null;
                    } else {
                        itemEnd2 = portEnd2.getItem();
                    }
                }
            }
        } else {
            itemEnd2 = null;
            portEnd2 = null;
        }
        setEnablementForCurrentTab();
    }

    /**
     * Handles unselect events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void unselectListenerEndpoint2(NodeUnselectEvent event) {
        itemEnd2 = null;
        portEnd2 = null;
        setEnablementForCurrentTab();
    }

    private List<String> addCableConnectors(List<ItemConnector> connectors) {
        List<String> connectorNames = new ArrayList<>();
        for (ItemConnector connector : connectors) {
            String connectorName = connector.getConnector().getName();
            connectorMap.put(connectorName, connector);
            connectorNames.add(connectorName);
        }
        return connectorNames;
    }
    
    public List<String> getUnmappedConnectorsEnd1() {
        List<ItemConnector> connectors = ItemDomainCableDesignController.getInstance().getUnmappedConnectorsForCableItem(
                cableItem, VALUE_CABLE_END_1);
        return addCableConnectors(connectors);
    }

    public List<String> getUnmappedConnectorsEnd2() {
        List<ItemConnector> connectors = ItemDomainCableDesignController.getInstance().getUnmappedConnectorsForCableItem(
                cableItem, VALUE_CABLE_END_2);
        return addCableConnectors(connectors);
    }

    public String getSelectedConnectorNameEnd1() {
        return selectedConnectorNameEnd1;
    }

    public void setSelectedConnectorNameEnd1(String selectedConnectorNameEnd1) {
        this.selectedConnectorNameEnd1 = selectedConnectorNameEnd1;
    }

    public String getSelectedConnectorNameEnd2() {
        return selectedConnectorNameEnd2;
    }

    public void setSelectedConnectorNameEnd2(String selectedConnectorNameEnd2) {
        this.selectedConnectorNameEnd2 = selectedConnectorNameEnd2;
    }
    
    public void selectListenerConnectorEnd1() {
        String connectorName = getSelectedConnectorNameEnd1();
        if (connectorName != null) {
            connectorEnd1 = connectorMap.get(connectorName);
        } else {
            connectorEnd1 = null;
        }
    }
    
    public void selectListenerConnectorEnd2() {
        String connectorName = getSelectedConnectorNameEnd2();
        if (connectorName != null) {
            connectorEnd2 = connectorMap.get(connectorName);
        } else {
            connectorEnd2 = null;
        }
    }
    
    /**
     * Implements the save operation, invoked by the wizard's "Save" navigation
     * button.
     */
    public String save() {

        if (itemEnd1 == null) {
            SessionUtility.addErrorMessage(
                    "Could not save cable",
                    "Please specify first endpoint.");
            return "";
        }

        if (itemEnd2 == null) {
            SessionUtility.addErrorMessage(
                    "Could not save cable",
                    "Please specify second endpoint.");
            return "";
        }

        ItemDomainCableDesignController controller = ItemDomainCableDesignController.getInstance();
        cableItem.setPrimaryEndpoint(itemEnd1, portEnd1, connectorEnd1, VALUE_CABLE_END_1);
        cableItem.setPrimaryEndpoint(itemEnd2, portEnd2, connectorEnd2, VALUE_CABLE_END_2);

        String result = "";
        result = controller.create();

        // get redirect before calling cleanup or it will be reset
        String redirect = "";
        if (getRedirectSuccess().isEmpty()) {
            redirect = result;
        } else {
            redirect = getRedirectSuccess();
        }
        cleanupClient();
        this.reset();
        return redirect;
    }
}
