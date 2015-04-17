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

import gov.anl.aps.cdb.portal.model.db.entities.AssemblyComponent;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * DB facade for assembly components.
 */
@Stateless
public class AssemblyComponentDbFacade extends CdbEntityDbFacade<AssemblyComponent> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AssemblyComponentDbFacade() {
        super(AssemblyComponent.class);
    }

    public AssemblyComponent findById(Integer id) {
        try {
            return (AssemblyComponent) em.createNamedQuery("AssemblyComponent.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
}
