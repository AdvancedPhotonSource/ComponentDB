/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(ClientViewManagerController.controllerNamed)
@SessionScoped
public class ClientViewManagerController implements Serializable {

    public static final String controllerNamed = "clientViewManagerController";

    String viewUUID = null;
    String lastGeneratedViewUUID = null;
    CdbEntityController entityController = null;
    String currentUrl = null;
    List<String> lastUrlList = null;
    boolean skipAddLastUrl = false;

    String defaultReturnToUrl = "list?faces-redirect=true";

    public void generateNewViewUUID() {
        viewUUID = UUID.randomUUID().toString();
        String sessionUrl = SessionUtility.getRedirectToCurrentView();

        if (!skipAddLastUrl) {
            addLastUrl(sessionUrl);
        }
        skipAddLastUrl = false;

        currentUrl = sessionUrl;
        lastGeneratedViewUUID = viewUUID;
    }

    public String returnToPreviousPage() {
        if (lastUrlList == null || lastUrlList.size() < 2) {
            return defaultReturnToUrl;
        }
        int redirectIdx = lastUrlList.size() - 2;
        String redirectUrl = lastUrlList.get(redirectIdx);
        lastUrlList = lastUrlList.subList(0, redirectIdx);

        return redirectUrl;
    }

    public static void addAppropriateLastUrl(String lastUrl) {
        ClientViewManagerController thisController = (ClientViewManagerController) SessionUtility.findBean(controllerNamed);

        if (thisController != null) {
            thisController.addLastUrl(lastUrl);
            thisController.skipAddLastUrl = true;
        }
    }

    private void addLastUrl(String lastUrl) {
        if (this.lastUrlList == null) {
            this.lastUrlList = new ArrayList<>();
        }

        this.lastUrlList.add(lastUrl);
    }

    public String getViewUUID() {
        return viewUUID;
    }

    public String getLastGeneratedViewUUID() {
        String tmp = lastGeneratedViewUUID;
        lastGeneratedViewUUID = null;
        return tmp;
    }

    public String redrectToLastShownUrl() {
        return currentUrl;
    }

    public int getCurrentTimeHash() {
        return LocalDateTime.now().hashCode();
    }
}
