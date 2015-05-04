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
import javax.persistence.NoResultException;

/**
 * DB facade for assembly components.
 */
@Stateless
public class AssemblyComponentDbFacade extends CdbEntityDbFacade<AssemblyComponent> {

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
