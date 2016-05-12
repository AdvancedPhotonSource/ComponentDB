/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
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
public class ItemFacade extends CdbEntityFacade<Item> {

    @PersistenceContext(unitName = "CdbWebPortal3PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public List<Item> findByDomainAndEntityType(String domainName, String entityTypeName) {
        try{
            return (List<Item>) em.createNamedQuery("Item.findByDomainName")
                    .setParameter("domainName", domainName)
                    .setParameter("entityTypeName", entityTypeName)
                    .getResultList();
        } catch (NoResultException ex) {
            
        }
        return null;
    }

    public ItemFacade() {
        super(Item.class);
    }
    
}
