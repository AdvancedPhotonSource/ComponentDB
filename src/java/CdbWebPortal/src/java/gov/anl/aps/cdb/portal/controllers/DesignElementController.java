package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.DesignElementType;
import gov.anl.aps.cdb.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentFacade;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import gov.anl.aps.cdb.portal.model.db.beans.DesignElementFacade;
import gov.anl.aps.cdb.portal.model.db.beans.DesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.LocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Location;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ComponentUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.DesignUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LocationUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
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
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.event.SelectEvent;

@Named("designElementController")
@SessionScoped
public class DesignElementController extends CrudEntityController<DesignElement, DesignElementFacade> implements Serializable {

    private static final String DisplayChildDesignSettingTypeKey = "DesignElement.List.Display.ChildDesign";
    private static final String DisplayComponentSettingTypeKey = "DesignElement.List.Display.Component";
    private static final String DisplayComponentTypeSettingTypeKey = "DesignElement.List.Display.ComponentType";
    private static final String DisplayCreatedByUserSettingTypeKey = "DesignElement.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "DesignElement.List.Display.CreatedOnDateTime";
    private static final String DisplayDescriptionSettingTypeKey = "DesignElement.List.Display.Description";
    private static final String DisplayFlatTableViewSettingTypeKey = "DesignElement.List.Display.FlatTableView";
    private static final String DisplayIdSettingTypeKey = "DesignElement.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "DesignElement.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "DesignElement.List.Display.LastModifiedOnDateTime";
    private static final String DisplayLocationSettingTypeKey = "DesignElement.List.Display.Location";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "DesignElement.List.Display.NumberOfItemsPerPage";
    private static final String DisplayOwnerUserSettingTypeKey = "DesignElement.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "DesignElement.List.Display.OwnerGroup";
    private static final String DisplaySortOrderSettingTypeKey = "DesignElement.List.Display.SortOrder";

    private static final String FilterByChildDesignSettingTypeKey = "DesignElement.List.FilterBy.ChildDesign";
    private static final String FilterByComponentSettingTypeKey = "DesignElement.List.FilterBy.Component";
    private static final String FilterByComponentTypeSettingTypeKey = "DesignElement.List.FilterBy.ComponentType";
    private static final String FilterByCreatedByUserSettingTypeKey = "DesignElement.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "DesignElement.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "DesignElement.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "DesignElement.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "DesignElement.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByLocationSettingTypeKey = "DesignElement.List.FilterBy.Location";
    private static final String FilterByNameSettingTypeKey = "DesignElement.List.FilterBy.Name";
    private static final String FilterByOwnerUserSettingTypeKey = "DesignElement.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "DesignElement.List.FilterBy.OwnerGroup";
    private static final String FilterBySortOrderSettingTypeKey = "DesignElement.List.FilterBy.SortOrder";

    private static final Logger logger = Logger.getLogger(DesignElementController.class.getName());

    @EJB
    private DesignElementFacade designElementFacade;

    @EJB
    private LocationFacade locationFacade;

    @EJB
    private ComponentFacade componentFacade;

    @EJB
    private DesignFacade designFacade;

    private Boolean displayChildDesign = null;
    private Boolean displayComponent = null;
    private Boolean displayComponentType = null;
    private Boolean displayFlatTableView = null;
    private Boolean displayLocation = null;
    private Boolean displaySortOrder = null;

    private String filterByChildDesign = null;
    private String filterByComponent = null;
    private String filterByComponentType = null;
    private String filterByLocation = null;
    private String filterBySortOrder = null;

    private List<Location> selectLocationCandidateList = null;
    private List<Component> selectComponentCandidateList = null;
    private List<Design> selectChildDesignCandidateList = null;

    private Design selectedParentDesign = null;

    private SelectOneMenu componentSelectOneMenu;
    private DataTable designElementPropertyValueListDataTable = null;
    private DataTable componentPropertyValueListDataTable = null;
    private DataTable childDesignPropertyValueListDataTable = null;
    private List<PropertyValue> filteredPropertyValueList = null;

    public DesignElementController() {
    }

