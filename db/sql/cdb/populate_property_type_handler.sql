LOCK TABLES `property_type_handler` WRITE;
/*!40000 ALTER TABLE `property_type_handler` DISABLE KEYS */;
INSERT INTO `property_type_handler` VALUES 
(1,'HTTP Link','Handler for standard HTTP links.'),
(2,'APS Link','Handler for various tyes of APS links.'),
(3,'Image','Image handler.'),
(4,'Document','Document handler.');
/*!40000 ALTER TABLE `property_type_handler` ENABLE KEYS */;
UNLOCK TABLES;
