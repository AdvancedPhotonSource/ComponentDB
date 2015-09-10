LOCK TABLES `design` WRITE;
/*!40000 ALTER TABLE `design` DISABLE KEYS */;
INSERT INTO `design` VALUES
(2,'A Generic Collection of Generic Components','A typical parts list for an IOC using generic components',157),
(4,'Top DAQ Collection','This collection contains the central DAQ server(s) and references child collections to each DAQ IOC',160),
(5,'RTFB DAQ IOC','IOC with Reflective Memory board tapped in to the existing RTFB network',161),
(6,'SR RF DAQ','Workstation interfaced to LBL LLRF4 board via USB',162),
(7,'Double Sector Controller DAQ','Workstation with a PCIe card (SPEC) which contains an FPGA and SFP cage. ',163),
(13,'DMM (Dummy Modular Multiplet) - Design 1','',173),
(14,'DMM Water Handling System','',174),
(15,'DMM Magnets','',175),
(16,'DMM Vacuum Components','< add WBS number for now >\r\nVacuum components needed to simulate the multiplet assembly and testing for DMM - Design 1',176),
(17,'DMM Supports','',177),
(18,'DMM Mechanical Design','',184),
(19,'Quad Doublet (upstream)','',194),
(20,'Quad Doublet (downstream)','',195),
(21,'Sector layout','',196),
(22,'Straight multiplet (upstream)','',197),
(23,'Straight multiplet (downstream)','',198),
(24,'Curved FODO','',199),
(25,'L-Bend','',200),
(28,'Lattice','',207),
(29,'Girder 1','',428),
(31,'S27 RTFB Teststand (4x4)','',447),
(32,'Double Sector Controller (DSC) - uTCA Version','uTCA crate and modules for double sector controller prototype',448),
(33,'S27 Integrated orbit feedback prototype','',459),
(34,'R&D - Vacuum sector mockup','Includes all major components required to build a simplified mockup of one sector of the APS-U storage ring vacuum system.',493),
(35,'R&D - Vacuum flange and joint test stand','',498),
(36,'M1 Dipole Prototypes','Container for the M1 design alternative prototypes',534),
(37,'M1 Measurement in MM1','Test setup for M1 measurements',547),
(38,'M1 Measurement PS Control/Monitoring','Required power supply components to support M1 measurements in MM1',569),
(39,'S1 Magnet Yoke','',652);
/*!40000 ALTER TABLE `design` ENABLE KEYS */;
UNLOCK TABLES;
