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
 * Methods for finding/saving IRMIS <code>ComponentTypeInterfaceType</code>.
 */
public class ComponentTypeInterfaceTypeDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentTypeInterfaceTypeDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all component types interface types, based on the
     * relationship type.
     * 
     * @param relType - the interface types for this relationship type only
     *
     * @return list of <code>ComponentTypeInterfaceType</code> objects
     * @throws DAOException
     */
    public List findAllInterfaceTypes(ComponentRelationshipType relType) throws DAOException {
        String query = "from ComponentTypeInterfaceType ctit " +
            "where ctit.relationshipType = ? " +
            "order by ctit.interfaceType";
        Object[]values = {relType};
        Type[]types = {Hibernate.entity(ComponentRelationshipType.class)};
        return retrieveObjs(query,values,types);
    }

    /**
     * Find a particular component type interface type by name, based on the
     * given relationship type.
     *
     * @param relType - the interface type for this relationship type only
     * @param typeName string interface type name
     * @return single object instance
     */
    public ComponentTypeInterfaceType findInterfaceType(ComponentRelationshipType relType,
                                                        String typeName) 
        throws DAOException {
        String query = "from ComponentTypeInterfaceType ctit " +
            "where ctit.interfaceType = ? and ctit.relationshipType = ?";
        Object[]values = {typeName, relType};
        Type[]types = {Hibernate.STRING, Hibernate.entity(ComponentRelationshipType.class)};

        return (ComponentTypeInterfaceType)retrieveObj(query, values, types);
                        
    }

    /**
     * Save a new instance of ComponentTypeInterfaceType.
     */
    public void save(ComponentTypeInterfaceType compTypeIntType) throws DAOException {
        storeObj(compTypeIntType);
    }

}
