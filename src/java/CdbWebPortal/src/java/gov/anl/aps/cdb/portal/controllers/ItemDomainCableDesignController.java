/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.ItemCoreMetadataFieldType;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.extensions.BundleWizard;
import gov.anl.aps.cdb.portal.controllers.extensions.CableWizard;
import gov.anl.aps.cdb.portal.controllers.extensions.CircuitWizard;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCableDesign;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import static gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign.CABLE_DESIGN_INTERNAL_PROPERTY_TYPE;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemCoreMetadataPropertyInfo;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author cmcchesney
 */
@Named(ItemDomainCableDesignController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableDesignController extends ItemController<ItemDomainCableDesignControllerUtility, ItemDomainCableDesign, ItemDomainCableDesignFacade, ItemDomainCableDesignSettings> {

    @Override
    protected ItemDomainCableDesignControllerUtility createControllerUtilityInstance() {
        return new ItemDomainCableDesignControllerUtility(); 
    }

    public class EndpointDialog {

        private Boolean disableButtonSave = true;
        private Item itemEndpoint = null;
        private TreeNode valueModelTree = null;
        private TreeNode selectionModelEndpoint = null;
        
        public Boolean getDisableButtonSave() {
            return disableButtonSave;
        }

        public void setDisableButtonSave(Boolean disableButtonSave) {
            this.disableButtonSave = disableButtonSave;
        }

        public Item getItemEndpoint() {
            return itemEndpoint;
        }

        public void setItemEndpoint(Item itemEndpoint) {
            this.itemEndpoint = itemEndpoint;
            expandToSpecificMachineDesignItem((ItemDomainMachineDesign) itemEndpoint);
        }

        public String getItemEndpointString() {
            if (itemEndpoint == null) {
                return "";
            } else {
                return itemEndpoint.getName();
            }
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
         * Determines whether endpoint changed, and call update if it did to
         * save the change.
         */
        public String save(String remoteCommandSuccess) {

            Item selectedItemEndpoint
                    = ((ItemElement) (selectionModelEndpoint.getData())).getContainedItem();

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

        /**
         * Handles select events generated by the machine design tree table
         * component. Must call client side remoteCommand to update button state
         * from oncomplete attribute for this event tag.
         */
        public void selectListenerEndpoint(NodeSelectEvent event) {
            setEnablement();
        }

        public void actionListenerSaveSuccess() {
        }

        /**
         * Resets the dialog components when closing.
         */
        public void reset() {
            setSelectionModelEndpoint(null);
            valueModelTree = null;
            setEnablement();
        }

        /**
         * Enables save button when a valid selection, different from the
         * original endpoint, is made.
         */
        private void setEnablement() {

            if (selectionModelEndpoint == null) {

                setDisableButtonSave((Boolean) true);

            } else {

                Item selectedItemEndpoint
                        = ((ItemElement) (selectionModelEndpoint.getData())).getContainedItem();

                if (selectedItemEndpoint.equals(getItemEndpoint())) {
                    setDisableButtonSave((Boolean) true);
                } else {
                    setDisableButtonSave((Boolean) false);
                }
            }
        }

        private void expandToSpecificMachineDesignItem(ItemDomainMachineDesign item) {

            TreeNode machineDesignTreeRootTreeNode = getValueModelTree();

            if (selectionModelEndpoint != null) {
                selectionModelEndpoint.setSelected(false);
                selectionModelEndpoint = null;
            }

            TreeNode selectedNode = ItemDomainMachineDesignController.
                    expandToSpecificMachineDesignItem(machineDesignTreeRootTreeNode, item);
            selectionModelEndpoint = selectedNode;
        }

    }

    public class CatalogDialog {

        private Boolean disableButtonSave = true;
        private Item itemCatalog = null;
        private Item selectionModelCatalog = null;
        
        public Boolean getDisableButtonSave() {
            return disableButtonSave;
        }

        public void setDisableButtonSave(Boolean disableButtonSave) {
            this.disableButtonSave = disableButtonSave;
        }

        public Item getItemCatalog() {
            return itemCatalog;
        }

        public void setItemCatalog(Item itemCatalog) {
            this.itemCatalog = itemCatalog;
            selectCatalogItem((ItemDomainCableCatalog) itemCatalog);
        }

        public String getItemCatalogString() {
            if (itemCatalog == null) {
                return "";
            } else {
                return itemCatalog.getName();
            }
        }

        public Item getSelectionModelCatalog() {
            return selectionModelCatalog;
        }

        public void setSelectionModelCatalog(Item selectionModelCatalog) {
            this.selectionModelCatalog = selectionModelCatalog;
        }

        /**
         * Determines whether catalog item changed, and calls update if it did.
         */
        public String save(String remoteCommandSuccess) {

            // catalog item changed, update cable and save
            if (!selectionModelCatalog.equals(getItemCatalog())) {
                
                getCurrent().setCatalogItem(selectionModelCatalog);

                String updateResult = update();

                // An error occured, reload the page with correct information. 
                if (updateResult == null) {
                    reloadCurrent();
                    return view();
                }

                SessionUtility.executeRemoteCommand(remoteCommandSuccess);
            }
            return null;
        }

        public void cancel() {
        }

        /**
         * Handles select events generated by the data table
         * component. Must call client side remoteCommand to update button state
         * from oncomplete attribute for this event tag.
         */
        public void selectListenerCatalog() {
            setEnablement();
        }

        public void actionListenerSaveSuccess() {
        }

        /**
         * Resets the dialog components when closing.
         */
        public void reset() {
            setSelectionModelCatalog(null);
            setEnablement();
        }

        /**
         * Enables save button when a valid selection, different from the
         * original catalog item, is made.
         */
        private void setEnablement() {

            if (selectionModelCatalog == null) {

                setDisableButtonSave((Boolean) true);

            } else {

                if (selectionModelCatalog.equals(getItemCatalog())) {
                    setDisableButtonSave((Boolean) true);
                } else {
                    setDisableButtonSave((Boolean) false);
                }
            }
        }

        private void selectCatalogItem(ItemDomainCableCatalog item) {
            selectionModelCatalog = item;
        }

    }

    public static final String CONTROLLER_NAMED = "itemDomainCableDesignController";

    @EJB
    ItemDomainCableDesignFacade itemDomainCableDesignFacade;

    private CatalogDialog dialogCatalog = new CatalogDialog();
    private EndpointDialog dialogEndpoint = new EndpointDialog();

    protected ImportHelperCableDesign importHelper = new ImportHelperCableDesign();
    
    public CatalogDialog getDialogCatalog() {
        return dialogCatalog;
    }

    public void setDialogCatalog(CatalogDialog endpoint2Dialog) {
        this.dialogCatalog = dialogCatalog;
    }

    public EndpointDialog getDialogEndpoint() {
        return dialogEndpoint;
    }

    public void setDialogEndpoint(EndpointDialog endpoint2Dialog) {
        this.dialogEndpoint = dialogEndpoint;
    }

    public static ItemDomainCableDesignController getInstance() {
        return (ItemDomainCableDesignController) SessionUtility.findBean(ItemDomainCableDesignController.CONTROLLER_NAMED);
    }        

    /**
     * Creates a cable design object and sets the core variables.
     *
     * @param itemEndpoint1
     * @param itemEndpoint2
     * @param cableName
     * @return
     */
    private ItemDomainCableDesign createCableCommon(Item itemEndpoint1,
            Item itemEndpoint2,
            String cableName,
            List<ItemProject> projectList,
            List<ItemCategory> technicalSystemList) {

        ItemDomainCableDesign newCable = this.createEntityInstance();
        newCable.setName(cableName);
        newCable.setItemProjectList(projectList);
        newCable.setTechnicalSystemList(technicalSystemList);

        // set endpoints
        newCable.setEndpoint1(itemEndpoint1);
        newCable.setEndpoint2(itemEndpoint2);

        return newCable;
    }

    /**
     * Creates cable design connecting the specified endpoints, with
     * unspecified cable catalog type.
     *
     * @param itemEndpoint1
     * @param itemEndpoint2
     * @param cableName
     * @return
     */
    public boolean createCableUnspecified(Item itemEndpoint1,
            Item itemEndpoint2,
            String cableName,
            List<ItemProject> projectList,
            List<ItemCategory> technicalSystemList) {

        ItemDomainCableDesign newCable = this.createCableCommon(itemEndpoint1,
                itemEndpoint2,
                cableName,
                projectList,
                technicalSystemList);

        if (this.create() == null) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Creates cable design of specified cable catalog type
     * connecting the specified endpoints.
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
            List<ItemCategory> technicalSystemList,
            Item itemCableCatalog) {

        ItemDomainCableDesign newCable = this.createCableCommon(itemEndpoint1,
                itemEndpoint2,
                cableName,
                projectList,
                technicalSystemList);

        newCable.setCatalogItem(itemCableCatalog);

        if (this.create() == null) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Prepares endpoint dialog for editing endpoint1.
     */
    public void prepareDialogCatalog() {
        dialogCatalog.reset();
        dialogCatalog.setItemCatalog(getCurrent().getCatalogItem());
    }

    /**
     * Prepares endpoint dialog for editing endpoint1.
     */
    public void prepareDialogEndpoint1() {
        dialogEndpoint.reset();
        dialogEndpoint.setItemEndpoint(getCurrent().getEndpoint1());
    }

    /**
     * Prepares endpoint dialog for editing endpoint2.
     */
    public void prepareDialogEndpoint2() {
        dialogEndpoint.reset();
        dialogEndpoint.setItemEndpoint(getCurrent().getEndpoint2());
    }
    
    /**
     * Prepares cable wizard.
     */
    public String prepareWizardCable() { 
        CableWizard.getInstance().reset();
        return "/views/itemDomainCableDesign/create?faces-redirect=true";
    }

    /**
     * Prepares import wizard.
     */
    public String prepareWizardCircuit() {
        CircuitWizard.getInstance().reset();
        return "/views/itemDomainCableDesign/createCircuit?faces-redirect=true";
    }

    /**
     * Prepares import wizard.
     */
    public String prepareWizardBundle() {        
        BundleWizard.getInstance().reset();
        return "/views/itemDomainCableDesign/createBundle?faces-redirect=true";
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
    public String getDefaultDomainName() {
        return ItemDomainName.cableDesign.getValue();
    }   

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
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
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
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
    
    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        formatInfo.add(new ImportExportFormatInfo("Basic Cable Design Create/Update Format", ImportHelperCableDesign.class));
        
        String completionUrl = "/views/itemDomainCableDesign/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }

    @Override
    public boolean getEntityDisplayExportButton() {
        return true;
    }
    
    @Override
    protected DomainImportExportInfo initializeDomainExportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        
        formatInfo.add(new ImportExportFormatInfo("Basic Cable Design Create/Update Format", ImportHelperCableDesign.class));
        
        String completionUrl = "/views/itemDomainCableDesign/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
}
