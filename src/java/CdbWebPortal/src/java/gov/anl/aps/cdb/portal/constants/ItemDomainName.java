/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDomainName {
        location("Location"),
        catalog("Catalog"),
        inventory("Inventory");
        
        public final static int LOCATION_ID = 1; 
        public final static int CATALOG_ID = 2; 
        public final static int INVENTORY_ID = 3; 

        private String value;

        private ItemDomainName(String value) {
            this.value = value;
        }               

        public String getValue() {
            return value;
        }
    };
