/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentInstanceDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentTypeDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.LocationDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AssemblyComponent;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstance;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentSource;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentType;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Location;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.AssemblyComponentUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ComponentTypeUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ComponentUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.model.TreeNode;

/**
 * Component controller class.
 */
@Named("componentController")
@SessionScoped
public class ComponentController extends CdbEntityController<Component, ComponentDbFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayCategorySettingTypeKey = "Component.List.Display.Category";
    private static final String DisplayCreatedByUserSettingTypeKey = "Component.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "Component.List.Display.CreatedOnDateTime";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Component.List.Display.NumberOfItemsPerPage";
    private static final String DisplayDescriptionSettingTypeKey = "Component.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "Component.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "Component.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "Component.List.Display.LastModifiedOnDateTime";
    private static final String DisplayModelNumberSettingTypeKey = "Component.List.Display.ModelNumber";
    private static final String DisplayOwnerUserSettingTypeKey = "Component.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "Component.List.Display.OwnerGroup";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "Component.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "Component.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "Component.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "Component.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "Component.List.Display.PropertyTypeId5";
    private static final String DisplayTypeSettingTypeKey = "Component.List.Display.Type";
    private static final String FilterByCategorySettingTypeKey = "Component.List.FilterBy.Category";
    private static final String FilterByCreatedByUserSettingTypeKey = "Component.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "Component.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "Component.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "Component.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "Component.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByNameSettingTypeKey = "Component.List.FilterBy.Name";
    private static final String FilterByModelNumberSettingTypeKey = "Component.List.FilterBy.ModelNumber";
    private static final String FilterByOwnerUserSettingTypeKey = "Component.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "Component.List.FilterBy.OwnerGroup";
    private static final String FilterByPropertyValue1SettingTypeKey = "Component.List.FilterBy.PropertyValue1";
    private static final String FilterByPropertyValue2SettingTypeKey = "Component.List.FilterBy.PropertyValue2";
    private static final String FilterByPropertyValue3SettingTypeKey = "Component.List.FilterBy.PropertyValue3";
    private static final String FilterByPropertyValue4SettingTypeKey = "Component.List.FilterBy.PropertyValue4";
    private static final String FilterByPropertyValue5SettingTypeKey = "Component.List.FilterBy.PropertyValue5";
    private static final String FilterByTypeSettingTypeKey = "Component.List.FilterBy.Type";

    private static final Logger logger = Logger.getLogger(ComponentController.class.getName());

    @EJB
    private ComponentDbFacade componentFacade;

    @EJB
    private ComponentTypeDbFacade componentTypeFacade;

    @EJB
    private ComponentInstanceDbFacade componentInstanceFacade;

    @EJB
    private PropertyTypeDbFacade propertyTypeFacade;

    @EJB
    private LocationDbFacade locationFacade;

    private Boolean displayType = null;
    private Boolean displayCategory = null;
    private Boolean displaySources = true;
    private Boolean displayModelNumber = null;

    private String filterByType = null;
    private String filterByCategory = null;
    private String filterByModelNumber = null;
    private String filterBySources = null;

    private Boolean selectDisplayType = true;
    private Boolean selectDisplayCategory = true;
    private Boolean selectDisplayModelNumber = true;

    private String selectFilterByType = null;
    private String selectFilterByCategory = null;
    private String selectFilterByModelNumber = null;

    private Integer displayPropertyTypeId1 = null;
    private Integer displayPropertyTypeId2 = null;
    private Integer displayPropertyTypeId3 = null;
    private Integer displayPropertyTypeId4 = null;
    private Integer displayPropertyTypeId5 = null;

    private String filterByPropertyValue1 = null;
    private String filterByPropertyValue2 = null;
    private String filterByPropertyValue3 = null;
    private String filterByPropertyValue4 = null;
    private String filterByPropertyValue5 = null;

    // There seems to be a problem with primefaces framework, as select one menu does not
    // recognize value change in some case, so we bind this variable to control the menu. 
    private SelectOneMenu componentTypeSelectOneMenu;

    private DataTable componentPropertyValueListDataTable = null;

    private Component selectedComponent = null;

    private List<Location> locationList = null;

    private List<PropertyValue> filteredPropertyValueList;
    private List<ComponentType> selectComponentTypeCandidateList = null;

    public ComponentController() {
        super();
    }

    @Override
    protected ComponentDbFacade getEntityDbFacade() {
        return componentFacade;
    }

    @Override
    protected Component createEntityInstance() {
        Component component = new Component();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        component.setEntityInfo(entityInfo);

        selectComponentTypeCandidateList = null;
        return component;
    }

    @Override
    public Component cloneEntityInstance(Component component) {
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        Component clonedComponent = component.copyAndSetEntityInfo(entityInfo);
        setLogText("Cloned from " + component.getName());
        return clonedComponent;
    }

    @Override
    public String getEntityTypeName() {
        return "component";
    }

    @Override
    public String getEntityTypeTypeName() {
        return "componentType";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().toString();
        }
        return "";
    }

    @Override
    public Component findById(Integer id) {
        return componentFacade.findById(id);
    }

    @Override
    public EntityInfo getEntityInfo(Component entity) {
        if (entity != null) {
            return entity.getEntityInfo();
        }
        return null;
    }

    @Override
    public void prepareEntityView(Component component) {
        prepareComponentImageList(component);
    }

    private void checkComponent(Component component) throws CdbException {
       Component existingComponent = null;
        String modelNumber = component.getModelNumber();
        if (modelNumber != null && !modelNumber.isEmpty()) {
            existingComponent = componentFacade.findByModelNumber(modelNumber);           
        }
        if (existingComponent == null) {
            existingComponent = componentFacade.findByNameAndModelNumber(component.getName(), modelNumber);            
        }
        if (existingComponent != null && !existingComponent.getId().equals(component.getId())) {
            throw new ObjectAlreadyExists("Component " + component.toString() + " already exists with id " + existingComponent.getId() + ".");
        }
        if (component.getComponentType() == null) {
            throw new InvalidObjectState("Component type for " + component.toString() + " must be selected.");
        }
         
    }
    
    @Override
    public void prepareEntityInsert(Component component) throws CdbException {
        checkComponent(component);
        Log logEntry = prepareLogEntry();
        if (logEntry != null) {
            List<Log> logList = new ArrayList<>();
            logList.add(logEntry);
            component.setLogList(logList);
        }

        component.addComponentTypeProperties();
        logger.debug("Inserting new component " + component.getName() + " (user: "
                + component.getEntityInfo().getCreatedByUser().getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdate(Component component) throws CdbException {
        checkComponent(component);
        EntityInfo entityInfo = component.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
        Log logEntry = prepareLogEntry();
        if (logEntry != null) {
            List<Log> logList = component.getLogList();
            logList.add(logEntry);
            component.setLogList(logList);
        }

        // Compare properties with what is in the db
        List<PropertyValue> originalPropertyValueList = componentFacade.findById(component.getId()).getPropertyValueList();
        List<PropertyValue> newPropertyValueList = component.getPropertyValueList();
        logger.debug("Verifying properties for component " + component);
        PropertyValueUtility.preparePropertyValueHistory(originalPropertyValueList, newPropertyValueList, entityInfo);
        component.clearPropertyValueCache();
        prepareComponentImageList(component);

        List<ComponentInstance> componentInstanceList = component.getComponentInstanceList();
        if (componentInstanceList != null) {
            for (ComponentInstance componentInstance : componentInstanceList) {
                componentInstance.resetAttributesToNullIfEmpty();
                componentInstanceFacade.checkUniqueness(componentInstance);
            }
        }

        List<AssemblyComponent> assemblyComponentList = component.getAssemblyComponentList();
        if (assemblyComponentList != null) {
            for (AssemblyComponent assemblyComponent : assemblyComponentList) {
                if (assemblyComponent.getComponent() == null) {
                    throw new InvalidObjectState("Assembly component must be selected.");
                }
            }
        }

        // Catch circular dependencies.
        AssemblyComponentUtility.createAssemblyRoot(component);

        logger.debug("Updating component " + component.getName()
                + " (user: " + entityInfo.getLastModifiedByUser().getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdateOnRemoval(Component component) {
        EntityInfo entityInfo = component.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
    }

    @Override
    public String prepareEdit(Component component) {
        locationList = locationFacade.findAll();
        return super.prepareEdit(component);
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
        for (PropertyType propertyType : propertyTypeList) {
            preparePropertyTypeValueAdd(propertyType);
        }
    }
    
    public void preparePropertyTypeValueAdd(PropertyType propertyType){
        preparePropertyTypeValueAdd(propertyType, propertyType.getDefaultValue()); 
    }
    
    public void preparePropertyTypeValueAdd(PropertyType propertyType, String propertyValueString){
        Component component = getCurrent();
        List<PropertyValue> propertyValueList = component.getPropertyValueList();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        
        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setPropertyType(propertyType);
        propertyValue.setValue(propertyValueString);
        propertyValue.setUnits(propertyType.getDefaultUnits());
        propertyValueList.add(propertyValue);
        propertyValue.setEnteredByUser(lastModifiedByUser);
        propertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
        
    }

    public void deleteProperty(PropertyValue componentProperty) {
        Component component = getCurrent();
        List<PropertyValue> componentPropertyList = component.getPropertyValueList();
        componentPropertyList.remove(componentProperty);
        updateOnRemoval();
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
        updateOnRemoval();
    }

    public void prepareAddComponentInstance(Component component) {
        List<ComponentInstance> componentInstanceList = component.getComponentInstanceList();
        ComponentInstance componentInstance = new ComponentInstance();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        componentInstance.setEntityInfo(entityInfo);
        componentInstance.setComponent(component);
        componentInstanceList.add(componentInstance);
    }

    public void prepareAddClonedComponentInstance(ComponentInstance componentInstance) {
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        List<ComponentInstance> componentInstanceList = componentInstance.getComponent().getComponentInstanceList();
        ComponentInstance clonedComponentInstance = componentInstance.copyAndSetEntityInfo(entityInfo);
        componentInstanceList.add(clonedComponentInstance);
    }

    public void saveComponentInstanceList() {
        Component component = getCurrent();
        List<ComponentInstance> componentInstanceList = component.getComponentInstanceList();
        if (componentInstanceList != null) {
            UserInfo createdByUser = (UserInfo) SessionUtility.getUser();
            Date createdOnDateTime = new Date();
            for (ComponentInstance componentInstance : componentInstanceList) {
                componentInstance.resetAttributesToNullIfEmpty();

                // If id is null, this is a new component instance; check its properties
                if (componentInstance.getId() == null) {
                    componentInstance.updateDynamicProperties(createdByUser, createdOnDateTime);
                }
            }
        }
        update();
    }

    public String addComponentInstance(ComponentInstance componentInstance) throws InvalidObjectState {
        Component component = componentInstance.getComponent();
        if (component == null) {
            SessionUtility.addWarningMessage("Warning", "Component must be selected.");
            return null;
        }

        UserInfo createdByUser = (UserInfo) SessionUtility.getUser();
        Date createdOnDateTime = new Date();

        // Reset some attributes to null.
        componentInstance.resetAttributesToNullIfEmpty();
        componentInstance.updateDynamicProperties(createdByUser, createdOnDateTime);

        List<ComponentInstance> componentInstanceList = component.getComponentInstanceList();
        componentInstanceList.add(componentInstance);
        setCurrent(component);
        update();
        return view();
    }

    public void deleteComponentInstance(ComponentInstance componentInstance) {
        Component component = componentInstance.getComponent();
        List<ComponentInstance> componentInstanceList = component.getComponentInstanceList();
        componentInstanceList.remove(componentInstance);
        setCurrent(component);
        updateOnRemoval();
    }

    public void prepareAddLog(Component component) {
        Log logEntry = LogUtility.createLogEntry();
        List<Log> componentLogList = component.getLogList();
        componentLogList.add(0, logEntry);
    }

    public void deleteLog(Log componentLog) {
        Component component = getCurrent();
        List<Log> componentLogList = component.getLogList();
        componentLogList.remove(componentLog);
        updateOnRemoval();
    }

    public List<Log> getLogList() {
        Component component = getCurrent();
        List<Log> componentLogList = component.getLogList();
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            if (settingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionUser(sessionUser);
                settingsTimestamp = new Date();
            }
        }
        return componentLogList;
    }

    public void saveLogList() {
        update();
    }

    public void prepareAddAssemblyComponent(Component component) {
        List<AssemblyComponent> assemblyComponentList = component.getAssemblyComponentList();
        AssemblyComponent assemblyComponent = new AssemblyComponent();
        assemblyComponent.setAssembly(component);
        assemblyComponentList.add(assemblyComponent);
    }

    public void saveAssemblyComponentList() {
        update();
    }

    public void deleteAssemblyComponent(AssemblyComponent assemblyComponent) {
        Component component = getCurrent();
        List<AssemblyComponent> assemblyComponentList = component.getAssemblyComponentList();
        assemblyComponentList.remove(assemblyComponent);
        updateOnRemoval();
    }

    @Override
    public String customizeListDisplay() {
        resetComponentPropertyTypeIdIndexMappings();
        return super.customizeListDisplay();
    }

    private void resetComponentPropertyTypeIdIndexMappings() {
        Component.setPropertyTypeIdIndex(1, displayPropertyTypeId1);
        Component.setPropertyTypeIdIndex(2, displayPropertyTypeId2);
        Component.setPropertyTypeIdIndex(3, displayPropertyTypeId3);
        Component.setPropertyTypeIdIndex(4, displayPropertyTypeId4);
        Component.setPropertyTypeIdIndex(5, displayPropertyTypeId5);
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
        displayCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayCategorySettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        displayModelNumber = Boolean.parseBoolean(settingTypeMap.get(DisplayModelNumberSettingTypeKey).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByType = settingTypeMap.get(FilterByTypeSettingTypeKey).getDefaultValue();
        filterByCategory = settingTypeMap.get(FilterByCategorySettingTypeKey).getDefaultValue();
        filterByModelNumber = settingTypeMap.get(FilterByModelNumberSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();

        filterByPropertyValue1 = settingTypeMap.get(FilterByPropertyValue1SettingTypeKey).getDefaultValue();
        filterByPropertyValue2 = settingTypeMap.get(FilterByPropertyValue2SettingTypeKey).getDefaultValue();
        filterByPropertyValue3 = settingTypeMap.get(FilterByPropertyValue3SettingTypeKey).getDefaultValue();
        filterByPropertyValue4 = settingTypeMap.get(FilterByPropertyValue4SettingTypeKey).getDefaultValue();
        filterByPropertyValue5 = settingTypeMap.get(FilterByPropertyValue5SettingTypeKey).getDefaultValue();

        resetComponentPropertyTypeIdIndexMappings();
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
        displayCategory = sessionUser.getUserSettingValueAsBoolean(DisplayCategorySettingTypeKey, displayCategory);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        displayModelNumber = sessionUser.getUserSettingValueAsBoolean(DisplayModelNumberSettingTypeKey, displayModelNumber);

        displayPropertyTypeId1 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByType = sessionUser.getUserSettingValueAsString(FilterByTypeSettingTypeKey, filterByType);
        filterByCategory = sessionUser.getUserSettingValueAsString(FilterByCategorySettingTypeKey, filterByCategory);
        filterByModelNumber = sessionUser.getUserSettingValueAsString(FilterByModelNumberSettingTypeKey, filterByModelNumber);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByPropertyValue1 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        filterByPropertyValue2 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        filterByPropertyValue3 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        filterByPropertyValue4 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        filterByPropertyValue5 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);

        resetComponentPropertyTypeIdIndexMappings();
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterByType = (String) filters.get("componentType");
        filterByCategory = (String) filters.get("componentTypeCategory");
        filterByModelNumber = (String) filters.get("modelNumber");

        filterByPropertyValue1 = (String) filters.get("propertyValue1");
        filterByPropertyValue2 = (String) filters.get("propertyValue2");
        filterByPropertyValue3 = (String) filters.get("propertyValue3");
        filterByPropertyValue4 = (String) filters.get("propertyValue4");
        filterByPropertyValue5 = (String) filters.get("propertyValue5");
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
        sessionUser.setUserSettingValue(DisplayModelNumberSettingTypeKey, displayModelNumber);

        sessionUser.setUserSettingValue(DisplayTypeSettingTypeKey, displayType);
        sessionUser.setUserSettingValue(DisplayCategorySettingTypeKey, displayCategory);

        sessionUser.setUserSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);

        sessionUser.setUserSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        sessionUser.setUserSettingValue(FilterByTypeSettingTypeKey, filterByType);
        sessionUser.setUserSettingValue(FilterByCategorySettingTypeKey, filterByCategory);
        sessionUser.setUserSettingValue(FilterByModelNumberSettingTypeKey, filterByModelNumber);

        sessionUser.setUserSettingValue(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        sessionUser.setUserSettingValue(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        sessionUser.setUserSettingValue(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        sessionUser.setUserSettingValue(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        sessionUser.setUserSettingValue(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByType = null;
        filterByCategory = null;
        filterByModelNumber = null;

        filterByPropertyValue1 = null;
        filterByPropertyValue2 = null;
        filterByPropertyValue3 = null;
        filterByPropertyValue4 = null;
        filterByPropertyValue5 = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByType = null;
        selectFilterByCategory = null;
        selectFilterByModelNumber = null;
    }

    public Boolean getDisplayType() {
        return displayType;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    /**
     * Converter class for component objects.
     */
    @FacesConverter(value = "componentConverter", forClass = Component.class)
    public static class ComponentControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            try {
                ComponentController controller = (ComponentController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "componentController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component object.");
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
            if (object instanceof Component) {
                Component o = (Component) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Component.class.getName());
            }
        }

    }

    public List<String> completeLocationName(String query) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<String> completedList = new ArrayList<>();
        for (Location location : locationList) {
            boolean nameContainsQuery = searchPattern.matcher(location.getName()).find();
            if (nameContainsQuery) {
                completedList.add(location.getName());
            }
        }
        return completedList;
    }

    public List<Location> completeLocation(String query) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<Location> completedList = new ArrayList<>();
        for (Location location : locationList) {
            boolean nameContainsQuery = searchPattern.matcher(location.getName()).find();
            if (nameContainsQuery) {
                completedList.add(location);
            }
        }
        return completedList;
    }

    public void selectComponentType(ComponentType componentType) {
        Component component = getCurrent();
        component.setComponentType(componentType);
    }

    public List<ComponentType> getSelectComponentTypeCandidateList() {
        if (selectComponentTypeCandidateList == null) {
            selectComponentTypeCandidateList = componentTypeFacade.findAll();
        }
        return selectComponentTypeCandidateList;
    }

    public List<ComponentType> completeComponentType(String query) {
        return ComponentTypeUtility.filterComponentType(query, getSelectComponentTypeCandidateList());
    }

    // This listener is accessed either after selection made in dialog,
    // or from selection menu.
    public void selectComponentTypeValueChangeListener(ValueChangeEvent valueChangeEvent) {
        Component component = getCurrent();
        ComponentType existingComponentType = component.getComponentType();
        ComponentType newEventComponentType = null;
        ComponentType oldEventComponentType = null;

        Object newValue = valueChangeEvent.getNewValue();
        if (newValue != null) {
            newEventComponentType = (ComponentType) newValue;
        }
        Object oldValue = valueChangeEvent.getOldValue();
        if (oldValue != null) {
            oldEventComponentType = (ComponentType) oldValue;
        }

        if (ObjectUtility.equals(existingComponentType, oldEventComponentType)) {
            // change via menu
            component.setComponentType(newEventComponentType);
        } else {
            // change via dialog
            component.setComponentType(oldEventComponentType);
        }
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

    public void setDisplayType(Boolean displayType) {
        this.displayType = displayType;
    }

    public Boolean getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(Boolean displayCategory) {
        this.displayCategory = displayCategory;
    }

    public String getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(String filterByType) {
        this.filterByType = filterByType;
    }

    public String getFilterByCategory() {
        return filterByCategory;
    }

    public void setFilterByCategory(String filterByCategory) {
        this.filterByCategory = filterByCategory;
    }

    public Boolean getSelectDisplayType() {
        return selectDisplayType;
    }

    public void setSelectDisplayType(Boolean selectDisplayType) {
        this.selectDisplayType = selectDisplayType;
    }

    public Boolean getSelectDisplayCategory() {
        return selectDisplayCategory;
    }

    public void setSelectDisplayCategory(Boolean selectDisplayCategory) {
        this.selectDisplayCategory = selectDisplayCategory;
    }

    public String getSelectFilterByType() {
        return selectFilterByType;
    }

    public void setSelectFilterByType(String selectFilterByType) {
        this.selectFilterByType = selectFilterByType;
    }

    public String getSelectFilterByCategory() {
        return selectFilterByCategory;
    }

    public void setSelectFilterByCategory(String selectFilterByCategory) {
        this.selectFilterByCategory = selectFilterByCategory;
    }

    public Boolean getSelectDisplayModelNumber() {
        return selectDisplayModelNumber;
    }

    public void setSelectDisplayModelNumber(Boolean selectDisplayModelNumber) {
        this.selectDisplayModelNumber = selectDisplayModelNumber;
    }

    public String getSelectFilterByModelNumber() {
        return selectFilterByModelNumber;
    }

    public void setSelectFilterByModelNumber(String selectFilterByModelNumber) {
        this.selectFilterByModelNumber = selectFilterByModelNumber;
    }

    public Boolean getDisplaySources() {
        return displaySources;
    }

    public void setDisplaySources(Boolean displaySources) {
        this.displaySources = displaySources;
    }

    public String getFilterBySources() {
        return filterBySources;
    }

    public void setFilterBySources(String filterBySources) {
        this.filterBySources = filterBySources;
    }

    public Boolean getDisplayModelNumber() {
        return displayModelNumber;
    }

    public void setDisplayModelNumber(Boolean displayModelNumber) {
        this.displayModelNumber = displayModelNumber;
    }

    public String getFilterByModelNumber() {
        return filterByModelNumber;
    }

    public void setFilterByModelNumber(String filterByModelNumber) {
        this.filterByModelNumber = filterByModelNumber;
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

    public Boolean getDisplayComponentImages() {
        List<PropertyValue> componentImageList = getComponentImageList();
        return (componentImageList != null && !componentImageList.isEmpty());
    }

    public SelectOneMenu getComponentTypeSelectOneMenu() {
        return componentTypeSelectOneMenu;
    }

    public void setComponentTypeSelectOneMenu(SelectOneMenu componentTypeSelectOneMenu) {
        this.componentTypeSelectOneMenu = componentTypeSelectOneMenu;
    }

    public List<PropertyValue> prepareComponentImageList(Component component) {
        if (component == null) {
            return null;
        }
        List<PropertyValue> componentImageList = PropertyValueUtility.prepareImagePropertyValueList(component.getPropertyValueList());
        component.setImagePropertyList(componentImageList);
        return componentImageList;
    }

    public List<PropertyValue> getComponentImageList() {
        Component component = getCurrent();
        if (component == null) {
            return null;
        }
        List<PropertyValue> componentImageList = component.getImagePropertyList();
        if (componentImageList == null) {
            componentImageList = prepareComponentImageList(component);
        }
        return componentImageList;
    }

    public Component getSelectedComponent() {
        return selectedComponent;
    }

    public void setSelectedComponent(Component selectedComponent) {
        this.selectedComponent = selectedComponent;
    }

    public DataTable getComponentPropertyValueListDataTable() {
        if (userSettingsChanged() || shouldResetListDataModel()) {
            componentPropertyValueListDataTable = new DataTable();
        }
        return componentPropertyValueListDataTable;
    }

    public void setComponentPropertyValueListDataTable(DataTable componentPropertyValueListDataTable) {
        this.componentPropertyValueListDataTable = componentPropertyValueListDataTable;
    }

    public List<PropertyValue> getFilteredPropertyValueList() {
        return filteredPropertyValueList;
    }

    public void setFilteredPropertyValueList(List<PropertyValue> filteredPropertyValueList) {
        this.filteredPropertyValueList = filteredPropertyValueList;
    }

    public TreeNode createAssemblyRoot(Component assembly) throws CdbException {
        return ComponentUtility.createAssemblyRoot(assembly);
    }
}
