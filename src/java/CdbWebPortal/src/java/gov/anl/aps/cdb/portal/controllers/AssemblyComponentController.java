package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
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

@Named("assemblyComponentController")
@SessionScoped
public class AssemblyComponentController extends CrudEntityController<AssemblyComponent, AssemblyComponentDbFacade> implements Serializable {

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

    public AssemblyComponentController() {
    }

    @Override
    protected AssemblyComponentDbFacade getFacade() {
        return assemblyComponentFacade;
    }

    @Override
    protected AssemblyComponent createEntityInstance() {
        AssemblyComponent assemblyComponent = new AssemblyComponent();
        selectComponentCandidateList = null;
        return assemblyComponent;
    }

    @Override
    public AssemblyComponent findById(Integer id) {
        return assemblyComponentFacade.findById(id);
    }

    @Override
    public String getEntityTypeName() {
        return "assemblyComponent";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "assembly component";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getAssembly().getName();
        }
        return "";
    }

    @Override
    public List<AssemblyComponent> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(AssemblyComponent assemblyComponent) throws ObjectAlreadyExists {
    }

    @Override
    public void prepareEntityUpdate(AssemblyComponent assemblyComponent) throws ObjectAlreadyExists {
    }

    @Override
    public void prepareEntityUpdateOnRemoval(AssemblyComponent assemblyComponent) {
    }

    public String prepareViewFromAssembly(AssemblyComponent assemblyComponent) {
        logger.debug("Preparing assembly component view from assembly view page");
        prepareView(assemblyComponent);
        return "/views/assemblyComponent/view.xhtml?faces-redirect=true";
    }

    public String prepareViewToAssembly(AssemblyComponent assemblyComponent) {
        return "/views/component/view.xhtml?id=" + assemblyComponent.getAssembly().getId() + "&faces-redirect=true";
    }

    public String destroyAndReturnAssemblyView(AssemblyComponent assemblyComponent) {
        Component assembly = assemblyComponent.getAssembly();
        setCurrent(assemblyComponent);
        try {
            logger.debug("Destroying " + assemblyComponent.getComponent().getName());
            getFacade().remove(assemblyComponent);
            SessionUtility.addInfoMessage("Success", "Deleted assembly component id " + assemblyComponent.getId() + ".");
            return "/views/component/view.xhtml?faces-redirect=true?id=" + assembly.getId();
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
        displaySortOrder = Boolean.parseBoolean(settingTypeMap.get(DisplaySortOrderSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterBySortOrder = settingTypeMap.get(FilterBySortOrderSettingTypeKey).getDefaultValue();
    }

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

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterBySortOrder = (String) filters.get("sortOrder");
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
        sessionUser.setUserSettingValue(DisplaySortOrderSettingTypeKey, displaySortOrder);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterBySortOrderSettingTypeKey, filterBySortOrder);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterBySortOrder = null;
    }

    @FacesConverter(forClass = AssemblyComponent.class)
    public static class AssemblyComponentControllerConverter implements Converter {

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
            if (object instanceof AssemblyComponent) {
                AssemblyComponent o = (AssemblyComponent) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + AssemblyComponent.class.getName());
            }
        }

    }

    public Boolean getDisplayFlatTableView() {
        return displayFlatTableView;
    }

    public void setDisplayFlatTableView(Boolean displayFlatTableView) {
        this.displayFlatTableView = displayFlatTableView;
    }

    public Boolean getDisplaySortOrder() {
        return displaySortOrder;
    }

    public void setDisplaySortOrder(Boolean displaySortOrder) {
        this.displaySortOrder = displaySortOrder;
    }

    public String getFilterBySortOrder() {
        return filterBySortOrder;
    }

    public void setFilterBySortOrder(String filterBySortOrder) {
        this.filterBySortOrder = filterBySortOrder;
    }

    public Component getSelectedAssembly() {
        return selectedAssembly;
    }

    public void setSelectedAssembly(Component selectedAssembly) {
        this.selectedAssembly = selectedAssembly;
    }

    // This listener is accessed either after selection made in dialog,
    // or from selection menu.    
    public void selectComponentValueChangeListener(ValueChangeEvent valueChangeEvent) {
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

    public void unselectComponentListener(SelectEvent selectEvent) {
        AssemblyComponent assemblyComponent = getCurrent();
        if (assemblyComponent == null) {
            return;
        }
        assemblyComponent.setComponent(null);
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
        AssemblyComponent assemblyComponent = getCurrent();
        if (assemblyComponent != null) {
            assemblyComponent.setComponent(component);
        }
    }

}
