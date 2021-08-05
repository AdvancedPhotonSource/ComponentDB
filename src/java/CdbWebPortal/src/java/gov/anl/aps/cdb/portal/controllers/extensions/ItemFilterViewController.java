/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.utilities.CollectionUtility;
import gov.anl.aps.cdb.portal.controllers.ItemControllerExtensionHelper;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.view.objects.FilterViewResultItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
public abstract class ItemFilterViewController extends ItemControllerExtensionHelper implements Serializable {
    
    private static final Logger logger = LogManager.getLogger(ItemFilterViewController.class.getName());
    
    protected List<ItemCategory> filterViewItemCategorySelectionList = null;

    // Geenerated based on the currently selected item category. 
    protected List<ItemType> filterViewItemTypeList = null;

    protected List<UserGroup> filterViewUserGroupSelectionList = null;

    // Generated based on the currently selected user group.  
    protected List<UserInfo> filterViewUserInfoList = null;

    protected ItemType filterViewSelectedItemType = null;

    protected UserInfo filterViewSelectedUserInfo = null;

    protected boolean filterViewCategoryTypeListDataModelLoaded = false;

    protected boolean filterViewOwnerListDataModelLoaded = false;

    protected ListDataModel filterViewCategoryTypeDataModel = null;

    protected ListDataModel filterViewOwnerListDataModel = null;
    
    public void processPreRenderFilterViewPage() {
        ItemProjectController ipr = ItemProjectController.getInstance();        
        ipr.addItemControllerProjectChangeListener(this);     
        
        // Reload results 
        filterViewCategoryTypeListDataModelLoaded = false;
        filterViewOwnerListDataModelLoaded = false; 
    }

    @Override
    public void itemProjectChanged() {
        filterViewItemProjectChanged();
    }
    
    public List<UserGroup> getFilterViewUserGroupSelectionList() {
        return filterViewUserGroupSelectionList;
    }

    public List<UserInfo> getFilterViewUserInfoList() {
        if (filterViewUserInfoList == null) {
            if (filterViewUserGroupSelectionList == null
                    || filterViewUserGroupSelectionList.isEmpty()) {
                filterViewUserInfoList = new ArrayList<>();
            } else {
                filterViewUserInfoList = new ArrayList<>();
                for (UserGroup userGroup : filterViewUserGroupSelectionList) {
                    for (UserInfo userInfo : userGroup.getUserInfoList()) {
                        if (!filterViewUserInfoList.contains(userInfo)) {
                            filterViewUserInfoList.add(userInfo);
                        }
                    }
                }
                if (filterViewUserGroupSelectionList.size() > 1) {
                    // Alphabetical sort order needs to be re-applied. 
                    Comparator<UserInfo> userInfoAlphabeticalComperitor;
                    userInfoAlphabeticalComperitor = new Comparator<UserInfo>() {
                        @Override
                        public int compare(UserInfo o1, UserInfo o2) {
                            return o1.toString().compareTo(o2.toString());
                        }
                    };
                    filterViewUserInfoList.sort(userInfoAlphabeticalComperitor);
                }
            }
        }
        return filterViewUserInfoList;
    }

    public void setFilterViewUserGroupSelectionList(List<UserGroup> filterViewUserGroupSelectionList) {
        // List is different.
        if (CollectionUtility.isListDifferent((List<Object>) (Object) this.filterViewUserGroupSelectionList, (List<Object>) (Object) filterViewUserGroupSelectionList)) {
            filterViewUserInfoList = null;
            this.filterViewUserGroupSelectionList = filterViewUserGroupSelectionList;
            this.filterViewOwnerListDataModelLoaded = false;
            // Verify validity of current selection. 
            setFilterViewSelectedUserInfo(filterViewSelectedUserInfo);
        }
    }

    public List<ItemCategory> getFilterViewItemCategorySelection() {
        return filterViewItemCategorySelectionList;
    }

    public void setFilterViewItemCategorySelection(List<ItemCategory> filterViewItemCategorySelectionList) {
        // List is diferent.
        if (CollectionUtility.isListDifferent((List<Object>) (Object) this.filterViewItemCategorySelectionList, (List<Object>) (Object) filterViewItemCategorySelectionList)) {
            this.filterViewItemCategorySelectionList = filterViewItemCategorySelectionList;
            filterViewItemTypeList = null;
            filterViewCategoryTypeListDataModelLoaded = false;
            // Verify validity of current selection. 
            setFilterViewSelectedItemType(filterViewSelectedItemType);
        }
    }

    public ItemType getFilterViewSelectedItemType() {
        return filterViewSelectedItemType;
    }

