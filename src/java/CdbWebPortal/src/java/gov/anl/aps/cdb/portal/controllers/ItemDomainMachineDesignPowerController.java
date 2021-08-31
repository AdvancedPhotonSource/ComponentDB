/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignPowerControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named(ItemDomainMachineDesignPowerController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignPowerController extends ItemDomainMachineDesignRelationshipBaseController {

    public final static String controllerNamed = "itemDomainMachineDesignPowerController";
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignPowerController.class.getName());

    @Override
    protected String getViewPath() {
        return "/views/itemDomainMachineDesignPower/view.xhtml";
    }

    @Override
    public String getItemListPageTitle() {
        return "Power Machine Elements";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Power Machine Element";
    }

    @Override
    public DataModel getTopLevelMachineDesignSelectionList() {
        ItemDomainMachineDesign current = getCurrent();
        DataModel topLevelMachineDesignSelectionList = current.getTopLevelMachineDesignSelectionList();

        if (topLevelMachineDesignSelectionList == null) {
            List<ItemDomainMachineDesign> topLevelMachineDesignInventory = itemDomainMachineDesignFacade.getTopLevelMachineDesignPower();

            removeTopLevelParentOfItemFromList(current, topLevelMachineDesignInventory);

            topLevelMachineDesignSelectionList = new ListDataModel(topLevelMachineDesignInventory);
            current.setTopLevelMachineDesignSelectionList(topLevelMachineDesignSelectionList);
        }

        return topLevelMachineDesignSelectionList;
    }

    public static ItemDomainMachineDesignPowerController getInstance() {
        return (ItemDomainMachineDesignPowerController) SessionUtility.findBean(controllerNamed);
    }

    @Override
    public List<ItemDomainMachineDesign> getDefaultTopLevelMachineList() {
        return itemDomainMachineDesignFacade.getTopLevelMachineDesignPower();
    }

    @Override
    protected ItemDomainMachineDesignPowerControllerUtility createControllerUtilityInstance() {
        return new ItemDomainMachineDesignPowerControllerUtility();
    }

    // todo resolve this 
    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this);
    }          

    @Override
    protected EntityTypeName getRelationshipMachineEntityType() {
        return EntityTypeName.power; 
    }

}
