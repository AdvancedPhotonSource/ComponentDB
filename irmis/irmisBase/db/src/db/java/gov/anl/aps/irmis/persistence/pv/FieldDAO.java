/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOContext;
import gov.anl.aps.irmis.persistence.DAOException;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Methods for explicitly finding IRMIS process variable fields and instantiating
 * <code>Field</code> objects from them.
 */
public class FieldDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public FieldDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor.
     */
    public FieldDAO(DAOContext dc) throws DAOException {
        super(dc);
    }

    /**
     * Find the set of <code>Field</code> associated with the primary key id of a given 
     * <code>Record</code>. Method explicitly joins in field type also.
     *
     * @param recId primary key id of a <code>Record</code> instance
     * @return list of <code>Field</code> objects
     * @throws DAOException
     */
    public List findByRecordId(Long recId) throws DAOException {
        String query = "from Field f join fetch f.fieldType ft where f.record.id = ?";
        Long[]values = {recId};
        Type[]types = {Hibernate.LONG};
        return retrieveObjs(query,values,types);
    }

    /**
     * Find all fields that are of type DBF_XXXLINK that have a reference to a
     * given record name in them. Constrain search to "current load" as given in
     * iocBootList. 
     *
     * @param iocBootList list of <code>IOCBoot</code> from 
     *                    <code>IOCBootDAO.findCurrentLoads()</code> call
     * @param recName process variable name minus any trailing period, field name, or modifiers
     * @return list of <code>Field</code> objects
     * @throws DAOException
     */
    public List findByRecordNameAndLink(List iocBootList, String recName) throws DAOException {
        // add SQL wildcard to end of record name
        String recNameGlob = recName + "%";

        // constrain field search to these dbd types
        String[] dbdConstraint = {"DBF_INLINK","DBF_OUTLINK","DBF_FWDLINK"};

        // build up list of ioc boot id's to constrain search 
        List bootIdConstraint = new ArrayList();
        Iterator iocBootIt = iocBootList.iterator();
        while (iocBootIt.hasNext()) {
            IOCBoot iocBoot = (IOCBoot)iocBootIt.next();
            bootIdConstraint.add(iocBoot.getId());
        }
        
        // use hibernate Criteria query mechanism
        List results = null;
        Criteria fieldCrit = null;
        try {
            fieldCrit = createCriteria(Field.class);
            Criteria fieldTypeCrit = fieldCrit.createCriteria("fieldType");
            Criteria recordCrit = fieldCrit.createCriteria("record");
            fieldCrit.add(Expression.like("fieldValue",recNameGlob));
            fieldTypeCrit.add(Expression.in("dbdType",dbdConstraint));
            recordCrit.add(Expression.in("iocBoot.id",bootIdConstraint));
            results = fieldCrit.list();

        } catch (HibernateException he) {
            throw new DAOException(he);
        }
        return results;
    }

}
