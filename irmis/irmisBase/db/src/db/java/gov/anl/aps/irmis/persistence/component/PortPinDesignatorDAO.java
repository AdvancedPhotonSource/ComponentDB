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
 * Methods for finding/saving IRMIS <code>PortPinDesignator</code>.
 */
public class PortPinDesignatorDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public PortPinDesignatorDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all port pin designators.
     * 
     * @return list of <code>PortPinDesignator</code> objects
     * @throws DAOException
     */
    public List findAllPortPinDesignators() throws DAOException {
        return retrieveObjs("from PortPinDesignator ppd",null,null);
    }

    /**
     * Find a particular port designator type by its parent ComponentPortType
     * id and designatorOrder.
     *
     * @param componentPortTypeId id of parent ComponentPortType object
     * @param designatorOrder integer designator order value
     * @return single object instance
     */
    public PortPinDesignator findPortPinDesignator(Long componentPortTypeId,
                                                   int designatorOrder) 
        throws DAOException {
        String query = "from PortPinDesignator ppd where " +
            "ppd.componentPortType.id = ? and " +
            "ppd.designatorOrder = ?";
        Object[]values = {componentPortTypeId, new Integer(designatorOrder)};
        Type[]types = {Hibernate.LONG, Hibernate.INTEGER};

        return (PortPinDesignator)retrieveObj(query, values, types);
                        
    }

    /**
     * Save a new instance of PortPinDesignator.
     */
    public void save(PortPinDesignator portPinDesignator) throws DAOException {
        storeObj(portPinDesignator);
    }

}
