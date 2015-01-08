package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import gov.anl.aps.cdb.portal.model.db.beans.DesignElementFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
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

@Named("designElementController")
@SessionScoped
public class DesignElementController extends CrudEntityController<DesignElement, DesignElementFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "DesignElement.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "DesignElement.List.Display.Id";
    private static final String DisplayTagSettingTypeKey = "DesignElement.List.Display.Tag";
    private static final String DisplayQuantitySettingTypeKey = "DesignElement.List.Display.Quantity";
    private static final String DisplayPrioritySettingTypeKey = "DesignElement.List.Display.Priority";
    private static final String DisplayDescriptionSettingTypeKey = "DesignElement.List.Display.Description";

    private static final String DisplayChildDesignSettingTypeKey = "DesignElement.List.Display.ChildDesign";
    
    private static final String DisplayComponentSettingTypeKey = "DesignElement.List.Display.Component";
    
//    ('DesignElement.List.Display.CreatedByUser', 'Display created by username.', 'false'),
//    ('DesignElement.List.Display.CreatedOnDateTime', 'Display created on date/time.', 'false'),
//    ('DesignElement.List.Display.Description','Display design element description.', 'false'),
//    ('DesignElement.List.Display.Id','Display design element id.', 'false'),
//    ('DesignElement.List.Display.LastModifiedByUser', 'Display last modified by username.', 'false'),
//    ('DesignElement.List.Display.LastModifiedOnDateTime', 'Display last modified on date/time.', 'false'),
//    ('DesignElement.List.Display.Location', 'Display location.', 'true'),
//    ('DesignElement.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
//    ('DesignElement.List.Display.OwnerUser', 'Display owner username.', 'true'),
//    ('DesignElement.List.Display.OwnerGroup', 'Display owner group name.', 'true'),
//    ('DesignElement.List.Display.SortOrder','Display design element sort order.', 'true'),
//
//    ('DesignElement.List.FilterBy.ChildDesign', 'Filter for child design.', NULL),
//    ('DesignElement.List.FilterBy.Component', 'Filter for component.', NULL),
//    ('DesignElement.List.FilterBy.CreatedByUser', 'Filter for design elements that were created by username.', NULL),
//    ('DesignElement.List.FilterBy.CreatedOnDateTime', 'Filter for design elements that were created on date/time.', NULL),
//    ('DesignElement.List.FilterBy.Description', 'Filter for design elements by description.', NULL),
//    ('DesignElement.List.FilterBy.LastModifiedByUser', 'Filter for design elements that were last modified by username.', NULL),
//    ('DesignElement.List.FilterBy.LastModifiedOnDateTime', 'Filter for design elements that were last modified on date/time.', NULL),
//    ('DesignElement.List.FilterBy.Location', 'Filter for component.', NULL),
//    ('DesignElement.List.FilterBy.Name', 'Filter for design elements by name.', NULL),
//    ('DesignElement.List.FilterBy.OwnerUser', 'Filter for design elements by owner username.', NULL),
//    ('DesignElement.List.FilterBy.OwnerGroup', 'Filter for design elements by owner group name.', NULL),
//    ('DesignElement.List.FilterBy.SortOrder', 'Filter for design elements by sort order.', NULL),


       
    private static final Logger logger = Logger.getLogger(DesignElementController.class.getName());

    @EJB
    private DesignElementFacade designElementFacade;

    private Boolean displayTag = null;
    private Boolean displayQuantity = null;
    private Boolean displayPriority = null;

    public DesignElementController() {
    }

    @Override
    protected DesignElementFacade getFacade() {
        return designElementFacade;
    }

    @Override
    protected DesignElement createEntityInstance() {
        DesignElement designElement = new DesignElement();
        return designElement;
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
            return getCurrent().getComponent().getName();
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
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayTag = Boolean.parseBoolean(settingTypeMap.get(DisplayTagSettingTypeKey).getDefaultValue());
        displayQuantity = Boolean.parseBoolean(settingTypeMap.get(DisplayQuantitySettingTypeKey).getDefaultValue());
        displayPriority = Boolean.parseBoolean(settingTypeMap.get(DisplayPrioritySettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayTag = sessionUser.getUserSettingValueAsBoolean(DisplayTagSettingTypeKey, displayTag);
        displayQuantity = sessionUser.getUserSettingValueAsBoolean(DisplayQuantitySettingTypeKey, displayQuantity);
        displayPriority = sessionUser.getUserSettingValueAsBoolean(DisplayPrioritySettingTypeKey, displayPriority);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayTagSettingTypeKey, displayTag);
        sessionUser.setUserSettingValue(DisplayQuantitySettingTypeKey, displayQuantity);
        sessionUser.setUserSettingValue(DisplayPrioritySettingTypeKey, displayPriority);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
    }
    
    public Boolean getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(Boolean displayTag) {
        this.displayTag = displayTag;
    }

    public Boolean getDisplayQuantity() {
        return displayQuantity;
    }

    public void setDisplayQuantity(Boolean displayQuantity) {
        this.displayQuantity = displayQuantity;
    }

    public Boolean getDisplayPriority() {
        return displayPriority;
    }

    public void setDisplayPriority(Boolean displayPriority) {
        this.displayPriority = displayPriority;
    }

    @FacesConverter(forClass = DesignElement.class)
    public static class DesignElementControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
            DesignElementController controller = (DesignElementController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "designElementController");
            return controller.getEntity(getKey(value));
            }
            catch (Exception ex) {
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
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + DesignElement.class.getName());
            }
        }

    }

}