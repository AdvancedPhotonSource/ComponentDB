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
import gov.anl.aps.irmis.persistence.login.RoleName;
import gov.anl.aps.irmis.persistence.login.RoleNameDAO;
import gov.anl.aps.irmis.persistence.login.GroupName;
import gov.anl.aps.irmis.persistence.login.GroupNameDAO;
import gov.anl.aps.irmis.persistence.DAOException;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Person Service provides a variety of methods to acquire and
 * manipulate the data objects representing persons and roles within
 * IRMIS. These objects and methods should be used to manage references
 * to facility staff.
 *
 * The methods here are themselves purely transactional, 
 * although some take existing object graphs as arguments.
 * In other words, you do not use new to make an instance of this class.
 *
 * An IRMISException is generally thrown when the supplied arguments are
 * not what is expected, or when there is some underlying database access
 * problem. 
 */
public class PersonService extends IRMISService {

    // DO NOT PUT ANY INSTANCE VARIABLES HERE

    /**
     * Hide constructor, since all methods are static.
     */
    private PersonService() {
    }

    /**
     * Find the list of all known persons in the database. From this,
     * the set of roles a person holds can also be found.
     *
     * @return list of <code>Person</code>objects
     * @throws IRMISException
     *
     * @see Person
     */
    public static List findPersonList() throws IRMISException {
        PersonDAO pDAO = null;
        List pList = null;
        try {
            pDAO = new PersonDAO();
            pList = pDAO.findAllPersons();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return pList;
    }

    /**
     * Find a single person by their userName, which is assumed to be unique.
     *
     * @param userName login name typically
     * @return the matching <code>Person</code> or null
     *
     * @throws IRMISException
     */
    public static Person findPersonByUserName(String userName) throws IRMISException {
        PersonDAO pDAO = null;
        Person person = null;
        try {
            pDAO = new PersonDAO();
            person = pDAO.findPerson(userName);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return person;
    }

    /**
     * Find the list of all known role names in the database. 
     *
     * @return list of <code>RoleName</code>objects
     * @throws IRMISException
     *
     * @see RoleName
     */
    public static List findRoleNameList() throws IRMISException {
        RoleNameDAO rnDAO = null;
        List rnList = null;
        try {
            rnDAO = new RoleNameDAO();
            rnList = rnDAO.findAllRoleNames();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return rnList;
    }

    /**
     * Find a <code>RoleName</code> object for the given roleName string.
     *
     * @param roleName text string representing the name of a role, such as "admin"
     * @return a <code>RoleName</code> object, or null if not found
     * @throws IRMISException
     */
    public static RoleName findRoleName(String roleName) throws IRMISException {
        RoleNameDAO rnDAO = null;
        RoleName rn = null;
        try {
            rnDAO = new RoleNameDAO();
            rn = rnDAO.findRoleName(roleName);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return rn;
    }

    /**
     * Find the list of all known group names in the database. 
     *
     * @return list of <code>GroupName</code>objects
     * @throws IRMISException
     *
     * @see GroupName
     */
    public static List findGroupNameList() throws IRMISException {
        GroupNameDAO gnDAO = null;
        List gnList = null;
        try {
            gnDAO = new GroupNameDAO();
            gnList = gnDAO.findAllGroupNames();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return gnList;
    }

    /**
     * Persist the given person to the database. If it is a completely new object
     * in a transient state (the id field is not initialized), a new row will be inserted
     * in the database, otherwise an update will occur. An exception will be thrown if 
     * the minimum of data is not provided. 
     *
     * @param person new (transient) or existing (persistent) <code>Person</code> object
     * @throws IRMISException
     */
    public static void savePerson(Person person) throws IRMISException {
        PersonDAO pDAO = null;
        try {
            pDAO = new PersonDAO();
            pDAO.save(person);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

    /**
     * Persist the given group name to the database. If it is a completely new object
     * in a transient state (the id field is not initialized), a new row will be inserted
     * in the database, otherwise an update will occur. An exception will be thrown if 
     * the minimum of data is not provided. 
     *
     * @param groupName new (transient) or existing (persistent) <code>GroupName</code> object
     * @throws IRMISException
     */
    public static void saveGroupName(GroupName groupName) throws IRMISException {
        GroupNameDAO gnDAO = null;
        try {
            gnDAO = new GroupNameDAO();
            gnDAO.save(groupName);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

}
