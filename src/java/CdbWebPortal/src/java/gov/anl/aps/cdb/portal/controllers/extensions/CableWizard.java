/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignWizardBase;
import static gov.anl.aps.cdb.portal.model.db.entities.CdbEntity.VALUE_CABLE_END_1;
import static gov.anl.aps.cdb.portal.model.db.entities.CdbEntity.VALUE_CABLE_END_2;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;

/**
 * This class is intended to be used by another controller class, e.g.,
 * ItemDomainMachineDesignController, and is utilized when adding a new cable
 * design item.
 *
 * @author cmcchesney
 */
@Named(CableWizard.CONTROLLER_NAMED)
@SessionScoped
public class CableWizard extends ItemDomainCableDesignWizardBase implements Serializable {

    public static final String CONTROLLER_NAMED = "cableWizard";
    
    private ItemDomainCableDesign cableItem = null;
    
    private String selectionCableType = null;
    private Item selectionCableCatalogItem = null;
    
    protected ItemConnector portEnd1 = null;
    protected ItemConnector connectorEnd1 = null;
    protected ItemConnector portEnd2 = null;
    protected ItemConnector connectorEnd2 = null;

    private String selectedConnectorNameEnd1 = null;
    private String selectedConnectorNameEnd2 = null;
    
    private Map<String, ItemConnector> connectorMap = new HashMap<>();
    
    public static final String cableTypeUnspecified = "unspecified";
    public static final String cableTypeCatalog = "catalog";
    
    private static final String tabType = "CableTypeTab";
    private static final String tabDetails = "CableDetailsTab";
    
    public static CableWizard getInstance() {
        return (CableWizard) SessionUtility.findBean(CableWizard.CONTROLLER_NAMED);
    } 
    
    /**
     * Returns whether to specify cable type (e.g., unspecified or catalog). 
     * This is the model for radio buttons on the wizard's cable type tab.
     */
    public String getSelectionCableType() {
        return selectionCableType;
    }

    /**
     * @link ItemDomainCableDesignWizard#getCableType
     */
    public void setSelectionCableType(String selectionCableType) {
        this.selectionCableType = selectionCableType;
    }
    
    /**
     * Returns true if the cable type is unspecified.
     */
    public Boolean isTypeUnspecified() {
        if (selectionCableType == null) {
            return false;
        } else {
            return selectionCableType.equals(cableTypeUnspecified);
        }
    }

    /**
     * Returns true if the cable type is catalog.
     */
    public Boolean isTypeCatalog() {
        if (selectionCableType == null) {
            return false;
        } else {
            return selectionCableType.equals(cableTypeCatalog);
        }
    }

    /**
     * Returns the selection model for the cable catalog data table.
     */
    public Item getSelectionCableCatalogItem() {
        return selectionCableCatalogItem;
    }

    /**
     * @link ItemDomainCableDesignWizard#getSelectedCableCatalogItem
     */
    public void setSelectionCableCatalogItem(Item selectionCableCatalogItem) {
        this.selectionCableCatalogItem = selectionCableCatalogItem;
    }


    /**
     * Handles click events for the selectionCableType selectOneRadio component.
     */
    public void clickListenerCableType() {
        setEnablementForCurrentTab();
    }

    /**
     * Handles selection events for the cable catalog item datatable component.
     */
    public void selectListenerCableCatalogItem() {
        setEnablementForCurrentTab();
    }
    
    public String getEnd1PortString() {
        if (portEnd1 == null) {
            return "";
        } else {
            return portEnd1.getConnector().getName();
        }
    }
    
    public ItemConnector getEnd1Connector() {
        return connectorEnd1;
    }
    
    public void setEnd1Connector(ItemConnector connector) {
        connectorEnd1 = connector;
    }
    
    public String getEnd1ConnectorString() {
        if (connectorEnd1 == null) {
            return "";
        } else {
            return connectorEnd1.getConnector().getName();
        }
    }

    public String getEnd2PortString() {
        if (portEnd2 == null) {
            return "";
        } else {
            return portEnd2.getConnector().getName();
        }
    }
    
    public ItemConnector getEnd2Connector() {
        return connectorEnd2;
    }
    
