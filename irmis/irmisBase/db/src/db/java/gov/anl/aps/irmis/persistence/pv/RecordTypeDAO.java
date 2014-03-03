/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOContext;
import gov.anl.aps.irmis.persistence.DAOException;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

/**
 * Methods for explicitly finding IRMIS process variable records types and
 * instantiating <code>RecordType</code> objects from them. 
 */        
public class RecordTypeDAO extends BaseDAO {

    /**
     * Do nothing constructor
     */
    public RecordTypeDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor
     */
    public RecordTypeDAO(DAOContext dc) throws DAOException {
        super(dc);
    }

    /**
     * Find single <code>RecordType</code> for given primary key id.
     *
     * @param recTypeId primary key id for a given record type
     * @throws DAOException
     */
    public RecordType findByRecordTypeId(Long recTypeId) throws DAOException {
        String query = "from RecordType rt join fetch rt.fieldTypes where rt.id = ?";
        Long[]values = {recTypeId};
        Type[]types = {Hibernate.LONG};
        return (RecordType)retrieveObj(query,values,types);
    }

    /**
     * Find list of all known unique RecordTypes.
     * This is not all RecordType objects for every
     * current IOCBoot, but rather all unique RecordType where unique is defined
     * as having different getRecordType() (ie. ai, ao, ...).
     */
    public List findAllRecordTypes() throws DAOException {
        String query = "from RecordType rt where rt.iocBoot.currentLoad = 1";
        List result = retrieveObjs(query,null,null);
        HashMap uniqueSet = new HashMap();
        Iterator resultIt = result.iterator();
        // find unique ones by repeatedly adding to hash set
        while (resultIt.hasNext()) {
            RecordType rt = (RecordType)resultIt.next();
            uniqueSet.put(rt.getRecordType(),rt);
        }

        // iterate over hash map values, converting to regular array list
        Iterator uniqueIt = uniqueSet.values().iterator();
        List returnList = new ArrayList();
        while (uniqueIt.hasNext()) {
            RecordType rt = (RecordType)uniqueIt.next();
            returnList.add(rt);
        }
        Collections.sort(returnList);
        return returnList;
    }

}
