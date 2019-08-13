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
  CONSTRAINT `user_user_group_fk1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `user_user_group_k2` (`user_group_id`),
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
  UNIQUE KEY `user_setting_u1` (`user_id`, `setting_type_id`),
  KEY `user_setting_k1` (`user_id`),
  CONSTRAINT `user_setting_fk1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `user_setting_k2` (`setting_type_id`),
  CONSTRAINT `user_setting_fk2` FOREIGN KEY (`setting_type_id`) REFERENCES `setting_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `entity_info`
--

DROP TABLE IF EXISTS `entity_info`;
CREATE TABLE `entity_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `owner_user_id` int(11) unsigned DEFAULT NULL,
  `owner_user_group_id` int(11) unsigned DEFAULT NULL,
  `is_group_writeable` bool DEFAULT NULL,
  `created_on_date_time` datetime NOT NULL,
  `created_by_user_id` int(11) unsigned NOT NULL,
  `last_modified_on_date_time` datetime NOT NULL,
  `last_modified_by_user_id` int(11) unsigned NOT NULL,
  `obsoleted_on_date_time` datetime DEFAULT NULL,
  `obsoleted_by_user_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `entity_info_k1` (`owner_user_id`),
  CONSTRAINT `entity_info_fk1` FOREIGN KEY (`owner_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  KEY `entity_info_k2` (`owner_user_group_id`),
  CONSTRAINT `entity_info_fk2` FOREIGN KEY (`owner_user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE,
  KEY `entity_info_k3` (`created_by_user_id`),
  CONSTRAINT `entity_info_fk3` FOREIGN KEY (`created_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  KEY `entity_info_k4` (`last_modified_by_user_id`),
  CONSTRAINT `entity_info_fk4` FOREIGN KEY (`last_modified_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE,
  KEY `entity_info_k5` (`obsoleted_by_user_id`),
  CONSTRAINT `entity_info_fk5` FOREIGN KEY (`obsoleted_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `attachment`
--

DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
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
  `log_topic_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `log_k1` (`entered_by_user_id`),
  CONSTRAINT `log_fk1` FOREIGN KEY (`entered_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE, 
  KEY `log_k2` (`log_topic_id`),
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
  CONSTRAINT `log_attachment_fk1` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `log_attachment_k2` (`attachment_id`),
  CONSTRAINT `log_attachment_fk2` FOREIGN KEY (`attachment_id`) REFERENCES `attachment` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
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
  `property_type_category_id` int(11) unsigned DEFAULT NULL,
  `property_type_handler_id` int(11) unsigned DEFAULT NULL,
  `default_value` varchar(64) DEFAULT NULL,
  `default_units` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_type_u1` (`name`),
  KEY `property_type_k1` (`property_type_category_id`),
  CONSTRAINT `property_type_fk1` FOREIGN KEY (`property_type_category_id`) REFERENCES `property_type_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL,
  KEY `property_type_k2` (`property_type_handler_id`),
  CONSTRAINT `property_type_fk2` FOREIGN KEY (`property_type_handler_id`) REFERENCES `property_type_handler` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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
-- Table `property_value`
--

DROP TABLE IF EXISTS `property_value`;
CREATE TABLE `property_value` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `property_type_id` int(11) unsigned NOT NULL,
  `tag` varchar(64) DEFAULT NULL,
  `value` varchar(256) DEFAULT NULL,
  `units` varchar(16) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `entered_on_date_time` datetime NOT NULL,
  `entered_by_user_id` int(11) unsigned NOT NULL,
  `is_user_writeable` bool NOT NULL DEFAULT 0,
  `is_dynamic` bool NOT NULL DEFAULT 0,
  `display_value` varchar(256) DEFAULT NULL,
  `target_value` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `property_value_k1` (`property_type_id`),
  CONSTRAINT `property_value_fk1` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `property_value_k2` (`entered_by_user_id`),
  CONSTRAINT `property_value_fk2` FOREIGN KEY (`entered_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE 
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
  `display_value` varchar(256) DEFAULT NULL,
  `target_value` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `property_value_history_k1` (`property_value_id`),
  CONSTRAINT `property_value_history_fk1` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `property_value_history_k2` (`entered_by_user_id`),
  CONSTRAINT `property_value_history_fk2` FOREIGN KEY (`entered_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE 
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

CREATE TABLE `resource_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `resource_type_category_id` int(11) unsigned DEFAULT NULL,
  `handler_name` varchar(64) DEFAULT NULL,
  `default_value` varchar(64) DEFAULT NULL,
  `default_units` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_type_u1` (`name`),
  KEY `resource_type_k1` (`resource_type_category_id`),
  CONSTRAINT `resource_type_fk1` FOREIGN KEY (`resource_type_category_id`) REFERENCES `resource_type_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_type_category`
--

DROP TABLE IF EXISTS `component_type_category`;
CREATE TABLE `component_type_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_type_category_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_type`
--

CREATE TABLE `component_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `component_type_category_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_type_u1` (`name`),
  KEY `component_type_k1` (`component_type_category_id`),
  CONSTRAINT `component_type_fk1` FOREIGN KEY (`component_type_category_id`) REFERENCES `component_type_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_type_property_type`
--

DROP TABLE IF EXISTS `component_type_property_type`;
CREATE TABLE `component_type_property_type` (
  `component_type_id` int(11) unsigned NOT NULL,
  `property_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`component_type_id`, `property_type_id`),
  KEY `component_type_property_type_k1` (`component_type_id`),
  CONSTRAINT `component_type_property_type_k1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_type_property_type_k2` (`property_type_id`),
  CONSTRAINT `component_type_property_type_fk2` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_type_resource_type`
--

DROP TABLE IF EXISTS `component_type_resource_type`;
CREATE TABLE `component_type_resource_type` (
  `component_type_id` int(11) unsigned NOT NULL,
  `resource_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`component_type_id`, `resource_type_id`),
  KEY `component_type_resource_type_k1` (`component_type_id`),
  CONSTRAINT `component_type_resource_type_k1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_type_resource_type_k2` (`resource_type_id`),
  CONSTRAINT `component_type_resource_type_fk2` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `connector_type_category`
--

DROP TABLE IF EXISTS `connector_type_category`;
CREATE TABLE `connector_type_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `connector_type_category_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `connector_type`
--

CREATE TABLE `connector_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `connector_type_category_id` int(11) unsigned DEFAULT NULL,
  `resource_type_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `connector_type_u1` (`name`),
  KEY `connector_type_k1` (`connector_type_category_id`),
  CONSTRAINT `connector_type_fk1` FOREIGN KEY (`connector_type_category_id`) REFERENCES `connector_type_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL,
  KEY `connector_type_k2` (`resource_type_id`),
  CONSTRAINT `connector_type_fk2` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `connector_type_property_type`
--

DROP TABLE IF EXISTS `connector_type_property_type`;
CREATE TABLE `connector_type_property_type` (
  `connector_type_id` int(11) unsigned NOT NULL,
  `property_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`connector_type_id`, `property_type_id`),
  KEY `connector_type_property_type_k1` (`connector_type_id`),
  CONSTRAINT `connector_type_property_type_k1` FOREIGN KEY (`connector_type_id`) REFERENCES `connector_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `connector_type_property_type_k2` (`property_type_id`),
  CONSTRAINT `connector_type_property_type_fk2` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `location_type`
--

CREATE TABLE `location_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `location_type_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `location`
--

CREATE TABLE `location` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `location_type_id` int(11) unsigned NOT NULL,
  `is_user_writeable` bool NOT NULL DEFAULT 0,
  `sort_order` float(10,2) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `location_u1` (`name`),
  KEY `location_k1` (`location_type_id`),
  CONSTRAINT `location_fk1` FOREIGN KEY (`location_type_id`) REFERENCES `location_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `location_link`
