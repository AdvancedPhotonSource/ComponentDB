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

import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import javax.ejb.Stateless;

/**
 * DB facade for setting types.
 */
@Stateless
public class SettingTypeDbFacade extends CdbEntityDbFacade<SettingType> {

    public SettingTypeDbFacade() {
        super(SettingType.class);
    }

}
