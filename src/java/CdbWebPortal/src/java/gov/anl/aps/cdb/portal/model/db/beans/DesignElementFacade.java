/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class DesignElementFacade extends AbstractDbFacade<DesignElement> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DesignElementFacade() {
        super(DesignElement.class);
    }

    public DesignElement findByName(String name) {
        try {
            return (DesignElement) em.createNamedQuery("DesignElement.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public DesignElement findById(Integer id) {
        try {
            return (DesignElement) em.createNamedQuery("DesignElement.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public DesignElement findByNameAndParentDesign(String name, Design parentDesign) {
        try {
            return (DesignElement) em.createNamedQuery("DesignElement.findByNameAndParentDesign")
                    .setParameter("name", name)
                    .setParameter("parentDesign", parentDesign)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
}
