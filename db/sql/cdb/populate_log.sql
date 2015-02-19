LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
INSERT INTO `log` VALUES
(2,'Received first unit ... bake out underway','2015-01-05 15:47:23',3,NULL),
(3,'Unit developed a leak, returning to vendor.  See attachment.','2015-01-07 10:47:59',3,NULL),
(4,'Received back from vendor, looks much better','2015-01-14 12:48:01',3,NULL),
(5,'Cloned from Generic: Plinth','2015-01-26 16:16:49',3,NULL),
(6,'12/01/2015 ; Began testing at 20KV after 14,841,368','2014-12-01 08:52:16',18,NULL),
(7,'11/??/2014 ; Began testing at 15KV','2014-11-01 08:52:18',18,NULL),
(8,'01/16/2015 ; Total 20KV pulses = 32,642,643','2015-01-16 08:52:15',18,NULL),
(10,'test','2015-02-03 14:34:19',4,NULL),
(11,'updated type','2015-02-03 14:48:59',4,NULL),
(12,'updated description','2015-02-03 14:50:06',4,NULL),
(13,'Added instance image','2015-02-04 10:23:45',4,NULL),
(14,'Added measurements doc','2015-02-04 10:29:01',4,NULL);
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;
