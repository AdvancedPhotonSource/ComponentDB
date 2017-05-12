use cdb; 

DELIMITER //
DROP TRIGGER IF EXISTS insert_item//
CREATE TRIGGER insert_item BEFORE INSERT ON item
FOR EACH ROW
BEGIN	
	set @unused = check_item(
		NEW.domain_id, 
		NEW.name, 
		NEW.item_identifier1, 
		NEW.item_identifier2, 
		NEW.derived_from_item_id, 
		NULL);
END//

DROP TRIGGER IF EXISTS update_item//
CREATE TRIGGER update_item BEFORE UPDATE ON item
FOR EACH ROW
BEGIN
	Declare original_domain_id INT;

	SELECT domain_id
	INTO original_domain_id
	FROM item
	WHERE id = NEW.id;

	IF (NEW.domain_id != original_domain_id)
	THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Cannot update the domain of an item.'; 
	END IF;
		
	set @unused = check_item(
		NEW.domain_id, 
		NEW.name, 
		NEW.item_identifier1, 
		NEW.item_identifier2, 
		NEW.derived_from_item_id, 
		NEW.id);
END//

DELIMITER ;
