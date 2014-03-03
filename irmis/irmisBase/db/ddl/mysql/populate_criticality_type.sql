# SQL for inserting data into the criticality_type table.

# criticality_type table definition:

# Columns:
#   criticality_id 	- primary key
#   criticality_level
#   criticality_classification


INSERT INTO criticality_type(criticality_level, criticality_classification) 
VALUES (1, 'Personnel Risk [e.g. Radiation Safety System]'),
       (2, 'Equipment or Beam Inhibit Risk'),
       (3, 'Beam Performance Risk'),
       (4, 'General Operations'),
       (5, 'R&D [e.g. Test Stand]'),
       (6, 'Undefined');	
