UPDATE user_info SET password = NULL; 

SET @MAARC_ID = 5; 

CREATE TEMPORARY TABLE relationshipIds AS
(
SELECT ier.id
FROM v_item_extras item INNER JOIN item_element_relationship ier on ier.second_item_element_id = item.self_element_id 
WHERE domain_id = @MAARC_ID
);

DELETE ierh
FROM item_element_relationship_history ierh JOIN relationshipIds on relationshipIds.id = ierh.item_element_relationship_id;

DELETE ier
FROM item_element_relationship ier JOIN relationshipIds on relationshipIds.id = ier.id; 

CREATE TEMPORARY TABLE pvids AS
(
SELECT iepv.property_value_id as id
FROM v_item_extras item LEFT OUTER JOIN item_element_property iepv on iepv.item_element_id = item.self_element_id
WHERE domain_id = @MAARC_ID
);

DELETE pm
FROM property_metadata pm JOIN pvids on pm.property_value_id = pvids.id;

DELETE iep 
FROM item_element_property iep JOIN pvids on iep.property_value_id = pvids.id;

DELETE pvh 
FROM property_value_history pvh JOIN pvids on pvh.property_value_id = pvids.id;

DELETE pv 
FROM property_value pv JOIN pvids on pv.id = pvids.id;

DELETE item_element 
FROM item_element INNER JOIN item on item.id = item_element.parent_item_id
WHERE domain_id = @MAARC_ID; 

DELETE iet
FROM item INNER JOIN item_entity_type iet on iet.item_id = item.id
WHERE domain_id = @MAARC_ID; 

DELETE iit
FROM item INNER JOIN item_item_project iit on iit.item_id = item.id
WHERE domain_id = @MAARC_ID; 

DELETE item
FROM item
WHERE domain_id = @MAARC_ID; 


CREATE TEMPORARY TABLE logIds AS
(
SELECT log_id 
FROM system_log
);

DELETE system_log
FROM system_log; 

DELETE log
FROM log JOIN logIds on logIds.log_id = log.id;

DELETE settings 
FROM user_group_setting settings;

DELETE settings
FROM user_setting settings;