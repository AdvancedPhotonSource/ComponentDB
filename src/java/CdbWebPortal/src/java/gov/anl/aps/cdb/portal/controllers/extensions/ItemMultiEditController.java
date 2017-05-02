/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemControllerExtensionHelper;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;

/**
 *
 * @author djarosz
 */
public abstract class ItemMultiEditController extends ItemControllerExtensionHelper {
    
    protected abstract String getControllerNamedConstant(); 
            
    protected final String EDIT_MULTIPLE_REDIRECT = "editMultiple?faces-redirect=true"; 
    
    protected ListDataModel editableListDataModel; 
        
    protected List<Item> selectedItemsToEdit; 
    
    protected DefaultMenuModel editStepsMenuModel; 
    
    protected int activeIndex = 0; 
    
    protected boolean updateItemIdentifier1 = false;
    protected boolean updateItemIdentifier2 = false;     
    protected boolean updateProject = false; 
    protected boolean updateDescription = false; 
    protected boolean updateQrId = false; 
    
    protected enum MultipleEditMenu {
        selection("Selection"),        
        updateItems("Update Items"); 
        
        private String value; 
        private MultipleEditMenu(String value) {
            this.value = value;
        }               

        public final String getValue() {
            return value;
        }
    }
    
    public String getPageTitle() {
        return "Multiple Edit for " + getDisplayEntityTypeName() + "s."; 
    }

    public DefaultMenuModel getEditStepsMenuModel() {
        if (editStepsMenuModel == null) {
            editStepsMenuModel = new DefaultMenuModel(); 
            
            for (MultipleEditMenu multiEditConstant : MultipleEditMenu.values()) {
                DefaultMenuItem menuItem = new DefaultMenuItem(multiEditConstant.getValue());                 
                menuItem.setCommand("#{" + getControllerNamedConstant() + ".setActiveIndex(" + multiEditConstant.ordinal() + ")}");
                menuItem.setOnstart("PF('loadingDialog').show();");
                menuItem.setOncomplete("PF('loadingDialog').hide();");
                menuItem.setUpdate("@form");
                editStepsMenuModel.addElement(menuItem);
            }
        }
        
        return editStepsMenuModel;
    }   
    
    public void updateSelectedItems() {
        for (Item item : selectedItemsToEdit) {
            this.setCurrent(item);
            this.update();
        }
    }
    
    public void removeSelection(Item item) {
        selectedItemsToEdit.remove(item);
    }
    
    public String prepareEditMultipleItems() {
        selectedItemsToEdit = new ArrayList<>(); 
        editableListDataModel = null; 
        activeIndex = 0;
        return EDIT_MULTIPLE_REDIRECT; 
    }

    public ListDataModel getEditableListDataModel() {
        if (editableListDataModel == null) {
            UserInfo userInfo = (UserInfo) SessionUtility.getUser();
            editableListDataModel = new ListDataModel(getItemDbFacade().findItemsWithPermissionsOfDomain(userInfo.getId(), getDomainId()));
        }
        return editableListDataModel;
    }
    
    public boolean getRenderPreviousButton() {
        return activeIndex > 0; 
    }
    
    public boolean getRenderNextButton() {
        return activeIndex < MultipleEditMenu.values().length -1; 
    }
    
    public boolean getRenderUpdateAllButton() {
        return activeIndex == MultipleEditMenu.values().length -1
                && selectedItemsToEdit.size() > 0; 
    }
    
    public void goToNextStep() {
        activeIndex ++; 
    }
    
    public void goToPrevStep() {
        activeIndex --;
    }
 
    public List<Item> getSelectedItemsToEdit() {
        return selectedItemsToEdit;
    }

    public void setSelectedItemsToEdit(List<Item> selectedItemsToEdit) {
        this.selectedItemsToEdit = selectedItemsToEdit;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }
    
    public boolean getRenderSelection() {
        return activeIndex == MultipleEditMenu.selection.ordinal(); 
    }
    
    public boolean getRenderUpdateItems() {
        return activeIndex == MultipleEditMenu.updateItems.ordinal(); 
    }

    public boolean isUpdateItemIdentifier1() {
        return updateItemIdentifier1;
    }

    public void setUpdateItemIdentifier1(boolean updateItemIdentifier1) {
        this.updateItemIdentifier1 = updateItemIdentifier1;
    }

    public boolean isUpdateItemIdentifier2() {
        return updateItemIdentifier2;
    }

    public void setUpdateItemIdentifier2(boolean updateItemIdentifier2) {
        this.updateItemIdentifier2 = updateItemIdentifier2;
    }

    public boolean isUpdateProject() {
        return updateProject;
    }

    public void setUpdateProject(boolean updateProject) {
        this.updateProject = updateProject;
    }

    public boolean isUpdateDescription() {
        return updateDescription;
    }

    public void setUpdateDescription(boolean updateDescription) {
        this.updateDescription = updateDescription;
    }

    public boolean isUpdateQrId() {
        return updateQrId;
    }

    public void setUpdateQrId(boolean updateQrId) {
        this.updateQrId = updateQrId;
    }
    
}
