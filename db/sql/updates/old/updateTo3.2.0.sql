--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.2.0.sql` 

select @domainId:=id
from domain 
where name = "Catalog";

update item_element inner join item on item_element.parent_item_id = item.id 
set is_required = 1 
where item.domain_id = @domainId;

insert into setting_type (id, name, description, default_value) 
values (2046, "ItemDomainCatalog.ItemElementList.Display.ItemIdentifier1", "Display column for item identifier 1 in the item elements list.", "true");
insert into setting_type (id, name, description, default_value)
values (2047, "ItemDomainCatalog.List.Load.FilterDataTable","Automatically load list filter values in data table on page load.", "true"); 

insert into setting_type (id, name, description, default_value) 
values (3044, "ItemDomainInventory.ItemElementList.Display.ItemIdentifier1", "Display column for item identifier 1 in the item elements list.", "true");
insert into setting_type (id, name, description, default_value)
values (3045, "ItemDomainInventory.List.Load.FilterDataTable","Automatically load list filter values in data table on page load.", "true");

