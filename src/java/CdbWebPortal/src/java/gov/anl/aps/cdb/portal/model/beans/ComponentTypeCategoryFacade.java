
package gov.anl.aps.cdb.portal.model.beans;

import gov.anl.aps.cdb.portal.model.entities.ComponentTypeCategory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class ComponentTypeCategoryFacade extends AbstractFacade<ComponentTypeCategory> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentTypeCategoryFacade() {
        super(ComponentTypeCategory.class);
    }

    public ComponentTypeCategory findByName(String name) {
        try {
            return (ComponentTypeCategory) em.createNamedQuery("ComponentTypeCategory.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } 
        catch (NoResultException ex) {
        }
        return null;
    }

    public ComponentTypeCategory findById(Integer id) {
        try {
            return (ComponentTypeCategory) em.createNamedQuery("ComponentTypeCategory.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } 
        catch (NoResultException ex) {
        }
        return null;
    }

}
