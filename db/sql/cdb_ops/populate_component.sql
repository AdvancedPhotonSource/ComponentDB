LOCK TABLES `component` WRITE;
/*!40000 ALTER TABLE `component` DISABLE KEYS */;
INSERT INTO `component` VALUES
(1,'Front End Components','35-ID','Collimator, Ratchet wall plate, Vacuum gauges, Mask',104,12),
(2,'Labyrinths',NULL,'',104,16),
(3,'Survey Port',NULL,'See specific beamline drawings for more details',104,19),
(4,'Mask','D14301-160000-00','Mask and Support Assembly',104,22),
(5,'White Beam Stop Assembly','D14308-110000-02','',104,26),
(7,'P10 ID White Beam Shutter','4105090910-150000-01','',104,29),
(8,'Mask Assembly','D14301-120000/D14301-130000','Mask 3 rigidly mounted to Mask 2',104,30),
(9,'Safety Shutter','D14305-110000-00','',104,31),
(10,'Lead (PB) Bricks',NULL,'',104,32),
(11,'Shielded Beam Transport',NULL,'',104,33),
(12,'Guillotine','Generic','',104,40),
(13,'Collimator','D14302-110000','',104,41),
(14,'White Beam Stop','D14309 - 110000','',104,44),
(15,'Bremsstrahlung Stop Assembly (Movable)','D14310-110000','Includes 4\" thick lead assembly (D14310-110240-00) and 8\" thick lead assembly (D14310-110250-00).',104,45),
(16,'Pink Beam Stop','D14306-110000','The  Pink Beam Stop is mounted  on the movable lead stop. Inboard side of  the stop  front  copper plate aligned 10 mm  outboard of white beam Bottom side of  the stop  front  copper plate aligned 8 mm below  white beam',104,51),
(21,'Mask','14301-15000-01','DCS Mask 5',104,57),
(22,'Access Shield','35-ID-C','B to C Station Beam Transport Line Port Access Shield',104,58),
(23,'Mask','D14301-140000-00','DCS Mask 4',104,59),
(24,'Collimator','D14302-120000-01','Tungsten K2 Collimator',104,60),
(28,'Pink Beam Stop ','D14803 - 107200','Lead Brick Stack in air',104,87);
/*!40000 ALTER TABLE `component` ENABLE KEYS */;
UNLOCK TABLES;
