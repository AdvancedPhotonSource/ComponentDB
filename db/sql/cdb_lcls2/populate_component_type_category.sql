LOCK TABLES `component_type_category` WRITE;
/*!40000 ALTER TABLE `component_type_category` DISABLE KEYS */;
INSERT INTO `component_type_category` VALUES
(1,'Support',''),
(2,'HGVPU',''),
(3,'Controls/Instrumentation',''),
(4,'Quadrupole',''),
(5,'Diagnostics',''),
(6,'Phase Shifter',''),
(7,'Collimator',''),
(8,'Vacuum','');
/*!40000 ALTER TABLE `component_type_category` ENABLE KEYS */;
UNLOCK TABLES;
