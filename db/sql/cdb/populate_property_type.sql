LOCK TABLES `property_type` WRITE;
/*!40000 ALTER TABLE `property_type` DISABLE KEYS */;
INSERT INTO `property_type` VALUES 
(1,'EDP Collection',NULL,4,4,NULL,NULL,0,0),
(2,'QA Level','',2,NULL,'D',NULL,0,0),
(3,'QA Inspection Template','Inspection Procedure Document',2,NULL,NULL,NULL,0,0),
(4,'QA Inspection Report','Inspection Result Document',2,NULL,NULL,NULL,0,0),
(5,'Electrical Equipment Status','NRTL Approved or APS Inspection Required or Not Required',2,NULL,NULL,NULL,0,0),
(6,'Electrical Inspection #','Inspection # from DEEI (use desc of Status?)',2,NULL,NULL,NULL,0,0),
(7,'Documentation URI','',4,3,NULL,NULL,0,0),
(8,'Form Factor','',3,NULL,NULL,NULL,0,0),
(9,'Slot Length','',3,NULL,NULL,NULL,0,0),
(10,'Required Water Flow',NULL,3,NULL,NULL,NULL,0,0),
(11,'WBS','',1,NULL,NULL,NULL,0,0),
(12,'Traveler Template',NULL,2,5,NULL,NULL,0,0),
(13,'Traveler Instance',NULL,2,5,NULL,NULL,0,0),
(14,'Image',NULL,4,2,NULL,NULL,0,0),
(15,'Document',NULL,4,1,NULL,NULL,0,0),
(16,'ICMS Document/Drawing',NULL,4,5,NULL,NULL,0,0),
(17,'PDMLink Drawing',NULL,4,6,NULL,NULL,0,0),
(18,'AMOS Order',NULL,4,7,NULL,NULL,0,0),
(19,'Purchase Requisition',NULL,4,8,NULL,NULL,0,0);
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
UNLOCK TABLES;
