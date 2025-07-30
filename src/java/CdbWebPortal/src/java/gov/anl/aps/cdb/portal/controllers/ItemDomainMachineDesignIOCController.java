/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignIOCSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignIOCControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemElementControllerUtility;
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

    private final String PRE_BOOT_PLACEHOLDER_TEXT = " ** Pre-Boot Checklist ** \n"
            + "1. This IOC can be rebooted with stored beam\n"
            + "2. No special preparations required\n"
            + "3. Use standard reboot procedure: `caput $iocname:SysReset.PROC 1`";

    private final String POST_BOOT_PLACEHOLDER_TEXT = " ** Post-Boot Recovery ** \n"
            + "1. Verify IOC communication is restored\n"
            + "2. Check system status indicators\n"
            + "3. No additional recovery steps required";

    private final String POWER_CYCLE_PLACEHOLDER_TEXT = " ** Power Cycle Warning ** \n"
            + "1. This IOC can be rebooted without affecting operations\n"
            + "2. Power cycling may interrupt system communications temporarily\n"
            + "3. Verify all dependent systems are in safe state before proceeding";

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
    public boolean getEntityDisplayItemMemberships() {
        return false;
    }

    @Override
    public boolean getDisplayCreatedFromTemplateForCurrent() {
        return false;
    }

    @Override
    public String prepareCreate() {
        // Reset variables
        parentMachineSelectionTreeNode = null;

        return super.prepareCreate();
    }

    public String clearMachineParent() {
        ItemDomainMachineDesign current = getCurrent();
        UserInfo user = SessionUtility.getUser();

        ItemElement parentMachineElement = current.getParentMachineElement();
        if (parentMachineElement != null) {
            ItemElementControllerUtility ieUtility = parentMachineElement.getControllerUtility();
            try {
                ieUtility.destroy(parentMachineElement, user);
            } catch (CdbException ex) {
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return viewForCurrentEntity();
            }
        }

        reloadCurrent();
        // Update the modified date. 
        return update();
    }

    @Override
    public String update() {
        prepareSetParentMachineAttributes();

        return super.update();
    }

    @Override
    public String create() {
        prepareSetParentMachineAttributes();

        String createRedirect = super.create();

        return createRedirect;
    }

    private void prepareSetParentMachineAttributes() {
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

    public String getPRE_BOOT_PLACEHOLDER_TEXT() {
        return PRE_BOOT_PLACEHOLDER_TEXT;
    }

    public String getPOST_BOOT_PLACEHOLDER_TEXT() {
        return POST_BOOT_PLACEHOLDER_TEXT;
    }

    public String getPOWER_CYCLE_PLACEHOLDER_TEXT() {
        return POWER_CYCLE_PLACEHOLDER_TEXT;
    }

    @Override
    public String getStyleName() {
        return "machineDesignIOC";
    }

}
