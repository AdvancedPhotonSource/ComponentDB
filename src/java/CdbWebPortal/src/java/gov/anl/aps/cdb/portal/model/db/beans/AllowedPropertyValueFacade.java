/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class AllowedPropertyValueFacade extends CdbEntityFacade<AllowedPropertyValue> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AllowedPropertyValueFacade() {
        super(AllowedPropertyValue.class);
    }
    
    public List<AllowedPropertyValue> findAllByPropertyTypeId(Integer propertyTypeId) {
        return (List<AllowedPropertyValue>) em.createNamedQuery("AllowedPropertyType.findAllByPropertyTypeId")
                .setParameter("propertyTypeId", propertyTypeId)
                .getResultList();
    }
    
    public static AllowedPropertyValueFacade getInstance() {
        return (AllowedPropertyValueFacade) SessionUtility.findFacade(AllowedPropertyValueFacade.class.getSimpleName()); 
    }
    
}
