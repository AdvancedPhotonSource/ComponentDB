/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDomainName {
        location("Location"),
        catalog("Catalog"),
        inventory("Inventory"),
        // Deprecated Domain
        cable("Cable"),
        
        maarc("MAARC"),
        machineDesign("Machine Design"),
        cableCatalog("Cable Catalog"),
        cableInventory("Cable Inventory"), 
        cableDesign("Cable Design"); 
        
        public final static int LOCATION_ID = 1; 
        public final static int CATALOG_ID = 2; 
        public final static int INVENTORY_ID = 3; 
        
        // Deprecated Domain
        public final static int CABLE_ID = 4; 
        
        public final static int MAARC_ID = 5; 
        public final static int MACHINE_DESIGN_ID = 6; 
        public final static int CABLE_CATALOG_ID = 7; 
        public final static int CABLE_INVENTORY_ID = 8; 
        public final static int CABLE_DESIGN_ID = 9; 

        private String value;

        private ItemDomainName(String value) {
            this.value = value;
        }               

        public final String getValue() {
            return value;
        }
    };
