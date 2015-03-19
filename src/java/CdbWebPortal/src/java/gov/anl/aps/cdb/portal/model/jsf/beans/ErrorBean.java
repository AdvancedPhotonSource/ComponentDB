package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("errorBean")
@RequestScoped
public class ErrorBean implements Serializable {

    private String error = null;

    /**
     * Constructor.
     */
    public ErrorBean() {
    }

    public String getError() {
        if (error == null) {
            error = SessionUtility.getAndClearLastSessionError();
            SessionUtility.addErrorMessage("Error", error);
        }
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
}
