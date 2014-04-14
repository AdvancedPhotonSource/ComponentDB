--
-- Table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(16) NOT NULL,
  `first_name` varchar(16) NOT NULL,
  `last_name` varchar(16) NOT NULL,
  `middle_name` varchar(16) DEFAULT NULL,
  `email` varchar(16) DEFAULT NULL,
  `password` varchar(16) DEFAULT NULL,
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
  CONSTRAINT `user_user_group_fk1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `user_user_group_k2` (`user_group_id`),
  CONSTRAINT `user_user_group_user_group_fk2` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `setting_type`
--

DROP TABLE IF EXISTS `setting_type`;
CREATE TABLE `setting_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
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
  CONSTRAINT `user_setting_fk1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
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
  CONSTRAINT `entity_info_fk1` FOREIGN KEY (`owner_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE,
  KEY `entity_info_k2` (`owner_user_group_id`),
  CONSTRAINT `entity_info_fk2` FOREIGN KEY (`owner_user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE,
  KEY `entity_info_k3` (`created_by_user_id`),
  CONSTRAINT `entity_info_fk3` FOREIGN KEY (`created_by_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE,
  KEY `entity_info_k4` (`last_modified_by_user_id`),
  CONSTRAINT `entity_info_fk4` FOREIGN KEY (`last_modified_by_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE,
  KEY `entity_info_k5` (`obsoleted_by_user_id`),
  CONSTRAINT `entity_info_fk5` FOREIGN KEY (`obsoleted_by_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
--
-- Table `log`
--

DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `text` text NOT NULL,
  `created_on_date_time` datetime NOT NULL,
  `created_by_user_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `log_k1` (`created_by_user_id`),
  CONSTRAINT `log_fk1` FOREIGN KEY (`created_by_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE 
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_type_u1` (`name`),
  KEY `property_type_k1` (`property_type_category_id`),
  CONSTRAINT `property_type_fk1` FOREIGN KEY (`property_type_category_id`) REFERENCES `property_type_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `connector_type_u1` (`name`),
  KEY `connector_type_k1` (`connector_type_category_id`),
  CONSTRAINT `connector_type_fk1` FOREIGN KEY (`connector_type_category_id`) REFERENCES `connector_type_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `connector_type_property`
--

CREATE TABLE `connector_type_property` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `connector_type_id` int(11) unsigned NOT NULL,
  `property_type_id` int(11) unsigned NOT NULL,
  `value` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `connector_type_property_u1` (`connector_type_id`, `property_type_id`, `value`),
  KEY `connector_type_property_k1` (`connector_type_id`),
  CONSTRAINT `connector_type_property_fk1` FOREIGN KEY (`connector_type_id`) REFERENCES `connector_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `connector_type_property_k2` (`property_type_id`),
  CONSTRAINT `connector_type_property_fk2` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE
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
  `location_type_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `location_u1` (`name`),
  KEY `location_k1` (`location_type_id`),
  CONSTRAINT `location_fk1` FOREIGN KEY (`location_type_id`) REFERENCES `location_type` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `source_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_state`
--

DROP TABLE IF EXISTS `component_state`;
CREATE TABLE `component_state` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_state_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component`
--

DROP TABLE IF EXISTS `component`;
CREATE TABLE `component` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `component_state_id` int(11) unsigned NOT NULL,
  `documentation_uri` varchar(256) DEFAULT NULL,
  `estimated_cost` float(10,2) DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_u1` (`name`),
  KEY `component_k1` (`component_state_id`),
  CONSTRAINT `component_fk1` FOREIGN KEY (`component_state_id`) REFERENCES `component_state` (`id`) ON UPDATE CASCADE,
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
  `quantity` int(11) unsigned DEFAULT 1,
  `description` varchar(256) DEFAULT NULL,
  `priority` float(10,2) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `assembly_component_k1` (`assembly_id`),
  CONSTRAINT `assembly_component_fk1` FOREIGN KEY (`assembly_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `assembly_component_k2` (`component_id`),
  CONSTRAINT `assembly_component_fk2` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `collection`
--

DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `parent_collection_id` int(11) unsigned DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `collection_u1` (`name`),
  KEY `collection_k1` (`parent_collection_id`),
  CONSTRAINT `collection_fk1` FOREIGN KEY (`parent_collection_id`) REFERENCES `collection` (`id`) ON UPDATE CASCADE,
  KEY `collection_k2` (`entity_info_id`),
  CONSTRAINT `collection_fk2` FOREIGN KEY (`entity_info_id`) REFERENCES `entity_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: Need trigger to prevent changing entity_info_id
--

--
-- Table `collection_link`
--

DROP TABLE IF EXISTS `collection_link`;
CREATE TABLE `collection_link` (
  `parent_collection_id` int(11) unsigned NOT NULL,
  `child_collection_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`parent_collection_id`, `child_collection_id`),
  KEY `collection_link_k1` (`parent_collection_id`),
  CONSTRAINT `collection_link_fk1` FOREIGN KEY (`parent_collection_id`) REFERENCES `collection` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `collection_link_fk2` FOREIGN KEY (`child_collection_id`) REFERENCES `collection` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
--
-- Table `collection_component`
--

DROP TABLE IF EXISTS `collection_component`;
CREATE TABLE `collection_component` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `collection_id` int(11) unsigned NOT NULL,
  `component_id` int(11) unsigned NOT NULL,
  `quantity` int(11) unsigned DEFAULT 1,
  `description` varchar(256) DEFAULT NULL,
  `tag` varchar(64) DEFAULT NULL,
  `priority` float(10,2) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `collection_component_u1` (`collection_id`, `component_id`, `tag`),
  KEY `collection_component_k1` (`collection_id`),
  CONSTRAINT `collection_component_fk1` FOREIGN KEY (`collection_id`) REFERENCES `collection` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `collection_component_k2` (`component_id`),
  CONSTRAINT `collection_component_fk2` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_source_u1` (`component_id`, `source_id`),
  KEY `component_source_k1` (`component_id`),
  CONSTRAINT `component_source_fk1` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_source_k2` (`source_id`),
  CONSTRAINT `component_source_fk2` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_component_type`
--

DROP TABLE IF EXISTS `component_component_type`;
CREATE TABLE `component_component_type` (
  `component_id` int(11) unsigned NOT NULL,
  `component_type_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`component_id`, `component_type_id`),
  KEY `component_component_type_k1` (`component_id`),
  CONSTRAINT `component_component_type_fk1` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_component_type_k2` (`component_type_id`),
  CONSTRAINT `component_component_type_fk2` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_property`
