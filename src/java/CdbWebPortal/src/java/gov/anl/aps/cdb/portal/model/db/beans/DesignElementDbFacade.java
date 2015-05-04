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
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for design elements.
 */
@Stateless
public class DesignElementDbFacade extends CdbEntityDbFacade<DesignElement> {

    public DesignElementDbFacade() {
        super(DesignElement.class);
    }

    public DesignElement findByName(String name) {
        try {
            return (DesignElement) em.createNamedQuery("DesignElement.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public DesignElement findById(Integer id) {
        try {
            return (DesignElement) em.createNamedQuery("DesignElement.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public DesignElement findByNameAndParentDesign(String name, Design parentDesign) {
        try {
            return (DesignElement) em.createNamedQuery("DesignElement.findByNameAndParentDesign")
                    .setParameter("name", name)
                    .setParameter("parentDesign", parentDesign)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
}
