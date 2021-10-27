--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

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
(10,9,'Direct Connection','','',0.00);
/*!40000 ALTER TABLE `allowed_property_value` ENABLE KEYS */;
UNLOCK TABLES;
