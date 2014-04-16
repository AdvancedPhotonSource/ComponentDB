# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: ctlappsdev (MySQL 5.1.73)
# Database: cms
# Generation Time: 2014-04-15 22:05:08 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table component_type_category
# ------------------------------------------------------------

LOCK TABLES `component_type_category` WRITE;
/*!40000 ALTER TABLE `component_type_category` DISABLE KEYS */;

INSERT INTO `component_type_category` (`id`, `name`, `description`)
VALUES
	(1,'Other',NULL),
	(2,'Housing',NULL),
	(3,'Instrumentation',NULL),
	(4,'Controls',NULL),
	(5,'Mechanical/Accelerator',NULL),
	(6,'Magnets',NULL),
	(7,'Power Supplies',NULL),
	(8,'Diagnostics',NULL),
	(9,'RF',NULL),
	(10,'Mechanical/Beamlines',NULL),
	(11,'Insertion Devices',NULL);

/*!40000 ALTER TABLE `component_type_category` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
