LOCK TABLES `domain` WRITE;
/*!40000 ALTER TABLE `domain` DISABLE KEYS */;
INSERT INTO `domain` VALUES
(1,'Location','Item Domain for managing locations.',NULL,NULL,'Type',NULL),
(2,'Catalog','Item domain for managing catalog items.','Model Number','Alternate Name','Function','Technical System'),
(3,'Inventory','Item domain for managing inventory items','Serial Number',NULL,NULL,NULL),
(4,'Cable','Item domain for managing cable items',NULL,NULL,NULL,NULL),
(5,'MAARC','Item domain for managing measurement archive items',NULL,NULL,NULL,NULL),
(6,'Machine Design','Item domain for managing the design of machines','Alternate Name','UUID',NULL,NULL),
(7,'Cable Catalog','Item domain for managing the cable catalog items','Part Number','Alternate Name',NULL,'Technical System'),
(8,'Cable Inventory','Item domain for managing cable inventory items',NULL,NULL,NULL,NULL),
(9,'Cable Design','Item domain for managing cable design items','Alternate Name','UUID',NULL,'Technical System');
/*!40000 ALTER TABLE `domain` ENABLE KEYS */;
UNLOCK TABLES;
