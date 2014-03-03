-- Alters pv schema, adding indexes and foreign key constraints.

-- Indexes 

--create index idx_ioc_id_ia on ioc_attr (ioc_id);

create index idx_ioc_id_ib on ioc_boot (ioc_id);
create index idx_current_load_ib on ioc_boot (current_load);

create index idx_ioc_boot_id_ie on ioc_error (ioc_boot_id);

create index idx_ioc_boot_id_ir on ioc_resource (ioc_boot_id);
create index idx_uri_id_ir on ioc_resource (uri_id);

create index idx_rec_nm_r on rec (rec_nm);
create index idx_ioc_boot_id_r on rec (ioc_boot_id);
create index idx_rec_type_id_r on rec (rec_type_id);

create index idx_rec_id_f on fld (rec_id);
create index idx_fld_type_id_f on fld (fld_type_id);
create index idx_fld_val_f on fld (fld_val);
create index idx_ioc_resource_id_f on fld (ioc_resource_id);

create index idx_ioc_boot_id_rt on rec_type (ioc_boot_id);
create index idx_ioc_resource_id_rt on rec_type (ioc_resource_id);

create index idx_rec_type_id_ft on fld_type (rec_type_id);
create index idx_dbd_type_ft on fld_type (dbd_type);

-- constraints

alter table ioc_boot add constraint fk_ioc_id_ib foreign key (ioc_id) references ioc (ioc_id);

alter table ioc_error add constraint fk_ioc_boot_id_ie foreign key (ioc_boot_id) references ioc_boot (ioc_boot_id);

alter table ioc_resource add constraint fk_ioc_boot_id_ir foreign key (ioc_boot_id) references ioc_boot (ioc_boot_id);
alter table ioc_resource add constraint fk_uri_id_ir foreign key (uri_id) references uri (uri_id);

alter table rec add constraint fk_ioc_boot_id_r foreign key (ioc_boot_id) references ioc_boot (ioc_boot_id);
alter table rec add constraint fk_rec_type_id_r foreign key (rec_type_id) references rec_type (rec_type_id);

alter table fld add constraint fk_rec_id_f foreign key (rec_id) references rec (rec_id);
alter table fld add constraint fk_fld_type_id_f foreign key (fld_type_id) references fld_type (fld_type_id);
alter table fld add constraint fk_ioc_resource_id_f foreign key (ioc_resource_id) references ioc_resource (ioc_resource_id);

alter table rec_type add constraint fk_ioc_boot_id_rt foreign key (ioc_boot_id) references ioc_boot (ioc_boot_id);
alter table rec_type add constraint fk_ioc_resource_id_rt foreign key (ioc_resource_id) references ioc_resource (ioc_resource_id);

alter table fld_type add constraint fk_rec_type_id_ft foreign key (rec_type_id) references rec_type (rec_type_id);

