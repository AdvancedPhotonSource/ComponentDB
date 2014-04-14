# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: ctlappsdev (MySQL 5.1.73)
# Database: cms
# Generation Time: 2014-03-18 15:08:53 -0500
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table component_type
# ------------------------------------------------------------

LOCK TABLES `component_type` WRITE;
/*!40000 ALTER TABLE `component_type` DISABLE KEYS */;

INSERT INTO `component_type` (`id`, `name`, `description`, `component_type_category_id`)
VALUES
	(1,'Gate Valve',NULL,2),
	(2,'Gate Valve Controller',NULL,3),
	(3,'Vacuum Chamber',NULL,2),
	(4,'BPM',NULL,2),
	(5,'Ion Pump',NULL,2),
	(6,'Ion Pump Controller',NULL,3),
	(7,'Vacuum Gauge',NULL,2),
	(8,'Vacuum Gauge Controller',NULL,3),
	(9,'Cable',NULL,1),
	(10,'Absorber',NULL,2),
	(11,'RGA',NULL,3),
	(12,'Heat Tape',NULL,2),
	(13,'Heat Tape Controller',NULL,3),
	(14,'Controller',NULL,3),
	(15,'DI Water Mon Sys',NULL,3),
	(16,'Power Supply',NULL,3),
	(17,'Accelerating Structure',NULL,2),
	(18,'BPM Instrumentation',NULL,3),
	(19,'Network',NULL,3),
	(20,'ADC',NULL,3),
	(21,'DAC',NULL,3),
	(22,'Discrete I/O',NULL,3),
	(23,'CPU',NULL,3),
	(24,'Girder',NULL,2),
	(25,'Rack',NULL,2),
	(26,'Card Cage',NULL,3),
	(27,'Thermocouple',NULL,3),
	(28,'Instrumentation',NULL,3),
	(29,'Module',NULL,3),
	(30,'Blackbox',NULL,1),
	(31,'Current Monitor',NULL,3),
	(32,'RF Cavity',NULL,3),
	(33,'PLC',NULL,3),
	(34,'Magnet',NULL,10),
	(35,'Cabinet',NULL,3);

/*!40000 ALTER TABLE `component_type` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