    public void setEnd2Connector(ItemConnector connector) {
        connectorEnd2 = connector;
    }
    
    public String getEnd2ConnectorString() {
        if (connectorEnd2 == null) {
            return "";
        } else {
            return connectorEnd2.getConnector().getName();
        }
    }

    public void selectListenerEndpoint1(NodeSelectEvent event) {
        if (selectionEndpoint1 != null) {
            ItemElement ieEnd1 = (ItemElement) selectionEndpoint1.getData();
            Item containedItemEnd1 = ieEnd1.getContainedItem();
            if (containedItemEnd1 instanceof ItemDomainMachineDesign) {
                itemEnd1 = containedItemEnd1;
            } else {
                portEnd1 = ieEnd1.getMdConnector();
                if (portEnd1 != null) {
                    if (portEnd1.isConnected()) {
                        SessionUtility.addErrorMessage(
                                "Invalid Connection",
                                "Selected port is already in use.");
                        itemEnd1 = null;
                        portEnd1 = null;
                    } else {
                        itemEnd1 = portEnd1.getItem();
                    }
                }
            }
        } else {
            itemEnd1 = null;
            portEnd1 = null;
        }
        setEnablementForCurrentTab();
    }

    /**
     * Handles unselect events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void unselectListenerEndpoint1(NodeUnselectEvent event) {
        itemEnd1 = null;
        portEnd1 = null;
        setEnablementForCurrentTab();
    }
    
    /**
     * Handles select events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void selectListenerEndpoint2(NodeSelectEvent event) {
        if (selectionEndpoint2 != null) {
            ItemElement ieEnd2 = (ItemElement) selectionEndpoint2.getData();
            Item containedItemEnd2 = ieEnd2.getContainedItem();
            if (containedItemEnd2 instanceof ItemDomainMachineDesign) {
                itemEnd2 = containedItemEnd2;
            } else {
                portEnd2 = ieEnd2.getMdConnector();
                if (portEnd2 != null) {
                    if (portEnd2.isConnected()) {
                        SessionUtility.addErrorMessage(
                                "Invalid Connection",
                                "Selected port is already in use.");
                        itemEnd2 = null;
                        portEnd2 = null;
                    } else {
                        itemEnd2 = portEnd2.getItem();
                    }
                }
            }
        } else {
            itemEnd2 = null;
            portEnd2 = null;
        }
        setEnablementForCurrentTab();
    }

    /**
     * Handles unselect events generated by the machine design tree table
     * component. Must call client side remoteCommand to update button state
     * from oncomplete attribute for this event tag.
     */
    public void unselectListenerEndpoint2(NodeUnselectEvent event) {
        itemEnd2 = null;
        portEnd2 = null;
        setEnablementForCurrentTab();
    }

    private List<String> addCableConnectors(List<ItemConnector> connectors) {
        List<String> connectorNames = new ArrayList<>();
        for (ItemConnector connector : connectors) {
            String connectorName = connector.getConnector().getName();
            connectorMap.put(connectorName, connector);
            connectorNames.add(connectorName);
        }
        return connectorNames;
    }
    
    public List<String> getUnmappedConnectorsEnd1() {
        List<ItemConnector> connectors = ItemDomainCableDesignController.getInstance().getUnmappedConnectorsForCableItem(
                cableItem, VALUE_CABLE_END_1);
        return addCableConnectors(connectors);
    }

    public List<String> getUnmappedConnectorsEnd2() {
        List<ItemConnector> connectors = ItemDomainCableDesignController.getInstance().getUnmappedConnectorsForCableItem(
                cableItem, VALUE_CABLE_END_2);
        return addCableConnectors(connectors);
    }

    public String getSelectedConnectorNameEnd1() {
        return selectedConnectorNameEnd1;
    }

    public void setSelectedConnectorNameEnd1(String selectedConnectorNameEnd1) {
        this.selectedConnectorNameEnd1 = selectedConnectorNameEnd1;
    }

    public String getSelectedConnectorNameEnd2() {
        return selectedConnectorNameEnd2;
    }

    public void setSelectedConnectorNameEnd2(String selectedConnectorNameEnd2) {
        this.selectedConnectorNameEnd2 = selectedConnectorNameEnd2;
    }
    
