 /*
  * Copyright (c) UChicago Argonne, LLC. All rights reserved.
  * See LICENSE file.
  */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ItemControllerExtensionHelper;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author djarosz
 */
public abstract class ItemCreateWizardController extends ItemControllerExtensionHelper {      
    
    private static final Logger logger = LogManager.getLogger(ItemCreateWizardController.class.getName());
    
    private String currentWizardStep;
    protected MenuModel createItemWizardStepsMenuModel = null;
    protected Integer currentWizardStepIndex = null;
    
    private Boolean enabledEnforcedProperties = Boolean.parseBoolean(ConfigurationUtility.getUiProperty("EnabledEnforcedPropertiesForInventoryOfCatalogItem"));
           
    
    public abstract String getItemCreateWizardControllerNamed(); 
    
    protected enum ItemCreateWizardSteps {
        derivedFromItemSelection("derivedFromItemSelectionTab", "Derived From Item"),        
        basicInformation("basicItemInformationTab", "Basic Information"),
        enforcedPropertyTypesTab("enforcedPropertyTypesTab", "Properties"),
        classification("itemClassificationTab", "Classification"),
        permissions("itemPermissionTab", "Permissions"),
        reviewSave("reviewItemTab", "Review");

        private final String value;
        private final String displayValue;

        private ItemCreateWizardSteps(String value, String displayValue) {
            this.value = value;
            this.displayValue = displayValue;
        }

        public String getValue() {
            return value;
        }

        public String getDisplayValue() {
            return displayValue;
        }
    };    
    
    /**
     * Method to be overridden by child controllers to control wizard steps in
     * menu.
     *
     * @param step
     * @return
     */
    protected String getCreateItemWizardMenuItemValue(ItemCreateWizardSteps step) {
        if (step.getValue().equals(ItemCreateWizardSteps.enforcedPropertyTypesTab.getValue())) {
            if (enabledEnforcedProperties) {
                ItemEnforcedPropertiesController iepc = getItemEnforcedPropertiesController();
                if (!iepc.isItemHasEditableEnforcedProperties()) {
                    return null; 
                }
            } else {
                return null; 
            }
        }
        else if (step.getValue().equals(ItemCreateWizardSteps.derivedFromItemSelection.getValue())) {            
            if (getEntityDisplayDerivedFromItem()) {
                return getDerivedFromItemTitle();
            } else {
                return null;
            }
        }

        return step.getDisplayValue();
    }

    public MenuModel getCreateItemWizardStepsMenuModel() {
        if (createItemWizardStepsMenuModel == null) {
            createItemWizardStepsMenuModel = new DefaultMenuModel();
            for (ItemCreateWizardSteps step : ItemCreateWizardSteps.values()) {
                String menuItemValue = getCreateItemWizardMenuItemValue(step);

                if (menuItemValue != null) {
                    DefaultMenuItem menuItem;
                    menuItem = createMenuItemForCreateWizardSteps(menuItemValue, step.getValue());
                    createItemWizardStepsMenuModel.getElements().add(menuItem);                     
                }
            }
        }

        return createItemWizardStepsMenuModel;
    }

    protected DefaultMenuItem createMenuItemForCreateWizardSteps(String displayValue, String stepValue) {
        DefaultMenuItem menuElement = new DefaultMenuItem();
        menuElement.setValue(displayValue);
        String menuElementCommand = "#{";
        menuElementCommand += getItemCreateWizardControllerNamed();
        menuElementCommand += ".updateCurrentWizardStep('" + stepValue + "')";
        menuElementCommand += "}";
        menuElement.setCommand(menuElementCommand);
        return menuElement;
    }

    private ItemCreateWizardSteps getStepForValue(String value) {
        for (ItemCreateWizardSteps step : ItemCreateWizardSteps.values()) {
            if (step.getValue().equals(value)) {
                return step;
            }
        }

        return null;
    }

    protected String getCreateItemWizardMenuItemCustomValue(String stepName) {
        return null;
    }

