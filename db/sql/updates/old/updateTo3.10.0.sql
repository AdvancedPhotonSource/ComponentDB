--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.10.0.sql`
-- After executing this file backup and rebuild db from backup to apply other changes.
-- NOTE: Update support 

# Enable part number as item_identifier1 for cable catalog domain
UPDATE `domain` SET item_identifier1_label = 'Part Number' where id = 7;
UPDATE `domain` SET item_identifier2_label = 'Alternate Name' where id = 7;

# Prepopulate list of categories for cable catalog. 
INSERT INTO `item_category` (name, description, domain_id) (select name,description, 9 AS domain_id FROM item_category WHERE domain_id = 2); 

# Add sort order for item_element_relationship
ALTER TABLE item_element_relationship ADD COLUMN first_sort_order float(10,2) unsigned DEFAULT NULL AFTER first_item_connector_id;
ALTER TABLE item_element_relationship ADD COLUMN second_sort_order float(10,2) unsigned DEFAULT NULL AFTER second_item_connector_id;

ALTER TABLE item_element_relationship_history ADD COLUMN first_sort_order float(10,2) unsigned DEFAULT NULL AFTER first_item_connector_id;
ALTER TABLE item_element_relationship_history ADD COLUMN second_sort_order float(10,2) unsigned DEFAULT NULL AFTER second_item_connector_id;

# Move assigned item to self element
update item inner join item_element as sie on item.id = sie.parent_item_id
inner join item_element as ie on item.id = ie.contained_item_id1
set sie.contained_item_id2 = ie.contained_item_id2
where domain_id = 6 and sie.name is null and ie.contained_item_id2 is not null;

# Clear assigned item from hierarchy representing element
update item inner join item_element as sie on item.id = sie.parent_item_id
inner join item_element as ie on item.id = ie.contained_item_id1
set ie.contained_item_id2 = null
where domain_id = 6 and sie.name is null and ie.contained_item_id2 is not null;

# Move over assigned history to the self element. 
insert into item_element_history (item_element_id, contained_item_id2, snapshot_contained_item_2_name, entered_on_date_time, entered_by_user_id)
select sie.id as item_element_id, ieh.contained_item_id2, snapshot_contained_item_2_name, entered_on_date_time, entered_by_user_id
from item inner join item_element as ie on item.id = ie.contained_item_id1 
inner join item_element as sie on item.id = sie.parent_item_id, item_element_history as ieh
where domain_id = 6 and sie.name is null and ieh.snapshot_contained_item_2_name is not null and ieh.item_element_id = ie.id;

# Clear redundant assigned item history from hierarchy representing element. 
update item_element_history as ieh inner join item_element as ie on ieh.item_element_id = ie.id inner join item on item.id = ie.contained_item_id1
set ieh.snapshot_contained_item_2_name = NULL, ieh.contained_item_id2 = NULL
where domain_id = 6;
