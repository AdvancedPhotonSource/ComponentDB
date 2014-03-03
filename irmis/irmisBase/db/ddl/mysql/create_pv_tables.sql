# Schema for holding inventory of pv's in ioc's, as well as a
# record of ioc boots, and the file system resources (dbd, db files).

# ioc table stores master list of facility ioc's. must be manually
# pre-populated for pv crawler to work.

# Columns:
#   ioc_nm - ioc name
#   system - arbitrary string identifying ioc group membership (optional)
#   active - flag (0 or 1) indicating whether ioc considered active (used)
#   modified_date - time of row insert
#   modified_by - username or app name who performed insert

create table ioc
  (
    ioc_id int not null primary key auto_increment,
    ioc_nm varchar(40),
    system varchar(20),
    active tinyint(1),
    component_id int,
    modified_date timestamp,
    modified_by varchar(60),
    unique(ioc_nm)
  ) type=InnoDb;

# ioc_boot table stores time-based record of ioc boot ocurrences

# Columns:
#   ioc_id - fk to ioc table
#   sys_boot_line - path of st.cmd file for this ioc
#   ioc_boot_date - time of ioc boot (as best we can determine)
#   current_load - flag (0 or 1) 1 if this ioc_boot record is the most recent
#                  ioc_boot record for the given ioc that has a set of records
#                  associated with it.
#   current_boot - flag (0 or 1) 1 if this ioc_boot record is just a record of
#                  a reboot, but does not have a set of records associated with it.
#   modified_date - time of row insert
#   modified_by - username or app name who performed insert

# Note: change tinyint(1) to boolean when we move to MySQL 4.1.x
create table ioc_boot
  (
    ioc_boot_id int not null primary key auto_increment,
    ioc_id int,
    sys_boot_line varchar(127),
    ioc_boot_date timestamp,
    current_load tinyint(1),
    current_boot tinyint(1),
    modified_date timestamp,
    modified_by varchar(60),
    boot_device varchar(127),
    boot_params_version float,
    console_connection varchar(127),
    host_inet_address varchar(127),
    host_name varchar(127),
    ioc_inet_address varchar(127),
    ioc_pid int,
    launch_script varchar(127),
    launch_script_pid int,
    os_file_name varchar(127),
    processor_number int,
    target_architecture varchar(127)
  ) type=InnoDb;     

create table ioc_error
  (
    ioc_error_id int not null primary key auto_increment,
    ioc_boot_id int,
    ioc_error_num int
  ) type=InnoDb;

create table ioc_resource
  (
    ioc_resource_id int not null primary key auto_increment,
    ioc_boot_id int,
    text_line varchar(255),
    load_order int,
    uri_id int,
    unreachable tinyint(1),
    subst_str varchar(255),
    ioc_resource_type_id int
  ) type=InnoDb;      

create table rec
  (
    rec_id int not null primary key auto_increment,
    ioc_boot_id int,
    rec_nm varchar(128) binary,
    rec_type_id int,
    rec_criticality int
  ) type=InnoDb;      

create table fld
  (
    fld_id int not null primary key auto_increment,
    rec_id int,
    fld_type_id int,
    fld_val varchar(128),
    ioc_resource_id int 
  ) type=InnoDb;      

create table rec_type
  (
    rec_type_id int not null primary key auto_increment,
    ioc_boot_id int,
    rec_type varchar(24),
    ioc_resource_id int
  ) type=InnoDb;      

create table fld_type
  (
    fld_type_id int not null primary key auto_increment,
    rec_type_id int,
    fld_type varchar(24),
    dbd_type varchar(24),
    def_fld_val varchar(128)
  ) type=InnoDb;      

create table rec_type_dev_sup
  (
    rec_type_dev_sup_id int not null primary key auto_increment,
    rec_type_id int,
    dtyp_str varchar(50),
    dev_sup_dset varchar(50),
    dev_sup_io_type varchar(50)
  ) type=InnoDb;


