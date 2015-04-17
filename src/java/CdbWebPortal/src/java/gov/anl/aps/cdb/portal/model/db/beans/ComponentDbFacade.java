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

import gov.anl.aps.cdb.portal.model.db.entities.Component;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * DB facade for components.
 */
@Stateless
public class ComponentDbFacade extends CdbEntityDbFacade<Component> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentDbFacade() {
        super(Component.class);
    }

    @Override
    public List<Component> findAll() {
        return (List<Component>) em.createNamedQuery("Component.findAll")
                .getResultList();
    }

    public Component findByName(String name) {
        try {
            return (Component) em.createNamedQuery("Component.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Component findById(Integer id) {
        try {
            return (Component) em.createNamedQuery("Component.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
}
