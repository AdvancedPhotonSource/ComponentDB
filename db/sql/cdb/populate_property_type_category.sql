LOCK TABLES `property_type_category` WRITE;
/*!40000 ALTER TABLE `property_type_category` DISABLE KEYS */;
INSERT INTO `property_type_category` VALUES
(1,'WBS',''),
(2,'QA',''),
(3,'Physical',''),
(4,'Documentation',NULL),
(5,'Maintenance',NULL),
(6,'Status',NULL),
(7,'Lattice','Properties associate with lattice elements'),
(8,'Design','Properties associated with Designs'),
(9,'Procedure','');
/*!40000 ALTER TABLE `property_type_category` ENABLE KEYS */;
UNLOCK TABLES;
