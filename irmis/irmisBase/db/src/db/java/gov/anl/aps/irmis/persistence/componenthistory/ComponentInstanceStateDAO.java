/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.componenthistory;


import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOContext;
import gov.anl.aps.irmis.persistence.DAOException;

import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstance;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Methods for explicitly finding IRMIS component instance state records and instantiating
 * <code>ComponentInstanceState</code> objects from them.
 */
public class ComponentInstanceStateDAO extends BaseDAO {


    /**
     * Do nothing constructor.
     */
    public ComponentInstanceStateDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor.
     */
    public ComponentInstanceStateDAO(DAOContext dc) throws DAOException {
        super(dc);
    }

    /**
     * Find all <code>ComponentInstanceState</code> objects for given 
     * <code>ComponentInstance</code>.
     *
     * @parm ci component instance
     * @return list of <code>ComponentInstanceState</code> objects
     * @throws DAOException
     */
    public List findStateList(ComponentInstance ci) throws DAOException {
        String query = "from ComponentInstanceState cis where cis.componentInstance = ?";

        Object[] values = {ci};
        Type[] types = {Hibernate.entity(ComponentInstance.class)};
        return retrieveObjs(query, values, types);

    }

}
