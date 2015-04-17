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

import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * DB facade for property types.
 */
@Stateless
public class PropertyTypeDbFacade extends CdbEntityDbFacade<PropertyType> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropertyTypeDbFacade() {
        super(PropertyType.class);
    }

    @Override
    public List<PropertyType> findAll() {
        return (List<PropertyType>) em.createNamedQuery("PropertyType.findAll")
                .getResultList();
    }

    public PropertyType findByName(String name) {
        try {
            return (PropertyType) em.createNamedQuery("PropertyType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public PropertyType findById(Integer id) {
        try {
            return (PropertyType) em.createNamedQuery("PropertyType.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
}
