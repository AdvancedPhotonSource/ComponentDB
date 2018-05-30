/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
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
public class PropertyTypeFacade extends CdbEntityFacade<PropertyType> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;
    
    private final String QUERY_STRING_START = "SELECT p FROM PropertyType p ";

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropertyTypeFacade() {
        super(PropertyType.class);
    }
    
    public PropertyType findByName(String name) {
        try {
            return (PropertyType) em.createNamedQuery("PropertyType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public PropertyType findById(Integer id) {
        try {
            return (PropertyType) em.createNamedQuery("PropertyType.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    public List<PropertyType> findByPropertyTypeHandler(PropertyTypeHandler handler){
        try{
            return (List<PropertyType>) em.createNamedQuery("PropertyType.findByPropertyTypeHandler")
                    .setParameter("propertyTypeHandler", handler)
                    .getResultList(); 
        }catch (NoResultException ex) {
        }
        return null; 
    }
    
    public List<PropertyType> findByPropertyTypeCategory(PropertyTypeCategory category){
        try{
            return (List<PropertyType>) em.createNamedQuery("PropertyType.findByPropertyTypeCategory")
                    .setParameter("propertyTypeCategory", category)
                    .getResultList(); 
        }catch (NoResultException ex) {
        }
        return null; 
    }
    
    public List<PropertyType> findByPropertyInternalStatus(Boolean isInternal){
        try{
            return (List<PropertyType>) em.createNamedQuery("PropertyType.findByInternalStatus")
                    .setParameter("isInternal", isInternal)
                    .getResultList(); 
        }catch (NoResultException ex) {
        }
        return null; 
    }
    
    @Override
    public List<PropertyType> findAll() {
        return (List<PropertyType>) em.createNamedQuery("PropertyType.findAll")
                .getResultList();
    }
    
    /**
     * Generates a query for a given category and handler list. 
     * 
     * @param propertyTypeCategoryList [Optional] will perform query using given attribute.
     * @param propertyTypeHandlerList [Optional] will perform query using given attribute.
     * @param isInternal Should the property types displayed be internal.  
     * 
     * @return 
     */
    public List<PropertyType> findByFilterViewAttributes(
            List<PropertyTypeCategory> propertyTypeCategoryList,
            List<PropertyTypeHandler> propertyTypeHandlerList, 
            String domainName,
            Boolean isInternal) {
        String queryString = QUERY_STRING_START; 
        String propertyTypeCategoryQueryString = null;
        String propertyTypeHandlerQueryString = null;
        
        if (domainName != null) {
            queryString += "JOIN p.allowedDomainList AS ad ";            
        }
        
        if (propertyTypeCategoryList != null) {
            if (!propertyTypeCategoryList.isEmpty()) {
                String propertyTypeCategoryRef = "p.propertyTypeCategory.name"; 
                
                propertyTypeCategoryQueryString = "("; 
                
                for (PropertyTypeCategory ptc : propertyTypeCategoryList) {
                    if (propertyTypeCategoryList.indexOf(ptc) != 0) {
                        propertyTypeCategoryQueryString += " OR "; 
                    }
                    propertyTypeCategoryQueryString += propertyTypeCategoryRef + " = '" + ptc.getName() + "'"; 
                }
                propertyTypeCategoryQueryString += ")";                 
            }
        }
        
        if (propertyTypeHandlerList != null) {
            if(!propertyTypeHandlerList.isEmpty()) {
                String propertyTypeHandlerRef = "p.propertyTypeHandler.name"; 
                
                propertyTypeHandlerQueryString = "(";
                
                for (PropertyTypeHandler pth : propertyTypeHandlerList) {
                    if (propertyTypeHandlerList.indexOf(pth) != 0) {
                        propertyTypeHandlerQueryString += " OR ";
                    }
                    propertyTypeHandlerQueryString += propertyTypeHandlerRef + " = '" + pth.getName() + "'";
                }
                
                propertyTypeHandlerQueryString += ")"; 
            }
        }
        
        if (propertyTypeCategoryQueryString != null 
                || propertyTypeHandlerQueryString != null 
                || domainName != null) {
            queryString += "WHERE "; 
            if (propertyTypeCategoryQueryString != null) {
                queryString += propertyTypeCategoryQueryString + " ";  
            } 
            if (propertyTypeHandlerQueryString != null) {
                if (propertyTypeCategoryQueryString != null) {
                    queryString += "AND "; 
                }
                queryString += propertyTypeHandlerQueryString;
            }
            if (propertyTypeCategoryQueryString != null || propertyTypeHandlerQueryString != null) {
                queryString += " AND ";
            }
            queryString += "p.isInternal = " + isInternal.toString();
            
            if (domainName != null) {
                queryString += " AND ad.name = '" + domainName + "'"; 
            }
            
            queryString += " ORDER BY p.name ASC"; 
            
            return (List<PropertyType>) em.createQuery(queryString).getResultList();
        }
        
        
        
        return null;         
    }
    
}
