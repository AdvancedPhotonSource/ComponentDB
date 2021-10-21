/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemCategorySettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemCategoryControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named(ItemCategoryController.CONTROLLER_NAMED)
@SessionScoped
public class ItemCategoryController extends ItemTypeCategoryController<ItemCategoryControllerUtility, ItemCategory, ItemCategoryFacade, ItemCategorySettings> implements Serializable {

    public static final String CONTROLLER_NAMED = "itemCategoryController";
        
    @EJB
    ItemCategoryFacade itemCategoryFacade;             
    
    private static final Logger logger = LogManager.getLogger(ItemCategoryController.class.getName());
    
    public ItemCategoryController() {
        super();        
    }

    public static ItemCategoryController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (ItemCategoryController) SessionUtility.findBean(ItemCategoryController.CONTROLLER_NAMED);
        } else {
            // TODO add apiInstance
            return null;
        }
    }        

    @Override
    protected ItemCategoryFacade getEntityDbFacade() {
        return itemCategoryFacade; 
    }       
    
    @Override
    public ItemCategory findById(Integer id) {
        return itemCategoryFacade.find(id); 
    }

    @Override
    public List<ItemCategory> getItemTypeCategoryEntityListByDomainName(String domainName) {
        return itemCategoryFacade.findByDomainName(domainName);
    }   

    @Override
    protected ItemCategorySettings createNewSettingObject() {
        return new ItemCategorySettings(this);
    }

    @Override
    protected ItemCategoryControllerUtility createControllerUtilityInstance() {
        return new ItemCategoryControllerUtility(); 
    }
    
    /**
     * Converter class for component category objects.
     */
    @FacesConverter(value = "itemCategoryConverter")
    public static class ItemCategoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            try {
                ItemCategoryController controller = (ItemCategoryController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "itemCategoryController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component category object.");
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
            if (object instanceof ItemCategory) {
                ItemCategory o = (ItemCategory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of category " + object.getClass().getName() + "; expected category: " + ItemCategory.class.getName());
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
                "Basic Item Category Create/Update/Delete Format", ImportHelperItemCategory.class));
        
        String completionUrl = "/views/itemCategory/list?faces-redirect=true";
        
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
                "Basic Item Category Create/Update/Delete Format", ImportHelperItemCategory.class));
        
        String completionUrl = "/views/itemCategory/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
}
