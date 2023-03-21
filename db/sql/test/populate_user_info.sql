LOCK TABLES `user_info` WRITE;
SET SESSION FOREIGN_KEY_CHECKS=0;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES
(1,'cdb','CDB','System Account',NULL,'cdb@aps.anl.gov','sqj7$C7w3IgWH5Ta02kHtnSZWTGPfbNTSj9yj',NULL),
(2,'testUser','User','Test','','','va4a$+d80SXK90kS4V8Bme7RTvygNp/cul9Cd','');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;
