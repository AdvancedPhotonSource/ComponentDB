LOCK TABLES `item_category` WRITE;
/*!40000 ALTER TABLE `item_category` DISABLE KEYS */;
INSERT INTO `item_category` VALUES
(1,'Mechanical/Accelerator',NULL,2),
(2,'Diagnostics',NULL,2),
(3,'Controls/Instrumentation',NULL,2),
(5,'Power Supplies',NULL,2),
(6,'Magnets',NULL,2),
(7,'RF',NULL,2),
(8,'Mechanical/Beamlines',NULL,2),
(9,'Mechanical/Insertion Devices',NULL,2),
(10,'Lattice Elements',NULL,2),
(12,'ACIS','',2),
(13,'Generic Functions/Placeholders','',2),
(14,'APS-U Machine Design','Includes components and assemblies for the \"Production\" design. Should evolve into a complete Bill Of Materials for the APSU',2);
/*!40000 ALTER TABLE `item_category` ENABLE KEYS */;
UNLOCK TABLES;
