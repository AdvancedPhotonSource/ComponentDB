/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignWizardBase;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * This class is intended to be used by another controller class, e.g.,
 * ItemDomainMachineDesignController, and is utilized when adding a new cable
 * design item.
 *
 * @author cmcchesney
 */
@Named(CableWizard.CONTROLLER_NAMED)
@SessionScoped
public class CableWizard extends ItemDomainCableDesignWizardBase implements Serializable {

    public static final String CONTROLLER_NAMED = "cableWizard";
    
    private String selectionCableType = null;
    private Item selectionCableCatalogItem = null;
    
    public static final String cableTypeUnspecified = "unspecified";
    public static final String cableTypeCatalog = "catalog";
    
    private static final String tabType = "CableTypeTab";
    private static final String tabDetails = "CableDetailsTab";
    
    public static CableWizard getInstance() {
        return (CableWizard) SessionUtility.findBean(CableWizard.CONTROLLER_NAMED);
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
    
    /**
     * Resets models for wizard components.
     */
    @Override
    protected void reset_() {
        selectionCableType = null;
        selectionCableCatalogItem = null;
    }

    /**
     * Returns custom tab navigation based on wizard state.  Subclass should
     * override for custom behavior.
     */
    @Override
    public String nextTab_(String currStep, String nextStep) {
        
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
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    @Override
    protected void setEnablement_(String tab) {
        
        if (tab.endsWith("CableTypeTab")) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonSave = true;
            if (selectionCableType == null) {
                disableButtonNext = true;
            } else {
                disableButtonNext = false;
            }
        } else if (tab.endsWith("CableDetailsTab")) {
            disableButtonPrev = false;
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
        }
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

            case cableTypeUnspecified:
                if (result = controller.createCableUnspecified(itemEndpoint1,
                        itemEndpoint2,
                        inputValueName,
                        selectionProjectList,
                        selectionTechnicalSystemList)) {
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
                            selectionTechnicalSystemList,
                            selectionCableCatalogItem)) {
                    } else {
                    }
                }
                break;
        }

        if (result) {
            
            // get redirect before calling cleanup or it will be reset
            String redirect = "";
            if (getRedirectSuccess().isEmpty()) {
                redirect = defaultRedirectSuccess;
            } else {
                redirect = getRedirectSuccess();
            }
            
            cleanupClient();
            
            this.reset();
            
            return redirect;
        }
        else {
            return "";
        }
    }
}
