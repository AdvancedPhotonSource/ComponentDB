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
 * Methods for finding/saving IRMIS <code>PortPinType</code>.
 */
public class PortPinTypeDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public PortPinTypeDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all port pin types.
     * 
     * @return list of <code>PortPinType</code> objects
     * @throws DAOException
     */
    public List findAllPortPinTypes() throws DAOException {
        return retrieveObjs("from PortPinType ppt",null,null);
    }

    /**
     * Find a particular port pin type by name.
     *
     * @param typeName string function name
     * @return single object instance
     */
    public PortPinType findPortPinType(String portPinName) 
        throws DAOException {
        String query = "from PortPinType ppt where ppt.portPinType = ?";
        String[]values = {portPinName};
        Type[]types = {Hibernate.STRING};

        return (PortPinType)retrieveObj(query, values, types);
                        
    }

    /**
     * Save a new instance of PortPinType.
     */
    public void save(PortPinType portPinType) throws DAOException {
        storeObj(portPinType);
    }

}