    private void updateCurrentWizardStepIndex(String currentStep) {
        if (createItemWizardStepsMenuModel != null) {
            ItemCreateWizardSteps step = getStepForValue(currentStep);
            String menuItemValue = null;
            if (step != null) {
                menuItemValue = getCreateItemWizardMenuItemValue(step);
            } else {
                // Step is null, this may be a custom step spefic to derived controller.
                menuItemValue = getCreateItemWizardMenuItemCustomValue(currentStep);
            }
            if (menuItemValue != null) {
                for (MenuElement menuElement : createItemWizardStepsMenuModel.getElements()) {
                    DefaultMenuItem menuItem = (DefaultMenuItem) menuElement;
                    if (menuItem.getValue().equals(menuItemValue)) {
                        currentWizardStepIndex = createItemWizardStepsMenuModel.getElements().indexOf(menuElement);
                        break;
                    }

                }
            }
        }
    }

    public int getCurrentWizardStepIndex() {
        if (currentWizardStepIndex == null) {
            currentWizardStepIndex = 0;
        }

        return currentWizardStepIndex;
    }

    public String getCurrentWizardStep() {
        if (currentWizardStep == null) {
            currentWizardStep = getFirstCreateWizardStep();
        }

        return currentWizardStep;
    }

    public String updateCurrentWizardStep(String currentWizardStep) {
        this.currentWizardStep = currentWizardStep;
        updateCurrentWizardStepIndex(currentWizardStep);

        return SessionUtility.getCurrentViewId() + "?faces-redirect=true";
    }

    public void setCurrentWizardStep(String currentWizardStep) {
        this.currentWizardStep = currentWizardStep;
    }

    public String createItemWizardFlowListener(FlowEvent event) {
        String prevStep = event.getOldStep();
        String nextStep = getNextStepForCreateItemWizard(event);

        currentWizardStep = nextStep;

        if (prevStep.equals(currentWizardStep) == false) {
            updateCurrentWizardStepIndex(nextStep);            
            SessionUtility.executeRemoteCommand("updateWizardButtons()");
        }
        return nextStep;
    }

    public boolean isCreateWizardOnLastStep() {
        if (currentWizardStep != null) {
            return getLastCreateWizardStep().equals(currentWizardStep);
        }
        return false;
    }

    public boolean isCreateWizardOnFirstStep() {
        if (currentWizardStep != null) {
            return getFirstCreateWizardStep().equals(currentWizardStep);
        }
        // On first step; flow listener never got a chance to set current step.
        return true;
    }

    public String getLastCreateWizardStep() {
        return ItemCreateWizardSteps.reviewSave.getValue();

    }

    public String getFirstCreateWizardStep() {
        return ItemCreateWizardSteps.derivedFromItemSelection.getValue();
    }

    public void resetCreateItemWizardVariables() {
        // Reset any variables for the wizard.
        currentWizardStep = null;
        currentWizardStepIndex = null;
    }
    
    public Boolean isRenderClassificationCreateWizardTab() {
        return this.isEntityTypeEditable()
                && this.getEntityDisplayItemType()
                && this.getEntityDisplayItemCategory();
    }
    
    public String getNextStepForCreateItemWizard(FlowEvent event) {
        logger.debug("User entering step " + event.getNewStep() + " in " + getDisplayEntityTypeName() + "Create Wizard.");
        String finishedStep = event.getOldStep();

        // Verify that the new item is unique. Prompt user to update information if this is not the case. 
        if ((finishedStep.equals(ItemCreateWizardSteps.basicInformation.getValue()))
                && (!event.getNewStep().equals(ItemCreateWizardSteps.derivedFromItemSelection.getValue()))) {
            try {
                checkItemUniqueness(getCurrent());
            } catch (CdbException ex) {
                SessionUtility.addWarningMessage("Requirement Not Met", ex.getErrorMessage());
                return ItemCreateWizardSteps.basicInformation.getValue();
            }
        } else if (finishedStep.equals(ItemCreateWizardSteps.classification.getValue())) {
            if (isItemProjectRequired()) {
                try {
                    checkItemProject(getCurrent());
                } catch (CdbException ex) {
                    SessionUtility.addWarningMessage("Requirement Not Met", ex.getErrorMessage());
                    return finishedStep;
                }
            }
        }

        return event.getNewStep();
    }
}
