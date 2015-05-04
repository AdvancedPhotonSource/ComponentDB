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

import gov.anl.aps.cdb.portal.model.db.entities.ComponentSource;
import java.util.List;
import javax.ejb.Stateless;

/**
 * DB facade for component source objects.
 */
@Stateless
public class ComponentSourceDbFacade extends CdbEntityDbFacade<ComponentSource> {

    public ComponentSourceDbFacade() {
        super(ComponentSource.class);
    }

    public List<ComponentSource> findAllByComponentId(Integer componentId) {
        return (List<ComponentSource>) em.createNamedQuery("ComponentSource.findAllByComponentId")
                .setParameter("componentId", componentId)
                .getResultList();
    }
}
