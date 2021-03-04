/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.constants;

/**
 * CDB role enum.
 */
public enum CdbRole {

    USER("user"),
    ADMIN("admin"),
    MAINTAINER("maintainer");

    private final String type;

    private CdbRole(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static CdbRole fromString(String type) {
        CdbRole role = null;
        switch (type) {
            case "user":
                role = USER;
                break;
            case "admin":
                role = ADMIN;
                break;
        }
        return role;
    }
}
