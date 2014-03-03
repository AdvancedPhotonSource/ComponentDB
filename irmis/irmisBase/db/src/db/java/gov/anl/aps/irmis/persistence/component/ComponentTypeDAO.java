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
 * Methods for finding/saving IRMIS <code>ComponentType</code>.
 */
public class ComponentTypeDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentTypeDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all component types.
     * 
     * @return list of <code>ComponentType</code> objects
     * @throws DAOException
     */
    public List findAllComponentTypes() throws DAOException {
        String query = "from ComponentType ct join fetch ct.componentTypeStatus "+
            "where ct.markForDelete = 0 "+
            "order by ct.componentTypeName";
        List results = retrieveObjs(query,null,null);

        results.size();
        // explicitly pull in interface collections into object graph
        String query2 = "from ComponentType ct join fetch ct.componentTypeInterfaces cti "+
            "join fetch cti.interfaceType";
        retrieveObjs(query2, null, null);

        return results;
    }

    /**
     * Find a particular component type by name.
     *
     * @param typeName string component type name
     * @return single object instance
     */
    public ComponentType findComponentType(String typeName) throws DAOException {
        String query = "from ComponentType ct join fetch ct.componentTypeStatus "+
            "where ct.componentTypeName = ? and ct.markForDelete = 0";
        String[]values = {typeName};
        Type[]types = {Hibernate.STRING};

        return (ComponentType)retrieveObj(query, values, types);
    }

    /**
     * Find set of all BeamlineInterest.
     *
     * @return list of <code>BeamlineInterest</code> objects
     * @throws DAOException
     */
    public List findAllBeamlineInterest() throws DAOException {
        String query = "from BeamlineInterest bi";
        List results = retrieveObjs(query, null, null);
        return results;
    }

    public void save(ComponentType compType) throws DAOException {
        storeObj(compType);
    }

}
