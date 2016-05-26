/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
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
public class EntityTypeFacade extends CdbEntityFacade<EntityType> {

    @PersistenceContext(unitName = "CdbWebPortal3PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntityTypeFacade() {
        super(EntityType.class);
    }
    
    public EntityType findByName(String name) {
        try{
            return (EntityType) em.createNamedQuery("EntityType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
}
