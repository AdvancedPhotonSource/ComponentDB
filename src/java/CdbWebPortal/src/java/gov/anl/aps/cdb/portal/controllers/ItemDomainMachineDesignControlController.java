/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignControlSettings;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControlControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignRelationshipTreeNode;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
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
        ItemDomainMachineDesignRelationshipTreeNode node = getSelectedItemInListTreeTable(); 
        PropertyValue controlInterfaceToParent = getControlInterfaceToParentForItem(node);
        
        if (controlInterfaceToParent.getId() != null) {
            controlInterfaceSelection = controlInterfaceToParent.getValue();
        }
    }

    public void updateInterfaceToParent() {
        ItemDomainMachineDesignRelationshipTreeNode node = getSelectedItemInListTreeTable(); 
        PropertyValue controlInterfaceToParent = getControlInterfaceToParentForItem(node);       
        controlInterfaceToParent.setValue(controlInterfaceSelection);

        update();
        expandToSelectedTreeNodeAndSelect();
    }
    
    public PropertyValue getControlInterfaceToParentForItem(ItemDomainMachineDesignRelationshipTreeNode node) {
        ItemDomainMachineDesign parentItem = getParentOfSelectedItemInHierarchy(node); 
        ItemElement element = node.getElement();
        ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) element.getContainedItem();
        PropertyValue interfaceToParentPV = getControllerUtility().getControlInterfaceToParentForItem(mdItem, parentItem); 
        return interfaceToParentPV;         
    }

    @Override
    protected void performApplyRelationship() throws InvalidArgument, InvalidObjectState {
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
