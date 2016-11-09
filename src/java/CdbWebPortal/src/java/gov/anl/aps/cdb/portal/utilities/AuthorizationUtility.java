/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;

/**
 * Utility for handling user authorization for manipulating CDB entities.
 */
public class AuthorizationUtility {

    public static boolean isEntityWriteableByUser(EntityInfo entityInfo, UserInfo userInfo) {
        // Users can write object if entityInfo != null and:
        // current user is owner, or the object is writeable by owner group
        // and current user is member of that group
        if (entityInfo == null || userInfo == null) {
            return false;
        }

        UserInfo ownerUser = entityInfo.getOwnerUser();
        if (ownerUser != null && ownerUser.getId().equals(userInfo.getId())) {
            return true;
        }

        Boolean isGroupWriteable = entityInfo.getIsGroupWriteable();
        if (isGroupWriteable == null || !isGroupWriteable.booleanValue()) {
            return false;
        }

        UserGroup ownerUserGroup = entityInfo.getOwnerUserGroup();
        if (ownerUserGroup == null) {
            return false;
        }

        for (UserGroup userGroup : userInfo.getUserGroupList()) {
            if (ownerUserGroup.getId().equals(userGroup.getId())) {
                return true;
            }
        }
        return false;
    }

    public static <EntityType extends CdbEntity> boolean isEntityWriteableByUser(EntityType entity, UserInfo userInfo) {
        if (entity instanceof UserInfo) {
            return ObjectUtility.equals(entity, userInfo);
        }
        return false;
    }

}