    @Override
    protected DesignElementFacade getFacade() {
        return designElementFacade;
    }

    @Override
    protected DesignElement createEntityInstance() {
        DesignElement designElement = new DesignElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        designElement.setEntityInfo(entityInfo);

        // clear location list
        selectLocationCandidateList = null;
        selectChildDesignCandidateList = null;
        selectComponentCandidateList = null;
        return designElement;
    }

    @Override
    public void selectByRequestParams() {
        if (idViewParam != null) {
            DesignElement designElement = findById(idViewParam);
            setCurrent(designElement);
            prepareEntityView(designElement);
            idViewParam = null;
        }
    }

    public DesignElement findById(Integer id) {
        return designElementFacade.findById(id);
    }

    @Override
    public String getEntityTypeName() {
        return "designElement";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "design element";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<DesignElement> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(DesignElement designElement) throws ObjectAlreadyExists {
    }

    @Override
    public void prepareEntityUpdate(DesignElement designElement) throws ObjectAlreadyExists {
        EntityInfo entityInfo = designElement.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
        prepareDesignElementImageList(designElement);
    }

    @Override
    public void prepareEntityUpdateOnRemoval(DesignElement designElement) {
        EntityInfo entityInfo = designElement.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
        prepareDesignElementImageList(designElement);
    }

    public String prepareViewFromDesign(DesignElement designElement) {
        logger.debug("Preparing design element view from design view page");
        prepareView(designElement);
        return "/views/designElement/view.xhtml?faces-redirect=true";
    }

    public String prepareViewToDesign(DesignElement designElement) {
        return "/views/design/view.xhtml?faces-redirect=true?id=" + designElement.getParentDesign().getId();
    }

    public void prepareAddLog(DesignElement designElement) {
        Log logEntry = LogUtility.createLogEntry();
        List<Log> designElementLogList = designElement.getLogList();
        designElementLogList.add(0, logEntry);
    }

    public List<Log> getLogList() {
        DesignElement designElement = getCurrent();
        List<Log> designElementLogList = designElement.getLogList();
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            if (settingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionUser(sessionUser);
                settingsTimestamp = new Date();
            }
        }
        return designElementLogList;
    }

    public void saveLogList() {
        update();
    }

    public void deleteLog(Log designElementLog) {
        DesignElement designElement = getCurrent();
        List<Log> designElementLogList = designElement.getLogList();
        designElementLogList.remove(designElementLog);
        updateOnRemoval();
    }

    public void prepareAddProperty() {
        DesignElement designElement = getCurrent();
        List<PropertyValue> propertyList = designElement.getPropertyValueList();
        PropertyValue property = new PropertyValue();
        propertyList.add(property);
    }