--

DROP TABLE IF EXISTS `location_link`;
CREATE TABLE `location_link` (
  `parent_location_id` int(11) unsigned NOT NULL,
  `child_location_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`parent_location_id`, `child_location_id`),
  KEY `location_link_k1` (`parent_location_id`),
  CONSTRAINT `location_link_fk1` FOREIGN KEY (`parent_location_id`) REFERENCES `location` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `location_link_k2` (`child_location_id`),
  CONSTRAINT `location_link_fk2` FOREIGN KEY (`child_location_id`) REFERENCES `location` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
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
-- Table `component`
--

DROP TABLE IF EXISTS `component`;
CREATE TABLE `component` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `model_number` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `component_type_id` int(11) unsigned NOT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_u1` (`name`, `model_number`),
  UNIQUE KEY `component_u2` (`model_number`),
  KEY `component_k1` (`component_type_id`),
  CONSTRAINT `component_fk1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`id`) ON UPDATE CASCADE,
  KEY `component_k2` (`entity_info_id`),
  CONSTRAINT `component_fk2` FOREIGN KEY (`entity_info_id`) REFERENCES `entity_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: Need trigger to prevent changing entity_info_id
--

--
-- Table `assembly_component`
--

DROP TABLE IF EXISTS `assembly_component`;
CREATE TABLE `assembly_component` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `assembly_id` int(11) unsigned NOT NULL,
  `component_id` int(11) unsigned NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `sort_order` float(10,2) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `assembly_component_k1` (`assembly_id`),
  CONSTRAINT `assembly_component_fk1` FOREIGN KEY (`assembly_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `assembly_component_k2` (`component_id`),
  CONSTRAINT `assembly_component_fk2` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_source`
--

DROP TABLE IF EXISTS `component_source`;
CREATE TABLE `component_source` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `component_id` int(11) unsigned NOT NULL,
  `source_id` int(11) unsigned NOT NULL,
  `part_number` varchar(64) DEFAULT NULL,
  `cost` float(10,2) unsigned DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `is_vendor` bool NOT NULL DEFAULT 0,
  `is_manufacturer` bool NOT NULL DEFAULT 0,
  `contact_info` varchar(64) DEFAULT NULL,
  `url` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_source_u1` (`component_id`, `source_id`),
  KEY `component_source_k1` (`component_id`),
  CONSTRAINT `component_source_fk1` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_source_k2` (`source_id`),
  CONSTRAINT `component_source_fk2` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_property`
--

DROP TABLE IF EXISTS `component_property`;
CREATE TABLE `component_property` (
  `component_id` int(11) unsigned NOT NULL,
  `property_value_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`component_id`, `property_value_id`),
  KEY `component_property_k1` (`component_id`),
  CONSTRAINT `component_property_fk1` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_property_k2` (`property_value_id`),
  CONSTRAINT `component_property_fk2` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_log`
--

DROP TABLE IF EXISTS `component_log`;
CREATE TABLE `component_log` (
  `component_id` int(11) unsigned NOT NULL,
  `log_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`component_id`, `log_id`),
  KEY `component_log_k1` (`component_id`),
  CONSTRAINT `component_log_fk1` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_log_k2` (`log_id`),
  CONSTRAINT `component_log_fk2` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_connector`
--

DROP TABLE IF EXISTS `component_connector`;
CREATE TABLE `component_connector` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `component_id` int(11) unsigned NOT NULL,
  `connector_type_id` int(11) unsigned NOT NULL,
  `label` varchar(64) DEFAULT NULL,
  `quantity` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_connector_u1` (`component_id`, `connector_type_id`, `label`),
  KEY `component_connector_k1` (`component_id`),
  CONSTRAINT `component_connector_fk1` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_connector_k2` (`connector_type_id`),
  CONSTRAINT `component_connector_fk2` FOREIGN KEY (`connector_type_id`) REFERENCES `connector_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_connector_property`
--

DROP TABLE IF EXISTS `component_connector_property`;
CREATE TABLE `component_connector_property` (
  `component_connector_id` int(11) unsigned NOT NULL,
  `property_value_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`component_connector_id`, `property_value_id`),
  KEY `component_connector_property_k1` (`component_connector_id`),
  CONSTRAINT `component_connector_property_fk1` FOREIGN KEY (`component_connector_id`) REFERENCES `component_connector` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_connector_property_k2` (`property_value_id`),
  CONSTRAINT `component_connector_property_fk2` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_resource`
--

DROP TABLE IF EXISTS `component_resource`;
CREATE TABLE `component_resource` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `component_id` int(11) unsigned NOT NULL,
  `resource_type_id` int(11) unsigned NOT NULL,
  `value` varchar(64) DEFAULT NULL,
  `units` varchar(16) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `component_connector_id` int(11) unsigned DEFAULT NULL,
  `is_provided` bool NOT NULL DEFAULT 0,
  `is_used_required` bool NOT NULL DEFAULT 0,
  `is_used_optional` bool NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_resource_u1` (`component_id`, `resource_type_id`, `value`),
  KEY `component_resource_k1` (`component_id`),
  CONSTRAINT `component_resource_fk1` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_resource_k2` (`resource_type_id`),
  CONSTRAINT `component_resource_fk2` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON UPDATE CASCADE,
  KEY `component_resource_k3` (`component_connector_id`),
  CONSTRAINT `component_resource_fk3` FOREIGN KEY (`component_connector_id`) REFERENCES `component_connector` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: CHECK constraint is not supported in MySQL.
-- Hence, we need triggers to verify that at least one of 
-- is_used_required/optional is NULL
--
-- Older versions of MySQL do not support SIGNAL statement, so we simply
-- issue erroneous command
--
DELIMITER $$
DROP TRIGGER IF EXISTS `check_is_used_constraint1` $$
CREATE TRIGGER `check_is_used_constraint1` BEFORE INSERT ON `component_resource` FOR EACH ROW 
  BEGIN
    IF (NEW.is_used_required <> 0 AND NEW.is_used_optional <> 0) THEN
      call raise_error('Constraint component_resource.is_used violated: both fields is_used_required and is_used_optional cannot be set at the same time.');
    END IF; 
  END
$$
DROP TRIGGER IF EXISTS `check_is_used_constraint2` $$
CREATE TRIGGER `check_is_used_constraint2` BEFORE UPDATE ON `component_resource` FOR EACH ROW 
  BEGIN
    IF (NEW.is_used_required <> 0 AND NEW.is_used_optional <> 0) THEN
      call raise_error('Constraint component_resource.is_used violated: both fields is_used_required and is_used_optional cannot be set at the same time.');
    END IF; 
  END
$$
DELIMITER ;

--
-- Table `assembly_component_connection`
--

DROP TABLE IF EXISTS `assembly_component_connection`;
CREATE TABLE `assembly_component_connection` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `first_assembly_component_id` int(11) unsigned NOT NULL,
  `first_component_connector_id` int(11) unsigned NOT NULL,
  `second_assembly_component_id` int(11) unsigned NOT NULL,
  `second_component_connector_id` int(11) unsigned NOT NULL,
  `link_assembly_component_id` int(11) unsigned DEFAULT NULL,
  `link_assembly_component_quantity` int(11) unsigned DEFAULT NULL,
  `label` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `assembly_component_connection_k1` (`first_assembly_component_id`),
  CONSTRAINT `assembly_component_connection_fk1` FOREIGN KEY (`first_assembly_component_id`) REFERENCES `assembly_component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `assembly_component_connection_k2` (`first_component_connector_id`),
  CONSTRAINT `assembly_component_connection_fk2` FOREIGN KEY (`first_component_connector_id`) REFERENCES `component_connector` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `assembly_component_connection_k3` (`second_assembly_component_id`),
  CONSTRAINT `assembly_component_connection_fk3` FOREIGN KEY (`second_assembly_component_id`) REFERENCES `assembly_component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `assembly_component_connection_k4` (`second_component_connector_id`),
  CONSTRAINT `assembly_component_connection_fk4` FOREIGN KEY (`second_component_connector_id`) REFERENCES `component_connector` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `assembly_component_connection_k5` (`link_assembly_component_id`),
  CONSTRAINT `assembly_component_connection_fk5` FOREIGN KEY (`link_assembly_component_id`) REFERENCES `assembly_component` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: We need triggers to verify that all assembly components belong to the
-- same assembly, and that connectors/assembly components belong to the
-- same component 
--

--
-- Table `component_instance`
--

DROP TABLE IF EXISTS `component_instance`;
CREATE TABLE `component_instance` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `component_id` int(11) unsigned NOT NULL,
  `location_id` int(11) unsigned DEFAULT NULL,
  `tag` varchar(64) DEFAULT NULL,
  `serial_number` varchar(32) DEFAULT NULL,
  `qr_id` int(11) unsigned DEFAULT NULL,
  `location_details` varchar(256) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_instance_u1` (`component_id`, `location_id`, `tag`),
  UNIQUE KEY `component_instance_u2` (`serial_number`,`component_id`),
  UNIQUE KEY `component_instance_u3` (`qr_id`),
  KEY `component_instance_k1` (`component_id`),
  CONSTRAINT `component_instance_fk1` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE,
  KEY `component_instance_k2` (`location_id`),
  CONSTRAINT `component_instance_fk2` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`) ON UPDATE CASCADE,
  KEY `component_instance_k3` (`entity_info_id`),
  CONSTRAINT `component_instance_fk3` FOREIGN KEY (`entity_info_id`) REFERENCES `entity_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: Need trigger to prevent changing entity_info_id; use
-- stored procedure for common code
--

--
-- Table `component_instance_property`
--

DROP TABLE IF EXISTS `component_instance_property`;
CREATE TABLE `component_instance_property` (
  `component_instance_id` int(11) unsigned NOT NULL,
  `property_value_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`component_instance_id`, `property_value_id`),
  KEY `component_instance_property_k1` (`component_instance_id`),
  CONSTRAINT `component_instance_property_fk1` FOREIGN KEY (`component_instance_id`) REFERENCES `component_instance` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_instance_property_k2` (`property_value_id`),
  CONSTRAINT `component_instance_property_fk2` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_instance_location_history`
