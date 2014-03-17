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
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_u1` (`username`),
  UNIQUE KEY `user_u2` (`first_name`, `last_name`, `middle_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `group`
--

DROP TABLE IF EXISTS `group`;
CREATE TABLE `group` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL,
  `group_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_group_u1` (`user_id`, `group_id`),
  KEY `user_group_k1` (`user_id`),
  CONSTRAINT `user_group_fk1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
  KEY `user_group_k2` (`group_id`),
  CONSTRAINT `user_group_group_fk2` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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
-- Table `resource_category`
--

DROP TABLE IF EXISTS `resource_category`;
CREATE TABLE `resource_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_category_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `resource_type`
--

CREATE TABLE `resource_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `resource_category_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_type_u1` (`name`),
  KEY `resource_type_k1` (`resource_category_id`),
  CONSTRAINT `resource_type_fk1` FOREIGN KEY (`resource_category_id`) REFERENCES `resource_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_category`
--

DROP TABLE IF EXISTS `property_category`;
CREATE TABLE `property_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_category_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `property_type`
--

CREATE TABLE `property_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `property_category_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `property_type_u1` (`name`),
  KEY `property_type_k1` (`property_category_id`),
  CONSTRAINT `property_type_fk1` FOREIGN KEY (`property_category_id`) REFERENCES `property_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `connector_category`
--

DROP TABLE IF EXISTS `connector_category`;
CREATE TABLE `connector_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `connector_category_u1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Table `connector_type`
--

CREATE TABLE `connector_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `connector_category_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `connector_type_u1` (`name`),
  KEY `connector_type_k1` (`connector_category_id`),
  CONSTRAINT `connector_type_fk1` FOREIGN KEY (`connector_category_id`) REFERENCES `connector_category` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
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
-- Table `manufacturer`
--

DROP TABLE IF EXISTS `manufacturer`;
CREATE TABLE `manufacturer` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `manufacturer_u1` (`name`)
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
  `owner_user_id` int(11) unsigned NOT NULL,
  `owner_group_id` int(11) unsigned NOT NULL,
  `component_state_id` int(11) unsigned NOT NULL,
  `documentation_uri` varchar(256) DEFAULT NULL,
  `estimated_cost` float(10,2) DEFAULT NULL,
  `created_on_date_time` datetime NOT NULL,
  `created_by_user_id` int(11) unsigned NOT NULL,
  `modified_on_date_time` datetime NOT NULL,
  `modified_by_user_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `component_u1` (`name`),
  KEY `component_k1` (`component_state_id`),
  CONSTRAINT `component_fk1` FOREIGN KEY (`component_state_id`) REFERENCES `component_state` (`id`) ON UPDATE CASCADE,
  KEY `component_k2` (`owner_user_id`),
  CONSTRAINT `component_fk2` FOREIGN KEY (`owner_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE,
  KEY `component_k3` (`owner_group_id`),
  CONSTRAINT `component_fk3` FOREIGN KEY (`owner_group_id`) REFERENCES `group` (`id`) ON UPDATE CASCADE,
  KEY `component_k4` (`created_by_user_id`),
  CONSTRAINT `component_fk4` FOREIGN KEY (`created_by_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE,
  KEY `component_k5` (`modified_by_user_id`),
  CONSTRAINT `component_fk5` FOREIGN KEY (`modified_by_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

