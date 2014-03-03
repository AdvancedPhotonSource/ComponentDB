/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.audit;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;

import java.util.List;

/**
 * Find/save <code>AuditAction</code> objects in database.
 */
public class AuditActionDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public AuditActionDAO() throws DAOException {
        super();
    }

    /**
     * Find list of all known <code>AuditAction</code> objects.
     *
     * @return list of <code>AuditAction</code> objects, empty list if none.
     * @throws DAOException
     */
    public List findAllAuditActions() throws DAOException {
        String query = "from AuditAction aa";
        return retrieveObjs(query, null, null);
    }

    public void save(AuditAction auditAction) throws DAOException {
        storeObj(auditAction);
    }
}
