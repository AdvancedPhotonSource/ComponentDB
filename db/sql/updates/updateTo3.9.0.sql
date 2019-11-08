--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.9.0.sql`
-- After executing this file backup and rebuild db from backup to apply other changes.
-- NOTE: Update support 

# Table definitions have been updated. 
### PLEASE REBUILD DB ###

# Utilize the new search settigns for all users
delete from user_group_setting where setting_type_id >= 15000 and setting_type_id < 16000;
delete from user_setting where setting_type_id >= 15000 and setting_type_id < 16000;

# Add history for the new item element History table

INSERT INTO item_element_history
(snapshot_element_name,
item_element_id,
snapshot_parent_name,
parent_item_id,
snapshot_contained_item_1_name,
contained_item_id1,
snapshot_contained_item_2_name,
contained_item_id2,
derived_from_item_element_id,
is_required,
description,
sort_order,
entered_on_date_time,
entered_by_user_id)
(SELECT
ie.name AS snapshot_element_name,
ie.id AS item_element_id,
parent_item.name AS snapshot_parent_name,
ie.parent_item_id AS parent_item_id,
CASE
    WHEN (contained_item_1.derived_from_item_id IS NOT NULL)
    THEN (select CONCAT(name, ' - [' ,contained_item_1.name, ']') from item where id = contained_item_1.derived_from_item_id)
    WHEN (contained_item_1.id is null) THEN NULL
    ELSE contained_item_1.name
END AS snapshot_contained_item_1_name,
ie.contained_item_id1,
CASE
    WHEN (contained_item_2.derived_from_item_id IS NOT NULL)
    THEN (select CONCAT(name, ' - [' ,contained_item_2.name, ']') from item where id = contained_item_2.derived_from_item_id)
    WHEN (contained_item_2.id is null) THEN NULL
    ELSE contained_item_2.name
END AS snapshot_contained_item_2_name,
ie.contained_item_id2,
ie.derived_from_item_element_id,
ie.is_required,
ie.description,
ie.sort_order,
ei.last_modified_on_date_time as entered_on_date_time,
ei.last_modified_by_user_id as entered_by_user_id
FROM item_element ie
INNER JOIN item parent_item on parent_item.id = parent_item_id
LEFT OUTER JOIN item contained_item_1 on contained_item_1.id = ie.contained_item_id1
LEFT OUTER JOIN item contained_item_2 on contained_item_2.id = ie.contained_item_id2
INNER JOIN entity_info ei on ei.id = ie.entity_info_id
WHERE (ie.name is not null));

