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

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import javax.ejb.Stateless;

/**
 * DB facade for property value history objects.
 */
@Stateless
public class PropertyValueHistoryDbFacade extends CdbEntityDbFacade<PropertyValueHistory> {

    public PropertyValueHistoryDbFacade() {
        super(PropertyValueHistory.class);
    }

}
