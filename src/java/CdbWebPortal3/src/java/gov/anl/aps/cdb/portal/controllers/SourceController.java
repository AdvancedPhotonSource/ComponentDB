package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.beans.SourceFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("sourceController")
@SessionScoped
public class SourceController extends CdbEntityController<Source, SourceFacade>implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Source.List.Display.NumberOfItemsPerPage";
    private static final String DisplayContactInfoSettingTypeKey = "Source.List.Display.ContactInfo";
    private static final String DisplayDescriptionSettingTypeKey = "Source.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "Source.List.Display.Id";
    private static final String DisplayUrlSettingTypeKey = "Source.List.Display.Url";
    private static final String FilterByNameSettingTypeKey = "Source.List.FilterBy.Name";
    private static final String FilterByContactInfoSettingTypeKey = "Source.List.FilterBy.ContactInfo";
    private static final String FilterByDescriptionSettingTypeKey = "Source.List.FilterBy.Description";
    private static final String FilterByUrlSettingTypeKey = "Source.List.FilterBy.Url";

    private static final Logger logger = Logger.getLogger(SourceController.class.getName());

    private Boolean displayContactInfo = null;
    private Boolean displayUrl = null;

    private String filterByContactInfo = null;
    private String filterByUrl = null;

    @EJB
    private SourceFacade sourceFacade;

    public SourceController() {
    }

    @Override
    protected SourceFacade getEntityDbFacade() {
        return sourceFacade;
    }

    @Override
    protected Source createEntityInstance() {
        Source source = new Source();
        return source;
    }

    @Override
    public String getEntityTypeName() {
        return "source";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public Source findById(Integer id) {
        return sourceFacade.findById(id);
    }

    @Override
    public List<Source> getAvailableItems() {
        return super.getAvailableItems();
    }

    public List<Source> getAvailableSourcesSortedByName() {
        return sourceFacade.findAllSortedByName();
    }

    @Override
    public void prepareEntityInsert(Source source) throws ObjectAlreadyExists {
        Source existingSource = sourceFacade.findByName(source.getName());
        if (existingSource != null) {
            throw new ObjectAlreadyExists("Source " + source.getName() + " already exists.");
        }
        logger.debug("Inserting new source " + source.getName());
    }

    @Override
    public void prepareEntityUpdate(Source source) throws ObjectAlreadyExists {
        Source existingSource = sourceFacade.findByName(source.getName());
        if (existingSource != null && !existingSource.getId().equals(source.getId())) {
            throw new ObjectAlreadyExists("Source " + source.getName() + " already exists.");
        }
        logger.debug("Updating source " + source.getName());
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayContactInfo = Boolean.parseBoolean(settingTypeMap.get(DisplayContactInfoSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayUrl = Boolean.parseBoolean(settingTypeMap.get(DisplayUrlSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByContactInfo = settingTypeMap.get(FilterByContactInfoSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByUrl = settingTypeMap.get(FilterByUrlSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayContactInfo = sessionUser.getUserSettingValueAsBoolean(DisplayContactInfoSettingTypeKey, displayContactInfo);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayUrl = sessionUser.getUserSettingValueAsBoolean(DisplayUrlSettingTypeKey, displayUrl);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByContactInfo = sessionUser.getUserSettingValueAsString(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByUrl = sessionUser.getUserSettingValueAsString(FilterByUrlSettingTypeKey, filterByUrl);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByContactInfo = (String) filters.get("contactInfo");
        filterByUrl = (String) filters.get("url");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayContactInfoSettingTypeKey, displayContactInfo);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayUrlSettingTypeKey, displayUrl);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByUrlSettingTypeKey, filterByUrl);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByContactInfo = null;
        filterByUrl = null;
    }

    /**
     * Converter class for source objects.
     */
    @FacesConverter(forClass = Source.class)
    public static class SourceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                SourceController controller = (SourceController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "sourceController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to source object.");
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
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Source) {
                Source o = (Source) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Source.class.getName());
            }
        }

    }

    public Boolean getDisplayContactInfo() {
        return displayContactInfo;
    }

    public void setDisplayContactInfo(Boolean displayContactInfo) {
        this.displayContactInfo = displayContactInfo;
    }

    public Boolean getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(Boolean displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getFilterByContactInfo() {
        return filterByContactInfo;
    }

    public void setFilterByContactInfo(String filterByContactInfo) {
        this.filterByContactInfo = filterByContactInfo;
    }

    public String getFilterByUrl() {
        return filterByUrl;
    }

    public void setFilterByUrl(String filterByUrl) {
        this.filterByUrl = filterByUrl;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }    

    
    
}
