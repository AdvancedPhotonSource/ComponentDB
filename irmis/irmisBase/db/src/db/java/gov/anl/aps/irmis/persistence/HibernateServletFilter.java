/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This servlet filter's job is to make sure any DAOContext created by the application
 * has a reference to it stored in the http session. Then, on subsequent executions,
 * extract that same DAOContext from the http session, and make sure it is available
 * to the application. The DAOContext is created/accessed by the application in ThreadLocal
 * storage. So basically, this servlet filter copies the DAOContext between the http
 * session and the ThreadLocal storage.
 */
public class HibernateServletFilter implements Filter {

    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain) 
        throws IOException, ServletException {

        HttpSession userSession = ((HttpServletRequest)req).getSession();
        HttpSessionDAOContext httpContext = 
            (HttpSessionDAOContext)userSession.getAttribute("HibernateDAOContext");

        // if we have context in http session, attach it to current thread
        if (httpContext != null)
            DAOContext.attach(httpContext.getContext());
        
        try {
            chain.doFilter(req, res);   // execute users servlet
            
        } finally {
            // detach context from current thread (could be null)
            DAOContext context = DAOContext.detach();

            // add it to http session if it wasn't there already
            if (context != null && httpContext == null) {
                userSession.setAttribute("HibernateDAOContext", new HttpSessionDAOContext(context));
            }
        }

    }

    public void init(FilterConfig filterConfig) {

    }

    public void destroy() {

    }

    /**
     * A trivial wrapper for DAOContext which allows us to detect when
     * the Http Session goes away. We use this valueUnbound event to
     * close the DAOContext session (which is the Hibernate session).
     * 
     */
    class HttpSessionDAOContext implements HttpSessionBindingListener {

        private DAOContext context;

        private HttpSessionDAOContext() {};

        public HttpSessionDAOContext(DAOContext context) {
            this.context = context;
        }

        public DAOContext getContext() {
            return this.context;
        }

        public void valueBound(HttpSessionBindingEvent e) {
            
        }
        public void valueUnbound(HttpSessionBindingEvent e) {
            // Close the hibernate session when http session goes away
            if (context != null)
                context.closeSession();
        }

    }

}