LOCK TABLES `relationship_type` WRITE;
/*!40000 ALTER TABLE `relationship_type` DISABLE KEYS */;
INSERT INTO `relationship_type` VALUES
(1,'Location','Location Relationship',1),
(2,'MAARC Connection','MAARC connection relationship to inventory items.',NULL),
(3,'Created From Template','',NULL),
(4,'Cable Connection',NULL,NULL);
/*!40000 ALTER TABLE `relationship_type` ENABLE KEYS */;
UNLOCK TABLES;
