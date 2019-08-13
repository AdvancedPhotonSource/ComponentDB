LOCK TABLES `domain` WRITE;
/*!40000 ALTER TABLE `domain` DISABLE KEYS */;
INSERT INTO `domain` VALUES
(1,'Location','Item Domain for managing locations.'),
(2,'Catalog','Item domain for managing catalog items.'),
(3,'Inventory','Item domain for managing inventory items');
/*!40000 ALTER TABLE `domain` ENABLE KEYS */;
UNLOCK TABLES;
