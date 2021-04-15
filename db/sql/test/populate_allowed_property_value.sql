LOCK TABLES `allowed_property_value` WRITE;
/*!40000 ALTER TABLE `allowed_property_value` DISABLE KEYS */;
INSERT INTO `allowed_property_value` VALUES
(1,8,'Ordered','','',2.00),
(2,8,'Failed/Rejected        ','','',4.00),
(3,8,'Returned','','',8.00),
(4,8,'-','','',1.00),
(5,8,'Received','','',3.00),
(6,8,'Inspected/Tested/Certified     ','','',5.00),
(7,8,'Installed','','',9.00),
(8,8,'Spare','','',7.00),
(9,8,'Available','','',6.00),
(10,11,'Spare',NULL,NULL,10.00),
(11,11,'Returned',NULL,NULL,13.00),
(12,11,'Accepted',NULL,NULL,5.00),
(13,11,'Post-Acceptance/Test/Certification in Progress',NULL,NULL,7.00),
(14,11,'Rejected',NULL,NULL,6.00),
(15,11,'Requisition Submitted',NULL,NULL,2.00),
(16,11,'Spare - Critical',NULL,NULL,11.00),
(17,11,'Installed',NULL,NULL,9.00),
(18,11,'Ready For Use',NULL,NULL,8.00),
(19,11,'Delivered',NULL,NULL,3.00),
(20,11,'Discarded',NULL,NULL,14.00),
(21,11,'Acceptance In Progress',NULL,NULL,4.00),
(22,11,'Failed',NULL,NULL,12.00),
(23,11,'Planned',NULL,NULL,1.10),
(24,11,'Unknown',NULL,NULL,1.00);
/*!40000 ALTER TABLE `allowed_property_value` ENABLE KEYS */;
UNLOCK TABLES;
