/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(ClientViewManagerController.controllerNamed)
@SessionScoped
public class ClientViewManagerController implements Serializable {

    public static final String controllerNamed = "clientViewManagerController";

    Map<String, String> viewUUID;
    CdbEntityController entityController = null;
    String currentUrl = null;
    List<String> lastUrlList = null;
    boolean skipAddLastUrl = false;

    String defaultReturnToUrl = "list?faces-redirect=true";

    public ClientViewManagerController() {
        viewUUID = new HashMap<>();
    }

    public void generateNewViewUUID(String singleTabViewKey) {
        addSessionUrl();

        String viewUUID = UUID.randomUUID().toString();
        this.viewUUID.put(singleTabViewKey, viewUUID);
    }

    public void addSessionUrl() {
        String sessionUrl = SessionUtility.getRedirectToCurrentView();

        if (!skipAddLastUrl) {
            addLastUrl(sessionUrl);
        }
        skipAddLastUrl = false;

        currentUrl = sessionUrl;
    }

    public String returnToPreviousPage() {
        if (lastUrlList == null || lastUrlList.size() < 2) {
            return defaultReturnToUrl;
        }
        int redirectIdx = lastUrlList.size() - 2;
        boolean valid = false;
        String redirectUrl = lastUrlList.get(redirectIdx);
        while (!valid) {
            if (redirectUrl.contains("edit.xhtml") || redirectUrl.contains("view.xhtml") || redirectUrl.contains("listView.xhtml")) {
                if (redirectUrl.contains("id=")) {
                    valid = true;
                } else {
                    redirectIdx = redirectIdx - 1;
                    if (redirectIdx < 0) {
                        redirectUrl = defaultReturnToUrl;
                        redirectIdx = 0;
                        break;
                    } else {
                        redirectUrl = lastUrlList.get(redirectIdx);
                    }
                }
            } else {
                valid = true;
            }
        }

        lastUrlList = lastUrlList.subList(0, redirectIdx);

        return redirectUrl;
    }

    private void addLastUrl(String lastUrl) {
        if (this.lastUrlList == null) {
            this.lastUrlList = new ArrayList<>();
        }

        this.lastUrlList.add(lastUrl);
    }

    public String getViewUUID(String singleTabViewKey) {
        return viewUUID.get(singleTabViewKey);
    }

    public String redrectToLastShownUrl() {
        return currentUrl;
    }

    public int getCurrentTimeHash() {
        return LocalDateTime.now().hashCode();
    }
}
