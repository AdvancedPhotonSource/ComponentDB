--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
INSERT INTO `user_group` VALUES
(1,'CDB_ADMIN','System Admin Group'),
(2,'CDB_MAINTAINER', 'System Maintainer Group'),
(3,'CDB_ADVANCED', 'System Advanced Group');
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;
