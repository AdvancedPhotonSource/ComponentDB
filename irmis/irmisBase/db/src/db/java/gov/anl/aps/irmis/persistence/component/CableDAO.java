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
 * Methods for finding/saving IRMIS <code>Cable</code>.
 */
public class CableDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public CableDAO() throws DAOException {
        super();
    }


    /**
     * Find all cables attached to a given port.
     */
    public List findCablesByPort(ComponentPort port) throws DAOException {
        String query = "from Cable c where c.markForDelete = 0 and (c.componentPortA = ? or c.componentPortB = ?)";
        Object[]values = {port, port};
        Type[]types = {Hibernate.entity(ComponentPort.class),Hibernate.entity(ComponentPort.class)};
        return retrieveObjs(query, values, types);
    }

    /**
     * Find all cables that contain given text in label column. Wildcard is implied
     * at beginning and end of given string.
     */
    public List findCablesByLabel(String labelText) throws DAOException {
        labelText = "%"+labelText+"%";
        String query = "from Cable c where c.markForDelete = 0 and c.label like ?";
        Object[]values = {labelText};
        Type[]types = {Hibernate.STRING};
        return retrieveObjs(query, values, types);
    }


    public void save(Cable cable) throws DAOException {
        storeObj(cable);
    }

    public void delete(Cable cable) throws DAOException {
        removeObj(cable);
    }

}
