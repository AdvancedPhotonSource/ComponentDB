/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.extensions.CableWizard;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCableDesign;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableDesignControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCableDesignConnections;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCableDesignPullList;
import gov.anl.aps.cdb.portal.model.ItemDomainCableDesignLazyDataModel;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.CableDesignConnectionListObject;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author cmcchesney
 */
@Named(ItemDomainCableDesignController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableDesignController extends ItemController<ItemDomainCableDesignControllerUtility, ItemDomainCableDesign, ItemDomainCableDesignFacade, ItemDomainCableDesignSettings, ItemDomainCableDesignLazyDataModel> {

    @Override
    protected ItemDomainCableDesignControllerUtility createControllerUtilityInstance() {
        return new ItemDomainCableDesignControllerUtility(); 
    }

    public class ConnectionEditDialog {
        
        protected static final String DEFAULT_MESSAGE = "Cable end and device or device port are required. Cable connector is optional.";

        private ItemElementRelationship cableRelationship;
        private Boolean disableButtonSave = true;
        private Item origMdItem = null;
        private ItemDomainMachineDesignTreeNode mdTree = null;
        private TreeNode selectedMdTreeNode = null;
        protected Item selectedMdItem;
        protected ItemConnector selectedMdConnector;
        private ItemConnector origMdConnector;
        private ItemConnector origCableConnector;
        private ItemConnector selectedCableConnector;
        private Map<String, ItemConnector> cableConnectorNameMap;
        private List<String> availableCableConnectorNames;
        private String selectedCableConnectorName;
        private String message;
        private String warningMessage = null;
        protected String cableEndDesignation = null;
        boolean selectionMade = false;
        
        public String getMenuValueEnd1() {
            return CdbEntity.VALUE_CABLE_END_1;
        }

        public String getMenuValueEnd2() {
            return CdbEntity.VALUE_CABLE_END_2;
        }
        
        public String getMenuLabelEnd1() {
            return CdbEntity.LABEL_CABLE_END_1;
        }

        public String getMenuLabelEnd2() {
            return CdbEntity.LABEL_CABLE_END_2;
        }
        
        public String getCableEndDesignation() {
            return cableEndDesignation;
        }
        
        public void setCableEndDesignation(String cableEndDesignation) {
            this.cableEndDesignation = cableEndDesignation;
        }
        
        public void selectListenerCableEnd(AjaxBehaviorEvent event) {
            selectionMade = true;
            updateCableConnectorMenu();
            setEnablement();
        }

        public boolean isRenderCableEndDesignation() {
            return true;
        }
        
        public boolean isRenderCableEndDesignationLabelDetail() {
            if (getCableRelationship() != null) {
                return (!getCableRelationship().isPrimaryCableConnection());  
            } else {
                return true;
            }
        }
        
        public boolean isRenderCableEndDesignationLabelPrimary() {
            if (getCableRelationship() != null) {
                return getCableRelationship().isPrimaryCableConnection();  
            } else {
                return false;
            }
        }
        
        public boolean isDisableCableEndDesignation() {
            if (getCableRelationship() != null) {
                return getCableRelationship().isPrimaryCableConnection();  
            } else {
                return false;
            }
        }

        public ItemElementRelationship getCableRelationship() {
            return cableRelationship;
        }

        public void setCableRelationship(ItemElementRelationship cableRelationship) {
            this.cableRelationship = cableRelationship;
        }
        
        public Boolean getDisableButtonSave() {
            return disableButtonSave;
        }

        public void setDisableButtonSave(Boolean disableButtonSave) {
            this.disableButtonSave = disableButtonSave;
        }

        public Item getOrigMdItem() {
            return origMdItem;
        }

        public void setOrigMdItem(Item origMdItem) {
            this.origMdItem = origMdItem;
            selectedMdItem = origMdItem;
        }

        public String getItemEndpointString() {
            if (origMdItem == null) {
                return "";
            } else {
                return origMdItem.getName();
            }
        }

        public ItemDomainMachineDesignTreeNode getMdTree() {
            if (mdTree == null) {
                ItemDomainMachineDesignController controller = ItemDomainMachineDesignController.getInstance();                
                mdTree = controller.loadMachineDesignRootTreeNode();
                ItemDomainMachineDesignTreeNode.MachineTreeConfiguration config = mdTree.getConfig();
                config.setShowConnectorsOnly(true);
            }
            return mdTree;
        }

        public void setMdTree(ItemDomainMachineDesignTreeNode mdTree) {
            this.mdTree = mdTree;
        }

        public TreeNode getSelectedMdTreeNode() {
            return selectedMdTreeNode;
        }

        public void setSelectedMdTreeNode(TreeNode selectedMdTreeNode) {
            this.selectedMdTreeNode = selectedMdTreeNode;
        }

        /**
         * Handles select events generated by the machine design tree table
         * component. Must call client side remoteCommand to update button state
         * from oncomplete attribute for this event tag.
         */
        public void selectListenerEndpoint(NodeSelectEvent event) {
            selectionMade = true;
            setSelectedEndpointAndConnector();
            setEnablement();
        }
        
        public void selectListenerEndpoint(NodeUnselectEvent event) {
            setSelectedEndpointAndConnector();
            setEnablement();
        }
        
        public ItemConnector getOrigMdConnector() {
            return origMdConnector;
        }

        public void setOrigMdConnector(ItemConnector origMdConnector) {
            this.origMdConnector = origMdConnector;
            selectedMdConnector = origMdConnector;
        }

        public ItemConnector getOrigCableConnector() {
            return origCableConnector;
        }

        public void setOrigCableConnector(ItemConnector origCableConnector) {
            this.origCableConnector = origCableConnector;
            setSelectedCableConnector(origCableConnector);
        }

        public ItemConnector getSelectedCableConnector() {
            return selectedCableConnector;
        }

        public void setSelectedCableConnector(ItemConnector selectedCableConnector) {
            this.selectedCableConnector = selectedCableConnector;
        }

        private void updateCableConnectorMenu() {
            
            availableCableConnectorNames = new ArrayList<>();
            cableConnectorNameMap = new HashMap<>();
            
            // null out selection and return empty list of conneectors if cable end not selected
            if ((cableEndDesignation == null) || cableEndDesignation.isEmpty()) {
                selectedCableConnector = null;
                
            } else {
            
                // get list of unmapped connectors, plus connector for this connection if any
                List<ItemConnector> availableConnectors = getUnmappedConnectorsForCurrent(cableEndDesignation);
                if ((origCableConnector != null) && 
                        (origCableConnector.getConnector().getCableEndDesignation().equals(cableEndDesignation))) {
                    availableConnectors.add(origCableConnector);
                }

                for (ItemConnector connector : availableConnectors) {
                    String connectorName = connector.getConnector().toString();
                    availableCableConnectorNames.add(connectorName);
                    cableConnectorNameMap.put(connectorName, connector);
                }

                // set selected item and original item
                if (selectedCableConnector != null) {
                    String currentConnectorName = selectedCableConnector.getConnector().toString();
                    setSelectedCableConnectorName(currentConnectorName);
                } else {
                    setSelectedCableConnectorName("");
                }
            }
        }

        public List<String> getAvailableCableConnectorNames() {
            if (availableCableConnectorNames == null) {
                if (cableEndDesignation != null) {
                    updateCableConnectorMenu();
                } else {
                    return new ArrayList<>();
                }
            }
            return availableCableConnectorNames;
        }

        public String getSelectedCableConnectorName() {
            return selectedCableConnectorName;
        }

        public void setSelectedCableConnectorName(String selectedCableConnectorName) {
            this.selectedCableConnectorName = selectedCableConnectorName;
        }

        public void selectListenerConnector(SelectEvent event){
            selectionMade = true;
            if ((selectedCableConnectorName != null) && (!selectedCableConnectorName.isBlank()))  {
                setSelectedCableConnector(cableConnectorNameMap.get(selectedCableConnectorName));
            } else {
                setSelectedCableConnector(null);
            }
            setEnablement();
        }
        
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getWarningMessage() {
            return warningMessage;
        }

        public void setWarningMessage(String message) {
            this.warningMessage = "<span style=\"color:red\">" + message + "</span>";
        }
        
        protected void setMessages(List<String> warnings, List<String> messages) {
            
            if (selectionMade) {
                String warningMsg = "";
                for (String warning : warnings) {
                    warningMsg = warningMsg + warning + " ";
                }
                setWarningMessage(warningMsg);
            }

            String msg = "";
            for (String message : messages) {
                msg = msg + message + " ";
            }
            if (msg.isEmpty()) {
                setMessage(DEFAULT_MESSAGE);
            } else {
                setMessage(msg);
            }
        }

        private void setSelectedEndpointAndConnector() {
            selectedMdItem = null;
            selectedMdConnector = null;
            if (selectedMdTreeNode != null) {
                if (this.selectedMdTreeNode.getType().equals("Connector")) {
                    selectedMdConnector = ((ItemElement) (this.selectedMdTreeNode.getData())).getMdConnector();
                    selectedMdItem = selectedMdConnector.getItem();
                } else {
                    selectedMdItem = ((ItemElement) (this.selectedMdTreeNode.getData())).getContainedItem();
                }
            }
        }
        
        /**
         * Resets the dialog components when closing.
         */
        public void reset() {            
            setCableRelationship(null);
            setOrigMdItem(null);
            setMdTree(null);
            setSelectedMdTreeNode(null);
            setOrigMdConnector(null);
            setOrigCableConnector(null);
            setSelectedCableConnector(null);
            setSelectedCableConnectorName(null);
            availableCableConnectorNames = null;
            cableConnectorNameMap = null;
            setSelectedCableConnectorName(null);
            cableEndDesignation = null;
            setMessage(DEFAULT_MESSAGE);
            warningMessage = null;
            setEnablement();            
        }

        private void expandTreeAndSelectNode() {
            ItemDomainMachineDesignTreeNode machineDesignTreeRootTreeNode = getMdTree();
            if (selectedMdTreeNode != null) {
                selectedMdTreeNode.setSelected(false);
                selectedMdTreeNode = null;
            }           
            selectedMdTreeNode = ItemDomainMachineDesignController.expandToItemOrPort(
                    machineDesignTreeRootTreeNode, (ItemDomainMachineDesign) getOrigMdItem(), getOrigMdConnector());
        }
        
        /**
         * Enables save button when a valid selection based on selections.
         */
        protected void setEnablement() {
            
            List<String> messages = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            boolean disableSaveButton = false;            
            
            // check cable end
            if (cableEndDesignation == null) {
                warnings.add("Cable End is required.");
                disableSaveButton = true;
            }            

            // check device/port
            if (selectedMdTreeNode == null) {
                warnings.add("Device or device port is required.");
                setDisableButtonSave((Boolean) true);

            } else {
                
                ItemConnector selCableConnector = getSelectedCableConnector();
                
                if ((((selectedMdItem == null) && (getOrigMdItem() == null)) || ((selectedMdItem != null) && (selectedMdItem.equals(getOrigMdItem()))))
                        && (((selectedMdConnector == null) && (getOrigMdConnector() == null)) || ((selectedMdConnector != null) && (selectedMdConnector.equals(getOrigMdConnector()))))
                        && (((selCableConnector == null) && (getOrigCableConnector() == null)) || ((selCableConnector != null) && (selCableConnector.equals(getOrigCableConnector()))))) {
                    
                    setDisableButtonSave((Boolean) true);
                    
                } else {
                    if ((selectedMdConnector != null) && (!selectedMdConnector.equals(origMdConnector))) {
                        // removed check that the new connector didn't already have cable connections,
                        // ned wants to allow multiple connections to same port
                        messages.add("Selected device and device port are valid.");
                    } else if ((selectedMdItem != null) 
                            && ((!selectedMdItem.equals(origMdItem)) 
                                || ((selectedMdItem.equals(origMdItem)) && (selectedMdConnector == null)))) {
                            disableSaveButton = false;
                            messages.add("Selected device is valid, optional device port not selected.");
                    } else if (((selCableConnector == null) && (getOrigCableConnector() != null)) || ((selCableConnector != null) && (!selCableConnector.equals(getOrigCableConnector())))) {
                        disableSaveButton = false;
                    } else {
                        disableSaveButton = true;
                    }
                }
            }

            setDisableButtonSave((Boolean) disableSaveButton);
            setMessages(warnings, messages);
        }
        
        protected void updateItem(String remoteCommandSuccess) {
            String updateResult = update();
            refreshConnectionListForCurrent(true);
            SessionUtility.executeRemoteCommand(remoteCommandSuccess);

        }

        public String save(String remoteCommandSuccess) {
            
            // make sure we don't change cable end for primary connection
            if (getCableRelationship().isPrimaryCableConnection()) {
                if (!getCableRelationship().getCableEndDesignation().equals(cableEndDesignation)) {
                    SessionUtility.addErrorMessage("Error", "Cable End cannot be updated for primarry connection.");
                    return null;
                }
            }

            getCurrent().updateCableRelationship(
                    getCableRelationship(),
                    selectedMdItem,
                    selectedMdConnector,
                    getSelectedCableConnector(),
                    cableEndDesignation);
            
            updateItem(remoteCommandSuccess);

            return null;
        }

        public void actionListenerSaveSuccess() {
        }

        public void cancel() {
        }
    }

    public class ConnectionCreateDialog extends ConnectionEditDialog {
        
        /**
         * Enables save button when a valid selection based on selections.
         */
        @Override
        protected void setEnablement() {

            List<String> messages = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            boolean disableSaveButton = false;            
            
            // check cable end
            if (cableEndDesignation == null) {
                warnings.add("Cable End is required.");
                disableSaveButton = true;
            }            

            // check device/port
            if (selectedMdItem == null) {
                warnings.add("Device or device port is required.");
                disableSaveButton = true;

            } else {

                if (selectedMdConnector != null) {
                    messages.add("Selected device and device port are valid.");
                } else if (selectedMdItem != null) {
                    messages.add("Selected device is valid, optional device port not selected.");
                } else {
                    messages.add(DEFAULT_MESSAGE);
                    disableSaveButton = true;
                }
            }
            
            setDisableButtonSave((Boolean) disableSaveButton);
            setMessages(warnings, messages);

        }
        
        @Override
        public String save(String remoteCommandSuccess) {  
            
            ItemElementRelationship ier = 
                    getCurrent().addCableRelationship(
                            selectedMdItem, 
                            selectedMdConnector, 
                            getSelectedCableConnector(), 
                            cableEndDesignation, 
                            false);
            
            updateItem(remoteCommandSuccess);
            
            return null;
        }

    }
    
    public class CatalogDialog {

        private Boolean disableButtonSave = true;
        private ItemDomainCableCatalog itemCatalog = null;
        private ItemDomainCableCatalog selectionModelCatalog = null;
        
        public Boolean getDisableButtonSave() {
            return disableButtonSave;
        }

        public void setDisableButtonSave(Boolean disableButtonSave) {
            this.disableButtonSave = disableButtonSave;
        }

        public ItemDomainCableCatalog getItemCatalog() {
            return itemCatalog;
        }

        public void setItemCatalog(ItemDomainCableCatalog itemCatalog) {
            this.itemCatalog = itemCatalog;
            selectCatalogItem(itemCatalog);
        }

        private void selectCatalogItem(ItemDomainCableCatalog item) {
            selectionModelCatalog = item;
        }

        public String getItemCatalogString() {
            if (itemCatalog == null) {
                return "";
            } else {
                return itemCatalog.getName();
            }
        }

        public ItemDomainCableCatalog getSelectionModelCatalog() {
            return selectionModelCatalog;
        }

        public void setSelectionModelCatalog(ItemDomainCableCatalog selectionModelCatalog) {
            this.selectionModelCatalog = selectionModelCatalog;
        }

        public void selectListenerCatalog(SelectEvent<ItemDomainCableCatalog> event) {
            setEnablement();
        }

        public void unselectListenerCatalog(UnselectEvent<ItemDomainCableCatalog> event) {
            setEnablement();
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
            
            boolean changedCableType =
                    ((selectionModelCatalog == null) && (getItemCatalog() != null))
                    || ((selectionModelCatalog != null) && (getItemCatalog() == null))
                    || ((selectionModelCatalog != null) && (!selectionModelCatalog.equals(getItemCatalog())));

            if (changedCableType) {
                setDisableButtonSave((Boolean) false);
            } else {
                setDisableButtonSave((Boolean) true);
            }
        }

        /**
         * Determines whether catalog item changed, and calls update if it did.
         */
        public String save(String remoteCommandSuccess) {

            boolean changedCableType
                    = ((selectionModelCatalog == null) && (getItemCatalog() != null))
                    || ((selectionModelCatalog != null) && (getItemCatalog() == null))
                    || ((selectionModelCatalog != null) && (!selectionModelCatalog.equals(getItemCatalog())));

            // catalog item changed, update cable and save
            if (changedCableType) {
                
                UserInfo user = SessionUtility.getUser();
                ItemDomainCableDesignControllerUtility utility = getControllerUtility();
                
                try {
                    utility.updateAssignedItem(getCurrent(), selectionModelCatalog, user, null);
                } catch (CdbException ex) {
                    SessionUtility.addErrorMessage("Error", ex.getMessage());
                    return null;
                }

                String updateResult = update();

                // An error occured, reload the page with correct information. 
                if (updateResult == null) {                    
                    return null;
                }

                SessionUtility.executeRemoteCommand(remoteCommandSuccess);
            }
            return null;
        }

        public void cancel() {
        }

        public void actionListenerSaveSuccess() {
        }

    }

    public class InventoryDialog {

        private Boolean disableButtonSave = true;
        private ItemDomainCableInventory itemInventory = null;
        private ItemDomainCableCatalog itemCatalog = null;
        private ItemDomainCableInventory selectionModelInventory = null;
        private Boolean inventoryIsInstalled = null;
        private Boolean selectionModelIsInstalled = null;
        
        public Boolean getDisableButtonSave() {
            return disableButtonSave;
        }

        public void setDisableButtonSave(Boolean disableButtonSave) {
            this.disableButtonSave = disableButtonSave;
        }

        public ItemDomainCableInventory getItemInventory() {
            return itemInventory;
        }

        public void setItemInventory(ItemDomainCableInventory itemInventory) {
            this.itemInventory = itemInventory;
            selectInventoryItem(itemInventory);
        }

        public void setItemCatalog(ItemDomainCableCatalog itemCatalog) {
            this.itemCatalog = itemCatalog;
        }
        
        public ItemDomainCableCatalog getItemCatalog() {
            return this.itemCatalog;
        }

        public String getItemInventoryString() {
            if (itemInventory == null) {
                return "";
            } else {
                return itemInventory.getName();
            }
        }

        private void selectInventoryItem(ItemDomainCableInventory item) {
            selectionModelInventory = item;
        }

        public ItemDomainCableInventory getSelectionModelInventory() {
            return selectionModelInventory;
        }

        public void setSelectionModelInventory(ItemDomainCableInventory selectionModelInventory) {
            this.selectionModelInventory = selectionModelInventory;
        }
        
        public void selectListenerInventory(SelectEvent<ItemDomainCableInventory> event) {
            setEnablement();
        }

        public void unselectListenerInventory(UnselectEvent<ItemDomainCableInventory> event) {
            setEnablement();
        }

        public Boolean getInventoryIsInstalled() {
            return inventoryIsInstalled;
        }
        
        public void setInventoryIsInstalled(Boolean isInstalled) {
            inventoryIsInstalled = isInstalled;
            setSelectionModelIsInstalled(isInstalled);
        }

        public Boolean getSelectionModelIsInstalled() {
            return selectionModelIsInstalled;
        }

        public void setSelectionModelIsInstalled(Boolean isInstalled) {
            selectionModelIsInstalled = isInstalled;
        }

        public void selectListenerIsInstalled() {
            setEnablement();
        }

        /**
         * Resets the dialog components when closing.
         */
        public void reset() {
            setSelectionModelInventory(null);
        }

        /**
         * Enables save button when a valid selection, different from the
         * original inventory item, is made.
         */
        private void setEnablement() {

            boolean changedInventory
                    = ((selectionModelInventory == null) && (getItemInventory() != null))
                    || ((selectionModelInventory != null) && (getItemInventory() == null))
                    || ((selectionModelInventory != null) && (!selectionModelInventory.equals(getItemInventory())));

            boolean changedInstalledStatus
                    = ((selectionModelIsInstalled == null) && (getInventoryIsInstalled() != null))
                    || ((selectionModelIsInstalled != null) && (getInventoryIsInstalled() == null))
                    || ((selectionModelIsInstalled != null) && (!selectionModelIsInstalled.equals(getInventoryIsInstalled())));

            if (changedInventory || changedInstalledStatus) {
                setDisableButtonSave((Boolean) false);
            } else {
                setDisableButtonSave((Boolean) true);
            }
        }

        /**
         * Determines whether inventory item changed, and calls update if it did.
         */
        public String save(String remoteCommandSuccess) {

            boolean changedInventory
                    = ((selectionModelInventory == null) && (getItemInventory() != null))
                    || ((selectionModelInventory != null) && (getItemInventory() == null))
                    || ((selectionModelInventory != null) && (!selectionModelInventory.equals(getItemInventory())));

            boolean changedInstalledStatus
                    = ((selectionModelIsInstalled == null) && (getInventoryIsInstalled() != null))
                    || ((selectionModelIsInstalled != null) && (getInventoryIsInstalled() == null))
                    || ((selectionModelIsInstalled != null) && (!selectionModelIsInstalled.equals(getInventoryIsInstalled())));

            if (changedInventory || changedInstalledStatus) {
                
                Boolean installationStatus = selectionModelIsInstalled;
                Item assignedItem = selectionModelInventory;
                
                if (selectionModelInventory == null) {
                    
                    // use null installationStatus if no inventory item is selected
                    if (installationStatus != null) {
                        installationStatus = null;
                    }
                    
                    assignedItem = getItemCatalog();
                }
                
                UserInfo user = SessionUtility.getUser();
                ItemDomainCableDesignControllerUtility utility = getControllerUtility();

                try {
                    utility.updateAssignedItem(getCurrent(), assignedItem, user, installationStatus);
                } catch (CdbException ex) {
                    SessionUtility.addErrorMessage("Error", ex.getMessage());
                    return null;
                }
                
                String updateResult = update();

                // An error occured, reload the page with correct information. 
                if (updateResult == null) {                    
                    return null;
                }

                SessionUtility.executeRemoteCommand(remoteCommandSuccess);
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

    private CatalogDialog dialogCatalog = null;
    private InventoryDialog dialogInventory = null;
    private ConnectionEditDialog dialogConnection = null;
    
    private List<CableDesignConnectionListObject> connectionListForCurrent;       

    protected ImportHelperCableDesign importHelper = new ImportHelperCableDesign();
    
    private transient CableDesignConnectionListObject connectionToDelete = null;
    
    public CatalogDialog getDialogCatalog() {
        if (dialogCatalog == null) {
            dialogCatalog = new CatalogDialog();
        }
        return dialogCatalog;
    }

    public void setDialogCatalog(CatalogDialog dialogCatalog) {
        this.dialogCatalog = dialogCatalog;
    }

    public InventoryDialog getDialogInventory() {
        if (dialogInventory == null) {
            dialogInventory = new InventoryDialog();
        }
        return dialogInventory;
    }

    public void setDialogInventory(InventoryDialog dialogInventory) {
        this.dialogInventory = dialogInventory;
    }

    public ConnectionEditDialog getDialogConnection() {
        if (dialogConnection == null) {
            dialogConnection = new ConnectionEditDialog();
        }
        return dialogConnection;
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
    public ItemDomainCableDesign createCableCommon(
            String cableName,
            List<ItemProject> projectList,
            List<ItemCategory> technicalSystemList) {

        ItemDomainCableDesign newCable = this.createEntityInstance();
        newCable.setName(cableName);
        newCable.setItemProjectList(projectList);
        newCable.setTechnicalSystemList(technicalSystemList);

        return newCable;
    }

    public void prepareDialogCatalog() {
        dialogCatalog.reset();
        dialogCatalog.setItemCatalog(getCurrent().getCatalogItem());
    }

    public boolean isRenderInventoryEdit() {
        return getCurrent().getCatalogItem() != null;
    }

    public void prepareDialogInventory() {
        dialogInventory.reset();
        dialogInventory.setItemInventory(getCurrent().getInventoryItem());
        dialogInventory.setItemCatalog(getCurrent().getCatalogItem());
        dialogInventory.setInventoryIsInstalled(getCurrent().isIsHoused());
    }

    public DataModel getInventoryForCableType() {
        ItemDomainCableCatalog catalogItem = getCurrent().getCatalogItem();
        if (catalogItem != null) {
            List<Item> inventoryList = catalogItem.getDerivedFromItemList();
            return new ListDataModel(inventoryList);
        } else {
            return null;
        }
    }

    public void prepareEditConnection(CableDesignConnectionListObject connection) {
        
        dialogConnection = new ConnectionEditDialog();
        dialogConnection.reset();
        
        dialogConnection.setOrigMdItem(connection.getMdItem());
        dialogConnection.setOrigMdConnector(connection.getMdConnector());
        dialogConnection.setCableRelationship(connection.getCableRelationship());
        String cableEnd = connection.getCableRelationship().getCableEndDesignation();
        dialogConnection.setCableEndDesignation(cableEnd);
        if (connection.getItemConnector() != null) {
            dialogConnection.setOrigCableConnector(connection.getItemConnector());
        }

        // expand MD tree to specified md item and connector
        dialogConnection.expandTreeAndSelectNode();
        
    }
    
    public void prepareAddConnection() {        
        dialogConnection = new ConnectionCreateDialog();
        dialogConnection.setCableEndDesignation(null);
        dialogConnection.reset();
    }
    
    public void prepareDeleteConnection(CableDesignConnectionListObject connection) {
        connectionToDelete = connection;
    }
    
    public CableDesignConnectionListObject getConnectionToDelete() {
        return connectionToDelete;
    }
    
    public Boolean renderEditLinkForConnection(CableDesignConnectionListObject connection) {
        return (connection.getMdItem() != null);
    }
    
    public Boolean renderDeleteLinkForConnection(CableDesignConnectionListObject connection) {
        return (!connection.getCableRelationship().isPrimaryCableConnection());
    }
    
    @Override
    public String prepareCreate() {
        return prepareWizardCable();
    }
    
        /**
     * Prepares cable wizard.
     */
    public String prepareWizardCable() { 
        CableWizard.getInstance().reset();
        return "/views/itemDomainCableDesign/create?faces-redirect=true";
    }

    @Override
    public ItemDomainCableDesignLazyDataModel createItemLazyDataModel() {
        return new ItemDomainCableDesignLazyDataModel(itemDomainCableDesignFacade, getDefaultDomain(), settingObject); 
    }        
    
    @Override
    protected Boolean fetchFilterablePropertyValue(Integer propertyTypeId) {
        return true;
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
        return false;
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
    
    public List<CableDesignConnectionListObject> getConnectionListForCurrent() {
        refreshConnectionListForCurrent();
        return connectionListForCurrent;
    }
    
    public List<ItemConnector> getUnmappedConnectorsForCurrent() {
        return getUnmappedConnectorsForCurrent(null);
    }
    
    public List<ItemConnector> getUnmappedConnectorsForCurrent(String cableEnd) {
        return getUnmappedConnectorsForCableItem(getCurrent(), cableEnd);
    }

    public List<ItemConnector> getUnmappedConnectorsForCableItem(ItemDomainCableDesign cableItem, String cableEnd) {
        
        List<ItemConnector> result = new ArrayList<>();
        boolean filterCableEnd = (cableEnd != null);
        List<ItemConnector> unmappedConnectors = cableItem.getSyncedConnectorList();
        
        // filter cable end if specified to do so
        if (filterCableEnd) {
            // return empty list if filtering but end not specified
            if ((cableEnd == null) || (cableEnd.isEmpty())) {
                return result;
            }
            for (ItemConnector c : unmappedConnectors) {
                if (c.getConnector().getCableEndDesignation().equals(cableEnd)) {
                    result.add(c);
                }
            }
        } else {
            result.addAll(unmappedConnectors);
        }
        
        // sort by end, device name, device port name, cable connector name
        Comparator<ItemConnector> comparator
                = Comparator
                        .comparing((ItemConnector c) -> c.getConnectorCableEndDesignation())
                        .thenComparing(c -> c.getConnectorName().toLowerCase());
        result = result.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        return result;
    }

    public List<CableDesignConnectionListObject> getConnectionListForItem(ItemDomainCableDesign item) {
        return CableDesignConnectionListObject.getConnectionList(item);
    }
    
    private void refreshConnectionListForCurrent() {
        refreshConnectionListForCurrent(false);
    }

    private void refreshConnectionListForCurrent(boolean reload) {
        if (reload) {
            reloadCurrent();
        }
        ItemDomainCableDesign item = getCurrent();
        connectionListForCurrent = getConnectionListForItem(item);
    }

    public boolean getDisplayConnectionsList() {
        return getConnectionListForCurrent().size() > 0;
    }
    
    public boolean getDisplayConnectorsList() {
        return getUnmappedConnectorsForCurrent().size() > 0;
    }
    
    @Override
    public boolean getEntityDisplayConnectorCableEndDesignation() {
        return true; 
    }    
    
    public void deleteConnection(CableDesignConnectionListObject connection) {
        
        // can't delete primary connection
        ItemElementRelationship cableRelationship = connection.getCableRelationship();
        if (cableRelationship.isPrimaryCableConnection()) {
            SessionUtility.addErrorMessage("Error", "Primary connection cannot be deleted.");
            return;
        }

        getCurrent().deleteCableRelationship(cableRelationship);
        
        // remove connectors from itemConnectorList for cable and endpoint
        ItemConnector cableConnector = cableRelationship.getSecondItemConnector();
        if (cableConnector != null) {
            getCurrent().getItemConnectorList().remove(cableConnector);
        }
        ItemConnector deviceConnector = cableRelationship.getFirstItemConnector();
        if (deviceConnector != null) {
            deviceConnector.getItem().getItemConnectorList().remove(deviceConnector);
        }

        String updateResult = update();
        refreshConnectionListForCurrent(true);
        connectionToDelete = null;
    }

    // <editor-fold defaultstate="collapsed" desc="import/export support">   
    
    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        
        formatInfo.add(new ImportExportFormatInfo(
                "Basic Cable Design Create/Update/Delete Format", ImportHelperCableDesign.class));

        formatInfo.add(new ImportExportFormatInfo(
                "Cable Design Connections Format", 
                ImportHelperCableDesignConnections.class));
        
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
        
        formatInfo.add(new ImportExportFormatInfo("Basic Cable Design Create/Update/Delete Format", ImportHelperCableDesign.class));
        formatInfo.add(new ImportExportFormatInfo("Cable Design Connections Format", ImportHelperCableDesignConnections.class));
        formatInfo.add(new ImportExportFormatInfo("Cable Design Pull List Extract Format", ImportHelperCableDesignPullList.class));
        
        String completionUrl = "/views/itemDomainCableDesign/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
    // </editor-fold>   
}
