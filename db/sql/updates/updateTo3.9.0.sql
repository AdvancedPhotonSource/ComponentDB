--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.9.0.sql`
-- After executing this file backup and rebuild db from backup to apply other changes.
-- NOTE: Update support 

# Table definitions have been updated. 
### PLEASE REBUILD DB ###

--
-- Table `item_element_history`
--

DROP TABLE IF EXISTS `item_element_history`;
CREATE TABLE `item_element_history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `snapshot_element_name` varchar(64) NULL,
  `item_element_id` int(11) unsigned NULL,
  `snapshot_parent_name` varchar(256) NULL,
  `parent_item_id` int(11) unsigned NULL,
  `snapshot_contained_item_1_name` varchar(256) NULL,
  `contained_item_id1` int(11) unsigned DEFAULT NULL,
  `snapshot_contained_item_2_name` varchar(256) NULL,
  `contained_item_id2` int(11) unsigned DEFAULT NULL,
  `derived_from_item_element_id` int(11) unsigned DEFAULT NULL,
  `is_required` bool NULL DEFAULT 0,
  `description` varchar(256) DEFAULT NULL,
  `sort_order` float(10,2) unsigned DEFAULT NULL,
  `entered_on_date_time` datetime NOT NULL,
  `entered_by_user_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `item_element_history_k1` (`item_element_id`),
  KEY `item_element_history_k2` (`parent_item_id`),
  KEY `item_element_history_k3` (`contained_item_id1`),
  KEY `item_element_history_k4` (`contained_item_id2`),
  KEY `item_element_history_k5` (`derived_from_item_element_id`),
  CONSTRAINT `item_element_history_fk1` FOREIGN KEY (`item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `item_element_history_fk2` FOREIGN KEY (`parent_item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `item_element_history_fk3` FOREIGN KEY (`contained_item_id1`) REFERENCES `item` (`id`) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `item_element_history_fk4` FOREIGN KEY (`contained_item_id2`) REFERENCES `item` (`id`) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `item_element_history_fk5` FOREIGN KEY (`derived_from_item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE ON DELETE SET NULL 
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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

# Prepopulate list of categories for cable catalog. 
INSERT INTO `domain` VALUES
(7,'Cable Catalog', 'Item domain for managing the cable catalog items', NULL, NULL, NULL, 'Technical System');
INSERT INTO `item_category` (name, description, domain_id) (select name,description, 7 AS domain_id FROM item_category WHERE domain_id = 2);


# Add effective date for properties.
ALTER TABLE property_value ADD `effective_from_date_time` datetime NULL AFTER `entered_by_user_id`;
ALTER TABLE property_value ADD `effective_to_date_time` datetime NULL AFTER `effective_from_date_time`;
ALTER TABLE property_value_history ADD `effective_from_date_time` datetime NULL AFTER `entered_by_user_id`;
ALTER TABLE property_value_history ADD `effective_to_date_time` datetime NULL AFTER `effective_from_date_time`;
