/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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
public class UserGroupDbFacade extends CdbEntityDbFacade<UserGroup> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<UserGroup> findAll() {
        return (List<UserGroup>) em.createNamedQuery("UserGroup.findAll")
                .getResultList();
    }

    public UserGroupDbFacade() {
        super(UserGroup.class);
    }

    public UserGroup findById(Integer id) {
        try {
            return (UserGroup) em.createNamedQuery("UserGroup.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public UserGroup findByName(String name) {
        try {
            return (UserGroup) em.createNamedQuery("UserGroup.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

}
