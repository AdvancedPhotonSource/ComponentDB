# Creates schema for PV clients. 

# rec_client contains list of record name/fld that are used by various clients

# Columns:
#   rec_client_type_id - fk to rec_client_type table identifying client type
#   rec_nm - record name (minus field)
#   fld_type - field name (ie. VAL or HOPR)
#   vuri_id - fk to vuri table giving path to file containing this record name
#   current_load - flag (0 or 1) indicating current data set

create table rec_client
  (
    rec_client_id int not null primary key auto_increment,
    rec_client_type_id int,
    rec_nm varchar(128),
    fld_type varchar(24),
    vuri_id int,
    current_load tinyint(1)
  ) type=InnoDb;

# VURI - Virtual URI
# vuri is a level of indirection before the uri table. This allows for
#   several unique vuri's that represent the same underlying concrete
#   file on the file system. This is useful in some cases when used
#   in conjunction with vuri relationships (vuri_rel)

create table vuri
   (
    vuri_id int not null primary key auto_increment,
    uri_id int
   ) type=InnoDb;

# vuri_rel provides a means to describe a hierarchical relationship between
# several vuri's. For example, a record name may appear in an adl file, but
# only as a result of a parent adl file providing a string substitution.
# Entries in this table are optional.

# Columns:
#   parent_vuri_id - fk to vuri (0 if the child_vuri is root of hierarchy)
#   child_vuri_id - fk to vuri
#   rel_info - describes relationship between parent/child. for adl files
#              this string will contain the substitution string used
#
create table vuri_rel
  (
    vuri_rel_id int not null primary key auto_increment,
    parent_vuri_id int,
    child_vuri_id int,
    rel_info text
  ) type=InnoDb;
    

