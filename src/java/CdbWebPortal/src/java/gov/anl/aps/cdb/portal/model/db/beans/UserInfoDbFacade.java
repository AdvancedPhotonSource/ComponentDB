/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
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
 * DB facade for user info objects.
 */
@Stateless
public class UserInfoDbFacade extends CdbEntityDbFacade<UserInfo> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<UserInfo> findAll() {
        return (List<UserInfo>) em.createNamedQuery("UserInfo.findAll")
                .getResultList();
    } 
    
    public UserInfoDbFacade() {
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

    public UserInfo findById(Integer id) {
        try {
            return (UserInfo) em.createNamedQuery("UserInfo.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public boolean checkIfUsernameExists(String username) {
        return findByUsername(username) != null;
    }

    public boolean isUserMemberOfUserGroup(String username, String groupName) {
        UserInfo user = findByUsername(username);
        if (user == null) {
            return false;
        }
        for (UserGroup userGroup : user.getUserGroupList()) {
            if (userGroup.getName().equals(groupName)) {
                return true;
            }
        }
        return false;
    }
}
