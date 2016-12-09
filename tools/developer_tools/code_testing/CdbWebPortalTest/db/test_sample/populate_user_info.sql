LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES
(1,'cdb','CDB','System Account',NULL,'cdb@aps.anl.gov','Jhcs$Tta7OK9Yw9LM6RZ8kkK9xHmpkze1Bgoy',NULL),
(3,'some_user','Some','User','','','4aRz$BXg5ZxXdwogbf6gpRrkDfgOjpzXRiIiT','');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;
