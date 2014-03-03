/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.service.shared;

// java imports
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.PersonDAO;
import gov.anl.aps.irmis.persistence.audit.AuditAction;
import gov.anl.aps.irmis.persistence.audit.AuditActionDAO;
import gov.anl.aps.irmis.persistence.audit.AuditActionType;
import gov.anl.aps.irmis.persistence.DAOException;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Audit Service provides methods to insert and query audit action
 * objects. These objects are used to keep an audit trail of who does
 * what with IRMIS data.
 *
 * The methods here are themselves purely transactional.
 * In other words, you do not use new to make an instance of this class.
 *
 * An IRMISException is generally thrown when the supplied arguments are
 * not what is expected, or when there is some underlying database access
 * problem. 
 */
public class AuditService extends IRMISService {

    // DO NOT PUT ANY INSTANCE VARIABLES HERE

    /**
     * Hide constructor, since all methods are static.
     */
    private AuditService() {
    }

    /**
     * Find the list of all known audit actions in the database. 
     *
     * @return list of <code>AuditAction</code>objects
     * @throws IRMISException
     *
     * @see AuditAction
     */
    public static List findAuditActionList() throws IRMISException {
        AuditActionDAO aaDAO = null;
        List aaList = null;
        try {
            aaDAO = new AuditActionDAO();
            aaList = aaDAO.findAllAuditActions();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return aaList;
    }

    /**
     * Persist the given audit action to the database. The object should always be a new
     * object (id field not initialized). We do not allow updates to existing audit actions.
     *
     * @param auditAction new (transient) <code>AuditAction</code> object
     * @throws IRMISException
     */
    public static void saveAuditAction(AuditAction auditAction) throws IRMISException {
        AuditActionDAO aaDAO = null;
        if (auditAction.getId() != null)
            throw new IRMISException("AuditService.saveAuditAction(action) action is already persistent");

        try {
            aaDAO = new AuditActionDAO();
            aaDAO.save(auditAction);

            // don't keep around in cache
            aaDAO.evict(auditAction.getActionType());
            aaDAO.evict(auditAction);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }
    
    /**
     * Convenience method to create an audit action. User just supplies the integer code
     * from <code>AuditActionType</code>, the description, and any foreign key (if
     * applicable). The method takes care of filling in additional info. Assumes 
     * existence of other services.
     *
     * @param type integer code enumerated in <code>AuditActionType</code>
     * @param description free text description
     * @param userName person who performed action being audited
     * @param key a foreign key id if applicable
     *
     * @return an initialized <code>AuditAction</code>
     *
     * @throws IRMISException
     */
    public static AuditAction createAuditAction(int type, 
                                                String description, 
                                                String userName, 
                                                Long key) 
        throws IRMISException {
        AuditAction auditAction = new AuditAction();

        AuditActionType actionType = new AuditActionType();
        actionType.setId(new Long(type));

        Person person = PersonService.findPersonByUserName(userName);

        auditAction.setActionType(actionType);
        auditAction.setPerson(person);
        auditAction.setActionDescription(description);
        auditAction.setActionKey(key);
        
        return auditAction;
    }
}
