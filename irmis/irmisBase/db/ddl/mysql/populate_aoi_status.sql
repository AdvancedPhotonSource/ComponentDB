# SQL for inserting data into the aoi_status table.
# It is likely that other aoi status types will be added later on as
# IRMIS development work continues.

# aoi_status table definition:

# Columns:
#   aoi_status_id 	- primary key, integer
#   aoi_status		- varchar(40)


INSERT INTO aoi_status (aoi_status) 
VALUES ('Active'),
       ('Inactive'),
       ('Decommissioned'),
       ('Under Development'),
       ('Other'),
       ('Undefined'),
       ('Deprecated');
