LOCK TABLES `domain_handler` WRITE;
/*!40000 ALTER TABLE `domain_handler` DISABLE KEYS */;
INSERT INTO `domain_handler` VALUES
(1,'Location','Handler responsible for locations.'),
(2,'Catalog','Handler responsible for items in a catalog type domain.'),
(3,'Inventory','Handler responsible for handling phyisical inventory of items.');
/*!40000 ALTER TABLE `domain_handler` ENABLE KEYS */;
UNLOCK TABLES;