--

DROP TABLE IF EXISTS `component_property`;
CREATE TABLE `component_property` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `component_id` int(11) unsigned NOT NULL,
  `property_type_id` int(11) unsigned NOT NULL,
  `value` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_property_u1` (`component_id`, `property_type_id`, `value`),
  KEY `component_property_k1` (`component_id`),
  CONSTRAINT `component_property_fk1` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_property_k2` (`property_type_id`),
  CONSTRAINT `component_property_fk2` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
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
-- Table `component_connector_resource`
--

DROP TABLE IF EXISTS `component_connector_resource`;
CREATE TABLE `component_connector_resource` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `component_connector_id` int(11) unsigned NOT NULL,
  `resource_type_id` int(11) unsigned NOT NULL,
  `value` varchar(64) NOT NULL,
  `quantity` int(11) unsigned DEFAULT NULL,
  `is_provided` bool NOT NULL DEFAULT 0,
  `is_used_required` bool NOT NULL DEFAULT 0,
  `is_used_optional` bool NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_connector_resource_u1` (`component_connector_id`, `resource_type_id`, `value`),
  KEY `component_connector_resource_k1` (`component_connector_id`),
  CONSTRAINT `component_connector_resource_fk1` FOREIGN KEY (`component_connector_id`) REFERENCES `component_connector` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `component_connector_resource_k2` (`resource_type_id`),
  CONSTRAINT `component_connector_resource_fk2` FOREIGN KEY (`resource_type_id`) REFERENCES `resource_type` (`id`) ON UPDATE CASCADE
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
CREATE TRIGGER `check_is_used_constraint1` BEFORE INSERT ON `component_connector_resource` FOR EACH ROW 
  BEGIN
    IF (NEW.is_used_required <> 0 AND NEW.is_used_optional <> 0) THEN
      call raise_error('Constraint component_connector_resource.is_used violated: both fields is_used_required and is_used_optional cannot be set at the same time.');
    END IF; 
  END
$$
DROP TRIGGER IF EXISTS `check_is_used_constraint2` $$
CREATE TRIGGER `check_is_used_constraint2` BEFORE UPDATE ON `component_connector_resource` FOR EACH ROW 
  BEGIN
    IF (NEW.is_used_required <> 0 AND NEW.is_used_optional <> 0) THEN
      call raise_error('Constraint component_connector_resource.is_used violated: both fields is_used_required and is_used_optional cannot be set at the same time.');
    END IF; 
  END
$$
DELIMITER ;


--
-- Table `collection_log`
--

DROP TABLE IF EXISTS `collection_log`;
CREATE TABLE `collection_log` (
  `collection_id` int(11) unsigned NOT NULL,
  `log_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`collection_id`, `log_id`),
  KEY `collection_log_k1` (`collection_id`),
  CONSTRAINT `collection_log_fk1` FOREIGN KEY (`collection_id`) REFERENCES `collection` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `collection_log_k2` (`log_id`),
  CONSTRAINT `collection_log_fk2` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `component_instance`
--

