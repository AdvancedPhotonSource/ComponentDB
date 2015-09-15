LOCK TABLES `property_type_category` WRITE;
/*!40000 ALTER TABLE `property_type_category` DISABLE KEYS */;
INSERT INTO `property_type_category` VALUES
(1,'Documentation',''),
(2,'Maintenance',''),
(3,'Physical',''),
(4,'QA',''),
(5,'Status',''),
(6,'WBS','');
/*!40000 ALTER TABLE `property_type_category` ENABLE KEYS */;
UNLOCK TABLES;
