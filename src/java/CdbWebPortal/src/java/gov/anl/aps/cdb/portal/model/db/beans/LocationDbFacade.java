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

import gov.anl.aps.cdb.portal.model.db.entities.Location;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for locations.
 */
@Stateless
public class LocationDbFacade extends CdbEntityDbFacade<Location> {
    
    public LocationDbFacade() {
        super(Location.class);
    }

    @Override
    public List<Location> findAll() {
        return (List<Location>) em.createNamedQuery("Location.findAll")
                .getResultList();
    }

    public Location findByName(String name) {
        try {
            return (Location) em.createNamedQuery("Location.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Location findById(Integer id) {
        try {
            return (Location) em.createNamedQuery("Location.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public List<Location> findLocationsWithoutParents() {
        return (List<Location>) em.createNamedQuery("Location.findLocationsWithoutParents")
                .getResultList();
    }
}
