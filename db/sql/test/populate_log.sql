LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
INSERT INTO `log` VALUES
(1,'User: cdb | Updated: Test, User (testUser)','2023-03-21 11:44:37',1,NULL,NULL,NULL),
(2,'User: cdb | Failed to create: Template_Test. Exception - Place parements within {} in template name. Example: \'templateName {paramName}\'','2023-10-09 10:23:50',1,NULL,NULL,NULL),
(3,'User: cdb | Created: Template_Test {nn} [Item Id: 114]','2023-10-09 10:24:04',1,NULL,NULL,NULL),
(4,'User: cdb | Updated: Template_Test {nn} [Item Id: 114]','2023-10-09 10:24:25',1,NULL,NULL,NULL),
(5,'User: cdb | Created: Template Holder [Item Id: 116]','2023-10-09 10:24:50',1,NULL,NULL,NULL),
(6,'User: cdb | Updated: Template Holder [Item Id: 116]','2023-10-09 10:25:54',1,NULL,NULL,NULL);
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;
