-- APS-specific table for holding extended ioc information.
-- This table is not required for a base configuration of 
-- IRMIS, although we supply this as an example. There is
-- a corresponding plug-in for the Java IRMIS desktop which
-- allows for displaying/editing this data. You may implement
-- your own table and plug-in as needed.
--

create table aps_ioc
  (
    aps_ioc_id int not null primary key,
    ioc_id int not null,
    location varchar(60),
    TermServRackNo varchar(60),
    TermServName varchar(60),
    TermServPort int,
    TermServFiberConvCh varchar(60),
    TermServFiberConvPort int,
    PrimEnetSwRackNo varchar(60),
    PrimEnetSwitch varchar(60),
    PrimEnetBlade varchar(60),
    PrimEnetPort int,
    PrimEnetMedConvCh varchar(60),
    PrimMediaConvPort int,
    SecEnetSwRackNo varchar(60),
    SecEnetSwitch varchar(60),
    SecEnetBlade varchar(60),
    SecEnetPort int,
    SecEnetMedConvCh varchar(60),
    SecMedConvPort int,
    cog_developer_id int,
    cog_technician_id int,
    general_functions varchar(255),
    pre_boot_instr varchar(255),
    post_boot_instr varchar(255),
    power_cycle_caution varchar(255),
    sysreset_reqd smallint,
    inhibit_auto_reboot smallint 
  );

drop sequence seq_aps_ioc;
create sequence seq_aps_ioc;


