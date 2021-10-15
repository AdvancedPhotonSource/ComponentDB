/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemTypeSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemTypeControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.beans.ItemTypeFacade;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named("itemTypeController")
@SessionScoped
public class ItemTypeController extends ItemTypeCategoryController<ItemTypeControllerUtility, ItemType, ItemTypeFacade, ItemTypeSettings> implements Serializable {       

    private static final Logger logger = LogManager.getLogger(ItemTypeController.class.getName());

    public static final String CONTROLLER_NAMED = "itemTypeController";
        
    @EJB
    private ItemTypeFacade itemTypeFacade;   

    private ItemType selectedItemType = null;

    public ItemTypeController() {
        super();
    }

    public static ItemTypeController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (ItemTypeController) SessionUtility.findBean(ItemTypeController.CONTROLLER_NAMED);
        } else {
            // TODO add apiInstance
            return null;
        }
    }        

    @Override
    protected ItemTypeFacade getEntityDbFacade() {
        return itemTypeFacade;
    }   

    @Override
    public ItemType findById(Integer id) {
        return itemTypeFacade.find(id);
    }

    @Override
    public List<ItemType> getAvailableItems() {
        return super.getAvailableItems();
    }       

    public void savePropertyTypeList() {
        update();
    }

    @Override
    public List<ItemType> getItemTypeCategoryEntityListByDomainName(String domainName) {
        return itemTypeFacade.findByDomainName(domainName);
    }

    @Override
    protected ItemTypeSettings createNewSettingObject() {
        return new ItemTypeSettings(this);
    }

    @Override
    protected ItemTypeControllerUtility createControllerUtilityInstance() {
        return new ItemTypeControllerUtility(); 
    }

    /**
     * Converter class for component type objects.
     */
    @FacesConverter(value = "itemTypeConverter")
    public static class ItemTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            try {
                ItemTypeController controller = (ItemTypeController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "itemTypeController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component type object.");
                return null;
            }
        }

        Integer getIntegerKey(String value) {
            return Integer.valueOf(value);
        }

        String getStringKey(Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ItemType) {
                ItemType o = (ItemType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ItemType.class.getName());
            }
        }

    }

    public ItemType getSelectedItemType() {
        return selectedItemType;
    }

    public void setSelectedItemType(ItemType selectedItemType) {
        this.selectedItemType = selectedItemType;
    }

}
