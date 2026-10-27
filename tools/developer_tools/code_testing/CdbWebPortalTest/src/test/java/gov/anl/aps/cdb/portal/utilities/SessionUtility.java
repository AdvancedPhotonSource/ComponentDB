/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

import gov.anl.aps.cdb.common.constants.CdbRole;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Test session state without a JSF container. */
public class SessionUtility {

    private static UserInfo user;
    private static Object role;
    private static String errorMessage;
    private static String infoMessage;
    private static String remoteCommand;

    public static UserInfo getUser() {
        return user;
    }

    public static void setUser(UserInfo value) {
        user = value;
    }

    public static Object getRole() {
        return role;
    }

    public static void setRole(Object value) {
        role = value;
    }

    public static boolean runningFaces() {
        return false;
    }

    public static Object findFacade(String facadeName) {
        return null;
    }

    public static void addErrorMessage(String summary, String detail) {
        errorMessage = detail;
    }

    public static void addWarningMessage(String summary, String detail) {
    }

    public static void addInfoMessage(String summary, String detail) {
        infoMessage = detail;
    }

    public static void executeRemoteCommand(String command) {
        remoteCommand = command;
    }

    public static void reset() {
        user = null;
        role = null;
        errorMessage = null;
        infoMessage = null;
        remoteCommand = null;
    }

    public static String getErrorMessage() {
        return errorMessage;
    }

    public static String getInfoMessage() {
        return infoMessage;
    }

    public static String getRemoteCommand() {
        return remoteCommand;
    }

    public static void setAdmin(boolean admin) {
        role = admin ? CdbRole.ADMIN : null;
    }
}
