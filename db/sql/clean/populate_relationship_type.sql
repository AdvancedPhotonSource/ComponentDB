--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

LOCK TABLES `relationship_type` WRITE;
/*!40000 ALTER TABLE `relationship_type` DISABLE KEYS */;
INSERT INTO `relationship_type` VALUES
(1,'Location','Location Relationship',1);
/*!40000 ALTER TABLE `relationship_type` ENABLE KEYS */;
UNLOCK TABLES;
