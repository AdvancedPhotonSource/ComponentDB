--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME -h 127.0.0.1 -u cdb -p < updateTo3.16.2.sql`


INSERT IGNORE INTO `entity_type` VALUES (11,'IOC','Entity type used for marking a sub domain of ioc type items.');
INSERT IGNORE INTO `allowed_entity_type_domain` VALUES (6,11);

INSERT IGNORE INTO `setting_type` VALUES 
(21500,'ItemDomainMachineDesignIOC.List.Display.AlternateName','Display alternate name.','false'),
(21501,'ItemDomainMachineDesignIOC.List.Display.Description','Display design description.','true'),
(21502,'ItemDomainMachineDesignIOC.List.Display.ItemProject','Display project.','false'),
(21503,'ItemDomainMachineDesignIOC.List.Display.Id','Display id.','false'),
(21504,'ItemDomainMachineDesignIOC.List.Display.QrId','Display QR id.','false'),
(21505,'ItemDomainMachineDesignIOC.List.Display.OwnerUser','Display owner username.','false'),
(21506,'ItemDomainMachineDesignIOC.List.Display.OwnerGroup','Display owner group name.','false'),
(21507,'ItemDomainMachineDesignIOC.List.Display.CreatedByUser','Display created by username.','false'),
(21508,'ItemDomainMachineDesignIOC.List.Display.CreatedOnDateTime','Display created on date/time.','false'),
(21509,'ItemDomainMachineDesignIOC.List.Display.LastModifiedByUser','Display last modified by username.','false'),
(21510,'ItemDomainMachineDesignIOC.List.Display.LastModifiedOnDateTime','Display last modified on date/time.','false'),
(21511,'ItemDomainMachineDesignIOC.List.Display.PropertyTypeId1','Display property value for property type id #1.',''),
(21512,'ItemDomainMachineDesignIOC.List.Display.PropertyTypeId2','Display property value for property type id #2.',''),
(21513,'ItemDomainMachineDesignIOC.List.Display.PropertyTypeId3','Display property value for property type id #3.',''),
(21514,'ItemDomainMachineDesignIOC.List.Display.PropertyTypeId4','Display property value for property type id #4.',''),
(21515,'ItemDomainMachineDesignIOC.List.Display.PropertyTypeId5','Display property value for property type id #5.','');