/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/portal/model/db/utilities/EntityInfoUtility.java $
 *   $Date: 2016-02-23 08:02:37 -0600 (Tue, 23 Feb 2016) $
 *   $Revision: 1068 $
 *   $Author: djarosz $
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DB utility class for entity info objects.
 */
public class EntityInfoUtility {

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
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        return entityInfo;
    }

    public static void updateEntityInfo(EntityInfo entityInfo) {
        if (entityInfo == null) {
            return;
        }
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
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
