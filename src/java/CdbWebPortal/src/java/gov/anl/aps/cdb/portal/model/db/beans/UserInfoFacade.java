/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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
public class UserInfoFacade extends CdbEntityFacade<UserInfo> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserInfoFacade() {
        super(UserInfo.class);
    }
    
    public UserInfo findByUsername(String username) {
        try {
            return (UserInfo) em.createNamedQuery("UserInfo.findByUsername")
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    /**
     * Find unique entity by name.  Returns null if none is found, or raises
     * CdbException if multiple instances are found.
     */
    public UserInfo findUniqueByName(String name, String filterDomainName) throws CdbException {
        
        if ((name == null) || (name.isEmpty())) {
            return null;
        }
        
        // uses findByName() since that method already finds a unique instance by name
        return findByUsername(name);       
    }
    
    public UserInfo findById(Integer id) {
        try {
            return (UserInfo) em.createNamedQuery("UserInfo.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    @Override
    public List<UserInfo> findAll() {
        return (List<UserInfo>) em.createNamedQuery("UserInfo.findAll")
                .getResultList();
    }

    public boolean checkIfUsernameExists(String username) {
        return findByUsername(username) != null;
    }   
    
    public static UserInfoFacade getInstance() {
        return (UserInfoFacade) SessionUtility.findFacade(UserInfoFacade.class.getSimpleName()); 
    }
    
}
