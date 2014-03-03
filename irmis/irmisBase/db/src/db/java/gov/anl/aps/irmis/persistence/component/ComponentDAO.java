/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.*;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;


/**
 * Methods for finding/saving IRMIS <code>Component</code>.
 */
public class ComponentDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all components.
     * 
     * @return list of <code>Component</code> objects
     * @throws DAOException
     */
    public List findAllComponents() throws DAOException {
        return retrieveObjs("from Component c",null,null);
    }

    /**
     * Find a particular component by its primary key id.
     *
     * @param componentId primary key id
     * @return a <code>Component</code> object, or null if not found
     */
    public Component findComponentById(Long id) throws DAOException {
        String query = "from Component c where c.id = ?";
        Long[]values = {id};
        Type[]types = {Hibernate.LONG};

        return (Component)retrieveObj(query, values, types);
    }

    /**
     * Find components whose type matches ct.
     *
     * @param ct component type
     * @return list of <code>Component</code> objects.
     * @throws DAOException
     */
    public List findComponentsByType(ComponentType ct) throws DAOException {
        String query = "from Component c join fetch c.componentType "+
            "join fetch c.apsComponent ac "+
            "where c.markForDelete = 0 and c.componentType = ?";
        Object[]values = {ct};
        Type[]types = {Hibernate.entity(ComponentType.class)};
        List results = retrieveObjs(query, values, types);
        results.size();

        // prefetch immediate control child data into object graph
        String query2 = "from Component c join fetch c.controlChildRelationships ccr "+
            "join fetch ccr.childComponent cc "+
            "join fetch cc.apsComponent "+
            "where c.markForDelete = 0 and c.componentType = ?";
        Object[]values2 = {ct};
        Type[]types2 = {Hibernate.entity(ComponentType.class)};
        retrieveObjs(query2, values2, types2);
        
        // prefetch immediate housing child data into object graph
        String query3 = "from Component c join fetch c.housingChildRelationships hcr "+
            "join fetch hcr.childComponent cc "+
            "join fetch cc.apsComponent "+
            "where c.markForDelete = 0 and c.componentType = ?";
        Object[]values3 = {ct};
        Type[]types3 = {Hibernate.entity(ComponentType.class)};
        retrieveObjs(query3, values3, types3);
        
        // prefetch immediate power child data into object graph
        String query4 = "from Component c join fetch c.powerChildRelationships pcr "+
            "join fetch pcr.childComponent cc "+
            "join fetch cc.apsComponent "+
            "where c.markForDelete = 0 and c.componentType = ?";
        Object[]values4 = {ct};
        Type[]types4 = {Hibernate.entity(ComponentType.class)};
        retrieveObjs(query4, values4, types4);
        
        return results;
    }

    /**
     * Find components who have searchText in their component name , type, or parent
     * relationship logical description.
     *
     * @param searchText text to search for (we insert wildcards before and after)
     * @param hierarchy which hierarchy from ComponentRelationshipType enum
     * @return list of <code>Component</code> objects.
     * @throws DAOException
     */
    public List findComponentsByName(String searchText, int hierarchy) throws DAOException {

        String percentSearchText = "%" + searchText + "%";

        // this query joins components and relationships, so further processing must occur
        String query = "from Component c left join fetch c.componentType ct " +
            "left join fetch c.parentRelationships pr "+
            "where c.markForDelete = 0 "+
            "and (c.componentName like ? or ct.componentTypeName like ? or pr.logicalDescription like ?)";
        Object[]values = {percentSearchText,percentSearchText,percentSearchText};
        Type[]types = {Hibernate.STRING,Hibernate.STRING,Hibernate.STRING};
        List results = retrieveObjs(query, values, types);
        if (results != null) {
            HashSet distinct = new HashSet(results);  // flatten repeated rows from join
            Iterator cit = distinct.iterator();
            ArrayList filteredResults = new ArrayList();
            while (cit.hasNext()) {
                Component c = (Component)cit.next();
                ComponentRelationship cr = c.getParentRelationship(hierarchy);
                // make sure we have parent relationship in hierarchy
                if (cr != null)
                    filteredResults.add(c);
            }
            if (filteredResults.size() == 0)
                return null;
            else
                return filteredResults;
        } else {
            return null;
        }
            

    }

    /**
     * Find a particular component by serial number.
     *
     * @param serialNumber string serial number
     * @return list of <code>Component</code> objects, empty if none
     */
    public List findComponentsBySerialNumber(String serialNumber) throws DAOException {
        String query = "from Component c where c.serialNumber = ?";
        String[]values = {serialNumber};
        Type[]types = {Hibernate.STRING};

        return retrieveObjs(query, values, types);
    }

    public void save(Component comp) throws DAOException {
        storeObj(comp);
    }

}
