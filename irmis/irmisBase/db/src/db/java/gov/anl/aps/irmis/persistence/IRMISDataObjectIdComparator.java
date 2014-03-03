/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence;

import java.util.Comparator;

/**
 * Comparator for ordering IRMISDataObjects by id if necessary. Although 
 * we normally impose order by some business field of objects, there are
 * occasions where we need the natural database ordering (id) of rows.
 */
public class IRMISDataObjectIdComparator implements Comparator {

    /**
     * Compare two IRMISDataObject by the id field.
     */
    public int compare(Object o1, Object o2) {
        IRMISDataObject ido1 = (IRMISDataObject)o1;
        IRMISDataObject ido2 = (IRMISDataObject)o2;
        return ido1.getId().compareTo(ido2.getId());
    }
    
}
