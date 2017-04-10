--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.3.0.sql` 


-- Add new configurations for item domain catalog columns in item element list. 
INSERT INTO `setting_type` (id, name, description, default_value) VALUES
(2048, "ItemDomainCatalog.ItemElementList.Display.ItemIdentifier2", "Display column for item identifier 2 in the item elements list.", "false"),
(2049, "ItemDomainCatalog.ItemElementList.Display.ItemType", "Display column for item type in the item elements list.", "false"), 
(2050, "ItemDomainCatalog.ItemElementList.Display.ItemCategory", "Display column for item category in the item elements list.", "false"),
(2051, "ItemDomainCatalog.ItemElementList.Display.Source" , "Display column for source in the item elements list.", "false"),
(2052, "ItemDomainCatalog.ItemElementList.Display.Project" , "Display column for project in the item elements list.", "false"),
(2053, "ItemDomainCatalog.ItemElementList.Display.Description" , "Display column for description in the item elements list.", "false");


-- Add new configurations for item domain inventory columns in item elements list. 
INSERT INTO `setting_type` (id, name, description, default_value) VALUES 
(3046, "ItemDomainInventory.ItemElementList.Display.Project" , "Display column for project in the item elements list.", "false"),
(3047, "ItemDomainInventory.ItemElementList.Display.Description" , "Display column for description in the item elements list.", "false"),
(3048, "ItemDomainInventory.ItemElementList.Display.QrId" , "Display column for qrid in the item elements list.", "false");
