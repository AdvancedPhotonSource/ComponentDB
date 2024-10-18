--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME -h 127.0.0.1 -u cdb -p < updateTo3.16.0.sql`

INSERT INTO property_type_handler (name, description) VALUES ('Markdown', 'Handler for properties containing markdown documentation');

ALTER TABLE property_value ADD COLUMN `text` text DEFAULT NULL AFTER `value`;  

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


LOCK TABLES `attachment` WRITE;
/*!40000 ALTER TABLE `attachment` DISABLE KEYS */;
ALTER TABLE `attachment` ADD column `original_filename` varchar(256) NOT NULL AFTER `name`;
UPDATE `attachment` set original_filename = name;
/*!40000 ALTER TABLE `attachment` ENABLE KEYS */;
UNLOCK TABLES;