package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.AssemblyComponent;
import gov.anl.aps.cdb.portal.controllers.util.JsfUtil;
import gov.anl.aps.cdb.portal.controllers.util.PaginationHelper;
import gov.anl.aps.cdb.portal.model.db.beans.AssemblyComponentFacade;

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

@Named("assemblyComponentController")
@SessionScoped
public class AssemblyComponentController implements Serializable
{

    private AssemblyComponent current;
    private DataModel items = null;
    @EJB
    private gov.anl.aps.cdb.portal.model.db.beans.AssemblyComponentFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public AssemblyComponentController() {
    }

    public AssemblyComponent getSelected() {
        if (current == null) {
            current = new AssemblyComponent();
            selectedItemIndex = -1;
        }
        return current;
    }

    private AssemblyComponentFacade getFacade() {
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
        current = (AssemblyComponent) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new AssemblyComponent();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources").getString("AssemblyComponentCreated"));
            return prepareCreate();
        }
        catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (AssemblyComponent) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources").getString("AssemblyComponentUpdated"));
            return "View";
        }
        catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (AssemblyComponent) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources").getString("AssemblyComponentDeleted"));
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

    public AssemblyComponent getAssemblyComponent(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = AssemblyComponent.class)
    public static class AssemblyComponentControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AssemblyComponentController controller = (AssemblyComponentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "assemblyComponentController");
            return controller.getAssemblyComponent(getKey(value));
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
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + AssemblyComponent.class.getName());
            }
        }

    }

}
