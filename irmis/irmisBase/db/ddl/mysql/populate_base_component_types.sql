# Creates base component types, and registers them in base_component_type
# table. These are the set of base types distributed with irmisBase.

insert into base_component_type values (1, 'Network');
insert into base_component_type values (2, 'Site');
insert into base_component_type values (3, 'Utility');
insert into base_component_type values (4, 'Building');
insert into base_component_type values (5, 'Room');
insert into base_component_type values (6, 'Rack');
insert into base_component_type values (7, 'Switch Gear');
insert into base_component_type values (8, 'Circuit Breaker');
insert into base_component_type values (9, 'AC Panel');
insert into base_component_type values (10, 'AC Circuit (120V)');
insert into base_component_type values (11, '120VAC Power Strip/Outlet');
insert into base_component_type values (12, 'VME Chassis-Mupac');
insert into base_component_type values (13, 'MVME 2100');
insert into base_component_type values (14, 'VME Power Supply - Mupac');

insert into component_type values (1, 'Network', 'Controls network. Root of controls hierarchy.', null, 1, null, 1, 1, 0, 4);
insert into component_type_if values (1, 1, 0, 1, 0, 1, 129, 0, 0);
insert into component_type_status values (1, 1, 0, 0, null, 1, 0, 0);

insert into component_type values (2, 'Site', 'Overall facility. Root of housing hierarchy.', null, 1, null, 1, 1, 0, 4);
insert into component_type_if values (2, 2, 0, 1, 0, 2, 51, 0, 0);
insert into component_type_status values (2, 2, 0, 0, null, 1, 0, 0);

insert into component_type values (3, 'Utility', 'Power utility. Root of power hierarchy.', null, 1, null, 1, 1, 0, 3);
insert into component_type_if values (3, 3, 0, 1, 0, 3, 114, 0, 0);
insert into component_type_status values (3, 3, 0, 0, null, 1, 0, 0);

insert into component_type values (4, 'Building', 'On site building', null, 1, null, 1, 1, 0, 3);
insert into component_type_function values (1, 4, 36, 0, 0);
insert into component_type_if values (4, 4, 0, 1, 0, 2, 52, 0, 0);
insert into component_type_if values (5, 4, 1, 0, 0, 2, 51, 0, 0);
insert into component_type_status values (4, 4, 0, 0, null, 1, 0, 0);

insert into component_type values (5, 'Room', 'Room', null, 1, null, 1, 1, 0, 3);
insert into component_type_function values (2, 5, 36, 0, 0);
insert into component_type_if values (6, 5, 1, 0, 0, 2, 52, 0, 0);
insert into component_type_if values (7, 5, 0, 1, 0, 2, 8, 0, 0);
insert into component_type_if values (8, 5, 0, 1, 0, 2, 5, 0, 0);
insert into component_type_if values (9, 5, 0, 1, 0, 2, 53, 0, 0);
insert into component_type_status values (5, 5, 0, 0, null, 1, 0, 0);

insert into component_type values (6, 'Rack', '19" Equipment Rack', null, 1, null, 1, 1, 0, 3);
insert into component_type_function values (3, 6, 36, 0, 0);
insert into component_type_if values (10, 6, 0, 1, 0, 2, 9, 0, 0);
insert into component_type_if values (11, 6, 1, 0, 0, 2, 53, 0, 0);
insert into component_type_if values (12, 6, 0, 1, 0, 2, 5, 0, 0);
insert into component_type_if values (13, 6, 0, 1, 0, 2, 2, 0, 0);
insert into component_type_status values (6, 6, 0, 0, null, 1, 0, 0);

insert into component_type values (7, 'Switch Gear', 'AC Switch Gear', null, 1, null, 8, 1, 0, 3);
insert into component_type_if values (14, 7, 0, 1, 0, 3, 258, 0, 0);
insert into component_type_if values (15, 7, 1, 0, 0, 3, 114, 0, 0);
insert into component_type_if values (39, 7, 1, 0, 0, 2, 53, 0, 0);
insert into component_type_if values (40, 7, 0, 1, 0, 2, 259, 0, 0);
insert into component_type_status values (7, 7, 0, 0, null, 1, 0, 0);

