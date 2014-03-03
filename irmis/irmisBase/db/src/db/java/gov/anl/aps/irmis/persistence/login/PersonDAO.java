/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.login;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;

import java.util.List;

/**
 * Find/save/delete <code>Person</code> objects in database.
 */
public class PersonDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public PersonDAO() throws DAOException {
        super();
    }

    /**
     * Find list of all known <code>Person</code> objects.
     *
     * @return list of <code>Person</code> objects, empty list if none.
     * @throws DAOException
     */
    public List findAllPersons() throws DAOException {
        String query = "from Person p order by p.lastName";
        return retrieveObjs(query, null, null);
    }

    /**
     * Find a particular Person by userid string.
     * 
     * @param userid string representing (typically) the unix userid
     * @return <code>Person</code> object, null if no match.
     * @throws DAOException
     */
    public Person findPerson(String userid) throws DAOException {
        String query = "from Person p where p.userid = ?";
        String[]values = {userid};
        Type[]types = {Hibernate.STRING};
        return (Person)retrieveObj(query,values,types);
    }

    /**
     * Find a Person as best we can by comparing the given name with
     * the last name field in the database.
     *
     * @param name free text name
     * @return <code>Person</code> object, null if no match.
     * @throws DAOException
     */
    public Person findByFreeTextName(String name) throws DAOException {
        String query = "from Person p where p.lastName = ?";
        String[]values = {name};
        Type[]types = {Hibernate.STRING};
        return (Person)retrieveObj(query,values,types);
    }

    public void save(Person person) throws DAOException {
        storeObj(person);
    }

}
