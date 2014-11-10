LOCK TABLES `property_type` WRITE;
/*!40000 ALTER TABLE `property_type` DISABLE KEYS */;
INSERT INTO `property_type` VALUES 
(1,'WBS','',1,NULL,NULL,NULL,0,0),
(2,'QA Level','',2,NULL,'D',NULL,0,0),
(3,'QA Inspection Template','Inspection Procedure Document',2,NULL,NULL,NULL,0,0),
(4,'QA Inspection Report','Inspection Result Document',2,NULL,NULL,NULL,0,0),
(5,'Electrical Equipment Status','NRTL Approved or APS Inspection Req\'d or Not Required',2,NULL,NULL,NULL,0,0),
(6,'Electrical Inspection #','Inspection # from DEEI (use desc of Status?)',2,NULL,NULL,NULL,0,0),
(7,'Drawing/Document Reference','',4,NULL,NULL,NULL,0,0),
(8,'Form Factor','',3,NULL,NULL,NULL,0,0),
(9,'Slot Length','',3,NULL,NULL,NULL,0,0),
(10,'Req\'d Water Flow',NULL,3,NULL,NULL,NULL,0,0),
(11,'Primary Documentation Reference',NULL,4,NULL,NULL,NULL,0,0),
(12,'Traveler Template',NULL,2,NULL,NULL,NULL,0,0),
(13,'Traveler Instance',NULL,2,NULL,NULL,NULL,0,0);
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
UNLOCK TABLES;
