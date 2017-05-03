--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

delimiter //

create procedure `inventory_items_with_avaiable_connector`  (IN connector_type_id int, IN is_male tinyint(1)) 
BEGIN  
    SET @query = 'select distinct item.* from item inner join v_item_domain_inventory_connector_status on inv_item_id = item.id where inv_connector_id is null';
    
    SET @query = CONCAT(@query, ' AND connector_type_id = ', connector_type_id);  
        
    SET @query = CONCAT(@query, ' AND is_male = ' , is_male); 

    PREPARE stmt FROM @query;
    EXECUTE stmt;
END //

create procedure `available_connectors_for_inventory_item` (IN item_id int, IN connector_type_id int, IN is_male tinyint(1)) 
BEGIN 
    SET @query = 'select connector.* from v_item_domain_inventory_connector_status ics inner join connector on ics.cat_connector_id = connector.id where ics.second_item_connector_id is null'; SET
    @query = CONCAT(@query, ' AND ics.inv_item_id =', item_id);
    IF (connector_type_id IS NOT NULL) 
    THEN 
        SET @query = CONCAT(@query, ' AND connector.connector_type_id =', connector_type_id); 
    END IF;
     
    IF (is_male IS NOT NULL) 
    THEN 
         SET @query = CONCAT(@query, ' AND connector.is_male =', is_male); 
    END IF;
    PREPARE stmt FROM @query; 
    EXECUTE stmt; 
END //

drop procedure if exists items_with_write_permission_for_user;//
create procedure `items_with_write_permission_for_user` (IN user_id int, IN domain_id int)
BEGIN
    SET @query = 'SELECT i.* FROM item i inner join v_item_self_element ise on i.id = ise.item_id inner join entity_info ei on ise.entity_info_id = ei.id WHERE';
    SET @query = CONCAT(@query, ' i.domain_id =', domain_id);
    SET @query = CONCAT(@query, ' AND (ei.owner_user_id =', user_id);
    SET @query = CONCAT(@query, ' OR ', '(ei.owner_user_group_id in (select ug.id from user_group ug inner join user_user_group uug on uug.user_group_id = ug.id where uug.user_id = ', user_id, ')');
    SET @query = CONCAT(@query, ' AND ei.is_group_writeable = 1))');

    PREPARE stmt FROM @query;
    EXECUTE stmt;
END //

delimiter ;
