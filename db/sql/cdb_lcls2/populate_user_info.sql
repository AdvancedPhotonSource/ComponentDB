LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES
(1,'cdb','CDB','System Account',NULL,'cdb@aps.anl.gov',NULL,NULL),
(3,'nda','Ned','Arnold','','nda@aps.anl.gov',NULL,NULL),
(4,'sveseli','Sinisa','Veseli','','sveseli@aps.anl.gov',NULL,NULL),
(6,'mwhite','Marion','White','','mwhite@aps.anl.gov',NULL,NULL),
(7,'brianr','Brian','Rusthoven','','brianr@aps.anl.gov',NULL,NULL),
(8,'dpjensen','Don','Jensen','','dpjensen@aps.anl.gov',NULL,NULL),
(9,'oschmidt','Oliver','Schmidt','','oschmidt@aps.anl.gov',NULL,NULL),
(10,'pileg','Geoff','Pile','','pileg',NULL,NULL),
(11,'hanuska','Steve','Hanuska','','hanuska@aps.anl.gov',NULL,NULL),
(12,'rjvoogd','Rich','Voogd','','rjvoogd@anl.gov',NULL,NULL),
(13,'wiemer','Greg','Wiemerslage','','wiemer@aps.anl.gov',NULL,NULL),
(14,'jlerch','Jason','Lerch','','jlerch@aps.anl.gov',NULL,NULL),
(15,'djarosz','Dariusz','Jarosz','','djarosz@aps.anl.gov',NULL,NULL);
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;
