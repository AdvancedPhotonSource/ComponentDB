/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.hibernate.Session;
import org.hibernate.SQLQuery;
import org.hibernate.type.Type;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;
import gov.anl.aps.irmis.persistence.SemaphoreValue;


/**
 * Methods for managing a <code>SemaphoreValue</code> for components.
 */
public class ComponentSemaphoreDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentSemaphoreDAO() throws DAOException {
        super();
    }

    /**
     * Take the semaphore used to protect against concurrent edits of components.
     * This method will NOT block, so caller needs to try again or poll it.
     * This semaphore is implemented via the component_semaphore table and the
     * select-for-update technique. Caller should only proceed with component
     * edits if SemaphoreValue.getSemaphoreValue() is 1. If the value is 0,
     * the person who is currenly editing is given by SemaphoreValue.getUserid().
     * 
     * Note: the standard notion of P has been extended here. We also consider
     *       the semaphore taken already if it was returned within 2.5 minutes
     *       by another user. This is to prevent constant swapping back-and-forth
     *       of the component editing lock between users.
     *
     * @param userid userid of person taking semaphore
     * 
     * @return value and assorted info about semaphore as <code>SemaphoreValue</code> object
     * @throws DAOException
     */
    public SemaphoreValue P(String userid) throws DAOException {
        String selectForUpdate = "select component_semaphore_id,semaphore,userid,modified_date from component_semaphore for update";
        String update = "update component_semaphore set component_semaphore_id=component_semaphore_id+1, semaphore=0, userid=?, modified_date=?";
        String staleUpdate = "update component_semaphore set component_semaphore_id=component_semaphore_id+1, userid=?, modified_date=?";

        Session s = getSession();
        try {
            Connection conn = s.connection();
            Statement st = conn.createStatement();
            // see what the state of the semaphore is using "select for update"
            conn.commit(); // make sure we get latest data 
            ResultSet rs = st.executeQuery(selectForUpdate);
            long id = 0;
            int semVal = 0;
            String dbUserid = null;
            long modifiedDate = 0;
            if (rs.next()) {
                id = rs.getLong("component_semaphore_id");
                semVal = rs.getInt("semaphore");
                dbUserid = rs.getString("userid");
                modifiedDate = rs.getLong("modified_date");
                //System.out.println("-- select for update gives semVal:"+semVal+" userid:"+dbUserid+" modified date:"+modifiedDate);
            }
            rs.close();
            st.close();
            Date currentDate = new Date();
            //System.out.println("-- currentDate:"+currentDate.getTime());
            // if sem is taken, check for staleness
            if (semVal == 0) {
                // if modified_date is old, someone probably forgot to V(), so
                // we will take it anyways, and update the userid/modified_date.
                if ((currentDate.getTime() - modifiedDate) > 300000) {  // 5 minutes
                    long timeDiff = currentDate.getTime() - modifiedDate;
                    //System.out.println("-- semaphore taken, but stale, we'll take it anyways, time diff:"+timeDiff);
                    PreparedStatement pst = conn.prepareStatement(staleUpdate);
                    pst.setString(1, userid);
                    pst.setLong(2, currentDate.getTime());
                    pst.executeUpdate();
                    conn.commit();
                    pst.close();
                    return new SemaphoreValue(id+1, 1, userid, 0);
                    
                } else {  // someone else has the sem, so let caller know who
                    //System.out.println("-- someone else has sem, let caller know");
                    conn.commit();
                    return new SemaphoreValue(0, 0, dbUserid, 0);
                }

                
            } else if (!dbUserid.equals(userid) &&
                       (currentDate.getTime() - modifiedDate) < 150000) {  // 2.5 minutes
                // Also consider sem taken if it was returned within last 2.5 minutes
                // by some other user than requesting user. Prevents constant swapping
                // back-and-forth of editing lock between users.
                //System.out.println("-- someone else had sem recently, let caller know they can't");
                conn.commit();
                return new SemaphoreValue(0, 0, dbUserid, 0);
            }
            // sem is not taken, so we take it now using an update statement
            PreparedStatement pst = conn.prepareStatement(update);
            pst.setString(1, userid);
            pst.setLong(2, currentDate.getTime());
            pst.executeUpdate();
            conn.commit();
            pst.close();
            return new SemaphoreValue(id+1, 1, userid, modifiedDate);
            
        } catch (Exception e) {
            throw new DAOException(e);
        } 
    }

    public boolean V(SemaphoreValue sv) throws DAOException {
        if (sv.getSemaphoreValue() == 0)
            return false;
        String update = "update component_semaphore set semaphore = 1, modified_date=? where component_semaphore_id=?";
        Session s = getSession();
        boolean updatedRow = false;
        try {
            Date currentDate = new Date();
            Connection conn = s.connection();
            PreparedStatement pst = conn.prepareStatement(update);
            pst.setLong(1, currentDate.getTime());
            pst.setLong(2, sv.getId());
            if (pst.executeUpdate() == 1) {
                sv.setModifiedDate(currentDate.getTime());
                updatedRow = true;
            } else {
                sv.setModifiedDate(0);
                updatedRow = false;
            }
            conn.commit();
            sv.setId(0);
            sv.setSemaphoreValue(0);
            pst.close();
            return updatedRow;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public boolean isValid(SemaphoreValue sv) throws DAOException {
        if (sv == null || sv.getSemaphoreValue() == 0)
            return false;
        String select = "select semaphore from component_semaphore where component_semaphore_id=?";
        boolean valid = false;
        Session s = getSession();
        try {
            Connection conn = s.connection();
            PreparedStatement pst = conn.prepareStatement(select);
            pst.setLong(1, sv.getId());

            // see if we have a valid sv with a valid id
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                valid = true;
            }
            rs.close();
            pst.close();
            return valid;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

}
