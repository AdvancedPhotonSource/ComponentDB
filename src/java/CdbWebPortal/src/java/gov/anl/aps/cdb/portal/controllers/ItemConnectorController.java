/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemConnectorSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemConnectorControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.beans.ItemConnectorFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("itemConnectorController")
@SessionScoped
public class ItemConnectorController extends CdbEntityController<ItemConnectorControllerUtility, ItemConnector, ItemConnectorFacade, ItemConnectorSettings> implements Serializable {

    @EJB
    ItemConnectorFacade itemConnectorFacade;

    public static ItemConnectorController getInstance() {
        return (ItemConnectorController) SessionUtility.findBean("itemConnectorController");
    }

    @Override
    protected ItemConnectorFacade getEntityDbFacade() {
        return itemConnectorFacade;
    }

    public String getConnectorGender(ItemConnector connector) {
        if (connector == null) {            
            return ""; 
        }
        if (connector.getConnector().getIsMale()) {
            return "male";
        }
        return "female";
    }

    public String getItemConnectedToReprentationalString(ItemConnector itemConnector) {
        String result = "";

        ItemConnector connectedTo = this.getItemConnectorOfItemConnectedTo(itemConnector);
        if (connectedTo != null) {
            Item item = connectedTo.getItem();
            result += item.toString() + " ";

            Connector connectorConnectedTo = connectedTo.getConnector();
            String connectorName = connectorConnectedTo.getName();
            if (connectorName != null) {
                result += "(" + connectorName + " - ";
            } else {
                result += "(";
            }

            result += connectorConnectedTo.getConnectorType().getName() + ")";
        }

        return result;
    }

    public ItemElementRelationship findConnectionRelationship(List<ItemElementRelationship> ierList) {
        String relationshipTypeName = ItemElementRelationshipTypeNames.itemCableConnection.getValue();

        for (ItemElementRelationship ittrIER : ierList) {
            if (ittrIER.getRelationshipType().getName().equals(relationshipTypeName)) {
                return ittrIER;

            }
        }
        return null;
    }

    public ItemConnector getItemConnectorOfItemConnectedTo(ItemConnector itemConnector) {
        if (itemConnector == null) {
            return null;
        }
        ItemConnector itemConnectorOfItemConnectedTo = itemConnector.getItemConnectorOfItemConnectedTo();
        if (itemConnectorOfItemConnectedTo == null) {
            // Connection from port via cable 
            if (itemConnector.getItemElementRelationshipList().size() > 0) {
                List<ItemElementRelationship> ierList = itemConnector.getItemElementRelationshipList();
                ItemElementRelationship ier = findConnectionRelationship(ierList);

                ItemConnector secondItemConnector = ier.getSecondItemConnector();
                // Get cable connected to. 
                if (secondItemConnector != null) {
                    ItemConnector anotherCableConnector = getConnectorOnOtherEndOfCable(secondItemConnector);
                    ItemConnector cableConnectorConnectedTo = getItemConnectorOfItemConnectedTo(anotherCableConnector);
                    itemConnector.setItemConnectorOfItemConnectedTo(cableConnectorConnectedTo);
                }
            } // Connection from cable connector 
            else if (itemConnector.getItemElementRelationshipList1().size() > 0) {
                List<ItemElementRelationship> ierList = itemConnector.getItemElementRelationshipList1();
                ItemElementRelationship ier = findConnectionRelationship(ierList);

                ItemConnector firstItemConnector = ier.getFirstItemConnector();
                ItemConnector itemConnectorConnectedTo = firstItemConnector;
                itemConnector.setItemConnectorOfItemConnectedTo(itemConnectorConnectedTo);
            }
        }

        return itemConnector.getItemConnectorOfItemConnectedTo();
    }

    private ItemConnector getConnectorOnOtherEndOfCable(ItemConnector itemConnector) {
        Item cableItem = itemConnector.getItem();
        if (cableItem != null) {
            List<ItemConnector> itemConnectors = cableItem.getItemConnectorList();
            // Cables have two connectors
            if (itemConnectors.size() == 2) {
                ItemConnector oneCableEnd = itemConnectors.get(0);
                if (oneCableEnd.equals(itemConnector)) {
                    return itemConnectors.get(1);
                }
                return itemConnectors.get(0);
            }
        }

        return null;
    }

    public Item getItemConnectedVia(ItemConnector itemConnector) {
        if (itemConnector == null) {
            return null;
        }
        Item itemConnectedVia = itemConnector.getItemConnectedVia();

        if (itemConnectedVia == null) {
            if (itemConnector.getItemElementRelationshipList().isEmpty() == false) {
                List<ItemElementRelationship> ierList = itemConnector.getItemElementRelationshipList();
                ItemElementRelationship ier = findConnectionRelationship(ierList);
                ItemConnector secondItemConnector = ier.getSecondItemConnector();
                if (secondItemConnector != null) {
                    itemConnectedVia = secondItemConnector.getItem();
                    itemConnector.setItemConnectedVia(itemConnectedVia);
                }
            }
            // Cable is not connected via therefore item element relationship list 1 is invalid.             
        }

        return itemConnectedVia;

    }

    @Override
    protected ItemConnectorSettings createNewSettingObject() {
        return new ItemConnectorSettings();
    }

    @Override
    protected ItemConnectorControllerUtility createControllerUtilityInstance() {
        return new ItemConnectorControllerUtility(); 
    }

    @FacesConverter(forClass = ItemConnector.class)
    public static class ItemConnectorControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemConnectorController controller = (ItemConnectorController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemConnectorController");
            return controller.findById(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ItemConnector) {
                ItemConnector o = (ItemConnector) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ItemConnector.class.getName());
            }
        }

    }

}