    public void selectListenerConnectorEnd1() {
        String connectorName = getSelectedConnectorNameEnd1();
        if (connectorName != null) {
            connectorEnd1 = connectorMap.get(connectorName);
        } else {
            connectorEnd1 = null;
        }
    }
    
    public void selectListenerConnectorEnd2() {
        String connectorName = getSelectedConnectorNameEnd2();
        if (connectorName != null) {
            connectorEnd2 = connectorMap.get(connectorName);
        } else {
            connectorEnd2 = null;
        }
    }
    
    /**
     * Resets models for wizard components.
     */
    @Override
    protected void reset_() {
        selectionCableType = null;
        selectionCableCatalogItem = null;
        cableItem = null;
        portEnd1 = null;
        connectorEnd1 = null;
        portEnd2 = null;
        connectorEnd2 = null;
        selectedConnectorNameEnd1 = null;
        selectedConnectorNameEnd2 = null;            
        connectorMap.clear();
    }

    /**
     * Returns custom tab navigation based on wizard state.  Subclass should
     * override for custom behavior.
     */
    @Override
    public String nextTab_(String currStep, String nextStep) {
        
        String nextTab = nextStep;
        
        // skip details tab for unspecified cable type
        if ((nextStep.endsWith(tabDetails)) && (isTypeUnspecified())) {
            if (currStep.endsWith(tabType)) {
                nextTab = CONTROLLER_NAMED+tabReview;
            } else if (currStep.endsWith(tabReview)) {
                nextTab = CONTROLLER_NAMED+tabType;
            }
        }
        
        return nextTab;
    }

    protected void handleTabStep_(String currStep, String nextStep) {
        
        ItemDomainCableDesignController controller = ItemDomainCableDesignController.getInstance();
        
        if (currStep.endsWith("CableBasicsTab") && nextStep.endsWith("CableTypeTab")) {
            cableItem = controller.createCableCommon(
                    inputValueName,
                    selectionProjectList,
                    selectionTechnicalSystemList);
        } else if (currStep.endsWith("CableDetailsTab") && nextStep.endsWith("CableConnectorTab")) {
            cableItem.setCatalogItem(selectionCableCatalogItem);
        }
    }
    
    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    @Override
    protected void setEnablement_(String tab) {
        
        if (tab.endsWith("CableTypeTab")) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonSave = true;
            if (selectionCableType == null) {
                disableButtonNext = true;
            } else {
                disableButtonNext = false;
            }
        } else if (tab.endsWith("CableDetailsTab")) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonSave = true;
            switch (selectionCableType) {
                case cableTypeUnspecified:
                    disableButtonNext = false;
                    break;
                case cableTypeCatalog:
                    if (selectionCableCatalogItem != null) {
                        disableButtonNext = false;
                    } else {
                        disableButtonNext = true;
                    }
                    break;
                default:
                    disableButtonNext = true;
            }
        } else if (tab.endsWith("CableConnectorTab")) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonSave = true;
            disableButtonNext = false;
        }
    }

    /**
     * Implements the save operation, invoked by the wizard's "Save" navigation
     * button.
     */
    public String save() {

        if (itemEnd1 == null) {
            SessionUtility.addErrorMessage(
                    "Could not save cable",
                    "Please specify first endpoint.");
            return "";
        }

        if (itemEnd2 == null) {
            SessionUtility.addErrorMessage(
                    "Could not save cable",
                    "Please specify second endpoint.");
            return "";
        }

        ItemDomainCableDesignController controller = ItemDomainCableDesignController.getInstance();
        cableItem.setPrimaryEndpoint(itemEnd1, portEnd1, connectorEnd1, VALUE_CABLE_END_1);
        cableItem.setPrimaryEndpoint(itemEnd2, portEnd2, connectorEnd2, VALUE_CABLE_END_2);

        String result = "";
        result = controller.create();

        // get redirect before calling cleanup or it will be reset
        String redirect = "";
        if (getRedirectSuccess().isEmpty()) {
            redirect = result;
        } else {
            redirect = getRedirectSuccess();
        }
        cleanupClient();
        this.reset();
        return redirect;
    }
}
