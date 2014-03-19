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

INSERT INTO `component` (`id`, `name`, `description`, `owner_user_id`, `owner_user_group_id`, `component_state_id`, `documentation_uri`, `estimated_cost`, `created_on_date_time`, `created_by_user_id`, `modified_on_date_time`, `modified_by_user_id`)
VALUES
	(1,'GateValve1','Ag-coated SST liner, 22 mm ID, standard SST 2 3/8\" CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(2,'GateValve2','Ag-coated SST liner, 22 mm ID with pocket for x-ray extraction, standard 2 3/8\" SST CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(3,'VacChamberAssy1','GTAW-welded SST 316L tube, SST 316L standard and chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(4,'VacChamberAssy2','GTAW-welded SST 316L tube, SST 316L standard and chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(5,'VacChamberAssy3','GTAW-welded aluminum extrusion with antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(6,'VacChamberAssy4','GTAW-welded, EDM-ed aluminum with side pocket for x-ray extraction, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(7,'VacChamberAssy5','GTAW-welded, EDM-ed aluminum with side pocket for x-ray extraction, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(8,'VacChamberAssy6','GTAW-welded SST 316L cross, SST 316L chain-clamped flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(9,'VacChamberAssy7','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(10,'VacChamberAssy8','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(11,'VacChamberAssy9','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(12,'VacChamberAssy10','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(13,'VacChamberAssy11','GTAW-welded aluminum extrusion with antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(14,'VacChamberAssy12','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(15,'VacChamberAssy13','E-beam welded Cu extrusion with internal NEG coating, bi-metal Cu-SST316L flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(16,'VacChamberAssy14','E-beam welded Cu extrusion with internal NEG coating, bi-metal Cu-SST316L flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(17,'VacChamberAssy15','E-beam welded Cu extrusion with internal NEG coating, bi-metal Cu-SST316L flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(18,'VacChamberAssy16','E-beam welded Cu extrusion with internal NEG coating, bi-metal Cu-SST316L flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(19,'VacChamberAssy17','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(20,'VacChamberAssy18','GTAW-welded aluminum extrusion with antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(21,'VacChamberAssy19','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(22,'VacChamberAssy20','GTAW-welded aluminum extrusion without antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(23,'VacChamberAssy21','GTAW-welded aluminum extrusion with antechamber, AL chain-clamped CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(24,'VacChamberAssy22','GTAW-welded SST316L rectandular tube, standard SST316L 2 3/4\" CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(25,'VacChamberAssy23','GTAW-welded SST316L rectandular tube, standard SST316L 2 3/4\" CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(26,'VacChamberAssy24','GTAW-welded SST316L rectandular tube, standard SST316L 2 3/4\" CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(27,'VacChamberAssy25','GTAW-welded SST316L rectandular tube, standard SST316L 2 3/4\" CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(28,'BpmRfShieldedBelAssy1','Bolted assembly of furnace-brazed Cu and BeCu liner, and SST 316L edge-welded bellows with SST 316L CF chain-clamped flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(29,'WedgeAbsorber1','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(30,'WedgeAbsorber2','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(31,'WedgeAbsorber3','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(32,'WedgeAbsorber4','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(33,'CrotchAbsorber1','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(34,'CrotchAbsorber2','Furnace-brazed Glidcop, copper tube, and SST 304 CF flange',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(35,'VacGgTreeAssy1','Bolted assembly of vendor items, SST 304 chambers, standard SST 304 CF flanges',1,1,3,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(36,'IonPump1','Vendor item, Ti/Ta electrodes with standard SST 304 CF flange',1,1,2,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(37,'Rga1','Residual Gas Analyzer',1,1,2,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(38,'IonPumpCtlr1','Rack-mountable chasis',1,1,2,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(39,'IonPumpHvCable1','Copper conductor with radiation-resistant insulation',1,1,2,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(40,'VacGaugeCtlr1','Rack-mountable chasis',1,1,2,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(41,'ConvGaugeCable1','Copper conductor with radiation-resistant insulation',1,1,2,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(42,'ColdCathodeGaugeCable1','Copper conductor with radiation-resistant insulation',1,1,2,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(43,'RgaCable1','Copper conductor with radiation-resistant insulation',1,1,2,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(44,'GateValveCtlr1','Rack-mountable chasis',4,4,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(45,'GateValveCable1','Copper conductor with radiation-resistant insulation',1,1,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(46,'DiWaterMontoringSys1','Cabinet with V-cones and transmitters, RTD transmitters, pressure monitoring, shut-off valves, etc',1,1,1,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(47,'VacChamberHeatTapeCtlr1','Rack-mountable chasis',1,1,1,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(48,'VacChamberHeatTapeCable1','Copper conductor with radiation-resistant insulation',1,1,1,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(49,'Generic Gate Valve','Generic Gate Valve',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(50,'Generic Gate Valve Controller','Generic Gate Valve Controller',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(51,'Generic Vacuum Chamber','Generic Vacuum Chamber',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(52,'Generic BPM','Generic BPM',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(53,'Generic Ion Pump','Generic Ion Pump',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(54,'Generic Ion Pump Controller','Generic Ion Pump Controller',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(55,'Generic Vacuum Gauge','Generic Vacuum Gauge',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(56,'Generic Vacuum Gauge Controller','Generic Vacuum Gauge Controller',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(57,'Generic Cable','Generic Cable',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(58,'Generic Absorber','Generic Absorber',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(59,'Generic RGA','Generic RGA',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(60,'Generic Heat Tape','Generic Heat Tape',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(61,'Generic Heat Tape Controller','Generic Heat Tape Controller',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(62,'Generic Controller','Generic Controller',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(63,'Generic DI Water Mon Sys','Generic DI Water Mon Sys',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(64,'Generic Power Supply','Generic Power Supply',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(65,'Generic Accelerating Structure','Generic Accelerating Structure',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(66,'Generic BPM Instrumentation','Generic BPM Instrumentation',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(67,'Generic Network','Generic Network',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(68,'Generic ADC','Generic ADC',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(69,'Generic DAC','Generic DAC',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(70,'Generic Discrete I/O','Generic Discrete I/O',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(71,'Generic CPU','Generic CPU',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(72,'Generic Girder','Generic Girder',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(73,'Generic Rack','Generic Rack',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(74,'Generic Card Cage','Generic Card Cage',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(75,'Generic Thermocouple','Generic Thermocouple',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(76,'Generic Instrumentation','Generic Instrumentation',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(77,'Generic Module','Generic Module',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(78,'Generic Blackbox','Generic Blackbox',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(79,'Generic Current Monitor','Generic Current Monitor',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(80,'Generic RF Cavity','Generic RF Cavity',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(81,'Generic Magnet','Generic Magnet',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2),
	(82,'Generic Cabinet','Generic Cabinet',2,2,4,NULL,NULL,'0000-00-00 00:00:00',2,'0000-00-00 00:00:00',2);

/*!40000 ALTER TABLE `component` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
