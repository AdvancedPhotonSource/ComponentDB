/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.io.Serializable;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;

/**
 * This class is intended to be used by another controller class, e.g.,
 * ItemDomainMachineDesignController, and is utilized when adding a new cable
 * design item.
 * 
 * I have not added annotations for the named bean or scope since it's not
 * envisioned to be used that way, and I'm not sure if they'd cause problems by
 * being present.
 * 
 * @author cmcchesney
 */
public class ItemDomainCableDesignWizard implements Serializable {

    private ItemDomainCableDesignWizardClient client;
    private TreeNode machineDesignTree = null;
    private String name = "";
    private TreeNode endpoint1 = null;
    private TreeNode selectionEndpoint2 = null;
    private String cableType = "";
    private Item selectionCableCatalogItem = null;
    private Boolean disableButtonPrev = true;
    private Boolean disableButtonNext = true;
    private Boolean disableButtonSave = true;
    private Boolean disableButtonCancel = false;
    private String currentTab = "";

    /**
     * Creates new instance with a "dummy" client implementation of the
     * ItemDomainCableDesignWizardClient using no ops.
     */
    public ItemDomainCableDesignWizard() {
        // "empty" lambda implementation of ItemDomainCableDesignWizardClient cleanupCableWizard() method
        client = (() -> {}); 
    }

    /**
     * Creates new instance with specified client.  Methods in
     * ItemDomainCableDesignWizardClient interface will be invoked on client.
     * 
     * @param aClient The client, which must implement ItemDomainCableDesignWizardClient
     * interface.
     */
     public ItemDomainCableDesignWizard(ItemDomainCableDesignWizardClient aClient) {
        client = aClient;
    }

    /**
     * Returns the root node of the machine design tree that is used to populate
     * the wizard's endpoint selection tab.
     */
    public TreeNode getMachineDesignTree() {
        return machineDesignTree;
    }

    /**
     * @link ItemDomainCableDesignWizard#getMachineDesignTree 
     */
    public void setMachineDesignTree(TreeNode machineDesignTree) {
        this.machineDesignTree = machineDesignTree;
    }

    /**
     * Returns the cable name.  This is the model for the name input on the
     * "basics" tab.
     */
    public String getName() {
        return name;
    }

    /**
     * @link ItemDomainCableDesignWizard#getName 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the cable's first endpoint, intended to be set by the client
     * after creating an instance of this class.
     */
    public TreeNode getEndpoint1() {
        return endpoint1;
    }

    /**
     * @link ItemDomainCableDesignWizard#getEndpoint1 
     */
    public void setEndpoint1(TreeNode endpoint1) {
        this.endpoint1 = endpoint1;
    }
    
    /**
     * Returns endpoint1 as a string. 
     * @link ItemDomainCableDesignWizard#getEndpoint1 
     */
    public String getEndpoint1String() {
        if (endpoint1 == null) {
            return new String();
        } else {
            return ((ItemElement)(endpoint1.getData())).getContainedItem().toString();
        }
    }

    /**
     * Returns the cable's second endpoint.  This is set by the wizard's
     * endpoint selection tab, and is the selection model for the machine design
     * tree table.
     */
    public TreeNode getSelectionEndpoint2() {
        return selectionEndpoint2;
    }
    
    public String getSelectedEndpointString() {
        if (selectionEndpoint2 == null) {
            return new String();
        } else {
            return ((ItemElement)(selectionEndpoint2.getData())).getContainedItem().toString();
        } 
    }

    /**
     * @link ItemDomainCableDesignWizard#getSelectedEndpoint 
     */
    public void setSelectionEndpoint2(TreeNode selectionEndpoint2) {
        this.selectionEndpoint2 = selectionEndpoint2;
    }

    /**
     * Returns the logical cable type (e.g., placeholder, catalog, bundle, virtual),
     * not to be confused with the cable catalog item for the cable design object
     * that will be created by this wizard (in that case, the cable type is
     * "catalog").  This is the model for radio buttons on the wizard's cable
     * type tab.
     */
    public String getCableType() {
        return cableType;
    }

    /**
     * @link ItemDomainCableDesignWizard#getCableType 
     */
    public void setCableType(String cableType) {
        this.cableType = cableType;
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
     * component.  Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void selectListenerEndpoint2(NodeSelectEvent event) {
        setEnablementForCurrentTab();
    }
    
    /**
     * Handles unselect events generated by the machine design tree table
     * component.  Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void unselectListenerEndpoint2(NodeUnselectEvent event) {
        setEnablementForCurrentTab();
    }
    
    /**
     * Handles keyup events for the name inputText component.
     */
    public void keyupListenerName()
    {
        setEnablementForCurrentTab();
    }
    
    /**
     * Handles click events for the cableType selectOneRadio component.
     */
    public void clickListenerCableType()
    {
        setEnablementForCurrentTab();
    }
    
     /**
     * Handles selection events for the cable catalog item datatable component.
     */
    public void selectListenerCableCatalogItem()
    {
        setEnablementForCurrentTab();
    }
    
   /**
     * Implements the cancel operation, invoked by the wizard's "Cancel"
     * navigation button.
     */
    public void cancel() {
        client.cleanupCableWizard();
    }
    
    /**
     * Implements the save operation, invoked by the wizard's "Save" navigation
     * button.
     */
    public void save() {
        client.cleanupCableWizard();
    }
    
    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    private void setEnablement(String tab)
    {

        switch(tab)
        {
            case "endpointTab":
                disableButtonPrev = true;
                disableButtonCancel = false;
                disableButtonSave = true;          
                if (selectionEndpoint2 != null) 
                    disableButtonNext = false;
                else
                    disableButtonNext = true;               
                break;
            
            case "cableBasicsTab":
                disableButtonPrev = false;
                disableButtonCancel = false;
                disableButtonSave = true;          
                if (name.isEmpty())
                    disableButtonNext = true;
                else
                    disableButtonNext = false;
                break;
            
            case "cableTypeTab":
                disableButtonPrev = false;
                disableButtonCancel = false;
                disableButtonSave = true;
                if (cableType.isEmpty())
                    disableButtonNext = true;
                else
                    disableButtonNext = false;
                break;
            
            case "cableDetailsTab":
                disableButtonPrev = false;
                disableButtonCancel = false;
                disableButtonSave = true;
                
                switch (cableType) {
                    case "placeholder":
                        disableButtonNext = false;
                        break;
                    case "catalog":
                        if (selectionCableCatalogItem != null)
                            disableButtonNext = false;
                        else
                            disableButtonNext = true;
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
    private void setEnablementForCurrentTab()
    {
        setEnablement(currentTab);
    }
}
