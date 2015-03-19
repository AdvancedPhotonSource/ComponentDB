package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.exceptions.CdbException;
import gov.anl.aps.cdb.exceptions.InvalidRequest;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstance;
import gov.anl.aps.cdb.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentInstanceFacade;
import gov.anl.aps.cdb.portal.model.db.beans.LocationFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Location;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LocationUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectonemenu.SelectOneMenu;

@Named("componentInstanceController")
@SessionScoped
public class ComponentInstanceController extends CrudEntityController<ComponentInstance, ComponentInstanceFacade> implements Serializable {

    private static final String DisplayCreatedByUserSettingTypeKey = "ComponentInstance.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ComponentInstance.List.Display.CreatedOnDateTime";
    private static final String DisplayDescriptionSettingTypeKey = "ComponentInstance.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "ComponentInstance.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ComponentInstance.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ComponentInstance.List.Display.LastModifiedOnDateTime";
    private static final String DisplayLocationDetailsSettingTypeKey = "ComponentInstance.List.Display.LocationDetails";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentInstance.List.Display.NumberOfItemsPerPage";
    private static final String DisplayOwnerUserSettingTypeKey = "ComponentInstance.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ComponentInstance.List.Display.OwnerGroup";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId5";
    private static final String DisplayQrIdSettingTypeKey = "ComponentInstance.List.Display.QrId";
    private static final String DisplaySerialNumberSettingTypeKey = "ComponentInstance.List.Display.SerialNumber";

    private static final String FilterByComponentSettingTypeKey = "ComponentInstance.List.FilterBy.Component";
    private static final String FilterByCreatedByUserSettingTypeKey = "ComponentInstance.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "ComponentInstance.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "ComponentInstance.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "ComponentInstance.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "ComponentInstance.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByLocationSettingTypeKey = "ComponentInstance.List.FilterBy.Location";
    private static final String FilterByLocationDetailsSettingTypeKey = "ComponentInstance.List.FilterBy.LocationDetails";
    private static final String FilterByOwnerUserSettingTypeKey = "ComponentInstance.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "ComponentInstance.List.FilterBy.OwnerGroup";
    private static final String FilterByPropertyValue1SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue1";
    private static final String FilterByPropertyValue2SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue2";
    private static final String FilterByPropertyValue3SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue3";
    private static final String FilterByPropertyValue4SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue4";
    private static final String FilterByPropertyValue5SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue5";
    private static final String FilterByTagSettingTypeKey = "ComponentInstance.List.FilterBy.Tag";
    private static final String FilterByQrIdSettingTypeKey = "ComponentInstance.List.FilterBy.QrId";
    private static final String FilterBySerialNumberSettingTypeKey = "ComponentInstance.List.FilterBy.SerialNumber";

    private static final Logger logger = Logger.getLogger(ComponentInstanceController.class.getName());

    @EJB
    private ComponentInstanceFacade componentInstanceFacade;

    @EJB
    private PropertyTypeFacade propertyTypeFacade;

    @EJB
    private LocationFacade locationFacade;

    private Boolean displayLocationDetails = null;
    private Boolean displayQrId = null;
    private Boolean displaySerialNumber = null;

    private String filterByComponent = null;
    private String filterByLocation = null;
    private String filterByLocationDetails = null;
    private String filterByQrId = null;
    private String filterBySerialNumber = null;
    private String filterByTag = null;

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

    private List<Location> locationList = null;

    private Integer qrIdViewParam = null;

    private SelectOneMenu componentSelectOneMenu;
    private DataTable componentInstancePropertyValueListDataTable = null;
    private DataTable componentPropertyValueListDataTable = null;

    private List<PropertyValue> filteredPropertyValueList = null;

    public ComponentInstanceController() {
    }

    @Override
    protected ComponentInstanceFacade getFacade() {
        return componentInstanceFacade;
    }

    @Override
    protected ComponentInstance createEntityInstance() {
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        if (entityInfo == null) {
            return null;
        }
        ComponentInstance componentInstance = new ComponentInstance();
        componentInstance.setEntityInfo(entityInfo);

        if (qrIdViewParam != null) {
            componentInstance.setQrId(qrIdViewParam);
            qrIdViewParam = null;
        }

        // clear location list
        locationList = locationFacade.findAll();

        return componentInstance;
    }

