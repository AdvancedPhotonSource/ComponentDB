# SQL for inserting data into the doc_type table.
# This data is unique to the Advanced Photon Source accelerator.

# machine table definition:

#  Columns:
#     doc_type_id 	- primary key
#     doc_type


INSERT INTO doc_type (doc_type) 
VALUES  ('Tutorial'),
	('Quick Reference Manual'),	
	('Block Diagram'),
        ('Validation Procedure'),
	('Trouble Shooting Guide'),
	('IOC Boot Startup Command'),
	('Undefined'),
	('Wiring/Cable'),
	('Other'),
	('Script'),
	('Functional Criteria'),
	('Design Specification');
