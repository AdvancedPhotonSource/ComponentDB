/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.componenthistory;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOContext;
import gov.anl.aps.irmis.persistence.DAOException;

import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.Component;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import java.util.List;
import java.util.ArrayList;

/**
 * Methods for explicitly finding IRMIS component instance records and instantiating
 * <code>ComponentInstance</code> objects from them.
 */
public class ComponentInstanceDAO extends BaseDAO {

    private boolean matchAllOption = true;
    private String componentTypeNameConstraint = null;
    private String serialNumberConstraint = null;
    private Component componentConstraint = null;

    /**
     * Do nothing constructor.
     */
    public ComponentInstanceDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor.
     */
    public ComponentInstanceDAO(DAOContext dc) throws DAOException {
        super(dc);
    }

    /**
     * Convenience method to reset all constraints back to null in preparation
     * for another different query.
     */
    public void resetConstraints() {
        matchAllOption = true;
        componentTypeNameConstraint = null;
        serialNumberConstraint = null;
        componentConstraint = null;
    }

    /**
     * Set flag indicating whether ALL or ANY of other constraints should apply.
     *
     * @param allOption boolean where true means all, and false means any
     */
    public void setMatchAllOption(boolean b) {
        matchAllOption = b;
    }

    /**
     * Set a component type constraint. Make it a substring wildcard search.
     *
     * @param ctName component type name
     */
    public void setComponentTypeNameConstraint(String ctName) {
        if (ctName != null && ctName.length() > 0) {
            componentTypeNameConstraint = "%" + ctName + "%";
        } else {
            componentTypeNameConstraint = null;
        }
    }

    /**
     * Set a component constraint. 
     *
     * @param c component object
     */
    public void setComponentConstraint(Component c) {
        componentConstraint = c;
    }

    /**
     * Set a serial number constraint. This must match exactly.
     *
     * @param sn string serial number or identifier
     */
    public void setSerialNumberConstraint(String sn) {
        if (sn != null && sn.length() > 0) {
            serialNumberConstraint = "%" + sn + "%";
        } else {
            serialNumberConstraint = null;
        }
    }

    /**
     * Find all <code>ComponentInstance</code> that match constraints. 
     *
     * @return list of <code>ComponentInstance</code> objects
     * @throws DAOException
     */
    public List findByConstraints() throws DAOException {

        List matches = new ArrayList();

        // if componentConstraint is given, find individual associated component instance
        if (componentConstraint != null) {
            ComponentInstance ci = null;
            ci = findByComponent(componentConstraint);
            if (ci != null)
                matches.add(ci);
            return matches;
        }

        // Do search by component type and/or serial number
        String query = "from ComponentInstance ci ";
        if (componentTypeNameConstraint != null && serialNumberConstraint == null) {
            query = query + "where ci.componentType.componentTypeName like ?";
            Object[] values = {componentTypeNameConstraint};
            Type[] types = {Hibernate.STRING};
            matches.addAll(retrieveObjs(query, values, types));

        } else if (serialNumberConstraint != null && componentTypeNameConstraint == null) {
            query = query + "where ci.serialNumber like ?";
            String[] values = {serialNumberConstraint};
            Type[] types = {Hibernate.STRING};
            matches.addAll(retrieveObjs(query, values, types));

        } else if (serialNumberConstraint != null && componentTypeNameConstraint != null) {
            query = query + "where ci.componentType.componentTypeName like ? and ci.serialNumber like ?";
            Object[] values = {componentTypeNameConstraint, serialNumberConstraint};
            Type[] types = {Hibernate.STRING, Hibernate.STRING};
            matches.addAll(retrieveObjs(query, values, types));

        } else {
            matches.addAll(retrieveObjs(query, null, null));
        }

        return matches;
    }

    /**
     * Find <code>ComponentInstance</code> for given component.
     *
     * @param component
     * @return <code>ComponentInstance</code> object
     * @throws DAOException
     */
    public ComponentInstance findByComponent(Component component) throws DAOException {
        String query = "from ComponentInstance ci where ci.component = ?";
        Object[] values = {component};
        Type[] types = {Hibernate.entity(Component.class)};
        return (ComponentInstance)retrieveObj(query, values, types);
    }

    public void save(ComponentInstance compInstance) throws DAOException {
        storeObj(compInstance);
    }

    public void remove(ComponentInstance compInstance) throws DAOException {
        removeObj(compInstance);
    }

}
