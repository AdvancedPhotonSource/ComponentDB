# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: ctlappsdev (MySQL 5.1.73)
# Database: cms
# Generation Time: 2014-03-18 14:58:31 -0500
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
	(49,'Generic Gate Valve','Generic Gate Valve',2,NULL,NULL,49),
	(50,'Generic Gate Valve Controller','Generic Gate Valve Controller',2,NULL,NULL,50),
	(51,'Generic Vacuum Chamber','Generic Vacuum Chamber',2,NULL,NULL,51),
	(52,'Generic BPM','Generic BPM',2,NULL,NULL,52),
	(53,'Generic Ion Pump','Generic Ion Pump',2,NULL,NULL,53),
	(54,'Generic Ion Pump Controller','Generic Ion Pump Controller',2,NULL,NULL,54),
	(55,'Generic Vacuum Gauge','Generic Vacuum Gauge',2,NULL,NULL,55),
	(56,'Generic Vacuum Gauge Controller','Generic Vacuum Gauge Controller',2,NULL,NULL,56),
	(57,'Generic Cable','Generic Cable',2,NULL,NULL,57),
	(58,'Generic Absorber','Generic Absorber',2,NULL,NULL,58),
	(59,'Generic RGA','Generic RGA',2,NULL,NULL,59),
	(60,'Generic Heat Tape','Generic Heat Tape',2,NULL,NULL,60),
	(61,'Generic Heat Tape Controller','Generic Heat Tape Controller',2,NULL,NULL,61),
	(62,'Generic Controller','Generic Controller',2,NULL,NULL,62),
	(63,'Generic DI Water Mon Sys','Generic DI Water Mon Sys',2,NULL,NULL,63),
	(64,'Generic Power Supply','Generic Power Supply',2,NULL,NULL,64),
	(65,'Generic Accelerating Structure','Generic Accelerating Structure',2,NULL,NULL,65),
	(66,'Generic BPM Instrumentation','Generic BPM Instrumentation',2,NULL,NULL,66),
	(67,'Generic Network','Generic Network',2,NULL,NULL,67),
	(68,'Generic ADC','Generic ADC',2,NULL,NULL,68),
	(69,'Generic DAC','Generic DAC',2,NULL,NULL,69),
	(70,'Generic Discrete I/O','Generic Discrete I/O',2,NULL,NULL,70),
	(71,'Generic CPU','Generic CPU',2,NULL,NULL,71),
	(72,'Generic Girder','Generic Girder',2,NULL,NULL,72),
	(73,'Generic Rack','Generic Rack',2,NULL,NULL,73),
	(74,'Generic Card Cage','Generic Card Cage',2,NULL,NULL,74),
	(75,'Generic Thermocouple','Generic Thermocouple',2,NULL,NULL,75),
	(76,'Generic Instrumentation','Generic Instrumentation',2,NULL,NULL,76),
	(77,'Generic Module','Generic Module',2,NULL,NULL,77),
	(78,'Generic Blackbox','Generic Blackbox',2,NULL,NULL,78),
	(79,'Generic Current Monitor','Generic Current Monitor',2,NULL,NULL,79),
	(80,'Generic RF Cavity','Generic RF Cavity',2,NULL,NULL,80),
	(81,'Generic Magnet','Generic Magnet',2,NULL,NULL,81),
	(82,'Generic Cabinet','Generic Cabinet',2,NULL,NULL,82);
	(83,'TransAbsorber1','',1,NULL,NULL,83),
	(84,'TransAbsorber1','',1,NULL,NULL,84),

/*!40000 ALTER TABLE `component` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
