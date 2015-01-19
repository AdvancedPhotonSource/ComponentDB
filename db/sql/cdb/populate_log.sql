LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
INSERT INTO `log` VALUES
(2,'Received first unit ... bake out underway','2015-01-05 15:47:23',3,NULL),
(3,'Unit developed a leak, returning to vendor.  See attachment.','2015-01-07 10:47:59',3,NULL),
(4,'Received back from vendor, looks much better','2015-01-14 12:48:01',3,NULL);
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;
