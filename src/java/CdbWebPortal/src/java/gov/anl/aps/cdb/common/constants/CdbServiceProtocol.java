/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.constants;

/**
 * CDB service protocol enum.
 */
public enum CdbServiceProtocol {

    HTTP("http"),
    HTTPS("https");

    private final String type;

    private CdbServiceProtocol(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static CdbServiceProtocol fromString(String type) {
        CdbServiceProtocol protocol = null;
        switch (type) {
            case "http":
                protocol = HTTP;
                break;
            case "https":
                protocol = HTTPS;
                break;
        }
        return protocol;
    }
}
