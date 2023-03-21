LOCK TABLES `log_level` WRITE;
SET SESSION FOREIGN_KEY_CHECKS=0;
/*!40000 ALTER TABLE `log_level` DISABLE KEYS */;
INSERT INTO `log_level` VALUES
(1,'Spares Warning',NULL),
(2,'loginInfo',NULL),
(3,'cdbEntityInfo',NULL),
(4,'cdbEntityWarning',NULL);
/*!40000 ALTER TABLE `log_level` ENABLE KEYS */;
UNLOCK TABLES;
