
--
-- Table structure for table `aoi`
--

DROP TABLE IF EXISTS `aoi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi` (
  `aoi_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi_name` varchar(60) DEFAULT NULL,
  `aoi_cognizant1_id` int(11) DEFAULT NULL,
  `aoi_cognizant2_id` int(11) DEFAULT NULL,
  `aoi_customer_contact_id` int(11) DEFAULT NULL,
  `aoi_status_id` int(11) DEFAULT NULL,
  `aoi_description` mediumtext,
  `aoi_worklog` mediumtext,
  `aoi_keyword` varchar(255) DEFAULT NULL,
  `aoi_customer_group_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`aoi_id`),
  UNIQUE KEY `aoi_name` (`aoi_name`),
  KEY `fk_aoi_cognizant1_id` (`aoi_cognizant1_id`),
  KEY `fk_aoi_cognizant2_id` (`aoi_cognizant2_id`),
  KEY `fk_aoi_customer_contact_id` (`aoi_customer_contact_id`),
  KEY `fk_aoi_status_id` (`aoi_status_id`),
  KEY `idx_aoi_group_name_id` (`aoi_customer_group_id`),
  CONSTRAINT `aoi_ibfk_1` FOREIGN KEY (`aoi_cognizant1_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `aoi_ibfk_2` FOREIGN KEY (`aoi_cognizant2_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `aoi_ibfk_3` FOREIGN KEY (`aoi_customer_contact_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `aoi_ibfk_4` FOREIGN KEY (`aoi_status_id`) REFERENCES `aoi_status` (`aoi_status_id`),
  CONSTRAINT `aoi_ibfk_5` FOREIGN KEY (`aoi_customer_group_id`) REFERENCES `group_name` (`group_name_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1538 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `aoi`
--

LOCK TABLES `aoi` WRITE;
/*!40000 ALTER TABLE `aoi` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_crawler`
--

DROP TABLE IF EXISTS `aoi_crawler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_crawler` (
  `aoi_crawler_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_id` int(11) DEFAULT NULL,
  `ioc_boot_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`aoi_crawler_id`),
  KEY `idx_aoi_crawler_ioc_id` (`ioc_id`),
  KEY `fk_ioc_boot_id` (`ioc_boot_id`),
  CONSTRAINT `aoi_crawler_ibfk_1` FOREIGN KEY (`ioc_id`) REFERENCES `ioc` (`ioc_id`),
  CONSTRAINT `aoi_crawler_ibfk_2` FOREIGN KEY (`ioc_boot_id`) REFERENCES `ioc_boot` (`ioc_boot_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11627 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_crawler`
--

LOCK TABLES `aoi_crawler` WRITE;
/*!40000 ALTER TABLE `aoi_crawler` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_crawler` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_criticality`
--

DROP TABLE IF EXISTS `aoi_criticality`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_criticality` (
  `aoi_criticality_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi_id` int(11) DEFAULT NULL,
  `criticality_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`aoi_criticality_id`),
  KEY `idx_criticality_aoi_id` (`aoi_id`),
  KEY `fk_criticality_id` (`criticality_id`),
  CONSTRAINT `aoi_criticality_ibfk_1` FOREIGN KEY (`aoi_id`) REFERENCES `aoi` (`aoi_id`),
  CONSTRAINT `aoi_criticality_ibfk_2` FOREIGN KEY (`criticality_id`) REFERENCES `criticality_type` (`criticality_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1485 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_criticality`
--

LOCK TABLES `aoi_criticality` WRITE;
/*!40000 ALTER TABLE `aoi_criticality` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_criticality` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_document`
--

DROP TABLE IF EXISTS `aoi_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_document` (
  `doc_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi_id` int(11) DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `doc_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`doc_id`),
  KEY `idx_doc_aoi_id` (`aoi_id`),
  KEY `fk_doc_type_id` (`doc_type_id`),
  CONSTRAINT `aoi_document_ibfk_1` FOREIGN KEY (`aoi_id`) REFERENCES `aoi` (`aoi_id`),
  CONSTRAINT `aoi_document_ibfk_2` FOREIGN KEY (`doc_type_id`) REFERENCES `doc_type` (`doc_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=392 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_document`
--

LOCK TABLES `aoi_document` WRITE;
/*!40000 ALTER TABLE `aoi_document` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_epics_record`
--

DROP TABLE IF EXISTS `aoi_epics_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_epics_record` (
  `aoi_epics_record_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi_ioc_stcmd_line_id` int(11) DEFAULT NULL,
  `rec_nm` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`aoi_epics_record_id`),
  KEY `fk_aoi_epics_record_stcmdline_id` (`aoi_ioc_stcmd_line_id`),
  CONSTRAINT `aoi_epics_record_ibfk_1` FOREIGN KEY (`aoi_ioc_stcmd_line_id`) REFERENCES `aoi_ioc_stcmd_line` (`aoi_ioc_stcmd_line_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15286684 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_epics_record`
--

LOCK TABLES `aoi_epics_record` WRITE;
/*!40000 ALTER TABLE `aoi_epics_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_epics_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_ioc_stcmd_line`
--

DROP TABLE IF EXISTS `aoi_ioc_stcmd_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_ioc_stcmd_line` (
  `aoi_ioc_stcmd_line_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi_id` int(11) DEFAULT NULL,
  `ioc_stcmd_line_id` int(11) DEFAULT NULL,
  `pv_filter` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`aoi_ioc_stcmd_line_id`),
  KEY `idx_ioc_stcmd_aoi_id` (`aoi_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1207761 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_ioc_stcmd_line`
--

LOCK TABLES `aoi_ioc_stcmd_line` WRITE;
/*!40000 ALTER TABLE `aoi_ioc_stcmd_line` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_ioc_stcmd_line` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_machine`
--

DROP TABLE IF EXISTS `aoi_machine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_machine` (
  `aoi_machine_id` int(11) NOT NULL AUTO_INCREMENT,
  `machine_id` int(11) DEFAULT NULL,
  `aoi_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`aoi_machine_id`),
  KEY `idx_machine_aoi_id` (`aoi_id`),
  CONSTRAINT `aoi_machine_ibfk_1` FOREIGN KEY (`aoi_id`) REFERENCES `aoi` (`aoi_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1545 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_machine`
--

LOCK TABLES `aoi_machine` WRITE;
/*!40000 ALTER TABLE `aoi_machine` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_machine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_note`
--

DROP TABLE IF EXISTS `aoi_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_note` (
  `aoi_note_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi_id` int(11) DEFAULT NULL,
  `aoi_note_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `aoi_note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`aoi_note_id`),
  KEY `idx_note_aoi_id` (`aoi_id`),
  CONSTRAINT `aoi_note_ibfk_1` FOREIGN KEY (`aoi_id`) REFERENCES `aoi` (`aoi_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_note`
--

LOCK TABLES `aoi_note` WRITE;
/*!40000 ALTER TABLE `aoi_note` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_plc_stcmd_line`
--

DROP TABLE IF EXISTS `aoi_plc_stcmd_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_plc_stcmd_line` (
  `aoi_plc_stcmd_line_id` int(11) NOT NULL AUTO_INCREMENT,
  `plc_id` int(11) DEFAULT NULL,
  `aoi_id` int(11) DEFAULT NULL,
  `ioc_stcmd_line_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`aoi_plc_stcmd_line_id`),
  KEY `idx_aoi_plc_stcmd_line_id` (`aoi_id`),
  CONSTRAINT `aoi_plc_stcmd_line_ibfk_1` FOREIGN KEY (`aoi_id`) REFERENCES `aoi` (`aoi_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16849 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_plc_stcmd_line`
--

LOCK TABLES `aoi_plc_stcmd_line` WRITE;
/*!40000 ALTER TABLE `aoi_plc_stcmd_line` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_plc_stcmd_line` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_relation`
--

DROP TABLE IF EXISTS `aoi_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_relation` (
  `aoi_relation_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi1_id` int(11) DEFAULT NULL,
  `aoi2_id` int(11) DEFAULT NULL,
  `aoi1_relation_type_id` int(11) DEFAULT NULL,
  `aoi2_relation_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`aoi_relation_id`),
  KEY `fk_aoi1_id` (`aoi1_id`),
  KEY `fk_aoi2_id` (`aoi2_id`),
  KEY `fk_aoi1_relation_type_id` (`aoi1_relation_type_id`),
  KEY `fk_aoi2_relation_type_id` (`aoi2_relation_type_id`),
  CONSTRAINT `aoi_relation_ibfk_1` FOREIGN KEY (`aoi1_id`) REFERENCES `aoi` (`aoi_id`),
  CONSTRAINT `aoi_relation_ibfk_2` FOREIGN KEY (`aoi2_id`) REFERENCES `aoi` (`aoi_id`),
  CONSTRAINT `aoi_relation_ibfk_3` FOREIGN KEY (`aoi1_relation_type_id`) REFERENCES `aoi_relation_type` (`aoi_relation_type_id`),
  CONSTRAINT `aoi_relation_ibfk_4` FOREIGN KEY (`aoi2_relation_type_id`) REFERENCES `aoi_relation_type` (`aoi_relation_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_relation`
--

LOCK TABLES `aoi_relation` WRITE;
/*!40000 ALTER TABLE `aoi_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_relation_type`
--

DROP TABLE IF EXISTS `aoi_relation_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_relation_type` (
  `aoi_relation_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi_relation` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`aoi_relation_type_id`),
  UNIQUE KEY `aoi_relation` (`aoi_relation`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_relation_type`
--

LOCK TABLES `aoi_relation_type` WRITE;
/*!40000 ALTER TABLE `aoi_relation_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_relation_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_status`
--

DROP TABLE IF EXISTS `aoi_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_status` (
  `aoi_status_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi_status` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`aoi_status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_status`
--

LOCK TABLES `aoi_status` WRITE;
/*!40000 ALTER TABLE `aoi_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_techsys`
--

DROP TABLE IF EXISTS `aoi_techsys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_techsys` (
  `aoi_techsystem_id` int(11) NOT NULL AUTO_INCREMENT,
  `technical_system_id` int(11) DEFAULT NULL,
  `aoi_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`aoi_techsystem_id`),
  KEY `idx_techsystem_aoi_id` (`aoi_id`),
  CONSTRAINT `aoi_techsys_ibfk_1` FOREIGN KEY (`aoi_id`) REFERENCES `aoi` (`aoi_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1544 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_techsys`
--

LOCK TABLES `aoi_techsys` WRITE;
/*!40000 ALTER TABLE `aoi_techsys` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_techsys` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aoi_topdisplay`
--

DROP TABLE IF EXISTS `aoi_topdisplay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aoi_topdisplay` (
  `aoi_topdisplay_id` int(11) NOT NULL AUTO_INCREMENT,
  `aoi_id` int(11) DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`aoi_topdisplay_id`),
  KEY `idx_topdisplay_aoi_id` (`aoi_id`),
  CONSTRAINT `aoi_topdisplay_ibfk_1` FOREIGN KEY (`aoi_id`) REFERENCES `aoi` (`aoi_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2700 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aoi_topdisplay`
--

LOCK TABLES `aoi_topdisplay` WRITE;
/*!40000 ALTER TABLE `aoi_topdisplay` DISABLE KEYS */;
/*!40000 ALTER TABLE `aoi_topdisplay` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aps_component`
--

DROP TABLE IF EXISTS `aps_component`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aps_component` (
  `aps_component_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_id` int(11) NOT NULL DEFAULT '0',
  `serial_number` varchar(60) DEFAULT NULL,
  `group_name_id` int(11) DEFAULT NULL,
  `verified` tinyint(1) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`aps_component_id`),
  KEY `idx_component_id_ac` (`component_id`),
  KEY `idx_group_name_id_ac` (`group_name_id`),
  CONSTRAINT `aps_component_ibfk_1` FOREIGN KEY (`component_id`) REFERENCES `component` (`component_id`),
  CONSTRAINT `aps_component_ibfk_2` FOREIGN KEY (`group_name_id`) REFERENCES `group_name` (`group_name_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37561 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aps_component`
--

LOCK TABLES `aps_component` WRITE;
/*!40000 ALTER TABLE `aps_component` DISABLE KEYS */;
/*!40000 ALTER TABLE `aps_component` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aps_ioc`
--

DROP TABLE IF EXISTS `aps_ioc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aps_ioc` (
  `aps_ioc_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_id` int(11) NOT NULL DEFAULT '0',
  `location` varchar(60) DEFAULT NULL,
  `TermServRackNo` varchar(60) DEFAULT NULL,
  `TermServName` varchar(60) DEFAULT NULL,
  `TermServPort` int(11) DEFAULT NULL,
  `TermServFiberConvCh` varchar(60) DEFAULT NULL,
  `TermServFiberConvPort` int(11) DEFAULT NULL,
  `PrimEnetSwRackNo` varchar(60) DEFAULT NULL,
  `PrimEnetSwitch` varchar(60) DEFAULT NULL,
  `PrimEnetBlade` varchar(60) DEFAULT NULL,
  `PrimEnetMedConvCh` varchar(60) DEFAULT NULL,
  `PrimEnetPort` int(11) DEFAULT NULL,
  `PrimMediaConvPort` int(11) DEFAULT NULL,
  `SecEnetSwRackNo` varchar(60) DEFAULT NULL,
  `SecEnetSwitch` varchar(60) DEFAULT NULL,
  `SecEnetBlade` varchar(60) DEFAULT NULL,
  `SecEnetPort` int(11) DEFAULT NULL,
  `SecEnetMedConvCh` varchar(60) DEFAULT NULL,
  `SecMedConvPort` int(11) DEFAULT NULL,
  `cog_developer_id` int(11) DEFAULT NULL,
  `cog_technician_id` int(11) DEFAULT NULL,
  `general_functions` text,
  `pre_boot_instr` text,
  `post_boot_instr` text,
  `power_cycle_caution` text,
  `sysreset_reqd` tinyint(1) DEFAULT '0',
  `inhibit_auto_reboot` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`aps_ioc_id`),
  KEY `idx_ioc_id_ai` (`ioc_id`),
  KEY `fk_cog_developer_id_ai` (`cog_developer_id`),
  KEY `fk_cog_technician_id_ai` (`cog_technician_id`),
  CONSTRAINT `aps_ioc_ibfk_1` FOREIGN KEY (`ioc_id`) REFERENCES `ioc` (`ioc_id`),
  CONSTRAINT `aps_ioc_ibfk_2` FOREIGN KEY (`cog_developer_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `aps_ioc_ibfk_3` FOREIGN KEY (`cog_technician_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=491 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aps_ioc`
--

LOCK TABLES `aps_ioc` WRITE;
/*!40000 ALTER TABLE `aps_ioc` DISABLE KEYS */;
/*!40000 ALTER TABLE `aps_ioc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_action`
--

DROP TABLE IF EXISTS `audit_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `audit_action` (
  `audit_action_id` int(11) NOT NULL AUTO_INCREMENT,
  `audit_action_type_id` int(11) NOT NULL DEFAULT '0',
  `action_key` int(11) DEFAULT NULL,
  `action_desc` varchar(100) DEFAULT NULL,
  `person_id` int(11) DEFAULT NULL,
  `action_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`audit_action_id`),
  KEY `idx_audit_action_type_id_aa` (`audit_action_type_id`),
  KEY `idx_person_id_aa` (`person_id`),
  CONSTRAINT `audit_action_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `audit_action_ibfk_2` FOREIGN KEY (`audit_action_type_id`) REFERENCES `audit_action_type` (`audit_action_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=132313 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_action`
--

LOCK TABLES `audit_action` WRITE;
/*!40000 ALTER TABLE `audit_action` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_action_type`
--

DROP TABLE IF EXISTS `audit_action_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `audit_action_type` (
  `audit_action_type_id` int(11) NOT NULL DEFAULT '0',
  `action_name` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`audit_action_type_id`),
  UNIQUE KEY `action_name` (`action_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_action_type`
--

LOCK TABLES `audit_action_type` WRITE;
/*!40000 ALTER TABLE `audit_action_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_action_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `base_component_type`
--

DROP TABLE IF EXISTS `base_component_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `base_component_type` (
  `base_component_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_type_name` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`base_component_type_id`),
  UNIQUE KEY `component_type_name` (`component_type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `base_component_type`
--

LOCK TABLES `base_component_type` WRITE;
/*!40000 ALTER TABLE `base_component_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `base_component_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cable`
--

DROP TABLE IF EXISTS `cable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cable` (
  `cable_id` int(11) NOT NULL AUTO_INCREMENT,
  `color` varchar(60) DEFAULT NULL,
  `label` varchar(60) DEFAULT NULL,
  `component_port_a_id` int(11) DEFAULT NULL,
  `component_port_b_id` int(11) DEFAULT NULL,
  `pin_detail` tinyint(1) DEFAULT NULL,
  `virtual` tinyint(1) DEFAULT NULL,
  `dest_desc` varchar(60) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `cable_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`cable_id`),
  KEY `idx_component_port_a_id` (`component_port_a_id`),
  KEY `idx_component_port_b_id` (`component_port_b_id`),
  KEY `idx_label` (`label`),
  CONSTRAINT `cable_ibfk_1` FOREIGN KEY (`component_port_a_id`) REFERENCES `component_port` (`component_port_id`),
  CONSTRAINT `cable_ibfk_2` FOREIGN KEY (`component_port_b_id`) REFERENCES `component_port` (`component_port_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11017 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cable`
--

LOCK TABLES `cable` WRITE;
/*!40000 ALTER TABLE `cable` DISABLE KEYS */;
/*!40000 ALTER TABLE `cable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cable_type`
--

DROP TABLE IF EXISTS `cable_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cable_type` (
  `cable_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `cable_type` varchar(50) DEFAULT NULL,
  `cable_type_description` varchar(255) DEFAULT NULL,
  `cable_diameter` float(9,3) DEFAULT NULL,
  PRIMARY KEY (`cable_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cable_type`
--

LOCK TABLES `cable_type` WRITE;
/*!40000 ALTER TABLE `cable_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `cable_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chc_beamline_interest`
--

DROP TABLE IF EXISTS `chc_beamline_interest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chc_beamline_interest` (
  `chc_beamline_interest_id` int(11) NOT NULL,
  `interest` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`chc_beamline_interest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chc_beamline_interest`
--

LOCK TABLES `chc_beamline_interest` WRITE;
/*!40000 ALTER TABLE `chc_beamline_interest` DISABLE KEYS */;
/*!40000 ALTER TABLE `chc_beamline_interest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component`
--

DROP TABLE IF EXISTS `component`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component` (
  `component_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_type_id` int(11) NOT NULL DEFAULT '0',
  `component_instance_name` varchar(60) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `image_uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`component_id`),
  KEY `idx_component_type_id_c` (`component_type_id`),
  CONSTRAINT `component_ibfk_1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`component_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37561 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component`
--

LOCK TABLES `component` WRITE;
/*!40000 ALTER TABLE `component` DISABLE KEYS */;
/*!40000 ALTER TABLE `component` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_instance`
--

DROP TABLE IF EXISTS `component_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_instance` (
  `component_instance_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_id` int(11) DEFAULT NULL,
  `component_type_id` int(11) NOT NULL,
  `serial_number` varchar(60) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_instance_id`),
  KEY `idx_component_id_ci` (`component_id`),
  KEY `idx_component_type_id_ci` (`component_type_id`),
  KEY `idx_serial_number_ci` (`serial_number`),
  CONSTRAINT `component_instance_ibfk_1` FOREIGN KEY (`component_id`) REFERENCES `component` (`component_id`),
  CONSTRAINT `component_instance_ibfk_2` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`component_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_instance`
--

LOCK TABLES `component_instance` WRITE;
/*!40000 ALTER TABLE `component_instance` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_instance_state`
--

DROP TABLE IF EXISTS `component_instance_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_instance_state` (
  `component_instance_state_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_instance_id` int(11) NOT NULL,
  `person_id` int(11) NOT NULL,
  `entered_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `component_state_id` int(11) NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `reference_data_1` varchar(255) DEFAULT NULL,
  `reference_data_2` timestamp NULL DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_instance_state_id`),
  KEY `idx_component_instance_id_cis` (`component_instance_id`),
  KEY `idx_component_state_id_cis` (`component_state_id`),
  CONSTRAINT `component_instance_state_ibfk_1` FOREIGN KEY (`component_instance_id`) REFERENCES `component_instance` (`component_instance_id`),
  CONSTRAINT `component_instance_state_ibfk_2` FOREIGN KEY (`component_state_id`) REFERENCES `component_state` (`component_state_id`)
) ENGINE=InnoDB AUTO_INCREMENT=264 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_instance_state`
--

LOCK TABLES `component_instance_state` WRITE;
/*!40000 ALTER TABLE `component_instance_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_instance_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_port`
--

DROP TABLE IF EXISTS `component_port`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_port` (
  `component_port_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_port_type_id` int(11) DEFAULT NULL,
  `component_id` int(11) DEFAULT NULL,
  `component_port_name` varchar(40) DEFAULT NULL,
  `component_port_order` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_port_id`),
  KEY `idx_component_port_type_id_cp` (`component_port_type_id`),
  KEY `idx_component_id` (`component_id`),
  CONSTRAINT `component_port_ibfk_1` FOREIGN KEY (`component_port_type_id`) REFERENCES `component_port_type` (`component_port_type_id`),
  CONSTRAINT `component_port_ibfk_2` FOREIGN KEY (`component_id`) REFERENCES `component` (`component_id`)
) ENGINE=InnoDB AUTO_INCREMENT=176435 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_port`
--

LOCK TABLES `component_port` WRITE;
/*!40000 ALTER TABLE `component_port` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_port` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_port_template`
--

DROP TABLE IF EXISTS `component_port_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_port_template` (
  `component_port_template_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_type_id` int(11) DEFAULT NULL,
  `component_port_type_id` int(11) DEFAULT NULL,
  `component_port_name` varchar(40) DEFAULT NULL,
  `component_port_order` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_port_template_id`),
  KEY `idx_component_type_id_cpt` (`component_type_id`),
  KEY `idx_component_port_type_id_cpt` (`component_port_type_id`),
  CONSTRAINT `component_port_template_ibfk_1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`component_type_id`),
  CONSTRAINT `component_port_template_ibfk_2` FOREIGN KEY (`component_port_type_id`) REFERENCES `component_port_type` (`component_port_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5936 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_port_template`
--

LOCK TABLES `component_port_template` WRITE;
/*!40000 ALTER TABLE `component_port_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_port_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_port_type`
--

DROP TABLE IF EXISTS `component_port_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_port_type` (
  `component_port_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_port_type` varchar(60) DEFAULT NULL,
  `component_port_group` varchar(60) DEFAULT NULL,
  `component_port_pin_count` int(11) DEFAULT NULL,
  `modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(10) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_port_type_id`),
  UNIQUE KEY `component_port_type` (`component_port_type`)
) ENGINE=InnoDB AUTO_INCREMENT=337 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_port_type`
--

LOCK TABLES `component_port_type` WRITE;
/*!40000 ALTER TABLE `component_port_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_port_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_rel`
--

DROP TABLE IF EXISTS `component_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_rel` (
  `component_rel_id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_component_id` int(11) DEFAULT NULL,
  `child_component_id` int(11) DEFAULT NULL,
  `logical_order` int(11) DEFAULT NULL,
  `logical_desc` varchar(60) DEFAULT NULL,
  `component_rel_type_id` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `verified_person_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_rel_id`),
  KEY `idx_parent_component_id` (`parent_component_id`),
  KEY `idx_child_component_id` (`child_component_id`),
  KEY `idx_component_rel_type_id_cr` (`component_rel_type_id`),
  KEY `idx_verified_person_id_cr` (`verified_person_id`),
  CONSTRAINT `component_rel_ibfk_1` FOREIGN KEY (`parent_component_id`) REFERENCES `component` (`component_id`),
  CONSTRAINT `component_rel_ibfk_2` FOREIGN KEY (`child_component_id`) REFERENCES `component` (`component_id`),
  CONSTRAINT `component_rel_ibfk_3` FOREIGN KEY (`component_rel_type_id`) REFERENCES `component_rel_type` (`component_rel_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=83072 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_rel`
--

LOCK TABLES `component_rel` WRITE;
/*!40000 ALTER TABLE `component_rel` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_rel_type`
--

DROP TABLE IF EXISTS `component_rel_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_rel_type` (
  `component_rel_type_id` int(11) NOT NULL DEFAULT '0',
  `rel_name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`component_rel_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_rel_type`
--

LOCK TABLES `component_rel_type` WRITE;
/*!40000 ALTER TABLE `component_rel_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_rel_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_semaphore`
--

DROP TABLE IF EXISTS `component_semaphore`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_semaphore` (
  `component_semaphore_id` int(11) NOT NULL DEFAULT '0',
  `semaphore` int(11) NOT NULL DEFAULT '0',
  `userid` varchar(30) DEFAULT NULL,
  `modified_date` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_semaphore`
--

LOCK TABLES `component_semaphore` WRITE;
/*!40000 ALTER TABLE `component_semaphore` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_semaphore` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_state`
--

DROP TABLE IF EXISTS `component_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_state` (
  `component_state_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_state_category_id` int(11) NOT NULL,
  `state` varchar(60) NOT NULL,
  PRIMARY KEY (`component_state_id`),
  KEY `idx_component_state_category_id_cs` (`component_state_category_id`),
  CONSTRAINT `component_state_ibfk_1` FOREIGN KEY (`component_state_category_id`) REFERENCES `component_state_category` (`component_state_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_state`
--

LOCK TABLES `component_state` WRITE;
/*!40000 ALTER TABLE `component_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_state_category`
--

DROP TABLE IF EXISTS `component_state_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_state_category` (
  `component_state_category_id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(60) NOT NULL,
  PRIMARY KEY (`component_state_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_state_category`
--

LOCK TABLES `component_state_category` WRITE;
/*!40000 ALTER TABLE `component_state_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_state_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_type`
--

DROP TABLE IF EXISTS `component_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_type` (
  `component_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_type_name` varchar(60) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `form_factor_id` int(11) DEFAULT NULL,
  `mfg_id` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `vdescription` text,
  `chc_beamline_interest_id` int(11) DEFAULT NULL,
  `chc_contact_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_type_id`),
  UNIQUE KEY `component_type_name` (`component_type_name`,`mark_for_delete`),
  KEY `idx_form_factor_id` (`form_factor_id`),
  KEY `idx_mfg_id` (`mfg_id`),
  KEY `idx_chc_beamline_interest_id` (`chc_beamline_interest_id`),
  KEY `idx_chc_contact_id` (`chc_contact_id`),
  CONSTRAINT `component_type_ibfk_1` FOREIGN KEY (`form_factor_id`) REFERENCES `form_factor` (`form_factor_id`),
  CONSTRAINT `component_type_ibfk_2` FOREIGN KEY (`mfg_id`) REFERENCES `mfg` (`mfg_id`),
  CONSTRAINT `component_type_ibfk_3` FOREIGN KEY (`chc_beamline_interest_id`) REFERENCES `chc_beamline_interest` (`chc_beamline_interest_id`),
  CONSTRAINT `component_type_ibfk_4` FOREIGN KEY (`chc_contact_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1103 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_type`
--

LOCK TABLES `component_type` WRITE;
/*!40000 ALTER TABLE `component_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_type_document`
--

DROP TABLE IF EXISTS `component_type_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_type_document` (
  `component_type_document_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_type_id` int(11) DEFAULT NULL,
  `document_type` varchar(100) DEFAULT NULL,
  `uri_id` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_type_document_id`),
  KEY `idx_component_type_id_ctd` (`component_type_id`),
  KEY `idx_uri_id_ctd` (`uri_id`),
  CONSTRAINT `component_type_document_ibfk_1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`component_type_id`),
  CONSTRAINT `component_type_document_ibfk_2` FOREIGN KEY (`uri_id`) REFERENCES `uri` (`uri_id`)
) ENGINE=InnoDB AUTO_INCREMENT=651 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_type_document`
--

LOCK TABLES `component_type_document` WRITE;
/*!40000 ALTER TABLE `component_type_document` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_type_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_type_function`
--

DROP TABLE IF EXISTS `component_type_function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_type_function` (
  `component_type_function_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_type_id` int(11) DEFAULT NULL,
  `function_id` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_type_function_id`),
  KEY `idx_component_type_id_ctf` (`component_type_id`),
  KEY `idx_function_id` (`function_id`),
  CONSTRAINT `component_type_function_ibfk_1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`component_type_id`),
  CONSTRAINT `component_type_function_ibfk_2` FOREIGN KEY (`function_id`) REFERENCES `function` (`function_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3632 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_type_function`
--

LOCK TABLES `component_type_function` WRITE;
/*!40000 ALTER TABLE `component_type_function` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_type_function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_type_if`
--

DROP TABLE IF EXISTS `component_type_if`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_type_if` (
  `component_type_if_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_type_id` int(11) DEFAULT NULL,
  `required` tinyint(1) DEFAULT NULL,
  `presented` tinyint(1) DEFAULT NULL,
  `max_children` int(11) DEFAULT NULL,
  `component_rel_type_id` int(11) DEFAULT NULL,
  `component_type_if_type_id` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_type_if_id`),
  KEY `idx_component_type_id_cti` (`component_type_id`),
  KEY `idx_component_rel_type_id_cti` (`component_rel_type_id`),
  KEY `idx_component_type_if_type_id` (`component_type_if_type_id`),
  CONSTRAINT `component_type_if_ibfk_1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`component_type_id`),
  CONSTRAINT `component_type_if_ibfk_2` FOREIGN KEY (`component_rel_type_id`) REFERENCES `component_rel_type` (`component_rel_type_id`),
  CONSTRAINT `component_type_if_ibfk_3` FOREIGN KEY (`component_type_if_type_id`) REFERENCES `component_type_if_type` (`component_type_if_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5294 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_type_if`
--

LOCK TABLES `component_type_if` WRITE;
/*!40000 ALTER TABLE `component_type_if` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_type_if` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_type_if_type`
--

DROP TABLE IF EXISTS `component_type_if_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_type_if_type` (
  `component_type_if_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_rel_type_id` int(11) DEFAULT NULL,
  `if_type` varchar(100) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_type_if_type_id`),
  UNIQUE KEY `if_type` (`if_type`,`component_rel_type_id`,`mark_for_delete`),
  KEY `idx_component_rel_type_id_ctit` (`component_rel_type_id`),
  CONSTRAINT `component_type_if_type_ibfk_1` FOREIGN KEY (`component_rel_type_id`) REFERENCES `component_rel_type` (`component_rel_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=399 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_type_if_type`
--

LOCK TABLES `component_type_if_type` WRITE;
/*!40000 ALTER TABLE `component_type_if_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_type_if_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_type_person`
--

DROP TABLE IF EXISTS `component_type_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_type_person` (
  `component_type_person_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_type_id` int(11) DEFAULT NULL,
  `person_id` int(11) DEFAULT NULL,
  `role_name_id` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`component_type_person_id`),
  KEY `idx_component_type_id_ctcp` (`component_type_id`),
  KEY `idx_person_id_ctcp` (`person_id`),
  KEY `idx_role_name_id` (`role_name_id`),
  CONSTRAINT `component_type_person_ibfk_1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`component_type_id`),
  CONSTRAINT `component_type_person_ibfk_2` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `component_type_person_ibfk_3` FOREIGN KEY (`role_name_id`) REFERENCES `role_name` (`role_name_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2311 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_type_person`
--

LOCK TABLES `component_type_person` WRITE;
/*!40000 ALTER TABLE `component_type_person` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_type_person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_type_status`
--

DROP TABLE IF EXISTS `component_type_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_type_status` (
  `component_type_status_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_type_id` int(11) DEFAULT NULL,
  `spare_qty` int(11) DEFAULT NULL,
  `stock_qty` int(11) DEFAULT NULL,
  `spare_loc` varchar(100) DEFAULT NULL,
  `instantiated` tinyint(1) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nrtl_status` varchar(10) DEFAULT 'TBD',
  `nrtl_agency` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`component_type_status_id`),
  KEY `idx_component_type_id_cts` (`component_type_id`),
  CONSTRAINT `component_type_status_ibfk_1` FOREIGN KEY (`component_type_id`) REFERENCES `component_type` (`component_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1103 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_type_status`
--

LOCK TABLES `component_type_status` WRITE;
/*!40000 ALTER TABLE `component_type_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `component_type_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conductor`
--

DROP TABLE IF EXISTS `conductor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conductor` (
  `conductor_id` int(11) NOT NULL AUTO_INCREMENT,
  `cable_id` int(11) DEFAULT NULL,
  `port_pin_a_id` int(11) DEFAULT NULL,
  `port_pin_b_id` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`conductor_id`),
  KEY `idx_cable_id` (`cable_id`),
  KEY `idx_port_pin_a_id` (`port_pin_a_id`),
  KEY `idx_port_pin_b_id` (`port_pin_b_id`),
  CONSTRAINT `conductor_ibfk_1` FOREIGN KEY (`cable_id`) REFERENCES `cable` (`cable_id`),
  CONSTRAINT `conductor_ibfk_2` FOREIGN KEY (`port_pin_a_id`) REFERENCES `port_pin` (`port_pin_id`),
  CONSTRAINT `conductor_ibfk_3` FOREIGN KEY (`port_pin_b_id`) REFERENCES `port_pin` (`port_pin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conductor`
--

LOCK TABLES `conductor` WRITE;
/*!40000 ALTER TABLE `conductor` DISABLE KEYS */;
/*!40000 ALTER TABLE `conductor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `criticality_type`
--

DROP TABLE IF EXISTS `criticality_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `criticality_type` (
  `criticality_id` int(11) NOT NULL AUTO_INCREMENT,
  `criticality_level` int(11) DEFAULT NULL,
  `criticality_classification` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`criticality_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `criticality_type`
--

LOCK TABLES `criticality_type` WRITE;
/*!40000 ALTER TABLE `criticality_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `criticality_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doc_type`
--

DROP TABLE IF EXISTS `doc_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doc_type` (
  `doc_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_type` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`doc_type_id`),
  UNIQUE KEY `doc_type` (`doc_type`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doc_type`
--

LOCK TABLES `doc_type` WRITE;
/*!40000 ALTER TABLE `doc_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `doc_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fld`
--

DROP TABLE IF EXISTS `fld`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fld` (
  `fld_id` int(11) NOT NULL AUTO_INCREMENT,
  `rec_id` int(11) DEFAULT NULL,
  `fld_type_id` int(11) DEFAULT NULL,
  `fld_val` varchar(128) DEFAULT NULL,
  `ioc_resource_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`fld_id`),
  KEY `idx_rec_id` (`rec_id`),
  KEY `idx_fld_type_id` (`fld_type_id`),
  KEY `idx_fld_val` (`fld_val`),
  KEY `idx_ioc_resource_id` (`ioc_resource_id`),
  CONSTRAINT `fld_ibfk_1` FOREIGN KEY (`rec_id`) REFERENCES `rec` (`rec_id`),
  CONSTRAINT `fld_ibfk_2` FOREIGN KEY (`fld_type_id`) REFERENCES `fld_type` (`fld_type_id`),
  CONSTRAINT `fld_ibfk_3` FOREIGN KEY (`ioc_resource_id`) REFERENCES `ioc_resource` (`ioc_resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=893613674 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fld`
--

LOCK TABLES `fld` WRITE;
/*!40000 ALTER TABLE `fld` DISABLE KEYS */;
/*!40000 ALTER TABLE `fld` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fld_history`
--

DROP TABLE IF EXISTS `fld_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fld_history` (
  `fld_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `rec_history_id` int(11) DEFAULT NULL,
  `fld_type_history_id` int(11) DEFAULT NULL,
  `fld_val` varchar(128) DEFAULT NULL,
  `ioc_resource_history_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`fld_history_id`),
  KEY `idx_rec_history_id` (`rec_history_id`),
  KEY `idx_fld_type_history_id` (`fld_type_history_id`),
  KEY `idx_fld_val` (`fld_val`),
  KEY `idx_ioc_resource_history_id` (`ioc_resource_history_id`),
  CONSTRAINT `fld_history_ibfk_1` FOREIGN KEY (`rec_history_id`) REFERENCES `rec_history` (`rec_history_id`),
  CONSTRAINT `fld_history_ibfk_2` FOREIGN KEY (`fld_type_history_id`) REFERENCES `fld_type_history` (`fld_type_history_id`),
  CONSTRAINT `fld_history_ibfk_3` FOREIGN KEY (`ioc_resource_history_id`) REFERENCES `ioc_resource_history` (`ioc_resource_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=882470225 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fld_history`
--

LOCK TABLES `fld_history` WRITE;
/*!40000 ALTER TABLE `fld_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `fld_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fld_type`
--

DROP TABLE IF EXISTS `fld_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fld_type` (
  `fld_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `rec_type_id` int(11) DEFAULT NULL,
  `fld_type` varchar(24) DEFAULT NULL,
  `dbd_type` varchar(24) DEFAULT NULL,
  `def_fld_val` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`fld_type_id`),
  KEY `idx_rec_type_id` (`rec_type_id`),
  KEY `idx_dbd_type` (`dbd_type`),
  CONSTRAINT `fld_type_ibfk_1` FOREIGN KEY (`rec_type_id`) REFERENCES `rec_type` (`rec_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=72701264 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fld_type`
--

LOCK TABLES `fld_type` WRITE;
/*!40000 ALTER TABLE `fld_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `fld_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fld_type_history`
--

DROP TABLE IF EXISTS `fld_type_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fld_type_history` (
  `fld_type_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `rec_type_history_id` int(11) DEFAULT NULL,
  `fld_type` varchar(24) DEFAULT NULL,
  `dbd_type` varchar(24) DEFAULT NULL,
  `def_fld_val` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`fld_type_history_id`),
  KEY `idx_rec_type_history_id` (`rec_type_history_id`),
  KEY `idx_dbd_history_type` (`dbd_type`),
  CONSTRAINT `fld_type_history_ibfk_1` FOREIGN KEY (`rec_type_history_id`) REFERENCES `rec_type_history` (`rec_type_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=71245684 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fld_type_history`
--

LOCK TABLES `fld_type_history` WRITE;
/*!40000 ALTER TABLE `fld_type_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `fld_type_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `form_factor`
--

DROP TABLE IF EXISTS `form_factor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_factor` (
  `form_factor_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_factor` varchar(100) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`form_factor_id`),
  UNIQUE KEY `form_factor` (`form_factor`,`mark_for_delete`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_factor`
--

LOCK TABLES `form_factor` WRITE;
/*!40000 ALTER TABLE `form_factor` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_factor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `function`
--

DROP TABLE IF EXISTS `function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `function` (
  `function_id` int(11) NOT NULL AUTO_INCREMENT,
  `function` varchar(100) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`function_id`),
  UNIQUE KEY `function` (`function`,`mark_for_delete`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `function`
--

LOCK TABLES `function` WRITE;
/*!40000 ALTER TABLE `function` DISABLE KEYS */;
/*!40000 ALTER TABLE `function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_name`
--

DROP TABLE IF EXISTS `group_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_name` (
  `group_name_id` int(11) NOT NULL DEFAULT '0',
  `group_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`group_name_id`),
  UNIQUE KEY `group_name` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_name`
--

LOCK TABLES `group_name` WRITE;
/*!40000 ALTER TABLE `group_name` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ioc`
--

DROP TABLE IF EXISTS `ioc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ioc` (
  `ioc_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_nm` varchar(40) DEFAULT NULL,
  `system` varchar(20) DEFAULT NULL,
  `active` tinyint(1) DEFAULT NULL,
  `modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(10) DEFAULT NULL,
  `component_id` int(11) DEFAULT NULL,
  `ioc_status_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ioc_id`),
  UNIQUE KEY `ioc_nm` (`ioc_nm`),
  KEY `fk_ioc_status_id` (`ioc_status_id`),
  CONSTRAINT `ioc_ibfk_1` FOREIGN KEY (`ioc_status_id`) REFERENCES `ioc_status` (`ioc_status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=494 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ioc`
--

LOCK TABLES `ioc` WRITE;
/*!40000 ALTER TABLE `ioc` DISABLE KEYS */;
/*!40000 ALTER TABLE `ioc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ioc_boot`
--

DROP TABLE IF EXISTS `ioc_boot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ioc_boot` (
  `ioc_boot_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_id` int(11) DEFAULT NULL,
  `sys_boot_line` varchar(127) DEFAULT NULL,
  `ioc_boot_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `current_load` tinyint(1) DEFAULT NULL,
  `current_boot` tinyint(1) DEFAULT NULL,
  `modified_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modified_by` varchar(10) DEFAULT NULL,
  `boot_device` varchar(127) DEFAULT NULL,
  `boot_params_version` float DEFAULT NULL,
  `console_connection` varchar(127) DEFAULT NULL,
  `host_inet_address` varchar(127) DEFAULT NULL,
  `host_name` varchar(127) DEFAULT NULL,
  `ioc_inet_address` varchar(127) DEFAULT NULL,
  `ioc_pid` int(11) DEFAULT NULL,
  `launch_script` varchar(127) DEFAULT NULL,
  `launch_script_pid` int(11) DEFAULT NULL,
  `os_file_name` varchar(127) DEFAULT NULL,
  `processor_number` int(11) DEFAULT NULL,
  `target_architecture` varchar(127) DEFAULT NULL,
  PRIMARY KEY (`ioc_boot_id`),
  KEY `idx_ioc_id` (`ioc_id`),
  KEY `idx_current_load` (`current_load`)
) ENGINE=InnoDB AUTO_INCREMENT=110274 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ioc_boot`
--

LOCK TABLES `ioc_boot` WRITE;
/*!40000 ALTER TABLE `ioc_boot` DISABLE KEYS */;
/*!40000 ALTER TABLE `ioc_boot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ioc_error`
--

DROP TABLE IF EXISTS `ioc_error`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ioc_error` (
  `ioc_error_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_boot_id` int(11) DEFAULT NULL,
  `ioc_error_num` int(11) DEFAULT NULL,
  PRIMARY KEY (`ioc_error_id`),
  KEY `idx_ioc_boot_id` (`ioc_boot_id`),
  CONSTRAINT `ioc_error_ibfk_1` FOREIGN KEY (`ioc_boot_id`) REFERENCES `ioc_boot` (`ioc_boot_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10083 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ioc_error`
--

LOCK TABLES `ioc_error` WRITE;
/*!40000 ALTER TABLE `ioc_error` DISABLE KEYS */;
/*!40000 ALTER TABLE `ioc_error` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ioc_error_message`
--

DROP TABLE IF EXISTS `ioc_error_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ioc_error_message` (
  `ioc_error_num` int(11) NOT NULL DEFAULT '0',
  `ioc_error_message` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`ioc_error_num`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ioc_error_message`
--

LOCK TABLES `ioc_error_message` WRITE;
/*!40000 ALTER TABLE `ioc_error_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `ioc_error_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ioc_resource`
--

DROP TABLE IF EXISTS `ioc_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ioc_resource` (
  `ioc_resource_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_boot_id` int(11) DEFAULT NULL,
  `text_line` varchar(255) DEFAULT NULL,
  `load_order` int(11) DEFAULT NULL,
  `uri_id` int(11) DEFAULT NULL,
  `unreachable` tinyint(1) DEFAULT NULL,
  `subst_str` varchar(255) DEFAULT NULL,
  `ioc_resource_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ioc_resource_id`),
  KEY `idx_ioc_boot_id` (`ioc_boot_id`),
  KEY `idx_uri_id` (`uri_id`),
  CONSTRAINT `ioc_resource_ibfk_1` FOREIGN KEY (`ioc_boot_id`) REFERENCES `ioc_boot` (`ioc_boot_id`),
  CONSTRAINT `ioc_resource_ibfk_2` FOREIGN KEY (`uri_id`) REFERENCES `uri` (`uri_id`)
) ENGINE=InnoDB AUTO_INCREMENT=766453 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ioc_resource`
--

LOCK TABLES `ioc_resource` WRITE;
/*!40000 ALTER TABLE `ioc_resource` DISABLE KEYS */;
/*!40000 ALTER TABLE `ioc_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ioc_resource_history`
--

DROP TABLE IF EXISTS `ioc_resource_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ioc_resource_history` (
  `ioc_resource_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_boot_id` int(11) DEFAULT NULL,
  `text_line` varchar(255) DEFAULT NULL,
  `load_order` int(11) DEFAULT NULL,
  `uri_history_id` int(11) DEFAULT NULL,
  `unreachable` tinyint(1) DEFAULT NULL,
  `subst_str` varchar(255) DEFAULT NULL,
  `ioc_resource_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ioc_resource_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=748696 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ioc_resource_history`
--

LOCK TABLES `ioc_resource_history` WRITE;
/*!40000 ALTER TABLE `ioc_resource_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `ioc_resource_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ioc_resource_type`
--

DROP TABLE IF EXISTS `ioc_resource_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ioc_resource_type` (
  `ioc_resource_type_id` int(11) NOT NULL DEFAULT '0',
  `ioc_resource_type` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`ioc_resource_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ioc_resource_type`
--

LOCK TABLES `ioc_resource_type` WRITE;
/*!40000 ALTER TABLE `ioc_resource_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `ioc_resource_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ioc_status`
--

DROP TABLE IF EXISTS `ioc_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ioc_status` (
  `ioc_status_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_status` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`ioc_status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ioc_status`
--

LOCK TABLES `ioc_status` WRITE;
/*!40000 ALTER TABLE `ioc_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `ioc_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ioc_stcmd_line`
--

DROP TABLE IF EXISTS `ioc_stcmd_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ioc_stcmd_line` (
  `ioc_stcmd_line_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_stcmd_line` varchar(255) DEFAULT NULL,
  `table_modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `table_modified_by` varchar(20) DEFAULT NULL,
  `ioc_id` int(11) DEFAULT NULL,
  `ioc_stcmd_line_number` int(11) DEFAULT NULL,
  `include_line_number` int(11) DEFAULT NULL,
  PRIMARY KEY (`ioc_stcmd_line_id`),
  KEY `fk_ioc_id` (`ioc_id`),
  CONSTRAINT `ioc_stcmd_line_ibfk_1` FOREIGN KEY (`ioc_id`) REFERENCES `ioc` (`ioc_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2403679 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ioc_stcmd_line`
--

LOCK TABLES `ioc_stcmd_line` WRITE;
/*!40000 ALTER TABLE `ioc_stcmd_line` DISABLE KEYS */;
/*!40000 ALTER TABLE `ioc_stcmd_line` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `machine`
--

DROP TABLE IF EXISTS `machine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `machine` (
  `machine_id` int(11) NOT NULL AUTO_INCREMENT,
  `machine` varchar(60) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`machine_id`),
  UNIQUE KEY `machine` (`machine`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `machine`
--

LOCK TABLES `machine` WRITE;
/*!40000 ALTER TABLE `machine` DISABLE KEYS */;
/*!40000 ALTER TABLE `machine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mfg`
--

DROP TABLE IF EXISTS `mfg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mfg` (
  `mfg_id` int(11) NOT NULL AUTO_INCREMENT,
  `mfg_name` varchar(100) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`mfg_id`),
  UNIQUE KEY `mfg_name` (`mfg_name`,`mark_for_delete`)
) ENGINE=InnoDB AUTO_INCREMENT=192 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mfg`
--

LOCK TABLES `mfg` WRITE;
/*!40000 ALTER TABLE `mfg` DISABLE KEYS */;
/*!40000 ALTER TABLE `mfg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person` (
  `person_id` int(11) NOT NULL AUTO_INCREMENT,
  `first_nm` varchar(30) DEFAULT NULL,
  `middle_nm` varchar(30) DEFAULT NULL,
  `last_nm` varchar(30) DEFAULT NULL,
  `userid` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_group`
--

DROP TABLE IF EXISTS `person_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_group` (
  `person_group_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) DEFAULT NULL,
  `group_name_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`person_group_id`),
  KEY `idx_person_id_p` (`person_id`),
  KEY `idx_group_name_id_pg` (`group_name_id`),
  CONSTRAINT `person_group_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `person_group_ibfk_2` FOREIGN KEY (`group_name_id`) REFERENCES `group_name` (`group_name_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_group`
--

LOCK TABLES `person_group` WRITE;
/*!40000 ALTER TABLE `person_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `person_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plc`
--

DROP TABLE IF EXISTS `plc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `plc` (
  `plc_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_id` int(11) DEFAULT NULL,
  `plc_description` text,
  `plc_version_pv_name` text,
  PRIMARY KEY (`plc_id`)
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plc`
--

LOCK TABLES `plc` WRITE;
/*!40000 ALTER TABLE `plc` DISABLE KEYS */;
/*!40000 ALTER TABLE `plc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plc_old`
--

DROP TABLE IF EXISTS `plc_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `plc_old` (
  `plc_id` int(11) NOT NULL AUTO_INCREMENT,
  `plc_name` varchar(60) DEFAULT NULL,
  `plc_description` text,
  PRIMARY KEY (`plc_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plc_old`
--

LOCK TABLES `plc_old` WRITE;
/*!40000 ALTER TABLE `plc_old` DISABLE KEYS */;
/*!40000 ALTER TABLE `plc_old` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `port_pin`
--

DROP TABLE IF EXISTS `port_pin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `port_pin` (
  `port_pin_id` int(11) NOT NULL AUTO_INCREMENT,
  `port_pin_type_id` int(11) DEFAULT NULL,
  `port_pin_usage` varchar(60) DEFAULT NULL,
  `signal_name` varchar(60) DEFAULT NULL,
  `component_port_id` int(11) DEFAULT NULL,
  `port_pin_designator_id` int(11) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`port_pin_id`),
  KEY `idx_port_pin_type_id_pp` (`port_pin_type_id`),
  KEY `idx_component_port_id_pp` (`component_port_id`),
  KEY `idx_port_pin_designator_id_pp` (`port_pin_designator_id`),
  CONSTRAINT `port_pin_ibfk_1` FOREIGN KEY (`port_pin_type_id`) REFERENCES `port_pin_type` (`port_pin_type_id`),
  CONSTRAINT `port_pin_ibfk_2` FOREIGN KEY (`component_port_id`) REFERENCES `component_port` (`component_port_id`),
  CONSTRAINT `port_pin_ibfk_3` FOREIGN KEY (`port_pin_designator_id`) REFERENCES `port_pin_designator` (`port_pin_designator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=999310 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `port_pin`
--

LOCK TABLES `port_pin` WRITE;
/*!40000 ALTER TABLE `port_pin` DISABLE KEYS */;
/*!40000 ALTER TABLE `port_pin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `port_pin_designator`
--

DROP TABLE IF EXISTS `port_pin_designator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `port_pin_designator` (
  `port_pin_designator_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_port_type_id` int(11) DEFAULT NULL,
  `designator_order` int(11) DEFAULT NULL,
  `designator` varchar(60) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`port_pin_designator_id`),
  KEY `idx_component_port_type_id_ppd` (`component_port_type_id`),
  CONSTRAINT `port_pin_designator_ibfk_1` FOREIGN KEY (`component_port_type_id`) REFERENCES `component_port_type` (`component_port_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6435 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `port_pin_designator`
--

LOCK TABLES `port_pin_designator` WRITE;
/*!40000 ALTER TABLE `port_pin_designator` DISABLE KEYS */;
/*!40000 ALTER TABLE `port_pin_designator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `port_pin_template`
--

DROP TABLE IF EXISTS `port_pin_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `port_pin_template` (
  `port_pin_template_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_port_template_id` int(11) DEFAULT NULL,
  `port_pin_type_id` int(11) DEFAULT NULL,
  `port_pin_designator_id` int(11) DEFAULT NULL,
  `port_pin_usage` varchar(60) DEFAULT NULL,
  `mark_for_delete` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`port_pin_template_id`),
  KEY `idx_component_port_template_id` (`component_port_template_id`),
  KEY `idx_port_pin_type_id_ppt` (`port_pin_type_id`),
  KEY `idx_port_pin_designator_id_ppt` (`port_pin_designator_id`),
  CONSTRAINT `port_pin_template_ibfk_1` FOREIGN KEY (`component_port_template_id`) REFERENCES `component_port_template` (`component_port_template_id`),
  CONSTRAINT `port_pin_template_ibfk_2` FOREIGN KEY (`port_pin_type_id`) REFERENCES `port_pin_type` (`port_pin_type_id`),
  CONSTRAINT `port_pin_template_ibfk_3` FOREIGN KEY (`port_pin_designator_id`) REFERENCES `port_pin_designator` (`port_pin_designator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50802 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `port_pin_template`
--

LOCK TABLES `port_pin_template` WRITE;
/*!40000 ALTER TABLE `port_pin_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `port_pin_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `port_pin_type`
--

DROP TABLE IF EXISTS `port_pin_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `port_pin_type` (
  `port_pin_type_id` int(11) NOT NULL DEFAULT '0',
  `port_pin_type` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`port_pin_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `port_pin_type`
--

LOCK TABLES `port_pin_type` WRITE;
/*!40000 ALTER TABLE `port_pin_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `port_pin_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec`
--

DROP TABLE IF EXISTS `rec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec` (
  `rec_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_boot_id` int(11) DEFAULT NULL,
  `rec_nm` varchar(128) DEFAULT NULL,
  `rec_type_id` int(11) DEFAULT NULL,
  `rec_criticality` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`rec_id`),
  KEY `idx_rec_nm` (`rec_nm`),
  KEY `idx_ioc_boot_id` (`ioc_boot_id`),
  KEY `idx_rec_type_id` (`rec_type_id`),
  CONSTRAINT `rec_ibfk_1` FOREIGN KEY (`ioc_boot_id`) REFERENCES `ioc_boot` (`ioc_boot_id`),
  CONSTRAINT `rec_ibfk_2` FOREIGN KEY (`rec_type_id`) REFERENCES `rec_type` (`rec_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55855953 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec`
--

LOCK TABLES `rec` WRITE;
/*!40000 ALTER TABLE `rec` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec_alias`
--

DROP TABLE IF EXISTS `rec_alias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec_alias` (
  `rec_alias_id` int(11) NOT NULL AUTO_INCREMENT,
  `rec_id` int(11) DEFAULT NULL,
  `alias_nm` varchar(128) DEFAULT NULL,
  `ioc_resource_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`rec_alias_id`),
  KEY `fk_rec_alias_id` (`rec_id`),
  CONSTRAINT `rec_alias_ibfk_1` FOREIGN KEY (`rec_id`) REFERENCES `rec` (`rec_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14520 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec_alias`
--

LOCK TABLES `rec_alias` WRITE;
/*!40000 ALTER TABLE `rec_alias` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec_alias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec_alias_history`
--

DROP TABLE IF EXISTS `rec_alias_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec_alias_history` (
  `rec_alias_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `rec_history_id` int(11) DEFAULT NULL,
  `alias_nm` varchar(128) DEFAULT NULL,
  `ioc_resource_history_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`rec_alias_history_id`),
  KEY `fk_rec_alias_history_id` (`rec_history_id`),
  CONSTRAINT `rec_alias_history_ibfk_1` FOREIGN KEY (`rec_history_id`) REFERENCES `rec_history` (`rec_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13848 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec_alias_history`
--

LOCK TABLES `rec_alias_history` WRITE;
/*!40000 ALTER TABLE `rec_alias_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec_alias_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec_client`
--

DROP TABLE IF EXISTS `rec_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec_client` (
  `rec_client_id` int(11) NOT NULL AUTO_INCREMENT,
  `rec_client_type_id` int(11) DEFAULT NULL,
  `rec_nm` varchar(128) DEFAULT NULL,
  `fld_type` varchar(24) DEFAULT NULL,
  `vuri_id` int(11) DEFAULT NULL,
  `current_load` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`rec_client_id`),
  KEY `idx_rec_nm` (`rec_nm`),
  KEY `idx_vuri_id` (`vuri_id`)
) ENGINE=InnoDB AUTO_INCREMENT=328053417 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec_client`
--

LOCK TABLES `rec_client` WRITE;
/*!40000 ALTER TABLE `rec_client` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec_client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec_client_type`
--

DROP TABLE IF EXISTS `rec_client_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec_client_type` (
  `rec_client_type_id` int(11) NOT NULL DEFAULT '0',
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`rec_client_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec_client_type`
--

LOCK TABLES `rec_client_type` WRITE;
/*!40000 ALTER TABLE `rec_client_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec_client_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec_history`
--

DROP TABLE IF EXISTS `rec_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec_history` (
  `rec_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_boot_id` int(11) DEFAULT NULL,
  `rec_nm` varchar(128) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `rec_type_history_id` int(11) DEFAULT NULL,
  `rec_criticality` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`rec_history_id`),
  KEY `idx_rec_history_nm` (`rec_nm`),
  KEY `idx_ioc_boot_history_id` (`ioc_boot_id`),
  KEY `idx_rec_type_history_id` (`rec_type_history_id`),
  CONSTRAINT `rec_history_ibfk_1` FOREIGN KEY (`ioc_boot_id`) REFERENCES `ioc_boot` (`ioc_boot_id`),
  CONSTRAINT `rec_history_ibfk_2` FOREIGN KEY (`rec_type_history_id`) REFERENCES `rec_type_history` (`rec_type_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55093728 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec_history`
--

LOCK TABLES `rec_history` WRITE;
/*!40000 ALTER TABLE `rec_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec_type`
--

DROP TABLE IF EXISTS `rec_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec_type` (
  `rec_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_boot_id` int(11) DEFAULT NULL,
  `rec_type` varchar(24) DEFAULT NULL,
  `ioc_resource_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`rec_type_id`),
  KEY `idx_ioc_boot_id` (`ioc_boot_id`),
  KEY `idx_ioc_resource_id` (`ioc_resource_id`),
  CONSTRAINT `rec_type_ibfk_1` FOREIGN KEY (`ioc_boot_id`) REFERENCES `ioc_boot` (`ioc_boot_id`),
  CONSTRAINT `rec_type_ibfk_2` FOREIGN KEY (`ioc_resource_id`) REFERENCES `ioc_resource` (`ioc_resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=978776 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec_type`
--

LOCK TABLES `rec_type` WRITE;
/*!40000 ALTER TABLE `rec_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec_type_dev_sup`
--

DROP TABLE IF EXISTS `rec_type_dev_sup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec_type_dev_sup` (
  `rec_type_dev_sup_id` int(11) NOT NULL AUTO_INCREMENT,
  `rec_type_id` int(11) DEFAULT NULL,
  `dtyp_str` varchar(50) DEFAULT NULL,
  `dev_sup_dset` varchar(50) DEFAULT NULL,
  `dev_sup_io_type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`rec_type_dev_sup_id`),
  KEY `fk_rec_type_id_rtds` (`rec_type_id`),
  CONSTRAINT `rec_type_dev_sup_ibfk_1` FOREIGN KEY (`rec_type_id`) REFERENCES `rec_type` (`rec_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3316051 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec_type_dev_sup`
--

LOCK TABLES `rec_type_dev_sup` WRITE;
/*!40000 ALTER TABLE `rec_type_dev_sup` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec_type_dev_sup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec_type_dev_sup_history`
--

DROP TABLE IF EXISTS `rec_type_dev_sup_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec_type_dev_sup_history` (
  `rec_type_dev_sup_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `rec_type_history_id` int(11) DEFAULT NULL,
  `dtyp_str` varchar(50) DEFAULT NULL,
  `dev_sup_dset` varchar(50) DEFAULT NULL,
  `dev_sup_io_type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`rec_type_dev_sup_history_id`),
  KEY `idx_rec_type_history_id_rtds` (`rec_type_history_id`),
  CONSTRAINT `rec_type_dev_sup_history_ibfk_1` FOREIGN KEY (`rec_type_history_id`) REFERENCES `rec_type_history` (`rec_type_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3260798 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec_type_dev_sup_history`
--

LOCK TABLES `rec_type_dev_sup_history` WRITE;
/*!40000 ALTER TABLE `rec_type_dev_sup_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec_type_dev_sup_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rec_type_history`
--

DROP TABLE IF EXISTS `rec_type_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rec_type_history` (
  `rec_type_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `ioc_boot_id` int(11) DEFAULT NULL,
  `rec_type` varchar(24) DEFAULT NULL,
  `ioc_resource_history_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`rec_type_history_id`),
  KEY `idx_ioc_boot_id` (`ioc_boot_id`),
  KEY `idx_ioc_resource_history_id` (`ioc_resource_history_id`),
  CONSTRAINT `rec_type_history_ibfk_1` FOREIGN KEY (`ioc_boot_id`) REFERENCES `ioc_boot` (`ioc_boot_id`),
  CONSTRAINT `rec_type_history_ibfk_2` FOREIGN KEY (`ioc_resource_history_id`) REFERENCES `ioc_resource_history` (`ioc_resource_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=958993 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rec_type_history`
--

LOCK TABLES `rec_type_history` WRITE;
/*!40000 ALTER TABLE `rec_type_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `rec_type_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) DEFAULT NULL,
  `role_name_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_name`
--

DROP TABLE IF EXISTS `role_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_name` (
  `role_name_id` int(11) NOT NULL DEFAULT '0',
  `role_name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`role_name_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_name`
--

LOCK TABLES `role_name` WRITE;
/*!40000 ALTER TABLE `role_name` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `server`
--

DROP TABLE IF EXISTS `server`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server` (
  `server_id` int(11) NOT NULL AUTO_INCREMENT,
  `component_id` int(11) DEFAULT NULL,
  `server_description` mediumtext,
  `cognizant_id` int(11) DEFAULT NULL,
  `operating_system` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`server_id`),
  KEY `idx_server_id_component` (`component_id`),
  KEY `server_ibfk_2` (`cognizant_id`),
  CONSTRAINT `server_ibfk_1` FOREIGN KEY (`component_id`) REFERENCES `component` (`component_id`),
  CONSTRAINT `server_ibfk_2` FOREIGN KEY (`cognizant_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `server`
--

LOCK TABLES `server` WRITE;
/*!40000 ALTER TABLE `server` DISABLE KEYS */;
/*!40000 ALTER TABLE `server` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `technical_system`
--

DROP TABLE IF EXISTS `technical_system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `technical_system` (
  `technical_system_id` int(11) NOT NULL AUTO_INCREMENT,
  `technical_system` varchar(60) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`technical_system_id`),
  UNIQUE KEY `technical_system` (`technical_system`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `technical_system`
--

LOCK TABLES `technical_system` WRITE;
/*!40000 ALTER TABLE `technical_system` DISABLE KEYS */;
/*!40000 ALTER TABLE `technical_system` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uri`
--

DROP TABLE IF EXISTS `uri`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uri` (
  `uri_id` int(11) NOT NULL AUTO_INCREMENT,
  `uri` varchar(255) DEFAULT NULL,
  `uri_modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modified_by` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`uri_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4877610 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uri`
--

LOCK TABLES `uri` WRITE;
/*!40000 ALTER TABLE `uri` DISABLE KEYS */;
/*!40000 ALTER TABLE `uri` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uri_history`
--

DROP TABLE IF EXISTS `uri_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uri_history` (
  `uri_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `uri` varchar(255) DEFAULT NULL,
  `uri_modified_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modified_by` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`uri_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=748696 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uri_history`
--

LOCK TABLES `uri_history` WRITE;
/*!40000 ALTER TABLE `uri_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `uri_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vuri`
--

DROP TABLE IF EXISTS `vuri`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vuri` (
  `vuri_id` int(11) NOT NULL AUTO_INCREMENT,
  `uri_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`vuri_id`),
  KEY `idx_uri_id` (`uri_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6590146 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vuri`
--

LOCK TABLES `vuri` WRITE;
/*!40000 ALTER TABLE `vuri` DISABLE KEYS */;
/*!40000 ALTER TABLE `vuri` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vuri_rel`
--

DROP TABLE IF EXISTS `vuri_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vuri_rel` (
  `vuri_rel_id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_vuri_id` int(11) DEFAULT NULL,
  `child_vuri_id` int(11) DEFAULT NULL,
  `rel_info` text,
  PRIMARY KEY (`vuri_rel_id`),
  KEY `idx_parent_vuri_id` (`parent_vuri_id`),
  KEY `idx_child_vuri_id` (`child_vuri_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6566706 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vuri_rel`
--

LOCK TABLES `vuri_rel` WRITE;
/*!40000 ALTER TABLE `vuri_rel` DISABLE KEYS */;
/*!40000 ALTER TABLE `vuri_rel` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-02-10 13:01:03
