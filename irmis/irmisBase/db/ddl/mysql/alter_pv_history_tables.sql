# Alters pv history schema, adding indexes and foreign key constraints.

# Indexes 

alter table rec_history add index idx_rec_history_nm (rec_nm);
alter table rec_history add index idx_ioc_boot_history_id (ioc_boot_id);
alter table rec_history add index idx_rec_type_history_id (rec_type_history_id);

alter table fld_history add index idx_rec_history_id (rec_history_id);
alter table fld_history add index idx_fld_type_history_id (fld_type_history_id);
alter table fld_history add index idx_fld_val (fld_val);
alter table fld_history add index idx_ioc_resource_history_id (ioc_resource_history_id);

alter table rec_type_history add index idx_ioc_boot_id (ioc_boot_id);
alter table rec_type_history add index idx_ioc_resource_history_id (ioc_resource_history_id);

alter table fld_type_history add index idx_rec_type_history_id (rec_type_history_id);
alter table fld_type_history add index idx_dbd_history_type (dbd_type);

alter table rec_type_dev_sup_history add index idx_rec_type_history_id_rtds (rec_type_history_id);

# Foreign key constraints

alter table ioc add foreign key fk_ioc_status_id (ioc_status_id) references ioc_status (ioc_status_id) on delete restrict;

alter table rec_history add foreign key fk_ioc_boot_history_id (ioc_boot_id) references ioc_boot (ioc_boot_id) on delete restrict;
alter table rec_history add foreign key fk_rec_type_history_id (rec_type_history_id) references rec_type_history (rec_type_history_id) on delete restrict;

alter table fld_history add foreign key fk_rec_history_id (rec_history_id) references rec_history (rec_history_id) on delete restrict;
alter table fld_history add foreign key fk_fld_type_history_id (fld_type_history_id) references fld_type_history (fld_type_history_id) on delete restrict;
alter table fld_history add foreign key fk_ioc_resource_history_id (ioc_resource_history_id) references ioc_resource_history (ioc_resource_history_id) on delete restrict;

alter table rec_type_history add foreign key fk_ioc_boot_history_id (ioc_boot_id) references ioc_boot (ioc_boot_id) on delete restrict;
alter table rec_type_history add foreign key fk_ioc_resource_history_id (ioc_resource_history_id) references ioc_resource_history (ioc_resource_history_id) on delete restrict;

alter table fld_type_history add foreign key fk_rec_type_history_id (rec_type_history_id) references rec_type_history (rec_type_history_id) on delete restrict;

alter table rec_type_dev_sup_history add foreign key fk_rec_type_history_id_rtds (rec_type_history_id) references rec_type_history (rec_type_history_id);

alter table rec_alias add foreign key fk_rec_alias_id (rec_id) references rec (rec_id) on delete restrict;

alter table rec_type_dev_sup add foreign key fk_rec_type_id_rtds (rec_type_id) references rec_type (rec_type_id);

alter table rec_alias_history add foreign key fk_rec_alias_history_id (rec_history_id) references rec_history (rec_history_id) on delete restrict;