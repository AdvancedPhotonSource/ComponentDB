/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.type.Type;
import org.hibernate.Transaction;

/**
 * Base class for DAOs (Data Access Objects). Provides hibernate-specific
 * methods to query and store objects, as well as session methods such as
 * rollback and commit. Although we have hibernate-specific types here, concrete
 * subclasses should not expose anything hibernate related.
 */
public class BaseDAO {

    private DAOContext daoContext;
    private Transaction tx;

    /**
     * Initialize DAO with singleton DAOContext and initiate new transaction.
     */
    public BaseDAO() throws DAOException {
        try {
            daoContext = DAOContext.getInstance();
            tx = daoContext.getSession().beginTransaction();
        } catch (HibernateException he) {
            throw new DAOException(he);
        } 
    }

    /**
     * Initialize DAO with supplied DAOContext, and initiate new transaction.
     */
    public BaseDAO(DAOContext dc) throws DAOException {
        try {
            if (dc == null)
                daoContext = DAOContext.getInstance();
            else
                daoContext = dc;
            tx = daoContext.getSession().beginTransaction();
        } catch (HibernateException he) {
            throw new DAOException(he);
        }   
    }

    /**
     * Return hibernate session for special case queries that cannot make
     * use of one of the methods below.
     *
     * @return hibernate <code>Session</code>
     * @throws DAOException
     */
    protected Session getSession() throws DAOException {
        Session session = daoContext.getSession();
        return session;
    }

    /**
     * Removes the object from the database with specified class
     * type and <code>id</code>.
     *
     * @param c the class type to remove
     * @param id the id of the class type
     * @throws DAOException
     */
    protected void removeObj(Class c, Long id) throws DAOException {
        try {
            Session session = daoContext.getSession();
            // first load the object with the current session.
            // the object must be loaded in this session before it
            //   is deleted.
            beginTransaction();
            Object obj = session.load(c, id);
            session.delete(obj);
            session.flush();
            commitTransaction();
        }
        catch(Exception e) {
            e.printStackTrace(System.out);
            rollbackTransaction();
            throw new DAOException(e);
        }
    }

    /**
     * Removes the object from the database. Assumes the object has already
     * been introduced into the session through a prior retrieve.
     *
     * @param o the data object to remove
     * @throws DAOException
     */
    protected void removeObj(Object obj) throws DAOException {
        try {
            Session session = daoContext.getSession();
            beginTransaction();
            session.delete(obj);
            session.flush();
            commitTransaction();
        }
        catch(Exception e) {
            e.printStackTrace(System.out);
            rollbackTransaction();
            throw new DAOException(e);
        }
    }

    /**
     * Creates a hibernate Criteria object for a given mapped hibernate class c.
     *
     * @param c the mapped hibernate class
     * @return hibernate Criteria object
     * @throws DAOException
     */
    protected Criteria createCriteria(Class c) throws DAOException {
        Criteria criteria = null;
        Session session = daoContext.getSession();
        criteria = session.createCriteria(c);
        return criteria;
    }

    /**
     * Retrieves and <code>Object</code> of the class type specified
     * by <code>c</code>, and having the given <code>id</code>.
     *
     * @param c the class to load
     * @param id the id of the class
     * @return Object may be null if object with ID doesn't exist
     * @throws DAOException
     */
    protected Object retrieveObj(Class c, Long id) throws DAOException {
        Object obj = null;

        try {
            Session session = daoContext.getSession();
            obj = session.load(c, id);
        }
        catch(HibernateException he) {
            he.printStackTrace(System.out);
            throw new DAOException(he);
        }
		return obj;
    }

    /**
     * Retrieves an <code>Object</code> from the database.
     *
     * @param query the HQL query
     * @param values the value(s) that is inserted into the query.  May be null
     * if the desired query does not take a parameter.
     * @param types the hibernate types of the above values
     * @return Object object retrieved via query
     * @throws DAOException
     */
    protected Object retrieveObj(String query, Object[] values, Type[] types) throws DAOException {
        List objects = retrieveObjs(query, values, types);
        if (objects != null) {
            if (objects.size() == 0) {
                return null;
            }
            else {
                return objects.get(0);
            }
        }
        else {
            return null;
        }
    }

    /**
     * Retrieves a <code>List</code> of <code>Object</code>s from the database.
     *
     * @param query the HQL query
     * @param values the value(s) that is inserted into the query.  May be null
     * if the desired query does not take a parameter.
     * @param types the hibernate types of the above values
     * @return List will be null if no objects are retrieved
     * @throws DAOException
     */
    protected List retrieveObjs(String query, Object[] values, Type[] types) throws DAOException {
        List results = null;

        try {
            Session session = daoContext.getSession();
            if (values != null) {
                results = (List) session.createQuery(query).setParameters(values, types).list();
            }
            else {
                results = (List) session.createQuery(query).list();
            }
        }
        catch (HibernateException he) {
            he.printStackTrace(System.out);
            throw new DAOException(he);
        }
		return results;
    }

