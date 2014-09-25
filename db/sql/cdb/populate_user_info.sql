
LOCK TABLES `user_info` WRITE;

INSERT INTO `user_info` (`id`, `username`, `first_name`, `last_name`, `middle_name`, `email`, `password`, `description`)
VALUES

(1,'cdb','CDB','System Account',NULL,'cdb@aps.anl.gov','cdb',NULL),
(2,'bstill','Ben','Stillwell','','bstill@aps.anl.gov',NULL,NULL),
(3,'nda','Ned','Arnold','','nda@aps.anl.gov',NULL,NULL),
(4,'sveseli','Sinisa','Veseli','','sveseli@aps.anl.gov','sv',NULL),
(5,'benes','Scott','Benes','','benes@aps.anl.gov',NULL,NULL),
(6,'nschwarz','Nicholas','Schwarz','','nschwarz@aps.anl.gov',NULL,NULL),
(7,'cease','Herman','Cease','','cease@aps.anl.gov',NULL,NULL),
(8,'carwar','John','Carwardine','','carwar@aps.anl.gov',NULL,NULL),
(9,'decker','Glenn','Decker','','decker@aps.anl.gov',NULL,NULL),
(10,'ngrossman','Nancy','Grossman','','ngrossman@aps.anl.gov',NULL,NULL),
(11,'cprokuski','Chuck','Prokuski','','cprokuski@aps.anl.gov',NULL,NULL),
(12,'jaski','Mark','Jaski','','jaski@aps.anl.gov',NULL,NULL),
(13,'juw','Ju','Wang','','juw@aps.anl.gov',NULL,NULL),
(14,'blill','Bob','Lill','','blill@aps.anl.gov',NULL,NULL),
(15,'horan','Doug','Horan','','horan@aps.anl.gov',NULL,NULL),
(16,'fornek','Tom','Fornek','','fornek@aps.anl.gov',NULL,NULL),
(17,'rif','Richard','Farnsworth','','rif@aps.anl.gov',NULL,NULL),
(18,'frl','Frank','Lenkszus','','frl@aps.anl.gov',NULL,NULL),
(19,'preissner','Curt','Preissner','','preissner@aps.anl.gov',NULL,NULL),
(20,'lhm','Leonard','Morrison','','lhm@aps.anl.gov',NULL,NULL),
(21,'jieliu','Jie','Liu','','jieliu@aps.anl.gov',NULL,NULL),
(22,'doose','Charles','Doose','','doose@aps.anl.gov',NULL,NULL);

UNLOCK TABLES;

