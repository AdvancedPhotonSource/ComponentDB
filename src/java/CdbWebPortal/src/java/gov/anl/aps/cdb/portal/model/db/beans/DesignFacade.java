/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Design;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class DesignFacade extends AbstractFacade<Design>
{
    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DesignFacade() {
        super(Design.class);
    }
    public Design findByName(String name) {
        try {
            return (Design)em.createNamedQuery("Design.findByName")
                .setParameter("name", name)
                .getSingleResult();
        }
        catch (NoResultException ex) {
        }
        return null;
    }
    
    public Design findById(Integer id) {
        try {
            return (Design)em.createNamedQuery("Design.findById")
                .setParameter("id", id)
                .getSingleResult();
        }
        catch (NoResultException ex) {
        }
        return null;
    }   
    
    
}
