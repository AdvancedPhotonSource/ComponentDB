# Schema for holding inventory of applications of interest (AOI).

# aoi table stores master list of facility aoi's. 

# Columns:
#   aoi_id 			- primary key
#   aoi_name		-
#   aoi_cognizant1_id	-
#   aoi_cognizant2_id	- 
#   aoi_customer_contact_id	-
#   aoi_status_id		-
#   aoi_description		-
#   aoi_worklog	-
#   aoi_keyword		-
#   aoi_customer_group_id	-

drop table if exists aoi_techsys;
create table aoi_techsys
  (
    aoi_techsystem_id int not null primary key auto_increment,
    technical_system_id int,
    aoi_id int
  ) type=InnoDb;
  
drop table if exists aoi_machine;
create table aoi_machine
  (
    aoi_machine_id int not null primary key auto_increment,
    machine_id int,
    aoi_id int
  ) type=InnoDb;
  
drop table if exists aoi_document;
create table aoi_document
  (
    doc_id int not null primary key auto_increment,
    aoi_id int,
    uri varchar(255),
    doc_type_id int
  ) type=InnoDb;
  
drop table if exists aoi_plc_stcmd_line;
create table aoi_plc_stcmd_line
  (
    aoi_plc_stcmd_line_id int not null primary key auto_increment,
    plc_id int,
    aoi_id int,
    ioc_stcmd_line_id int
  ) type=InnoDb;
    
drop table if exists aoi_relation;
create table aoi_relation
  (
    aoi_relation_id int not null primary key auto_increment,
    aoi1_id int,
    aoi2_id int,
    aoi1_relation_type_id int,
    aoi2_relation_type_id int
  ) type=InnoDb;  
  
drop table if exists aoi_relation_type;
create table aoi_relation_type
  (
    aoi_relation_type_id int not null primary key auto_increment,
    aoi_relation varchar(40),
    unique(aoi_relation)
  ) type=InnoDb;
  
drop table if exists doc_type;
create table doc_type
  (
    doc_type_id int not null primary key auto_increment,
    doc_type varchar(40),
    unique(doc_type)
  ) type=InnoDb;
  
drop table if exists aoi_topdisplay;
create table aoi_topdisplay
  (
    aoi_topdisplay_id int not null primary key auto_increment,
    aoi_id int,
    uri varchar(255)
  ) type=InnoDb;
  
drop table if exists aoi_criticality;
create table aoi_criticality
  (
    aoi_criticality_id int not null primary key auto_increment,
    aoi_id int,
    criticality_id int
  ) type=InnoDb;
  
drop table if exists criticality_type;
create table criticality_type
  (
    criticality_id int not null primary key auto_increment,
    criticality_level int,
    criticality_classification varchar(255)
  ) type=InnoDb;
  
drop table if exists aoi_epics_record;
create table aoi_epics_record
  (
    aoi_epics_record_id int not null primary key auto_increment,
    aoi_ioc_stcmd_line_id int,
    rec_nm varchar(128)
  ) type=InnoDb;  
  
drop table if exists aoi_ioc_stcmd_line;
create table aoi_ioc_stcmd_line
  (
    aoi_ioc_stcmd_line_id int not null primary key auto_increment,
    aoi_id int,
    ioc_stcmd_line_id int,
    pv_filter varchar(128)
  ) type=InnoDb;
  
drop table if exists ioc_stcmd_line;
create table ioc_stcmd_line
  (
    ioc_stcmd_line_id int not null primary key auto_increment,
    ioc_stcmd_line varchar(255),
    table_modified_date timestamp(14),
    table_modified_by varchar(20),
    ioc_id int,
    ioc_stcmd_line_number int,
    include_line_number int
  ) type=InnoDb;  
    
drop table if exists aoi;
create table aoi
  (
    aoi_id int not null primary key auto_increment,
    aoi_name varchar(60),
    aoi_cognizant1_id int,
    aoi_cognizant2_id int,
    aoi_customer_contact_id int,
    aoi_status_id int,
    aoi_description varchar(255),
    aoi_func_criteria varchar(255),
    aoi_keyword varchar(255),
    aoi_customer_group_id int,
    unique(aoi_name)
  ) type=InnoDb;

drop table if exists aoi_crawler;
create table aoi_crawler
  (
    aoi_crawler_id int not null primary key auto_increment,
    ioc_id int,
    ioc_boot_id int
  ) type=InnoDb;

drop table if exists aoi_status;
create table aoi_status
  (
    aoi_status_id int not null primary key auto_increment,
    aoi_status varchar(40)
  ) type=InnoDb;

