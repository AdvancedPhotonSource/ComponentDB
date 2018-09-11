/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDomainMachineDesignCreateOptions {
        newMachineDesign("Create Placeholder"),
        template("Create From Template"),
        catalog("Create With Catalog Item"),
        assignExisting("Move Existing Machine Design");

        private String value;

        private ItemDomainMachineDesignCreateOptions(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };
