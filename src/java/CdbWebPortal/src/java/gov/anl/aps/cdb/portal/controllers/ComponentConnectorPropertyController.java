package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.entities.ComponentConnectorProperty;
import gov.anl.aps.cdb.portal.controllers.util.JsfUtil;
import gov.anl.aps.cdb.portal.controllers.util.PaginationHelper;
import gov.anl.aps.cdb.portal.model.beans.ComponentConnectorPropertyFacade;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("componentConnectorPropertyController")
@SessionScoped
public class ComponentConnectorPropertyController implements Serializable
{

    private ComponentConnectorProperty current;
    private DataModel items = null;
    @EJB
    private gov.anl.aps.cdb.portal.model.beans.ComponentConnectorPropertyFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public ComponentConnectorPropertyController() {
    }

    public ComponentConnectorProperty getSelected() {
        if (current == null) {
            current = new ComponentConnectorProperty();
            current.setComponentConnectorPropertyPK(new gov.anl.aps.cdb.portal.model.entities.ComponentConnectorPropertyPK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private ComponentConnectorPropertyFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10)
            {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (ComponentConnectorProperty) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new ComponentConnectorProperty();
        current.setComponentConnectorPropertyPK(new gov.anl.aps.cdb.portal.model.entities.ComponentConnectorPropertyPK());
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources").getString("ComponentConnectorPropertyCreated"));
            return prepareCreate();
        }
        catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (ComponentConnectorProperty) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources").getString("ComponentConnectorPropertyUpdated"));
            return "View";
        }
        catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (ComponentConnectorProperty) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        }
        else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources").getString("ComponentConnectorPropertyDeleted"));
        }
        catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public ComponentConnectorProperty getComponentConnectorProperty(gov.anl.aps.cdb.portal.model.entities.ComponentConnectorPropertyPK id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = ComponentConnectorProperty.class)
    public static class ComponentConnectorPropertyControllerConverter implements Converter
    {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ComponentConnectorPropertyController controller = (ComponentConnectorPropertyController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentConnectorPropertyController");
            return controller.getComponentConnectorProperty(getKey(value));
        }

        gov.anl.aps.cdb.portal.model.entities.ComponentConnectorPropertyPK getKey(String value) {
            gov.anl.aps.cdb.portal.model.entities.ComponentConnectorPropertyPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new gov.anl.aps.cdb.portal.model.entities.ComponentConnectorPropertyPK();
            key.setComponentConnectorId(Integer.parseInt(values[0]));
            key.setPropertyTypeId(Integer.parseInt(values[1]));
            key.setPropertyValueId(Integer.parseInt(values[2]));
            return key;
        }

        String getStringKey(gov.anl.aps.cdb.portal.model.entities.ComponentConnectorPropertyPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getComponentConnectorId());
            sb.append(SEPARATOR);
            sb.append(value.getPropertyTypeId());
            sb.append(SEPARATOR);
            sb.append(value.getPropertyValueId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ComponentConnectorProperty) {
                ComponentConnectorProperty o = (ComponentConnectorProperty) object;
                return getStringKey(o.getComponentConnectorPropertyPK());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentConnectorProperty.class.getName());
            }
        }

    }

}