--

DROP TABLE IF EXISTS `component_instance_location_history`;
CREATE TABLE `component_instance_location_history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `component_instance_id` int(11) unsigned NOT NULL,
  `location_id` int(11) unsigned DEFAULT NULL,
  `location_details` varchar(256) DEFAULT NULL,
  `entered_on_date_time` datetime NOT NULL,
  `entered_by_user_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `component_instance_location_k1` (`component_instance_id`),
  CONSTRAINT `component_instance_location_fk1` FOREIGN KEY (`component_instance_id`) REFERENCES `component_instance` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_instance_location_k2` (`location_id`),
  CONSTRAINT `component_instance_location_fk2` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`) ON UPDATE CASCADE,
  KEY `component_instance_location_k3` (`entered_by_user_id`),
  CONSTRAINT `component_instance_location_fk3` FOREIGN KEY (`entered_by_user_id`) REFERENCES `user_info` (`id`) ON UPDATE CASCADE 
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_instance_log`
--

DROP TABLE IF EXISTS `component_instance_log`;
CREATE TABLE `component_instance_log` (
  `component_instance_id` int(11) unsigned NOT NULL,
  `log_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`component_instance_id`, `log_id`),
  KEY `component_instance_log_k1` (`component_instance_id`),
  CONSTRAINT `component_instance_log_fk1` FOREIGN KEY (`component_instance_id`) REFERENCES `component_instance` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_instance_log_k2` (`log_id`),
  CONSTRAINT `component_instance_log_fk2` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `design`
--

DROP TABLE IF EXISTS `design`;
CREATE TABLE `design` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `design_u1` (`name`),
  KEY `design_k1` (`entity_info_id`),
  CONSTRAINT `design_fk1` FOREIGN KEY (`entity_info_id`) REFERENCES `entity_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: Need trigger to prevent changing entity_info_id
