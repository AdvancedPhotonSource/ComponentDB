/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;
import java.util.List;

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
}
