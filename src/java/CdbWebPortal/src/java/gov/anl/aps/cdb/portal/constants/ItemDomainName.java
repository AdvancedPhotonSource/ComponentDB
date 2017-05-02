/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDomainName {
        location("Location"),
        catalog("Catalog"),
        inventory("Inventory"),
        cable("Cable"),
        maarc("MAARC");
        
        public final static int LOCATION_ID = 1; 
        public final static int CATALOG_ID = 2; 
        public final static int INVENTORY_ID = 3; 
        public final static int CABLE_ID = 4; 
        public final static int MAARC_ID = 5; 

        private String value;

        private ItemDomainName(String value) {
            this.value = value;
        }               

        public final String getValue() {
            return value;
        }
    };
