LOCK TABLES `property_type_category` WRITE;
/*!40000 ALTER TABLE `property_type_category` DISABLE KEYS */;
INSERT INTO `property_type_category` VALUES
(1,'Documentation',NULL),
(2,'QA',NULL),
(3,'Physical',NULL),
(4,'Other',NULL);
/*!40000 ALTER TABLE `property_type_category` ENABLE KEYS */;
UNLOCK TABLES;
