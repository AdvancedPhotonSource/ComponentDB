/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cms.portal.model.beans;

import gov.anl.aps.cms.portal.model.entities.UserUserGroup;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class UserUserGroupFacade extends AbstractFacade<UserUserGroup> {

    @PersistenceContext(unitName = "CmsWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserUserGroupFacade() {
        super(UserUserGroup.class);
    }

    public UserUserGroup findByUserIdAndUserGroupId(Integer userId, Integer userGroupId) {
        try {
            return (UserUserGroup) em.createNamedQuery("UserUserGroup.findByUserIdAndUserGroupId")
                    .setParameter("userId", userId)
                    .setParameter("userGroupId", userGroupId)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public UserUserGroup findByUserIdAndUserGroupName(Integer userId, String userGroupName) {
        try {
            return (UserUserGroup) em.createNamedQuery("UserUserGroup.findByUserIdAndUserGroupName")
                    .setParameter("userId", userId)
                    .setParameter("userGroupName", userGroupName)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public UserUserGroup findByUserUsernameAndUserGroupName(String userUsername, String userGroupName) {
        try {
            return (UserUserGroup) em.createNamedQuery("UserUserGroup.findByUserUsernameAndUserGroupName")
                    .setParameter("userUsername", userUsername)
                    .setParameter("userGroupName", userGroupName)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public boolean isUserMemberOfUserGroup(String username, String groupName) {
        return findByUserUsernameAndUserGroupName(username, groupName) != null;
    }
}
