LOCK TABLES `item_project` WRITE;
/*!40000 ALTER TABLE `item_project` DISABLE KEYS */;
INSERT INTO `item_project` VALUES
(1,'APS-U Other',NULL),
(2,'APS-OPS',NULL),
(3,'APS-U Production',NULL),
(4,'Beamline',NULL),
(5,'LEA',NULL),
(6,'Accelerator',NULL),
(7,'CMB-S4','Cosmic Microwave Background Experiment - Stage 4');
/*!40000 ALTER TABLE `item_project` ENABLE KEYS */;
UNLOCK TABLES;
