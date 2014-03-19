# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: ctlappsdev (MySQL 5.1.73)
# Database: cms
# Generation Time: 2014-03-18 15:04:06 -0500
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table component_connector
# ------------------------------------------------------------

LOCK TABLES `component_connector` WRITE;
/*!40000 ALTER TABLE `component_connector` DISABLE KEYS */;

INSERT INTO `component_connector` (`id`, `component_id`, `label`, `quantity`, `connector_type_id`)
VALUES
	(1,3,'',2.00,2),
	(2,4,'',2.00,2),
	(3,5,'',6.00,2),
	(4,6,'',2.00,2),
	(5,7,'',2.00,2),
	(6,8,'',2.00,2),
	(7,9,'',2.00,2),
	(8,10,'',2.00,2),
	(9,11,'',2.00,2),
	(10,12,'',2.00,2),
	(11,13,'',6.00,2),
	(12,14,'',2.00,2),
	(13,15,'',2.00,2),
	(14,16,'',2.00,2),
	(15,17,'',2.00,2),
	(16,18,'',2.00,2),
	(17,19,'',2.00,2),
	(18,20,'',6.00,2),
	(19,21,'',2.00,2),
	(20,22,'',2.00,2),
	(21,23,'',6.00,2),
	(22,29,'',2.00,2),
	(23,30,'',2.00,2),
	(24,31,'',2.00,2),
	(25,32,'',2.00,2),
	(26,33,'',2.00,2),
	(27,34,'',2.00,2),
	(28,1,'',6.00,11),
	(29,2,'',6.00,11),
	(30,1,'air',2.00,1),
	(31,2,'air',2.00,1),
	(32,28,'',4.00,7),
	(33,35,'',2.00,12),
	(34,36,'',1.00,9),
	(35,37,'',1.00,10),
	(36,38,'',1.00,9),
	(37,38,'',1.00,5),
	(38,38,'',2.00,11),
	(39,39,'',2.00,9),
	(40,40,'',2.00,12),
	(41,40,'',1.00,5),
	(42,41,'',2.00,12),
	(43,42,'',2.00,12),
	(44,43,'',2.00,4),
	(45,44,'',6.00,11),
	(46,44,'',1.00,4),
	(47,45,'',6.00,11),
	(48,37,'',1.00,13),
	(49,38,'',1.00,13),
	(50,40,'',1.00,13),
	(51,44,'',1.00,13),
	(52,47,'',1.00,13);

/*!40000 ALTER TABLE `component_connector` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
