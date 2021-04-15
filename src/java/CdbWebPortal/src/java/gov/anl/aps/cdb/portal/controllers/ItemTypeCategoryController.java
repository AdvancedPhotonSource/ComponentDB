/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.controllers.settings.CdbEntitySettingsBase;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemTypeCategoryControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemTypeCategoryEntity;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public abstract class ItemTypeCategoryController<ControllerUtility extends ItemTypeCategoryControllerUtility<EntityType, FacadeType>, EntityType extends ItemTypeCategoryEntity, FacadeType extends CdbEntityFacade<EntityType>, SettingObject extends CdbEntitySettingsBase> extends CdbEntityController<ControllerUtility, EntityType, FacadeType, SettingObject> {

    protected Domain currentViewDomain = null;

    protected DataModel currentDomainListDataModel = null;

    public abstract List<EntityType> getItemTypeCategoryEntityListByDomainName(String domainName);   

    @Override
    protected EntityType createEntityInstance() {
        UserInfo user = SessionUtility.getUser();
        return getControllerUtility().createEntityInstance(user, currentViewDomain); 
    }

    @Override
    public void resetListDataModel() {
        currentDomainListDataModel = null; 
        super.resetListDataModel(); 
    }

    public Domain getCurrentViewDomain() {
        return currentViewDomain;
    }

    public DataModel getCurrentDomainListDataModel() {
        if (currentViewDomain == null) {
            return super.getListDataModel();
        } else {
            if (currentDomainListDataModel == null) {
                List<EntityType> domainItemTypeList;
                String domainName = currentViewDomain.getName();
                domainItemTypeList = getItemTypeCategoryEntityListByDomainName(domainName);
                currentDomainListDataModel = new ListDataModel(domainItemTypeList);
            }
            return currentDomainListDataModel;
        }
    }

    private String getCompleteItemTypeCategoryTitle(String itemControllerTitle) {
        return currentViewDomain.getName() + " Item " + itemControllerTitle; 
    }

    public String getItemTypeTitle() {
        if (currentViewDomain != null) {
            String itemTypeTitle = ItemController.getItemItemTypeTitleForDomain(currentViewDomain); 
            return getCompleteItemTypeCategoryTitle(itemTypeTitle);
        }
        return "Item Type";
    }

    public String getItemCategoryTitle() {
        if (currentViewDomain != null) {
            String itemCategoryTitle = ItemController.getItemItemCategoryTitleForDomain(currentViewDomain); 
            return getCompleteItemTypeCategoryTitle(itemCategoryTitle);
        }
        return "Item Category";
    }

    private String getCurrentViewDisplayEntityTypeName() {
        if (currentViewDomain != null) {
            if (this instanceof ItemCategoryController) {
                return getItemCategoryTitle();
            } else if (this instanceof ItemTypeController) {
                return getItemTypeTitle();
            }
        }
        return null;
    }

    @Override
    public String getDisplayEntityTypeName() {
        String displayEntityTypeName;
        displayEntityTypeName = getCurrentViewDisplayEntityTypeName();
        if (displayEntityTypeName == null) {
            displayEntityTypeName = super.getDisplayEntityTypeName(); 
        }
        return displayEntityTypeName;
    }

    public String prepareItemTypeCategoryEntityList(Domain itemDomain) {
        if (!ObjectUtility.equals(itemDomain, currentViewDomain)) {
            currentDomainListDataModel = null;
        }
        currentViewDomain = itemDomain;

        return "/views/" + getEntityTypeName() + "/list.xhtml?faces-redirect=true";
    }

    public boolean isItemTypeCategoryDomainEditDisabled() {
        return currentViewDomain != null;
    }

}
