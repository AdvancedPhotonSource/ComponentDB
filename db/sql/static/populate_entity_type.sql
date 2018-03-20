LOCK TABLES `entity_type` WRITE;
/*!40000 ALTER TABLE `entity_type` DISABLE KEYS */;
INSERT INTO `entity_type` VALUES
(1,'Design','classification for design type objects'),
(2,'Component','classification for component type objects'),
(3,'Study', 'cassification for a study type items'),
(4,'File', 'classification for a file type items'),
(5,'Template', 'classification for a machine design template items'); 
/*!40000 ALTER TABLE `entity_type` ENABLE KEYS */;
UNLOCK TABLES;
