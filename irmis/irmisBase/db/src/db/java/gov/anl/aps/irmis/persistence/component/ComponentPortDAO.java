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
 * Methods for finding/saving IRMIS <code>ComponentPort</code>.
 */
public class ComponentPortDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentPortDAO() throws DAOException {
        super();
    }

    public void save(ComponentPort port) throws DAOException {
        storeObj(port);
    }

}
