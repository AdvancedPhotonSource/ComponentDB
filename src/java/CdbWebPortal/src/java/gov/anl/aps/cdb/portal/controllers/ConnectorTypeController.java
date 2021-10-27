/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ConnectorTypeSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ConnectorTypeControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperConnectorType;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.model.db.beans.ConnectorTypeFacade;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("connectorTypeController")
@SessionScoped
public class ConnectorTypeController extends CdbEntityController<ConnectorTypeControllerUtility, ConnectorType, ConnectorTypeFacade, ConnectorTypeSettings> implements Serializable {
    
    @EJB
    ConnectorTypeFacade connectorTypeFacade;

    public static ConnectorTypeController getInstance() {
        return (ConnectorTypeController) SessionUtility.findBean("connectorTypeController"); 
    }         

    @Override
    protected ConnectorTypeFacade getEntityDbFacade() {
        return connectorTypeFacade;
    }   
    
    @Override
    protected ConnectorTypeSettings createNewSettingObject() {
        return new ConnectorTypeSettings(this);
    }

    @Override
    protected ConnectorTypeControllerUtility createControllerUtilityInstance() {
        return new ConnectorTypeControllerUtility(); 
    }

    @FacesConverter(forClass = ConnectorType.class)
    public static class ConnectorTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConnectorTypeController controller = (ConnectorTypeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "connectorTypeController");
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
            if (object instanceof ConnectorType) {
                ConnectorType o = (ConnectorType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ConnectorType.class.getName());
            }
        }

    }

    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        
        formatInfo.add(new ImportExportFormatInfo(
                "Basic Connector Type Create/Update/Delete Format", ImportHelperConnectorType.class));
        
        String completionUrl = "/views/connectorType/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
    @Override
    public boolean getEntityDisplayExportButton() {
        return true;
    }
    
    @Override
    protected DomainImportExportInfo initializeDomainExportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        
        formatInfo.add(new ImportExportFormatInfo(
                "Basic Connector Type Create/Update/Delete Format", ImportHelperConnectorType.class));
        
        String completionUrl = "/views/connectorType/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
}
