# Alters aoi schema, adding indexes and foreign key constraints.

# Indexes 

alter table aoi_machine add index idx_machine_aoi_id (aoi_id);

alter table aoi_document add index idx_doc_aoi_id (aoi_id);

alter table aoi_techsys add index idx_techsystem_aoi_id (aoi_id);

alter table aoi_crawler add index idx_aoi_crawler_ioc_id (ioc_id);

alter table aoi_topdisplay add index idx_topdisplay_aoi_id (aoi_id);

alter table aoi_criticality add index idx_criticality_aoi_id (aoi_id);

alter table aoi_ioc_stcmd_line add index idx_ioc_stcmd_aoi_id (aoi_id);

alter table aoi_plc_stcmd_line add index idx_aoi_plc_stcmd_line_id (aoi_id);

alter table aoi add index idx_aoi_cognizant1_id (aoi_cognizant1_id);

alter table aoi add index idx_aoi_cognizant2_id (aoi_cognizant2_id);

alter table aoi add index idx_aoi_customer_contact_id (aoi_customer_contact_id);

alter table aoi add index idx_aoi_status_id (aoi_status_id);

alter table aoi add index idx_aoi_group_name_id (aoi_customer_group_id);

# Foreign key constraints

alter table aoi add foreign key fk_aoi_cognizant1_id (aoi_cognizant1_id) references person (person_id);
alter table aoi add foreign key fk_aoi_cognizant2_id (aoi_cognizant2_id) references person (person_id);
alter table aoi add foreign key fk_aoi_customer_contact_id (aoi_customer_contact_id) references person (person_id);
alter table aoi add foreign key fk_aoi_status_id (aoi_status_id) references aoi_status (aoi_status_id);
alter table aoi add foreign key fk_aoi_customer_group_id (aoi_customer_group_id) references group_name (group_name_id);

alter table aoi_machine add foreign key fk_aoi_id (aoi_id) references aoi (aoi_id);
alter table aoi_machine add foreign key fk_machine_id (machine_id) references machine (machine_id);

alter table aoi_document add foreign key fk_aoi_id (aoi_id) references aoi (aoi_id);
alter table aoi_document add foreign key fk_doc_type_id (doc_type_id) references doc_type (doc_type_id);

alter table aoi_topdisplay add foreign key fk_aoi_id (aoi_id) references aoi (aoi_id);

alter table aoi_techsys add foreign key fk_aoi_id (aoi_id) references aoi (aoi_id);
alter table aoi_techsys add foreign key fk_technical_system_id (technical_system_id) references technical_system (technical_system_id);

alter table aoi_crawler add foreign key fk_ioc_id (ioc_id) references ioc (ioc_id);
alter table aoi_crawler add foreign key fk_ioc_boot_id (ioc_boot_id) references ioc_boot (ioc_boot_id);

alter table aoi_plc_stcmd_line add foreign key fk_aoi_id (aoi_id) references aoi (aoi_id);
alter table aoi_plc_stcmd_line add foreign key fk_plc_id (plc_id) references plc (plc_id);

alter table aoi_criticality add foreign key fk_aoi_id (aoi_id) references aoi (aoi_id);
alter table aoi_criticality add foreign key fk_criticality_id (criticality_id) references criticality_type (criticality_id);

alter table aoi_ioc_stcmd_line add foreign key fk_aoi_id (aoi_id) references aoi (aoi_id);
alter table aoi_ioc_stcmd_line add foreign key fk_ioc_stcmd_line_id (ioc_stcmd_line_id) references ioc_stcmd_line (ioc_stcmd_line_id);

alter table ioc_stcmd_line add foreign key fk_ioc_id (ioc_id) references ioc (ioc_id);

alter table aoi_relation add foreign key fk_aoi1_id (aoi1_id) references aoi (aoi_id);
alter table aoi_relation add foreign key fk_aoi2_id (aoi2_id) references aoi (aoi_id);
alter table aoi_relation add foreign key fk_aoi1_relation_type_id (aoi1_relation_type_id) references aoi_relation_type (aoi_relation_type_id);
alter table aoi_relation add foreign key fk_aoi2_relation_type_id (aoi2_relation_type_id) references aoi_relation_type (aoi_relation_type_id);

alter table aoi_epics_record add foreign key fk_aoi_epics_record_stcmdline_id (aoi_ioc_stcmd_line_id) references aoi_ioc_stcmd_line (aoi_ioc_stcmd_line_id);