insert into component_type values (8, 'Circuit Breaker', 'AC Circuit Breaker', null, 1, null, 8, 1, 0, 0);
insert into component_type_if values (16, 8, 0, 1, 0, 3, 91, 0, 0);
insert into component_type_if values (17, 8, 1, 0, 0, 3, 258, 0, 0);
insert into component_type_if values (41, 8, 1, 0, 0, 2, 259, 0, 0);
insert into component_type_status values (8, 8, 0, 0, null, 1, 0, 0);

insert into component_type values (9, 'AC Panel', 'AC Distribution/Breaker Panel', null, 1, null, 18, 1, 0, 0);
insert into component_type_function values (4, 9, 2, 0, 0);
insert into component_type_if values (18, 9, 0, 1, 0, 2, 58, 0, 0);
insert into component_type_if values (19, 9, 0, 1, 0, 3, 106, 0, 0);
insert into component_type_if values (20, 9, 0, 1, 0, 3, 125, 0, 0);
insert into component_type_if values (21, 9, 1, 0, 0, 3, 91, 0, 0);
insert into component_type_if values (22, 9, 1, 0, 0, 2, 8, 0, 0);
insert into component_type_status values (9, 9, 0, 0, null, 1, 0, 0);

insert into component_type values (10, 'AC Circuit (120V)', '120VAC Circuit', null, 1, null, 5, 1, 0, 0);
insert into component_type_function values (5, 10, 2, 0, 0);
insert into component_type_if values (23, 10, 0, 1, 0, 3, 106, 0, 0);
insert into component_type_if values (24, 10, 1, 0, 0, 3, 106, 0, 0);
insert into component_type_if values (42, 10, 1, 0, 0, 2, 58, 0, 0);
insert into component_type_status values (10, 10, 0, 0, null, 1, 0, 0);

insert into component_type values (11, '120VAC Power Strip/Outlet', '120VAC Power Strip/Circuit', null, 1, null, 5, 1, 0, 0);
insert into component_type_function values (6, 11, 2, 0, 0);
insert into component_type_if values (25, 11, 0, 1, 0, 3, 78, 0, 0);
insert into component_type_if values (26, 11, 1, 0, 0, 2, 2, 0, 0);
insert into component_type_if values (27, 11, 1, 0, 0, 3, 106, 0, 0);
insert into component_type_if values (28, 11, 1, 0, 0, 3, 78, 0, 0);
insert into component_type_status values (11, 11, 0, 0, null, 1, 0, 0);

insert into component_type values (12, 'VME Chassis-Mupac', '20 Slot VME Chassis - 9U', null, 1, null, 2, 62, 0, 0);
insert into component_type_function values (7, 12, 12, 0, 0);
insert into component_type_if values (29, 12, 0, 1, 0, 1, 136, 0, 0);
insert into component_type_if values (30, 12, 0, 1, 0, 2, 3, 0, 0);
insert into component_type_if values (31, 12, 1, 0, 0, 1, 185, 0, 0);
insert into component_type_if values (32, 12, 0, 1, 0, 3, 79, 0, 0);
insert into component_type_if values (33, 12, 1, 0, 0, 3, 111, 0, 0);
insert into component_type_if values (34, 12, 0, 1, 0, 2, 11, 0, 0);
insert into component_type_if values (35, 12, 1, 0, 0, 2, 2, 0, 0);
insert into component_type_if values (36, 12, 1, 0, 0, 1, 142, 0, 0);
insert into component_type_if values (37, 12, 0, 1, 0, 2, 54, 0, 0);
insert into component_type_if values (38, 12, 0, 1, 0, 1, 150, 0, 0);
insert into component_type_status values (12, 12, 0, 0, null, 1, 0, 0);

