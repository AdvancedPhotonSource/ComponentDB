LOCK TABLES `relationship_type` WRITE;
SET SESSION FOREIGN_KEY_CHECKS=0;
/*!40000 ALTER TABLE `relationship_type` DISABLE KEYS */;
INSERT INTO `relationship_type` VALUES
(1,'Location','Location Relationship',1),
(2,'MAARC Connection','MAARC connection relationship to inventory items.',NULL),
(3,'Created From Template','',NULL),
(4,'Cable Connection',NULL,NULL),
(5,'Controlled By','Defines how a machine element is controlled',NULL),
(6,'Powered By','Defines how a machine element is powered',NULL);
/*!40000 ALTER TABLE `relationship_type` ENABLE KEYS */;
UNLOCK TABLES;
