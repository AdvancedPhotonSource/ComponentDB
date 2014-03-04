package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.beans.AbstractFacade;
import gov.anl.aps.cms.portal.utility.SessionUtility;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public abstract class EntityController<EntityType, FacadeType extends AbstractFacade<EntityType> > implements Serializable {

    private EntityType current = null;
    private DataModel items = null;

    public EntityController() {
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
    

    public EntityType getSelected() {
        if (current == null) {
            current = createEntityInstance();
        }
        return current;
    }

    public DataModel createDataModel() {
        return new ListDataModel(getFacade().findAll());
    }
                
    public String prepareList() {
        recreateDataModel();
        return "list?faces-redirect=true";
    }

    public String prepareView() {
        current = (EntityType) getItems().getRowData();
        return "view?faces-redirect=true";
    }

    public String prepareCreate() {
        current = createEntityInstance();
        return "create?faces-redirect=true";
    }

    public String create() {
        try {
            getFacade().create(current);
            SessionUtility.addInfoMessage("Success", "Created " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            return prepareList();
        } 
        catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not create " + getEntityTypeName() + ": " + ex.getMessage());                
            return null;
        }
    }

    public String prepareEdit() {
        current = (EntityType) getItems().getRowData();
        return "edit?faces-redirect=true";
    }

    public String edit() {
        return "edit?faces-redirect=true";
    }
    
    public String update() {
        try {
            getFacade().edit(current);
            SessionUtility.addInfoMessage("Success", "Updated " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            return "view?faces-redirect=true";
        } 
        catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not update " + getEntityTypeName() + ": " + ex.getMessage());              
            return null;
        }
    }

    public void destroy() {
        performDestroy();
        recreateDataModel();
    }

    public void performDestroy() {
        try {
            getFacade().remove(current);
            SessionUtility.addInfoMessage("Success", "Deleted " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
        } 
        catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getEntityTypeName() + ": " + ex.getMessage());                
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = createDataModel();
        }
        return items;
    }

    private void recreateDataModel() {
        items = null;
    }

    public List<EntityType> getAvailableItems() {
        return getFacade().findAll();
    }

    public EntityType getEntity(Integer id) {
        return getFacade().find(id);
    }

}
