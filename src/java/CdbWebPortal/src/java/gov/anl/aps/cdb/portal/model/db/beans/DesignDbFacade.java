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

import gov.anl.aps.cdb.portal.model.db.entities.Design;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for designs.
 */
@Stateless
public class DesignDbFacade extends CdbEntityDbFacade<Design> {

    public DesignDbFacade() {
        super(Design.class);
    }

    @Override
    public List<Design> findAll() {
        return (List<Design>) em.createNamedQuery("Design.findAll")
                .getResultList();
    }

    public Design findByName(String name) {
        try {
            return (Design) em.createNamedQuery("Design.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Design findById(Integer id) {
        try {
            return (Design) em.createNamedQuery("Design.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

}
