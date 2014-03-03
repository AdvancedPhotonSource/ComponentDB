/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence;

import java.io.PrintStream;

/**
 * General purpose application exception to wrap underlying data access exceptions.  
 */
public class DAOException extends Exception {

    public DAOException() {
        super();
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(Exception e) {
        super(e);
    }

    public DAOException(Exception e, String message) {
        super(message, e);
    }

}
