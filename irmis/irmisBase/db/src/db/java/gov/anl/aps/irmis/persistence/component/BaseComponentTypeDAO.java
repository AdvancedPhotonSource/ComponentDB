/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;

import java.util.List;

/**
 * Methods for finding/saving IRMIS <code>BaseComponentType</code>.
 */
public class BaseComponentTypeDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public BaseComponentTypeDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all base component types.
     * 
     * @return list of <code>BaseComponentType</code> objects
     * @throws DAOException
     */
    public List findAllComponentTypes() throws DAOException {
        return retrieveObjs("from BaseComponentType ct",null,null);
    }

    /**
     * Find a particular base component type by name.
     *
     * @param typeName string component type name
     * @return single object instance
     */
    public BaseComponentType findComponentType(String typeName) throws DAOException {
        String query = "from BaseComponentType ct where ct.componentTypeName = ?";
        String[]values = {typeName};
        Type[]types = {Hibernate.STRING};

        return (BaseComponentType)retrieveObj(query, values, types);
    }

    public void save(BaseComponentType compType) throws DAOException {
        storeObj(compType);
    }

}
