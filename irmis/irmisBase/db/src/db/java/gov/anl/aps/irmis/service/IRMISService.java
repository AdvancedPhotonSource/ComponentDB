/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.service;

// persistence layer
import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.DAOException;
import gov.anl.aps.irmis.persistence.BaseDAO;
//import gov.anl.aps.irmis.persistence.pv.RecordDAO;

import java.util.Collection;

/**
 * Base abstract class for all IRMIS transactional services. Common utility
 * methods are provided here.
 */
public abstract class IRMISService {

    /**
     * Clear all data objects from underlying data access layer object cache (if any).
     * This assumes the underlying DAO (Data Access Objects) layer makes use of a cache.
     * Otherwise, this method does nothing.
     *
     * @throws IRMISException
     */
    public static void clearCache() throws IRMISException {
        // doesn't matter which DAO we use here
        BaseDAO rDAO = null;
        try {
            // Clear the hibernate first-level cache of all previously retrieved objects.
            rDAO = new BaseDAO();
            rDAO.evictAll();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
    }
    
    /**
     * Clear given data object from underlying data access layer object cache (if any).
     * This assumes the underlying DAO (Data Access Objects) layer makes use of a cache.
     * Otherwise, this method does nothing.
     *
     * @param irmisDataObject a data object in persistent state (not new)
     * @throws IRMISException
     */
    public static void clearCache(IRMISDataObject irmisDataObject) throws IRMISException {
        if (irmisDataObject == null)
            return;
        // doesn't matter which DAO we use here
        BaseDAO rDAO = null;
        try {
            // Clear the hibernate first-level cache of previously retrieved objects.
            rDAO = new BaseDAO();
            rDAO.evict(irmisDataObject);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
    }

    /**
     * Clear given data object collection from underlying data access layer object cache.
     * This assumes the underlying DAO (Data Access Objects) layer makes use of a cache.
     * Otherwise, this method does nothing.
     *
     * @param irmisDataObjects a collection of data objects in persistent state (not new)
     * @throws IRMISException
     */
    public static void clearCache(Collection irmisDataObjects) throws IRMISException {
        if (irmisDataObjects == null || irmisDataObjects.size() == 0)
            return;
        // doesn't matter which DAO we use here
        BaseDAO rDAO = null;
        try {
            // Clear the hibernate first-level cache of previously retrieved objects.
            rDAO = new BaseDAO();
            rDAO.evict(irmisDataObjects);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
    }

    /**
     * Clear given data object from underlying data access layer object cache (if any).
     * This assumes the underlying DAO (Data Access Objects) layer makes use of a cache.
     * Otherwise, this method does nothing.
     *
     * @param irmisDataObject a data object in persistent state (not new)
     * @throws IRMISException
     */
    public static void refreshCache(IRMISDataObject irmisDataObject) throws IRMISException {

        // doesn't matter which DAO we use here
        BaseDAO rDAO = null;
        try {
            // Clear the hibernate first-level cache of previously retrieved objects.
            rDAO = new BaseDAO();
            rDAO.refresh(irmisDataObject);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
    }

    /**
     * Clear given data object collection from underlying data access layer object cache.
     * This assumes the underlying DAO (Data Access Objects) layer makes use of a cache.
     * Otherwise, this method does nothing.
     *
     * @param irmisDataObjects a collection of data objects in persistent state (not new)
     * @throws IRMISException
     */
    public static void refreshCache(Collection irmisDataObjects) throws IRMISException {

        // doesn't matter which DAO we use here
        BaseDAO rDAO = null;
        try {
            // Clear the hibernate first-level cache of previously retrieved objects.
            rDAO = new BaseDAO();
            rDAO.refresh(irmisDataObjects);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
    }

}
