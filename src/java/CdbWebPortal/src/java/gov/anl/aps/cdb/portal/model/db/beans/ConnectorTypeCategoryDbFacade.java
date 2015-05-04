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

import gov.anl.aps.cdb.portal.model.db.entities.ConnectorTypeCategory;
import javax.ejb.Stateless;

/**
 * DB facade for component type categories.
 */
@Stateless
public class ConnectorTypeCategoryDbFacade extends CdbEntityDbFacade<ConnectorTypeCategory> {

    public ConnectorTypeCategoryDbFacade() {
        super(ConnectorTypeCategory.class);
    }

}
