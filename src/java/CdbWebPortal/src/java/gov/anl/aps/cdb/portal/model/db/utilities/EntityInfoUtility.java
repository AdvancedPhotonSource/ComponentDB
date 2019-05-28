/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DB utility class for entity info objects.
 */
public class EntityInfoUtility {

    private static final String AdminGroupListPropertyName = "cdb.portal.adminGroupList";
    private static final List<String> adminGroupNameList = ConfigurationUtility.getPortalPropertyList(AdminGroupListPropertyName);

    public static EntityInfo createEntityInfo() {
        UserInfo createdByUser = (UserInfo) SessionUtility.getUser();
        if (createdByUser == null) {
            // Created by user cannot be empty.
            return null;
        }
        Date createdOnDateTime = new Date();
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setOwnerUser(createdByUser);
        entityInfo.setCreatedOnDateTime(createdOnDateTime);
        entityInfo.setCreatedByUser(createdByUser);
        entityInfo.setLastModifiedOnDateTime(createdOnDateTime);
        entityInfo.setLastModifiedByUser(createdByUser);
        entityInfo.setIsGroupWriteable(true);
        List<UserGroup> ownerUserGroupList = createdByUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            UserGroup ownerUserGroup = null; 
            for (UserGroup userGroup : ownerUserGroupList) {
                if (adminGroupNameList.contains(userGroup.getName()) == false) {
                    ownerUserGroup = userGroup;
                    break;
                }
            }
            if (ownerUserGroup == null) {
                ownerUserGroup = ownerUserGroupList.get(0);
            }
            
            entityInfo.setOwnerUserGroup(ownerUserGroup);

        }
        return entityInfo;
    }

    public static void updateEntityInfo(EntityInfo entityInfo) {
        if (entityInfo == null) {
            return;
        }
        UserInfo userInfo = (UserInfo) SessionUtility.getUser();
        updateEntityInfo(entityInfo, userInfo);
    }
    
    public static void updateEntityInfo(EntityInfo entityInfo, UserInfo userInfo) {
        if (entityInfo == null) {
            return;
        }
        UserInfo lastModifiedByUser = userInfo; 
        Date lastModifiedOnDateTime = new Date();
        entityInfo.setLastModifiedOnDateTime(lastModifiedOnDateTime);
        entityInfo.setLastModifiedByUser(lastModifiedByUser);
    }

    public static void searchEntityInfo(EntityInfo entityInfo, Pattern searchPattern, SearchResult searchResult) {
        String baseKey = "entityInfo";
        String entityInfoKey = baseKey + "/ownerUsername";
        searchResult.doesValueContainPattern(entityInfoKey, entityInfo.getOwnerUser().getUsername(), searchPattern);
        entityInfoKey = baseKey + "/ownerLastName";
        searchResult.doesValueContainPattern(entityInfoKey, entityInfo.getOwnerUser().getLastName(), searchPattern);
        entityInfoKey = baseKey + "/ownerFirstName";
        searchResult.doesValueContainPattern(entityInfoKey, entityInfo.getOwnerUser().getFirstName(), searchPattern);
        entityInfoKey = baseKey + "/ownerGroupName";
        searchResult.doesValueContainPattern(entityInfoKey, entityInfo.getOwnerUserGroup().getName(), searchPattern);
    }
}
