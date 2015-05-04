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

import gov.anl.aps.cdb.portal.model.db.entities.ComponentType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for component types.
 */
@Stateless
public class ComponentTypeDbFacade extends CdbEntityDbFacade<ComponentType> {

    @Override
    public List<ComponentType> findAll() {
        return (List<ComponentType>) em.createNamedQuery("ComponentType.findAll")
                .getResultList();
    }

    public ComponentTypeDbFacade() {
        super(ComponentType.class);
    }

    public ComponentType findByName(String name) {
        try {
            return (ComponentType) em.createNamedQuery("ComponentType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public ComponentType findById(Integer id) {
        try {
            return (ComponentType) em.createNamedQuery("ComponentType.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

}
