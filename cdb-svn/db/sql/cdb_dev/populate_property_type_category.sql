LOCK TABLES `property_type_category` WRITE;
/*!40000 ALTER TABLE `property_type_category` DISABLE KEYS */;
INSERT INTO `property_type_category` VALUES
(1,'WBS',NULL),
(2,'QA',NULL),
(3,'Physical',NULL),
(4,'Documentation',NULL),
(5,'Maintenance',NULL),
(6,'Status',NULL),
(7,'Lattice','Properties associate with lattice elements'),
(8,'Design','Properties associated with Designs'),
(9,'Procedure',NULL),
(10,'Safety',NULL);
/*!40000 ALTER TABLE `property_type_category` ENABLE KEYS */;
UNLOCK TABLES;
