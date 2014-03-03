/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.login;

import java.util.*;
import java.security.Principal;

/**
 * JAAS Principal representing an IRMIS role. Any given user
 * must posess the right set of these Principals in order
 * to have permission to perform certain actions within IRMIS.
 */
public class RolePrincipal implements Principal {

    private String role;

    public RolePrincipal(String role) {
        this.role = role;
    }

    public String getName() {
        return role;
    }

    public int hashCode() {
        return role.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof RolePrincipal)) {
            return false;
        }
        return role.equals(((RolePrincipal)o).getName());
    }

    public String toString() {
        return role;
    }

}
