/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.login;

import java.util.*;
import java.security.Principal;

/**
 * JAAS Principal representing an IRMIS username. This is
 * typically the same as the unix username.
 */
public class UserPrincipal implements Principal {

    private String username;

    public UserPrincipal(String username) {
        this.username = username;
    }

    public String getName() {
        return username;
    }

    public int hashCode() {
        return username.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof UserPrincipal)) {
            return false;
        }
        return username.equals(((UserPrincipal)o).getName());
    }

    public String toString() {
        return username;
    }

}
