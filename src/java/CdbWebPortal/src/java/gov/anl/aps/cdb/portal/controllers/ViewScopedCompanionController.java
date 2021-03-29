/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author darek
 */
@Named(ViewScopedCompanionController.controllerNamed)
@ViewScoped
public class ViewScopedCompanionController implements Serializable {

    public static final String controllerNamed = "viewScopedCompanionController";   

    @PreDestroy
    public void destdeltet() {
        for (String key : currentMap.keySet()) {
            Object get = currentMap.get(key);
            if (get != null) {
                SessionUtility.setFlashValue(key, get);
            }
        }
    }

    Map<String, Object> currentMap = new HashMap<>();

    public static ViewScopedCompanionController getInstance() {
        Object findBean = SessionUtility.findBean(controllerNamed);
        return (ViewScopedCompanionController) findBean;
    }

    public Object getCurrent(String key) {
        return currentMap.get(key);
    }

    public void setCurrent(Object current, String key) {
        currentMap.put(key, current);
    }

}
