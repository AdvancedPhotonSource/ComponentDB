--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

--
-- Table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(16) NOT NULL,
  `first_name` varchar(16) NOT NULL,
  `last_name` varchar(16) NOT NULL,
  `middle_name` varchar(16) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `password` varchar(256) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_u1` (`username`),
  UNIQUE KEY `user_u2` (`first_name`, `last_name`, `middle_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_group_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `user_user_group`
--

DROP TABLE IF EXISTS `user_user_group`;
CREATE TABLE `user_user_group` (
  `user_id` int(11) unsigned NOT NULL,
  `user_group_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`user_id`, `user_group_id`),
  KEY `user_user_group_k1` (`user_id`),
  KEY `user_user_group_k2` (`user_group_id`),
  CONSTRAINT `user_user_group_fk1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `user_user_group_fk2` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `setting_type`
--

DROP TABLE IF EXISTS `setting_type`;
CREATE TABLE `setting_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `default_value` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `setting_type_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `user_setting`
--

DROP TABLE IF EXISTS `user_setting`;
CREATE TABLE `user_setting` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL,
  `setting_type_id` int(11) unsigned NOT NULL,
  `value` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_setting_u1` (`user_id`, `setting_type_id`, `value`),
  KEY `user_setting_k1` (`user_id`),
  KEY `user_setting_k2` (`setting_type_id`),
  CONSTRAINT `user_setting_fk1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `user_setting_fk2` FOREIGN KEY (`setting_type_id`) REFERENCES `setting_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `user_group_setting`
--

DROP TABLE IF EXISTS `user_group_setting`;
CREATE TABLE `user_group_setting` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_group_id` int(11) unsigned NOT NULL,
  `setting_type_id` int(11) unsigned NOT NULL,
  `value` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_group_setting_u1` (`user_group_id`, `setting_type_id`, `value`),
  KEY `user_group_setting_k1` (`user_group_id`),
  KEY `user_group_setting_k2` (`setting_type_id`),
  CONSTRAINT `user_group_setting_fk1` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `user_group_setting_fk2` FOREIGN KEY (`setting_type_id`) REFERENCES `setting_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `entity_info`
--

DROP TABLE IF EXISTS `entity_info`;
CREATE TABLE `entity_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `owner_user_id` int(11) unsigned DEFAULT NULL,
  `owner_user_group_id` int(11) unsigned DEFAULT NULL,
  `is_group_writeable` bool NULL DEFAULT 0,
  `created_on_date_time` datetime NOT NULL,
  `created_by_user_id` int(11) unsigned NOT NULL,
  `last_modified_on_date_time` datetime NOT NULL,
  `last_modified_by_user_id` int(11) unsigned NOT NULL,
  `obsoleted_on_date_time` datetime DEFAULT NULL,
  `obsoleted_by_user_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `entity_info_k1` (`owner_user_id`),
  KEY `entity_info_k2` (`owner_user_group_id`),
  KEY `entity_info_k3` (`created_by_user_id`),
  KEY `entity_info_k4` (`last_modified_by_user_id`),
  KEY `entity_info_k5` (`obsoleted_by_user_id`),
  CONSTRAINT `entity_info_fk1` FOREIGN KEY (`owner_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `entity_info_fk2` FOREIGN KEY (`owner_user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `entity_info_fk3` FOREIGN KEY (`created_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `entity_info_fk4` FOREIGN KEY (`last_modified_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `entity_info_fk5` FOREIGN KEY (`obsoleted_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `role_type`
--

DROP TABLE IF EXISTS `role_type`;
CREATE TABLE `role_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_type_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `user_id` int(11) unsigned NOT NULL,
  `role_type_id` int(11) unsigned NOT NULL,
  `user_group_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`user_id`,`role_type_id`,`user_group_id`),
  KEY `user_role_k1` (`user_id`),
  KEY `user_role_k2` (`role_type_id`),
  KEY `user_role_k3` (`user_group_id`),
  CONSTRAINT `user_role_fk1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `user_role_fk2` FOREIGN KEY (`role_type_id`) REFERENCES `role_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `user_role_fk3` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `list`
--

DROP TABLE IF EXISTS `list`;
CREATE TABLE `list` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `list_u1` (`entity_info_id`),
  KEY `list_k1` (`entity_info_id`),
  CONSTRAINT `list_fk1` FOREIGN KEY (`entity_info_id`) REFERENCES `entity_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `user_list`
--

DROP TABLE IF EXISTS `user_list`;
CREATE TABLE `user_list` (
  `user_id` int(11) unsigned NOT NULL,
  `list_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`user_id`, `list_id`),
  KEY `user_list_k1` (`user_id`),
  KEY `user_list_k2` (`list_id`),
  CONSTRAINT `user_list_fk1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `user_list_fk2` FOREIGN KEY (`list_id`) REFERENCES `list` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


--
-- Table `user_group_list`
--

DROP TABLE IF EXISTS `user_group_list`;
CREATE TABLE `user_group_list` (
  `user_group_id` int(11) unsigned NOT NULL,
  `list_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`user_group_id`, `list_id`),
  KEY `group_list_k1` (`user_group_id`),
  KEY `group_list_k2` (`list_id`),
  CONSTRAINT `group_list_fk1` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `group_list_fk2` FOREIGN KEY (`list_id`) REFERENCES `list` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `attachment`
--

DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `original_filename` varchar(256) NOT NULL, 
  `tag` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `attachment_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `log_topic`
--

DROP TABLE IF EXISTS `log_topic`;
CREATE TABLE `log_topic` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `log_topic_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `log`
--

DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `text` text NOT NULL,
  `entered_on_date_time` datetime NOT NULL,
  `entered_by_user_id` int(11) unsigned NOT NULL,
  `effective_from_date_time` datetime NULL,
  `effective_to_date_time` datetime NULL,
  `log_topic_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `log_k1` (`entered_by_user_id`),
  KEY `log_k2` (`log_topic_id`),
  CONSTRAINT `log_fk1` FOREIGN KEY (`entered_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `log_fk2` FOREIGN KEY (`log_topic_id`) REFERENCES `log_topic` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `log_attachment`
--

DROP TABLE IF EXISTS `log_attachment`;
CREATE TABLE `log_attachment` (
  `log_id` int(11) unsigned NOT NULL,
  `attachment_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`log_id`, `attachment_id`),
  KEY `log_attachment_k1` (`log_id`),
  KEY `log_attachment_k2` (`attachment_id`),
  CONSTRAINT `log_attachment_fk1` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `log_attachment_fk2` FOREIGN KEY (`attachment_id`) REFERENCES `attachment` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `log_level`
--

DROP TABLE IF EXISTS `log_level`;
CREATE TABLE `log_level` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `log_level_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `system_log`
--

DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log` (
  `log_id` int(11) unsigned NOT NULL,
  `log_level_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`log_id`, `log_level_id`),
  KEY `system_log_k1` (`log_id`),
  KEY `system_log_k2` (`log_level_id`),
  CONSTRAINT `system_log_fk1` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `system_log_fk2` FOREIGN KEY (`log_level_id`) REFERENCES `log_level` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `domain`
--

DROP TABLE IF EXISTS `domain`;
CREATE TABLE `domain` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `item_identifier1_label` varchar(32) DEFAULT NULL,
  `item_identifier2_label` varchar(32) DEFAULT NULL, 
  `item_type_label` varchar(32) DEFAULT NULL,
  `item_category_label` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `domain_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item`
--
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `domain_id` int(11) unsigned NOT NULL,
  `name` varchar(128) NOT NULL,
  `derived_from_item_id` int(11) unsigned DEFAULT NULL,
  `item_identifier1` varchar(128) DEFAULT NULL,
  `item_identifier2` varchar(128) DEFAULT NULL,
  `qr_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_u1` (`domain_id`, `name`, `item_identifier1`, `item_identifier2`, `derived_from_item_id`),
  UNIQUE KEY `item_u2` (`qr_id`),
  KEY `item_k1` (`domain_id`),
  KEY `item_k2` (`derived_from_item_id`),
  CONSTRAINT `item_fk1` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_fk2` FOREIGN KEY (`derived_from_item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_element`
--

DROP TABLE IF EXISTS `item_element`;
CREATE TABLE `item_element` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NULL,
  `parent_item_id` int(11) unsigned NOT NULL,
  `contained_item_id1` int(11) unsigned DEFAULT NULL,
  `contained_item_id2` int(11) unsigned DEFAULT NULL,
  `derived_from_item_element_id` int(11) unsigned DEFAULT NULL,
  `represents_item_element_id` int(11) unsigned DEFAULT NULL,
  `is_required` bool NULL DEFAULT 0,
  `is_housed` bool  NULL DEFAULT 1,
  `description` varchar(256) DEFAULT NULL,
  `sort_order` float(10,2) unsigned DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_element_u1` (`parent_item_id`, `name`, `derived_from_item_element_id`),
  UNIQUE KEY `item_element_u2` (`entity_info_id`),
  KEY `item_element_k1` (`parent_item_id`),
  KEY `item_element_k2` (`contained_item_id1`),
  KEY `item_element_k3` (`contained_item_id2`),
  KEY `item_element_k4` (`entity_info_id`),
  KEY `item_element_k5` (`derived_from_item_element_id`),
  CONSTRAINT `item_element_fk1` FOREIGN KEY (`parent_item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_fk2` FOREIGN KEY (`contained_item_id1`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_fk3` FOREIGN KEY (`contained_item_id2`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_fk4` FOREIGN KEY (`entity_info_id`) REFERENCES `entity_info` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_fk5` FOREIGN KEY (`derived_from_item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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
  `represents_item_element_id` int(11) unsigned DEFAULT NULL,
  `is_required` bool NULL DEFAULT 0,
  `is_housed` bool  NULL DEFAULT 1,
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

--
-- Table `item_element_log
--

DROP TABLE IF EXISTS `item_element_log`;
CREATE TABLE `item_element_log` (
  `item_element_id` int(11) unsigned NOT NULL,
  `log_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_element_id`, `log_id`),
  KEY `item_element_log_k1` (`item_element_id`),
  KEY `item_element_log_k2` (`log_id`),
  CONSTRAINT `item_element_log_fk1` FOREIGN KEY (`item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_log_fk2` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_element_list
--

DROP TABLE IF EXISTS `item_element_list`;
CREATE TABLE `item_element_list` (
  `item_element_id` int(11) unsigned NOT NULL,
  `list_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_element_id`, `list_id`),
  KEY `item_element_list_k1` (`item_element_id`),
  KEY `item_element_list_k2` (`list_id`),
  CONSTRAINT `item_element_list_fk1` FOREIGN KEY (`item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_list_fk2` FOREIGN KEY (`list_id`) REFERENCES `list` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_category`
--

DROP TABLE IF EXISTS `item_category`;
CREATE TABLE `item_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `domain_id` int(11) unsigned NOT NULL,
  `sort_order` float(10,2) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_category_u1` (`name`, `domain_id`),
  KEY `item_category_k1` (`domain_id`),
  CONSTRAINT `item_category_fk1` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_type`
--

DROP TABLE IF EXISTS `item_type`;
CREATE TABLE `item_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `domain_id` int(11) unsigned NOT NULL,
  `sort_order` float(10,2) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_type_u1` (`name`, `domain_id`),
  KEY `item_type_k1` (`domain_id`),
  CONSTRAINT `item_type_fk1` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_category_item_type`
--

DROP TABLE IF EXISTS `item_category_item_type`;
CREATE TABLE `item_category_item_type` (
  `item_category_id` int(11) unsigned NOT NULL,
  `item_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_type_id`, `item_category_id`),
  KEY `item_category_item_type_k1` (`item_category_id`),
  KEY `item_category_item_type_k2` (`item_type_id`),
  CONSTRAINT `item_category_item_type_fk1` FOREIGN KEY (`item_category_id`) REFERENCES `item_category` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_category_item_type_fk2` FOREIGN KEY (`item_type_id`) REFERENCES `item_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_item_category`
--

DROP TABLE IF EXISTS `item_item_category`;
CREATE TABLE `item_item_category` (
  `item_id` int(11) unsigned NOT NULL,
  `item_category_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_id`, `item_category_id`),
  KEY `item_item_category_k1` (`item_id`),
  KEY `item_item_category_k2` (`item_category_id`),
  CONSTRAINT `item_item_category_fk1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_item_category_fk2` FOREIGN KEY (`item_category_id`) REFERENCES `item_category` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_item_type`
--

DROP TABLE IF EXISTS `item_item_type`;
CREATE TABLE `item_item_type` (
  `item_id` int(11) unsigned NOT NULL,
  `item_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_id`, `item_type_id`),
  KEY `item_item_type_k1` (`item_id`),
  KEY `item_item_type_k2` (`item_type_id`),
  CONSTRAINT `item_item_type_fk1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_item_type_fk2` FOREIGN KEY (`item_type_id`) REFERENCES `item_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_project`
--

DROP TABLE IF EXISTS `item_project`;
CREATE TABLE `item_project` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_project_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_item_project`
--

DROP TABLE IF EXISTS `item_item_project`;
CREATE TABLE `item_item_project` (
  `item_id` int(11) unsigned NOT NULL,
  `item_project_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_id`, `item_project_id`),
  KEY `item_item_project_k1` (`item_id`),
  KEY `item_item_project_k2` (`item_project_id`),
  CONSTRAINT `item_item_project_fk1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_item_project_fk2` FOREIGN KEY (`item_project_id`) REFERENCES `item_project` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `entity_type`
--

DROP TABLE IF EXISTS `entity_type`;
CREATE TABLE `entity_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `entity_type_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_entity_type`
--

DROP TABLE IF EXISTS `item_entity_type`;
CREATE TABLE `item_entity_type` (
  `item_id` int(11) unsigned NOT NULL,
  `entity_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_id`, `entity_type_id`),
  KEY `item_entity_type_k1` (`item_id`),
  KEY `item_entity_type_k2` (`entity_type_id`),
  CONSTRAINT `item_entity_type_fk1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_entity_type_fk2` FOREIGN KEY (`entity_type_id`) REFERENCES `entity_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `allowed_child_entity_type`
--

DROP TABLE IF EXISTS `allowed_child_entity_type`;
CREATE TABLE `allowed_child_entity_type` (
  `parent_entity_type_id` int(11) unsigned NOT NULL,
  `child_entity_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`parent_entity_type_id`, `child_entity_type_id`),
  KEY `allowed_child_entity_type_k1` (`parent_entity_type_id`),
  KEY `allowed_child_entity_type_k2` (`child_entity_type_id`),
  CONSTRAINT `allowed_child_entity_type_fk1` FOREIGN KEY (`parent_entity_type_id`) REFERENCES `entity_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `allowed_child_entity_type_fk2` FOREIGN KEY (`child_entity_type_id`) REFERENCES `entity_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `source`
--

DROP TABLE IF EXISTS `source`;
CREATE TABLE `source` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `contact_info` varchar(64) DEFAULT NULL,
  `url` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `source_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_source`
--

DROP TABLE IF EXISTS `item_source`;
CREATE TABLE `item_source` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `item_id` int(11) unsigned NOT NULL,
  `source_id` int(11) unsigned NOT NULL,
  `part_number` varchar(64) DEFAULT NULL,
  `cost` float(10,2) unsigned DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `is_vendor` bool NOT NULL DEFAULT 0,
  `is_manufacturer` bool NOT NULL DEFAULT 0,
  `contact_info` varchar(64) DEFAULT NULL,
  `url` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_source_u1` (`item_id`, `source_id`),
  KEY `item_source_k1` (`item_id`),
  KEY `item_source_k2` (`source_id`),
  CONSTRAINT `item_source_fk1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_source_fk2` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `resource_type_category`
--

DROP TABLE IF EXISTS `resource_type_category`;
CREATE TABLE `resource_type_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_type_category_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `resource_type`
--

DROP TABLE IF EXISTS `resource_type`;
CREATE TABLE `resource_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `resource_type_category` int(11) unsigned DEFAULT NULL,
  `handler_name` varchar(64) DEFAULT NULL,
  `default_value` varchar(64) DEFAULT NULL,
  `default_units` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_type_u1` (`name`),
  KEY `resource_type_k1` (`resource_type_category`),
  CONSTRAINT `resource_type_fk1` FOREIGN KEY (`resource_type_category`) REFERENCES `resource_type_category` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `connector_type`
--

DROP TABLE IF EXISTS `connector_type`;
CREATE TABLE `connector_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `connector_type_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `connector`
--

DROP TABLE IF EXISTS `connector`;
CREATE TABLE `connector` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NULL,
  `description` varchar(256) DEFAULT NULL,
  `is_male` bool NOT NULL DEFAULT 0,
  `connector_type_id` int(11) unsigned DEFAULT NULL,
  `resource_type_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `connector_k1` (`connector_type_id`),
  KEY `connector_k2` (`resource_type_id`),
  CONSTRAINT `connector_fk1` FOREIGN KEY (`connector_type_id`) REFERENCES `connector_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `connector_fk2` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_connector`
--

DROP TABLE IF EXISTS `item_connector`;
CREATE TABLE `item_connector` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `item_id` int(11) unsigned NOT NULL,
  `connector_id` int(11) unsigned NOT NULL,
  `label` varchar(64) DEFAULT NULL,
  `quantity` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_connector_u1` (`item_id`, `connector_id`, `label`),
  KEY `item_connector_k1` (`item_id`),
  KEY `item_connector_k2` (`connector_id`),
  CONSTRAINT `item_connector_fk1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_connector_fk2` FOREIGN KEY (`connector_id`) REFERENCES `connector` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_resource`
--

DROP TABLE IF EXISTS `item_resource`;
CREATE TABLE `item_resource` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `item_id` int(11) unsigned NOT NULL,
  `resource_type_id` int(11) unsigned NOT NULL,
  `value` varchar(64) DEFAULT NULL,
  `units` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `item_connector_id` int(11) unsigned DEFAULT NULL,
  `is_provided` bool NULL DEFAULT 0,
  `is_used_required` bool NULL DEFAULT 0,
  `is_used_optional` bool NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_resource_u1` (`item_id`, `resource_type_id`, `value`),
  KEY `item_resource_k1` (`item_id`),
  KEY `item_resource_k2` (`resource_type_id`),
  KEY `item_resource_k3` (`item_connector_id`),
  CONSTRAINT `item_resource_fk1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_resource_fk2` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_resource_fk3` FOREIGN KEY (`item_connector_id`) REFERENCES `item_connector` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `relationship_type_handler`
--

DROP TABLE IF EXISTS `relationship_type_handler`;
CREATE TABLE `relationship_type_handler` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `relationship_type_handler_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `relationship_type`
--

DROP TABLE IF EXISTS `relationship_type`;
CREATE TABLE `relationship_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `relationship_type_handler_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `relationship_type_u1` (`name`),
  KEY `relationship_type_k1` (`relationship_type_handler_id`),
  CONSTRAINT `relationship_type_fk1` FOREIGN KEY (`relationship_type_handler_id`) REFERENCES `relationship_type_handler` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_element_relationship`
--

DROP TABLE IF EXISTS `item_element_relationship`;
CREATE TABLE `item_element_relationship` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `first_item_element_id` int(11) unsigned NOT NULL,
  `first_item_connector_id` int(11) unsigned DEFAULT NULL,
  `first_sort_order` float(10,2) unsigned DEFAULT NULL,
  `second_item_element_id` int(11) unsigned NULL,
  `second_item_connector_id` int(11) unsigned DEFAULT NULL,
  `second_sort_order` float(10,2) unsigned DEFAULT NULL,
  `relationship_id_for_parent` int(11) unsigned DEFAULT NULL,
  `relationship_type_id` int(11) unsigned NOT NULL,
  `link_item_element_id` int(11) unsigned DEFAULT NULL,
  `relationship_details` varchar(64) DEFAULT NULL,
  `resource_type_id` int(11) unsigned DEFAULT NULL,
  `label` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_element_relationship_u1` (`first_item_element_id`, `second_item_element_id`, `link_item_element_id`),
  KEY `item_element_relationship_k1` (`first_item_element_id`),
  KEY `item_element_relationship_k2` (`first_item_connector_id`),
  KEY `item_element_relationship_k3` (`second_item_element_id`),
  KEY `item_element_relationship_k4` (`second_item_connector_id`),
  KEY `item_element_relationship_k5` (`relationship_type_id`),
  KEY `item_element_relationship_k6` (`link_item_element_id`),
  KEY `item_element_relationship_k7` (`resource_type_id`),
  KEY `item_element_relationship_k8` (`relationship_id_for_parent`),
  CONSTRAINT `item_element_relationship_fk1` FOREIGN KEY (`first_item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_fk2` FOREIGN KEY (`first_item_connector_id`) REFERENCES `item_connector` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_fk3` FOREIGN KEY (`second_item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_fk4` FOREIGN KEY (`second_item_connector_id`) REFERENCES `item_connector` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_fk5` FOREIGN KEY (`relationship_type_id`) REFERENCES `relationship_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_fk6` FOREIGN KEY (`link_item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_fk7` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_fk8` FOREIGN KEY (`relationship_id_for_parent`) REFERENCES `item_element_relationship` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_element_relationship_history`
--

DROP TABLE IF EXISTS `item_element_relationship_history`;
CREATE TABLE `item_element_relationship_history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `item_element_relationship_id` int(11) unsigned NOT NULL,
  `first_item_element_id` int(11) unsigned NOT NULL,
  `first_item_connector_id` int(11) unsigned DEFAULT NULL,
  `first_sort_order` float(10,2) unsigned DEFAULT NULL,
  `second_item_element_id` int(11) unsigned NULL,
  `second_item_connector_id` int(11) unsigned DEFAULT NULL,
  `second_sort_order` float(10,2) unsigned DEFAULT NULL,
  `relationship_id_for_parent` int(11) unsigned DEFAULT NULL,
  `link_item_element_id` int(11) unsigned DEFAULT NULL,
  `relationship_details` varchar(64) DEFAULT NULL,
  `resource_type_id` int(11) unsigned DEFAULT NULL,
  `label` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `entered_on_date_time` datetime NOT NULL,
  `entered_by_user_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `item_element_relationship_history_k1` (`item_element_relationship_id`),
  KEY `item_element_relationship_history_k2` (`first_item_element_id`),
  KEY `item_element_relationship_history_k3` (`first_item_connector_id`),
  KEY `item_element_relationship_history_k4` (`second_item_element_id`),
  KEY `item_element_relationship_history_k5` (`second_item_connector_id`),
  KEY `item_element_relationship_history_k6` (`link_item_element_id`),
  KEY `item_element_relationship_history_k7` (`resource_type_id`),
  KEY `item_element_relationship_history_k8` (`entered_by_user_id`),
  KEY `item_element_relationship_history_k9` (`item_element_relationship_id`),
  CONSTRAINT `item_element_relationship_history_fk1` FOREIGN KEY (`item_element_relationship_id`) REFERENCES `item_element_relationship` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_history_fk2` FOREIGN KEY (`first_item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_history_fk3` FOREIGN KEY (`first_item_connector_id`) REFERENCES `item_connector` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_history_fk4` FOREIGN KEY (`second_item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_history_fk5` FOREIGN KEY (`second_item_connector_id`) REFERENCES `item_connector` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_history_fk6` FOREIGN KEY (`link_item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_history_fk7` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_history_fk8` FOREIGN KEY (`entered_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_history_fk9` FOREIGN KEY (`relationship_id_for_parent`) REFERENCES `item_element_relationship` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_type_handler`
--

DROP TABLE IF EXISTS `property_type_handler`;
CREATE TABLE `property_type_handler` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_type_handler_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_type_category`
--

DROP TABLE IF EXISTS `property_type_category`;
CREATE TABLE `property_type_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_type_category_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_type`
--

CREATE TABLE `property_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `prompt_description` varchar(256) DEFAULT NULL, 
  `property_type_category_id` int(11) unsigned DEFAULT NULL,
  `property_type_handler_id` int(11) unsigned DEFAULT NULL,
  `default_value` varchar(64) DEFAULT NULL,
  `default_units` varchar(16) DEFAULT NULL,
  `is_user_writeable` bool NULL DEFAULT 0,
  `is_dynamic` bool NULL DEFAULT 0,
  `is_internal` bool NULL DEFAULT 0,
  `is_active` bool NULL DEFAULT 1,
  `is_metadata_dynamic` bool NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_type_u1` (`name`),
  KEY `property_type_k1` (`property_type_category_id`),
  KEY `property_type_k2` (`property_type_handler_id`),
  CONSTRAINT `property_type_fk1` FOREIGN KEY (`property_type_category_id`) REFERENCES `property_type_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `property_type_fk2` FOREIGN KEY (`property_type_handler_id`) REFERENCES `property_type_handler` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_type_metadata`
--

CREATE TABLE `property_type_metadata` (
	`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `property_type_id` int(11) unsigned NOT NULL,
    `metadata_key` varchar(64) NOT NULL,
    `description` varchar(256) DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `property_type_metadata_u1` (`metadata_key`, `property_type_id`), 
    KEY `property_type_metadata_k1` (`property_type_id`),
    CONSTRAINT `property_type_metadata_fk1` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `allowed_property_metadata_value`
--

CREATE TABLE `allowed_property_metadata_value` (
    `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `property_type_metadata_id` int(11) unsigned NOT NULL,
    `metadata_value` varchar(255) NOT NULL, 
    `description` varchar(256) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `allowed_property_metadata_value_u1` (`property_type_metadata_id`,`metadata_value`), 
    KEY `allowed_property_metadata_value_k1` (`property_type_metadata_id`),
    CONSTRAINT `allowed_property_metadata_value_fk1` FOREIGN KEY (`property_type_metadata_id`) REFERENCES `property_type_metadata` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
)  ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8;


--
-- Table `allowed_property_value`
--

CREATE TABLE `allowed_property_value` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `property_type_id` int(11) unsigned NOT NULL,
  `value` varchar(64) DEFAULT NULL,
  `units` varchar(16) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `sort_order` float(10,2) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `allowed_property_value_u1` (`property_type_id`, `value`),
  KEY `allowed_property_value_k1` (`property_type_id`),
  CONSTRAINT `allowed_property_value_fk1` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `allowed_entity_type`
--

DROP TABLE IF EXISTS `allowed_entity_type`;
CREATE TABLE `allowed_entity_type` (
  `property_type_id` int(11) unsigned NOT NULL,
  `entity_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`property_type_id`, `entity_type_id`),
  KEY `allowed_entity_type_k1` (`property_type_id`),
  KEY `allowed_entity_type_k2` (`entity_type_id`),
  CONSTRAINT `allowed_entity_type_fk1` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `allowed_entity_type_fk2` FOREIGN KEY (`entity_type_id`) REFERENCES `entity_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `allowed_property_domain`
--

DROP TABLE IF EXISTS `allowed_property_domain`;
CREATE TABLE `allowed_property_domain` (
  `property_type_id` int(11) unsigned NOT NULL,
  `domain_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`property_type_id`, `domain_id`),
  KEY `allowed_property_domain_k1` (`property_type_id`),
  KEY `allowed_property_domain_k2` (`domain_id`),
  CONSTRAINT `allowed_property_domain_fk1` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `allowed_property_domain_fk2` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `allowed_entity_type_domain`
--

DROP TABLE IF EXISTS `allowed_entity_type_domain`;
CREATE TABLE `allowed_entity_type_domain` (
  `domain_id` int(11) unsigned NOT NULL,
  `entity_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`domain_id`, `entity_type_id`),
  KEY `allowed_entity_type_domain_k1` (`domain_id`),
  KEY `allowed_entity_type_domain_k2` (`entity_type_id`),
  CONSTRAINT `allowed_entity_type_domain_fk1` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `allowed_entity_type_domain_fk2` FOREIGN KEY (`entity_type_id`) REFERENCES `entity_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_value`
--

DROP TABLE IF EXISTS `property_value`;
CREATE TABLE `property_value` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `property_type_id` int(11) unsigned NOT NULL,
  `tag` varchar(64) DEFAULT NULL,
  `value` varchar(256) DEFAULT NULL,
  `text` text DEFAULT NULL, 
  `units` varchar(16) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `entered_on_date_time` datetime NOT NULL,
  `entered_by_user_id` int(11) unsigned NOT NULL,
  `effective_from_date_time` datetime NULL,
  `effective_to_date_time` datetime NULL,
  `is_user_writeable` bool NOT NULL DEFAULT 0,
  `is_dynamic` bool NOT NULL DEFAULT 0,
  `display_value` varchar(256) DEFAULT NULL,
  `target_value` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `property_value_k1` (`property_type_id`),
  KEY `property_value_k2` (`entered_by_user_id`),
  CONSTRAINT `property_value_fk1` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `property_value_fk2` FOREIGN KEY (`entered_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_metadata`
--

DROP TABLE IF EXISTS `property_metadata`;
CREATE TABLE `property_metadata` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `property_value_id` int(11) unsigned NOT NULL,
  `metadata_key` varchar(64) NOT NULL,
  `metadata_value` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_metadata_u1` (`property_value_id`, `metadata_key`),
  KEY `property_metadata_k1` (`property_value_id`),
  CONSTRAINT `property_metadata_fk1` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_value_history`
--

DROP TABLE IF EXISTS `property_value_history`;
CREATE TABLE `property_value_history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `property_value_id` int(11) unsigned NOT NULL,
  `tag` varchar(64) DEFAULT NULL,
  `value` varchar(256) DEFAULT NULL,
  `units` varchar(16) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `entered_on_date_time` datetime NOT NULL,
  `entered_by_user_id` int(11) unsigned NOT NULL,
  `effective_from_date_time` datetime NULL,
  `effective_to_date_time` datetime NULL,
  `display_value` varchar(256) DEFAULT NULL,
  `target_value` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `property_value_history_k1` (`property_value_id`),
  KEY `property_value_history_k2` (`entered_by_user_id`),
  CONSTRAINT `property_value_history_fk1` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `property_value_history_fk2` FOREIGN KEY (`entered_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_metadata_history`
--

DROP TABLE IF EXISTS `property_metadata_history`;
CREATE TABLE `property_metadata_history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `property_value_history_id` int(11) unsigned NOT NULL,
  `metadata_key` varchar(64) NOT NULL,
  `metadata_value` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_metadata_history_u1` (`property_value_history_id`, `metadata_key`),
  KEY `property_metadata_history_k1` (`property_value_history_id`),
  CONSTRAINT `property_metadata_history_fk1` FOREIGN KEY (`property_value_history_id`) REFERENCES `property_value_history` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_attachment`
--

DROP TABLE IF EXISTS `property_attachment`;
CREATE TABLE `property_attachment` (
  `property_value_id` int(11) unsigned NOT NULL,
  `attachment_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`property_value_id`, `attachment_id`),
  KEY `property_attachment_k1` (`property_value_id`),
  KEY `property_attachment_k2` (`attachment_id`),
  CONSTRAINT `property_attachment_fk1` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `property_attachment_fk2` FOREIGN KEY (`attachment_id`) REFERENCES `attachment` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_connector_property`
--

DROP TABLE IF EXISTS `item_connector_property`;
CREATE TABLE `item_connector_property` (
  `item_connector_id` int(11) unsigned NOT NULL,
  `property_value_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_connector_id`, `property_value_id`),
  KEY `item_connector_property_k1` (`item_connector_id`),
  KEY `item_connector_property_k2` (`property_value_id`),
  CONSTRAINT `item_connector_property_fk1` FOREIGN KEY (`item_connector_id`) REFERENCES `item_connector` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_connector_property_fk2` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_element_relationship_property`
--

DROP TABLE IF EXISTS `item_element_relationship_property`;
CREATE TABLE `item_element_relationship_property` (
  `item_element_relationship_id` int(11) unsigned NOT NULL,
  `property_value_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_element_relationship_id`, `property_value_id`),
  KEY `item_element_relationship_property_k1` (`item_element_relationship_id`),
  KEY `item_element_relationship_property_k2` (`property_value_id`),
  CONSTRAINT `item_element_relationship_property_fk1` FOREIGN KEY (`item_element_relationship_id`) REFERENCES `item_element_relationship` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_relationship_property_fk2` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `item_element_property`
--

DROP TABLE IF EXISTS `item_element_property`;
CREATE TABLE `item_element_property` (
  `item_element_id` int(11) unsigned NOT NULL,
  `property_value_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`item_element_id`, `property_value_id`),
  KEY `item_element_property_k1` (`item_element_id`),
  KEY `item_element_property_k2` (`property_value_id`),
  CONSTRAINT `item_element_property_fk1` FOREIGN KEY (`item_element_id`) REFERENCES `item_element` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `item_element_property_fk2` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `connector_property`
--

DROP TABLE IF EXISTS `connector_property`;
CREATE TABLE `connector_property` (
  `connector_id` int(11) unsigned NOT NULL,
  `property_value_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`connector_id`, `property_value_id`),
  KEY `connector_property_k1` (`connector_id`),
  KEY `connector_property_k2` (`property_value_id`),
  CONSTRAINT `connector_property_fk1` FOREIGN KEY (`connector_id`) REFERENCES `connector` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `connector_property_fk2` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- Note: CHECK constraint is not supported in MySQL.
-- Hence, we need triggers to verify that at least one of
-- is_used_required/optional is NULL
--
-- Older versions of MySQL do not support SIGNAL statement, so we simply
-- issue erroneous command
--
-- DELIMITER $$
-- DROP TRIGGER IF EXISTS `check_is_used_constraint1` $$
-- CREATE TRIGGER `check_is_used_constraint1` BEFORE INSERT ON `component_resource` FOR EACH ROW
--   BEGIN
--     IF (NEW.is_used_required <> 0 AND NEW.is_used_optional <> 0) THEN
--       call raise_error('Constraint component_resource.is_used violated: both fields is_used_required and is_used_optional cannot be set at the same time.');
--     END IF;
--   END
-- $$
-- DROP TRIGGER IF EXISTS `check_is_used_constraint2` $$
-- CREATE TRIGGER `check_is_used_constraint2` BEFORE UPDATE ON `component_resource` FOR EACH ROW
--   BEGIN
--     IF (NEW.is_used_required <> 0 AND NEW.is_used_optional <> 0) THEN
--       call raise_error('Constraint component_resource.is_used violated: both fields is_used_required and is_used_optional cannot be set at the same time.');
--     END IF;
--   END
-- $$
-- DELIMITER ;
