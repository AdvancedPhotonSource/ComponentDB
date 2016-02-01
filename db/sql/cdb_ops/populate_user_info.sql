LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES
(1,'cdb','CDB','System Account',NULL,'cdb@aps.anl.gov',NULL,NULL),
(3,'nda','Ned','Arnold','','nda@aps.anl.gov',NULL,NULL),
(4,'sveseli','Sinisa','Veseli','','sveseli@aps.anl.gov',NULL,NULL);
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;
