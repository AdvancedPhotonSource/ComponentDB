LOCK TABLES `component_instance` WRITE;
/*!40000 ALTER TABLE `component_instance` DISABLE KEYS */;
INSERT INTO `component_instance` VALUES
(1,180,396,'Glenn\'s Windows Laptop',NULL,42,'Mobile','',383),
(2,6,397,'3-D Printed Example',NULL,11,'','',384),
(5,1,232,'First Unit','330 223 093',NULL,'','',387),
(6,181,NULL,'First Unit',NULL,12,'','',389),
(7,182,NULL,'First Unit',NULL,13,'','',391),
(8,183,214,'First Unit','FPG2014000416',1,'','',396),
(9,184,NULL,NULL,NULL,2,'','',397),
(22,181,NULL,'Second Unit',NULL,14,'','',410),
(23,186,NULL,NULL,NULL,8,'','',413),
(24,186,NULL,NULL,NULL,5,'','',414),
(25,186,NULL,NULL,NULL,6,'','',411),
(26,186,NULL,NULL,NULL,7,'','',412),
(27,186,NULL,NULL,NULL,10,'','',416),
(28,186,NULL,NULL,NULL,9,'','',415),
(29,185,NULL,NULL,'ATF20140004553',3,'','',418),
(30,185,NULL,NULL,'ATF20140004552',4,'','',417);
/*!40000 ALTER TABLE `component_instance` ENABLE KEYS */;
UNLOCK TABLES;
