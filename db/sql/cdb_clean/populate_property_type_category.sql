LOCK TABLES `property_type_category` WRITE;
/*!40000 ALTER TABLE `property_type_category` DISABLE KEYS */;
INSERT INTO 'property_type_category' VALUES
(1, 'System Default', 'Only remove these properties if another property type uses the same handler. Provide various functionality to the system.');
/*!40000 ALTER TABLE `property_type_category` ENABLE KEYS */;
UNLOCK TABLES;
