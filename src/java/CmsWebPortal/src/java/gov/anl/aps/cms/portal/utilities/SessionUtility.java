package gov.anl.aps.cms.portal.utilities;

import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Session utility class.
 */
public class SessionUtility
{

    /**
     * Keys.
     */
    public static final String MessagesKey = "messages";
    public static final String UserKey = "user";

    /**
     * Constructor.
     */
    public SessionUtility() {
    }

    /**
     * Add error message.
     *
     * @param summary message summary
     * @param detail detailed message
     */
    public static void addErrorMessage(String summary, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(MessagesKey, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    /**
     * Add warning message.
     *
     * @param summary message summary
     * @param detail detailed message
     */
    public static void addWarningMessage(String summary, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(MessagesKey, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
    }

    /**
     * Add info message.
     *
     * @param summary message summary
     * @param detail detailed message
     */
    public static void addInfoMessage(String summary, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(MessagesKey, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    /**
     * Get request parameter value.
     *
     * @param parameterName parameter name
     * @return parameter value
     */
    public static String getRequestParameterValue(String parameterName) {
        Map parameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        return (String) parameterMap.get(parameterName);
    }

    /**
     * Set user.
     *
     * @param user user
     */
    public static void setUser(Object user) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put(UserKey, user);
    }

    /**
     * Get user.
     *
     * @return user
     */
    public static Object getUser() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return sessionMap.get(UserKey);
    }

    public static String getCurrentView() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getViewRoot().getViewId();
    }

    public static void clearSession() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.clear();
    }
}