insert into component_type values (13, 'MVME 2100', 'MVME 2100', null, 1, null, 3, 60, 0, 0);
insert into component_type_function values (8, 13, 19, 0, 0);
insert into component_type_function values (9, 13, 41, 0, 0);
insert into component_type_function values (10, 13, 43, 0, 0);
insert into component_type_if values (43, 13, 0, 1, 0, 1, 224, 0, 0);
insert into component_type_if values (44, 13, 1, 0, 0, 3, 79, 0, 0);
insert into component_type_if values (45, 13, 0, 1, 0, 1, 185, 0, 0);
insert into component_type_if values (46, 13, 0, 1, 0, 2, 76, 0, 0);
insert into component_type_if values (47, 13, 0, 1, 0, 1, 256, 0, 0);
insert into component_type_if values (48, 13, 0, 1, 0, 1, 190, 0, 0);
insert into component_type_if values (49, 13, 0, 1, 0, 1, 131, 0, 0);
insert into component_type_if values (50, 13, 0, 1, 0, 1, 223, 0, 0);
insert into component_type_if values (51, 13, 1, 0, 0, 2, 3, 0, 0);
insert into component_type_if values (52, 13, 1, 0, 0, 1, 136, 0, 0);
insert into component_type_if values (53, 13, 1, 0, 0, 1, 129, 0, 0);
insert into component_type_if values (54, 13, 0, 1, 0, 1, 172, 0, 0);
insert into component_type_status values (13, 13, 0, 0, null, 1, 0, 0);

insert into component_port_type values (1, 'RJ45', 'Miscellaneous', 8, null, null, 0, 0);

insert into port_pin_designator values (1, 1, 0, 1, 0, 0);
insert into port_pin_designator values (2, 1, 1, 2, 0, 0);
insert into port_pin_designator values (3, 1, 2, 3, 0, 0);
insert into port_pin_designator values (4, 1, 3, 4, 0, 0);
insert into port_pin_designator values (5, 1, 4, 5, 0, 0);
insert into port_pin_designator values (6, 1, 5, 6, 0, 0);
insert into port_pin_designator values (7, 1, 6, 7, 0, 0);
insert into port_pin_designator values (8, 1, 7, 8, 0, 0);

insert into component_port_template values (1, 13, 1, '10/100 Base T', 0, 0, 0);
insert into port_pin_template values (1, 1, null, 1, ' ', 0, 0);
insert into port_pin_template values (2, 1, null, 2, ' ', 0, 0);
insert into port_pin_template values (3, 1, null, 3, ' ', 0, 0);
insert into port_pin_template values (4, 1, null, 4, ' ', 0, 0);
insert into port_pin_template values (5, 1, null, 5, ' ', 0, 0);
insert into port_pin_template values (6, 1, null, 6, ' ', 0, 0);
insert into port_pin_template values (7, 1, null, 7, ' ', 0, 0);
insert into port_pin_template values (8, 1, null, 8, ' ', 0, 0);
insert into component_port_template values (2, 13, 1, 'Debug', 1, 0, 0);
insert into port_pin_template values (9, 2, null, 1, ' ', 0, 0);
insert into port_pin_template values (10, 2, null, 2, ' ', 0, 0);
insert into port_pin_template values (11, 2, null, 3, ' ', 0, 0);
insert into port_pin_template values (12, 2, null, 4, ' ', 0, 0);
insert into port_pin_template values (13, 2, null, 5, ' ', 0, 0);
insert into port_pin_template values (14, 2, null, 6, ' ', 0, 0);
insert into port_pin_template values (15, 2, null, 7, ' ', 0, 0);
insert into port_pin_template values (16, 2, null, 8, ' ', 0, 0);


insert into component_type values (14, 'VME Power Supply - Mupac', 'Integrated PS w fan for MUPAC', null, 1, null, 5, 112, 0, 0);
insert into component_type_function values (11, 14, 2, 0, 0);
insert into component_type_if values (55, 14, 1, 0, 0, 2, 54, 0, 0);
insert into component_type_if values (56, 14, 1, 0, 0, 3, 78, 0, 0);
insert into component_type_if values (57, 14, 0, 1, 0, 3, 111, 0, 0);
insert into component_type_status values (14, 14, 0, 0, null, 1, 0, 0);
