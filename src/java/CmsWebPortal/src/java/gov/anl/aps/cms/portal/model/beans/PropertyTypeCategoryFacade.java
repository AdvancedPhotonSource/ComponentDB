/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.beans;

import gov.anl.aps.cms.portal.model.entities.PropertyTypeCategory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class PropertyTypeCategoryFacade extends AbstractFacade<PropertyTypeCategory> {
    @PersistenceContext(unitName = "CmsWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropertyTypeCategoryFacade() {
        super(PropertyTypeCategory.class);
    }

        public PropertyTypeCategory findByName(String name) {
        try {
            return (PropertyTypeCategory)em.createNamedQuery("PropertyTypeCategory.findByName")
                .setParameter("name", name)
                .getSingleResult();
        }
        catch (NoResultException ex) {
        }
        return null;
    }    
}
