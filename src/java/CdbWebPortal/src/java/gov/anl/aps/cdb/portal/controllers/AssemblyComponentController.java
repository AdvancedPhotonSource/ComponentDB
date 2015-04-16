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

import gov.anl.aps.cdb.portal.model.db.entities.AssemblyComponent;
import gov.anl.aps.cdb.portal.model.db.beans.AssemblyComponentDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ComponentUtility;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
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
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;

/**
 * Controller class for assembly components.
 */
@Named("assemblyComponentController")
@SessionScoped
public class AssemblyComponentController extends CdbEntityController<AssemblyComponent, AssemblyComponentDbFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayDescriptionSettingTypeKey = "AssemblyComponent.List.Display.Description";
    private static final String DisplayFlatTableViewSettingTypeKey = "AssemblyComponent.List.Display.FlatTableView";
    private static final String DisplayIdSettingTypeKey = "AssemblyComponent.List.Display.Id";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "AssemblyComponent.List.Display.NumberOfItemsPerPage";
    private static final String DisplaySortOrderSettingTypeKey = "AssemblyComponent.List.Display.SortOrder";
    private static final String FilterByDescriptionSettingTypeKey = "AssemblyComponent.List.FilterBy.Description";
    private static final String FilterByNameSettingTypeKey = "AssemblyComponent.List.FilterBy.Name";
    private static final String FilterBySortOrderSettingTypeKey = "AssemblyComponent.List.FilterBy.SortOrder";

    private static final Logger logger = Logger.getLogger(AssemblyComponentController.class.getName());

    @EJB
    private AssemblyComponentDbFacade assemblyComponentFacade;

    @EJB
    private ComponentDbFacade componentFacade;

    private Boolean displayFlatTableView = null;
    private Boolean displaySortOrder = null;

    private String filterBySortOrder = null;

    private List<Component> selectComponentCandidateList = null;
    private Component selectedAssembly = null;

    /**
     * Default constructor.
     */
    public AssemblyComponentController() {
    }

    /**
     * Get entity DB facade.
     *
     * @return entity DB facade
     */
    @Override
    protected AssemblyComponentDbFacade getEntityDbFacade() {
        return assemblyComponentFacade;
    }

    /**
     * Create entity instance.
     *
     * @return new entity instance
     */
    @Override
    protected AssemblyComponent createEntityInstance() {
        AssemblyComponent assemblyComponent = new AssemblyComponent();
        selectComponentCandidateList = null;
        return assemblyComponent;
    }

    /**
     * Find assembly component by id.
     *
     * @param id assembly component id
     * @return assembly component object
     */
    @Override
    public AssemblyComponent findById(Integer id) {
        return assemblyComponentFacade.findById(id);
    }

    /**
     * Get entity type name.
     *
     * @return entity type name
     */
    @Override
    public String getEntityTypeName() {
        return "assemblyComponent";
    }

    /**
     * Get display string for entity type name.
     *
     * @return entity type name display string
     */
    @Override
    public String getDisplayEntityTypeName() {
        return "assembly component";
    }

    /**
     * Get name of the current entity instance.
     *
     * @return current entity instance name
     */
    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getAssembly().getName();
        }
        return "";
    }

    /**
     * Retrieve list of all available assembly component objects.
     *
     * @return list of available assembly component objects
     */
    @Override
    public List<AssemblyComponent> getAvailableItems() {
        return super.getAvailableItems();
    }

    /**
     * Prepare assembly component view from assembly view page.
     *
     * @param assemblyComponent assembly component
     * @return assembly component view URL
     */
    public String prepareViewFromAssembly(AssemblyComponent assemblyComponent) {
        logger.debug("Preparing assembly component view from assembly view page");
        prepareView(assemblyComponent);
        return "/views/assemblyComponent/view.xhtml?faces-redirect=true";
    }

    /**
     * Prepare assembly view from assembly component view page.
     *
     * @param assemblyComponent assembly component
     * @return assembly view URL
     */
    public String prepareViewToAssembly(AssemblyComponent assemblyComponent) {
        return "/views/component/view.xhtml?id=" + assemblyComponent.getAssembly().getId() + "&faces-redirect=true";
    }

    /**
     * Delete assembly component and return assembly view page.
     *
     * @param assemblyComponent assembly component to be deleted
     * @return assembly view URL
     */
    public String destroyAndReturnAssemblyView(AssemblyComponent assemblyComponent) {
        Component assembly = assemblyComponent.getAssembly();
        setCurrent(assemblyComponent);
        try {
            logger.debug("Destroying " + assemblyComponent.getComponent().getName());
            getEntityDbFacade().remove(assemblyComponent);
            SessionUtility.addInfoMessage("Success", "Deleted assembly component id " + assemblyComponent.getId() + ".");
            return "/views/component/view.xhtml?faces-redirect=true?id=" + assembly.getId();
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    /**
     * Update controller session settings with setting type default values.
     *
     * @param settingTypeMap map of setting types
     */
    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayFlatTableView = Boolean.parseBoolean(settingTypeMap.get(DisplayFlatTableViewSettingTypeKey).getDefaultValue());
        displaySortOrder = Boolean.parseBoolean(settingTypeMap.get(DisplaySortOrderSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterBySortOrder = settingTypeMap.get(FilterBySortOrderSettingTypeKey).getDefaultValue();
    }

    /**
     * Update controller session settings with user-specific values from the
     * database.
     *
     * @param sessionUser current session user
     */
    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayFlatTableView = sessionUser.getUserSettingValueAsBoolean(DisplayFlatTableViewSettingTypeKey, displayFlatTableView);
        displaySortOrder = sessionUser.getUserSettingValueAsBoolean(DisplaySortOrderSettingTypeKey, displaySortOrder);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterBySortOrder = sessionUser.getUserSettingValueAsString(FilterBySortOrderSettingTypeKey, filterBySortOrder);
    }

    /**
     * Update entity list display settings using current data table values.
     *
     * @param dataTable entity list data table
     */
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterBySortOrder = (String) filters.get("sortOrder");
    }

    /**
     * Save controller session settings for the current user.
     *
     * @param sessionUser current session user
     */
    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayFlatTableViewSettingTypeKey, displayFlatTableView);
        sessionUser.setUserSettingValue(DisplaySortOrderSettingTypeKey, displaySortOrder);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterBySortOrderSettingTypeKey, filterBySortOrder);
    }

    /**
     * Clear entity list filters.
     */
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterBySortOrder = null;
    }

    /**
     * Converter class for assembly component objects.
     */
    @FacesConverter(forClass = AssemblyComponent.class)
    public static class AssemblyComponentControllerConverter implements Converter {

        /**
         * Convert string to allowed property value object.
         *
         * @param facesContext JSF context
         * @param component UI component
         * @param value string value
         * @return assembly component object, or null if string cannot be
         * converted
         */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                AssemblyComponentController controller = (AssemblyComponentController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "assemblyComponentController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to assembly component object.");
                return null;
            }
        }

        /**
         * Get object key (id) as integer from its string value.
         *
         * @param value string key value
         * @return integer key value
         */
        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        /**
         * Get object key (id) as string from its integer value.
         *
         * @param value integer key value
         * @return string key value
         */
        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        /**
         * Get object string key.
         *
         * @param facesContext JSF context
         * @param component UI component
         * @param object object to be converted
         * @return object key as string
         */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof AssemblyComponent) {
                AssemblyComponent o = (AssemblyComponent) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + AssemblyComponent.class.getName());
            }
        }

    }

    /**
     * Get display flat table view flag (determines whether or not flat table
     * view should be displayed).
     *
     * @return flag value
     */
    public Boolean getDisplayFlatTableView() {
        return displayFlatTableView;
    }

    /**
     * Set display flat table view flag (determines whether or not flat table
     * view should be displayed).
     *
     * @param displayFlatTableView flag value
     */
    public void setDisplayFlatTableView(Boolean displayFlatTableView) {
        this.displayFlatTableView = displayFlatTableView;
    }

    /**
     * Get display sort order flag (determines whether or not allowed property
     * value sort order column should be displayed).
     *
     * @return flag value
     */
    public Boolean getDisplaySortOrder() {
        return displaySortOrder;
    }

    /**
     * Set display sort order flag (determines whether or not allowed property
     * value sort order column should be displayed).
     *
     * @param displaySortOrder flag value
     */
    public void setDisplaySortOrder(Boolean displaySortOrder) {
        this.displaySortOrder = displaySortOrder;
    }

    /**
     * Get sort order column filter.
     *
     * @return filter value
     */
    public String getFilterBySortOrder() {
        return filterBySortOrder;
    }

    /**
     * Set sort order column filter,
     *
     * @param filterBySortOrder filter value
     */
    public void setFilterBySortOrder(String filterBySortOrder) {
        this.filterBySortOrder = filterBySortOrder;
    }

    /**
     * Get selected assembly.
     *
     * @return selected assembly
     */
    public Component getSelectedAssembly() {
        return selectedAssembly;
    }

    /**
     * Set selected assembly.
     *
     * @param selectedAssembly selected assembly
     */
    public void setSelectedAssembly(Component selectedAssembly) {
        this.selectedAssembly = selectedAssembly;
    }

    /**
     * Listener for assembly component value change.
     *
     * @param valueChangeEvent change event
     */
    public void selectComponentValueChangeListener(ValueChangeEvent valueChangeEvent) {
        // This listener is accessed either after selection made in dialog,
        // or from selection menu. 
        AssemblyComponent assemblyComponent = getCurrent();
        if (assemblyComponent == null || valueChangeEvent == null) {
            return;
        }

        Component existingComponent = assemblyComponent.getComponent();
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
            assemblyComponent.setComponent(newEventComponent);
        } else {
            // change via dialog
            assemblyComponent.setComponent(oldEventComponent);
        }
    }

    /**
     * Listener for assembly component selection event.
     *
     * @param selectEvent assembly component selection event
     */
    public void selectComponentListener(SelectEvent selectEvent) {
        AssemblyComponent assemblyComponent = getCurrent();
        if (assemblyComponent == null) {
            return;
        }
        AutoComplete autoComplete = (AutoComplete) selectEvent.getSource();
        Object itemValue = autoComplete.getItemValue();
        if (itemValue != null) {
            Component component = (Component) itemValue;
            assemblyComponent.setComponent(component);
        }
    }

    /**
     * Listener for un-selecting assembly component.
     *
     * @param selectEvent clear assembly component selection event
     */
    public void unselectComponentListener(SelectEvent selectEvent) {
        AssemblyComponent assemblyComponent = getCurrent();
        if (assemblyComponent == null) {
            return;
        }
        assemblyComponent.setComponent(null);
    }

    /**
     * Retrieve list of component candidates suitable for assembly component
     * selection.
     *
     * @return component list
     */
    public List<Component> getSelectComponentCandidateList() {
        if (selectComponentCandidateList == null) {
            selectComponentCandidateList = componentFacade.findAll();
        }
        return selectComponentCandidateList;
    }

    /**
     * Retrieve list of components that satisfy auto-complete query.
     *
     * @param query component name pattern
     * @return component list
     */
    public List<Component> completeComponent(String query) {
        return ComponentUtility.filterComponent(query, getSelectComponentCandidateList());
    }

    /**
     * Select assembly component.
     *
     * @param component component to become part of assembly
     */
    public void selectComponent(Component component) {
        AssemblyComponent assemblyComponent = getCurrent();
        if (assemblyComponent != null) {
            assemblyComponent.setComponent(component);
        }
    }

}
