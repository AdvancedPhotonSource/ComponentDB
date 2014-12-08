LOCK TABLES `property_type_handler` WRITE;
/*!40000 ALTER TABLE `property_type_handler` DISABLE KEYS */;
INSERT INTO `property_type_handler` VALUES 
(1,'Document','Generic document handler.'),
(2,'Image','Image handler.'),
(3,'HTTP Link','Handler for standard HTTP links.'),
(4,'EDP Link','Handler for EDP collection links.'),
(5,'ICMS Link','Handler for ICMS links.'),
(6,'PDMLink','Handler for PDMLink drawings.'),
(7,'AMOS Link','Handler for AMOS purchasing links.'),
(8,'PARIS Link','Handler for PARIS purchasing links.'),
(9,'Currency','Handler for monetary values.');
/*!40000 ALTER TABLE `property_type_handler` ENABLE KEYS */;
UNLOCK TABLES;
