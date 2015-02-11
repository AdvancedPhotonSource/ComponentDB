package gov.anl.aps.cdb.api;


import gov.anl.aps.cdb.constants.CdbRole;
import gov.anl.aps.cdb.exceptions.InvalidSession;
import java.io.Serializable;
import java.net.HttpCookie;

/**
 * @author Sinisa Veseli
 * @version 13.03
 *
 * Cdb session class.
 */
public class CdbSession implements Serializable {

    /*
     * UID
     */
    private static final long serialVersionUID = 1L;


    /*
     * Session id.
     */
    private String id = null;

    /*
     * Session username.
     */
    private String username = null;

    /*
     * Session cookie.
     */
    private String cookie = null;

    /*
     * Session role.
     */
    private CdbRole role = null;

    /*
     * Constructor.
     */
    public CdbSession() {}

    /**
     * Get session username.
     *
     * @return session username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set session username.
     *
     * @param username session username
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Get session id.
     *
     * @return session id
     */
    public String getId() {
        return id;
    }

    /**
     * Set session id.
     *
     * @param id default session id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get session cookie.
     *
     * @return session cookie
     */
    public String getCookie() {
        return cookie;
    }


    /**
     * Set session cookie.
     *
     * @param cookie session cookie
     */
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }


    /**
     * Verify session cookie.
     *
     * @return session cookie
     * @throws InvalidSession if session cookie is expired or null
     */
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

    /**
     * Get session role.
     *
     * @return session role
     */
    public CdbRole getRole() {
        return role;
    }


    /**
     * Set session role.
     *
     * @param role session role
     */
    public void setRole(CdbRole role) {
        this.role = role;
    }

    /**
     * Check if session is for admin role.
     *
     * @return true if this is an admin session, false otherwise
     */
    public boolean isAdminRole() {
        if (role != null) {
            return role.equals(CdbRole.ADMIN);
        }
        return false;
    }

    /**
     * Check if session is for user role.
     *
     * @return true if this is an user session, false otherwise
     */
    public boolean isUserRole() {
        if (role != null) {
            return role.equals(CdbRole.USER);
        }
        return false;
    }

    /**
     * Cdb session string representation.
     *
     * @return session string representation
     */
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
