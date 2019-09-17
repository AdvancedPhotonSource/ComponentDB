/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.RelationshipTypeController;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
@Named(ItemDomainCableDesignWizard.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableDesignWizard implements Serializable {

    public static final String CONTROLLER_NAMED = "cableWizard";
    
    private ItemDomainCableDesignWizardClient client;
    private TreeNode machineDesignTreeEndpoint1 = null;
    private TreeNode machineDesignTreeEndpoint2 = null;
    private String inputValueName = "";
    private List<ItemProject> selectionProjectList = new ArrayList<>();
    private TreeNode selectionEndpoint1 = null;
    private TreeNode selectionEndpoint2 = null;
    private String selectionCableType = null;
    private Item selectionCableCatalogItem = null;
    private Boolean disableButtonPrev = true;
    private Boolean disableButtonNext = true;
    private Boolean disableButtonSave = true;
    private Boolean disableButtonCancel = false;
    private String currentTab = "endpointTab";

    public static final String cableTypePlaceholder = "placeholder";
    public static final String cableTypeCatalog = "catalog";

    /**
     * Creates new instance with a "dummy" client implementation of the
     * ItemDomainCableDesignWizardClient using no ops.
     */
    public ItemDomainCableDesignWizard() {
    }

    public static ItemDomainCableDesignWizard getInstance() {
        return (ItemDomainCableDesignWizard) SessionUtility.findBean(ItemDomainCableDesignWizard.CONTROLLER_NAMED);
    } 

    public void registerClient(ItemDomainCableDesignWizardClient client) {
        this.client = client;
    }
    
    public void unregisterClient(ItemDomainCableDesignWizardClient client) {
        this.client = null;
    }
    
    /**
     * Returns the root node of the machine design tree that is used to populate
     * the wizard's endpoint selection tab.
     */
    public TreeNode getMachineDesignTreeEndpoint1() {
        if (machineDesignTreeEndpoint1 == null) {
            ItemDomainMachineDesignController controller = ItemDomainMachineDesignController.getInstance();
            machineDesignTreeEndpoint1 = controller.loadMachineDesignRootTreeNode(false);
        }
        return machineDesignTreeEndpoint1;
    }

    /**
     * @link ItemDomainCableDesignWizard#getMachineDesignTreeEndpoint1
     */
    public void setMachineDesignTreeEndpoint1(TreeNode machineDesignTree) {
        this.machineDesignTreeEndpoint1 = machineDesignTree;
    }

    /**
     * Returns the root node of the machine design tree that is used to populate
     * the wizard's endpoint selection tab.
     */
    public TreeNode getMachineDesignTreeEndpoint2() {
        if (machineDesignTreeEndpoint2 == null) {
            ItemDomainMachineDesignController controller = ItemDomainMachineDesignController.getInstance();
            machineDesignTreeEndpoint2 = controller.loadMachineDesignRootTreeNode(false);
        }
        return machineDesignTreeEndpoint2;
    }

    /**
     * @link ItemDomainCableDesignWizard#getMachineDesignTreeEndpoint2
     */
    public void setMachineDesignTreeEndpoint2(TreeNode machineDesignTree) {
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

    /**
     * Returns selectionEndpoint1 as a string.
     * @link ItemDomainCableDesignWizard#getSelectionEndpoint1
     */
    public String getEndpoint1String() {
        if (selectionEndpoint1 == null) {
            return new String();
        } else {
            return ((ItemElement) (selectionEndpoint1.getData())).getContainedItem().toString();
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

    /**
     * Returns endpoint2 as a string.
     * @link ItemDomainCableDesignWizard#getSelectionEndpoint2
     */
    public String getEndpoint2String() {
        if (selectionEndpoint2 == null) {
            return new String();
        } else {
            return ((ItemElement) (selectionEndpoint2.getData())).getContainedItem().toString();
        }
    }

    /**
     * @link ItemDomainCableDesignWizard#getSelectedEndpoint
     */
    public void setSelectionEndpoint2(TreeNode selectionEndpoint2) {
        this.selectionEndpoint2 = selectionEndpoint2;
    }

    /**
     * Returns the logical cable type (e.g., placeholder, catalog, bundle,
     * virtual), not to be confused with the cable catalog item for the cable
     * design object that will be created by this wizard (in that case, the
     * cable type is "catalog"). This is the model for radio buttons on the
     * wizard's cable type tab.
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
     * Returns true if the previous button should be disabled.
     */
    public Boolean getDisableButtonPrev() {
        return disableButtonPrev;
    }

    /**
     * @link ItemDomainCableDesignWizard#getDisableButtonPrev
     */
    public void setDisableButtonPrev(Boolean disableButtonPrev) {
        this.disableButtonPrev = disableButtonPrev;
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

    /**
     * Handles FlowEvents generated by the wizard component.
     */
    public String onFlowProcess(FlowEvent event) {

        String nextStep = event.getNewStep();
        String currStep = event.getOldStep();

        setEnablement(nextStep);

        currentTab = nextStep;

        return nextStep;
    }

    /**
     * Handles select events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void selectListenerEndpoint1(NodeSelectEvent event) {
        setEnablementForCurrentTab();
    }

    /**
     * Handles select events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void selectListenerEndpoint2(NodeSelectEvent event) {
        setEnablementForCurrentTab();
    }

    /**
     * Handles unselect events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void unselectListenerEndpoint2(NodeUnselectEvent event) {
        setEnablementForCurrentTab();
    }

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
     * Handles keyup events for the inputValueName inputText component.
     */
    public void keyupListenerName() {
        setEnablementForCurrentTab();
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
    
    /**
     * Resets models for wizard components.
     */
    private void reset() {
        currentTab = "endpointTab";
        machineDesignTreeEndpoint1 = null;
        machineDesignTreeEndpoint2 = null;
        inputValueName = "";
        selectionEndpoint1 = null;
        selectionEndpoint2 = null;
        selectionCableType = null;
        selectionCableCatalogItem = null;
        selectionProjectList = null;
    }

    /**
     * Implements the cancel operation, invoked by the wizard's "Cancel"
     * navigation button.
     */
    public String cancel() {
        if (client != null) {
            client.cleanupCableWizard();
        }
        this.reset();
        return "list";
    }

    /**
     * Implements the save operation, invoked by the wizard's "Save" navigation
     * button.
     */
    public String save() {

        if (selectionEndpoint1 == null) {
            SessionUtility.addErrorMessage(
                    "Could not save cable",
                    "Please specify first endpoint.");
            return "";
        }

        if (selectionEndpoint2 == null) {
            SessionUtility.addErrorMessage(
                    "Could not save cable",
                    "Please specify second endpoint.");
            return "";
        }

        if (inputValueName.isEmpty()) {
            SessionUtility.addErrorMessage(
                    "Could not save cable",
                    "Please specify cable name.");
            return "";
        }

        if (selectionCableType == null) {
            SessionUtility.addErrorMessage(
                    "Could not save cable",
                    "Please specify second endpoint.");
            return "";
        }

        Item itemEndpoint1 = ((ItemElement) (selectionEndpoint1.getData())).getContainedItem();
        Item itemEndpoint2 = ((ItemElement) (selectionEndpoint2.getData())).getContainedItem();

        ItemDomainCableDesignController controller = ItemDomainCableDesignController.getInstance();

        boolean result = false;

        switch (selectionCableType) {

            case cableTypePlaceholder:
                if (result = controller.createCablePlaceholder(itemEndpoint1,
                        itemEndpoint2,
                        inputValueName,
                        selectionProjectList)) {
                } else {
                }
                break;

            case cableTypeCatalog:
                if (selectionCableCatalogItem == null) {
                    SessionUtility.addErrorMessage(
                            "Could not save cable",
                            "Please select cable catalog item.");
                    return "";
                } else {
                    
                    if (result = controller.createCableCatalog(itemEndpoint1,
                            itemEndpoint2,
                            inputValueName,
                            selectionProjectList,
                            selectionCableCatalogItem)) {
                    } else {
                    }
                }
                break;
        }

        if (result) {
            if (client != null) {
                client.cleanupCableWizard();
            }
            this.reset();
            return "/views/itemDomainCableDesign/view?faces-redirect=true"; 
        }
        else {
            return "";
        }
    }

    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    private void setEnablement(String tab) {

        switch (tab) {
            case "endpointTab":
                disableButtonPrev = true;
                disableButtonCancel = false;
                disableButtonSave = true;
                if (selectionEndpoint2 != null) {
                    disableButtonNext = false;
                } else {
                    disableButtonNext = true;
                }
                break;

            case "cableBasicsTab":
                disableButtonPrev = false;
                disableButtonCancel = false;
                disableButtonSave = true;
                if ((inputValueName.isEmpty())
                        || (selectionProjectList.isEmpty())) {
                    disableButtonNext = true;
                } else {
                    disableButtonNext = false;
                }
                break;

            case "cableTypeTab":
                disableButtonPrev = false;
                disableButtonCancel = false;
                disableButtonSave = true;
                if (selectionCableType == null) {
                    disableButtonNext = true;
                } else {
                    disableButtonNext = false;
                }
                break;

            case "cableDetailsTab":
                disableButtonPrev = false;
                disableButtonCancel = false;
                disableButtonSave = true;

                switch (selectionCableType) {
                    case cableTypePlaceholder:
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
                break;

            case "cableReviewTab":
                disableButtonPrev = false;
                disableButtonCancel = false;
                disableButtonSave = false;
                disableButtonNext = true;
                break;

            default:
                disableButtonPrev = true;
                disableButtonCancel = false;
                disableButtonSave = true;
                disableButtonNext = true;
        }
    }

    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    private void setEnablementForCurrentTab() {
        setEnablement(currentTab);
    }
}
