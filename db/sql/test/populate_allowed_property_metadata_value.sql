LOCK TABLES `allowed_property_metadata_value` WRITE;
/*!40000 ALTER TABLE `allowed_property_metadata_value` DISABLE KEYS */;
INSERT INTO `allowed_property_metadata_value` VALUES
(1,2,'Received',NULL),
(2,2,'Ordered',NULL),
(3,2,'Unspecified',NULL);
/*!40000 ALTER TABLE `allowed_property_metadata_value` ENABLE KEYS */;
UNLOCK TABLES;
