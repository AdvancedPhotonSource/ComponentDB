/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.login;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;

import java.util.List;

/**
 * Find/save/delete <code>GroupName</code> objects in database.
 */
public class GroupNameDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public GroupNameDAO() throws DAOException {
        super();
    }

    /**
     * Find list of all known <code>GroupName</code>.
     *
     * @return list of <code>GroupName</code>, empty list if none.
     * @throws DAOException
     * @see GroupName
     */
    public List findAllGroupNames() throws DAOException {
        String query = "from GroupName gn order by gn.groupName";
        return retrieveObjs(query, null, null);
    }

    /**
     * Find a particular <code>GroupName</code> by text name.
     * 
     * @param groupName string representing the group name
     * @return <code>GroupName</code> object, null if no match.
     * @throws DAOException
     * @see GroupName
     */
    public GroupName findGroupName(String groupName) throws DAOException {
        String query = "from GroupName gn where gn.groupName = ?";
        String[]values = {groupName};
        Type[]types = {Hibernate.STRING};
        return (GroupName)retrieveObj(query,values,types);
    }

    public void save(GroupName groupName) throws DAOException {
        storeObj(groupName);
    }

}
