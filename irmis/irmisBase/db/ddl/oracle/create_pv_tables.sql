-- Schema for holding inventory of pv's in ioc's, as well as a
-- record of ioc boots, and the file system resources (dbd, db files).

-- ioc table stores master list of facility ioc's. must be manually
-- pre-populated for pv crawler to work.

-- Columns:
--   ioc_nm - ioc name
--   system - arbitrary string identifying ioc group membership (optional)
--   active - flag (0 or 1) indicating whether ioc considered active (used)
--   modified_date - time of row insert
--   modified_by - username or app name who performed insert

create table ioc
  (
    ioc_id int not null primary key,
    ioc_nm varchar(40),
    system varchar(20),
    active smallint,
    component_id int,
    modified_date timestamp,
    modified_by varchar(60),
    unique(ioc_nm)
  );

drop sequence seq_ioc;
create sequence seq_ioc;

-- ioc_boot table stores time-based record of ioc boot ocurrences

-- Columns:
--   ioc_id - fk to ioc table
--   sys_boot_line - path of st.cmd file for this ioc
--   ioc_boot_date - time of ioc boot (as best we can determine)
--   current_load - flag (0 or 1) 1 if this ioc_boot record is the most recent
--                  ioc_boot record for the given ioc that has a set of records
--                  associated with it.
--   current_boot - flag (0 or 1) 1 if this ioc_boot record is just a record of
--                  a reboot, but does not have a set of records associated with it.
--   modified_date - time of row insert
--   modified_by - username or app name who performed insert

create table ioc_boot
  (
    ioc_boot_id int not null primary key,
    ioc_id int,
    sys_boot_line varchar(127),
    ioc_boot_date timestamp,
    current_load smallint,
    current_boot smallint,
    modified_date timestamp,
    modified_by varchar(60)
  );     

drop sequence seq_ioc_boot;
create sequence seq_ioc_boot;

create table ioc_error
  (
    ioc_error_id int not null primary key,
    ioc_boot_id int,
    ioc_error_num int
  );

drop sequence seq_ioc_error;
create sequence seq_ioc_error;

create table ioc_resource
  (
    ioc_resource_id int not null primary key,
    ioc_boot_id int,
    text_line varchar(255),
    load_order int,
    uri_id int,
    unreachable smallint,
    subst_str varchar(255),
    ioc_resource_type_id int
  );      

drop sequence seq_ioc_resource;
create sequence seq_ioc_resource;

create table rec
  (
    rec_id int not null primary key,
    ioc_boot_id int,
    rec_nm varchar(128),
    rec_type_id int
  );      

drop sequence seq_rec;
create sequence seq_rec;

create table fld
  (
    fld_id int not null primary key,
    rec_id int,
    fld_type_id int,
    fld_val varchar(128),
    ioc_resource_id int 
  );      

drop sequence seq_fld;
create sequence seq_fld;

create table rec_type
  (
    rec_type_id int not null primary key,
    ioc_boot_id int,
    rec_type varchar(24),
    ioc_resource_id int
  );      

drop sequence seq_rec_type;
create sequence seq_rec_type;

create table fld_type
  (
    fld_type_id int not null primary key,
    rec_type_id int,
    fld_type varchar(24),
    dbd_type varchar(24),
    def_fld_val varchar(128)
  );      

drop sequence seq_fld_type;
create sequence seq_fld_type;

create table rec_type_dev_sup
  (
    rec_type_dev_sup_id int not null primary key,
    rec_type_id int,
    dtyp_str varchar(50),
    dev_sup_dset varchar(50),
    dev_sup_io_type varchar(50)
  );

drop sequence seq_rec_type_dev_sup;
create sequence seq_rec_type_dev_sup;

