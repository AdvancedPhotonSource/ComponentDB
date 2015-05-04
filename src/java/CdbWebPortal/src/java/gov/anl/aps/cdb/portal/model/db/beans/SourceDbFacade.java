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

import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for sources.
 */
@Stateless
public class SourceDbFacade extends CdbEntityDbFacade<Source> {

    public SourceDbFacade() {
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

    public List<Source> findAllSortedByName() {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        javax.persistence.criteria.Root<Source> s = cq.from(Source.class);
        cq.select(s);
        cq.orderBy(cb.asc(s.get("name")));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
