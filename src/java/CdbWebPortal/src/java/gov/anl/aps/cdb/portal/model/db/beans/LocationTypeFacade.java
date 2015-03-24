/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.LocationType;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class LocationTypeFacade extends AbstractDbFacade<LocationType>
{
    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LocationTypeFacade() {
        super(LocationType.class);
    }
   
    
    public LocationType findByName(String name) {
        try {
            return (LocationType)em.createNamedQuery("LocationType.findByName")
                .setParameter("name", name)
                .getSingleResult();
        }
        catch (NoResultException ex) {
        }
        return null;
    }

    public LocationType findById(Integer id) {
        try {
            return (LocationType) em.createNamedQuery("LocationType.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } 
        catch (NoResultException ex) {
        }
        return null;
    }        
}    

