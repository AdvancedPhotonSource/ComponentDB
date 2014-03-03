/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import java.util.Comparator;

/**
 * Comparator for ordering fields in a record by the id of the underlying
 * field type. Kind of hokey, but since we aren't explicitly representing
 * this ordering with an "order" column in the fld or fld_type tables,
 * we can't use hibernate's natural mapping to do this.
 */
public class FieldComparator implements Comparator {

    /**
     * Compare two Field objects by the id of the underlying FieldType.
     */
    public int compare(Object o1, Object o2) {
        Field f1 = (Field)o1;
        Field f2 = (Field)o2;
        return f1.getFieldType().getId().compareTo(f2.getFieldType().getId());
    }
    
}
