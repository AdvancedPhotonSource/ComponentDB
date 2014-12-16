/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstance;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class ComponentInstanceFacade extends AbstractFacade<ComponentInstance>
{
    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentInstanceFacade() {
        super(ComponentInstance.class);
    }
 
    public ComponentInstance findById(Integer id) {
        try {
            return (ComponentInstance) em.createNamedQuery("ComponentInstance.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }    
}
