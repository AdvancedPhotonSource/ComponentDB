LOCK TABLES `property_type` WRITE;
/*!40000 ALTER TABLE `property_type` DISABLE KEYS */;
INSERT INTO `property_type` VALUES
(1,'AMOS Order','Provides a link to AMOS, enter the Master Order number, e.g. MO_nnnnnn',1,7,'',''),
(2,'Documentation URL','Provides a link to any web address (URL)',1,3,'',''),
(3,'PDMLink Drawing','',1,6,'',''),
(4,'WBS','',6,NULL,'','');
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
UNLOCK TABLES;
