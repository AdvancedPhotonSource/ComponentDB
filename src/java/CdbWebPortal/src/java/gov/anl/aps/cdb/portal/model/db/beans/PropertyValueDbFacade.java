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

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import javax.ejb.Stateless;

/**
 * DB facade for property values.
 */
@Stateless
public class PropertyValueDbFacade extends CdbEntityDbFacade<PropertyValue> {

    public PropertyValueDbFacade() {
        super(PropertyValue.class);
    }

}
