/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * JSF bean for handling errors.
 */
@Named("errorBean")
@RequestScoped
public class ErrorBean implements Serializable {

    private String error = null;

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
