/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
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

    @PersistenceContext(unitName = "CdbWebPortal3PU")
    private EntityManager em;

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
    
}
