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
 * Methods for explicitly finding IRMIS record client records and instantiating
 * <code>RecordClient</code> objects from them.
 */
public class RecordClientDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public RecordClientDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor.
     */
    public RecordClientDAO(DAOContext dc) throws DAOException {
        super(dc);
    }

    /**
     * Find list of <code>RecordClient</code> by record name. Retrieves the records
     * marked with current_load=1.
     *
     * @param recordName string with record name
     * @return List of <code>RecordClient</code> objects
     * @throws DAOException
     */
    public List findByRecordName(String recordName) throws DAOException {

        String query = "from RecordClient rc join fetch rc.vuri.uri where rc.recordName = ? and rc.currentLoad = 1";
        String[] values = {recordName};
        Type[] types = {Hibernate.STRING};
        return retrieveObjs(query,values,types);
    }

}
