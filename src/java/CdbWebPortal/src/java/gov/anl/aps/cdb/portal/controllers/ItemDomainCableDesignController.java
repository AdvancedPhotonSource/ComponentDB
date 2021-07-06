/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.extensions.BundleWizard;
import gov.anl.aps.cdb.portal.controllers.extensions.CableWizard;
import gov.anl.aps.cdb.portal.controllers.extensions.CircuitWizard;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCableDesign;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainCableDesignLazyDataModel;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.CableDesignConnectionListObject;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
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
        private ItemDomainMachineDesignTreeNode valueModelTree = null;
        private TreeNode selectionModelEndpoint = null;
        private Integer sortOrder;
        
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

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public ItemDomainMachineDesignTreeNode getValueModelTree() {
            if (valueModelTree == null) {
                ItemDomainMachineDesignController controller = ItemDomainMachineDesignController.getInstance();
                valueModelTree = controller.loadMachineDesignRootTreeNode();
            }
            return valueModelTree;
        }

        public void setValueModelTree(ItemDomainMachineDesignTreeNode valueModelTree) {
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
                
                getCurrent().setEndpoint(selectedItemEndpoint, null, null, getSortOrder());

                String updateResult = update();

                // An error occured, reload the page with correct information. 
                if (updateResult == null) {
                    reloadCurrent();
                    return view();
                }

                refreshConnectionListForCurrent();

                SessionUtility.executeRemoteCommand(remoteCommandSuccess);
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

            ItemDomainMachineDesignTreeNode machineDesignTreeRootTreeNode = getValueModelTree();

            if (selectionModelEndpoint != null) {
                selectionModelEndpoint.setSelected(false);
                selectionModelEndpoint = null;
            }

            TreeNode selectedNode = ItemDomainMachineDesignController.
                    expandToSpecificMachineDesignItem(machineDesignTreeRootTreeNode, item);
            selectionModelEndpoint = selectedNode;
        }

    }

    public class ConnectionEditDialog {
        
        protected static final String DEFAULT_MESSAGE = "Select endpoint item, and optionally cable and endpoint connectors.";

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
            setSelectedEndpointAndConnector();
            setEnablement();
        }

        public ItemConnector getOrigMdConnector() {
            return origMdConnector;
        }

        public void setOrigMdConnector(ItemConnector origMdConnector) {
            this.origMdConnector = origMdConnector;
        }

        public ItemConnector getOrigCableConnector() {
            return origCableConnector;
        }

        public void setOrigCableConnector(ItemConnector origCableConnector) {
            this.origCableConnector = origCableConnector;
        }

        public ItemConnector getSelectedCableConnector() {
            return selectedCableConnector;
        }

        public void setSelectedCableConnector(ItemConnector selectedCableConnector) {
            this.selectedCableConnector = selectedCableConnector;
        }

        /**
         * Prepares list of available cable connectors to use as model for menu,
         * and map for looking up connector by name when menu selection changes.
         * This is called in preparing the dialog. This is to work around issue
         * where newly cloned connector doesn't have an id, which causes an 
         * exception in the framework code trying to look up connector by id
         * when the menu selection changes.
        */
        public void setAvailableCableConnectors(
                List<ItemConnector> availableCableConnectors,
                ItemConnector currentConnector) {
            
            availableCableConnectorNames = new ArrayList<>();
            cableConnectorNameMap = new HashMap<>();
            for (ItemConnector connector : availableCableConnectors) {
                String connectorName = connector.getConnector().toString();
                availableCableConnectorNames.add(connectorName);
                cableConnectorNameMap.put(connectorName, connector);
            }
            
            // set selected item and original item
            setOrigCableConnector(currentConnector);
            if (currentConnector != null) {
                String currentConnectorName = currentConnector.getConnector().toString();
                setSelectedCableConnectorName(currentConnectorName);
            } else {
                setSelectedCableConnectorName("");
            }
            
        }

        public List<String> getAvailableCableConnectorNames() {
            return availableCableConnectorNames;
        }

        public String getSelectedCableConnectorName() {
            return selectedCableConnectorName;
        }

        public void setSelectedCableConnectorName(String selectedCableConnectorName) {
            this.selectedCableConnectorName = selectedCableConnectorName;
        }

        public void selectListenerConnector(SelectEvent event){
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

        public void setWarningMessage(String message) {
            this.message = "<span style=\"color:red\">" + message + "</span>";
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
            setMessage(DEFAULT_MESSAGE);
            setEnablement();            
        }

        private void expandTreeAndSelectNode() {

            ItemDomainMachineDesignTreeNode machineDesignTreeRootTreeNode = getMdTree();

            if (selectedMdTreeNode != null) {
                selectedMdTreeNode.setSelected(false);
                selectedMdTreeNode = null;
            }

            TreeNode selectedNode = ItemDomainMachineDesignController.expandToSpecificMachineDesignItem(
                    machineDesignTreeRootTreeNode, 
                    (ItemDomainMachineDesign)getOrigMdItem());
            
            selectedMdTreeNode = selectedNode;
            
            if ((selectedNode != null) && (getOrigMdConnector() != null)) {
                selectedNode.setSelected(false);
                selectedNode.setExpanded(true);
                List<TreeNode> children = selectedNode.getChildren();
                for (TreeNode child : children) {
                    if (child.getType().equals("Connector")) {
                        ItemConnector connectorChild = 
                                ((ItemElement) (child.getData())).getMdConnector();
                        if (connectorChild.equals(getOrigMdConnector())) {
                            child.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }
        
        /**
         * Enables save button when a valid selection based on selections.
         */
        protected void setEnablement() {

            if (selectedMdTreeNode == null) {

                setDisableButtonSave((Boolean) true);

            } else {
                
                ItemConnector selectedCableConnector = getSelectedCableConnector();
                
                if ((((selectedMdItem == null) && (getOrigMdItem() == null)) || ((selectedMdItem != null) && (selectedMdItem.equals(getOrigMdItem()))))
                        && (((selectedMdConnector == null) && (getOrigMdConnector() == null)) || ((selectedMdConnector != null) &&(selectedMdConnector.equals(getOrigMdConnector()))))
                        && (((selectedCableConnector == null) && (getOrigCableConnector() == null)) || ((selectedCableConnector != null) && (selectedCableConnector.equals(getOrigCableConnector()))))) {
                    
                    setDisableButtonSave((Boolean) true);
                    setMessage(DEFAULT_MESSAGE);
                    
                } else {
                    boolean disableSaveButton = false;
                    if ((selectedMdConnector != null) && (!selectedMdConnector.equals(origMdConnector))) {
                        boolean hasCableRelationships = false;
                        List<ItemElementRelationship> ierList
                                = selectedMdConnector.getItemElementRelationshipList();
                        if (ierList != null) {
                            // find just the cable relationship items
                            RelationshipType cableIerType
                                    = RelationshipTypeFacade.getInstance().findByName(
                                            ItemElementRelationshipTypeNames.itemCableConnection.getValue());
                            if (cableIerType != null) {
                                ierList = ierList.stream().
                                        filter(ier -> ier.getRelationshipType().getName().equals(cableIerType.getName())).
                                        collect(Collectors.toList());
                                if (!ierList.isEmpty()) {
                                    // cable is in use for other cable relationships
                                    hasCableRelationships = true;
                                }
                            }
                        }
                        if (hasCableRelationships) {
                            disableSaveButton = true;
                            setWarningMessage("Selected conector is already in use for cable connection.");
                        } else {
                            setMessage("Selected endpoint item and connector are valid.");
                        }
                    } else if ((selectedMdItem != null) && (!selectedMdItem.equals(origMdItem))) {
                            setMessage("Selected endpoint item is valid, no endpoint connector selected.");
                    } else if (((selectedCableConnector == null) && (getOrigCableConnector() != null)) || ((selectedCableConnector != null) && (!selectedCableConnector.equals(getOrigCableConnector())))) {
                        setMessage(DEFAULT_MESSAGE);
                        disableSaveButton = false;
                    } else {
                        setMessage(DEFAULT_MESSAGE);
                        disableSaveButton = true;
                    }
                    
                    setDisableButtonSave((Boolean) disableSaveButton);
                }
            }
        }

        public String save(String remoteCommandSuccess) {

            getCurrent().updateCableRelationship(
                    getCableRelationship(),
                    selectedMdItem,
                    selectedMdConnector,
                    getSelectedCableConnector());

            String updateResult = update();

            refreshConnectionListForCurrent();

            SessionUtility.executeRemoteCommand(remoteCommandSuccess);

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

            if (selectedMdItem == null) {

                setDisableButtonSave((Boolean) true);
                setMessage(DEFAULT_MESSAGE);

            } else {

                boolean disableSaveButton = false;
                if (selectedMdConnector != null) {
                    boolean hasCableRelationships = false;
                    List<ItemElementRelationship> ierList
                            = selectedMdConnector.getItemElementRelationshipList();
                    if (ierList != null) {
                        // find just the cable relationship items
                        RelationshipType cableIerType
                                = RelationshipTypeFacade.getInstance().findByName(
                                        ItemElementRelationshipTypeNames.itemCableConnection.getValue());
                        if (cableIerType != null) {
                            ierList = ierList.stream().
                                    filter(ier -> ier.getRelationshipType().getName().equals(cableIerType.getName())).
                                    collect(Collectors.toList());
                            if (!ierList.isEmpty()) {
                                // cable is in use for other cable relationships
                                hasCableRelationships = true;
                            }
                        }
                    }
                    if (hasCableRelationships) {
                        disableSaveButton = true;
                        setWarningMessage("Selected conector is already in use for cable connection.");
                    } else {
                        setMessage("Selected endpoint item and connector are valid.");
                    }
                } else if (selectedMdItem != null) {
                    setMessage("Selected endpoint item is valid, no endpoint connector selected.");
                } else {
                    setMessage(DEFAULT_MESSAGE);
                    disableSaveButton = true;
                }

                setDisableButtonSave((Boolean) disableSaveButton);
            }
        }
        
        @Override
        public String save(String remoteCommandSuccess) {            
            ItemElementRelationship ier = getCurrent().addCableRelationship(selectedMdItem, null, null, null);
            setCableRelationship(ier);
            return super.save(remoteCommandSuccess);
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

    private EndpointDialog dialogEndpoint = null;
    private CatalogDialog dialogCatalog = null;
    private ConnectionEditDialog dialogConnection = null;
    
    private List<CableDesignConnectionListObject> connectionListForCurrent;
    
    private ItemDomainCableDesignLazyDataModel itemDomainCableDesignLazyDataModel; 

    protected ImportHelperCableDesign importHelper = new ImportHelperCableDesign();
    
    public EndpointDialog getDialogEndpoint() {
        if (dialogEndpoint == null) {
            dialogEndpoint = new EndpointDialog();
        }
        return dialogEndpoint;
    }

    public void setDialogEndpoint(EndpointDialog dialogEndpoint) {
        this.dialogEndpoint = dialogEndpoint;
    }

    public CatalogDialog getDialogCatalog() {
        if (dialogCatalog == null) {
            dialogCatalog = new CatalogDialog();
        }
        return dialogCatalog;
    }

    public void setDialogCatalog(CatalogDialog dialogCatalog) {
        this.dialogCatalog = dialogCatalog;
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

    public void prepareEditEndpoint(Item endpoint, Integer sortOrder) {
        dialogEndpoint.reset();
        dialogEndpoint.setItemEndpoint(endpoint);
        dialogEndpoint.setSortOrder(sortOrder);
    }
    
    /**
     * Prepares endpoint dialog for editing endpoint1.
     */
    public void prepareDialogEndpoint1() {
        prepareEditEndpoint(getCurrent().getEndpoint1(), 1);
    }

    /**
     * Prepares endpoint dialog for editing endpoint2.
     */
    public void prepareDialogEndpoint2() {
        prepareEditEndpoint(getCurrent().getEndpoint2(), 2);
    }
    
    public void prepareDialogCatalog() {
        dialogCatalog.reset();
        dialogCatalog.setItemCatalog(getCurrent().getCatalogItem());
    }

    public void prepareEditConnection(CableDesignConnectionListObject connection) {
        
        dialogConnection = new ConnectionEditDialog();
        dialogConnection.reset();
        
        dialogConnection.setOrigMdItem(connection.getMdItem());
        dialogConnection.setOrigMdConnector(connection.getMdConnector());
        dialogConnection.setCableRelationship(connection.getCableRelationship());
        
        // get list of unmapped connectors, plus connector for this connection if any
        List<ItemConnector> unmappedConnectors = getUnmappedConnectorsForCurrent();
        if (connection.getItemConnector() != null) {
            unmappedConnectors.add(connection.getItemConnector());
            dialogConnection.setSelectedCableConnector(connection.getItemConnector());
        }
        dialogConnection.setAvailableCableConnectors(unmappedConnectors, connection.getItemConnector());
        
        // expand MD tree to specified md item and connector
        dialogConnection.expandTreeAndSelectNode();
        dialogConnection.setSelectedEndpointAndConnector();
        
    }
    
    public void prepareAddConnection() {
        
        dialogConnection = new ConnectionCreateDialog();
        
        dialogConnection.reset();
        
        // get list of unmapped connectors
        List<ItemConnector> unmappedConnectors = getUnmappedConnectorsForCurrent();
        dialogConnection.setAvailableCableConnectors(unmappedConnectors, null);
    }
    
    public Boolean renderEditLinkForConnection(CableDesignConnectionListObject connection) {
        return (connection.getMdItem() != null);
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
    public void resetListDataModel() {
        super.resetListDataModel(); 
        itemDomainCableDesignLazyDataModel = null; 
    }

    public ItemDomainCableDesignLazyDataModel getItemDomainCableDesignLazyDataModel() {
        if (itemDomainCableDesignLazyDataModel == null) {
            itemDomainCableDesignLazyDataModel = new ItemDomainCableDesignLazyDataModel(itemDomainCableDesignFacade, getDefaultDomain());
        }
        return itemDomainCableDesignLazyDataModel;
    }

    @Override
    public DataModel getListDataModel() {
        return getItemDomainCableDesignLazyDataModel();
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
        List<ItemConnector> unmappedConnectors = new ArrayList<>();
        for (ItemConnector connector : getCurrent().getItemConnectorList()) {
            if (!connector.isConnected()) {
                unmappedConnectors.add(connector);
            }
        }
        return unmappedConnectors;
    }

    public List<CableDesignConnectionListObject> getConnectionListForItem(ItemDomainCableDesign item) {
        this.getControllerUtility().syncConnectors(item);
        return CableDesignConnectionListObject.getConnectionList(item);
    }
    
    private void refreshConnectionListForCurrent() {
        ItemDomainCableDesign item = getCurrent();
        connectionListForCurrent = getConnectionListForItem(item);
    }

    public boolean getDisplayConnectionsList() {
        return getConnectionListForCurrent().size() > 0;
    }
    
    public boolean getDisplayConnectorsList() {
        return getUnmappedConnectorsForCurrent().size() > 0;
    }
    
    // <editor-fold defaultstate="collapsed" desc="import/export support">   
    
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
    
    // </editor-fold>   
}
