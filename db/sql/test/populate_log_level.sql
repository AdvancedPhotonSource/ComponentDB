LOCK TABLES `log_level` WRITE;
/*!40000 ALTER TABLE `log_level` DISABLE KEYS */;
INSERT INTO `log_level` VALUES
(1,'Spares Warning',NULL),
(2,'loginInfo',NULL),
(3,'cdbEntityInfo',NULL);
/*!40000 ALTER TABLE `log_level` ENABLE KEYS */;
UNLOCK TABLES;
