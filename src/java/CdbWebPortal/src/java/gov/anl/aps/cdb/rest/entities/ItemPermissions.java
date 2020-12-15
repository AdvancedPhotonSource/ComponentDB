/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author djarosz
 */
public class ItemPermissions {

    private Integer itemId;
    private UserInfo ownerUser;
    private UserGroup ownerGroup; 
    private Boolean groupWriteable; 

    public ItemPermissions(Item item) {
        itemId = item.getId(); 
        ownerUser = item.getOwnerUser();
        ownerGroup = item.getOwnerUserGroup(); 
        groupWriteable = item.getEntityInfo().getIsGroupWriteable();
    }

    public ItemPermissions() {
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public UserInfo getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(UserInfo ownerUser) {
        this.ownerUser = ownerUser;
    }

    public UserGroup getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(UserGroup ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    public Boolean getGroupWriteable() {
        return groupWriteable;
    }

    public void setGroupWriteable(Boolean groupWriteable) {
        this.groupWriteable = groupWriteable;
    }

}
