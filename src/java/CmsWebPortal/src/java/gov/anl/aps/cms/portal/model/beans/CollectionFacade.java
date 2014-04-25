/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.beans;

import gov.anl.aps.cms.portal.model.entities.Collection;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class CollectionFacade extends AbstractFacade<Collection> {
    @PersistenceContext(unitName = "CmsWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CollectionFacade() {
        super(Collection.class);
    }

    public Collection findByName(String name) {
        try {
            return (Collection)em.createNamedQuery("Collection.findByName")
                .setParameter("name", name)
                .getSingleResult();
        }
        catch (NoResultException ex) {
        }
        return null;
    }
    
    public Collection findById(Integer id) {
        try {
            return (Collection)em.createNamedQuery("Collection.findById")
                .setParameter("id", id)
                .getSingleResult();
        }
        catch (NoResultException ex) {
        }
        return null;
    }       
}
