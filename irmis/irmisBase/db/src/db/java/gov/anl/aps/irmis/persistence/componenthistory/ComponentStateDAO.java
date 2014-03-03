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
 * Methods for explicitly finding IRMIS component states and instantiating
 * <code>ComponentState</code> objects from them.
 */
public class ComponentStateDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentStateDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor.
     */
    public ComponentStateDAO(DAOContext dc) throws DAOException {
        super(dc);
    }


    /**
     * Find set of all component state.
     * 
     * @return list of <code>ComponentState</code> objects
     * @throws DAOException
     */
    public List findAll() throws DAOException {
        return retrieveObjs("from ComponentState cs",null,null);
    }

    /**
     * Find particular set of component states for the given category.
     * 
     * @param category a <code>ComponentStateCategory</code> object
     * @return list of <code>ComponentState</code> objects
     * @throws DAOException
     */
    public List findByCategory(ComponentStateCategory category) throws DAOException {
        String query = "from ComponentState cs where cs.componentStateCategory = ?";
        Object[] values = {category};
        Type[] types = {Hibernate.entity(ComponentStateCategory.class)};
        return retrieveObjs(query, values, types);
    }

}