--

--
-- Table `design_link`
--

DROP TABLE IF EXISTS `design_link`;
CREATE TABLE `design_link` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `parent_design_id` int(11) unsigned NOT NULL,
  `child_design_id` int(11) unsigned NOT NULL,
  `label` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `design_link_u1` (`parent_design_id`, `child_design_id`, `label`),
  KEY `design_link_k1` (`parent_design_id`),
  CONSTRAINT `design_link_fk1` FOREIGN KEY (`parent_design_id`) REFERENCES `design` (`id`) ON UPDATE CASCADE,
  KEY `design_link_k2` (`child_design_id`),
  CONSTRAINT `design_link_fk2` FOREIGN KEY (`child_design_id`) REFERENCES `design` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `design_property`
--

DROP TABLE IF EXISTS `design_property`;
CREATE TABLE `design_property` (
  `design_id` int(11) unsigned NOT NULL,
  `property_value_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`design_id`, `property_value_id`),
  UNIQUE KEY `design_property_u1` (`design_id`, `property_value_id`),
  KEY `design_property_k1` (`design_id`),
  CONSTRAINT `design_property_fk1` FOREIGN KEY (`design_id`) REFERENCES `design` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_property_k2` (`property_value_id`),
  CONSTRAINT `design_property_fk2` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `design_log`
--

DROP TABLE IF EXISTS `design_log`;
CREATE TABLE `design_log` (
  `design_id` int(11) unsigned NOT NULL,
  `log_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`design_id`, `log_id`),
  KEY `design_log_k1` (`design_id`),
  CONSTRAINT `design_log_fk1` FOREIGN KEY (`design_id`) REFERENCES `design` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_log_k2` (`log_id`),
  CONSTRAINT `design_log_fk2` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `design_element`
--

DROP TABLE IF EXISTS `design_element`;
CREATE TABLE `design_element` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `parent_design_id` int(11) unsigned NOT NULL,
  `parent_design_element_id` int(11) unsigned DEFAULT NULL,
  `child_design_id` int(11) unsigned DEFAULT NULL,
  `component_id` int(11) unsigned DEFAULT NULL,
  `location_id` int(11) unsigned DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `sort_order` float(10,2) unsigned DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `design_element_u1` (`name`, `parent_design_id`),
  KEY `design_element_k1` (`parent_design_id`),
  CONSTRAINT `design_element_fk1` FOREIGN KEY (`parent_design_id`) REFERENCES `design` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_element_k2` (`parent_design_element_id`),
  CONSTRAINT `design_element_fk2` FOREIGN KEY (`parent_design_element_id`) REFERENCES `design_element` (`id`) ON UPDATE CASCADE,
  KEY `design_element_k3` (`child_design_id`),
  CONSTRAINT `design_element_fk3` FOREIGN KEY (`child_design_id`) REFERENCES `design` (`id`) ON UPDATE CASCADE,
  KEY `design_element_k4` (`component_id`),
  CONSTRAINT `design_element_fk4` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE,
  KEY `design_element_k5` (`location_id`),
  CONSTRAINT `design_element_fk5` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`) ON UPDATE CASCADE,
  KEY `design_element_k6` (`entity_info_id`),
  CONSTRAINT `design_element_fk6` FOREIGN KEY (`entity_info_id`) REFERENCES `entity_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: Need trigger to prevent changing entity_info_id
--

--
-- Table `design_element_property`
--

DROP TABLE IF EXISTS `design_element_property`;
CREATE TABLE `design_element_property` (
  `design_element_id` int(11) unsigned NOT NULL,
  `property_value_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`design_element_id`, `property_value_id`),
  UNIQUE KEY `design_element_property_u1` (`design_element_id`, `property_value_id`),
  KEY `design_element_property_k1` (`design_element_id`),
  CONSTRAINT `design_element_property_fk1` FOREIGN KEY (`design_element_id`) REFERENCES `design_element` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_element_property_k2` (`property_value_id`),
  CONSTRAINT `design_element_property_fk2` FOREIGN KEY (`property_value_id`) REFERENCES `property_value` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `design_element_component_instance`
--

DROP TABLE IF EXISTS `design_element_component_instance`;
CREATE TABLE `design_element_component_instance` (
  `design_element_id` int(11) unsigned NOT NULL,
  `component_instance_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`design_element_id`, `component_instance_id`),
  KEY `design_element_component_instance_k1` (`design_element_id`),
  CONSTRAINT `design_element_component_instance_fk1` FOREIGN KEY (`design_element_id`) REFERENCES `design_element` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_element_component_instance_k2` (`component_instance_id`),
  CONSTRAINT `design_element_component_instance_fk2` FOREIGN KEY (`component_instance_id`) REFERENCES `component_instance` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `design_element_log`
--

DROP TABLE IF EXISTS `design_element_log`;
CREATE TABLE `design_element_log` (
  `design_element_id` int(11) unsigned NOT NULL,
  `log_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`design_element_id`, `log_id`),
  KEY `design_element_log_k1` (`design_element_id`),
  CONSTRAINT `design_element_log_fk1` FOREIGN KEY (`design_element_id`) REFERENCES `design_element` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_element_log_k2` (`log_id`),
  CONSTRAINT `design_element_log_fk2` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `design_element_connection`
--

DROP TABLE IF EXISTS `design_element_connection`;
CREATE TABLE `design_element_connection` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `first_design_element_id` int(11) unsigned NOT NULL,
  `first_component_connector_id` int(11) unsigned DEFAULT NULL,
  `second_design_element_id` int(11) unsigned NOT NULL,
  `second_component_connector_id` int(11) unsigned DEFAULT NULL,
  `link_component_id` int(11) unsigned DEFAULT NULL,
  `link_component_quantity` int(11) unsigned DEFAULT NULL,
  `label` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `resource_type_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `design_element_connection_k1` (`first_design_element_id`),
  CONSTRAINT `design_element_connection_fk1` FOREIGN KEY (`first_design_element_id`) REFERENCES `design_element` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_element_connection_k2` (`first_component_connector_id`),
  CONSTRAINT `design_element_connection_fk2` FOREIGN KEY (`first_component_connector_id`) REFERENCES `component_connector` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_element_connection_k3` (`second_design_element_id`),
  CONSTRAINT `design_element_connection_fk3` FOREIGN KEY (`second_design_element_id`) REFERENCES `design_element` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_element_connection_k4` (`second_component_connector_id`),
  CONSTRAINT `design_element_connection_fk4` FOREIGN KEY (`second_component_connector_id`) REFERENCES `component_connector` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_element_connection_k5` (`link_component_id`),
  CONSTRAINT `design_element_connection_fk5` FOREIGN KEY (`link_component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE,
  KEY `design_element_connection_k6` (`resource_type_id`),
  CONSTRAINT `design_element_connection_fk6` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON UPDATE CASCADE 
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

