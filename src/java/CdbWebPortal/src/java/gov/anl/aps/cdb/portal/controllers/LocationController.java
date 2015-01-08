package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.Location;
import gov.anl.aps.cdb.portal.exceptions.CdbPortalException;
import gov.anl.aps.cdb.portal.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.LocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.LocationType;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.ObjectUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

@Named("locationController")
@SessionScoped
public class LocationController extends CrudEntityController<Location, LocationFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Location.List.Display.NumberOfItemsPerPage";
    private static final String DisplayDescriptionSettingTypeKey = "Location.List.Display.Description";
    private static final String DisplayFlatTableViewSettingTypeKey = "Location.List.Display.FlatTableView";
    private static final String DisplayIdSettingTypeKey = "Location.List.Display.Id";
    private static final String DisplayParentSettingTypeKey = "Location.List.Display.Parent";
    private static final String DisplayTypeSettingTypeKey = "Location.List.Display.Type";

    private static final String FilterByNameSettingTypeKey = "Location.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "Location.List.FilterBy.Description";
    private static final String FilterByParentSettingTypeKey = "Location.List.FilterBy.Parent";
    private static final String FilterByTypeSettingTypeKey = "Location.List.FilterBy.Type";

    private static final Logger logger = Logger.getLogger(LocationController.class.getName());

    @EJB
    private LocationFacade locationFacade;

    private Boolean displayFlatTableView = null;
    private Boolean displayParent = null;
    private Boolean displayType = null;

    private String filterByParent = null;
    private String filterByType = null;

    private Boolean selectDisplayParent = false;
    private Boolean selectDisplayType = true;
    private Boolean selectDisplayFlatTableView = false;
    private Boolean selectDisplayMenuView = false;

    private String selectFilterByParent = null;
    private String selectFilterByType = null;

    private Location selectedParentLocation = null;

    private MenuModel locationMenu = null;

    public LocationController() {
        selectDisplayDescription = true;
    }

    @Override
    protected LocationFacade getFacade() {
        return locationFacade;
    }

    @Override
    protected Location createEntityInstance() {
        Location location = new Location();
        return location;
    }

    @Override
    public String getEntityTypeName() {
        return "location";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "location";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    public Location findById(Integer id) {
        return locationFacade.findById(id);
    }

    @Override
    public void selectByRequestParams() {
        if (idViewParam != null) {
            Location location = findById(idViewParam);
            setCurrent(location);
            idViewParam = null;
        }
    }

    @Override
    public List<Location> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(Location location) throws CdbPortalException {
        Location existingLocation = locationFacade.findByName(location.getName());
        if (existingLocation != null) {
            throw new ObjectAlreadyExists("Location " + location.getName() + " already exists.");
        }
        if (location.getLocationType() == null) {
            throw new InvalidObjectState("Location type for " + location.getName() + " must be selected.");
        }
        selectedParentLocation = null;
        logger.debug("Inserting new location " + location.getName());
    }

    @Override
    public void prepareEntityUpdate(Location location) throws CdbPortalException {
        Location existingLocation = locationFacade.findByName(location.getName());
        if (existingLocation != null && !existingLocation.getId().equals(location.getId())) {
            throw new ObjectAlreadyExists("Location " + location.getName() + " already exists.");
        }
        if (location.getLocationType() == null) {
            throw new InvalidObjectState("Location type for " + location.getName() + " must be selected.");
        }
        selectedParentLocation = null;
        logger.debug("Updating location " + location.getName());
    }

    @Override
    public void prepareEntityDestroy(Location location) throws CdbPortalException {
        Location parentLocation = location.getParentLocation();
        if (parentLocation != null) {
            List<Location> parentChildLocationList = parentLocation.getChildLocationList();
            parentChildLocationList.remove(location);
        }
        logger.debug("Updating location " + location.getName());
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayFlatTableView = Boolean.parseBoolean(settingTypeMap.get(DisplayFlatTableViewSettingTypeKey).getDefaultValue());

        displayParent = Boolean.parseBoolean(settingTypeMap.get(DisplayParentSettingTypeKey).getDefaultValue());
        displayType = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByParent = settingTypeMap.get(FilterByParentSettingTypeKey).getDefaultValue();
        filterByType = settingTypeMap.get(FilterByTypeSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayFlatTableView = sessionUser.getUserSettingValueAsBoolean(DisplayFlatTableViewSettingTypeKey, displayFlatTableView);

        displayParent = sessionUser.getUserSettingValueAsBoolean(DisplayParentSettingTypeKey, displayParent);
        displayType = sessionUser.getUserSettingValueAsBoolean(DisplayTypeSettingTypeKey, displayType);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByParent = sessionUser.getUserSettingValueAsString(FilterByParentSettingTypeKey, filterByParent);
        filterByType = sessionUser.getUserSettingValueAsString(FilterByTypeSettingTypeKey, filterByType);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, String> filters = dataTable.getFilters();
        filterByParent = filters.get("locationParent.name");
        filterByType = filters.get("type");

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

        sessionUser.setUserSettingValue(DisplayParentSettingTypeKey, displayParent);
        sessionUser.setUserSettingValue(DisplayTypeSettingTypeKey, displayType);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);

        sessionUser.setUserSettingValue(FilterByParentSettingTypeKey, filterByParent);
        sessionUser.setUserSettingValue(FilterByTypeSettingTypeKey, filterByType);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByParent = null;
        filterByType = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByParent = null;
        selectFilterByType = null;
    }

    @FacesConverter(value = "locationConverter", forClass = Location.class)
    public static class LocationControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                LocationController controller = (LocationController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "locationController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to location object.");
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
            if (object instanceof Location) {
                Location o = (Location) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Location.class.getName());
            }
        }

    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    // This listener is accessed either after selection made in dialog,
    // or from selection menu.
    public void selectParentLocationValueChangeListener(ValueChangeEvent valueChangeEvent) {
        Location location = getCurrent();
        Location existingParentLocation = location.getParentLocation();
        Location newEventParentLocation = null;
        Location oldEventParentLocation = null;

        Object newValue = valueChangeEvent.getNewValue();
        if (newValue != null) {
            newEventParentLocation = (Location) newValue;
        }
        Object oldValue = valueChangeEvent.getOldValue();
        if (oldValue != null) {
            oldEventParentLocation = (Location) oldValue;
        }

        if (ObjectUtility.equals(existingParentLocation, oldEventParentLocation)) {
            // change via menu
            location.setParentLocation(newEventParentLocation);
        } else {
            // change via dialog
            location.setParentLocation(oldEventParentLocation);
        }
    }

    public void selectParentLocationActionListener(ActionEvent actionEvent) {
        Object newValue = actionEvent.getSource();
        if (newValue != null) {
            Location parentLocation = (Location) newValue;
            setSelectedParentLocation(parentLocation);
        }
    }

    public void selectParentLocation(Location parentLocation) {
        Location location = getCurrent();
        if (parentLocation != null) {
            location.setParentLocation(parentLocation);
        }
    }

    public void selectLocationType(LocationType locationType) {
        Location location = getCurrent();
        if (locationType != null) {
            location.setLocationType(locationType);
        }
    }

    public MenuModel createLocationMenu() {
        locationMenu = new DefaultMenuModel();
        List<Location> locationsWithoutParents = locationFacade.findLocationsWithoutParents();
        for (Location location : locationsWithoutParents) {
            if (location.getChildLocationList().isEmpty()) {
                locationMenu.addElement(new DefaultMenuItem(location.getName()));
            } else {
                DefaultSubMenu subMenu = new DefaultSubMenu(location.getName());
                populateLocationSubMenu(subMenu, location);
                locationMenu.addElement(subMenu);
            }
        }
        return locationMenu;
    }

    private void populateLocationSubMenu(DefaultSubMenu subMenu, Location location) {
        for (Location childLocation : location.getChildLocationList()) {
            if (childLocation.getChildLocationList().isEmpty()) {
                subMenu.addElement(new DefaultMenuItem(childLocation.getName()));
            } else {
                DefaultSubMenu childSubMenu = new DefaultSubMenu(childLocation.getName());
                populateLocationSubMenu(childSubMenu, childLocation);
                subMenu.addElement(childSubMenu);
            }
        }
    }

    public MenuModel getLocationMenu() {
        if (locationMenu == null) {
            createLocationMenu();
        }
        return locationMenu;
    }

    public Boolean getDisplayParent() {
        return displayParent;
    }

    public void setDisplayParent(Boolean displayParent) {
        this.displayParent = displayParent;
    }

    public Boolean getDisplayType() {
        return displayType;
    }

    public void setDisplayType(Boolean displayType) {
        this.displayType = displayType;
    }

    public Boolean getDisplayFlatTableView() {
        return displayFlatTableView;
    }

    public void setDisplayFlatTableView(Boolean displayFlatTableView) {
        this.displayFlatTableView = displayFlatTableView;
    }

    public Boolean getSelectDisplayFlatTableView() {
        return selectDisplayFlatTableView;
    }

    public void setSelectDisplayFlatTableView(Boolean selectDisplayFlatTableView) {
        this.selectDisplayFlatTableView = selectDisplayFlatTableView;
    }

    public Boolean getSelectDisplayMenuView() {
        return selectDisplayMenuView;
    }

    public void setSelectDisplayMenuView(Boolean selectDisplayMenuView) {
        this.selectDisplayMenuView = selectDisplayMenuView;
    }

    public String getFilterByParent() {
        return filterByParent;
    }

    public void setFilterByParent(String filterByParent) {
        this.filterByParent = filterByParent;
    }

    public String getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(String filterByType) {
        this.filterByType = filterByType;
    }

    public Boolean getSelectDisplayParent() {
        return selectDisplayParent;
    }

    public void setSelectDisplayParent(Boolean selectDisplayParent) {
        this.selectDisplayParent = selectDisplayParent;
    }

    public Boolean getSelectDisplayType() {
        return selectDisplayType;
    }

    public void setSelectDisplayType(Boolean selectDisplayType) {
        this.selectDisplayType = selectDisplayType;
    }

    public String getSelectFilterByParent() {
        return selectFilterByParent;
    }

    public void setSelectFilterByParent(String selectFilterByParent) {
        this.selectFilterByParent = selectFilterByParent;
    }

    public String getSelectFilterByType() {
        return selectFilterByType;
    }

    public void setSelectFilterByType(String selectFilterByType) {
        this.selectFilterByType = selectFilterByType;
    }

    public Location getSelectedParentLocation() {
        return selectedParentLocation;
    }

    public void setSelectedParentLocation(Location selectedParentLocation) {
        this.selectedParentLocation = selectedParentLocation;
    }

}
