/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Domain;
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

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public List<Item> findByDomainAndEntityType(String domainName, String entityTypeName) {
        try{
            return (List<Item>) em.createNamedQuery("Item.findByDomainNameAndEntityType")
                    .setParameter("domainName", domainName)
                    .setParameter("entityTypeName", entityTypeName)
                    .getResultList();
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
    public List<Item> findByDomain(String domainName) {
        try{
            return (List<Item>) em.createNamedQuery("Item.findByDomainName")
                    .setParameter("domainName", domainName)                    
                    .getResultList();
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
    public List<Item> findByDomainOrderByQrId(String domainName) {
        try{
            return (List<Item>) em.createNamedQuery("Item.findByDomainNameOrderByQrId")
                    .setParameter("domainName", domainName)                    
                    .getResultList();
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
    public List<Item> findByName(String name) {
        try{
            return (List<Item>) em.createNamedQuery("Item.findByName")
                    .setParameter("name", name)
                    .getResultList();
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
    public Item findByUniqueAttributes(Item derivedFromItem, Domain domain, 
            String name, String itemIdentifier1, String itemIdentifier2) {
        String queryString = "SELECT i FROM Item i WHERE "; 
        if (derivedFromItem == null) {
           queryString += "i.derivedFromItem is null ";  
        } else {
            queryString += "i.derivedFromItem.id = " + derivedFromItem.getId() + " "; 
        }
        
        queryString += " AND i.domain.id = " + domain.getId() + " "; 
        
        if(name == null || name.isEmpty()) {
           queryString += "AND (i.name is null OR i.name = '') ";  
        } else {
           queryString += "AND i.name = '" + name + "' ";   
        }
        
        if(itemIdentifier1 == null || itemIdentifier1.isEmpty()) {
           queryString += "AND (i.itemIdentifier1 is null OR i.itemIdentifier1 = '')";  
        } else {
           queryString += "AND i.itemIdentifier1 = '" + itemIdentifier1 + "' ";   
        }
        
        if(itemIdentifier2 == null || itemIdentifier2.isEmpty()) {
           queryString += "AND (i.itemIdentifier2 is null OR i.itemIdentifier2 = '') ";  
        } else {
           queryString += "AND i.itemIdentifier2 = '" + itemIdentifier2 + "' ";   
        }
        
        try {
            return (Item) em.createQuery(queryString).getSingleResult(); 
        } catch (NoResultException ex) {
        }       
        
        return null; 
    }
    
    public List<Item> findByDerivedFromItemId(int derivedFromItemId) {
        try{
            return (List<Item>) em.createNamedQuery("Item.findByDerivedFromItemId")
                    .setParameter("id", derivedFromItemId)
                    .getResultList();
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
    public List<Item> findByDomainAndEntityTypeOrderByQrId(String domainName, String entityTypeName) {
        try{
            return (List<Item>) em.createNamedQuery("Item.findByDomainNameOderByQrId")
                    .setParameter("domainName", domainName)
                    .setParameter("entityTypeName", entityTypeName)
                    .getResultList();
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
    public List<Item> findByDomainAndDerivedEntityTypeOrderByQrId(String domainName, String entityTypeName) {
        try{
            return (List<Item>) em.createNamedQuery("Item.findByDomainAndDerivedEntityTypeOrderByQrId")
                    .setParameter("domainName", domainName)
                    .setParameter("entityTypeName", entityTypeName)
                    .getResultList();
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
    public List<Item> findAllWithName() {
        return (List<Item>) em.createNamedQuery("Item.findAllWithName").getResultList(); 
    }

    public ItemFacade() {
        super(Item.class);
    }
    
    public Item findByQrId(Integer qrId) {
        try {
            return (Item) em.createNamedQuery("Item.findByQrId")
                    .setParameter("qrId", qrId)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    public Item findById(Integer id) {
        try {
            return (Item) em.createNamedQuery("Item.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
}