    /**
     * Stores <code>obj</code>, making it persistent.
     *
     * @param obj
     * @throws DAOException
     */
    protected void storeObj(Object obj) throws DAOException {
        try {
            Session session = daoContext.getSession();
            beginTransaction();
            session.saveOrUpdate(obj);
            session.flush();
            commitTransaction();

        } catch (StaleObjectStateException sose) {
            rollbackTransaction();
            throw new DAOStaleObjectStateException(sose);

        } catch(HibernateException he) {
            rollbackTransaction();
            throw new DAOException(he);
        }
    }


    /**
     * Evicts all objects from Hibernate first-level cache.
     * Ideally we shouldn't have
     * a hibernate-specific call in the DAO API, but this can be considered a
     * generic cache evict for other underlying DAO providers.
     *
     * @throws DAOException
     */
    public void evictAll() throws DAOException {
        Session session = daoContext.getSession();
        commitTransaction();
        session.clear();
    }

    /**
     * Evicts given object from Hibernate first-level cache, and any associated
     * objects (if they are mapped with cascade="all". Ideally we shouldn't have
     * a hibernate-specific call in the DAO API, but this can be considered a
     * generic cache evict for other underlying DAO providers.
     *
     * @param o data object that was returned by a hibernate query
     * @throws DAOException
     */
    public void evict(Object o) throws DAOException {
        if (o != null) {
            try {
                Session session = daoContext.getSession();
                commitTransaction();
                session.evict(o);
            } catch (HibernateException he) {
                he.printStackTrace(System.out);
                throw new DAOException(he);
            }
        }
    }

    /**
     * Evicts given collection from Hibernate first-level cache, and any associated
     * objects (if they are mapped with cascade="all". Ideally we shouldn't have
     * a hibernate-specific call in the DAO API, but this can be considered a
     * generic cache evict for other underlying DAO providers.
     *
     * @param c collection of data objects that was returned by a hibernate query
     * @throws DAOException
     */
    public void evict(Collection c) throws DAOException {
        if (c != null) {
            try {
                Session session = daoContext.getSession();
                commitTransaction();
                Iterator it = c.iterator();
                while (it.hasNext()) {
                    Object o = it.next();
                    session.evict(o);
                }
            } catch (HibernateException he) {
                he.printStackTrace(System.out);
                throw new DAOException(he);
            }
        }
    }

    /**
     * Refreshes given object with fresh data from database (skipping cache).
     * Includes associated objects (if they are mapped with cascade="all").
     *
     * @param o data object that was returned by a hibernate query
     * @throws DAOException
     */
    public void refresh(Object o) throws DAOException {
        if (o != null) {
            try {
                Session session = daoContext.getSession();
                session.refresh(o);
            } catch (HibernateException he) {
                he.printStackTrace(System.out);
                throw new DAOException(he);
            }
        }
    }

    /**
     * Refreshes all objects in the collection with fresh data from database. 
     * Includes associated objects (if they are mapped with cascade="all").
     *
     * @param c collection of data objects that was returned by a hibernate query
     * @throws DAOException
     */
    public void refresh(Collection c) throws DAOException {
        if (c != null) {
            try {
                Session session = daoContext.getSession();
                Iterator it = c.iterator();
                while (it.hasNext()) {
                    Object o = it.next();
                    session.refresh(o);
                }
            } catch (HibernateException he) {
                he.printStackTrace(System.out);
                throw new DAOException(he);
            }
        }
    }

    /**
     * Begin a new transaction on the session, or if one is in progress
     * that has not been committed or rolled back, then join that one.
     *
     * @throws DAOException
     */
    private void beginTransaction() throws DAOException {
        try {
            tx = daoContext.getSession().beginTransaction();
        } catch (HibernateException he) {
            throw new DAOException(he);
        }
    }

    /**
     * Rollback the current session's database transaction. Closes the
     * session afterwards.
     *
     * @throws DAOException
     */
    private void rollbackTransaction() throws DAOException {
        try {
            Transaction tempTx = tx;
            tx = null;
            if (tempTx != null && !tempTx.wasCommitted() && !tempTx.wasRolledBack()) {
                tempTx.rollback();
            }            
        } catch (HibernateException he) {
            throw new DAOException(he);

        } finally {
            try {
                Session s = daoContext.getSession();
                if (s != null && s.isOpen())
                    s.close();
            } catch (HibernateException he2) {
                throw new DAOException(he2);
            }
        }
    }

    /**
     * Commit the current session's database transaction.
     *
     * @throws DAOException
     */
    protected void commitTransaction() throws DAOException {
        try {
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack())
                tx.commit();
            tx = null;
        } catch (HibernateException he) {
            rollbackTransaction();
            throw new DAOException(he);
        } 
    }
        

}
