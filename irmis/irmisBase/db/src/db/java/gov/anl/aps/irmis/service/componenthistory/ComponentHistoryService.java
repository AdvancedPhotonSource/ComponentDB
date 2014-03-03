/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.service.componenthistory;

// java imports
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.Collections;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.PersonDAO;
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstance;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstanceDAO;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstanceState;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstanceStateDAO;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentState;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentStateDAO;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentStateCategory;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentStateCategoryDAO;
import gov.anl.aps.irmis.persistence.DAOContext;
import gov.anl.aps.irmis.persistence.DAOException;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Component History Service provides methods to manage component instances
 * and the component instance state associated with them.
 *
 * The methods here are themselves purely transactional.
 * In other words, you do not use new to make an instance of this class.
 *
 * An IRMISException is generally thrown when the supplied arguments are
 * not what is expected, or when there is some underlying database access
 * problem. 
 */
public class ComponentHistoryService extends IRMISService {

    // DO NOT PUT ANY INSTANCE VARIABLES HERE

    /**
     * Hide constructor, since all methods are static.
     */
    private ComponentHistoryService() {
    }

    /**
     * Find the list of all known component state categories in the database. 
     *
     * @return list of <code>ComponentStateCategory</code>objects
     * @throws IRMISException
     *
     * @see ComponentStateCategory
     */
    public static List findComponentStateCategoryList() throws IRMISException {
        ComponentStateCategoryDAO cscDAO = null;
        List cscList = null;
        try {
            cscDAO = new ComponentStateCategoryDAO();
            cscList = cscDAO.findAll();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return cscList;
    }

    /**
     * Find a particular ComponentStateCategory by category name.
     *
     * @param category string category name
     * @return <code>ComponentStateCategory</code> object
     * @throws IRMISException
     *
     * @see ComponentStateCategory
     */
    public static ComponentStateCategory findComponentStateCategoryByCategory(String category) 
        throws IRMISException {

        ComponentStateCategoryDAO cscDAO = null;
        ComponentStateCategory csc = null;
        try {
            cscDAO = new ComponentStateCategoryDAO();
            csc = cscDAO.findByCategory(category);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return csc;
    }    

    /**
     * Find the list of all known component states for a given category.
     *
     * @param category a <code>ComponentStateCategory</code> object
     * @return list of <code>ComponentState</code> objects
     * @throws IRMISException
     *
     * @see ComponentState
     */
    public static List findComponentStatesByCategory(ComponentStateCategory category) 
        throws IRMISException {

        ComponentStateDAO csDAO = null;
        List csList = null;
        try {
            csDAO = new ComponentStateDAO();
            csList = csDAO.findByCategory(category);
        } catch (DAOException de) {
            throw new IRMISException(de);
        } 
        return csList;
    }

    /**
     * Find the ComponentState for given category and state.
     *
     * @param category a <code>ComponentStateCategory</code> object
     * @param state string state name
     * @return <code>ComponentState</code> object
     * @throws IRMISException
     *
     * @see ComponentState
     */
    public static ComponentState findComponentStateByCategoryAndState(ComponentStateCategory category,
                                                                      String state) 
        throws IRMISException {

        ComponentStateDAO csDAO = null;
        List csList = null;
        ComponentState cs = null;
        try {
            csDAO = new ComponentStateDAO();
            csList = csDAO.findByCategory(category);
            Iterator it = csList.iterator();
            while (it.hasNext()) {
                ComponentState candidate = (ComponentState)it.next();
                if (candidate.getState().equals(state)) {
                    cs = candidate;
                    break;
                }
            }
        } catch (DAOException de) {
            throw new IRMISException(de);
        } 
        return cs;
    }

    /**
     * Find the set of <code>ComponentInstance</code> objects that meets the constraints given by
     * searchParams. 
     *
     * @param searchParams search parameters object with constraints
     * @return list of <code>ComponentInstance</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findComponentInstanceList(ComponentInstanceSearchParameters searchParams) 
        throws IRMISException {

        ComponentInstanceDAO ciDAO = null;
        List ciList = null;
        try {
            ciDAO = new ComponentInstanceDAO();
            // set up search constraints
            
            ciDAO.setMatchAllOption(searchParams.getMatchAll());
            ciDAO.setComponentTypeNameConstraint(searchParams.getComponentTypeName());
            ciDAO.setSerialNumberConstraint(searchParams.getSerialNumber());
            ciDAO.setComponentConstraint(searchParams.getComponent());

            // do search
            ciList = ciDAO.findByConstraints();

            // If location is given, then filter matches by current location substring match
            String locationConstraint = searchParams.getLocation();
            if (locationConstraint != null)
                locationConstraint = locationConstraint.toUpperCase();
            Component c = searchParams.getComponent();
            if (c == null && locationConstraint != null && locationConstraint.length() > 0) {
                List tempMatches = ciList;
                ciList = new ArrayList();
                Iterator matchIt = tempMatches.iterator();
                while (matchIt.hasNext()) {
                    ComponentInstance ci = (ComponentInstance)matchIt.next();
                    String currentLocation = findComponentInstanceCurrentLocation(ci);
                    if (currentLocation != null) {
                        if ((currentLocation.toUpperCase()).contains(locationConstraint))
                            ciList.add(ci);
                    }
                }
            }


        } catch (DAOException de) {
            throw new IRMISException(de);
        }
        return ciList;
    }

    /**
     * Find the <code>ComponentInstance</code> associated with given component.
     *
     * @param component
     * @return <code>ComponentInstance</code> object
     * @throws IRMISException
     */
    public static ComponentInstance findComponentInstanceByComponent(Component component) 
        throws IRMISException {

        ComponentInstanceDAO ciDAO = null;
        ComponentInstance ci = null;
        try {
            ciDAO = new ComponentInstanceDAO();
            ci = ciDAO.findByComponent(component);

        } catch (DAOException de) {
            throw new IRMISException(de);
        }
        return ci;
    }

    /**
     * Find the list of all known component instance state for a given component instance.
     * NOTE: can probably get rid of this, since ComponentInstance has this already.
     *
     * @param ci component instance
     * @return list of <code>ComponentInstanceState</code>objects
     * @throws IRMISException
     *
     * @see ComponentInstanceState
     */
    public static List findComponentInstanceStateList(ComponentInstance ci) throws IRMISException {
        ComponentInstanceStateDAO cisDAO = null;
        List cisList = null;
        try {
            cisDAO = new ComponentInstanceStateDAO();
            cisList = cisDAO.findStateList(ci);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return cisList;
    }

    /**
     * Find the list of all component instance states that are entered under
     * given category. Ie. what are all the "location" entries for this component
     * instance. Make sure list is sorted by date the entry was entered.
     *
     * @param ci component instance
     * @param category category
     * @return list of <code>ComponentInstanceState</code> objects
     */
    public static List findComponentInstanceStatesByCategory(ComponentInstance ci,
                                                             ComponentStateCategory category) {
        ArrayList states = new ArrayList();
        Set allStates = ci.getComponentInstanceStates();
        if (allStates == null || allStates.size() == 0)
            return null;

        Iterator it = allStates.iterator();
        while (it.hasNext()) {
            ComponentInstanceState cis = (ComponentInstanceState)it.next();
            if (cis.getComponentState().getComponentStateCategory().equals(category))
                states.add(cis);
        }
        // sort it by date
        Collections.sort(states);
        return states;
    }


    /**
     * Get all location history from given component instance. Find last update to
     * location and return the string representation of that location.
     *
     * @param ci component instance
     * @return string representing location
     * @throws IRMISException
     */
    public static String findComponentInstanceCurrentLocation(ComponentInstance ci) 
        throws IRMISException {
        
        ComponentStateCategory locationCategory = null;
        List locationStates = null;

        locationCategory = findComponentStateCategoryByCategory("location");
        locationStates = findComponentInstanceStatesByCategory(ci, locationCategory);

        if (locationStates == null || locationStates.size() == 0)
            return null;

        // search through states and get last location entry
        int size = locationStates.size();
        ComponentInstanceState lastLocation = 
            (ComponentInstanceState)locationStates.get(size-1);
        return lastLocation.getReferenceData1();
    }
       

    /**
     * Persist the given component instance to the database. If it is a completely new object
     * in a transient state (the id field is not initialized), a new row will be inserted
     * in the database, otherwise an update will occur. Any other associations will also
     * be inserted or updated. An exception will be thrown if the minimum of data is not
     * provided. 
     *
     * @param component new (transient) or existing (persistent) <code>ComponentInstance</code> object
     * @throws IRMISException
     */
    public static void saveComponentInstance(ComponentInstance componentInstance) 
        throws IRMISException {

        ComponentInstanceDAO ciDAO = null;
        try {
            ciDAO = new ComponentInstanceDAO();
            ciDAO.save(componentInstance);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }    

    /**
     * Delete the given component instance from the database. This will also delete any
     * associated history data for this instance.
     *
     * @param component existing (persistent) <code>ComponentInstance</code> object
     * @throws IRMISException
     */
    public static void removeComponentInstance(ComponentInstance componentInstance) 
        throws IRMISException {

        ComponentInstanceDAO ciDAO = null;
        try {
            ciDAO = new ComponentInstanceDAO();
            ciDAO.remove(componentInstance);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }    

}
