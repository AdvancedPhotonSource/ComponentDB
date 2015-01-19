LOCK TABLES `location_type` WRITE;
/*!40000 ALTER TABLE `location_type` DISABLE KEYS */;
INSERT INTO `location_type` VALUES
(1,'Building',NULL),
(2,'Area',NULL),
(3,'Room',NULL),
(4,'Table',NULL),
(5,'Cabinet',NULL),
(6,'Rack',NULL),
(7,'Shelf',NULL);
/*!40000 ALTER TABLE `location_type` ENABLE KEYS */;
UNLOCK TABLES;
