/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.beans;

import gov.anl.aps.cdb.portal.model.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.entities.UserInfo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class UserInfoFacade extends AbstractFacade<UserInfo>
{
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
            return (UserInfo)em.createNamedQuery("UserInfo.findByUsername")
                .setParameter("username", username)
                .getSingleResult();
        }
        catch (NoResultException ex) {
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
