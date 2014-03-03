/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOContext;
import gov.anl.aps.irmis.persistence.DAOException;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Methods for explicitly finding IRMIS ioc boot records and instantiating
 * <code>IOCBoot</code> objects from them.
 */
public class IOCBootDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public IOCBootDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor.
     */
    public IOCBootDAO(DAOContext dc) throws DAOException {
        super(dc);
    }

    /**
     * Find an individual <code>IOCBoot</code> by ioc name. Retrieves the record
     * marked with current_load=1.
     *
     * @param iocName string with ioc name
     * @return <code>IOCBoot</code> object
     * @throws DAOException
     */
    public IOCBoot findByIocName(String iocName) throws DAOException {
        String query = "from IOCBoot ib where ib.ioc.iocName = ? and ib.currentLoad = 1";
        String[] values = {iocName};
        Type[] types = {Hibernate.STRING};
        return (IOCBoot)retrieveObj(query,values,types);
    }

    /**
     * Find all <code>IOCBoot</code> over time for a given ioc name.
     *
     * @param iocName string with ioc name
     * @return list of <code>IOCBoot</code> objects
     * @throws DAOException
     */
    public List findAllByIocName(String iocName) throws DAOException {
        String query = "from IOCBoot ib where ib.ioc.iocName = ?";
        String[] values = {iocName};
        Type[] types = {Hibernate.STRING};
        return retrieveObjs(query,values,types);
    }

    /**
     * Find all the most recent <code>IOCBoot</code> that contain underlying
     * resource, record, and field data. This is the root of most IRMIS process
     * variable querying, since you must first determine the current set of
     * booted data. Lazy instantiation can be used from each <code>IOCBoot</code>
     * to get much of the underlying data without further explicit queries.
     *
     * @return list of <code>IOCBoot</code> objects ordered by ioc name
     * @throws DAOException
     */
    public List findCurrentLoads() throws DAOException {

        String query = "from IOCBoot ib " +
            "join fetch ib.ioc " +
            "where ib.currentLoad = 1 order by ib.ioc.iocName";

        List results = retrieveObjs(query, null, null);

        // pull in needed data into object graph
        String query2 = "from IOCBoot ib join fetch ib.iocResources ir "+
            "join fetch ir.uri " +
            "where ib.currentLoad = 1";
        retrieveObjs(query2, null, null); 

        return results;
    }

}
