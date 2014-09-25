
LOCK TABLES `setting_type` WRITE;

INSERT INTO `setting_type` (`name`, `description`, `default_value`)
VALUES
	('Component.List.Display.CreatedByUser', 'Display created by username.', 'false'),
	('Component.List.Display.CreatedOnDateTime', 'Display created on date/time.', 'false'),
	('Component.List.Display.Description', 'Display component description.', 'true'),
	('Component.List.Display.Id', 'Display component id.', 'true'),
	('Component.List.Display.LastModifiedByUser', 'Display last modified by username.', 'false'),
	('Component.List.Display.LastModifiedOnDateTime', 'Display last modified on date/time.', 'false'),
	('Component.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),
	('Component.List.Display.OwnerUser', 'Display owner username.', 'true'),
	('Component.List.Display.OwnerGroup', 'Display owner group name.', 'true'),
	('Component.List.Display.State', 'Display component state.', 'false'),
	('Component.List.Display.Type', 'Display component type.', 'true'),
	('Component.List.Display.TypeCategory', 'Display component type category.', 'true'),

	('Component.List.FilterBy.CreatedByUser', 'Find components that were created by username.', NULL),
	('Component.List.FilterBy.CreatedOnDateTime', 'Find components that were created on date/time.', NULL),
	('Component.List.FilterBy.Description', 'Find components by description.', NULL),
	('Component.List.FilterBy.LastModifiedByUser', 'Find components that were last modified by username.', NULL),
	('Component.List.FilterBy.LastModifiedOnDateTime', 'Find components that were last modified on date/time.', NULL),
	('Component.List.FilterBy.Name', 'Find components by name.', NULL),
	('Component.List.FilterBy.OwnerUser', 'Find components by owner username.', NULL),
	('Component.List.FilterBy.OwnerGroup', 'Find components by owner group name.', NULL),
	('Component.List.FilterBy.State', 'Find components by state.', NULL),
	('Component.List.FilterBy.Type', 'Find components by type.', NULL),
	('Component.List.FilterBy.TypeCategory', 'Find components by type category.', NULL),

	('ComponentType.List.Display.Description', 'Display component type description.', 'true'),
	('ComponentType.List.Display.Id', 'Display component type id.', 'true'),
	('ComponentType.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),

	('ComponentTypeCategory.List.Display.Description', 'Display component type category description.', 'true'),
	('ComponentTypeCategory.List.Display.Id', 'Display component type category id.', 'true'),
	('ComponentTypeCategory.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),

	('Design.List.Display.CreatedByUser', 'Display created by username.', 'false'),
	('Design.List.Display.CreatedOnDateTime', 'Display created on date/time.', 'false'),
	('Design.List.Display.Description', 'Display design description.', 'true'),
	('Design.List.Display.Id', 'Display design id.', 'true'),
	('Design.List.Display.LastModifiedByUser', 'Display last modified by username.', 'false'),
	('Design.List.Display.LastModifiedOnDateTime', 'Display last modified on date/time.', 'false'),
	('Design.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),
	('Design.List.Display.OwnerUser', 'Display owner username.', 'true'),
	('Design.List.Display.OwnerGroup', 'Display owner group name.', 'true'),

	('Design.List.FilterBy.CreatedByUser', 'Find designs that were created by username.', NULL),
	('Design.List.FilterBy.CreatedOnDateTime', 'Find designs that were created on date/time.', NULL),
	('Design.List.FilterBy.Description', 'Find designs by description.', NULL),
	('Design.List.FilterBy.LastModifiedByUser', 'Find designs that were last modified by username.', NULL),
	('Design.List.FilterBy.LastModifiedOnDateTime', 'Find designs that were last modified on date/time.', NULL),
	('Design.List.FilterBy.Name', 'Find designs by name.', NULL),
	('Design.List.FilterBy.OwnerUser', 'Find designs by owner username.', NULL),
	('Design.List.FilterBy.OwnerGroup', 'Find designs by owner group name.', NULL),

	('DesignComponent.List.Display.Description','Display design component description.', 'false'),
	('DesignComponent.List.Display.Id','Display design component id.', 'true'),
	('DesignComponent.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
	('DesignComponent.List.Display.Priority','Display design component priority.', 'true'),
	('DesignComponent.List.Display.Quantity','Display design component quantity.', 'true'),
	('DesignComponent.List.Display.Tag','Display design component tag.', 'true'),

	('DesignLink.List.Display.Description','Display design link description.', 'false'),
	('DesignLink.List.Display.Id','Display design link id.', 'true'),
	('DesignLink.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
	('DesignLink.List.Display.Tag','Display design link tag.', 'true'),

	('Log.List.Display.CreatedOnDateTime','Display log entry created on date/time.', 'true'),
	('Log.List.Display.CreatedByUser','Display log entry created by user.', 'true'),
	('Log.List.Display.Id','Display log entry id.', 'false'),
	('Log.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('PropertyType.List.Display.Description','Display property type description.', 'true'),
	('PropertyType.List.Display.Id','Display property type id.', 'true'),
	('PropertyType.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('PropertyTypeCategory.List.Display.Description','Display property type category description.', 'true'),
	('PropertyTypeCategory.List.Display.Id','Display property type category id.', 'true'),
	('PropertyTypeCategory.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('Search.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('Source.List.Display.Id','Display source id.', 'true'),
	('Source.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('UserInfo.List.Display.Id','Display user id.', 'true'),
	('UserInfo.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('UserGroup.List.Display.Id','Display user group id.', 'true'),
	('UserGroup.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25');


UNLOCK TABLES;

