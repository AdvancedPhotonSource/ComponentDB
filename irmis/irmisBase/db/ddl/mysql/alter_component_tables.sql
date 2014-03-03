# Alters component schema, adding indexes and foreign key constraints.

# Indexes 
alter table component add index idx_component_type_id_c (component_type_id);

alter table aps_component add index idx_component_id_ac (component_id);
alter table aps_component add index idx_group_name_id_ac (group_name_id);

alter table component_rel add index idx_parent_component_id (parent_component_id);
alter table component_rel add index idx_child_component_id (child_component_id);
alter table component_rel add index idx_component_rel_type_id_cr (component_rel_type_id);
alter table component_rel add index idx_verified_person_id_cr (verified_person_id);

alter table component_type add index idx_form_factor_id (form_factor_id);
alter table component_type add index idx_mfg_id (mfg_id);
alter table component_type add index idx_chc_beamline_interest_id (chc_beamline_interest_id);
alter table component_type add index idx_chc_contact_id (chc_contact_id);

alter table component_type_function add index idx_component_type_id_ctf (component_type_id);
alter table component_type_function add index idx_function_id (function_id);

alter table component_type_person add index idx_component_type_id_ctcp (component_type_id);
alter table component_type_person add index idx_person_id_ctcp (person_id);
alter table component_type_person add index idx_role_name_id (role_name_id);

alter table component_type_document add index idx_component_type_id_ctd (component_type_id);
alter table component_type_document add index idx_uri_id_ctd (uri_id);

alter table component_type_status add index idx_component_type_id_cts (component_type_id);

alter table component_type_if add index idx_component_type_id_cti (component_type_id);
alter table component_type_if add index idx_component_rel_type_id_cti (component_rel_type_id);
alter table component_type_if add index idx_component_type_if_type_id (component_type_if_type_id);

alter table component_type_if_type add index idx_component_rel_type_id_ctit (component_rel_type_id);

alter table component_port add index idx_component_port_type_id_cp (component_port_type_id);
alter table component_port add index idx_component_id (component_id);

alter table port_pin add index idx_port_pin_type_id_pp (port_pin_type_id);
alter table port_pin add index idx_component_port_id_pp (component_port_id);
alter table port_pin add index idx_port_pin_designator_id_pp (port_pin_designator_id);

alter table port_pin_designator add index idx_component_port_type_id_ppd (component_port_type_id);

alter table conductor add index idx_cable_id (cable_id);
alter table conductor add index idx_port_pin_a_id (port_pin_a_id);
alter table conductor add index idx_port_pin_b_id (port_pin_b_id);

alter table cable add index idx_component_port_a_id (component_port_a_id);
alter table cable add index idx_component_port_b_id (component_port_b_id);
alter table cable add index idx_label (label);

alter table component_port_template add index idx_component_type_id_cpt (component_type_id);
alter table component_port_template add index idx_component_port_type_id_cpt (component_port_type_id);

alter table port_pin_template add index idx_component_port_template_id (component_port_template_id);
alter table port_pin_template add index idx_port_pin_type_id_ppt (port_pin_type_id);
alter table port_pin_template add index idx_port_pin_designator_id_ppt (port_pin_designator_id);


# Foreign key constraints
alter table component add foreign key fk_component_type_id_c (component_type_id) references component_type (component_type_id);

alter table aps_component add foreign key fk_component_id_ac (component_id) references component (component_id);
alter table aps_component add foreign key fk_group_name_id_ac (group_name_id) references group_name (group_name_id);

alter table component_rel add foreign key fk_parent_component_id (parent_component_id) references component (component_id);
alter table component_rel add foreign key fk_child_component_id (child_component_id) references component (component_id);
alter table component_rel add foreign key fk_component_rel_type_id_cr (component_rel_type_id) references component_rel_type (component_rel_type_id);
alter table component_rel add foreign key fk_verified_person_id_cr (verified_person_id) references person (person_id);

alter table component_type add foreign key fk_form_factor_id (form_factor_id) references form_factor (form_factor_id);
alter table component_type add foreign key fk_mfg_id (mfg_id) references mfg (mfg_id);
alter table component_type add foreign key fk_chc_beamline_interest_id (chc_beamline_interest_id) references chc_beamline_interest (chc_beamline_interest_id);
alter table component_type add foreign key fk_chc_contact_id (chc_contact_id) references person (person_id);

alter table component_type_function add foreign key fk_component_type_id_ctf (component_type_id) references component_type (component_type_id);
alter table component_type_function add foreign key fk_function_id (function_id) references function (function_id);

alter table component_type_person add foreign key fk_component_type_id_ctcp (component_type_id) references component_type (component_type_id);
alter table component_type_person add foreign key fk_person_id_ctcp (person_id) references person (person_id);
alter table component_type_person add foreign key fk_role_name_id_ctcp (role_name_id) references role_name (role_name_id);

alter table component_type_document add foreign key fk_component_type_id_ctd (component_type_id) references component_type (component_type_id);
alter table component_type_document add foreign key fk_uri_id_ctd (uri_id) references uri (uri_id);

alter table component_type_status add foreign key fk_component_type_id_cts (component_type_id) references component_type (component_type_id);

alter table component_type_if add foreign key fk_component_type_id_cti (component_type_id) references component_type (component_type_id);
alter table component_type_if add foreign key fk_component_rel_type_id_cti (component_rel_type_id) references component_rel_type (component_rel_type_id);
alter table component_type_if add foreign key fk_component_type_if_type_id (component_type_if_type_id) references component_type_if_type (component_type_if_type_id);

alter table component_type_if_type add foreign key fk_component_rel_type_id_ctit (component_rel_type_id) references component_rel_type (component_rel_type_id);

alter table component_port add foreign key fk_component_port_type_id_cp (component_port_type_id) references component_port_type (component_port_type_id);
alter table component_port add foreign key fk_component_id (component_id) references component (component_id);

alter table port_pin add foreign key fk_port_pin_type_id_pp (port_pin_type_id) references port_pin_type (port_pin_type_id);
alter table port_pin add foreign key fk_component_port_id (component_port_id) references component_port (component_port_id);
alter table port_pin add foreign key fk_port_pin_designator_id_pp (port_pin_designator_id) references port_pin_designator (port_pin_designator_id);

alter table port_pin_designator add foreign key fk_component_port_type_id_ppd (component_port_type_id) references component_port_type (component_port_type_id);

alter table conductor add foreign key fk_cable_id (cable_id) references cable (cable_id);
alter table conductor add foreign key fk_port_pin_a_id (port_pin_a_id) references port_pin (port_pin_id);
alter table conductor add foreign key fk_port_pin_b_id (port_pin_b_id) references port_pin (port_pin_id);

alter table cable add foreign key fk_component_port_a_id (component_port_a_id) references component_port (component_port_id);
alter table cable add foreign key fk_component_port_b_id (component_port_b_id) references component_port (component_port_id);

alter table component_port_template add foreign key fk_component_type_id_cpt (component_type_id) references component_type (component_type_id);
alter table component_port_template add foreign key fk_component_port_type_id_cpt (component_port_type_id) references component_port_type (component_port_type_id);

alter table port_pin_template add foreign key fk_component_port_template_id (component_port_template_id) references component_port_template (component_port_template_id);
alter table port_pin_template add foreign key fk_port_pin_type_id_ppt (port_pin_type_id) references port_pin_type (port_pin_type_id);
alter table port_pin_template add foreign key fk_port_pin_designator_id_ppt (port_pin_designator_id) references port_pin_designator (port_pin_designator_id);



