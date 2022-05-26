LOCK TABLES `property_type_handler` WRITE;
SET SESSION FOREIGN_KEY_CHECKS=0;
/*!40000 ALTER TABLE `property_type_handler` DISABLE KEYS */;
INSERT INTO `property_type_handler` VALUES
(1,'Document','Generic document handler.'),
(2,'Image','Image handler.'),
(3,'HTTP Link','Handler for standard HTTP links.'),
(4,'Currency','Handler for monetary values.'),
(5,'Boolean','Handler for boolean values.'),
(6,'Date','Handler for date values.'),
(7,'Traveler Template','Handler used for creating/viewing traveler templates in the traveler system.'),
(8,'Traveler Instance','Handler used for creating/viewing traveler instances in the traveler system.');
/*!40000 ALTER TABLE `property_type_handler` ENABLE KEYS */;
UNLOCK TABLES;
