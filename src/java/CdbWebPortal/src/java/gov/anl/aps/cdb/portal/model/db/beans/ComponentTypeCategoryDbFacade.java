
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ComponentTypeCategory;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class ComponentTypeCategoryDbFacade extends CdbEntityDbFacade<ComponentTypeCategory> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentTypeCategoryDbFacade() {
        super(ComponentTypeCategory.class);
    }

    @Override
    public List<ComponentTypeCategory> findAll() {
        return (List<ComponentTypeCategory>) em.createNamedQuery("ComponentTypeCategory.findAll")
                .getResultList();
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
