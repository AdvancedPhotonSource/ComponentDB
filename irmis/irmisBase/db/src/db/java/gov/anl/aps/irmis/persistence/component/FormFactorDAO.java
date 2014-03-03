/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import org.hibernate.type.Type;
import org.hibernate.Hibernate;

import gov.anl.aps.irmis.persistence.BaseDAO;
import gov.anl.aps.irmis.persistence.DAOException;

import java.util.List;

/**
 * Methods for finding/saving IRMIS <code>FormFactor</code>.
 */
public class FormFactorDAO extends BaseDAO {

    /**
     * Do nothing constructor.
     */
    public FormFactorDAO() throws DAOException {
        super();
    }

    /**
     * Find set of all form factors.
     * 
     * @return list of <code>FormFactor</code> objects
     * @throws DAOException
     */
    public List findAllFormFactors() throws DAOException {
        return retrieveObjs("from FormFactor ff order by ff.formFactor",null,null);
    }

    /**
     * Find a particular form factor by name.
     *
     * @param typeName string form factor name
     * @return single object instance
     */
    public FormFactor findFormFactor(String formFactorName) 
        throws DAOException {
        String query = "from FormFactor ff where ff.formFactor = ?";
        String[]values = {formFactorName};
        Type[]types = {Hibernate.STRING};

        return (FormFactor)retrieveObj(query, values, types);
                        
    }

    /**
     * Save a new instance of FormFactor.
     */
    public void save(FormFactor formFactor) throws DAOException {
        storeObj(formFactor);
    }

}
