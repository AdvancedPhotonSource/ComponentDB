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
 * Methods for explicitly finding IRMIS process variable records and instantiating
 * <code>Record</code> objects from them. As a convenience, you can set a number
 * of search constraints using several methods, and then conduct the actual query
 * via <code>findByConstraints()</code> method. Be sure to reset the constraints
 * if you are doing another different query. 
 */
public class RecordDAO extends BaseDAO {

    private List bootIdConstraints = new ArrayList();
    private List recordTypeConstraints = new ArrayList();
    private List fieldIocResourceIdConstraints = new ArrayList();
    private String recordNameGlobConstraint = null;
    private String fieldNameGlobConstraint = null;
    private String fieldValueGlobConstraint = null;

    /**
     * Do nothing constructor
     */
    public RecordDAO() throws DAOException {
        super();
    }

    /**
     * Do nothing constructor
     */
    public RecordDAO(DAOContext dc) throws DAOException {
        super(dc);
    }

    /**
     * Convenience method to reset all constraints back to null in preparation
     * for another different query.
     */
    public void resetConstraints() {
        bootIdConstraints = new ArrayList();
        recordTypeConstraints = new ArrayList();
        fieldIocResourceIdConstraints = new ArrayList();
        recordNameGlobConstraint = null;
        fieldNameGlobConstraint = null;
        fieldValueGlobConstraint = null;
    }

    /**
     * Set a list of <code>IOCBoot</code> ids to constrain the query.
     *
     * @param bootIds list of <code>Long</code> primary key ids
     */
    public void setBootIdConstraints(List bootIds) {
        bootIdConstraints = bootIds;
    }
    
    /**
     * Add a <code>IOCBoot</code> id to constrain the query.
     *
     * @param id <code>Long</code> primary key id
     */
    public void addBootIdConstraint(Long id) {
        bootIdConstraints.add(id);
    }

    /**
     * Set a list of record types (String) to constrain the query.
     *
     * @param recTypes list of <code>String</code> record types such as ai or mbbo
     */
    public void setRecordTypeConstraints(List recTypes) {
        recordTypeConstraints = recTypes;
    }

    /**
     * Add a record type to constrain the query.
     *
     * @param exp <code>String</code> record type such as ai or mbbo
     */
    public void addRecordTypeConstraint(String exp) {
        recordTypeConstraints.add(exp);
    }

    /**
     * Set a list of <code>IOCResource</code> ids to constrain the query. These ids
     * are assumed to be references to ioc resources which define fields
     * such as db or template files, not dbd files.
     *
     * @param fieldIocResourceIds list of <code>Long</code> primary key ids
     */
    public void setFieldIocResourceIdConstraints(List fieldIocResourceIds) {
        fieldIocResourceIdConstraints = fieldIocResourceIds;
    }

    /**
     * Add a <code>IOCResource</code> id to constrain the query.
     *
     * @param id <code>Long</code> primary key id
     */
    public void addFieldIocResourceIdConstraint(Long id) {
        fieldIocResourceIdConstraints.add(id);
    }

    /**
     * Set a record name constraint. Expression may contain glob-style
     * wildcards such as S1P1*sum*AI.
     *
     * @param exp record name string with * wildcard
     */
    public void setRecordNameGlobConstraint(String exp) {
        if (exp == null)
            recordNameGlobConstraint = null;
        else
            recordNameGlobConstraint = exp.replace('*','%');
    }

    /**
     * Set a field name constraint. Expression may contain glob-style
     * wildcards such as INP*. Constrains search to those records
     * that contain field of this name.
     *
     * @param exp field name (such as HOPR) with * wildcard
     */
    public void setFieldNameGlobConstraint(String exp) {
        if (exp == null)
            fieldNameGlobConstraint = null;
        else
            fieldNameGlobConstraint = exp.replace('*','%');
    }

    /**
     * Set a field value constraint. Expression may contain glob-style
     * wildcards such as C1 S0 L*. Constrains search to those records
     * that contain a field with this given value.
     *
     * @param exp field value with * wildcard
     */
    public void setFieldValueGlobConstraint(String exp) {
        if (exp == null) 
            fieldValueGlobConstraint = null;
        else
            fieldValueGlobConstraint = exp.replace('*','%');
    }

    /**
     * Conduct search for records that meet given constraints. Instantiates
     * the set of matching <code>Record</code> objects and returns as a list.
     *
     * @return list of <code>Record</code> objects
     * @throws DAOException
     */
    public List findByConstraints() throws DAOException {

        // Use hibernate Criteria query mechanism
        List results = null;
        Criteria recordCrit = null;
        try {
            // get Record
            recordCrit = createCriteria(Record.class);

            // join with RecordType
            Criteria recordTypeCrit = recordCrit.createCriteria("recordType");

            Criteria fieldCrit = null;
            Criteria fieldTypeCrit = null;
            if (recordNameGlobConstraint != null &&
                recordNameGlobConstraint.length() > 0) {
                recordCrit.add(Expression.like("recordName",recordNameGlobConstraint));
            }
            if (bootIdConstraints != null &&
                bootIdConstraints.size() > 0) {
                recordCrit.add(Expression.in("iocBoot.id",bootIdConstraints));
            }
            if (recordTypeConstraints != null &&
                recordTypeConstraints.size() > 0) {
                recordTypeCrit.add(Expression.in("recordType",recordTypeConstraints));
            }
            if (fieldValueGlobConstraint != null &&
                fieldValueGlobConstraint.length() > 0) {
                // possibly also join with Field
                if (fieldCrit == null)
                    fieldCrit = recordCrit.createCriteria("fields");
                fieldCrit.add(Expression.like("fieldValue",fieldValueGlobConstraint));
            }
            if (fieldIocResourceIdConstraints != null &&
                fieldIocResourceIdConstraints.size() > 0) {
                if (fieldCrit == null)
                    fieldCrit = recordCrit.createCriteria("fields");
                fieldCrit.add(Expression.in("iocResource.id",fieldIocResourceIdConstraints));
            }
            if (fieldNameGlobConstraint != null &&
                fieldNameGlobConstraint.length() > 0) {
                if (fieldCrit == null)
                    fieldCrit = recordCrit.createCriteria("fields");
                // possibly also join with FieldType
                if (fieldTypeCrit == null) 
                    fieldTypeCrit = fieldCrit.createCriteria("fieldType");
                fieldTypeCrit.add(Expression.like("fieldType",fieldNameGlobConstraint));
            }

            // filter out duplicate rows that may result from multi-table join
            recordCrit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            results = recordCrit.list();

        } catch (HibernateException he) {
            throw new DAOException(he);
        }
        return results;
    }

    /**
     * Find a single <code>Record</code> given its primary key id.
     *
     * @param recId primary key id of a record
     * @throws DAOException
     */
    public Record findByRecordId(Long recId) throws DAOException {
        String query = "from Record r join fetch r.recordType rt where r.id = ?";
        Long[]values = {recId};
        Type[]types = {Hibernate.LONG};
        return (Record)retrieveObj(query,values,types);
    }

}
