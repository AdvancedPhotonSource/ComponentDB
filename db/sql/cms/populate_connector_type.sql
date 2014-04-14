# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: ctlappsdev (MySQL 5.1.73)
# Database: cms
# Generation Time: 2014-03-18 15:10:30 -0500
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table connector_type
# ------------------------------------------------------------

LOCK TABLES `connector_type` WRITE;
/*!40000 ALTER TABLE `connector_type` DISABLE KEYS */;

INSERT INTO `connector_type` (`id`, `name`, `description`, `connector_type_category_id`)
VALUES
	(1,'TBD',NULL,1),
	(2,'Parker CPI 3/\" compression',NULL,1),
	(3,'RJ45',NULL,1),
	(4,'DB-9 Female',NULL,1),
	(5,'DB-9 Male',NULL,1),
	(6,'ST (fiber)',NULL,1),
	(7,'SMA',NULL,1),
	(8,'SMB',NULL,1),
	(9,'Gamma SAFECONN',NULL,1),
	(10,'Ametek',NULL,1),
	(11,'screw terminal',NULL,1),
	(12,'Telavac',NULL,1),
	(13,'AC Plug',NULL,1);

/*!40000 ALTER TABLE `connector_type` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
