
LOCK TABLES `setting_type` WRITE;

INSERT INTO `setting_type` (`name`, `description`, `default_value`)
VALUES
	('Collection.List.Display.CreatedByUser','Display created by username.', 'true'),
	('Collection.List.Display.CreatedOnDateTime','Display created on date/time.', 'false'),
	('Collection.List.Display.Id','Display collection id.', 'true'),
	('Collection.List.Display.LastModifiedByUser','Display last modified by username.', 'true'),
	('Collection.List.Display.LastModifiedOnDateTime','Display last modified on date/time.', 'false'),
	('Collection.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
	('Collection.List.Display.OwnerUser','Display owner username.', 'true'),
	('Collection.List.Display.OwnerGroup','Display owner group name.', 'true'),
	('Collection.List.Display.ParentCollection','Display parent collection name.', 'true'),

	('Collection.List.FilterBy.CreatedByUser','Find collections that were created by username.', NULL),
	('Collection.List.FilterBy.CreatedOnDateTime','Find collections that were created on date/time.', NULL),
	('Collection.List.FilterBy.LastModifiedByUser','Find collections that were last modified by username.', NULL),
	('Collection.List.FilterBy.LastModifiedOnDateTime','Find collections that were last modified on date/time.', NULL),
	('Collection.List.FilterBy.Name','Find collections by name.', NULL),
	('Collection.List.FilterBy.OwnerUser','Find collections by owner username.', NULL),
	('Collection.List.FilterBy.OwnerGroup','Find collections by owner group name.', NULL),
	('Collection.List.FilterBy.ParentCollection','Find collections by parent collection name.', NULL),

	('CollectionComponent.List.Display.Description','Display collection component description.', 'false'),
	('CollectionComponent.List.Display.Id','Display collection component id.', 'false'),
	('CollectionComponent.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
	('CollectionComponent.List.Display.Priority','Display collection component priority.', 'true'),
	('CollectionComponent.List.Display.Quantity','Display collection component quantity.', 'true'),
	('CollectionComponent.List.Display.Tag','Display collection component tag.', 'true'),

	('Component.List.Display.CreatedByUser','Display created by username.', 'true'),
	('Component.List.Display.CreatedOnDateTime','Display created on date/time.', 'false'),
	('Component.List.Display.DocumentationUri','Display documentation URI.', 'true'),
	('Component.List.Display.EstimatedCost','Display estimated cost.', 'true'),
	('Component.List.Display.Id','Display component id.', 'true'),
	('Component.List.Display.LastModifiedByUser','Display last modified by username.', 'true'),
	('Component.List.Display.LastModifiedOnDateTime','Display last modified on date/time.', 'false'),
	('Component.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
	('Component.List.Display.OwnerUser','Display owner username.', 'true'),
	('Component.List.Display.OwnerGroup','Display owner group name.', 'true'),
	('Component.List.Display.State','Display component state.', 'true'),
	('Component.List.Display.Type','Display component type.', 'true'),

	('Component.List.FilterBy.CreatedByUser','Find components that were created by username.', NULL),
	('Component.List.FilterBy.CreatedOnDateTime','Find components that were created on date/time.', NULL),
	('Component.List.FilterBy.DocumentationUri','Find components by documentation URI.', NULL),
	('Component.List.FilterBy.LastModifiedByUser','Find components that were last modified by username.', NULL),
	('Component.List.FilterBy.LastModifiedOnDateTime','Find components that were last modified on date/time.', NULL),
	('Component.List.FilterBy.Name','Find components by name.', NULL),
	('Component.List.FilterBy.OwnerUser','Find components by owner username.', NULL),
	('Component.List.FilterBy.OwnerGroup','Find components by owner group name.', NULL),
	('Component.List.FilterBy.State','Find components by state.', NULL),
	('Component.List.FilterBy.Type','Find components by type.', NULL),
	('Component.List.FilterBy.EstimatedCost','Find components by estimated cost.', NULL),

	('PropertyType.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('PropertyTypeCategory.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('Source.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('User.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25');


UNLOCK TABLES;

