/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.objects;

/**
 *
 * @author djarosz
 */
public class EntityInfo extends CdbObject{
    private UserGroup ownerUserGroup; 
    private int ownerUserGroupId; 
    private UserInfo ownerUserInfo; 
    private int ownerUserId; 
    private UserInfo createdByUserInfo; 
    private int createdByUserId; 
    private String createdOnDateTime; 
    private UserInfo lastModifiedByUserInfo; 
    private String lastModifiedOnDateTime;
    private int lastModifiedByUserId;  
    
    public EntityInfo(){
        
    }

    public void setOwnerUserGroup(UserGroup ownerUserGroup) {
        this.ownerUserGroup = ownerUserGroup;
    }

    public void setOwnerUserGroupId(int ownerUserGroupId) {
        this.ownerUserGroupId = ownerUserGroupId;
    }

    public void setOwnerUserInfo(UserInfo ownerUserInfo) {
        this.ownerUserInfo = ownerUserInfo;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public void setCreatedByUserInfo(UserInfo createdByUserInfo) {
        this.createdByUserInfo = createdByUserInfo;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public void setCreatedOnDateTime(String createdOnDateTime) {
        this.createdOnDateTime = createdOnDateTime;
    }

    public void setLastModifiedByUserInfo(UserInfo lastModifiedByUserInfo) {
        this.lastModifiedByUserInfo = lastModifiedByUserInfo;
    }

    public void setLastModifiedOnDateTime(String lastModifiedOnDateTime) {
        this.lastModifiedOnDateTime = lastModifiedOnDateTime;
    }

    public void setLastModifiedByUserId(int lastModifiedByUserId) {
        this.lastModifiedByUserId = lastModifiedByUserId;
    }

    public UserGroup getOwnerUserGroup() {
        return ownerUserGroup;
    }

    public int getOwnerUserGroupId() {
        return ownerUserGroupId;
    }

    public UserInfo getOwnerUserInfo() {
        return ownerUserInfo;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public UserInfo getCreatedByUserInfo() {
        return createdByUserInfo;
    }

    public int getCreatedByUserId() {
        return createdByUserId;
    }

    public String getCreatedOnDateTime() {
        return createdOnDateTime;
    }

    public UserInfo getLastModifiedByUserInfo() {
        return lastModifiedByUserInfo;
    }

    public String getLastModifiedOnDateTime() {
        return lastModifiedOnDateTime;
    }

    public int getLastModifiedByUserId() {
        return lastModifiedByUserId;
    }
    
}
