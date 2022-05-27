LOCK TABLES `allowed_child_entity_type` WRITE;
SET SESSION FOREIGN_KEY_CHECKS=0;
/*!40000 ALTER TABLE `allowed_child_entity_type` DISABLE KEYS */;
INSERT INTO `allowed_child_entity_type` VALUES
(1,1),
(1,2),
(2,1),
(2,2);
/*!40000 ALTER TABLE `allowed_child_entity_type` ENABLE KEYS */;
UNLOCK TABLES;
