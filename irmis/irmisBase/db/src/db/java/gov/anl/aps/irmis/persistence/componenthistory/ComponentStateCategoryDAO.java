/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.componenthistory;

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
 * Methods for explicitly finding IRMIS component categories and states and instantiating
 * <code>ComponentStateCategory</code> objects from them.
 */
public class ComponentStateCategoryDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentStateCategoryDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor.
     */
    public ComponentStateCategoryDAO(DAOContext dc) throws DAOException {
        super(dc);
    }


    /**
     * Find set of all component state categories.
     * 
     * @return list of <code>ComponentStateCategory</code> objects
     * @throws DAOException
     */
    public List findAll() throws DAOException {
        return retrieveObjs("from ComponentStateCategory csc order by csc.category",null,null);
    }

    /**
     * Find particular component state category by category name.
     * 
     * @param category string category name
     * @return <code>ComponentStateCategory</code> object
     * @throws DAOException
     */
    public ComponentStateCategory findByCategory(String category) throws DAOException {
        String query = "from ComponentStateCategory csc where csc.category = ?";
        String[] values = {category};
        Type[] types = {Hibernate.STRING};
        ComponentStateCategory csc = (ComponentStateCategory)retrieveObj(query, values, types);
        return csc;
    }

}
