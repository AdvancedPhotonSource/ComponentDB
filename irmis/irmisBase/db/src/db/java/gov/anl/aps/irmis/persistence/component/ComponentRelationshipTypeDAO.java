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
 * Methods for finding/saving IRMIS <code>ComponentRelationshipType</code>.
 */
public class ComponentRelationshipTypeDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentRelationshipTypeDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all component relationship types.
     * 
     * @return list of <code>ComponentRelationshipType</code> objects
     * @throws DAOException
     */
    public List findAllComponentRelationshipTypes() throws DAOException {
        return retrieveObjs("from ComponentRelationshipType crt",null,null);
    }

    /**
     * Find a particular component relationship type by name.
     *
     * @param typeName string component relationship type name
     * @return single object instance
     */
    public ComponentRelationshipType findRelationshipType(String typeName) 
        throws DAOException {
        String query = "from ComponentRelationshipType crt where crt.relationshipType = ?";
        String[]values = {typeName};
        Type[]types = {Hibernate.STRING};

        return (ComponentRelationshipType)retrieveObj(query, values, types);
    }

    /**
     * Find a particular component relationship type by integer hierarchy number.
     *
     * @param hierarchy integer statically defined in <code>ComponentRelationshipType</code>
     * @return single object instance
     */
    public ComponentRelationshipType findRelationshipType(int hierarchy) 
        throws DAOException {
        switch (hierarchy) {
        case ComponentRelationshipType.CONTROL: {
            return findRelationshipType("control");
        }
        case ComponentRelationshipType.HOUSING: {
            return findRelationshipType("housing");
        }
        case ComponentRelationshipType.POWER: {
            return findRelationshipType("power");
        }
        default: {
            return null;
        }
        }
    }

    public void save(ComponentRelationshipType compRelType) throws DAOException {
        storeObj(compRelType);
    }

}
