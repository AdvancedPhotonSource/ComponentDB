package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.CmsPortalException;
import gov.anl.aps.cms.portal.model.beans.AbstractFacade;
import gov.anl.aps.cms.portal.utilities.CollectionUtility;
import gov.anl.aps.cms.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

public abstract class CrudEntityController<EntityType, FacadeType extends AbstractFacade<EntityType>> implements Serializable
{

    private EntityType current = null;
    private DataModel dataModel = null;
    private List<EntityType> filteredItems = null;

    public CrudEntityController() {
    }

    protected abstract FacadeType getFacade();

    protected abstract EntityType createEntityInstance();

    public abstract String getEntityTypeName();

    public abstract String getCurrentEntityInstanceName();

    public EntityType getCurrent() {
        return current;
    }

    public void setCurrent(EntityType current) {
        this.current = current;
    }

    public void selectByRequestParams() {    
    }
    
    public EntityType getSelected() {
        selectByRequestParams();
        if (current == null) {
            current = createEntityInstance();
        }
        return current;
    }

    public DataModel createDataModel() {
        return new ListDataModel(getFacade().findAll());
    }

    public String prepareList() {
        resetDataModel();
        return "list?faces-redirect=true";
    }

    public boolean isViewValid() {
        selectByRequestParams();
        return current != null;
    }
    
    public String prepareView(EntityType entity) {
        current = entity;
        return "view?faces-redirect=true";
    }

    public String view() {
        return "view?faces-redirect=true";
    }

    public String prepareCreate() {
        current = createEntityInstance();
        return "create?faces-redirect=true";
    }

    protected void prepareEntityInsert(EntityType entity) throws CmsPortalException {
    }

    public String create() {
        try {
            prepareEntityInsert(current);
            getFacade().create(current);
            SessionUtility.addInfoMessage("Success", "Created " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            return view();
        }
        catch (CmsPortalException ex) {
            SessionUtility.addErrorMessage("Error", "Could not create " + getEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    public String prepareEdit(EntityType entity) {
        current = entity;
        return "edit?faces-redirect=true";
    }

    public String edit() {
        return "edit?faces-redirect=true";
    }

    protected void prepareEntityUpdate(EntityType entity) throws CmsPortalException {
    }

    public String update() {
        try {
            prepareEntityUpdate(current);
            getFacade().edit(current);
            SessionUtility.addInfoMessage("Success", "Updated " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            return view();
        }
        catch (CmsPortalException ex) {
            SessionUtility.addErrorMessage("Error", "Could not update " + getEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    public String destroy() {
        try {
            getFacade().remove(current);
            SessionUtility.addInfoMessage("Success", "Deleted " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            return prepareList();
        }
        catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    public DataModel getItems() {
        if (dataModel == null) {
            dataModel = createDataModel();
        }
        return dataModel;
    }

    public List<EntityType> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<EntityType> filteredItems) {
        this.filteredItems = filteredItems;
    }

    private void resetDataModel() {
        dataModel = null;
        filteredItems = null;
    }

    public List<EntityType> getAvailableItems() {
        return getFacade().findAll();
    }

    public EntityType getEntity(Integer id) {
        return getFacade().find(id);
    }

    public SelectItem[] getAvailableItemsForSelectMany() {
        return CollectionUtility.getSelectItems(getFacade().findAll(), false);
    }

    public SelectItem[] getAvailableItemsForSelectOne() {
        return CollectionUtility.getSelectItems(getFacade().findAll(), true);
    }

    public static String displayEntityList(List<?> entityList) {
        String itemDelimiter = ", ";
        return CollectionUtility.displayItemListWithoutOutsideDelimiters(entityList, itemDelimiter);
    }
}
