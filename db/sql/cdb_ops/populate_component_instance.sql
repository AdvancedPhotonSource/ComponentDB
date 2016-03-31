LOCK TABLES `component_instance` WRITE;
/*!40000 ALTER TABLE `component_instance` DISABLE KEYS */;
INSERT INTO `component_instance` VALUES
(1,1,402,'35-ID-A-F-01',NULL,NULL,NULL,'Collimator, Ratchet wall plate, Vacuum gauges, Mask',14),
(3,2,402,NULL,NULL,NULL,NULL,'',17),
(4,3,402,NULL,NULL,NULL,NULL,'',20),
(7,28,NULL,NULL,NULL,NULL,NULL,'',96),
(8,28,NULL,NULL,NULL,NULL,NULL,'',97),
(10,32,402,NULL,NULL,NULL,NULL,'',104),
(11,34,402,NULL,NULL,NULL,NULL,'',105),
(12,35,402,NULL,NULL,NULL,NULL,'',106),
(14,36,402,NULL,NULL,NULL,NULL,'',109),
(15,40,403,'Spare Part',NULL,NULL,NULL,'9 Spare kits',117),
(16,41,60,'Spare Parts',NULL,NULL,NULL,'Cabinet 75, One Spare',119),
(19,42,405,'Spare Part #1',NULL,NULL,'','Special Process Spare',125),
(24,42,405,'Spare Part #2',NULL,NULL,'','Special Process Spare',130),
(25,44,406,NULL,'.5M-015-std',NULL,'On the floor between row G & H','AS1, AQ3, BQ4',135);
/*!40000 ALTER TABLE `component_instance` ENABLE KEYS */;
UNLOCK TABLES;
