/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

/**
 *
 * @author djarosz
 */
@Stateless
public class PropertyValueFacade extends CdbEntityFacade<PropertyValue> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropertyValueFacade() {
        super(PropertyValue.class);
    }
    
    public static PropertyValueFacade getInstance() {
        return (PropertyValueFacade) SessionUtility.findFacade(PropertyValueFacade.class.getSimpleName()); 
    }
    
    public List<PropertyValue> getPropertyValueListByTypeIdAndValue(String propertyValue, Integer propertyTypeId) {
        try {
            
            return (List<PropertyValue> ) em.createNamedQuery("PropertyValue.findByValueAndTypeId")
                    .setParameter("value", propertyValue)
                    .setParameter("propertyTypeId", propertyTypeId)
                    .getResultList();                        
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    public List<PropertyValue> fetchRelationshipParentPropertyValues(Integer itemId, Integer parentItemId, Integer relationshipTypeId) {        
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("propertyValue.fetchRelationshipParentPropertyValues");
            query.setParameter("item_id", itemId);
            query.setParameter("parent_item_id", parentItemId);
            query.setParameter("relationship_type_id", relationshipTypeId);            

            List<PropertyValue> resultList = query.getResultList();

            return resultList;
        } catch (NoResultException ex) {
        }
        return null;
    }
    
}
