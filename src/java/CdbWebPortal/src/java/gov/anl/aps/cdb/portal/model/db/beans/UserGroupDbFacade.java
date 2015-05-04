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
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * DB facade for user groups.
 */
@Stateless
public class UserGroupDbFacade extends CdbEntityDbFacade<UserGroup> {

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
