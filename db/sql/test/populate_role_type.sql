LOCK TABLES `role_type` WRITE;
/*!40000 ALTER TABLE `role_type` DISABLE KEYS */;
INSERT INTO `role_type` VALUES
(1,'group_settings_admin','Ability to edit view settings for a particular group.');
/*!40000 ALTER TABLE `role_type` ENABLE KEYS */;
UNLOCK TABLES;
