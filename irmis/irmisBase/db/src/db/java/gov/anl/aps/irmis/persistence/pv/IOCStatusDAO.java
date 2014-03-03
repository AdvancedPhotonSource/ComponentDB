/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;

import java.util.List;

/**
 * Methods for finding/saving IRMIS <code>IOCStatus</code>.
 */
public class IOCStatusDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public IOCStatusDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all ioc status.
     * 
     * @return list of <code>IOCStatus</code> objects
     * @throws DAOException
     */
    public List findAllIOCStatus() throws DAOException {
        return retrieveObjs("from IOCStatus istat",null,null);
    }

    /**
     * Find a particular ioc status by name.
     *
     * @param statusName string status name
     * @return single object instance
     */
    public IOCStatus findIOCStatus(String statusName) 
        throws DAOException {
        String query = "from IOCStatus is where is.name = ?";
        String[]values = {statusName};
        Type[]types = {Hibernate.STRING};

        return (IOCStatus)retrieveObj(query, values, types);
    }
}
