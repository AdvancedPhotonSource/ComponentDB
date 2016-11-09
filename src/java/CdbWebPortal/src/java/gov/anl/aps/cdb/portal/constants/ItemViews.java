/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemViews {
        simpleListView("simpleView"),
        advancedListView("advancedView");

        private String value;

        private ItemViews(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };
