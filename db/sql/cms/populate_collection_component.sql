# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: ctlappsdev (MySQL 5.1.73)
# Database: cms
# Generation Time: 2014-04-03 13:29:43 +0000
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

INSERT INTO `collection_component` (`id`, `collection_id`, `component_id`, `quantity`, `description`, `tag`)
VALUES
	(1,1,1,1,'Upstream end of arc','S01A:GV1'),
	(2,1,3,1,'Inside A:Q1','S01A:VC1'),
	(3,1,28,1,'Between A:Q1 and A:Q2','S01A:BPM1'),
	(4,1,4,1,'Inside A:Q2','S01A:VC2'),
	(5,1,5,1,'Inside A:M1','S01A:VC3'),
	(6,1,29,1,'Downstream end of S01A:VC3','S01A:WA1'),
	(7,1,6,1,'Inside A:Q3','S01A:VC4'),
	(8,1,28,1,'Between A:Q3 and A:S1','S01A:BPM2'),
	(9,1,7,1,'Inside A:S1','S01A:VC5'),
	(10,1,8,1,'Between A:S1 and A:Q4','S01A:VC6'),
	(11,1,33,1,'Mounted to side of S01A:VC6','S01A:CA1'),
	(12,1,36,1,'Mounted to bottom of S01A:VC6','S01A:IP1'),
	(13,1,35,1,'Mounted to top of S01A:VC6','S01A:VGP1'),
	(14,1,9,1,'Inside A:Q4','S01A:VC7'),
	(15,1,28,1,'Between A:Q4 and A:S2','S01A:BPM3'),
	(16,1,10,1,'Inside A:S2 and A:Q5','S01A:VC8'),
	(17,1,28,1,'Between A:Q5 and A:S3','S01A:BPM4'),
	(18,1,11,1,'Inside A:S3','S01A:VC9'),
	(19,1,28,1,'Between A:S3 and A:Q6','S01A:BPM5'),
	(20,1,12,1,'Inside A:Q6','S01A:VC10'),
	(21,1,13,1,'Inside A:M2','S01A:VC11'),
	(22,1,30,1,'Downstream end of S01A:VC3','S01A:WA2'),
	(23,1,14,1,'Inside A:Q7 ','S01A:VC12'),
	(24,1,83,1,'Immediately upstream of S01A:GV2 ','S01A:TA1'),
	(25,1,1,1,'Between A:Q7 and A:M3','S01A:GV2'),
	(26,1,15,1,'Inside A:M3','S01A:VC13'),
	(27,1,28,1,'Between A:M3 and A:Q8','S01A:BPM6'),
	(28,1,16,1,'Inside A:Q8','S01A:VC14'),
	(29,1,84,1,'Immediately upstream of S01A:VC15 ','S01A:TA2'),
	(30,1,8,1,'Between A:Q8 and A:M4','S01A:VC15'),
	(31,1,36,1,'Mounted to bottom of S01A:VC15','S01A:IP2'),
	(32,1,35,1,'Mounted to top of S01A:VC15','S01A:VGP2'),
	(33,1,17,1,'Inside A:M4 and B:Q8','S01A:VC16'),
	(34,1,24,1,'Between S01A:VC6 and S01A:VC11','S01A:VC17'),
	(35,1,25,1,'Between S01A:VC11 and ID front end','S01A:VC18'),
	(36,1,28,1,'Between B:Q8 and B:M3','S01B:BPM6'),
	(37,1,18,1,'Inside B:M3 and B:Q7','S01B:VC13'),
	(38,1,84,1,'Immediately upstream of S01B:GV2 ','S01B:TA2'),
	(39,1,2,1,'Between B:M3 and Q:Q8','S01B:GV2'),
	(40,1,19,1,'Inside B:Q7','S01B:VC12'),
	(41,1,20,1,'Inside B:M2','S01B:VC11'),
	(42,1,33,1,'Downstream end of S01B:VC11','S01B:CA1'),
	(43,1,31,1,'Downstream end of S01B:VC11','S01B:WA2'),
	(44,1,12,1,'Inside B:Q6','S01B:VC10'),
	(45,1,28,1,'Between B:Q6 and B:S3','S01B:BPM5'),
	(46,1,11,1,'Inside B:S3','S01B:VC9'),
	(47,1,28,1,'Between B:S3 and B:Q5','S01B:BPM4'),
	(48,1,10,1,'Inside B:Q5 and B:S2','S01B:VC8'),
	(49,1,28,1,'Between B:S2 and B:Q4','S01B:BPM3'),
	(50,1,9,1,'Inside B:Q4','S01B:VC7'),
	(51,1,84,1,'Immediately upstream of S01B:VC6 ','S01B:TA1'),
	(52,1,8,1,'Between B:Q4 and B:S1','S01B:VC6'),
	(53,1,36,1,'Mounted to bottom of S01B:VC6','S01B:IP1'),
	(54,1,35,1,'Mounted to top of S01B:VC6','S01B:VGP1'),
	(55,1,37,1,'Mounted to outboard side of S01B:VC6','S01B:RGA1'),
	(56,1,21,1,'Inside B:S1','S01B:VC5'),
	(57,1,28,1,'Between B:S1 and B:Q3','S01B:BPM2'),
	(58,1,22,1,'Inside B:Q3','S01B:VC4'),
	(59,1,23,1,'Inside B:M1','S01B:VC3'),
	(60,1,32,1,'Downstream end of S01B:VC3','S01B:WA1'),
	(61,1,4,1,'Inside B:Q2','S01B:VC2'),
	(62,1,28,1,'Between B:Q2 and B:Q1','S01B:BPM1'),
	(63,1,3,1,'Inside B:Q1','S01B:VC1'),
	(64,1,1,1,'Downstream end of arc','S01B:GV1'),
	(65,1,26,1,'Between S01B:VC11 and S01B:VC6','S01B:VC14'),
	(66,1,27,1,'Between S01B:VC6 and BM front end','S01B:VC15'),
	(67,1,38,1,'Storage ring mezzanine rack #','S01A:IPCTL1'),
	(68,1,38,1,'Storage ring mezzanine rack #','S01B:IPCTL1'),
	(69,1,39,1,'HV cable tray: S01A:IP1 to S01A:IPCTL1','S01A:IPCBL1'),
	(70,1,39,1,'HV cable tray: S01A:IP2 to S01A:IPCTL1','S01A:IPCBL2'),
	(71,1,39,1,'HV cable tray: S01B:IP1 to S01B:IPCTL1','S01B:IPCBL1'),
	(72,1,40,1,'Storage ring mezzanine rack #','S01A:VGCTL1'),
	(73,1,40,1,'Storage ring mezzanine rack #','S01B:VGCTL1'),
	(74,1,41,1,'Signal cable tray: S01A:VGP1 to S01A:VGCTL1','S01A:VGCBL1'),
	(75,1,42,1,'Signal cable tray: S01A:VGP1 to S01A:VGCTL1','S01A:VGCBL2'),
	(76,1,41,1,'Signal cable tray: S01A:VGP2 to S01A:VGCTL1','S01A:VGCBL3'),
	(77,1,42,1,'Signal cable tray: S01A:VGP2 to S01A:VGCTL1','S01A:VGCBL4'),
	(78,1,41,1,'Signal cable tray: S01B:VGP1 to S01A:VGCTL2','S01B:VGCBL1'),
	(79,1,42,1,'Signal cable tray: S01B:VGP1 to S01B:VGCTL2','S01B:VGCBL2'),
	(80,1,43,1,'Signal cable tray: S01B:VGP1 to TBD','S01B:RGACBL'),
	(81,1,44,1,'Storage ring mezzanine rack #','S01A:GVCTL1'),
	(82,1,44,1,'Storage ring mezzanine rack #','S01A:GVCTL2'),
	(83,1,44,1,'Storage ring mezzanine rack #','S01B:GVCTL2'),
	(84,1,44,1,'Storage ring mezzanine rack #','S01B:GVCTL1'),
	(85,1,45,1,'Signal cable tray: S01A:GV1 to S01A:GVCTL1','S01A:GVCBL1'),
	(86,1,45,1,'Signal cable tray: S01B:GV1 to S01B:GVCTL1','S01A:GVCBL2'),
	(87,1,46,1,'Storage ring mezzanine rack #','S01A:DIWCTL1'),
	(88,1,46,1,'Storage ring mezzanine rack #','S01B:DIWCTL1'),
	(89,1,47,1,'Storage ring mezzanine rack #','S01A:HTRCTL'),
	(90,1,47,1,'Storage ring mezzanine rack #','S01B:HTRCTL'),
	(91,1,48,1,'Signal cable tray: S01A:HTRCTL to S01A:VCX','S01A:HTRCBL'),
	(92,1,48,1,'Signal cable tray: S01B:HTRCTL to S01B:VCX','S01B:HTRCBL'),
	(93,2,74,1,'',''),
	(94,2,71,1,'','IOCLIC2'),
	(95,2,68,1,'','ADC1'),
	(96,2,68,1,'','ADC2'),
	(97,2,68,1,'','ADC3'),
	(98,2,69,1,'',''),
	(99,2,79,1,'','CM1'),
	(100,2,79,1,'','CM2'),
	(101,2,79,1,'','CM3'),
	(102,3,66,1,'',''),
	(103,3,57,1,'',''),
	(104,3,68,1,'',''),
	(105,3,78,1,NULL,'');

/*!40000 ALTER TABLE `collection_component` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
