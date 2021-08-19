/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControlControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
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

    // todo resolve this 
    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this); 
    }             
    
    @Override
    protected ItemElementRelationshipTypeNames getRelationshipTypeName() {
        return ItemElementRelationshipTypeNames.control; 
    }

    @Override
    protected EntityTypeName getRelationshipMachineEntityType() {
        return EntityTypeName.control; 
    }    
}
