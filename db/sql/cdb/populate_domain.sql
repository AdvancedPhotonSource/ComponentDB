LOCK TABLES `domain` WRITE;
/*!40000 ALTER TABLE `domain` DISABLE KEYS */;
INSERT INTO `domain` VALUES
(1,'Location','Location Domain',1),
(2,'Catalog','Catalog Domain',2),
(3,'Instance','Instance Domain',3),
(4,'Library','Library Domain',2);
/*!40000 ALTER TABLE `domain` ENABLE KEYS */;
UNLOCK TABLES;
