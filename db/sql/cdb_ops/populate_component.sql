LOCK TABLES `component` WRITE;
/*!40000 ALTER TABLE `component` DISABLE KEYS */;
INSERT INTO `component` VALUES
(1,'Front End Components','35-ID','Collimator, Ratchet wall plate, Vacuum gauges, Mask',104,12),
(2,'Labyrinths',NULL,'',104,16),
(3,'Survey Port',NULL,'See specific beamline drawings for more details',104,19),
(8,'Mask Assembly','D14301-120000/D14301-130000','Mask 3 rigidly mounted to Mask 2',72,30),
(9,'Safety Shutter','D14305-110000-00','',104,31),
(10,'Lead (PB) Bricks',NULL,'',104,32),
(11,'Shielded Beam Transport',NULL,'',104,33),
(12,'Guillotine','Generic','',104,40),
(14,'White Beam Stop','D14309 - 110000','',104,44),
(15,'Bremsstrahlung Stop Assembly (Movable)','D14310-110000','Includes 4\" thick lead assembly (D14310-110240-00) and 8\" thick lead assembly (D14310-110250-00).',104,45),
(16,'Pink Beam Stop','D14306-110000','The  Pink Beam Stop is mounted  on the movable lead stop. Inboard side of  the stop  front  copper plate aligned 10 mm  outboard of white beam Bottom side of  the stop  front  copper plate aligned 8 mm below  white beam',104,51),
(21,'Mask','14301-15000-01','DCS Mask 5',104,57),
(22,'Access Shield','35-ID-C','B to C Station Beam Transport Line Port Access Shield',104,58),
(23,'Mask','D14301-140000-00','DCS Mask 4',104,59),
(24,'Collimator','D14302-120000-01','Tungsten K2 Collimator',104,60),
(28,'Lead Stop and Brick Assembly','D14803 - 107200','Lead Brick Stack in air',104,87),
(32,'MASK AND SUPPORT ASSEMBLY','D14301-160000-00','DYNAMIC COMPRESSION SECTOR BEAMLINE; RSS COMPONENTS; MASKS; -; MASK AND SUPPORT ASSEMBLY',104,100),
(34,'TUNGSTEN K1 COLLIMATOR','D14302-110000-00','DYNAMIC COMPRESSION BEAMLINE; RSS COMPONENTS; COLLIMATOR W; COMPONENT ASSY; TUNGSTEN K1 COLLIMATOR',104,102),
(35,'DCS WHITE BEAMSTOP ASSY','D14308-110000-02','DYNAMIC COMPRESSION SECTOR BEAMLINE; RSS COMPONENTS; DCS STOP WHITE MOVABLE; ---; DCS WHITE BEAMSTOP ASSY',104,103),
(36,'ABSORBER & ACTUATOR ASSY','4105090910-150000-01','APS ENGINEERING SUPPORT; XOR/SRI-CAT BEAMLINES; P10 ID WHITE BEAM SHUTTER; ABSORBER & ACTUATOR ASSY; ABSORBER & ACTUATOR ASSY',104,108),
(37,'PHOTON MASK ASSEMBLY','D14301-120000','DYNAMIC COMPRESSION SECTOR BEAMLINE; RSS COMPONENTS; MASKS; -; PHOTON MASK ASSEMBLY',104,110),
(38,'PRE-MASK ASSEMBLY','D14301-130000','DYNAMIC COMPRESSION SECTOR BEAMLINE; RSS COMPONENTS; MASKS; DCS M3 ASSEMBLY; PRE-MASK ASSEMBLY',104,111),
(39,'SAFETY SHUTTER ASSEMBLY','D14305-110000','EXPERIMENTAL FACILITIES; IXS/NANO FRONT END; COMPONENT ASSEMBLIES; (16MM X 16MM APERTURE); SAFETY SHUTTER ASSEMBLY',104,115),
(40,'Bearing Kit','BNCP-30-100','Overhaul kit for Barber-Nichols cryogenic pump',132,116),
(41,'Pnuematic Function Module','FM-OAP102-0','',37,118),
(42,'GUIDED CYLINDER ASSEMBLY','4105090104-810100','A4 HEAVY LOAD PNEUMATIC; 33 mm - 8\" LINEAR ACTUATOR; GUIDED CYLINDER ASSEMBLY.                                                    \r\nSP-XXA4-0001 17-0350-42/1-1b, 17-0350-42/1-1a \"BOTH items are part of One Assempbly\"',134,122),
(43,'P4-30 Integral Shutter','P4-300000-03-2','',104,131),
(44,'Storage Ring Quadrupole','31010203-00029','.5 meter Quadrupole (AQ1, AQ3, BQ4)',83,134);
/*!40000 ALTER TABLE `component` ENABLE KEYS */;
UNLOCK TABLES;
