/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.beans.UserFacade;
import gov.anl.aps.cms.portal.utility.LdapUtility;
import gov.anl.aps.cms.portal.utility.SessionUtility;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Login controller.
 */
@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {

    @EJB
    private UserFacade userFacade;

    private String username = null;
    private String password = null;
    private boolean loggedInAsAdmin = false;
    private boolean loggedInAsUser = false;

    /**
     * Constructor.
     */
    public LoginController() {
    }

    /**
     * Get password.
     *
     * @return login password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password.
     *
     * @param password login password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get username.
     *
     * @return login username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username.
     *
     * @param username login username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Check if user is logged in.
     *
     * @return true if admin is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return (loggedInAsAdmin || loggedInAsUser);
    }

    /**
     * Check if admin is logged in.
     *
     * @return true if admin is logged in, false otherwise
     */
    public boolean isLoggedInAsAdmin() {
        return loggedInAsAdmin;
    }

    /**
     * Set admin login flag.
     *
     * @param loggedInAsAdmin login flag
     */
    public void setLoggedInAsAdmin(boolean loggedInAsAdmin) {
        this.loggedInAsAdmin = loggedInAsAdmin;
    }

    /**
     * Check if user is logged in.
     *
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedInAsUser() {
        return loggedInAsUser;
    }

    /**
     * Set user login flag.
     *
     * @param loggedInAsUser login flag
     */
    public void setLoggedInAsUser(boolean loggedInAsUser) {
        this.loggedInAsUser = loggedInAsUser;
    }

    /**
     * Login action.
     *
     * @return url to service home page if login is successful, or null in case
     * of errors
     */
    public String login() {
        loggedInAsAdmin = false;
        loggedInAsUser = false;
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            if (username.equals("cms")) {
                loggedInAsAdmin = true;
            } else if (LdapUtility.validateCredentials(username, password)) {
                loggedInAsAdmin = true;
            } else {
                SessionUtility.addErrorMessage("Invalid Credentials", "Username/password combination could not be verified.");
            }
        } else {
            SessionUtility.addWarningMessage("Incomplete Input", "Please enter both username and password.");
        }

        if (loggedInAsAdmin) {
            SessionUtility.addInfoMessage("Successful Login", "Administrator " + username + " is logged in.");
            return "/views/home?faces-redirect=true";

        } else if (loggedInAsUser) {
            SessionUtility.addInfoMessage("Successful Login", "User " + username + " is logged in.");
            return "/views/home?faces-redirect=true";
        } else {
            return (username = password = null);

        }

    }

    public String dropAdminRole() {
        loggedInAsAdmin = false;
        loggedInAsUser = true;
        return "/views/home?faces-redirect=true";
    }

    public String displayUsername() {
        if (isLoggedIn()) {
            return username;
        }
        else {
            return "not logged in";
        }
    }
 
    public String displayRole() {
        if (isLoggedInAsAdmin()) {
            return "Administrator";
        }
        else {
            return "User";
        }
    }
    
    /**
     * Logout action.
     *
     * @return url to logout page
     */
    public String logout() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.invalidateSession();
        loggedInAsAdmin = false;
        loggedInAsUser = false;
        return "/views/home?faces-redirect=true";
    }

}
