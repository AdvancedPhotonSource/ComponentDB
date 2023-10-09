LOCK TABLES `domain` WRITE;
/*!40000 ALTER TABLE `domain` DISABLE KEYS */;
INSERT INTO `domain` VALUES
(1,'Location','Item Domain for managing locations.',NULL,NULL,'Type',NULL),
(2,'Catalog','Item domain for managing catalog items.','Model Number','Alternate Name','Function','Technical System'),
(3,'Inventory','Item domain for managing inventory items','Serial Number',NULL,NULL,NULL),
(5,'MAARC','Item domain for managing measurement archive items',NULL,NULL,NULL,NULL),
(6,'Machine Design','Item domain for managing the design of machines','Alternate Name','UUID',NULL,NULL),
(7,'Cable Catalog','Item domain for managing the cable catalog items','Part Number','Alternate Name','Function','Technical System'),
(8,'Cable Inventory','Item domain for managing cable inventory items','Serial Number',NULL,NULL,NULL),
(9,'Cable Design','Item domain for managing cable design items','Alternate Name','UUID',NULL,'Technical System'),
(10,'App','Item domain for managing C2 apps.',NULL,NULL,'Tag','Application Type'),
(11,'App Deployment','Item domain for managing C2 app deployments.',NULL,NULL,NULL,'Deployment Type');
/*!40000 ALTER TABLE `domain` ENABLE KEYS */;
UNLOCK TABLES;
