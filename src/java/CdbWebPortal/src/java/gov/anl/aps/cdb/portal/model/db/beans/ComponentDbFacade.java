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
import javax.persistence.NoResultException;

/**
 * DB facade for components.
 */
@Stateless
public class ComponentDbFacade extends CdbEntityDbFacade<Component> {

    public ComponentDbFacade() {
        super(Component.class);
    }

    @Override
    public List<Component> findAll() {
        return (List<Component>) em.createNamedQuery("Component.findAll")
                .getResultList();
    }

    public Component findByNameAndModelNumber(String name, String modelNumber) {
        try {
            return (Component) em.createNamedQuery("Component.findByNameAndModelNumber")
                    .setParameter("name", name)
                    .setParameter("modelNumber", modelNumber)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Component findByModelNumber(String modelNumber) {
        try {
            return (Component) em.createNamedQuery("Component.findByModelNumber")
                    .setParameter("modelNumber", modelNumber)
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
