/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDomainName {
        inventory("Inventory"),
        catalog("Catalog"),
        location("Location"),
        cable("Cable");

        private String value;

        private ItemDomainName(String value) {
            this.value = value;
        }

        public final String getValue() {
            return value;
        }
    };
