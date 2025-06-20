/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignIOCSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignIOCControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignIOCLazyDataModel;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainMachineDesignIOCController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainMachineDesignIOCController extends ItemDomainMachineDesignBaseController<ItemDomainMachineDesignTreeNode, ItemDomainMachineDesignIOCControllerUtility, ItemDomainMachineDesignIOCLazyDataModel, ItemDomainMachineDesignIOCSettings> {

    public final static String CONTROLLER_NAMED = "itemDomainMachineDesignIOCController";

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignIOCController.class.getName());

    private ItemDomainMachineDesignTreeNode parentMachineSelectionTreeNode;
    private ItemDomainMachineDesignTreeNode parentMachineSelectedTreeNode = null;

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
    protected ItemDomainMachineDesignIOCSettings createNewSettingObject() {
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

    @Override
    public boolean getEntityDisplayImportButton() {
        return false;
    }

    @Override
    public boolean getEntityDisplayExportButton() {
        return false;
    }

    @Override
    public String prepareCreate() {
        // Reset variables
        parentMachineSelectionTreeNode = null;

        String createRedirect = super.prepareCreate();

        // Assign IOC type
        ItemDomainMachineDesign current = getCurrent();
        String iocEntityTypeName = EntityTypeName.ioc.getValue();
        EntityType iocEntityType = entityTypeFacade.findByName(iocEntityTypeName);
        try {
            current.setEntityTypeList(new ArrayList<>());
        } catch (CdbException ex) {
            LOGGER.error(ex);
        }
        current.getEntityTypeList().add(iocEntityType);

        // Create placeholder for machine parent element.
        UserInfo user = SessionUtility.getUser();
        ItemElement parentItemElement = getControllerUtility().createItemElement(current, user);
        parentItemElement.setParentItem(null);
        parentItemElement.setContainedItem(current);
        try {
            current.setParentMachineElement(parentItemElement);
        } catch (CdbException ex) {
            LOGGER.error(ex);
        }

        return createRedirect;
    }

    @Override
    public String create() {
        ItemDomainMachineDesign current = getCurrent();
        ItemDomainMachineDesign parentMachineDesign = current.getParentMachineDesign();

        if (parentMachineDesign == null) {
            SessionUtility.addWarningMessage("Machine Parent", "Machine Parent must be specified");
            return null;
        }

        String createRedirect = super.create();

        return createRedirect;
    }

    public ItemDomainMachineDesignTreeNode getParentMachineSelectionTreeNode() {
        if (parentMachineSelectionTreeNode == null) {
            ItemDomainMachineDesignController instance = ItemDomainMachineDesignController.getInstance();
            parentMachineSelectionTreeNode = instance.loadMachineDesignRootTreeNode();

        }
        return parentMachineSelectionTreeNode;
    }

    public ItemDomainMachineDesignTreeNode getParentMachineSelectedTreeNode() {
        return parentMachineSelectedTreeNode;
    }

    public void setParentMachineSelectedTreeNode(ItemDomainMachineDesignTreeNode parentMachineSelectedTreeNode) {
        this.parentMachineSelectedTreeNode = parentMachineSelectedTreeNode;

        if (parentMachineSelectedTreeNode != null) {
            ItemElement machineElement = (ItemElement) parentMachineSelectedTreeNode.getData();

            if (machineElement.getId() == null) {
                return;
            }

            ItemDomainMachineDesign current = getCurrent();
            Item parentItem = machineElement.getContainedItem();

            String elementName = getControllerUtility().generateUniqueElementNameForItem(parentItem);

            ItemElement parentMachineElement = current.getParentMachineElement();
            parentMachineElement.setName(elementName);

            parentMachineElement.setParentItem(parentItem);
        }
    }

}
