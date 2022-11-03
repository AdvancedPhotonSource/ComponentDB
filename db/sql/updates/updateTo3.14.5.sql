--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.14.5.sql`

INSERT INTO setting_type VALUES
(21021,'ItemDomainMachineDesign.List.Display.MaximumNumberOfSearchResults','Display maximum number of result rows for a machine filter.','600');

DROP VIEW IF EXISTS v_relationship_hierarchy;
CREATE VIEW v_relationship_hierarchy
AS
SELECT 
	parent_item.id as parent_item_id, 
	parent_item.name as parent_name, 
	child_item.id as child_item_id, 
	child_item.name as child_name,
	ier.id as item_element_relationship_id,
	ier.relationship_type_id
FROM v_item_extras AS child_item INNER JOIN item_element_relationship AS ier ON ier.first_item_element_id = child_item.self_element_id
INNER JOIN v_item_extras AS parent_item ON parent_item.self_element_id = ier.second_item_element_id; 

delimiter //
DROP PROCEDURE IF EXISTS is_item_relationship_have_circular_reference;//
CREATE PROCEDURE `is_item_relationship_have_circular_reference` (IN relationship_type_id int, IN parent_item_id int, IN proposed_child_item_id int)
BEGIN
	SELECT * from item where ID in (
	WITH RECURSIVE child_relationship_hierarchy AS (
	SELECT *
	FROM v_relationship_hierarchy vrh
	WHERE vrh.parent_item_id = parent_item_id
	AND vrh.relationship_type_id = relationship_type_id
	UNION
	SELECT vrh2.*
	FROM v_relationship_hierarchy vrh2, child_relationship_hierarchy a
	WHERE vrh2.parent_item_id = a.child_item_id
	AND vrh2.relationship_type_id = relationship_type_id
	)
	SELECT crh.parent_item_id
	FROM child_relationship_hierarchy crh
	WHERE child_item_id = proposed_child_item_id
	)
	UNION ALL
	SELECT * FROM item WHERE id in (
	WITH RECURSIVE parent_relationship_hierarchy AS (
	SELECT *
	FROM v_relationship_hierarchy vrh
	WHERE vrh.child_item_id = parent_item_id
	AND vrh.relationship_type_id = relationship_type_id
	UNION
	SELECT vrh2.*
	FROM v_relationship_hierarchy vrh2, parent_relationship_hierarchy a
	WHERE vrh2.child_item_id = a.parent_item_id
	AND vrh2.relationship_type_id = relationship_type_id
	)
	SELECT prh.child_item_id
	FROM parent_relationship_hierarchy as prh
	WHERE prh.parent_item_id = proposed_child_item_id
	);
END //