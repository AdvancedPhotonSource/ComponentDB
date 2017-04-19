/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.settings.ConnectorSettings;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.beans.ConnectorFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("connectorController")
@SessionScoped
public class ConnectorController extends CdbEntityController<Connector, ConnectorFacade, ConnectorSettings> implements Serializable {
    
    @EJB
    private ConnectorFacade connectorFacade;
    
    public static ConnectorController getInstance() {
        return (ConnectorController) SessionUtility.findBean("connectorController"); 
    }
    
    public boolean verifySafeRemovalOfConnector(Connector connector) {
        // Get latest version from DB. 
        connector = findById(connector.getId());        
        if (connector.getItemConnectorList().size() != 1) {
            return false;
        } else {
            // connector itemconnector list has size of 1 
            ItemConnector itemConnector = connector.getItemConnectorList().get(0); 
            Item item = itemConnector.getItem(); 
            if (!item.getDomain().getName().equals(ItemDomainName.catalog.getValue())) {
                return false; 
            }
        } 
        return true; 
    }

    @Override
    protected void prepareEntityDestroy(Connector connector) throws CdbException {
        if (verifySafeRemovalOfConnector(connector) == false) {
            throw new CdbException("Cannot remove connector, it has invalid usages."); 
        }
        super.prepareEntityDestroy(connector); 
    }

    @Override
    protected ConnectorFacade getEntityDbFacade() {
        return connectorFacade; 
    }

    @Override
    protected Connector createEntityInstance() {
        return new Connector(); 
    }

    @Override
    public String getEntityTypeName() {
        return "connector"; 
    }

    @Override
    public String getCurrentEntityInstanceName() {
        return getCurrent().toString(); 
    }     

    @Override
    protected ConnectorSettings createNewSettingObject() {
        return new ConnectorSettings();
    }

    @FacesConverter(value = "connectorConverter", forClass = Connector.class)
    public static class ConnectorControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConnectorController controller = (ConnectorController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "connectorController");
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
            if (object instanceof Connector) {
                Connector o = (Connector) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Connector.class.getName());
            }
        }

    }

}
