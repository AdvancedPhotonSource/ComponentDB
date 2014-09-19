
LOCK TABLES `user_group` WRITE;

INSERT INTO `user_group` (`id`, `name`, `description`)
VALUES

(1,'CDB_ADMIN','System Admin Group'),
(2,'CS',NULL),
(3,'SSG','Software Services Group'),
(4,'CTL','Controls'),
(5,'MED',NULL),
(6,'VAC','Vacuum'),
(7,'RF',NULL),
(8,'PS','Power Systems'),
(9,'PSC-PA','APS Upgrade'),
(10,'DIAG','Diagnostics'),
(11,'MD','Magnetic Devices'),
(12,'APSU_VAC','Those working on vacuum for APSU');

UNLOCK TABLES;



