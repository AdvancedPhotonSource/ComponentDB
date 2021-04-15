/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class UserGroupFacade extends CdbEntityFacade<UserGroup> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserGroupFacade() {
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
    
    @Override
    public List<UserGroup> findAll() {
        return (List<UserGroup>) em.createNamedQuery("UserGroup.findAll")
                .getResultList();
    }
    
    public List<UserGroup> findUserGroupsWithSettings() {
        try {
            return (List<UserGroup>) em.createNamedQuery("UserGroup.findGroupsWithSettings")
                    .getResultList();
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

    /**
     * Find unique entity by name.  Returns null if none is found, or raises
     * CdbException if multiple instances are found.
     */
    public UserGroup findUniqueByName(String name, String filterDomainName) throws CdbException {
        
        if ((name == null) || (name.isEmpty())) {
            return null;
        }
        
        return findByName(name);       
    }
    
    public static UserGroupFacade getInstance() {
        return (UserGroupFacade) SessionUtility.findFacade(UserGroupFacade.class.getSimpleName()); 
    }
    
}
