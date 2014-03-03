/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Methods for explicitly finding IRMIS ioc resource records and instantiating
 * <code>IOCResource</code> objects from them.
 */
public class IOCResourceDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */    
    public IOCResourceDAO() throws DAOException {
        super();
    }

    /**
     * Find all <code>IOCResource</code> objects associated with the primary
     * key id of a given <code>IOCBoot</code> object.
     *
     * @param bootId primary key id from <code>IOCBoot<code> object
     * @return list of <code>IOCResource</code> objects
     * @throws DAOException
     */
    public List findByIOCBootId(Long bootId) throws DAOException {
        String query = "from IOCResource ir join fetch ir.uri where ir.iocBoot.id = ?";
        Long[]values = {bootId};
        Type[]types = {Hibernate.LONG};
        return retrieveObjs(query,values,types);
    }

}
