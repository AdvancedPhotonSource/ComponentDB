/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.constants.SystemPropertyTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignControlSettings;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControlControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named(ItemDomainMachineDesignControlController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignControlController extends ItemDomainMachineDesignRelationshipBaseController<ItemDomainMachineDesignControlControllerUtility> {

    public final static String controllerNamed = "itemDomainMachineDesignControlController";
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignControlController.class.getName());
    
    @EJB
    protected PropertyValueFacade propertyValueFacade; 

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
        return "Machine: Control Hierarchy";
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
    
    public PropertyValue getControlInterfaceToParentForItem(ItemDomainMachineDesign mdItem) {
        if (mdItem == null) {
            return null; 
        }
        PropertyValue controlInterfaceToParent = mdItem.getControlInterfaceToParent();
        if (controlInterfaceToParent == null) {
            ItemElementRelationshipTypeNames relationshipTypeName = getRelationshipTypeName();
            int relationshipId = relationshipTypeName.getDbId();
            
            List<ItemDomainMachineDesign> parentItems = itemDomainMachineDesignFacade.fetchRelationshipParentItems(mdItem.getId(), relationshipId);
            if (parentItems.size() == 1) {
                Integer parentId = parentItems.get(0).getId();
                List<PropertyValue> pvs = propertyValueFacade.fetchRelationshipParentPropertyValues(mdItem.getId(), parentId, relationshipId); 
                
                String controlInterfacePvName = SystemPropertyTypeNames.cotrolInterface.getValue();
                for (PropertyValue pv : pvs) {
                    PropertyType propertyType = pv.getPropertyType();
                    if (propertyType.getName().equals(controlInterfacePvName)) {
                        mdItem.setControlInterfaceToParent(pv);
                        return pv; 
                    }
                    
                }
            }            
            
            // Prevent reloading non existent property. 
            mdItem.setControlInterfaceToParent(new PropertyValue());            
        }
        
        return controlInterfaceToParent;                 
    }

    @Override
    protected void performApplyRelationship() throws InvalidArgument {
        ItemDomainMachineDesignControlControllerUtility controllerUtility = getControllerUtility();
        UserInfo enteredByUser = (UserInfo) SessionUtility.getUser();
        controllerUtility.applyRelationship(getMachineRelatedByCurrent(), getCurrent(), controlInterfaceSelection, enteredByUser);
    }
    
    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignControlSettings(this); 
    }   

    @Override
    protected EntityTypeName getRelationshipMachineEntityType() {
        return EntityTypeName.control;
    }

    @Override
    public boolean getEntityDisplayDeletedItems() {
        return true; 
    }

    @Override
    public String deletedItemsList() {
        String deletedItemsList = super.deletedItemsList();
        return "/views/itemDomainMachineDesign/" + deletedItemsList; 
    }

}
