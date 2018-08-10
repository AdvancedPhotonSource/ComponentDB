/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemElementRelationshipFacade extends CdbEntityFacade<ItemElementRelationship> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemElementRelationshipFacade() {
        super(ItemElementRelationship.class);
    }
    
    public ItemElementRelationship findItemElementRelationshipByNameAndItemElementId(String relationshipTypeName, int itemElementId) throws CdbException {
        try{
            return (ItemElementRelationship) em
                    .createNamedQuery("ItemElementRelationship.findByRelationshipTypeNameAndFirstElementId")
                    .setParameter("relationshipTypeName", relationshipTypeName)
                    .setParameter("itemElementId", itemElementId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            
        } catch (NonUniqueResultException ex) {
            throw new CdbException(ex); 
        }
        return null;
    }
    
    public List<ItemElementRelationship> findItemElementRelationshipListByNameAndItemElementId(String relationshipTypeName, int itemElementId) {
        try{
            return (List<ItemElementRelationship>) em
                    .createNamedQuery("ItemElementRelationship.findByRelationshipTypeNameAndFirstElementId")
                    .setParameter("relationshipTypeName", relationshipTypeName)
                    .setParameter("itemElementId", itemElementId)
                    .getResultList(); 
        } catch (NoResultException ex) {
            
        }
        return null;
    }
    
}
