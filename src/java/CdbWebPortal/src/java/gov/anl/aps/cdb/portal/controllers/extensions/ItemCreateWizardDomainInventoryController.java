/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author djarosz
 */
@Named(ItemCreateWizardDomainInventoryController.controllerNamed)
@SessionScoped
public class ItemCreateWizardDomainInventoryController extends ItemCreateWizardController implements Serializable {
    
    private final String ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP = "itemElementInstantiation";
    
    public final static String controllerNamed = "itemCreateWizardDomainInventoryController";

    @Override
    public String getItemCreateWizardControllerNamed() {
        return controllerNamed; 
    }
    
    ItemDomainInventoryController itemDomainController = null; 
    
    private ItemDomainInventoryController getItemDomainInventoryController() {
        if (itemDomainController == null) {
            itemDomainController = ItemDomainInventoryController.getInstance(); 
        }
        return itemDomainController;         
    }
    
    @Override
    public ItemController getItemController() {
        return getItemDomainInventoryController(); 
    }
    
    public static ItemCreateWizardDomainInventoryController getInstance() {
        return (ItemCreateWizardDomainInventoryController) SessionUtility.findBean(controllerNamed);
    }
    
    @Override
    protected String getCreateItemWizardMenuItemValue(ItemCreateWizardSteps step) {
        switch (step) {
            case basicInformation:
                return null;
            case classification:
                return null;
            case permissions:
                return null;
            case reviewSave:
                return null;
            default:
                break;
        }

        return super.getCreateItemWizardMenuItemValue(step);
    }

    @Override
    protected String getCreateItemWizardMenuItemCustomValue(String stepName) {
        if (stepName.equals(ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP)) {
            return "Bill of Materials";
        }

        return super.getCreateItemWizardMenuItemCustomValue(stepName);
    }

    @Override
    public MenuModel getCreateItemWizardStepsMenuModel() {
        if (createItemWizardStepsMenuModel == null) {
            // Create all of the standard menu items.
            super.getCreateItemWizardStepsMenuModel();

            DefaultMenuItem menuItem;
            String menuItemDisplayValue = getCreateItemWizardMenuItemCustomValue(ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP);
            menuItem = createMenuItemForCreateWizardSteps(menuItemDisplayValue, ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP);

            createItemWizardStepsMenuModel.getElements().add(menuItem);

        }

        return createItemWizardStepsMenuModel;
    }

    @Override
    public String getNextStepForCreateItemWizard(FlowEvent event) {
        if (getCurrent().getDerivedFromItem() == null) {
            SessionUtility.addWarningMessage("No Catalog Item Selected", "Please select a catalog item.");
            return ItemCreateWizardSteps.derivedFromItemSelection.getValue();
        }

        String nsEvent = event.getNewStep();

        if (nsEvent.equals(ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP)) {
            getItemDomainInventoryController().prepareBillOfMaterialsForCurrentItem();
        }

        return super.getNextStepForCreateItemWizard(event);
    }
    
    @Override
    public String getLastCreateWizardStep() {
        return ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP;
    }
            
}