    @Override
    public ComponentInstance cloneEntityInstance(ComponentInstance componentInstance) {
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        List<ComponentInstance> componentInstanceList = componentInstance.getComponent().getComponentInstanceList();
        ComponentInstance clonedComponentInstance = componentInstance.copyAndSetEntityInfo(entityInfo);
        componentInstanceList.add(clonedComponentInstance);
        super.setLogText("Cloned from component instance id " + componentInstance.getId());
        return clonedComponentInstance;
    }

    @Override
    public String getEntityTypeName() {
        return "componentInstance";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "component instance";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public ComponentInstance findById(Integer id) {
        return componentInstanceFacade.findById(id);
    }

    public ComponentInstance findByQrId(Integer qrId) {
        return componentInstanceFacade.findByQrId(qrId);
    }

    @Override
    public EntityInfo getEntityInfo(ComponentInstance entity) {
        if (entity != null) {
            return entity.getEntityInfo();
        }
        return null;
    }

    @Override
    public DataModel createListDataModel() {
        listDataModel = new ListDataModel(getFacade().findAllOrderByQrId());
        return listDataModel;
    }

    @Override
    public ComponentInstance selectByViewRequestParams() throws CdbException {
        setBreadcrumbRequestParams();
        Integer idParam = null;
        String paramValue = SessionUtility.getRequestParameterValue("id");
        try {
            if (paramValue != null) {
                idParam = Integer.parseInt(paramValue);
            }
        } catch (NumberFormatException ex) {
            throw new InvalidRequest("Invalid value supplied for " + getDisplayEntityTypeName() + " id: " + paramValue);
        }
        if (idParam != null) {
            ComponentInstance componentInstance = findById(idParam);
            if (componentInstance == null) {
                throw new InvalidRequest("Component instance id " + idParam + " does not exist.");
            }
            setCurrent(componentInstance);
            return componentInstance;
        } else {
            // Due to bug in primefaces, we cannot have more than one
            // f:viewParam on the web page, so process qrId here
            paramValue = SessionUtility.getRequestParameterValue("qrId");
            if (paramValue != null) {
                try {
                    qrIdViewParam = Integer.parseInt(paramValue);
                    ComponentInstance componentInstance = findByQrId(qrIdViewParam);
                    setCurrent(componentInstance);
                    if (componentInstance == null) {
                        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
                        if (sessionUser != null) {
                            SessionUtility.navigateTo("/views/componentInstance/create.xhtml?faces-redirect=true");
                        } else {
                            SessionUtility.pushViewOnStack("/views/componentInstance/create.xhtml");
                            SessionUtility.navigateTo("/views/login.xhtml?faces-redirect=true");
                        }
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    throw new InvalidRequest("Invalid value supplied for QR id: " + paramValue);
                }
            } else if (current == null) {
                throw new InvalidRequest("Component instance has not been selected.");
            }
            return current;
        }
    }

    @Override
    public List<ComponentInstance> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public String prepareEdit(ComponentInstance componentInstance) {
        locationList = locationFacade.findAll();
        return super.prepareEdit(componentInstance);
    }

    @Override
    public void prepareEntityView(ComponentInstance componentInstance) {
        if (componentInstance == null) {
            return;
        }
        componentInstance.clearPropertyValueCache();
        prepareComponentInstanceImageList(componentInstance);
        prepareComponentInstancePropertyValueDisplay(componentInstance);
    }

    @Override
    public void prepareEntityInsert(ComponentInstance componentInstance) throws ObjectAlreadyExists {
        componentInstance.resetAttributesToNullIfEmpty();
        componentInstanceFacade.checkUniqueness(componentInstance);
    }

    @Override
    public String update() {
        ComponentInstance componentInstance = getCurrent();
        componentInstance.resetAttributesToNullIfEmpty();
        return super.update();
    }

    @Override
    public void prepareEntityUpdate(ComponentInstance componentInstance) throws ObjectAlreadyExists {
        componentInstance.resetAttributesToNullIfEmpty();
        componentInstanceFacade.checkUniqueness(componentInstance);
        EntityInfo entityInfo = componentInstance.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);

        // Compare properties with what is in the db
        List<PropertyValue> originalPropertyValueList = componentInstanceFacade.findById(componentInstance.getId()).getPropertyValueList();
        List<PropertyValue> newPropertyValueList = componentInstance.getPropertyValueList();
        logger.debug("Verifying properties for component instance id " + componentInstance.getId());
        for (PropertyValue newPropertyValue : newPropertyValueList) {
            int index = originalPropertyValueList.indexOf(newPropertyValue);
            if (index >= 0) {
                // Original property was there.
                PropertyValue originalPropertyValue = originalPropertyValueList.get(index);
                if (!newPropertyValue.equalsByTagAndValueAndUnitsAndDescription(originalPropertyValue)) {
                    // Property value was modified.
                    logger.debug("Property value for type " + originalPropertyValue.getPropertyType()
                            + " was modified (original value: " + originalPropertyValue + "; new value: " + newPropertyValue + ")");
                    newPropertyValue.setEnteredByUser(entityInfo.getLastModifiedByUser());
                    newPropertyValue.setEnteredOnDateTime(entityInfo.getLastModifiedOnDateTime());

                    // Save history
                    List<PropertyValueHistory> propertyValueHistoryList = newPropertyValue.getPropertyValueHistoryList();
                    PropertyValueHistory propertyValueHistory = new PropertyValueHistory();
                    propertyValueHistory.updateFromPropertyValue(originalPropertyValue);
                    propertyValueHistoryList.add(propertyValueHistory);
                }
            } else {
                // New property value.
                logger.debug("Adding new property value for type " + newPropertyValue.getPropertyType()
                        + ": " + newPropertyValue);
                newPropertyValue.setEnteredByUser(entityInfo.getLastModifiedByUser());
                newPropertyValue.setEnteredOnDateTime(entityInfo.getLastModifiedOnDateTime());
            }
        }
        componentInstance.clearPropertyValueCache();
        prepareComponentInstanceImageList(componentInstance);
        logger.debug("Updating component instance id " + componentInstance.getId()
                + " (user: " + entityInfo.getLastModifiedByUser().getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdateOnRemoval(ComponentInstance componentInstance
    ) {
        EntityInfo entityInfo = componentInstance.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
    }

    public String prepareViewFromComponent(ComponentInstance componentInstance) {
        logger.debug("Preparing component instance view from component view page");
        prepareView(componentInstance);
        return "/views/componentInstance/view.xhtml?faces-redirect=true";
    }

    public String prepareViewToComponent(ComponentInstance componentInstance) {
        return "/views/component/view.xhtml?id=" + componentInstance.getComponent().getId() + "&faces-redirect=true";
    }

    public void prepareAddLog(ComponentInstance componentInstance) {
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        Log logEntry = new Log();
        logEntry.setEnteredByUser(lastModifiedByUser);
        logEntry.setEnteredOnDateTime(lastModifiedOnDateTime);
        List<Log> componentInstanceLogList = componentInstance.getLogList();
        componentInstanceLogList.add(0, logEntry);
    }

    public List<Log> getLogList() {
        ComponentInstance componentInstance = getCurrent();
        List<Log> componentInstanceLogList = componentInstance.getLogList();
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            if (settingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionUser(sessionUser);
                settingsTimestamp = new Date();
            }
        }
        return componentInstanceLogList;
    }

    public void saveLogList() {
        update();
    }

    public void deleteLog(Log componentInstanceLog) {
        ComponentInstance componentInstance = getCurrent();
        List<Log> componentInstanceLogList = componentInstance.getLogList();
        componentInstanceLogList.remove(componentInstanceLog);
        updateOnRemoval();
    }

    public void prepareAddProperty() {
        ComponentInstance componentInstance = getCurrent();
        List<PropertyValue> propertyList = componentInstance.getPropertyValueList();
        PropertyValue property = new PropertyValue();
        propertyList.add(property);
        componentInstance.resetImagePropertyList();
    }

    public void savePropertyList() {
        update();
    }

    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        ComponentInstance componentInstance = getCurrent();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        List<PropertyValue> propertyValueList = componentInstance.getPropertyValueList();
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

    public void deleteProperty(PropertyValue componentInstanceProperty) {
        ComponentInstance componentInstance = getCurrent();
        List<PropertyValue> componentInstancePropertyList = componentInstance.getPropertyValueList();
        componentInstancePropertyList.remove(componentInstanceProperty);
        updateOnRemoval();
    }

    public String destroyAndReturnComponentView(ComponentInstance componentInstance) {
        Component component = componentInstance.getComponent();
        setCurrent(componentInstance);
        try {
            logger.debug("Destroying " + getCurrentEntityInstanceName());
            getFacade().remove(componentInstance);
            SessionUtility.addInfoMessage("Success", "Deleted component instance id " + componentInstance.getId() + ".");
            return "/views/component/view.xhtml?faces-redirect=true?id=" + component.getId();
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    private void resetComponentInstancePropertyTypeIdIndexMappings() {
        ComponentInstance.setPropertyTypeIdIndex(1, displayPropertyTypeId1);
        ComponentInstance.setPropertyTypeIdIndex(2, displayPropertyTypeId2);
        ComponentInstance.setPropertyTypeIdIndex(3, displayPropertyTypeId3);
        ComponentInstance.setPropertyTypeIdIndex(4, displayPropertyTypeId4);
        ComponentInstance.setPropertyTypeIdIndex(5, displayPropertyTypeId5);
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLocationDetails = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationDetailsSettingTypeKey).getDefaultValue());

        displayQrId = Boolean.parseBoolean(settingTypeMap.get(DisplayQrIdSettingTypeKey).getDefaultValue());
        displaySerialNumber = Boolean.parseBoolean(settingTypeMap.get(DisplaySerialNumberSettingTypeKey).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        filterByComponent = settingTypeMap.get(FilterByComponentSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();

        filterByLocation = settingTypeMap.get(FilterByLocationSettingTypeKey).getDefaultValue();
        filterByLocationDetails = settingTypeMap.get(FilterByLocationDetailsSettingTypeKey).getDefaultValue();
        filterByQrId = settingTypeMap.get(FilterByQrIdSettingTypeKey).getDefaultValue();
        filterBySerialNumber = settingTypeMap.get(FilterBySerialNumberSettingTypeKey).getDefaultValue();
        filterByTag = settingTypeMap.get(FilterByTagSettingTypeKey).getDefaultValue();

        filterByPropertyValue1 = settingTypeMap.get(FilterByPropertyValue1SettingTypeKey).getDefaultValue();
        filterByPropertyValue2 = settingTypeMap.get(FilterByPropertyValue2SettingTypeKey).getDefaultValue();
        filterByPropertyValue3 = settingTypeMap.get(FilterByPropertyValue3SettingTypeKey).getDefaultValue();
        filterByPropertyValue4 = settingTypeMap.get(FilterByPropertyValue4SettingTypeKey).getDefaultValue();
        filterByPropertyValue5 = settingTypeMap.get(FilterByPropertyValue5SettingTypeKey).getDefaultValue();

    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        displayLocationDetails = sessionUser.getUserSettingValueAsBoolean(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        displayQrId = sessionUser.getUserSettingValueAsBoolean(DisplayQrIdSettingTypeKey, displayQrId);
        displaySerialNumber = sessionUser.getUserSettingValueAsBoolean(DisplaySerialNumberSettingTypeKey, displaySerialNumber);

        displayPropertyTypeId1 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        filterByComponent = sessionUser.getUserSettingValueAsString(FilterByComponentSettingTypeKey, filterByComponent);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByLocation = sessionUser.getUserSettingValueAsString(FilterByLocationSettingTypeKey, filterByLocation);
        filterByLocationDetails = sessionUser.getUserSettingValueAsString(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
        filterByQrId = sessionUser.getUserSettingValueAsString(FilterByQrIdSettingTypeKey, filterByQrId);
        filterBySerialNumber = sessionUser.getUserSettingValueAsString(FilterBySerialNumberSettingTypeKey, filterBySerialNumber);
        filterByTag = sessionUser.getUserSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);

        filterByPropertyValue1 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        filterByPropertyValue2 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        filterByPropertyValue3 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        filterByPropertyValue4 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        filterByPropertyValue5 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);

        resetComponentInstancePropertyTypeIdIndexMappings();

    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByComponent = (String) filters.get("component.name");
        filterByLocation = (String) filters.get("location.name");
        filterByLocationDetails = (String) filters.get("locationDetails");
        filterByQrId = (String) filters.get("qrId");
        filterBySerialNumber = (String) filters.get("serialNumber");
        filterByTag = (String) filters.get("tag");

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

        sessionUser.setUserSettingValue(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        sessionUser.setUserSettingValue(DisplayQrIdSettingTypeKey, displayQrId);
        sessionUser.setUserSettingValue(DisplaySerialNumberSettingTypeKey, displaySerialNumber);

        sessionUser.setUserSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        sessionUser.setUserSettingValue(FilterByComponentSettingTypeKey, filterByComponent);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        sessionUser.setUserSettingValue(FilterByLocationSettingTypeKey, filterByLocation);
        sessionUser.setUserSettingValue(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
        sessionUser.setUserSettingValue(FilterByQrIdSettingTypeKey, filterByQrId);
        sessionUser.setUserSettingValue(FilterBySerialNumberSettingTypeKey, filterBySerialNumber);
        sessionUser.setUserSettingValue(FilterByTagSettingTypeKey, filterByTag);

        sessionUser.setUserSettingValue(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        sessionUser.setUserSettingValue(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        sessionUser.setUserSettingValue(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        sessionUser.setUserSettingValue(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        sessionUser.setUserSettingValue(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByComponent = null;
        filterByLocation = null;
        filterByLocationDetails = null;
        filterByQrId = null;
        filterBySerialNumber = null;
        filterByTag = null;
        filterByPropertyValue1 = null;
        filterByPropertyValue2 = null;
        filterByPropertyValue3 = null;
        filterByPropertyValue4 = null;
        filterByPropertyValue5 = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    @FacesConverter(forClass = ComponentInstance.class)
    public static class ComponentInstanceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                ComponentInstanceController controller = (ComponentInstanceController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "componentInstanceController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component instance object.");
                return null;
            }
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
            if (object instanceof ComponentInstance) {
                ComponentInstance o = (ComponentInstance) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentInstance.class.getName());
            }
        }

    }

    public List<Location> completeLocation(String query) {
        return LocationUtility.filterLocation(query, locationList);
    }

    public void selectLocation(Location location) {
        ComponentInstance componentInstance = getCurrent();
        if (componentInstance != null) {
            componentInstance.setLocation(location);
        }
    }

    public void selectComponent(Component component) {
        ComponentInstance componentInstance = getCurrent();
        componentInstance.setComponent(component);
        componentSelectOneMenu.setSubmittedValue(component);
    }

    // This listener is accessed either after selection made in dialog,
    // or from selection menu.    
    public void selectLocationValueChangeListener(ValueChangeEvent valueChangeEvent) {
        ComponentInstance componentInstance = getCurrent();
        if (componentInstance == null) {
            return;
        }
        Location existingLocation = componentInstance.getLocation();
        Location newEventLocation = null;
        Location oldEventLocation = null;

        Object newValue = valueChangeEvent.getNewValue();
        if (newValue != null) {
            newEventLocation = (Location) newValue;
        }
        Object oldValue = valueChangeEvent.getOldValue();
        if (oldValue != null) {
            oldEventLocation = (Location) oldValue;
        }

        if (ObjectUtility.equals(existingLocation, oldEventLocation)) {
            // change via menu
            componentInstance.setLocation(newEventLocation);
        } else {
            // change via dialog
            componentInstance.setLocation(oldEventLocation);
        }
    }

    public Boolean getDisplayLocationDetails() {
        return displayLocationDetails;
    }

    public void setDisplayLocationDetails(Boolean displayLocationDetails) {
        this.displayLocationDetails = displayLocationDetails;
    }

    public Boolean getDisplayQrId() {
        return displayQrId;
    }

    public void setDisplayQrId(Boolean displayQrId) {
        this.displayQrId = displayQrId;
    }

    public Boolean getDisplaySerialNumber() {
        return displaySerialNumber;
    }

    public void setDisplaySerialNumber(Boolean displaySerialNumber) {
        this.displaySerialNumber = displaySerialNumber;
    }

    public String getFilterByComponent() {
        return filterByComponent;
    }

    public void setFilterByComponent(String filterByComponent) {
        this.filterByComponent = filterByComponent;
    }

    public String getFilterByLocation() {
        return filterByLocation;
    }

    public void setFilterByLocation(String filterByLocation) {
        this.filterByLocation = filterByLocation;
    }

    public String getFilterByLocationDetails() {
        return filterByLocationDetails;
    }

    public void setFilterByLocationDetails(String filterByLocationDetails) {
        this.filterByLocationDetails = filterByLocationDetails;
    }

    public String getFilterByQrId() {
        return filterByQrId;
    }

    public void setFilterByQrId(String filterByQrId) {
        this.filterByQrId = filterByQrId;
    }

    public String getFilterBySerialNumber() {
        return filterBySerialNumber;
    }

    public void setFilterBySerialNumber(String filterBySerialNumber) {
        this.filterBySerialNumber = filterBySerialNumber;
    }

    public String getFilterByTag() {
        return filterByTag;
    }

    public void setFilterByTag(String filterByTag) {
        this.filterByTag = filterByTag;
    }

    public Boolean getDisplayComponentInstanceImages() {
        List<PropertyValue> componentInstanceImageList = getComponentInstanceImageList();
        return (componentInstanceImageList != null && !componentInstanceImageList.isEmpty());
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

    public Integer getQrIdViewParam() {
        return qrIdViewParam;
    }

    public void setQrIdViewParam(Integer qrIdViewParam) {
        this.qrIdViewParam = qrIdViewParam;
    }

    public List<PropertyValue> prepareComponentInstanceImageList(ComponentInstance componentInstance) {
        if (componentInstance == null) {
            return null;
        }
        List<PropertyValue> componentInstanceImageList = PropertyValueUtility.prepareImagePropertyValueList(componentInstance.getPropertyValueList());
        Component component = componentInstance.getComponent();
        if (component != null) {
            List<PropertyValue> componentImageList = PropertyValueUtility.prepareImagePropertyValueList(component.getPropertyValueList());
            componentInstanceImageList.addAll(componentImageList);
        }
        componentInstance.setImagePropertyList(componentInstanceImageList);
        return componentInstanceImageList;
    }

    public void prepareComponentInstancePropertyValueDisplay(ComponentInstance componentInstance) {
        if (componentInstance == null) {
            return;
        }        
        List<PropertyValue> propertyValueList = componentInstance.getPropertyValueList();
        for (PropertyValue propertyValue : propertyValueList) {
            PropertyValueUtility.configurePropertyValueDisplay(propertyValue);
        }
    }

    public List<PropertyValue> getComponentInstanceImageList() {
        ComponentInstance componentInstance = getCurrent();
        if (componentInstance == null) {
            return null;
        }
        List<PropertyValue> componentInstanceImageList = componentInstance.getImagePropertyList();
        if (componentInstanceImageList == null) {
            componentInstanceImageList = prepareComponentInstanceImageList(componentInstance);
        }
        return componentInstanceImageList;
    }

    public SelectOneMenu getComponentSelectOneMenu() {
        return componentSelectOneMenu;
    }

    public void setComponentSelectOneMenu(SelectOneMenu componentTypeSelectOneMenu) {
        this.componentSelectOneMenu = componentTypeSelectOneMenu;
    }

    public DataTable getComponentInstancePropertyValueListDataTable() {
        if (userSettingsChanged() || isListDataModelReset()) {
            componentInstancePropertyValueListDataTable = new DataTable();
        }
        return componentInstancePropertyValueListDataTable;
    }

    public void setComponentInstancePropertyValueListDataTable(DataTable componentInstancePropertyValueListDataTable) {
        this.componentInstancePropertyValueListDataTable = componentInstancePropertyValueListDataTable;
    }

    public DataTable getComponentPropertyValueListDataTable() {
        if (userSettingsChanged() || isListDataModelReset()) {
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

}
