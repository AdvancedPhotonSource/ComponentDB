/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/api/CdbSession.java $
 *   $Date: 2015-04-17 12:25:03 -0500 (Fri, 17 Apr 2015) $
 *   $Revision: 594 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.api;

import gov.anl.aps.cdb.common.constants.CdbRole;
import gov.anl.aps.cdb.common.exceptions.InvalidSession;
import java.io.Serializable;
import java.net.HttpCookie;

/**
 * CDB session class, used for keeping all session-related information (session
 * id, username, role, etc.).
 */
public class CdbSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id = null;
    private String username = null;
    private String cookie = null;
    private CdbRole role = null;

    public CdbSession() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String verifyCookie() throws InvalidSession {
        if (cookie == null) {
            throw new InvalidSession("Valid session has not been established.");
        } else {
            HttpCookie httpCookie = HttpCookie.parse(cookie).get(0);
            if (httpCookie.hasExpired()) {
                throw new InvalidSession("Session id " + id + " has expired.");
            }
        }
        return cookie;
    }

    public CdbRole getRole() {
        return role;
    }

    public void setRole(CdbRole role) {
        this.role = role;
    }

    public boolean isAdminRole() {
        if (role != null) {
            return role.equals(CdbRole.ADMIN);
        }
        return false;
    }

    public boolean isUserRole() {
        if (role != null) {
            return role.equals(CdbRole.USER);
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "{ ";
        String delimiter = "";
        if (username != null) {
            result += "username :" + username;
            delimiter = "; ";
        }
        if (id != null) {
            result += delimiter + "id : " + id;
            delimiter = "; ";
        }
        if (cookie != null) {
            result += delimiter + "cookie : " + cookie;
        }
        result += " }";
        return result;
    }
}
