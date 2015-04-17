/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * DB facade for allowed property values.
 */
@Stateless
public class AllowedPropertyValueDbFacade extends CdbEntityDbFacade<AllowedPropertyValue> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AllowedPropertyValueDbFacade() {
        super(AllowedPropertyValue.class);
    }

    public List<AllowedPropertyValue> findAllByPropertyTypeId(Integer propertyTypeId) {
        return (List<AllowedPropertyValue>) em.createNamedQuery("AllowedPropertyType.findAllByPropertyTypeId")
                .setParameter("propertyTypeId", propertyTypeId)
                .getResultList();
    }
}
