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
 * Find/save/delete <code>RoleName</code> objects in database.
 */
public class RoleNameDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public RoleNameDAO() throws DAOException {
        super();
    }

    /**
     * Find list of all known <code>RoleName</code>.
     *
     * @return list of <code>RoleName</code>, empty list if none.
     * @throws DAOException
     * @see RoleName
     */
    public List findAllRoleNames() throws DAOException {
        String query = "from RoleName rn order by rn.roleName";
        return retrieveObjs(query, null, null);
    }

    /**
     * Find a particular <code>RoleName</code> by text name.
     * 
     * @param roleName string representing the role name
     * @return <code>RoleName</code> object, null if no match.
     * @throws DAOException
     * @see RoleName
     */
    public RoleName findRoleName(String roleName) throws DAOException {
        String query = "from RoleName rn where rn.roleName = ?";
        String[]values = {roleName};
        Type[]types = {Hibernate.STRING};
        return (RoleName)retrieveObj(query,values,types);
    }

}
