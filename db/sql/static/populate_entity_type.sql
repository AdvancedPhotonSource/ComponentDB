LOCK TABLES `entity_type` WRITE;
/*!40000 ALTER TABLE `entity_type` DISABLE KEYS */;
INSERT INTO `entity_type` VALUES
(1,'Design','classification for design type objects'),
(2,'Component','classification for component type objects'),
(3,'Study', 'cassification for a study type items'),
(4,'File', 'classification for a file type items'),
(5,'Template', 'classification for a machine design template items'),
(6,'Measurement Data', 'classification for MAARC measurment data type items.'),
(7,'Inventory','Entity type used for marking a sub domain of intevntory type items.'),
(8,'Deleted','Entity type used for marking an item deleted.'),
(9,'Control','Entity type used for marking a sub domain of control type items.'),
(10,'Power','Entity type used for marking a sub domain of power type items.'),
(11,'IOC','Entity type used for marking a sub domain of ioc type items.');
/*!40000 ALTER TABLE `entity_type` ENABLE KEYS */;
UNLOCK TABLES;
