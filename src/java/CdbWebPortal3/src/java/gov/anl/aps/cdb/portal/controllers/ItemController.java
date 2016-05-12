package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

@Named("itemController")
@SessionScoped
public class ItemController extends CdbEntityController<Item, ItemFacade> implements Serializable {

    @EJB
    private ItemFacade itemFacade;
    
    private PropertyValue currentEditPropertyValue;
    
    @EJB
    private PropertyValueFacade propertyValueDbFacade;
    @EJB
    private PropertyTypeFacade propertyTypeFacade;
        
    protected Boolean displayItemIdentifier1 = true;
    protected Boolean displayItemIdentifier2 = true;

    protected Integer displayPropertyTypeId1 = null;
    protected Integer displayPropertyTypeId2 = null;
    protected Integer displayPropertyTypeId3 = null;
    protected Integer displayPropertyTypeId4 = null;
    protected Integer displayPropertyTypeId5 = null;
        
    protected Boolean displayRowExpansion = null; 
    protected Boolean loadRowExpansionPropertyValues = null; 
    protected Boolean displayGalleryViewableDocuments = null;

    protected String filterByPropertyValue1 = null;
    protected String filterByPropertyValue2 = null;
    protected String filterByPropertyValue3 = null;
    protected String filterByPropertyValue4 = null;
    protected String filterByPropertyValue5 = null;
    protected String filterByItemIdentifier1 = null;
    protected String filterByItemIdentifier2 = null;
    protected Boolean filterByPropertiesAutoLoad = null;

    protected Integer loadedDataTableHashCode = null;
    protected List<Integer> loadedDisplayPropertyTypes = null;
    
    protected Log newLogEdit; 

    private static final Logger logger = Logger.getLogger(ItemController.class.getName());
    
    private static final String DisplayGalleryViewableDocumentsSettingTypeKey = "DomainEntity.Detail.Display.GalleryViewableDocuments";
    
    private List<PropertyValue> filteredPropertyValueList;

    public ItemController() {
    }

    public Item getItem(java.lang.Integer id) {
        return itemFacade.find(id);
    }

    @Override
    protected ItemFacade getEntityDbFacade() {
       return itemFacade; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Item createEntityInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEntityTypeName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCurrentEntityInstanceName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
     @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }
        
        logger.debug("Updating list settings from setting type defaults for: " + this.getEntityTypeName());
        
