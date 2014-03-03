-- Alters component schema, adding indexes and foreign key constraints.

-- Indexes 

create index idx_component_type_id_c on component (component_type_id);

create index idx_component_id_ac on aps_component (component_id);
create index idx_group_name_id_ac on aps_component (group_name_id);

create index idx_parent_component_id on component_rel (parent_component_id);
create index idx_child_component_id on component_rel (child_component_id);
create index idx_component_rel_type_id_cr on component_rel (component_rel_type_id);
create index idx_verified_person_id_cr on component_rel (verified_person_id);

create index idx_form_factor_id on component_type (form_factor_id);
create index idx_mfg_id on component_type (mfg_id);

create index idx_component_type_id_ctf on component_type_function (component_type_id);
create index idx_function_id on component_type_function (function_id);

create index idx_component_type_id_ctcp on component_type_person (component_type_id);
create index idx_person_id_ctcp on component_type_person (person_id);
create index idx_role_name_id on component_type_person (role_name_id);

create index idx_component_type_id_ctd on component_type_document (component_type_id);
create index idx_uri_id_ctd on component_type_document (uri_id);

create index idx_component_type_id_cts on component_type_status (component_type_id);

create index idx_component_type_id_cti on component_type_if (component_type_id);
create index idx_component_rel_type_id_cti on component_type_if (component_rel_type_id);
create index idx_component_type_if_type_id on component_type_if (component_type_if_type_id);

create index idx_component_rel_type_id_ctit on component_type_if_type (component_rel_type_id);

create index idx_component_port_type_id_cp on component_port (component_port_type_id);
create index idx_component_id on component_port (component_id);

create index idx_port_pin_type_id_pp on port_pin (port_pin_type_id);
create index idx_component_port_id_pp on port_pin (component_port_id);
create index idx_port_pin_designator_id_pp on port_pin (port_pin_designator_id);

create index idx_component_port_type_id_ppd on port_pin_designator (component_port_type_id);

create index idx_cable_id on conductor (cable_id);
create index idx_port_pin_a_id on conductor (port_pin_a_id);
create index idx_port_pin_b_id on conductor (port_pin_b_id);

create index idx_component_port_a_id on cable (component_port_a_id);
create index idx_component_port_b_id on cable (component_port_b_id);
create index idx_label on cable (label);

create index idx_component_type_id_cpt on component_port_template (component_type_id);
create index idx_component_port_type_id_cpt on component_port_template (component_port_type_id);

create index idx_component_port_template_id on port_pin_template (component_port_template_id);
create index idx_port_pin_type_id_ppt on port_pin_template (port_pin_type_id);
create index idx_port_pin_designator_id_ppt on port_pin_template (port_pin_designator_id);


-- Foreign key constraints
alter table component add constraint fk_component_type_id_c foreign key (component_type_id) references component_type (component_type_id);

alter table component_rel add constraint fk_parent_component_id foreign key (parent_component_id) references component (component_id);
alter table component_rel add constraint fk_child_component_id foreign key (child_component_id) references component (component_id);
alter table component_rel add constraint fk_component_rel_type_id_cr foreign key (component_rel_type_id) references component_rel_type (component_rel_type_id);
alter table component_rel add constraint fk_verified_person_id_cr foreign key (verified_person_id) references person (person_id);

alter table component_type add constraint fk_form_factor_id foreign key (form_factor_id) references form_factor (form_factor_id);
alter table component_type add constraint fk_mfg_id foreign key (mfg_id) references mfg (mfg_id);

alter table component_type_function add constraint fk_component_type_id_ctf foreign key (component_type_id) references component_type (component_type_id);
alter table component_type_function add constraint fk_function_id foreign key (function_id) references function (function_id);

alter table component_type_person add constraint fk_component_type_id_ctcp foreign key (component_type_id) references component_type (component_type_id);
alter table component_type_person add constraint fk_person_id_ctcp foreign key (person_id) references person (person_id);
alter table component_type_person add constraint fk_role_name_id_ctcp foreign key (role_name_id) references role_name (role_name_id);

alter table component_type_document add constraint fk_component_type_id_ctd foreign key (component_type_id) references component_type (component_type_id);
alter table component_type_document add constraint fk_uri_id_ctd foreign key (uri_id) references uri (uri_id);

alter table component_type_status add constraint fk_component_type_id_cts foreign key (component_type_id) references component_type (component_type_id);

alter table component_type_if add constraint fk_component_type_id_cti foreign key (component_type_id) references component_type (component_type_id);
alter table component_type_if add constraint fk_component_rel_type_id_cti foreign key (component_rel_type_id) references component_rel_type (component_rel_type_id);
alter table component_type_if add constraint fk_component_type_if_type_id foreign key (component_type_if_type_id) references component_type_if_type (component_type_if_type_id);

alter table component_type_if_type add constraint fk_component_rel_type_id_ctit foreign key (component_rel_type_id) references component_rel_type (component_rel_type_id);

alter table component_port add constraint fk_component_port_type_id_cp foreign key (component_port_type_id) references component_port_type (component_port_type_id);
alter table component_port add constraint fk_component_id foreign key (component_id) references component (component_id);

alter table port_pin add constraint fk_port_pin_type_id_pp foreign key (port_pin_type_id) references port_pin_type (port_pin_type_id);
alter table port_pin add constraint fk_component_port_id foreign key (component_port_id) references component_port (component_port_id);
alter table port_pin add constraint fk_port_pin_designator_id_pp foreign key (port_pin_designator_id) references port_pin_designator (port_pin_designator_id);

alter table port_pin_designator add constraint fk_component_port_type_id_ppd foreign key (component_port_type_id) references component_port_type (component_port_type_id);

alter table conductor add constraint fk_cable_id foreign key (cable_id) references cable (cable_id);
alter table conductor add constraint fk_port_pin_a_id foreign key (port_pin_a_id) references port_pin (port_pin_id);
alter table conductor add constraint fk_port_pin_b_id foreign key (port_pin_b_id) references port_pin (port_pin_id);

alter table cable add constraint fk_component_port_a_id foreign key (component_port_a_id) references component_port (component_port_id);
alter table cable add constraint fk_component_port_b_id foreign key (component_port_b_id) references component_port (component_port_id);

alter table component_port_template add constraint fk_component_type_id_cpt foreign key (component_type_id) references component_type (component_type_id);
alter table component_port_template add constraint fk_component_port_type_id_cpt foreign key (component_port_type_id) references component_port_type (component_port_type_id);

alter table port_pin_template add constraint fk_component_port_template_id foreign key (component_port_template_id) references component_port_template (component_port_template_id);
alter table port_pin_template add constraint fk_port_pin_type_id_ppt foreign key (port_pin_type_id) references port_pin_type (port_pin_type_id);
alter table port_pin_template add constraint fk_port_pin_designator_id_ppt foreign key (port_pin_designator_id) references port_pin_designator (port_pin_designator_id);



