LOCK TABLES `component_type_category` WRITE;
/*!40000 ALTER TABLE `component_type_category` DISABLE KEYS */;
INSERT INTO `component_type_category` VALUES
(1,'Other',NULL),
(2,'Housing',NULL),
(3,'Controls/Instrumentation',NULL),
(5,'Mechanical/Accelerator',NULL),
(6,'Magnets',NULL),
(7,'Power Supplies',NULL),
(8,'Diagnostics',NULL),
(9,'RF',NULL),
(10,'Mechanical/Beamlines',NULL),
(11,'Mechanical/Insertion Devices',NULL),
(12,'Lattice Elements',NULL);
/*!40000 ALTER TABLE `component_type_category` ENABLE KEYS */;
UNLOCK TABLES;
