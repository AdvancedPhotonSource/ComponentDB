/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.service;

import java.io.PrintStream;

/**
 * General purpose IRMIS exception to wrap underlying hibernate and
 * data access object exceptions.
 */
public class IRMISException extends Exception {

    public IRMISException() {
        super();
    }

    public IRMISException(String message) {
        super(message);
    }

    public IRMISException(Exception e) {
        super(e);
    }

    public IRMISException(Exception e, String message) {
        super(message, e);
    }
}
