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

import gov.anl.aps.cdb.portal.model.db.entities.LocationType;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for location types.
 */
@Stateless
public class LocationTypeDbFacade extends CdbEntityDbFacade<LocationType> {

    public LocationTypeDbFacade() {
        super(LocationType.class);
    }

    public LocationType findByName(String name) {
        try {
            return (LocationType) em.createNamedQuery("LocationType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public LocationType findById(Integer id) {
        try {
            return (LocationType) em.createNamedQuery("LocationType.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
}