    public void setFilterViewSelectedItemType(ItemType filterViewSelectedItemType) {
        if (getFilterViewItemTypeList().contains(filterViewSelectedItemType)) {
            filterViewCategoryTypeListDataModelLoaded = false;
            this.filterViewSelectedItemType = filterViewSelectedItemType;
        } else if (!getFilterViewItemTypeList().contains(this.filterViewSelectedItemType)) {
            // Current item is not valid
            filterViewCategoryTypeListDataModelLoaded = false;
            this.filterViewSelectedItemType = null;
        } else if (filterViewSelectedItemType == null) {
            filterViewCategoryTypeListDataModelLoaded = false;
            this.filterViewSelectedItemType = null;
        }
    }

    public UserInfo getFilterViewSelectedUserInfo() {
        return filterViewSelectedUserInfo;
    }

    public void setFilterViewSelectedUserInfo(UserInfo filterViewSelectedUserInfo) {
        if (filterViewSelectedUserInfo == null) {
            this.filterViewSelectedUserInfo = filterViewSelectedUserInfo;
            this.filterViewOwnerListDataModelLoaded = false;
        } else if (getFilterViewUserInfoList().contains(filterViewSelectedUserInfo)) {
            this.filterViewSelectedUserInfo = filterViewSelectedUserInfo;
            this.filterViewOwnerListDataModelLoaded = false;
        } else if (!getFilterViewUserInfoList().contains(filterViewSelectedUserInfo)) {
            this.filterViewSelectedUserInfo = null;
            this.filterViewOwnerListDataModelLoaded = false;
        }
    }

    public boolean isFilterViewItemTypeSelectOneDisabled() {
        return getFilterViewItemTypeList().isEmpty();
    }

    public boolean isFilterViewUserInfoSelectOneDisabled() {
        return getFilterViewUserInfoList().isEmpty();
    }

    protected void filterViewItemProjectChanged() {
        filterViewCategoryTypeListDataModelLoaded = false;
        filterViewOwnerListDataModelLoaded = false;
    }

    public List<ItemType> getFilterViewItemTypeList() {
        if (filterViewItemTypeList == null) {
            filterViewItemTypeList = new ArrayList<>();
            if (filterViewItemCategorySelectionList != null) {
                filterViewItemTypeList = getItemController().getAvaiableTypesForItemCategoryList(filterViewItemCategorySelectionList);
            }
        }
        return filterViewItemTypeList;
    }

    public ListDataModel getFilterViewCategoryTypeListDataModel() {
        if (filterViewCategoryTypeListDataModelLoaded == false) {
            List<Item> filterViewItemList = null;
            ItemProject project = ItemProjectController.getSelectedItemProject();
            if (project != null
                    || filterViewItemCategorySelectionList != null
                    || filterViewSelectedItemType != null) {
                filterViewItemList = getItemDbFacade().findByFilterViewCategoryTypeAttributes(project,
                        filterViewItemCategorySelectionList, filterViewSelectedItemType, getItemController().getDefaultDomainName());
            }

            filterViewCategoryTypeDataModel = createFilterViewListDataModel(filterViewItemList);
            filterViewCategoryTypeListDataModelLoaded = true;
        }

        return filterViewCategoryTypeDataModel;
    }

    public ListDataModel getFilterViewOwnerListDataModel() {
        if (filterViewOwnerListDataModelLoaded == false) {
            List<Item> filterViewItemList = null;
            ItemProject project = ItemProjectController.getSelectedItemProject();
            if (project != null
                    || filterViewSelectedUserInfo != null
                    || filterViewUserGroupSelectionList != null) {
                filterViewItemList = getItemDbFacade().findByFilterViewOwnerAttributes(project, filterViewUserGroupSelectionList, filterViewSelectedUserInfo, getItemController().getDefaultDomainName());
            }

            filterViewOwnerListDataModel = createFilterViewListDataModel(filterViewItemList);
            filterViewOwnerListDataModelLoaded = true;
        }

        return filterViewOwnerListDataModel;
    }
    
    protected ListDataModel createFilterViewListDataModel(List<Item> itemList) {
        if (itemList != null) {
            List<FilterViewResultItem> filterViewItemObjectList = new ArrayList<>();
            for (Item item : itemList) {
                FilterViewResultItem fvio = new FilterViewResultItem(item);
                prepareFilterViewResultItem(fvio);
                filterViewItemObjectList.add(fvio);
            }

            return new ListDataModel(filterViewItemObjectList);
        }
        return null;
    }
    
    protected void prepareFilterViewResultItem(FilterViewResultItem fvio) {
        Item item = fvio.getItemObject();
        if (!item.getItemElementDisplayList().isEmpty()) {
            try {
                TreeNode rootTreeNode = ItemElementUtility.createItemElementRoot(item);
                fvio.addFilterViewItemExpansion(rootTreeNode, "Item Assembly");
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
    }   
}
