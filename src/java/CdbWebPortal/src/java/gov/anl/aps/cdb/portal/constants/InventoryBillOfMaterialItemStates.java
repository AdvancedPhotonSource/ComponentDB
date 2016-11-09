/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum InventoryBillOfMaterialItemStates {
        placeholder("placeholder"),
        existingItem("existingItem"),
        newItem("newItem");

        private String value;

        private InventoryBillOfMaterialItemStates(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };
