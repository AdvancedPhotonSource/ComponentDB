/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOContext;
import gov.anl.aps.irmis.persistence.DAOException;

import java.util.List;

/**
 * Methods for explicitly finding all APS extended ioc info and instantiating
 * <code>APSIOC</code> objects from them.
 */
public class APSIOCDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public APSIOCDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor.
     */
    public APSIOCDAO(DAOContext dc) throws DAOException {
        super(dc);
    }

    /**
     * Find a particular aps ioc object by ioc name.
     * 
     * @param iocName
     * @return <code>APSIOC</code> object
     * @throws DAOException
     */
    public APSIOC findByName(String iocName) throws DAOException {
        String query = "from APSIOC ai where ai.ioc.iocName = ?";
        String[]values = {iocName};
        Type[]types = {Hibernate.STRING};
        return (APSIOC)retrieveObj(query,values,types);
    }

    /**
     * Find a particular aps ioc object by ioc.
     * 
     * @param ioc  - a <code>IOC</code> object
     * @return <code>APSIOC</code> object
     * @throws DAOException
     */
    public APSIOC findByIoc(IOC ioc) throws DAOException {
        String query = "from APSIOC ai where ai.ioc = ?";
        Object[]values = {ioc};
        Type[]types = {Hibernate.entity(IOC.class)};
        return (APSIOC)retrieveObj(query,values,types);
    }

    public void save(APSIOC ioc) throws DAOException {
        storeObj(ioc);
    }

}
