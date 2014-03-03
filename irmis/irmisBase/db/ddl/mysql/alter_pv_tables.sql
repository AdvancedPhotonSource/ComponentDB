# Alters pv schema, adding indexes and foreign key constraints.

# Indexes 

#alter table ioc_attr add index idx_ioc_id (ioc_id);

alter table ioc_boot add index idx_ioc_id (ioc_id);
alter table ioc_boot add index idx_current_load (current_load);

alter table ioc_error add index idx_ioc_boot_id (ioc_boot_id);

alter table ioc_resource add index idx_ioc_boot_id (ioc_boot_id);
alter table ioc_resource add index idx_uri_id (uri_id);

alter table rec add index idx_rec_nm (rec_nm);
alter table rec add index idx_ioc_boot_id (ioc_boot_id);
alter table rec add index idx_rec_type_id (rec_type_id);

alter table fld add index idx_rec_id (rec_id);
alter table fld add index idx_fld_type_id (fld_type_id);
alter table fld add index idx_fld_val (fld_val);
alter table fld add index idx_ioc_resource_id (ioc_resource_id);

alter table rec_type add index idx_ioc_boot_id (ioc_boot_id);
alter table rec_type add index idx_ioc_resource_id (ioc_resource_id);

alter table fld_type add index idx_rec_type_id (rec_type_id);
alter table fld_type add index idx_dbd_type (dbd_type);

alter table rec_type_dev_sup add index idx_rec_type_id_rtds (rec_type_id);

# Foreign key constraints
#alter table ioc_attr add foreign key fk_ioc_id (ioc_id) references ioc (ioc_id) on delete restrict;
#alter table ioc_attr add foreign key fk_ioc_attr_type_id (ioc_attr_type_id) references ioc_attr_type (ioc_attr_type_id) on delete restrict;

alter table ioc_boot add foreign key fk_ioc_id (ioc_id) references ioc (ioc_id) on delete restrict;

alter table ioc_error add foreign key fk_ioc_boot_id (ioc_boot_id) references ioc_boot (ioc_boot_id) on delete restrict;

alter table ioc_resource add foreign key fk_ioc_boot_id (ioc_boot_id) references ioc_boot (ioc_boot_id) on delete restrict;
alter table ioc_resource add foreign key fk_uri_id (uri_id) references uri (uri_id) on delete restrict;

alter table rec add foreign key fk_ioc_boot_id (ioc_boot_id) references ioc_boot (ioc_boot_id) on delete restrict;
alter table rec add foreign key fk_rec_type_id (rec_type_id) references rec_type (rec_type_id) on delete restrict;

alter table fld add foreign key fk_rec_id (rec_id) references rec (rec_id) on delete restrict;
alter table fld add foreign key fk_fld_type_id (fld_type_id) references fld_type (fld_type_id) on delete restrict;
alter table fld add foreign key fk_ioc_resource_id (ioc_resource_id) references ioc_resource (ioc_resource_id) on delete restrict;

alter table rec_type add foreign key fk_ioc_boot_id (ioc_boot_id) references ioc_boot (ioc_boot_id) on delete restrict;
alter table rec_type add foreign key fk_ioc_resource_id (ioc_resource_id) references ioc_resource (ioc_resource_id) on delete restrict;

alter table fld_type add foreign key fk_rec_type_id (rec_type_id) references rec_type (rec_type_id) on delete restrict;

alter table rec_type_dev_sup add foreign key fk_rec_type_id_rtds (rec_type_id) references rec_type (rec_type_id);


