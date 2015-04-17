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

import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * DB facade for property type categories.
 */
@Stateless
public class PropertyTypeCategoryDbFacade extends CdbEntityDbFacade<PropertyTypeCategory> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropertyTypeCategoryDbFacade() {
        super(PropertyTypeCategory.class);
    }

    @Override
    public List<PropertyTypeCategory> findAll() {
        return (List<PropertyTypeCategory>) em.createNamedQuery("PropertyTypeCategory.findAll")
                .getResultList();
    }

    public PropertyTypeCategory findByName(String name) {
        try {
            return (PropertyTypeCategory) em.createNamedQuery("PropertyTypeCategory.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public PropertyTypeCategory findById(Integer id) {
        try {
            return (PropertyTypeCategory) em.createNamedQuery("PropertyTypeCategory.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
}
