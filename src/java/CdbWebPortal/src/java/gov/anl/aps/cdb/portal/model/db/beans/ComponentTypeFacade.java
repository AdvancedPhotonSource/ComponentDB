
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ComponentType;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class ComponentTypeFacade extends AbstractFacade<ComponentType>
{
    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentTypeFacade() {
        super(ComponentType.class);
    }
    
    public ComponentType findByName(String name) {
        try {
            return (ComponentType) em.createNamedQuery("ComponentType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        }
        catch (NoResultException ex) {
        }
        return null;
    }

    public ComponentType findById(Integer id) {
        try {
            return (ComponentType) em.createNamedQuery("ComponentType.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        }
        catch (NoResultException ex) {
        }
        return null;
    }
    
}
