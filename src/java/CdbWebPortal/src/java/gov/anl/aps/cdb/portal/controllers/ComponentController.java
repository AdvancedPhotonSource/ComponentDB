package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.exceptions.CdbPortalException;
import gov.anl.aps.cdb.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.entities.Component;
import gov.anl.aps.cdb.portal.model.beans.ComponentFacade;
import gov.anl.aps.cdb.portal.model.entities.ComponentSource;
import gov.anl.aps.cdb.portal.model.entities.ComponentType;
import gov.anl.aps.cdb.portal.model.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.entities.Log;
import gov.anl.aps.cdb.portal.model.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.entities.SettingType;
import gov.anl.aps.cdb.portal.model.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("componentController")
@SessionScoped
public class ComponentController extends CrudEntityController<Component, ComponentFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Component.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Component.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "Component.List.Display.Description";
    private static final String DisplayTypeSettingTypeKey = "Component.List.Display.Type";
    private static final String DisplayTypeCategorySettingTypeKey = "Component.List.Display.TypeCategory";
    private static final String DisplayOwnerUserSettingTypeKey = "Component.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "Component.List.Display.OwnerGroup";
    private static final String DisplayCreatedByUserSettingTypeKey = "Component.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "Component.List.Display.CreatedOnDateTime";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "Component.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "Component.List.Display.LastModifiedOnDateTime";

    private static final String FilterByNameSettingTypeKey = "Component.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "Component.List.FilterBy.Description";
    private static final String FilterByTypeSettingTypeKey = "Component.List.FilterBy.Type";
    private static final String FilterByTypeCategorySettingTypeKey = "Component.List.FilterBy.TypeCategory";
    private static final String FilterByOwnerUserSettingTypeKey = "Component.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "Component.List.FilterBy.OwnerGroup";
    private static final String FilterByCreatedByUserSettingTypeKey = "Component.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "Component.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "Component.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "Component.List.FilterBy.LastModifiedOnDateTime";

    private static final Logger logger = Logger.getLogger(ComponentController.class.getName());

    @EJB
    private ComponentFacade componentFacade;

    private Boolean displayType = null;
    private Boolean displayTypeCategory = null;

    private String filterByType = null;
    private String filterByTypeCategory = null;

    private Boolean selectDisplayType = true;
    private Boolean selectDisplayTypeCategory = true;

    private String selectFilterByType = null;
    private String selectFilterByTypeCategory = null;

    public ComponentController() {
        super();
    }

    @Override
    protected ComponentFacade getFacade() {
        return componentFacade;
    }

    @Override
    protected Component createEntityInstance() {
        Component component = new Component();
        UserInfo ownerUser = (UserInfo) SessionUtility.getUser();
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setOwnerUser(ownerUser);
        List<UserGroup> ownerUserGroupList = ownerUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        component.setEntityInfo(entityInfo);
        return component;
    }

    @Override
    public Component cloneEntityInstance(Component component) {
        Component clonedComponent = super.cloneEntityInstance(component);
        UserInfo ownerUser = (UserInfo) SessionUtility.getUser();
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setOwnerUser(ownerUser);
        List<UserGroup> ownerUserGroupList = ownerUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        clonedComponent.setEntityInfo(entityInfo);
        return clonedComponent;
    }

    @Override
    public String getEntityTypeName() {
        return "component";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public void prepareEntityInsert(Component component) throws ObjectAlreadyExists {
        Component existingComponent = componentFacade.findByName(component.getName());
        if (existingComponent != null) {
            throw new ObjectAlreadyExists("Component " + component.getName() + " already exists.");
        }
        EntityInfo entityInfo = component.getEntityInfo();
        UserInfo createdByUser = (UserInfo) SessionUtility.getUser();
        Date createdOnDateTime = new Date();
        entityInfo.setCreatedOnDateTime(createdOnDateTime);
        entityInfo.setCreatedByUser(createdByUser);
        entityInfo.setLastModifiedOnDateTime(createdOnDateTime);
        entityInfo.setLastModifiedByUser(createdByUser);
        String logText = getLogText();
        if (logText != null && !logText.isEmpty()) {
            Log logEntry = new Log();
            logEntry.setText(logText);
            logEntry.setEnteredByUser(createdByUser);
            logEntry.setEnteredOnDateTime(createdOnDateTime);
            List<Log> logList = new ArrayList<>();
            logList.add(logEntry);
            component.setLogList(logList);
            resetLogText();
        }
        logger.debug("Inserting new component " + component.getName() + " (user: " + createdByUser.getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdate(Component component) throws CdbPortalException {
        EntityInfo entityInfo = component.getEntityInfo();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        entityInfo.setLastModifiedOnDateTime(lastModifiedOnDateTime);
        entityInfo.setLastModifiedByUser(lastModifiedByUser);
        String logText = getLogText();
        if (logText != null && !logText.isEmpty()) {
            Log logEntry = new Log();
            logEntry.setText(logText);
            logEntry.setEnteredByUser(lastModifiedByUser);
            logEntry.setEnteredOnDateTime(lastModifiedOnDateTime);
            component.getLogList().add(logEntry);
            resetLogText();
        }

        // Compare properties with what is in the db
        List<PropertyValue> originalPropertyValueList = componentFacade.findById(component.getId()).getPropertyValueList();
        List<PropertyValue> newPropertyValueList = component.getPropertyValueList();
        logger.debug("Verifying properties for component " + component);
        for (PropertyValue newPropertyValue : newPropertyValueList) {
            int index = originalPropertyValueList.indexOf(newPropertyValue);
            if (index >= 0) {
                // Original property was there.
                PropertyValue originalPropertyValue = originalPropertyValueList.get(index);
                if (!newPropertyValue.equalsByValueAndUnitsAndDescription(originalPropertyValue)) {
                    // Property value was modified.
                    logger.debug("Property value for type " + originalPropertyValue.getPropertyType()
                            + " was modified (original value: " + originalPropertyValue + "; new value: " + newPropertyValue + ")");
                    newPropertyValue.setEnteredByUser(lastModifiedByUser);
                    newPropertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
                }
            } else {
                // New property value.
                logger.debug("Adding new property value for type " + newPropertyValue.getPropertyType()
                        + ": " + newPropertyValue);
                newPropertyValue.setEnteredByUser(lastModifiedByUser);
                newPropertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
            }
        }
        logger.debug("Updating component " + component.getName() + " (user: " + lastModifiedByUser.getUsername() + ")");
    }

    public Component findById(Integer id) {
        return componentFacade.findById(id);
    }

    @Override
    public void selectByRequestParams() {
        if (idViewParam != null) {
            Component component = findById(idViewParam);
            setCurrent(component);
            idViewParam = null;
        }
    }

    public void prepareAddProperty() {
        Component component = getCurrent();
        List<PropertyValue> propertyList = component.getPropertyValueList();
        PropertyValue property = new PropertyValue();
        propertyList.add(property);
    }

    public void savePropertyList() {
        update();
    }

    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        Component component = getCurrent();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        List<PropertyValue> propertyValueList = component.getPropertyValueList();
        for (PropertyType propertyType : propertyTypeList) {
            PropertyValue propertyValue = new PropertyValue();
            propertyValue.setPropertyType(propertyType);
            propertyValue.setValue(propertyType.getDefaultValue());
            propertyValue.setUnits(propertyType.getDefaultUnits());
            propertyValueList.add(propertyValue);
            propertyValue.setEnteredByUser(lastModifiedByUser);
            propertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
        }
    }

    public void deleteProperty(PropertyValue componentProperty) {
        Component component = getCurrent();
        List<PropertyValue> componentPropertyList = component.getPropertyValueList();
        componentPropertyList.remove(componentProperty);
    }

    public void prepareAddSource(Component component) {
        List<ComponentSource> sourceList = component.getComponentSourceList();
        ComponentSource source = new ComponentSource();
        source.setComponent(component);
        sourceList.add(source);
    }

    public void saveSourceList() {
        update();
    }

    public void deleteSource(ComponentSource source) {
        Component component = getCurrent();
        List<ComponentSource> sourceList = component.getComponentSourceList();
        sourceList.remove(source);
        update();
    }

    public void prepareAddLog(Component component) {
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        Log logEntry = new Log();
        logEntry.setEnteredByUser(lastModifiedByUser);
        logEntry.setEnteredOnDateTime(lastModifiedOnDateTime);
        component.getLogList().add(logEntry);
    }

    public void deleteLog(Log componentLog) {
        Component component = getCurrent();
        List<Log> componentLogList = component.getLogList();
        componentLogList.remove(componentLog);
    }

    public List<Log> getLogList() {
        Component component = getCurrent();
        List<Log> componentLogList = component.getLogList();
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            Date settingsTimestamp = getSettingsTimestamp();
            if (settingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionUser(sessionUser);
                settingsTimestamp = new Date();
                setSettingsTimestamp(settingsTimestamp);
            }
        }
        return componentLogList;
    }

    public void saveLogList() {
        update();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        logger.debug("Updating list settings from setting type defaults");

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayType = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeSettingTypeKey).getDefaultValue());
        displayTypeCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeCategorySettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByType = settingTypeMap.get(FilterByTypeSettingTypeKey).getDefaultValue();
        filterByTypeCategory = settingTypeMap.get(FilterByTypeCategorySettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        logger.debug("Updating list settings from session user");

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayType = sessionUser.getUserSettingValueAsBoolean(DisplayTypeSettingTypeKey, displayType);
        displayTypeCategory = sessionUser.getUserSettingValueAsBoolean(DisplayTypeCategorySettingTypeKey, displayTypeCategory);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByType = sessionUser.getUserSettingValueAsString(FilterByTypeSettingTypeKey, filterByType);
        filterByTypeCategory = sessionUser.getUserSettingValueAsString(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterByType = filters.get("componentType");
        filterByTypeCategory = filters.get("componentTypeCategory");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        sessionUser.setUserSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        sessionUser.setUserSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        sessionUser.setUserSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        sessionUser.setUserSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        sessionUser.setUserSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        sessionUser.setUserSettingValue(DisplayTypeSettingTypeKey, displayType);
        sessionUser.setUserSettingValue(DisplayTypeCategorySettingTypeKey, displayTypeCategory);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        sessionUser.setUserSettingValue(FilterByTypeSettingTypeKey, filterByType);
        sessionUser.setUserSettingValue(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByType = null;
        filterByTypeCategory = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByType = null;
        selectFilterByTypeCategory = null;
    }

    public void selectComponentType(ComponentType componentType) {
        Component component = getCurrent();
        if (componentType != null) {
            component.setComponentType(componentType);
        }
    }

    public Boolean getDisplayType() {
        return displayType;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    @FacesConverter(value = "componentConverter", forClass = Component.class)
    public static class ComponentControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            ComponentController controller = (ComponentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentController");
            return controller.getEntity(getKey(value));
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
            if (object instanceof Component) {
                Component o = (Component) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Component.class.getName());
            }
        }

    }

    public void setDisplayType(Boolean displayType) {
        this.displayType = displayType;
    }

    public Boolean getDisplayTypeCategory() {
        return displayTypeCategory;
    }

    public void setDisplayTypeCategory(Boolean displayTypeCategory) {
        this.displayTypeCategory = displayTypeCategory;
    }

    public String getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(String filterByType) {
        this.filterByType = filterByType;
    }

    public String getFilterByTypeCategory() {
        return filterByTypeCategory;
    }

    public void setFilterByTypeCategory(String filterByTypeCategory) {
        this.filterByTypeCategory = filterByTypeCategory;
    }

    public Boolean getSelectDisplayType() {
        return selectDisplayType;
    }

    public void setSelectDisplayType(Boolean selectDisplayType) {
        this.selectDisplayType = selectDisplayType;
    }

    public Boolean getSelectDisplayTypeCategory() {
        return selectDisplayTypeCategory;
    }

    public void setSelectDisplayTypeCategory(Boolean selectDisplayTypeCategory) {
        this.selectDisplayTypeCategory = selectDisplayTypeCategory;
    }

    public String getSelectFilterByType() {
        return selectFilterByType;
    }

    public void setSelectFilterByType(String selectFilterByType) {
        this.selectFilterByType = selectFilterByType;
    }

    public String getSelectFilterByTypeCategory() {
        return selectFilterByTypeCategory;
    }

    public void setSelectFilterByTypeCategory(String selectFilterByTypeCategory) {
        this.selectFilterByTypeCategory = selectFilterByTypeCategory;
    }

}
