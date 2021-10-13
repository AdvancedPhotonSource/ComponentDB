/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum SystemPropertyTypeNames {
        cotrolInterface("Control Interface");  

        private String value;

        private SystemPropertyTypeNames(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };
