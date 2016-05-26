/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ResourceTypeCategory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class ResourceTypeCategoryFacade extends CdbEntityFacade<ResourceTypeCategory> {

    @PersistenceContext(unitName = "CdbWebPortal3PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ResourceTypeCategoryFacade() {
        super(ResourceTypeCategory.class);
    }
    
}
