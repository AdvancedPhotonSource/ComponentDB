--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.15.4.sql`


delimiter //

DROP PROCEDURE IF EXISTS is_item_relationship_have_circular_reference;//
CREATE PROCEDURE `is_item_relationship_have_circular_reference` (IN relationship_type_id int, IN parent_item_id int, IN proposed_child_item_id int)
BEGIN	
	-- Get all parents for the existing parent item.
	SELECT * FROM item WHERE ID IN (
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
		SELECT prh.parent_item_id
		FROM parent_relationship_hierarchy prh
	) and ID IN (
	-- Compare if any children match the parent in proposed children items.	
	SELECT id FROM item WHERE 
	-- Ensure single level circular reference doesn't occur.
	ID = proposed_child_item_id 
	OR ID IN (
		WITH RECURSIVE child_relationship_hierarchy AS (
			SELECT * 
			FROM v_relationship_hierarchy vrh
			WHERE vrh.parent_item_id = proposed_child_item_id
			AND vrh.relationship_type_id = relationship_type_id
			UNION
			SELECT vrh2.*
			FROM v_relationship_hierarchy vrh2, child_relationship_hierarchy a
			WHERE vrh2.parent_item_id = a.child_item_id
			AND vrh2.relationship_type_id = relationship_type_id
		)		
		SELECT crh.child_item_id
		FROM child_relationship_hierarchy crh)
	);
END //