# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: ctlappsdev (MySQL 5.1.73)
# Database: cms
# Generation Time: 2014-04-15 21:19:27 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table collection_component
# ------------------------------------------------------------

LOCK TABLES `collection_component` WRITE;
/*!40000 ALTER TABLE `collection_component` DISABLE KEYS */;

INSERT INTO `collection_component` (`id`, `collection_id`, `component_id`, `quantity`, `description`, `tag`, `priority`)
VALUES
	(1,1,1,1,'Upstream end of arc','S01A:GV1',1.00),
	(2,1,3,1,'Inside A:Q1','S01A:VC1',2.00),
	(3,1,28,1,'Between A:Q1 and A:Q2','S01A:BPM1',3.00),
	(4,1,4,1,'Inside A:Q2','S01A:VC2',4.00),
	(5,1,5,1,'Inside A:M1','S01A:VC3',5.00),
	(6,1,29,1,'Downstream end of S01A:VC3','S01A:WA1',6.00),
	(7,1,6,1,'Inside A:Q3','S01A:VC4',7.00),
	(8,1,28,1,'Between A:Q3 and A:S1','S01A:BPM2',8.00),
	(9,1,7,1,'Inside A:S1','S01A:VC5',9.00),
	(10,1,8,1,'Between A:S1 and A:Q4','S01A:VC6',10.00),
	(11,1,33,1,'Mounted to side of S01A:VC6','S01A:CA1',11.00),
	(12,1,36,1,'Mounted to bottom of S01A:VC6','S01A:IP1',12.00),
	(13,1,35,1,'Mounted to top of S01A:VC6','S01A:VGP1',13.00),
	(14,1,9,1,'Inside A:Q4','S01A:VC7',14.00),
	(15,1,28,1,'Between A:Q4 and A:S2','S01A:BPM3',15.00),
	(16,1,10,1,'Inside A:S2 and A:Q5','S01A:VC8',16.00),
	(17,1,28,1,'Between A:Q5 and A:S3','S01A:BPM4',17.00),
	(18,1,11,1,'Inside A:S3','S01A:VC9',18.00),
	(19,1,28,1,'Between A:S3 and A:Q6','S01A:BPM5',19.00),
	(20,1,12,1,'Inside A:Q6','S01A:VC10',20.00),
	(21,1,13,1,'Inside A:M2','S01A:VC11',21.00),
	(22,1,30,1,'Downstream end of S01A:VC3','S01A:WA2',22.00),
	(23,1,14,1,'Inside A:Q7 ','S01A:VC12',23.00),
	(24,1,49,1,'Immediately upstream of S01A:GV2 ','S01A:TA1',24.00),
	(25,1,1,1,'Between A:Q7 and A:M3','S01A:GV2',25.00),
	(26,1,15,1,'Inside A:M3','S01A:VC13',26.00),
	(27,1,28,1,'Between A:M3 and A:Q8','S01A:BPM6',27.00),
	(28,1,16,1,'Inside A:Q8','S01A:VC14',28.00),
	(29,1,50,1,'Immediately upstream of S01A:VC15 ','S01A:TA2',29.00),
	(30,1,8,1,'Between A:Q8 and A:M4','S01A:VC15',30.00),
	(31,1,36,1,'Mounted to bottom of S01A:VC15','S01A:IP2',31.00),
	(32,1,35,1,'Mounted to top of S01A:VC15','S01A:VGP2',32.00),
	(33,1,17,1,'Inside A:M4 and B:Q8','S01A:VC16',33.00),
	(34,1,24,1,'Between S01A:VC6 and S01A:VC11','S01A:VC17',34.00),
	(35,1,25,1,'Between S01A:VC11 and ID front end','S01A:VC18',35.00),
	(36,1,28,1,'Between B:Q8 and B:M3','S01B:BPM6',36.00),
	(37,1,18,1,'Inside B:M3 and B:Q7','S01B:VC13',37.00),
	(38,1,84,1,'Immediately upstream of S01B:GV2 ','S01B:TA2',38.00),
	(39,1,2,1,'Between B:M3 and Q:Q8','S01B:GV2',39.00),
	(40,1,19,1,'Inside B:Q7','S01B:VC12',40.00),
	(41,1,20,1,'Inside B:M2','S01B:VC11',41.00),
	(42,1,33,1,'Downstream end of S01B:VC11','S01B:CA1',42.00),
	(43,1,31,1,'Downstream end of S01B:VC11','S01B:WA2',43.00),
	(44,1,12,1,'Inside B:Q6','S01B:VC10',44.00),
	(45,1,28,1,'Between B:Q6 and B:S3','S01B:BPM5',45.00),
	(46,1,11,1,'Inside B:S3','S01B:VC9',46.00),
	(47,1,28,1,'Between B:S3 and B:Q5','S01B:BPM4',47.00),
	(48,1,10,1,'Inside B:Q5 and B:S2','S01B:VC8',48.00),
	(49,1,28,1,'Between B:S2 and B:Q4','S01B:BPM3',49.00),
	(50,1,9,1,'Inside B:Q4','S01B:VC7',50.00),
	(51,1,84,1,'Immediately upstream of S01B:VC6 ','S01B:TA1',51.00),
	(52,1,8,1,'Between B:Q4 and B:S1','S01B:VC6',52.00),
	(53,1,36,1,'Mounted to bottom of S01B:VC6','S01B:IP1',53.00),
	(54,1,35,1,'Mounted to top of S01B:VC6','S01B:VGP1',54.00),
	(55,1,37,1,'Mounted to outboard side of S01B:VC6','S01B:RGA1',55.00),
	(56,1,21,1,'Inside B:S1','S01B:VC5',56.00),
	(57,1,28,1,'Between B:S1 and B:Q3','S01B:BPM2',57.00),
	(58,1,22,1,'Inside B:Q3','S01B:VC4',58.00),
	(59,1,23,1,'Inside B:M1','S01B:VC3',59.00),
	(60,1,32,1,'Downstream end of S01B:VC3','S01B:WA1',60.00),
	(61,1,4,1,'Inside B:Q2','S01B:VC2',61.00),
	(62,1,28,1,'Between B:Q2 and B:Q1','S01B:BPM1',62.00),
	(63,1,3,1,'Inside B:Q1','S01B:VC1',63.00),
	(64,1,1,1,'Downstream end of arc','S01B:GV1',64.00),
	(65,1,26,1,'Between S01B:VC11 and S01B:VC6','S01B:VC14',65.00),
	(66,1,27,1,'Between S01B:VC6 and BM front end','S01B:VC15',66.00),
	(67,1,38,1,'Storage ring mezzanine rack #','S01A:IPCTL1',67.00),
	(68,1,38,1,'Storage ring mezzanine rack #','S01B:IPCTL1',68.00),
	(69,1,39,1,'HV cable tray: S01A:IP1 to S01A:IPCTL1','S01A:IPCBL1',69.00),
	(70,1,39,1,'HV cable tray: S01A:IP2 to S01A:IPCTL1','S01A:IPCBL2',70.00),
	(71,1,39,1,'HV cable tray: S01B:IP1 to S01B:IPCTL1','S01B:IPCBL1',71.00),
	(72,1,40,1,'Storage ring mezzanine rack #','S01A:VGCTL1',72.00),
	(73,1,40,1,'Storage ring mezzanine rack #','S01B:VGCTL1',73.00),
	(74,1,41,1,'Signal cable tray: S01A:VGP1 to S01A:VGCTL1','S01A:VGCBL1',74.00),
	(75,1,42,1,'Signal cable tray: S01A:VGP1 to S01A:VGCTL1','S01A:VGCBL2',75.00),
	(76,1,41,1,'Signal cable tray: S01A:VGP2 to S01A:VGCTL1','S01A:VGCBL3',76.00),
	(77,1,42,1,'Signal cable tray: S01A:VGP2 to S01A:VGCTL1','S01A:VGCBL4',77.00),
	(78,1,41,1,'Signal cable tray: S01B:VGP1 to S01A:VGCTL2','S01B:VGCBL1',78.00),
	(79,1,42,1,'Signal cable tray: S01B:VGP1 to S01B:VGCTL2','S01B:VGCBL2',79.00),
	(80,1,43,1,'Signal cable tray: S01B:VGP1 to TBD','S01B:RGACBL',80.00),
	(81,1,44,1,'Storage ring mezzanine rack #','S01A:GVCTL1',81.00),
	(82,1,44,1,'Storage ring mezzanine rack #','S01A:GVCTL2',82.00),
	(83,1,44,1,'Storage ring mezzanine rack #','S01B:GVCTL2',83.00),
	(84,1,44,1,'Storage ring mezzanine rack #','S01B:GVCTL1',84.00),
	(85,1,45,1,'Signal cable tray: S01A:GV1 to S01A:GVCTL1','S01A:GVCBL1',85.00),
	(86,1,45,1,'Signal cable tray: S01B:GV1 to S01B:GVCTL1','S01A:GVCBL2',86.00),
	(87,1,46,1,'Storage ring mezzanine rack #','S01A:DIWCTL1',87.00),
	(88,1,46,1,'Storage ring mezzanine rack #','S01B:DIWCTL1',88.00),
	(89,1,47,1,'Storage ring mezzanine rack #','S01A:HTRCTL',89.00),
	(90,1,47,1,'Storage ring mezzanine rack #','S01B:HTRCTL',90.00),
	(91,1,48,1,'Signal cable tray: S01A:HTRCTL to S01A:VCX','S01A:HTRCBL',91.00),
	(92,1,48,1,'Signal cable tray: S01B:HTRCTL to S01B:VCX','S01B:HTRCBL',92.00),
	(93,2,58,1,'','',NULL),
	(94,2,107,1,'','IOCLIC2',NULL),
	(95,2,89,1,'','ADC1',NULL),
	(96,2,89,1,'','ADC2',NULL),
	(97,2,89,1,'','ADC3',NULL),
	(98,2,90,1,'','',NULL),
	(99,2,139,1,'','CM1',NULL),
	(100,2,139,1,'','CM2',NULL),
	(101,2,139,1,'','CM3',NULL),
	(102,3,137,1,'','',NULL),
	(103,3,84,1,'','',NULL),
	(104,3,89,1,'','',NULL),
	(105,3,88,1,NULL,'',NULL);

/*!40000 ALTER TABLE `collection_component` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
