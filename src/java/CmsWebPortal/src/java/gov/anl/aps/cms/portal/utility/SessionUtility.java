package gov.anl.aps.cms.portal.utility;

import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Session utility class.
 */
public class SessionUtility {

    /**
     * Keys.
     */
    public static final String MessagesKey = "messages";
    public static final String UserIdKey = "userId";

    /**
     * Constructor.
     */
    public SessionUtility() 
    {
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
    public static void addInfoMessage(String summary, String detail) 
    {
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
    public static String getRequestParameterValue(String parameterName) 
    {
        Map parameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        return (String) parameterMap.get(parameterName);
    }

    /**
     * Set user id.
     *
     * @param userId user id
     */
    public static void setUserId(Integer userId) {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put(UserIdKey, userId);
    }

    /**
     * Get user id.
     *
     * @return user id
     */
    public static Integer getUserId() {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return (Integer) sessionMap.get(UserIdKey);
    }    

}
