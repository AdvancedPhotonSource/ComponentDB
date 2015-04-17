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

import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstanceLocationHistory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * DB facade for component instance location history objects.
 */
@Stateless
public class ComponentInstanceLocationHistoryDbFacade extends CdbEntityDbFacade<ComponentInstanceLocationHistory>
{
    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentInstanceLocationHistoryDbFacade() {
        super(ComponentInstanceLocationHistory.class);
    }
    
}