        displayGalleryViewableDocuments = Boolean.parseBoolean(settingTypeMap.get(DisplayGalleryViewableDocumentsSettingTypeKey).getDefaultValue());
    }
    
    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }
        
        logger.debug("Updating list settings from session user for: " + this.getEntityTypeName());
        
        displayGalleryViewableDocuments = sessionUser.getUserSettingValueAsBoolean(DisplayGalleryViewableDocumentsSettingTypeKey, displayGalleryViewableDocuments);
        
        prepareImageList(getCurrent()); 
    }
    
    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayGalleryViewableDocumentsSettingTypeKey, displayGalleryViewableDocuments);
    }

    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        for (PropertyType propertyType : propertyTypeList) {
            preparePropertyTypeValueAdd(propertyType);
        }
    }

    public void selectPropertyType(PropertyType propertyType, String onSuccessCommand) {
        if (propertyType != null) {
            PropertyValue propertyValue = preparePropertyTypeValueAdd(propertyType);
            setCurrentEditPropertyValue(propertyValue);
            RequestContext.getCurrentInstance().execute(onSuccessCommand);
        } else {
            SessionUtility.addWarningMessage("No property type selected", "Please select a property type.");
            currentEditPropertyValue = null;
        }
    }

    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType) {
        return preparePropertyTypeValueAdd(propertyType, propertyType.getDefaultValue());
    }

    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType, String propertyValueString) {
        return preparePropertyTypeValueAdd(propertyType, propertyValueString, null);
    }

    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType, String propertyValueString, String tag) {
        Item item = getCurrent();
        List<PropertyValue> propertyValueList = item.getPropertyValueList();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();

        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setPropertyType(propertyType);
        propertyValue.setValue(propertyValueString);
        propertyValue.setUnits(propertyType.getDefaultUnits());
        propertyValueList.add(0, propertyValue);
        propertyValue.setEnteredByUser(lastModifiedByUser);
        propertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
        if (tag != null) {
            propertyValue.setTag(tag);
        }

        return propertyValue;
    }

    public List<PropertyValue> prepareImageList(Item item) {
        if (item == null) {
            return null;
        }
        List<PropertyValue> imageList = PropertyValueUtility.prepareImagePropertyValueList(item.getPropertyValueList(), displayGalleryViewableDocuments);
        item.setImagePropertyList(imageList);
        return imageList;
    }

    public List<PropertyValue> getImageList() {
        Item item = getCurrent();
        if (item == null) {
            return null;
        }
        List<PropertyValue> itemImageList = item.getImagePropertyList();
        if (itemImageList == null) {
            itemImageList = prepareImageList(item);
        }
        return itemImageList;
    }

    public Boolean getDisplayImages() {
        List<PropertyValue> itemImageList = getImageList();
        return (itemImageList != null && !itemImageList.isEmpty());
    }

    public Boolean getDisplayLogList() {
        Item item = getCurrent();
        if (item != null) {
            List<Log> logList = item.getLogList();
            return logList != null && !logList.isEmpty();
        }
        return false; 
    }

    public Boolean getDisplayPropertyList() {
        Item item = getCurrent();
        if (item != null) {
            List<PropertyValue> propertyValueList = item.getPropertyValueList();
            return propertyValueList != null && !propertyValueList.isEmpty();
        }
        return false;
    }

    public PropertyValue getCurrentEditPropertyValue() {
        return currentEditPropertyValue;
    }

    public void setCurrentEditPropertyValue(PropertyValue currentEditPropertyValue) {
        this.currentEditPropertyValue = currentEditPropertyValue;
    }

    public void removeCurrentEditPropertyValue() {
        if (currentEditPropertyValue != null) {
            Item item = getCurrent();
            if (currentEditPropertyValue.getId() == null) {
                // Never saved so it should be removed from the property value list
                item.getPropertyValueList().remove(currentEditPropertyValue);
            } else {
                // Update the current Edit value pointer to db info.
                PropertyValue originalValue = propertyValueDbFacade.find(currentEditPropertyValue.getId());
                currentEditPropertyValue.setValue(originalValue.getValue());
                currentEditPropertyValue.setTargetValue("");
                currentEditPropertyValue.setDisplayValue("");
                currentEditPropertyValue.setTag(originalValue.getTag());
                currentEditPropertyValue.setDescription(originalValue.getDescription());
                currentEditPropertyValue.setUnits(originalValue.getUnits());
                currentEditPropertyValue.setIsDynamic(originalValue.getIsDynamic());
                currentEditPropertyValue.setIsUserWriteable(originalValue.getIsUserWriteable());
            }
            currentEditPropertyValue = null;
        }

    }

    public void updateEditProperty() {
        // Will cause refetching of display value.
        currentEditPropertyValue.setDisplayValue("");
        this.update();
    }

    public void deleteCurrentEditPropertyValue() {
        this.deleteProperty(currentEditPropertyValue);
        currentEditPropertyValue = null;
    }

    public void deleteProperty(PropertyValue cdbDomainEntityProperty) {
        Item entity = getCurrent();
        List<PropertyValue> cdbDomainEntityPropertyList = entity.getPropertyValueList();
        cdbDomainEntityPropertyList.remove(cdbDomainEntityProperty);
        updateOnRemoval();
    }

    public void savePropertyList() {
        update();
    }

    @Override
    public void processPreRenderList() {
        super.processPreRenderList();
        
        loadPropertyTypeFilterIfNeeded(); 
    }
    
    @Override
    public String customizeListDisplay() {
        resetDomainEntityPropertyTypeIdIndexMappings();
        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            preparePropertyTypeFilter();
        }
        return super.customizeListDisplay();
    }

    protected void resetDomainEntityPropertyTypeIdIndexMappings() {
        ItemElement.setPropertyTypeIdIndex(1, displayPropertyTypeId1);
        ItemElement.setPropertyTypeIdIndex(2, displayPropertyTypeId2);
        ItemElement.setPropertyTypeIdIndex(3, displayPropertyTypeId3);
        ItemElement.setPropertyTypeIdIndex(4, displayPropertyTypeId4);
        ItemElement.setPropertyTypeIdIndex(5, displayPropertyTypeId5);
    }
    
    @Override
    public DataModel getListDataModel() {
        DataModel cdbDomainEntityDataModel = super.getListDataModel();

        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            if (loadedDataTableHashCode == null || cdbDomainEntityDataModel.hashCode() != loadedDataTableHashCode) {
                loadedDisplayPropertyTypes = null;
                preparePropertyTypeFilter();
            }
        }

        return cdbDomainEntityDataModel;
    }
    
    private void loadPropertyTypeFilterIfNeeded() {
        getListDataModel(); 
    }
    
    private void forceLoadPropertyTypeFilter() {
        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            loadedDisplayPropertyTypes = null;
            preparePropertyTypeFilter();
        }
    }
    
    @Override
    public void saveListSettingsForSessionUserActionListener(ActionEvent actionEvent) {
        super.saveListSettingsForSessionUserActionListener(actionEvent);

        forceLoadPropertyTypeFilter(); 
    }
    
    @Override
    public void saveListSettingsForSessionUserSetCurrentActionListener(ActionEvent actionEvent) {
        super.saveListSettingsForSessionUserSetCurrentActionListener(actionEvent);

        forceLoadPropertyTypeFilter(); 
    }
    
    public void preparePropertyTypeFilter() {

        if (loadedDisplayPropertyTypes == null) {
            loadedDisplayPropertyTypes = new ArrayList<>();
        }

        preparePropertyTypeFilter(displayPropertyTypeId1);
        preparePropertyTypeFilter(displayPropertyTypeId2);
        preparePropertyTypeFilter(displayPropertyTypeId3);
        preparePropertyTypeFilter(displayPropertyTypeId4);
        preparePropertyTypeFilter(displayPropertyTypeId5);

        loadedDataTableHashCode = super.getListDataModel().hashCode();

    }

    public Boolean preparePropertyTypeFilter(Integer propertyTypeId) {
        if (propertyTypeId != null) {
            DataModel<Item> itemList = super.getListDataModel();
            Iterator<Item> itemIterator = itemList.iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                item.getPropertyValueInformation(propertyTypeId);
            }
            loadedDisplayPropertyTypes.add(propertyTypeId);
            return true;
        }
        return false;
    }

    private Boolean fetchFilterablePropertyValue(Integer propertyTypeId) {
        if (loadedDisplayPropertyTypes != null) {
            if (loadedDataTableHashCode != null && super.getListDataModel().hashCode() != loadedDataTableHashCode) {
                loadedDisplayPropertyTypes = null;
                return false;
            }
            for (Integer loadedPropertyTypeId : loadedDisplayPropertyTypes) {
                if (Objects.equals(propertyTypeId, loadedPropertyTypeId)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Boolean getDisplayGalleryViewableDocuments() {
        return displayGalleryViewableDocuments;
    }

    public void setDisplayGalleryViewableDocuments(Boolean displayGalleryViewableDocuments) {
        this.displayGalleryViewableDocuments = displayGalleryViewableDocuments;
    }

    public Boolean getFilterablePropertyValue1() {
        return fetchFilterablePropertyValue(displayPropertyTypeId1);
    }

    public Boolean getFilterablePropertyValue2() {
        return fetchFilterablePropertyValue(displayPropertyTypeId2);
    }

    public Boolean getFilterablePropertyValue3() {
        return fetchFilterablePropertyValue(displayPropertyTypeId3);
    }

    public Boolean getFilterablePropertyValue4() {
        return fetchFilterablePropertyValue(displayPropertyTypeId4);
    }

    public Boolean getFilterablePropertyValue5() {
        return fetchFilterablePropertyValue(displayPropertyTypeId5);
    }

    public Boolean getFilterByPropertiesAutoLoad() {
        return filterByPropertiesAutoLoad;
    }

    public void setFilterByPropertiesAutoLoad(Boolean filterByPropertiesAutoLoad) {
        this.filterByPropertiesAutoLoad = filterByPropertiesAutoLoad;
    }

    private Boolean checkDisplayLoadPropertyValueButtonByProperty(Integer propertyTypeId) {
        if (propertyTypeId == null) {
            return false;
        } else {
            return !fetchFilterablePropertyValue(propertyTypeId);
        }
    }
    
    @Override
    public Boolean getDisplayLoadPropertyValuesButton() {
        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            return false;
        }

        return (checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId1)
                || checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId2)
                || checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId3)
                || checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId4)
                || checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId5));
    }
    
    public String getDisplayPropertyTypeName(Integer propertyTypeId) {
        if (propertyTypeId != null) {

            try {
                PropertyType propertyType = propertyTypeFacade.find(propertyTypeId);
                return propertyType.getName();
            } catch (Exception ex) {
                return "Unknown Property";
            }
        }
        return null;
    }

    public Integer getDisplayPropertyTypeId1() {
        return displayPropertyTypeId1;
    }

    public void setDisplayPropertyTypeId1(Integer displayPropertyTypeId1) {
        this.displayPropertyTypeId1 = displayPropertyTypeId1;
    }

    public Integer getDisplayPropertyTypeId2() {
        return displayPropertyTypeId2;
    }

    public void setDisplayPropertyTypeId2(Integer displayPropertyTypeId2) {
        this.displayPropertyTypeId2 = displayPropertyTypeId2;
    }

    public Integer getDisplayPropertyTypeId3() {
        return displayPropertyTypeId3;
    }

    public void setDisplayPropertyTypeId3(Integer displayPropertyTypeId3) {
        this.displayPropertyTypeId3 = displayPropertyTypeId3;
    }

    public Integer getDisplayPropertyTypeId4() {
        return displayPropertyTypeId4;
    }

    public void setDisplayPropertyTypeId4(Integer displayPropertyTypeId4) {
        this.displayPropertyTypeId4 = displayPropertyTypeId4;
    }

    public Integer getDisplayPropertyTypeId5() {
        return displayPropertyTypeId5;
    }

    public void setDisplayPropertyTypeId5(Integer displayPropertyTypeId5) {
        this.displayPropertyTypeId5 = displayPropertyTypeId5;
    }

    public Boolean getDisplayRowExpansion() {
        return displayRowExpansion;
    }

    public void setDisplayRowExpansion(Boolean displayRowExpansion) {
        this.displayRowExpansion = displayRowExpansion;
    }

    public Boolean getLoadRowExpansionPropertyValues() {
        return loadRowExpansionPropertyValues;
    }

    public void setLoadRowExpansionPropertyValues(Boolean loadRowExpansionPropertyValues) {
        this.loadRowExpansionPropertyValues = loadRowExpansionPropertyValues;
    }

    public String getFilterByPropertyValue1() {
        return filterByPropertyValue1;
    }

    public void setFilterByPropertyValue1(String filterByPropertyValue1) {
        this.filterByPropertyValue1 = filterByPropertyValue1;
    }

    public String getFilterByPropertyValue2() {
        return filterByPropertyValue2;
    }

    public void setFilterByPropertyValue2(String filterByPropertyValue2) {
        this.filterByPropertyValue2 = filterByPropertyValue2;
    }

    public String getFilterByPropertyValue3() {
        return filterByPropertyValue3;
    }

    public void setFilterByPropertyValue3(String filterByPropertyValue3) {
        this.filterByPropertyValue3 = filterByPropertyValue3;
    }

    public String getFilterByPropertyValue4() {
        return filterByPropertyValue4;
    }

    public void setFilterByPropertyValue4(String filterByPropertyValue4) {
        this.filterByPropertyValue4 = filterByPropertyValue4;
    }

    public String getFilterByPropertyValue5() {
        return filterByPropertyValue5;
    }

    public void setFilterByPropertyValue5(String filterByPropertyValue5) {
        this.filterByPropertyValue5 = filterByPropertyValue5;
    }
    
    public void logObjectEditRowEvent(RowEditEvent event) {
        this.saveLogList();
    }

    public Log getNewLogEdit() {
        return newLogEdit;
    }

    public void setNewLogEdit(Log newLogEdit) {
        this.newLogEdit = newLogEdit;
    }
    
    public void removeNewLog() {
        if (newLogEdit != null) {
            Item item = this.current; 
            item.getLogList().remove(newLogEdit);
            newLogEdit = null;
        }
    }
    
    public void saveLogList() {
        newLogEdit = null; 
        update();
    }
    
    public void prepareAddLog(Item item) {
        Log logEntry = LogUtility.createLogEntry();
        setNewLogEdit(logEntry);
        List<Log> itemLogList = item.getLogList();
        itemLogList.add(0, logEntry);
    }

    public List<Log> getLogList() {
        Item item = getCurrent();
        List<Log> componentInstanceLogList = item.getLogList();
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            if (settingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionUser(sessionUser);
                settingsTimestamp = new Date();
            }
        }
        return componentInstanceLogList;
    }

    public void deleteLog(Log itemLog) {
        Item item = getCurrent();
        List<Log> itemLogList = item.getLogList();
        itemLogList.remove(itemLog);
        updateOnRemoval();
    }
    
    public List<PropertyValue> getFilteredPropertyValueList() {
        return filteredPropertyValueList;
    }

    public void setFilteredPropertyValueList(List<PropertyValue> filteredPropertyValueList) {
        this.filteredPropertyValueList = filteredPropertyValueList;
    } 

    
    public String getItemIdentifier1ColumnHeader() {
        return "Item Identifier 1";
    }
    
    
    public String getItemIdentifier2ColumnHeader() {
        return "Item Identifier 2";
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByItemIdentifier1 = null;
        filterByItemIdentifier2 = null; 

        filterByPropertyValue1 = null;
        filterByPropertyValue2 = null;
        filterByPropertyValue3 = null;
        filterByPropertyValue4 = null;
        filterByPropertyValue5 = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        filterByItemIdentifier1 = null;
        filterByItemIdentifier2 = null; 
    }

    public String getFilterByItemIdentifier1() {
        return filterByItemIdentifier1;
    }

    public void setFilterByItemIdentifier1(String filterByItemIdentifier1) {
        this.filterByItemIdentifier1 = filterByItemIdentifier1;
    }

    public String getFilterByItemIdentifier2() {
        return filterByItemIdentifier2;
    }

    public void setFilterByItemIdentifier2(String filterByItemIdentifier2) {
        this.filterByItemIdentifier2 = filterByItemIdentifier2;
    } 

    public Boolean getDisplayItemIdentifier1() {
        return displayItemIdentifier1;
    }

    public void setDisplayItemIdentifier1(Boolean displayItemIdentifier1) {
        this.displayItemIdentifier1 = displayItemIdentifier1;
    }

    public Boolean getDisplayItemIdentifier2() {
        return displayItemIdentifier2;
    }

    public void setDisplayItemIdentifier2(Boolean displayItemIdentifier2) {
        this.displayItemIdentifier2 = displayItemIdentifier2;
    }
    
    @FacesConverter(forClass = Item.class)
    public static class ItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemController controller = (ItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemController");
            return controller.getItem(getKey(value));
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
            if (object instanceof Item) {
                Item o = (Item) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Item.class.getName());
            }
        }

    }

}
