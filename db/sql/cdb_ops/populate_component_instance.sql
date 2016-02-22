LOCK TABLES `component_instance` WRITE;
/*!40000 ALTER TABLE `component_instance` DISABLE KEYS */;
INSERT INTO `component_instance` VALUES
(1,1,402,'35-ID-A-F-01',NULL,NULL,NULL,'',14),
(2,1,NULL,NULL,NULL,NULL,NULL,'',13),
(3,2,402,NULL,NULL,NULL,NULL,'',17),
(4,3,402,NULL,NULL,NULL,NULL,'',20),
(5,4,402,NULL,NULL,NULL,'','Mask and support assembly',23);
/*!40000 ALTER TABLE `component_instance` ENABLE KEYS */;
UNLOCK TABLES;
