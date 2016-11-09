--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

LOCK TABLES `property_type` WRITE;
/*!40000 ALTER TABLE `property_type` DISABLE KEYS */;
INSERT INTO `property_type` VALUES
(1,'Image',NULL,1,2,NULL,NULL,0,0,0,1,1),
(2,'Document (Upload)','',1,1,'','',0,0,0,1,1),
(3,'Traveler Template (Electronic)','Allows integration of traveler templates from traveler application.',2,7,'','',0,0,0,1,1),
(4,'Traveler Instance (Electronic)','Allows integration of traveler instances from traveler application.',2,8,'','',0,0,0,1,1),
(5,'Item Element Row Color','',4,NULL,'','',0,0,0,1,1),
(6,'Spare Parts Configuration','',3,NULL,'','',0,0,1,1,1),
(7,'Spare Part Indication','',3,NULL,'','',0,0,1,1,1);
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
UNLOCK TABLES;
