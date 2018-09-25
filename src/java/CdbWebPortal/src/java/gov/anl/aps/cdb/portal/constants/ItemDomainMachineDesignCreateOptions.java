/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDomainMachineDesignCreateOptions {
        newMachineDesign("Placeholder"),
        template("From Template"),
        catalog("From Catalog"),
        assignExisting("Move Existing Machine Design");

        private String value;

        private ItemDomainMachineDesignCreateOptions(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };
