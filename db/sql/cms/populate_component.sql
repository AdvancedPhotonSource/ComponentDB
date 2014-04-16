# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: ctlappsdev (MySQL 5.1.73)
# Database: cms
# Generation Time: 2014-04-15 21:23:55 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table component
# ------------------------------------------------------------

LOCK TABLES `component` WRITE;
/*!40000 ALTER TABLE `component` DISABLE KEYS */;

INSERT INTO `component` (`id`, `name`, `description`, `component_state_id`, `documentation_uri`, `estimated_cost`, `entity_info_id`)
VALUES
	(1,'Gate Valve (RF Shielded)','Ag-coated SST liner, 22 mm ID, standard SST 2 3/8\" CF flanges',1,'https://edpstage.aps.anl.gov/browse/index/collection_id/1280',NULL,1),
	(2,'Gate Valve (xray extraction)','Ag-coated SST liner, 22 mm ID with pocket for x-ray extraction, standard 2 3/8\" SST CF flanges',1,NULL,NULL,2),
	(3,'VacChamberAssy1','GTAW-welded SST 316L tube, SST 316L standard and chain-clamped CF flanges',1,NULL,NULL,3),
	(4,'VacChamberAssy2','GTAW-welded SST 316L tube, SST 316L standard and chain-clamped CF flanges',1,NULL,NULL,4),
	(5,'VacChamberAssy3','GTAW-welded aluminum extrusion with antechamber, AL chain-clamped CF flanges',1,NULL,NULL,5),
	(6,'VacChamberAssy4','GTAW-welded, EDM-ed aluminum with side pocket for x-ray extraction, AL chain-clamped CF flanges',1,'https://edpstage.aps.anl.gov/browse/index/collection_id/1338',NULL,6),
	(7,'VacChamberAssy5','GTAW-welded, EDM-ed aluminum with side pocket for x-ray extraction, AL chain-clamped CF flanges',1,NULL,NULL,7),
	(8,'VacChamberAssy6','GTAW-welded SST 316L cross, SST 316L chain-clamped flanges',1,NULL,NULL,8),
	(9,'VacChamberAssy7','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,NULL,NULL,9),
	(10,'VacChamberAssy8','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,NULL,NULL,10),
	(11,'VacChamberAssy9','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,NULL,NULL,11),
	(12,'VacChamberAssy10','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,NULL,NULL,12),
	(13,'VacChamberAssy11','GTAW-welded aluminum extrusion with antechamber, AL chain-clamped CF flanges',1,NULL,NULL,13),
	(14,'VacChamberAssy12','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,NULL,NULL,14),
	(15,'VacChamberAssy13','E-beam welded Cu extrusion with internal NEG coating, bi-metal Cu-SST316L flanges',1,NULL,NULL,15),
	(16,'VacChamberAssy14','E-beam welded Cu extrusion with internal NEG coating, bi-metal Cu-SST316L flanges',1,NULL,NULL,16),
	(17,'VacChamberAssy15','E-beam welded Cu extrusion with internal NEG coating, bi-metal Cu-SST316L flanges',1,NULL,NULL,17),
	(18,'VacChamberAssy16','E-beam welded Cu extrusion with internal NEG coating, bi-metal Cu-SST316L flanges',1,NULL,NULL,18),
	(19,'VacChamberAssy17','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,NULL,NULL,19),
	(20,'VacChamberAssy18','GTAW-welded aluminum extrusion with antechamber, AL chain-clamped CF flanges',1,NULL,NULL,20),
	(21,'VacChamberAssy19','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,NULL,NULL,21),
	(22,'VacChamberAssy20','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,NULL,NULL,22),
	(23,'VacChamberAssy21','GTAW-welded aluminum extrusion with antechamber, AL chain-clamped CF flanges',1,NULL,NULL,23),
	(24,'VacChamberAssy22','GTAW-welded SST316L rectandular tube, standard SST316L 2 3/4\" CF flanges',1,NULL,NULL,24),
	(25,'VacChamberAssy23','GTAW-welded SST316L rectandular tube, standard SST316L 2 3/4\" CF flanges',1,NULL,NULL,25),
	(26,'VacChamberAssy24','GTAW-welded SST316L rectandular tube, standard SST316L 2 3/4\" CF flanges',1,NULL,NULL,26),
	(27,'VacChamberAssy25','GTAW-welded SST316L rectandular tube, standard SST316L 2 3/4\" CF flanges',1,NULL,NULL,27),
	(28,'BpmRfShieldedBelAssy1','Bolted assembly of furnace-brazed Cu and BeCu liner, and SST 316L edge-welded bellows with SST 316L CF chain-clamped flanges',1,'https://edpstage.aps.anl.gov/browse/index/collection_id/1284',NULL,28),
	(29,'WedgeAbsorber1','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,NULL,NULL,29),
	(30,'WedgeAbsorber2','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,NULL,NULL,30),
	(31,'WedgeAbsorber3','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,NULL,NULL,31),
	(32,'WedgeAbsorber4','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,NULL,NULL,32),
	(33,'CrotchAbsorber1','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,NULL,NULL,33),
	(34,'CrotchAbsorber2','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,NULL,NULL,34),
	(35,'VacGgTreeAssy1','Bolted assembly of vendor items, SST 304 chambers, standard SST 304 CF flanges',1,NULL,NULL,35),
	(36,'Ion Pump (Gamma)','Vendor item, Ti/Ta electrodes with standard SST 304 CF flange',1,'https://edpstage.aps.anl.gov/browse/index/collection_id/1279',NULL,36),
	(37,'RGA (Ametek)','Residual Gas Analyzer',1,'https://edpstage.aps.anl.gov/browse/index/collection_id/1278',NULL,37),
	(38,'IonPumpCtlr1','Rack-mountable chasis',1,NULL,NULL,38),
	(39,'IonPumpHvCable1','Copper conductor with radiation-resistant insulation',1,NULL,NULL,39),
	(40,'VacGaugeCtlr1','Rack-mountable chasis',1,NULL,NULL,40),
	(41,'ConvGaugeCable1','Copper conductor with radiation-resistant insulation',1,NULL,NULL,41),
	(42,'ColdCathodeGaugeCable1','Copper conductor with radiation-resistant insulation',1,NULL,NULL,42),
	(43,'RgaCable1','Copper conductor with radiation-resistant insulation',1,NULL,NULL,43),
	(44,'VVC210 Gate Valve Controller','Rack-mountable chasis',4,'https://edpstage.aps.anl.gov/browse/index/collection_id/1258',NULL,44),
	(45,'GateValveCable1','Copper conductor with radiation-resistant insulation',1,NULL,NULL,45),
	(46,'DiWaterMontoringSys1','Cabinet with V-cones and transmitters, RTD transmitters, pressure monitoring, shut-off valves, etc',1,NULL,NULL,46),
	(47,'VacChamberHeatTapeCtlr1','Rack-mountable chasis',1,NULL,NULL,47),
	(48,'VacChamberHeatTapeCable1','Copper conductor with radiation-resistant insulation',1,NULL,NULL,48),
	(49,'TransAbsorber1','',1,NULL,NULL,49),
	(50,'TransAbsorber2','',1,NULL,NULL,50),
	(51,'Generic: Housing Component',NULL,6,NULL,NULL,51),
	(52,'Generic: Building',NULL,6,NULL,NULL,52),
	(53,'Generic: Room',NULL,6,NULL,NULL,53),
	(54,'Generic: Area',NULL,6,NULL,NULL,54),
	(55,'Generic: Rack',NULL,6,NULL,NULL,55),
	(56,'Generic: Cabinet',NULL,6,NULL,NULL,56),
	(57,'Generic: Enclosure',NULL,6,NULL,NULL,57),
	(58,'Generic: Card Cage',NULL,6,NULL,NULL,58),
	(59,'Generic: Instrumentation Component',NULL,6,NULL,NULL,59),
	(60,'Generic: Controller - Generic',NULL,6,NULL,NULL,60),
	(61,'Generic: Controller - Ion Pump',NULL,6,NULL,NULL,61),
	(62,'Generic: Controller - Gate Valve',NULL,6,NULL,NULL,62),
	(63,'Generic: Controller - Vacuum Gauge',NULL,6,NULL,NULL,63),
	(64,'Generic: Controller - Heat tape',NULL,6,NULL,NULL,64),
	(65,'Generic: Controller - PID',NULL,6,NULL,NULL,65),
	(66,'Generic: Controller - Temperature',NULL,6,NULL,NULL,66),
	(67,'Generic: Controller - Motor',NULL,6,NULL,NULL,67),
	(68,'Generic: Controller - Power Supply',NULL,6,NULL,NULL,68),
	(69,'Generic: Controller - PLC',NULL,6,NULL,NULL,69),
	(70,'Generic: Controller - Water flow',NULL,6,NULL,NULL,70),
	(71,'Generic: Controller - RGA',NULL,6,NULL,NULL,71),
	(72,'Generic: Monitoring System',NULL,6,NULL,NULL,72),
	(73,'Generic: Gauge/Sensor - strain',NULL,6,NULL,NULL,73),
	(74,'Generic: Gauge/Sensor  - vacuum',NULL,6,NULL,NULL,74),
	(75,'Generic: Gauge/Sensor  - thermocouple',NULL,6,NULL,NULL,75),
	(76,'Generic: Gauge/Sensor  - RTD',NULL,6,NULL,NULL,76),
	(77,'Generic: Gauge/Sensor  - pressure',NULL,6,NULL,NULL,77),
	(78,'Generic: Gauge/Sensor - waterflow',NULL,6,NULL,NULL,78),
	(79,'Generic: Gauge/Sensor - RGA',NULL,6,NULL,NULL,79),
	(80,'Generic: Motor',NULL,6,NULL,NULL,80),
	(81,'Generic: Motor - Driver',NULL,6,NULL,NULL,81),
	(82,'Generic: Motor - Position Monitor',NULL,6,NULL,NULL,82),
	(83,'Generic: Motor - Limit Switch',NULL,6,NULL,NULL,83),
	(84,'Generic: Cable',NULL,6,NULL,NULL,84),
	(85,'Generic: Patch Panel',NULL,6,NULL,NULL,85),
	(86,'Generic: Adapter',NULL,6,NULL,NULL,86),
	(87,'Generic: Module',NULL,6,NULL,NULL,87),
	(88,'Generic: Blackbox',NULL,6,NULL,NULL,88),
	(89,'Generic: ADC',NULL,6,NULL,NULL,89),
	(90,'Generic: DAC',NULL,6,NULL,NULL,90),
	(91,'Generic: Discrete I/O',NULL,6,NULL,NULL,91),
	(92,'Generic: CPU',NULL,6,NULL,NULL,92),
	(93,'Generic: FPGA',NULL,6,NULL,NULL,93),
	(94,'Generic: Oscilloscope/DSA',NULL,6,NULL,NULL,94),
	(95,'Generic: Counter',NULL,6,NULL,NULL,95),
	(96,'Generic: Function Generator',NULL,6,NULL,NULL,96),
	(97,'Generic: Frequency Synthesizer',NULL,6,NULL,NULL,97),
	(98,'Generic: Voltmeter',NULL,6,NULL,NULL,98),
	(99,'Generic: Power Supply',NULL,6,NULL,NULL,99),
	(100,'Generic: Amplifier',NULL,6,NULL,NULL,100),
	(101,'Generic: Multiplexor',NULL,6,NULL,NULL,101),
	(102,'Generic: Interlock',NULL,6,NULL,NULL,102),
	(103,'Generic: Readout/Display',NULL,6,NULL,NULL,103),
	(104,'Generic: Controls Component',NULL,6,NULL,NULL,104),
	(105,'Generic: Network',NULL,6,NULL,NULL,105),
	(106,'Generic: Timing',NULL,6,NULL,NULL,106),
	(107,'Generic: IOC',NULL,6,NULL,NULL,107),
	(108,'Generic: Server/Workstation',NULL,6,NULL,NULL,108),
	(109,'Generic: Video',NULL,6,NULL,NULL,109),
	(110,'Generic: Interface Adapter',NULL,6,NULL,NULL,110),
	(111,'Generic: Accelerator Component',NULL,6,NULL,NULL,111),
	(112,'Generic: Girder',NULL,6,NULL,NULL,112),
	(113,'Generic: Stand',NULL,6,NULL,NULL,113),
	(114,'Generic: Vacuum Chamber',NULL,6,NULL,NULL,114),
	(115,'Generic: Transition Piece',NULL,6,NULL,NULL,115),
	(116,'Generic: Vacuum Pump',NULL,6,NULL,NULL,116),
	(117,'Generic: Absorber',NULL,6,NULL,NULL,117),
	(118,'Generic: Heat Tape',NULL,6,NULL,NULL,118),
	(119,'Generic: Flag',NULL,6,NULL,NULL,119),
	(120,'Generic: Scraper',NULL,6,NULL,NULL,120),
	(121,'Generic: Bellows',NULL,6,NULL,NULL,121),
	(122,'Generic: Assembly',NULL,6,NULL,NULL,122),
	(123,'Generic: Vacuum Flange',NULL,6,NULL,NULL,123),
	(124,'Generic: Vacuum Seal',NULL,6,NULL,NULL,124),
	(125,'Generic: Vacuum Valve',NULL,6,NULL,NULL,125),
	(126,'Generic: Fastener',NULL,6,NULL,NULL,126),
	(127,'Generic: Water line',NULL,6,NULL,NULL,127),
	(128,'Generic: Water fitting',NULL,6,NULL,NULL,128),
	(129,'Generic: Water seal',NULL,6,NULL,NULL,129),
	(130,'Generic: Magnet Component',NULL,6,NULL,NULL,130),
	(131,'Generic: Trim',NULL,6,NULL,NULL,131),
	(132,'Generic: Dipole',NULL,6,NULL,NULL,132),
	(133,'Generic: Quadraple',NULL,6,NULL,NULL,133),
	(134,'Generic: Sextapole',NULL,6,NULL,NULL,134),
	(135,'Generic: PS Component',NULL,6,NULL,NULL,135),
	(136,'Generic: Diagnostic Component',NULL,6,NULL,NULL,136),
	(137,'Generic: BPM',NULL,6,NULL,NULL,137),
	(138,'Generic: Loss Monitor',NULL,6,NULL,NULL,138),
	(139,'Generic: Current Monitor',NULL,6,NULL,NULL,139),
	(140,'Generic: Screen',NULL,6,NULL,NULL,140),
	(141,'Generic: Optics',NULL,6,NULL,NULL,141),
	(142,'Generic: RF Component',NULL,6,NULL,NULL,142),
	(143,'Generic: cavity/accelerating structure',NULL,6,NULL,NULL,143),
	(144,'Generic: phase shifter',NULL,6,NULL,NULL,144),
	(145,'Generic: attenuator',NULL,6,NULL,NULL,145),
	(146,'Generic: coupler',NULL,6,NULL,NULL,146),
	(147,'Generic: envelope detector',NULL,6,NULL,NULL,147),
	(148,'Generic: phase monitor/detector',NULL,6,NULL,NULL,148),
	(149,'Generic: klystron',NULL,6,NULL,NULL,149),
	(150,'Generic: HVPS',NULL,6,NULL,NULL,150),
	(151,'Generic: splitter',NULL,6,NULL,NULL,151),
	(152,'Generic: RF Source',NULL,6,NULL,NULL,152),
	(153,'Generic: circulator',NULL,6,NULL,NULL,153),
	(154,'Generic: Beamline Component',NULL,6,NULL,NULL,154),
	(155,'Generic: Insertion Device Component',NULL,6,NULL,NULL,155);

/*!40000 ALTER TABLE `component` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
