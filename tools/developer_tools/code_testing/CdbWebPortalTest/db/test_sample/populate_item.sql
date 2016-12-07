LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES
(1,1,'ANL',NULL,NULL,NULL,NULL),
(2,1,'APS',NULL,'','',NULL),
(3,2,'Something you can buy',NULL,'123','',NULL),
(4,2,'An Assembly',NULL,NULL,NULL,NULL),
(5,3,'Unit: 1',3,'','',NULL),
(6,3,'Unit: 4',3,'','',NULL),
(7,3,'Unit: 1',4,NULL,NULL,NULL),
(8,3,'Unit: 5',3,'','',NULL),
(9,3,'Unit: 2',3,'','',NULL),
(10,3,'Unit: 3',3,'','',NULL),
(11,3,'Unit: 2',4,NULL,NULL,NULL),
(12,3,'Unit: 7',3,'','',NULL),
(13,3,'Unit: 6',3,'','',NULL);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;
