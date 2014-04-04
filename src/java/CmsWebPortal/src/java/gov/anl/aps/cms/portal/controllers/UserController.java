package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.CmsPortalException;
import gov.anl.aps.cms.portal.model.beans.SettingTypeFacade;
import gov.anl.aps.cms.portal.model.entities.User;
import gov.anl.aps.cms.portal.model.beans.UserFacade;
import gov.anl.aps.cms.portal.model.entities.SettingType;
import gov.anl.aps.cms.portal.model.entities.UserSetting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

@Named("userController")
@SessionScoped
public class UserController extends CrudEntityController<User, UserFacade> implements Serializable
{

    private static final Logger logger = Logger.getLogger(ComponentController.class.getName());

    @EJB
    private UserFacade userFacade;
    @EJB
    private SettingTypeFacade settingTypeFacade;

    public UserController() {
    }

    @Override
    protected UserFacade getFacade() {
        return userFacade;
    }

    @Override
    protected User createEntityInstance() {
        return new User();
    }

    @Override
    public String getEntityTypeName() {
        return "user";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getUsername();
        }
        return "";
    }

    @Override
    public List<User> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public String prepareEdit(User user) {
        List<SettingType> settingTypeList = settingTypeFacade.findAll();
        ArrayList<UserSetting> userSettingList = new ArrayList<>();
        for (SettingType settingType : settingTypeList) {
            UserSetting setting = user.getUserSetting(settingType.getName());
            if (setting == null || setting.getValue().isEmpty()) {
                setting = new UserSetting();
                setting.setUser(user);
                setting.setSettingType(settingType);
                setting.setValue(settingType.getDefaultValue());
            }
            userSettingList.add(setting);
        }
        user.setUserSettingList(userSettingList);
        return super.prepareEdit(user);
    }

    @Override
    public void prepareEntityUpdate(User user) throws CmsPortalException {
        logger.debug("Updating user " + user.getUsername());
        List<UserSetting> userSettingList = user.getUserSettingList();
        for (UserSetting userSetting : userSettingList) {
            if (userSetting.getValue() == null) {
                userSetting.setValue(userSetting.getSettingType().getDefaultValue());
            }
        }
    }

    public void saveSettingList() {
        update();
    }

    
    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
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
            if (object instanceof User) {
                User o = (User) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + User.class.getName());
            }
        }

    }

}
