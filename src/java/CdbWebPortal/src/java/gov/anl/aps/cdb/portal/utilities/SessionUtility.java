/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 * Session utility class.
 */
public class SessionUtility {

    /**
     * Session keys.
     */
    public static final String MESSAGES_KEY = "messages";
    public static final String USER_KEY = "user";
    public static final String LAST_USERNAME_KEY = "lastUsername";
    public static final String VIEW_STACK_KEY = "viewStack";
    public static final String LAST_SESSION_ERROR_KEY = "lastSessionError";
    public static final String ROLE_KEY = "role";    
    private static final String SAFE_TRANSFER_CURRENT_KEY = "safeTransferCurrent"; 
    private static final String MODULE_NAME_LOOKUP = "java:module/ModuleName";
    private static final String JAVA_LOOKUP_START = "java:global/";
    private static String FACADE_LOOKUP_STRING_START = null;    

    private static final Logger logger = LogManager.getLogger(SessionUtility.class.getName());

    public SessionUtility() {
    }
    
    public static void addErrorMessage(String summary, String detail) {
        addErrorMessage(summary, detail, false);
    }

    public static void addErrorMessage(String summary, String detail, boolean isAjax) {
        addMessage(MESSAGES_KEY, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail), isAjax);
    }

    public static void addWarningMessage(String summary, String detail) {
        addWarningMessage(summary, detail, false);
    }
    
    public static void addWarningMessage(String summary, String detail, boolean isAjax) {
        addMessage(MESSAGES_KEY, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail), isAjax);
    }
    
    public static void addInfoMessage(String summary, String detail) {
        addInfoMessage(summary, detail, false);
    }

    public static void addInfoMessage(String summary, String detail, boolean isAjax) {
        addMessage(MESSAGES_KEY, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail), isAjax);
    }

    private static void addMessage(String clientId, FacesMessage message, boolean isAjax) {
        FacesContext context = FacesContext.getCurrentInstance();
        
        if (!isAjax) {
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
        
        context.addMessage(clientId, message);
        PrimeFaces.current().ajax().update(clientId);
    }
    
    private static Flash getFlash() {
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        ExternalContext externalContext = currentInstance.getExternalContext();
        Flash flash = externalContext.getFlash();
        return flash;
    }
    
    public static void setFlashValueIfSafe(String key, Object value) {
        Boolean sessionKey = (Boolean) getSessionKey(SAFE_TRANSFER_CURRENT_KEY);
        if (sessionKey != null && sessionKey) {
            setFlashValue(key, value);
            setSessionKey(SAFE_TRANSFER_CURRENT_KEY, false);
        }
    }
    
    public static void setFlashValue(String key, Object value) {
        Flash flash = getFlash();
        flash.put(key, value); 
    }
    
    public static Object getFlashValue(String key) {
        Flash flash = getFlash();
        return flash.get(key); 
    }

    public static String getRequestParameterValue(String parameterName) {
        Map parameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        return (String) parameterMap.get(parameterName);
    }

    public static void setUser(UserInfo user) {
        setSessionKey(USER_KEY, user);
    }

    public static UserInfo getUser() {
        Object user = getSessionKey(USER_KEY);
        
        return (UserInfo) user;
    }
    
    private static void setSessionKey(String key, Object value) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.remove(key);
        sessionMap.put(key, value); 
    }
    
    private static Object getSessionKey(String key) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return sessionMap.get(key);

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

    public static String getRedirectToCurrentView() {
        String currentView = getCurrentViewId();
        if (currentView.contains("?")) {
            currentView += "&";
        } else {
            currentView += "?";
        }
        currentView += "faces-redirect=true";

        return currentView;
    }
    
    public static String getCurrentViewIdWithCurrentHandlerTransfer() {
        FacesContext context = FacesContext.getCurrentInstance();
        String viewId = context.getViewRoot().getViewId();
        ViewHandler handler = context.getApplication().getViewHandler();
        UIViewRoot root = handler.createView(context, viewId);
        root.setViewId(viewId);
        
        setSessionKey(SAFE_TRANSFER_CURRENT_KEY, true);
        context.setViewRoot(root);
        
        return viewId; 
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

    public static void invalidateSession() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.invalidateSession();
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

    public static void redirectTo(String url) throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(externalContext.getRequestContextPath() + url);
    }

    public static HttpSession getCurrentSession() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session;
    }

    public static int getSessionTimeoutInSeconds() {
        HttpSession session = getCurrentSession();
        return session.getMaxInactiveInterval();
    }

    public static long getLastAccessedTime() {
        HttpSession session = getCurrentSession();
        return session.getLastAccessedTime();
    }

    public static void setLastSessionError(String error) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put(LAST_SESSION_ERROR_KEY, error);
    }

    public static String getLastSessionError() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return (String) sessionMap.get(LAST_SESSION_ERROR_KEY);
    }

    public static void clearLastSessionError() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.remove(LAST_SESSION_ERROR_KEY);
    }

    public static String getAndClearLastSessionError() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String error = (String) sessionMap.remove(LAST_SESSION_ERROR_KEY);
        return error;
    }

    public static void setRole(Object role) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put(ROLE_KEY, role);
    }

    public static Object getRole() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return sessionMap.get(ROLE_KEY);
    }

    public static void setLastUsername(String lastUsername) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put(LAST_USERNAME_KEY, (Object) lastUsername);
    }

    public static String getLastUsername() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return (String) sessionMap.get(LAST_USERNAME_KEY);
    }

    public static void executeRemoteCommand(String commandName) {
        PrimeFaces.current().executeScript(commandName);
    }

    /**
     * Finds a named bean for local use within the current bean.
     *
     * @param beanName Name of the named bean needed for further execution.
     * @return Named bean that has been requested.
     */
    @SuppressWarnings("unchecked")
    public static Object findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (Object) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }

    public static Object findFacade(String facadeName) {
        try {
            InitialContext context = new InitialContext();

            if (FACADE_LOOKUP_STRING_START == null) {
                String modName = (String) context.lookup(MODULE_NAME_LOOKUP);
                FACADE_LOOKUP_STRING_START = JAVA_LOOKUP_START + modName + "/";
            }

            return context.lookup(FACADE_LOOKUP_STRING_START + facadeName);
        } catch (NamingException ex) {
            logger.error(ex);
        }
        return null;
    }

    public static boolean runningFaces() {
        return FacesContext.getCurrentInstance() != null;
    }

}
