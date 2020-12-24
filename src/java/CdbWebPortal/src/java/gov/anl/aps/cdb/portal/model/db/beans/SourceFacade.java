/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class SourceFacade extends CdbEntityFacade<Source> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    public static SourceFacade getInstance() {
        return (SourceFacade) SessionUtility.findFacade(SourceFacade.class.getSimpleName()); 
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SourceFacade() {
        super(Source.class);
    }
    
    @Override
    public List<Source> findAll() {
        return (List<Source>) em.createNamedQuery("Source.findAll")
                .getResultList();
    }

    public Source findById(Integer id) {
        try {
            return (Source) em.createNamedQuery("Source.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public Source findByName(String name) {
        try {
            return (Source) em.createNamedQuery("Source.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    /**
     * Find unique entity by name.  Returns null if none is found, or raises
     * CdbException if multiple instances are found.
     */
    public Source findUniqueByName(String name, String filterDomainName) throws CdbException {
        
        if ((name == null) || (name.isEmpty())) {
            return null;
        }
        
        // uses findByName() since that method already finds a unique instance by name
        return findByName(name);       
    }


    public List<Source> findAllSortedByName() {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root<Source> s = cq.from(Source.class);
        cq.select(s);
        cq.orderBy(cb.asc(s.get("name")));
        return getEntityManager().createQuery(cq).getResultList();
    }
    
}
