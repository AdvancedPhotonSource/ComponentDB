--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

LOCK TABLES `property_type` WRITE;
/*!40000 ALTER TABLE `property_type` DISABLE KEYS */;
INSERT INTO `property_type` VALUES
(1,'Image',NULL,NULL,1,2,NULL,NULL,0,0,0,1,1),
(2,'Document (Upload)','',NULL,1,1,'','',0,0,0,1,1),
(3,'Traveler Template (Electronic)','Allows integration of traveler templates from traveler application.',NULL,2,7,'','',0,0,1,1,1),
(4,'Traveler Instance (Electronic)','Allows integration of traveler instances from traveler application.',NULL,2,8,'','',0,0,1,1,1),
(5,'Item Element Row Color','',NULL,4,NULL,'','',0,0,0,1,1),
(6,'Spare Parts Configuration','',NULL,3,NULL,'','',0,0,1,1,1),
(8,'Component Instance Status','','',3,NULL,'','',0,0,1,1,1),
(9,'Control Interface','Describes the interface to parent in the control hierarchy','',NULL,NULL,'','',0,0,1,1,NULL);
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
UNLOCK TABLES;
