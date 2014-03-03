/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Methods for finding/saving IRMIS <code>ComponentPortType</code>.
 */
public class ComponentPortTypeDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public ComponentPortTypeDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all component port types.
     * 
     * @return list of <code>ComponentPortType</code> objects
     * @throws DAOException
     */
    public List findAllComponentPortTypes() throws DAOException {
        return retrieveObjs("from ComponentPortType cpt order by cpt.componentPortType",null,null);
    }

    /**
     * Find set of all component port type groups.
     * 
     * @return list of strings, each a distinct port type group name
     * @throws DAOException
     */
    public List findAllComponentPortTypeGroups() throws DAOException {
        String query = "select distinct cpt.componentPortGroup from ComponentPortType cpt " +
            "order by cpt.componentPortGroup";
        Iterator it = null;
        try {
            it = getSession().createQuery(query).list().iterator();
        } catch (HibernateException he) {
            throw new DAOException(he);
        }

        List groupList = new ArrayList();
        while (it.hasNext()) {
            Object row = it.next();
            String groupName = (String)row;
            groupList.add(groupName);
        }
        return groupList;
    }

    /**
     * Find a particular component port type by name.
     *
     * @param typeName string port type name
     * @return single object instance
     */
    public ComponentPortType findComponentPortType(String componentPortTypeName) 
        throws DAOException {
        String query = "from ComponentPortType cpt where cpt.componentPortType = ?";
        String[]values = {componentPortTypeName};
        Type[]types = {Hibernate.STRING};

        return (ComponentPortType)retrieveObj(query, values, types);
                        
    }

    /**
     * Save a new instance of ComponentPortType.
     */
    public void save(ComponentPortType componentPortType) throws DAOException {
        storeObj(componentPortType);
    }

}
