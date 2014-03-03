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
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Methods for explicitly finding all IRMIS iocs and instantiating
 * <code>IOC</code> objects from them.
 */
public class IOCDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public IOCDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor.
     */
    public IOCDAO(DAOContext dc) throws DAOException {
        super(dc);
    }

    /**
     * Find set of all ioc's from ioc table.
     * 
     * @return list of <code>IOC</code> objects
     * @throws DAOException
     */
    public List findIOCs() throws DAOException {
        return retrieveObjs("from IOC i order by i.iocName",null,null);
    }

    /**
     * Find set of all ioc's from ioc table which are part of given system.
     * 
     * @param system string giving system name, such as "PAR", or "Booster"
     * @return list of <code>IOC</code> objects
     * @throws DAOException
     */
    public List findIOCsBySystem(String system) throws DAOException {
        String query = "from IOC i where i.system = ? order by i.iocName";
        String[]values = {system};
        Type[]types = {Hibernate.STRING};

        return retrieveObjs(query, values, types);
    }

    /**
     * Find set of all unique ioc "system" names.
     * @return list of strings
     * @throws DAOException
     */
    public List findSystems() throws DAOException {
        String query = "select distinct i.system from IOC i";
        List objArrays = retrieveObjs(query, null, null);
        Iterator it = objArrays.iterator();
        List systemList = new ArrayList();
        while (it.hasNext()) {
            Object row = (Object)it.next();
            systemList.add((String)row);
        }
        return systemList;
    }

    public void save(IOC ioc) throws DAOException {
        storeObj(ioc);
    }

}
