/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence;

/**
 * Special case of a DAO exception indicating that a DAO operation failed
 * due to the object graph being stale with respect to underlying database.
 */
public class DAOStaleObjectStateException extends DAOException {

    public DAOStaleObjectStateException() {
        super();
    }

    public DAOStaleObjectStateException(String message) {
        super(message);
    }

    public DAOStaleObjectStateException(Exception e) {
        super(e);
    }

    public DAOStaleObjectStateException(Exception e, String message) {
        super(e, message);
    }

}
