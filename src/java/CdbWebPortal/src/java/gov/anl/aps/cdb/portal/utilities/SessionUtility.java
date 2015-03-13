package gov.anl.aps.cdb.portal.utilities;

import java.util.Map;
import java.util.Stack;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Session utility class.
 */
public class SessionUtility
{

    /**
     * Keys.
     */
    public static final String MESSAGES_KEY = "messages";
    public static final String USER_KEY = "user";
    public static final String VIEW_STACK_KEY = "viewStack";
    public static final String LAST_SESSION_ERROR_KEY = "lastSessionError";

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
        context.addMessage(MESSAGES_KEY, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
        setLastSessionError(detail);
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
        context.addMessage(MESSAGES_KEY, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
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
        context.addMessage(MESSAGES_KEY, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
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
        sessionMap.put(USER_KEY, user);
    }

    /**
     * Get user.
     *
     * @return user
     */
    public static Object getUser() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return sessionMap.get(USER_KEY);
    }

    public static void pushViewOnStack(String viewId) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Stack<String> viewStack = (Stack) sessionMap.get(VIEW_STACK_KEY);
        if (viewStack == null) {
            viewStack = new Stack<>();
            sessionMap.put(VIEW_STACK_KEY, viewStack);
        }
        viewStack.push(viewId);
    }

    public static String popViewFromStack() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Stack<String> viewStack = (Stack) sessionMap.get(VIEW_STACK_KEY);
        if (viewStack != null && !viewStack.empty()) {
            return viewStack.pop();
        }
        return null;
    }
    
    public static String getCurrentViewId() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getViewRoot().getViewId();
    }
    
    public static String getReferrerViewId() {
        String referrer = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("referer");
        if (referrer != null) {
            int beginViewId = referrer.indexOf("/views");
            if (beginViewId >= 0) {
                return referrer.substring(beginViewId);
            }
        }
        return null;
    }

    public static void clearSession() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.clear();
    }
    
    public static void navigateTo(String url) {
        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(context, null, url);
    }
    
    public static String getContextRoot() {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return contextPath;
    }
    
    public static int getSessionTimeoutInSeconds() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session.getMaxInactiveInterval();
    }
    
    public static void setLastSessionError(String error) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put(LAST_SESSION_ERROR_KEY, error);
    }

    public static String getLastSessionError() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return (String) sessionMap.get(LAST_SESSION_ERROR_KEY);
    }    
}
