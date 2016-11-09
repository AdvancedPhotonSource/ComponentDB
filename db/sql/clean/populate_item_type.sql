--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

LOCK TABLES `item_type` WRITE;
/*!40000 ALTER TABLE `item_type` DISABLE KEYS */;
INSERT INTO `item_type` VALUES
(1,'Building',NULL,1),
(2,'Area',NULL,1),
(3,'Room',NULL,1),
(4,'Table',NULL,1),
(5,'Cabinet',NULL,1),
(6,'Rack',NULL,1),
(7,'Shelf',NULL,1),
(8,'Other',NULL,2);
/*!40000 ALTER TABLE `item_type` ENABLE KEYS */;
UNLOCK TABLES;
