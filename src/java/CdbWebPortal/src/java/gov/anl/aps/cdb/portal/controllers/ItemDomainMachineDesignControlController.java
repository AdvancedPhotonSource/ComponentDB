/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControlControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named(ItemDomainMachineDesignControlController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignControlController extends ItemDomainMachineDesignRelationshipBaseController {

    public final static String controllerNamed = "itemDomainMachineDesignControlController";
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignControlController.class.getName());

    private String controlInterfaceSelection = null;
    private PropertyType controlInterfacePropertyType = null;        

    @Override
    protected ItemDomainMachineDesignControlControllerUtility getControllerUtility() {
        return (ItemDomainMachineDesignControlControllerUtility) super.getControllerUtility(); 
    }

    @Override
    protected String getViewPath() {
        return "/views/itemDomainMachineDesignControl/view.xhtml";
    }

    @Override
    public String getItemListPageTitle() {
        return "Control Machine Elements";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Control Machine Element";
    }

    @Override
    public DataModel getTopLevelMachineDesignSelectionList() {
        ItemDomainMachineDesign current = getCurrent();
        if (current == null) {
            return null;
        }
        
        DataModel topLevelMachineDesignSelectionList = current.getTopLevelMachineDesignSelectionList();

        if (topLevelMachineDesignSelectionList == null) {
            List<ItemDomainMachineDesign> topLevelMachineDesignInventory = itemDomainMachineDesignFacade.getTopLevelMachineDesignControl();

            removeTopLevelParentOfItemFromList(current, topLevelMachineDesignInventory);

            topLevelMachineDesignSelectionList = new ListDataModel(topLevelMachineDesignInventory);
            current.setTopLevelMachineDesignSelectionList(topLevelMachineDesignSelectionList);
        }

        return topLevelMachineDesignSelectionList;
        
    }

    public static ItemDomainMachineDesignControlController getInstance() {
        return (ItemDomainMachineDesignControlController) SessionUtility.findBean(controllerNamed);
    }

    @Override
    public List<ItemDomainMachineDesign> getDefaultTopLevelMachineList() {
        return itemDomainMachineDesignFacade.getTopLevelMachineDesignControl();
    }

    @Override
    protected ItemDomainMachineDesignControlControllerUtility createControllerUtilityInstance() {
        return new ItemDomainMachineDesignControlControllerUtility();
    }

    public PropertyType getControlInterfacePropertyType() {
        if (controlInterfacePropertyType == null) {
            ItemDomainMachineDesignControlControllerUtility controllerUtility = getControllerUtility();
            controlInterfacePropertyType = controllerUtility.fetchInterfaceToParentPropertyType();
        }
        return controlInterfacePropertyType;
    }

    public String getControlInterfaceSelection() {
        return controlInterfaceSelection;
    }

    public void setControlInterfaceSelection(String controlInterfaceSelection) {
        this.controlInterfaceSelection = controlInterfaceSelection;
    }

    public void prepareUpdateInterfaceToParent() {
        updateCurrentUsingSelectedItemInTreeTable();

        ItemDomainMachineDesign current = getCurrent();
        PropertyValue controlInterfaceToParent = current.getControlInterfaceToParent();
        if (controlInterfaceToParent.getId() != null) {
            controlInterfaceSelection = controlInterfaceToParent.getValue();
        }
    }

    public void updateInterfaceToParent() {
        ItemDomainMachineDesign current = getCurrent();
        PropertyValue controlInterfaceToParent = current.getControlInterfaceToParent();
        ItemElementRelationship controlRelationshipToParent = current.getControlRelationshipToParent();
        if (controlInterfaceToParent.getId() == null) {
            ItemDomainMachineDesignControlControllerUtility controllerUtility = getControllerUtility();
            UserInfo enteredByUser = (UserInfo) SessionUtility.getUser();
            try {
                controllerUtility.createInterfaceToParentPropertyValue(controlRelationshipToParent, controlInterfaceSelection, enteredByUser);
            } catch (InvalidArgument ex) {
                LOGGER.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return;
            }
        } else {
            controlInterfaceToParent.setValue(controlInterfaceSelection);
        }
        
        update();
        expandToSelectedTreeNodeAndSelect();
    }

    @Override
    protected void performApplyRelationship() throws InvalidArgument {
        ItemDomainMachineDesignControlControllerUtility controllerUtility = getControllerUtility();
        UserInfo enteredByUser = (UserInfo) SessionUtility.getUser();
        controllerUtility.applyRelationship(getMachineRelatedByCurrent(), getCurrent(), controlInterfaceSelection, enteredByUser);
    }

    // todo resolve this 
    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this);
    }   

    @Override
    protected EntityTypeName getRelationshipMachineEntityType() {
        return EntityTypeName.control;
    }   
}
