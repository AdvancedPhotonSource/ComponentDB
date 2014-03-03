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
 * Methods for finding/saving IRMIS <code>ComponentRelationship</code>.
 */
public class ComponentRelationshipDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentRelationshipDAO() throws DAOException {
        super();
    }

    /**
     * Find list of all component relationships.
     * 
     * @return list of <code>ComponentRelationship</code> objects
     * @throws DAOException
     */
    public List findAllComponentRelationships() throws DAOException {
        return retrieveObjs("from ComponentRelationship cr",null,null);
    }

    /**
     * Find all component relationships who have the given
     * component as their child, and the given relationship type.
     *
     * @param 
     * @return list of <code>Component</code> objects, empty if none
     */
    /*
    public List findComponentsBySerialNumber(String serialNumber) throws DAOException {
        String query = "from Component c where c.serialNumber = ?";
        String[]values = {serialNumber};
        Type[]types = {Hibernate.STRING};

        return retrieveObjs(query, values, types);
    }
    */

    public void save(ComponentRelationship compRel) throws DAOException {
        storeObj(compRel);
    }

}
