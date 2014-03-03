# Delete all tables for AOI in order of foreign key dependencies.

drop table if exists aoi_techsys;
drop table if exists aoi_machine;
drop table if exists aoi_document;
drop table if exists aoi_plc_stcmd_line;
drop table if exists aoi_relation;
drop table if exists aoi_relation_type;
drop table if exists doc_type;
drop table if exists aoi_topdisplay;
drop table if exists aoi_criticality;
drop table if exists criticality_type;
drop table if exists aoi_epics_record;
drop table if exists aoi_ioc_stcmd_line;
drop table if exists ioc_stcmd_line;
drop table if exists aoi;
drop table if exists aoi_status;
drop table if exists aoi_crawler;