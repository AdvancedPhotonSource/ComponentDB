/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class AllowedPropertyValueFacade extends AbstractFacade<AllowedPropertyValue>
{
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
        return (List<AllowedPropertyValue>)em.createNamedQuery("AllowedPropertyType.findAllByPropertyTypeId")
                .setParameter("propertyTypeId", propertyTypeId)
                .getResultList();
    }    
}
