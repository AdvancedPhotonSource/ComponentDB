LOCK TABLES `user_info` WRITE;
SET SESSION FOREIGN_KEY_CHECKS=0;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES
(1,'cdb','CDB','System Account',NULL,'cdb@aps.anl.gov','LOBv$O6On1e9c/ckxMRkvjLniYy5Huhl6ZMSS',NULL),
(2,'testUser','User','Test','','','idQQ$5meTY3iDPx9rXmV9TVuVEx0VWjKUDx6V','');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;
