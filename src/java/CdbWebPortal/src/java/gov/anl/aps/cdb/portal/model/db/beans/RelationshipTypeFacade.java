/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class RelationshipTypeFacade extends CdbEntityFacade<RelationshipType> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public static RelationshipType getRelationshipTypeLocation() {
        RelationshipType relationshipType
                = RelationshipTypeFacade.getInstance().findByName(
                        ItemElementRelationshipTypeNames.itemLocation.getValue());
        return relationshipType;
    }

    public static RelationshipType getRelationshipTypeTemplate() {
        RelationshipType relationshipType
                = RelationshipTypeFacade.getInstance().findByName(
                        ItemElementRelationshipTypeNames.template.getValue());
        return relationshipType;
    }

    public static RelationshipType getRelationshipTypeCable() {
        RelationshipType relationshipType
                = RelationshipTypeFacade.getInstance().findByName(
                        ItemElementRelationshipTypeNames.itemCableConnection.getValue());
        return relationshipType;
    }

    public static RelationshipType getRelationshipTypeMaarc() {
        RelationshipType relationshipType
                = RelationshipTypeFacade.getInstance().findByName(
                        ItemElementRelationshipTypeNames.maarc.getValue());
        return relationshipType;
    }

    public RelationshipType findByName(String name) {
        try{
            return (RelationshipType) em.createNamedQuery("RelationshipType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            
        }
        return null;
    }

    public RelationshipTypeFacade() {
        super(RelationshipType.class);
    }
    
    public static RelationshipTypeFacade getInstance() {
        return (RelationshipTypeFacade) SessionUtility.findFacade(RelationshipTypeFacade.class.getSimpleName()); 
    }
    
}
