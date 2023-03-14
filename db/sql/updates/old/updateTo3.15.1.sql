
INSERT INTO `setting_type` VALUES
(26000,'ItemDomainApp.List.Display.Id','Display for components by description.','false'),
(26001,'ItemDomainApp.List.Display.DisplayItemType','Display for components by description.','true'),
(26002,'ItemDomainApp.List.Display.DisplayItemCategory','Display for components by description.','true'),
(26003,'ItemDomainApp.List.Display.Description','Display for components by description.','true'),
(26004,'ItemDomainApp.List.Display.CreatedByUser','Display for components that were created by username.','false'),
(26005,'ItemDomainApp.List.Display.CreatedOnDateTime','Display for components that were created on date/time.','false'),
(26006,'ItemDomainApp.List.Display.LastModifiedByUser','Display for components that were last modified by username.','false'),
(26007,'ItemDomainApp.List.Display.LastModifiedOnDateTime','Display for components that were last modified on date/time.','false'),
(26008,'ItemDomainApp.List.Display.OwnerUser','Display for component instances by owner username.','false'),
(26009,'ItemDomainApp.List.Display.OwnerGroup','Display for component instances by owner group name.','false'),
(26010,'ItemDomainApp.List.Display.PropertyTypeId1','Display for property value with displayed property type id #1.',''),
(26011,'ItemDomainApp.List.Display.PropertyTypeId2','Display for property value with displayed property type id #2.',''),
(26012,'ItemDomainApp.List.Display.PropertyTypeId3','Display for property value with displayed property type id #3.',''),
(26013,'ItemDomainApp.List.Display.PropertyTypeId4','Display for property value with displayed property type id #4.',''),
(26014,'ItemDomainApp.List.Display.PropertyTypeId5','Display for property value with displayed property type id #5.',''),
(26015,'ItemDomainApp.List.Display.NumberOfItemsPerPage','Display for components by description.','false');

INSERT INTO `domain` VALUES
(10,'App', 'Item domain for managing C2 apps.', NULL, NULL, "Type", 'Technical System');

INSERT INTO `setting_type` VALUES
(27000,'ItemDomainAppDeployment.List.Display.Id','Display for components by description.','false'),
(27001,'ItemDomainAppDeployment.List.Display.DisplayItemCategory','Display for components by description.','true'),
(27002,'ItemDomainAppDeployment.List.Display.Description','Display for components by description.','true'),
(27003,'ItemDomainAppDeployment.List.Display.CreatedByUser','Display for components that were created by username.','false'),
(27004,'ItemDomainAppDeployment.List.Display.CreatedOnDateTime','Display for components that were created on date/time.','false'),
(27005,'ItemDomainAppDeployment.List.Display.LastModifiedByUser','Display for components that were last modified by username.','false'),
(27006,'ItemDomainAppDeployment.List.Display.LastModifiedOnDateTime','Display for components that were last modified on date/time.','false'),
(27007,'ItemDomainAppDeployment.List.Display.OwnerUser','Display for component instances by owner username.','false'),
(27008,'ItemDomainAppDeployment.List.Display.OwnerGroup','Display for component instances by owner group name.','false'),
(27009,'ItemDomainAppDeployment.List.Display.PropertyTypeId1','Display for property value with displayed property type id #1.',''),
(27010,'ItemDomainAppDeployment.List.Display.PropertyTypeId2','Display for property value with displayed property type id #2.',''),
(27011,'ItemDomainAppDeployment.List.Display.PropertyTypeId3','Display for property value with displayed property type id #3.',''),
(27012,'ItemDomainAppDeployment.List.Display.PropertyTypeId4','Display for property value with displayed property type id #4.',''),
(27013,'ItemDomainAppDeployment.List.Display.PropertyTypeId5','Display for property value with displayed property type id #5.',''),
(27014,'ItemDomainAppDeployment.List.Display.NumberOfItemsPerPage','Display for components by description.','false');

INSERT INTO `domain` VALUES
(11,'App Deployment', 'Item domain for managing C2 app deployments.', NULL, NULL, NULL, 'Deployment Type');

UPDATE setting_type set default_value = 'true' where name = 'ItemDomainMachineDesign.List.Display.QrId';
UPDATE setting_type set default_value = 'false' where name = 'ItemDomainMachineDesign.List.Display.InstalledQrId';
