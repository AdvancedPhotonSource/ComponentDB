/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.AllowedPropertyValueSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.AllowedPropertyValueControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.AllowedPropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named("allowedPropertyValueController")
@SessionScoped
public class AllowedPropertyValueController extends CdbEntityController<AllowedPropertyValueControllerUtility, AllowedPropertyValue, AllowedPropertyValueFacade, AllowedPropertyValueSettings> implements Serializable {    

    @EJB
    private AllowedPropertyValueFacade allowedPropertyValueFacade;
    private static final Logger logger = LogManager.getLogger(AllowedPropertyValueController.class.getName());   

    public AllowedPropertyValueController() {
    }

    @Override
    protected AllowedPropertyValueFacade getEntityDbFacade() {
        return allowedPropertyValueFacade;
    }

    @Override
    public List<AllowedPropertyValue> getAvailableItems() {
        return super.getAvailableItems();
    }

    /**
     * Retrieve data model with all allowed property value objects that
     * correspond to the given property type.
     *
     * @param propertyTypeId property type id
     * @return data model with allowed property values
     */
    public DataModel getListDataModelByPropertyTypeId(Integer propertyTypeId) {
        return new ListDataModel(allowedPropertyValueFacade.findAllByPropertyTypeId(propertyTypeId));
    }

    /**
     * Find list of all allowed property value objects that correspond to the
     * given property type.
     *
     * @param propertyTypeId property type id
     * @return list of allowed property values
     */
    public List<AllowedPropertyValue> findAllByPropertyTypeId(Integer propertyTypeId) {
        return allowedPropertyValueFacade.findAllByPropertyTypeId(propertyTypeId);
    }

    /**
     * Remove specified allowed property value from the database
     *
     * @param allowedPropertyValue object to be deleted from the database
     */
    @Override
    public void destroy(AllowedPropertyValue allowedPropertyValue) {
        if (allowedPropertyValue.getId() != null) {
            super.destroy(allowedPropertyValue);
        }
    }    

    @Override
    protected AllowedPropertyValueSettings createNewSettingObject() {
        return new AllowedPropertyValueSettings(this);
    }

    @Override
    protected AllowedPropertyValueControllerUtility createControllerUtilityInstance() {
        return new AllowedPropertyValueControllerUtility(); 
    }

    /**
     * Converter class for allowed property value objects.
     */
    @FacesConverter(value = "allowedPropertyValueConverter")
    public static class AllowedPropertyValueControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                AllowedPropertyValueController controller = (AllowedPropertyValueController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "allowedPropertyValueController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to allowed property valueS object.");
                return null;
            }
        }

        private Integer getIntegerKey(String value) {
            return Integer.valueOf(value);
        }

        private String getStringKey(Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof AllowedPropertyValue) {
                AllowedPropertyValue o = (AllowedPropertyValue) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + AllowedPropertyValue.class.getName());
            }
        }

    }

}
