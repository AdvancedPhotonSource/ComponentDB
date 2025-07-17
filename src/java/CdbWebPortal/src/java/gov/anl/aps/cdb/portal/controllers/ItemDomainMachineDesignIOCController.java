/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignIOCSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignIOCControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignIOCLazyDataModel;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
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

    public static ItemDomainMachineDesignIOCController getInstance() {
        return (ItemDomainMachineDesignIOCController) SessionUtility.findBean(CONTROLLER_NAMED);
    }

    @Override
    protected String getViewPath() {
        return "/views/itemDomainMachineDesignIOC/view.xhtml";
    }

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
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return false;
    }

    @Override
    public String prepareCreate() {
        // Reset variables
        parentMachineSelectionTreeNode = null;

        return super.prepareCreate();
    }

    @Override
    public String create() {
        ItemDomainMachineDesign current = getCurrent();
        ItemDomainMachineDesign parentMachineDesign = current.getParentMachineDesignPlaceholder();

        if (parentMachineDesign != null) {
            UserInfo user = SessionUtility.getUser();

            try {
                getControllerUtility().updateMachineParent(current, user, parentMachineDesign);
            } catch (CdbException ex) {
                LOGGER.error(ex);
            }
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
            ItemDomainMachineDesign parentItem = (ItemDomainMachineDesign) machineElement.getContainedItem();

            current.setParentMachineDesignPlaceholder(parentItem);

        }
    }

}
