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
 * Methods for finding/saving IRMIS <code>FunctionName</code>.
 */
public class FunctionDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public FunctionDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all functions.
     * 
     * @return list of <code>Function</code> objects
     * @throws DAOException
     */
    public List findAllFunctions() throws DAOException {
        return retrieveObjs("from Function f order by f.functionName",null,null);
    }

    /**
     * Find a particular function by name.
     *
     * @param typeName string function name
     * @return single object instance
     */
    public Function findFunction(String functionName) 
        throws DAOException {
        String query = "from Function f where f.functionName = ?";
        String[]values = {functionName};
        Type[]types = {Hibernate.STRING};

        return (Function)retrieveObj(query, values, types);
                        
    }

    /**
     * Save a new instance of Function.
     */
    public void save(Function function) throws DAOException {
        storeObj(function);
    }

}
