/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import javax.ejb.Stateless;

/**
 * DB facade for user settings.
 */
@Stateless
public class UserSettingDbFacade extends CdbEntityDbFacade<UserSetting> {

    public UserSettingDbFacade() {
        super(UserSetting.class);
    }

}
