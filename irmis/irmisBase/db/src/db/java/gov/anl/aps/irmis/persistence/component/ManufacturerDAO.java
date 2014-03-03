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
 * Methods for finding/saving IRMIS <code>Manufacturer</code>.
 */
public class ManufacturerDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ManufacturerDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all manufacturers.
     * 
     * @return list of <code>Manufacturer</code> objects
     * @throws DAOException
     */
    public List findAllManufacturers() throws DAOException {
        return retrieveObjs("from Manufacturer m order by m.manufacturerName",null,null);
    }

    /**
     * Find a particular Manufacturer by name.
     *
     * @param typeName string manufacturer name
     * @return single object instance
     */
    public Manufacturer findManufacturer(String mfgName) 
        throws DAOException {
        String query = "from Manufacturer m where m.manufacturerName = ?";
        String[]values = {mfgName};
        Type[]types = {Hibernate.STRING};

        return (Manufacturer)retrieveObj(query, values, types);
                        
    }

    /**
     * Save a new instance of Manufacturer.
     */
    public void save(Manufacturer mfg) throws DAOException {
        storeObj(mfg);
    }

}