    public void savePropertyList() {
        update();
    }

    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        DesignElement designElement = getCurrent();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        List<PropertyValue> propertyValueList = designElement.getPropertyValueList();
        for (PropertyType propertyType : propertyTypeList) {
            PropertyValue propertyValue = new PropertyValue();
            propertyValue.setPropertyType(propertyType);
            propertyValue.setValue(propertyType.getDefaultValue());
            propertyValue.setUnits(propertyType.getDefaultUnits());
            propertyValue.setEnteredByUser(lastModifiedByUser);
            propertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
            propertyValueList.add(propertyValue);
        }
    }

    public void deleteProperty(PropertyValue designElementProperty) {
        DesignElement designElement = getCurrent();
        List<PropertyValue> designElementPropertyList = designElement.getPropertyValueList();
        designElementPropertyList.remove(designElementProperty);
        updateOnRemoval();
    }

    public String destroyAndReturnDesignView(DesignElement designElement) {
        Design parentDesign = designElement.getParentDesign();
        setCurrent(designElement);
        try {
            logger.debug("Destroying " + designElement.getName());
            getFacade().remove(designElement);
            SessionUtility.addInfoMessage("Success", "Deleted design element id " + designElement.getId() + ".");
            return "/views/design/view.xhtml?faces-redirect=true?id=" + parentDesign.getId();
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayFlatTableView = Boolean.parseBoolean(settingTypeMap.get(DisplayFlatTableViewSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());

        displayChildDesign = Boolean.parseBoolean(settingTypeMap.get(DisplayChildDesignSettingTypeKey).getDefaultValue());
        displayComponent = Boolean.parseBoolean(settingTypeMap.get(DisplayComponentSettingTypeKey).getDefaultValue());
        displayComponentType = Boolean.parseBoolean(settingTypeMap.get(DisplayComponentTypeSettingTypeKey).getDefaultValue());
        displayLocation = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationSettingTypeKey).getDefaultValue());
        displaySortOrder = Boolean.parseBoolean(settingTypeMap.get(DisplaySortOrderSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();

        filterByChildDesign = settingTypeMap.get(FilterByChildDesignSettingTypeKey).getDefaultValue();
        filterByComponent = settingTypeMap.get(FilterByComponentSettingTypeKey).getDefaultValue();
        filterByComponentType = settingTypeMap.get(FilterByComponentTypeSettingTypeKey).getDefaultValue();
        filterByLocation = settingTypeMap.get(FilterByLocationSettingTypeKey).getDefaultValue();
        filterBySortOrder = settingTypeMap.get(FilterBySortOrderSettingTypeKey).getDefaultValue();

    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayFlatTableView = sessionUser.getUserSettingValueAsBoolean(DisplayFlatTableViewSettingTypeKey, displayFlatTableView);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        displayChildDesign = sessionUser.getUserSettingValueAsBoolean(DisplayChildDesignSettingTypeKey, displayChildDesign);
        displayComponent = sessionUser.getUserSettingValueAsBoolean(DisplayComponentSettingTypeKey, displayComponent);
        displayComponentType = sessionUser.getUserSettingValueAsBoolean(DisplayComponentTypeSettingTypeKey, displayComponentType);
        displayLocation = sessionUser.getUserSettingValueAsBoolean(DisplayLocationSettingTypeKey, displayLocation);
        displaySortOrder = sessionUser.getUserSettingValueAsBoolean(DisplaySortOrderSettingTypeKey, displaySortOrder);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByChildDesign = sessionUser.getUserSettingValueAsString(FilterByChildDesignSettingTypeKey, filterByChildDesign);
        filterByComponent = sessionUser.getUserSettingValueAsString(FilterByComponentSettingTypeKey, filterByComponent);
        filterByComponentType = sessionUser.getUserSettingValueAsString(FilterByComponentTypeSettingTypeKey, filterByComponentType);
        filterByLocation = sessionUser.getUserSettingValueAsString(FilterByLocationSettingTypeKey, filterByLocation);
        filterBySortOrder = sessionUser.getUserSettingValueAsString(FilterBySortOrderSettingTypeKey, filterBySortOrder);

    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterByChildDesign = filters.get("childDesign");
        filterByComponent = filters.get("component");
        filterByComponentType = filters.get("componentType");
        filterByLocation = filters.get("location");
        filterBySortOrder = filters.get("sortOrder");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayFlatTableViewSettingTypeKey, displayFlatTableView);
        sessionUser.setUserSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        sessionUser.setUserSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        sessionUser.setUserSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        sessionUser.setUserSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        sessionUser.setUserSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        sessionUser.setUserSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        sessionUser.setUserSettingValue(DisplayChildDesignSettingTypeKey, displayChildDesign);
        sessionUser.setUserSettingValue(DisplayComponentSettingTypeKey, displayComponent);
        sessionUser.setUserSettingValue(DisplayComponentTypeSettingTypeKey, displayComponentType);
        sessionUser.setUserSettingValue(DisplayLocationSettingTypeKey, displayLocation);
        sessionUser.setUserSettingValue(DisplaySortOrderSettingTypeKey, displaySortOrder);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        sessionUser.setUserSettingValue(FilterByChildDesignSettingTypeKey, filterByChildDesign);
        sessionUser.setUserSettingValue(FilterByComponentSettingTypeKey, filterByComponent);
        sessionUser.setUserSettingValue(FilterByComponentTypeSettingTypeKey, filterByComponentType);
        sessionUser.setUserSettingValue(FilterByLocationSettingTypeKey, filterByLocation);
        sessionUser.setUserSettingValue(FilterBySortOrderSettingTypeKey, filterBySortOrder);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByChildDesign = null;
        filterByComponent = null;
        filterByComponentType = null;
        filterByLocation = null;
        filterBySortOrder = null;
    }

    @FacesConverter(forClass = DesignElement.class)
    public static class DesignElementControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                DesignElementController controller = (DesignElementController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "designElementController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to design element object.");
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
            if (object instanceof DesignElement) {
                DesignElement o = (DesignElement) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + DesignElement.class.getName());
            }
        }

    }

    public Boolean getDisplayChildDesign() {
        return displayChildDesign;
    }

    public void setDisplayChildDesign(Boolean displayChildDesign) {
        this.displayChildDesign = displayChildDesign;
    }

    public Boolean getDisplayComponent() {
        return displayComponent;
    }

    public void setDisplayComponent(Boolean displayComponent) {
        this.displayComponent = displayComponent;
    }

    public Boolean getDisplayComponentType() {
        return displayComponentType;
    }

    public void setDisplayComponentType(Boolean displayComponentType) {
        this.displayComponentType = displayComponentType;
    }

    public Boolean getDisplayFlatTableView() {
        return displayFlatTableView;
    }

    public void setDisplayFlatTableView(Boolean displayFlatTableView) {
        this.displayFlatTableView = displayFlatTableView;
    }

    public Boolean getDisplayLocation() {
        return displayLocation;
    }

    public void setDisplayLocation(Boolean displayLocation) {
        this.displayLocation = displayLocation;
    }

    public Boolean getDisplaySortOrder() {
        return displaySortOrder;
    }

    public void setDisplaySortOrder(Boolean displaySortOrder) {
        this.displaySortOrder = displaySortOrder;
    }

    public String getFilterByChildDesign() {
        return filterByChildDesign;
    }

    public void setFilterByChildDesign(String filterByChildDesign) {
        this.filterByChildDesign = filterByChildDesign;
    }

    public String getFilterByComponent() {
        return filterByComponent;
    }

    public void setFilterByComponent(String filterByComponent) {
        this.filterByComponent = filterByComponent;
    }

    public String getFilterByComponentType() {
        return filterByComponentType;
    }

    public void setFilterByComponentType(String filterByComponentType) {
        this.filterByComponentType = filterByComponentType;
    }

    public String getFilterByLocation() {
        return filterByLocation;
    }

    public void setFilterByLocation(String filterByLocation) {
        this.filterByLocation = filterByLocation;
    }

    public String getFilterBySortOrder() {
        return filterBySortOrder;
    }

    public void setFilterBySortOrder(String filterBySortOrder) {
        this.filterBySortOrder = filterBySortOrder;
    }

    public SelectOneMenu getComponentSelectOneMenu() {
        return componentSelectOneMenu;
    }

    public void setComponentSelectOneMenu(SelectOneMenu componentSelectOneMenu) {
        this.componentSelectOneMenu = componentSelectOneMenu;
    }

    public Design getSelectedParentDesign() {
        return selectedParentDesign;
    }

    public void setSelectedParentDesign(Design selectedParentDesign) {
        selectChildDesignCandidateList = designFacade.findAll();
        this.selectedParentDesign = selectedParentDesign;
        if (selectedParentDesign == null) {
            return;
        }
        selectChildDesignCandidateList.remove(selectedParentDesign);
    }

    // This listener is accessed either after selection made in dialog,
    // or from selection menu.    
    public void selectLocationValueChangeListener(ValueChangeEvent valueChangeEvent) {
        DesignElement designElement = getCurrent();
        if (designElement == null || valueChangeEvent == null) {
            return;
        }
        Location existingLocation = designElement.getLocation();
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
            designElement.setLocation(newEventLocation);
        } else {
            // change via dialog
            designElement.setLocation(oldEventLocation);
        }
    }

    public List<Location> getSelectLocationCandidateList() {
        if (selectLocationCandidateList == null) {
            selectLocationCandidateList = locationFacade.findAll();
        }
        return selectLocationCandidateList;
    }

    public List<Location> completeLocation(String query) {
        return LocationUtility.filterLocation(query, getSelectLocationCandidateList());
    }

    public void selectLocation(Location location) {
        DesignElement designElement = getCurrent();
        if (designElement != null) {
            designElement.setLocation(location);
        }
    }

    // This listener is accessed either after selection made in dialog,
    // or from selection menu.    
    public void selectComponentValueChangeListener(ValueChangeEvent valueChangeEvent) {
        DesignElement designElement = getCurrent();
        if (designElement == null || valueChangeEvent == null) {
            return;
        }

        Component existingComponent = designElement.getComponent();
        Component newEventComponent = null;
        Component oldEventComponent = null;

        Object newValue = valueChangeEvent.getNewValue();
        if (newValue != null) {
            newEventComponent = (Component) newValue;
        }
        Object oldValue = valueChangeEvent.getOldValue();
        if (oldValue != null) {
            oldEventComponent = (Component) oldValue;
        }

        if (ObjectUtility.equals(existingComponent, oldEventComponent)) {
            // change via menu
            designElement.setComponent(newEventComponent);
        } else {
            // change via dialog
            designElement.setComponent(oldEventComponent);
        }
    }

    public void selectComponentListener(SelectEvent selectEvent) {
        DesignElement designElement = getCurrent();
        if (designElement == null) {
            return;
        }
        AutoComplete autoComplete = (AutoComplete) selectEvent.getSource();
        Object itemValue = autoComplete.getItemValue();
        if (itemValue != null) {
            Component component = (Component) itemValue;
            designElement.setComponent(component);
            designElement.setChildDesign(null);
        }
    }

    public void unselectComponentListener(SelectEvent selectEvent) {
        DesignElement designElement = getCurrent();
        if (designElement == null) {
            return;
        }
        designElement.setComponent(null);
    }

    public List<Component> getSelectComponentCandidateList() {
        if (selectComponentCandidateList == null) {
            selectComponentCandidateList = componentFacade.findAll();
        }
        return selectComponentCandidateList;
    }

    public List<Component> completeComponent(String query) {
        return ComponentUtility.filterComponent(query, getSelectComponentCandidateList());
    }

    public void selectComponent(Component component) {
        DesignElement designElement = getCurrent();
        if (designElement != null) {
            designElement.setComponent(component);
            designElement.setChildDesign(null);
        }
    }

    // This listener is accessed either after selection made in dialog,
    // or from selection menu.    
    public void selectChildDesignValueChangeListener(ValueChangeEvent valueChangeEvent) {
        DesignElement designElement = getCurrent();
        if (designElement == null || valueChangeEvent == null) {
            return;
        }
        Design existingChildDesign = designElement.getChildDesign();
        Design newEventChildDesign = null;
        Design oldEventChildDesign = null;

        Object newValue = valueChangeEvent.getNewValue();
        if (newValue != null) {
            newEventChildDesign = (Design) newValue;
        }
        Object oldValue = valueChangeEvent.getOldValue();
        if (oldValue != null) {
            oldEventChildDesign = (Design) oldValue;
        }

        if (ObjectUtility.equals(existingChildDesign, oldEventChildDesign)) {
            // change via menu
            designElement.setChildDesign(newEventChildDesign);
        } else {
            // change via dialog
            designElement.setChildDesign(oldEventChildDesign);
        }
    }

    public void selectChildDesignListener(SelectEvent selectEvent) {
        DesignElement designElement = getCurrent();
        if (designElement == null) {
            return;
        }
        AutoComplete autoComplete = (AutoComplete) selectEvent.getSource();
        Object itemValue = autoComplete.getItemValue();
        if (itemValue != null) {
            Design childDesign = (Design) itemValue;
            designElement.setChildDesign(childDesign);
            designElement.setComponent(null);
        }
    }

    public void unselectChildDesignListener(SelectEvent selectEvent) {
        DesignElement designElement = getCurrent();
        if (designElement == null) {
            return;
        }
        designElement.setChildDesign(null);
    }

    public List<Design> getSelectChildDesignCandidateList() {
        if (selectChildDesignCandidateList == null) {
            selectChildDesignCandidateList = designFacade.findAll();
        }
        return selectChildDesignCandidateList;
    }

    public List<Design> completeChildDesign(String query) {
        return DesignUtility.filterDesign(query, getSelectChildDesignCandidateList());
    }

    public void selectChildDesign(Design childDesign) {
        DesignElement designElement = getCurrent();
        if (designElement != null) {
            designElement.setChildDesign(childDesign);
            designElement.setComponent(null);
        }
    }

    public DataTable getDesignElementPropertyValueListDataTable() {
        if (userSettingsChanged() || isListDataModelReset()) {
            designElementPropertyValueListDataTable = new DataTable();
        }
        return designElementPropertyValueListDataTable;
    }

    public void setDesignElementPropertyValueListDataTable(DataTable designElementPropertyValueListDataTable) {
        this.designElementPropertyValueListDataTable = designElementPropertyValueListDataTable;
    }

    public List<PropertyValue> getFilteredPropertyValueList() {
        return filteredPropertyValueList;
    }

    public void setFilteredPropertyValueList(List<PropertyValue> filteredPropertyValueList) {
        this.filteredPropertyValueList = filteredPropertyValueList;
    }

    public Boolean getDisplayChildDesignSelection(DesignElement designElement) {
        if (designElement == null) {
            return true;
        }
        DesignElementType designElementType = designElement.getContainedObjectType();
        return (designElementType == null || designElementType.equals(DesignElementType.DESIGN));
    }

    public Boolean getDisplayComponentSelection(DesignElement designElement) {
        if (designElement == null) {
            return true;
        }
        DesignElementType designElementType = designElement.getContainedObjectType();
        return (designElementType == null || designElementType.equals(DesignElementType.COMPONENT));
    }

    public Boolean getDisplayDesignElementImages() {
        List<PropertyValue> designElementImageList = getDesignElementImageList();
        return (designElementImageList != null && !designElementImageList.isEmpty());
    }

    public List<PropertyValue> prepareDesignElementImageList(DesignElement designElement) {
        List<PropertyValue> componentInstanceImageList = PropertyValueUtility.prepareImagePropertyValueList(designElement.getPropertyValueList());
        designElement.setImagePropertyList(componentInstanceImageList);
        return componentInstanceImageList;
    }

    public void prepareComponentInstancePropertyValueDisplay(DesignElement designElement) {
        List<PropertyValue> propertyValueList = designElement.getPropertyValueList();
        for (PropertyValue propertyValue : propertyValueList) {
            PropertyValueUtility.configurePropertyValueDisplay(propertyValue);
        }
    }

    public List<PropertyValue> getDesignElementImageList() {
        DesignElement designElement = getCurrent();
        List<PropertyValue> designElementImageList = designElement.getImagePropertyList();
        if (designElementImageList == null) {
            designElementImageList = prepareDesignElementImageList(designElement);
        }
        return designElementImageList;
    }

    public Boolean getDisplayChildDesignProperties(DesignElement designElement) {
        if (designElement == null) {
            return false;
        }
        DesignElementType designElementType = designElement.getContainedObjectType();
        return (designElementType != null && designElementType.equals(DesignElementType.DESIGN));
    }

    public Boolean getDisplayComponentProperties(DesignElement designElement) {
        if (designElement == null) {
            return false;
        }
        DesignElementType designElementType = designElement.getContainedObjectType();
        return (designElementType != null && designElementType.equals(DesignElementType.COMPONENT));
    }    

    public DataTable getComponentPropertyValueListDataTable() {
        return componentPropertyValueListDataTable;
    }

    public void setComponentPropertyValueListDataTable(DataTable componentPropertyValueListDataTable) {
        this.componentPropertyValueListDataTable = componentPropertyValueListDataTable;
    }

    public DataTable getChildDesignPropertyValueListDataTable() {
        return childDesignPropertyValueListDataTable;
    }

    public void setChildDesignPropertyValueListDataTable(DataTable childDesignPropertyValueListDataTable) {
        this.childDesignPropertyValueListDataTable = childDesignPropertyValueListDataTable;
    }
}
