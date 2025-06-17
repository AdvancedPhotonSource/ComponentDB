/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignIOCSettings;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignIOCControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignIOCLazyDataModel;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainMachineDesignIOCController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainMachineDesignIOCController extends ItemDomainMachineDesignBaseController<ItemDomainMachineDesignTreeNode, ItemDomainMachineDesignIOCControllerUtility, ItemDomainMachineDesignIOCLazyDataModel> {

    public final static String CONTROLLER_NAMED = "itemDomainMachineDesignIOCController";

    @Override
    public ItemDomainMachineDesignTreeNode loadMachineDesignRootTreeNode(List<ItemDomainMachineDesign> itemsWithoutParents) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ItemDomainMachineDesignTreeNode createMachineTreeNodeInstance() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected ItemDomainMachineDesignIOCControllerUtility createControllerUtilityInstance() {
        return new ItemDomainMachineDesignIOCControllerUtility();
    }

    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignIOCSettings(this);
    }

    @Override
    public String getItemListPageTitle() {
        return "IOC Item List";
    }

    @Override
    public ItemDomainMachineDesignIOCLazyDataModel createItemLazyDataModel() {
        return new ItemDomainMachineDesignIOCLazyDataModel(getEntityDbFacade(), getDefaultDomain(), settingObject);
    }

    @Override
    public boolean getEntityDisplayDeletedItems() {
        return false;
    }

}
