/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableDesignSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.annotation.PostConstruct;
import org.primefaces.model.TreeNode;

/**
 *
 * @author cmcchesney
 */
@Named(ItemDomainCableDesignController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableDesignController extends ItemController<ItemDomainCableDesign, ItemDomainCableDesignFacade, ItemDomainCableDesignSettings> {

    public class EndpointDialog {

        private Item itemEndpoint;
        private TreeNode valueModelTree = null;
        private TreeNode selectionModelEndpoint = null;

        public Item getItemEndpoint() {
            return itemEndpoint;
        }

        public void setItemEndpoint(Item itemEndpoint) {
            this.itemEndpoint = itemEndpoint;
        }

        public TreeNode getValueModelTree() {
            if (valueModelTree == null) {
                ItemDomainMachineDesignController controller = ItemDomainMachineDesignController.getInstance();
                valueModelTree = controller.loadMachineDesignRootTreeNode(false);
            }
            return valueModelTree;
        }

        public void setValueModelTree(TreeNode valueModelTree) {
            this.valueModelTree = valueModelTree;
        }

        public TreeNode getSelectionModelEndpoint() {
            return selectionModelEndpoint;
        }

        public void setSelectionModelEndpoint(TreeNode selectionModelEndpoint) {
            this.selectionModelEndpoint = selectionModelEndpoint;
        }

        /**
         * Determine whether endpoint changed, and call update if it did to save
         * the change.
         */
        public String save(String remoteCommandSuccess) {

            Item selectedItemEndpoint = ((ItemElement) (selectionModelEndpoint.getData())).getContainedItem();
            
            // endpoint changed, update cable and save
            if (!selectedItemEndpoint.equals(getItemEndpoint())) {
                if (getCurrent().updateEndpoint(getItemEndpoint(), selectedItemEndpoint)) {
                    
                    String updateResult = update();

                    // An error occured, reload the page with correct information. 
                    if (updateResult == null) {
                        reloadCurrent();
                        return view();
                    }

                    SessionUtility.executeRemoteCommand(remoteCommandSuccess);
                }
            }        
            return null;
        }

        public void cancel() {

        }

        public void actionListenerSaveSuccess() {

        }
    }

    public static final String CONTROLLER_NAMED = "itemDomainCableDesignController";

    @EJB
    ItemDomainCableDesignFacade itemDomainCableDesignFacade;

    private EndpointDialog dialogEndpoint1 = new EndpointDialog();
    private EndpointDialog dialogEndpoint2 = new EndpointDialog();

    public EndpointDialog getDialogEndpoint1() {
        return dialogEndpoint1;
    }

    public void setDialogEndpoint1(EndpointDialog endpoint1Dialog) {
        this.dialogEndpoint1 = endpoint1Dialog;
    }

    public EndpointDialog getDialogEndpoint2() {
        return dialogEndpoint2;
    }

    public void setDialogEndpoint2(EndpointDialog endpoint2Dialog) {
        this.dialogEndpoint2 = dialogEndpoint2;
    }

    public static ItemDomainCableDesignController getInstance() {
        return (ItemDomainCableDesignController) SessionUtility.findBean(ItemDomainCableDesignController.CONTROLLER_NAMED);
    }

    /**
     * Creates a cable design object and sets the core variables, intended to be
     * invoked for creating the various "subtypes" of cable design, e.g.,
     * placeholder, catalog, bundle, etc.
     *
     * @param itemEndpoint1
     * @param itemEndpoint2
     * @param cableName
     * @return
     */
    private ItemDomainCableDesign createCableCommon(Item itemEndpoint1,
            Item itemEndpoint2,
            String cableName,
            List<ItemProject> projectList) {

        ItemDomainCableDesign newCable = this.createEntityInstance();
        newCable.setName(cableName);
        newCable.setItemProjectList(projectList);

        // set endpoints
        newCable.setEndpoint1(itemEndpoint1);
        newCable.setEndpoint2(itemEndpoint2);

        return newCable;
    }

    /**
     * Creates placeholder cable design connecting the specified endpoints.
     *
     * @param itemEndpoint1
     * @param itemEndpoint2
     * @param cableName
     * @return
     */
    public boolean createCablePlaceholder(Item itemEndpoint1,
            Item itemEndpoint2,
            String cableName,
            List<ItemProject> projectList) {

        ItemDomainCableDesign newCable = this.createCableCommon(itemEndpoint1,
                itemEndpoint2,
                cableName,
                projectList);

        if (this.create() == null) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Creates placeholder cable design connecting the specified endpoints.
     *
     * @param itemEndpoint1
     * @param itemEndpoint2
     * @param cableName
     * @return
     */
    public boolean createCableCatalog(Item itemEndpoint1,
            Item itemEndpoint2,
            String cableName,
            List<ItemProject> projectList,
            Item itemCableCatalog) {

        ItemDomainCableDesign newCable = this.createCableCommon(itemEndpoint1,
                itemEndpoint2,
                cableName,
                projectList);

        newCable.setCatalogItem(itemCableCatalog);

        if (this.create() == null) {
            return false;
        } else {
            return true;
        }

    }
    
    /**
     * Prepares dialog for editing endpoint2.
     */
    public void prepareDialogEndpoint2() {
        dialogEndpoint2.setItemEndpoint(getCurrent().getEndpoint2());
    }

    @Override
    protected ItemDomainCableDesign createEntityInstance() {
        ItemDomainCableDesign item = super.createEntityInstance();
        setCurrent(item);
        return item;
    }

    @Override
    protected ItemDomainCableDesign instenciateNewItemDomainEntity() {
        return new ItemDomainCableDesign();
    }

    @Override
    protected ItemDomainCableDesignSettings createNewSettingObject() {
        return new ItemDomainCableDesignSettings(this);
    }

    @Override
    protected ItemDomainCableDesignFacade getEntityDbFacade() {
        return itemDomainCableDesignFacade;
    }

    @Override
    public String getEntityTypeName() {
        return "cableDesign";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Cable Design";
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.cableDesign.getValue();
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getStyleName() {
        return "machineDesign";
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
