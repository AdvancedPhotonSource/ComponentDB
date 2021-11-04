--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.13.0.sql`

SET @property_type_name = 'Control Interface'; 
INSERT INTO property_type(name, description, is_internal) VALUES (@property_type_name, 'Describes the interface to parent in the control hierarchy.', 1);

SET @property_type_id = (SELECT id FROM property_type WHERE name = @property_type_name);
INSERT INTO allowed_property_value (property_type_id, value) VALUES (@property_type_id, 'Direct Connection'); 

ALTER TABLE item_element ADD COLUMN `represents_item_element_id` int(11) unsigned DEFAULT NULL AFTER derived_from_item_element_id;
ALTER TABLE item_element_history ADD COLUMN `represents_item_element_id` int(11) unsigned DEFAULT NULL AFTER derived_from_item_element_id;