DROP TABLE IF EXISTS `component_instance`;
CREATE TABLE `component_instance` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `component_id` int(11) unsigned NOT NULL,
  `location_id` int(11) unsigned NOT NULL,
  `serial_number` varchar(16) DEFAULT NULL,
  `quantity` int(11) unsigned DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_instance_u1` (`component_id`, `location_id`),
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
  `parent_design_id` int(11) unsigned DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `design_u1` (`name`),
  KEY `design_k1` (`parent_design_id`),
  CONSTRAINT `design_fk1` FOREIGN KEY (`parent_design_id`) REFERENCES `design` (`id`) ON UPDATE CASCADE,
  KEY `design_k2` (`entity_info_id`),
  CONSTRAINT `design_fk2` FOREIGN KEY (`entity_info_id`) REFERENCES `entity_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: Need trigger to prevent changing entity_info_id
--

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
-- Table `design_component`
--

DROP TABLE IF EXISTS `design_component`;
CREATE TABLE `design_component` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `design_id` int(11) unsigned NOT NULL,
  `component_id` int(11) unsigned NOT NULL,
  `location_id` int(11) unsigned NOT NULL,
  `quantity` int(11) unsigned DEFAULT 1,
  `description` varchar(256) DEFAULT NULL,
  `priority` float(10,2) unsigned DEFAULT NULL,
  `entity_info_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `design_component_u1` (`name`, `design_id`),
  KEY `design_component_k1` (`design_id`),
  CONSTRAINT `design_component_fk1` FOREIGN KEY (`design_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_component_k2` (`component_id`),
  CONSTRAINT `design_component_fk2` FOREIGN KEY (`component_id`) REFERENCES `component` (`id`) ON UPDATE CASCADE,
  KEY `design_component_k3` (`location_id`),
  CONSTRAINT `design_component_fk3` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`) ON UPDATE CASCADE,
  KEY `design_component_k4` (`entity_info_id`),
  CONSTRAINT `design_component_fk4` FOREIGN KEY (`entity_info_id`) REFERENCES `entity_info` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: Need trigger to prevent changing entity_info_id
--

--
-- Table `design_component_property`
--

DROP TABLE IF EXISTS `design_component_property`;
CREATE TABLE `design_component_property` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `design_component_id` int(11) unsigned NOT NULL,
  `property_type_id` int(11) unsigned NOT NULL,
  `value` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `design_component_property_u1` (`design_component_id`, `property_type_id`, `value`),
  KEY `design_component_property_k1` (`design_component_id`),
  CONSTRAINT `design_component_property_fk1` FOREIGN KEY (`design_component_id`) REFERENCES `design_component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_component_property_k2` (`property_type_id`),
  CONSTRAINT `design_component_property_fk2` FOREIGN KEY (`property_type_id`) REFERENCES `property_type` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `design_component_log`
--

DROP TABLE IF EXISTS `design_component_log`;
CREATE TABLE `design_component_log` (
  `design_component_id` int(11) unsigned NOT NULL,
  `log_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`design_component_id`, `log_id`),
  KEY `design_component_log_k1` (`design_component_id`),
  CONSTRAINT `design_component_log_fk1` FOREIGN KEY (`design_component_id`) REFERENCES `design_component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_component_log_k2` (`log_id`),
  CONSTRAINT `design_component_log_fk2` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `design_component_connection`
--

DROP TABLE IF EXISTS `design_component_connection`;
CREATE TABLE `design_component_connection` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `first_design_component_id` int(11) unsigned NOT NULL,
  `first_component_connector_id` int(11) unsigned NOT NULL,
  `second_design_component_id` int(11) unsigned NOT NULL,
  `second_component_connector_id` int(11) unsigned NOT NULL,
  `link_design_component_id` int(11) unsigned DEFAULT NULL,
  `link_design_component_quantity` int(11) unsigned DEFAULT NULL,
  `label` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `parent_design_component_connection_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `design_component_connection_k1` (`first_design_component_id`),
  CONSTRAINT `design_component_connection_fk1` FOREIGN KEY (`first_design_component_id`) REFERENCES `design_component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_component_connection_k2` (`first_component_connector_id`),
  CONSTRAINT `design_component_connection_fk2` FOREIGN KEY (`first_component_connector_id`) REFERENCES `component_connector` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_component_connection_k3` (`second_design_component_id`),
  CONSTRAINT `design_component_connection_fk3` FOREIGN KEY (`second_design_component_id`) REFERENCES `design_component` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_component_connection_k4` (`second_component_connector_id`),
  CONSTRAINT `design_component_connection_fk4` FOREIGN KEY (`second_component_connector_id`) REFERENCES `component_connector` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `design_component_connection_k5` (`link_design_component_id`),
  CONSTRAINT `design_component_connection_fk5` FOREIGN KEY (`link_design_component_id`) REFERENCES `design_component` (`id`) ON UPDATE CASCADE,
  KEY `design_component_connection_k6` (`parent_design_component_connection_id`),
  CONSTRAINT `design_component_connection_fk6` FOREIGN KEY (`parent_design_component_connection_id`) REFERENCES `design_component_connection` (`id`) ON UPDATE CASCADE 
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Note: We need triggers to verify that all design components belong to the
-- same design, and that connectors/design components belong to the
-- same component 
--

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
