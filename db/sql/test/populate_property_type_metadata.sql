LOCK TABLES `property_type_metadata` WRITE;
SET SESSION FOREIGN_KEY_CHECKS=0;
/*!40000 ALTER TABLE `property_type_metadata` DISABLE KEYS */;
INSERT INTO `property_type_metadata` VALUES
(1,9,'heatLimit','Heat limit rating.'),
(2,9,'procurementStatus','Procurement status.'),
(3,9,'leadTime','Standard procurement lead time for this type of cable.'),
(4,9,'jacketColor','Jacket color.'),
(5,9,'conductors','Number of conductors/fibers.'),
(6,9,'voltageRating','Voltage rating (VRMS).'),
(7,9,'bendRadius','Bend radius in inches.'),
(8,9,'insulation','Description of cable insulation.'),
(9,9,'reelLength','Standard reel length for this type of cable.'),
(10,9,'url','Raw URL for documentation pdf file.'),
(11,9,'radTolerance','Radiation tolerance rating.'),
(12,9,'fireLoad','Fire load rating.'),
(13,9,'diameter','Diameter in inches (max).'),
(14,9,'altPartNumber','Manufacturer\'s alternate part number.'),
(15,9,'weight','Nominal weight in lbs/1000 feet.'),
(16,9,'imageUrl','Raw URL for image file.'),
(17,9,'totalLength','Total cable length required.'),
(18,9,'reelQuantity','Number of standard reels required for total length.'),
(19,10,'length','Installed length of cable.'),
(20,12,'legacyQrId','Legacy QR identifier, e.g., for cables that have already been assigned a QR code.'),
(21,12,'voltage','Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage'),
(22,12,'endpoint1Route','Routing waypoint for first endpoint.'),
(23,12,'alternateCableId','Alternate (e.g., group-specific) cable identifier.'),
(24,12,'importCableId','Import cable identifier.'),
(25,12,'externalCableName','External cable name (e.g., from CAD or routing tool).'),
(26,12,'endpoint2Description','Endpoint details useful for external editing.'),
(27,12,'endpoint2Route','Routing waypoint for second endpoint.'),
(28,12,'laying','Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle'),
(29,12,'endpoint1Description','Endpoint details useful for external editing.');
/*!40000 ALTER TABLE `property_type_metadata` ENABLE KEYS */;
UNLOCK TABLES;
