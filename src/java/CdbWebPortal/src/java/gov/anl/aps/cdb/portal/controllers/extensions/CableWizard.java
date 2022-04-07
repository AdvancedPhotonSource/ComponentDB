/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignWizardBase;
import static gov.anl.aps.cdb.portal.model.db.entities.CdbEntity.VALUE_CABLE_END_1;
import static gov.anl.aps.cdb.portal.model.db.entities.CdbEntity.VALUE_CABLE_END_2;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.List;
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
    
    private ItemDomainCableDesign cableItem = null;
    
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
    
    public List<ItemConnector> getUnmappedConnectorsEnd1() {
        return ItemDomainCableDesignController.getInstance().getUnmappedConnectorsForCableItem(
                cableItem, VALUE_CABLE_END_1);
    }

    public List<ItemConnector> getUnmappedConnectorsEnd2() {
        return ItemDomainCableDesignController.getInstance().getUnmappedConnectorsForCableItem(
                cableItem, VALUE_CABLE_END_2);
    }
    /**
     * Resets models for wizard components.
     */
    @Override
    protected void reset_() {
        selectionCableType = null;
        selectionCableCatalogItem = null;
        cableItem = null;
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

    protected void handleTabStep_(String currStep, String nextStep) {
        
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
        } else if (tab.endsWith("CableConnectorTab")) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonSave = true;
            disableButtonNext = false;
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
