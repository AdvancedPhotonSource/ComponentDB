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
(9,'cable_catalog_internal_property_type','Cable Type Metadata',NULL,NULL,NULL,NULL,NULL,0,0,1,1,NULL),
(10,'cable_inventory_internal_property_type','Cable Inventory Metadata',NULL,NULL,NULL,NULL,NULL,0,0,1,1,NULL),
(11,'Cable Instance Status',NULL,NULL,NULL,NULL,'Planned',NULL,0,0,1,1,NULL),
(12,'cable_design_internal_property_type','Cable Design Metadata',NULL,NULL,NULL,NULL,NULL,0,0,1,1,NULL),
(13,'Test Property','','',1,NULL,'','',0,0,0,1,NULL);
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
UNLOCK TABLES;
