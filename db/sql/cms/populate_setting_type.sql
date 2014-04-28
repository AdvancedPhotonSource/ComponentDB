
LOCK TABLES `setting_type` WRITE;

INSERT INTO `setting_type` (`name`, `description`, `default_value`)
VALUES
	('Collection.List.Display.CreatedByUser','Display created by username.', 'false'),
	('Collection.List.Display.CreatedOnDateTime','Display created on date/time.', 'false'),
	('Collection.List.Display.Description','Display collection description.', 'true'),
	('Collection.List.Display.Id','Display collection id.', 'true'),
	('Collection.List.Display.LastModifiedByUser','Display last modified by username.', 'false'),
	('Collection.List.Display.LastModifiedOnDateTime','Display last modified on date/time.', 'false'),
	('Collection.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
	('Collection.List.Display.OwnerUser','Display owner username.', 'true'),
	('Collection.List.Display.OwnerGroup','Display owner group name.', 'true'),

	('Collection.List.FilterBy.CreatedByUser','Find collections that were created by username.', NULL),
	('Collection.List.FilterBy.CreatedOnDateTime','Find collections that were created on date/time.', NULL),
	('Collection.List.FilterBy.Description','Find collections by description.', NULL),
	('Collection.List.FilterBy.LastModifiedByUser','Find collections that were last modified by username.', NULL),
	('Collection.List.FilterBy.LastModifiedOnDateTime','Find collections that were last modified on date/time.', NULL),
	('Collection.List.FilterBy.Name','Find collections by name.', NULL),
	('Collection.List.FilterBy.OwnerUser','Find collections by owner username.', NULL),
	('Collection.List.FilterBy.OwnerGroup','Find collections by owner group name.', NULL),

	('Collection.View.ChildCollectionList.Display.CreatedByUser','Display created by username for child collection list.', 'false'),
	('Collection.View.ChildCollectionList.Display.CreatedOnDateTime','Display created on date/time for child collection list.', 'false'),
	('Collection.View.ChildCollectionList.Display.Description','Display collection description for child collection list.', 'false'),
	('Collection.View.ChildCollectionList.Display.Id','Display collection id for child collection list.', 'true'),
	('Collection.View.ChildCollectionList.Display.LastModifiedByUser','Display last modified by username for child collection list.', 'false'),
	('Collection.View.ChildCollectionList.Display.LastModifiedOnDateTime','Display last modified on date/time for child collection list.', 'false'),
	('Collection.View.ChildCollectionList.Display.NumberOfItemsPerPage','Display specified number of items per page for child collection list.', '25'),
	('Collection.View.ChildCollectionList.Display.OwnerUser','Display owner username for child collection list.', 'false'),
	('Collection.View.ChildCollectionList.Display.OwnerGroup','Display owner group name for child collection list.', 'false'),

	('Collection.View.CollectionComponentList.Display.Description','Display collection component description.', 'false'),
	('Collection.View.CollectionComponentList.Display.Id','Display collection component id.', 'true'),
	('Collection.View.CollectionComponentList.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
	('Collection.View.CollectionComponentList.Display.Priority','Display collection component priority.', 'true'),
	('Collection.View.CollectionComponentList.Display.Quantity','Display collection component quantity.', 'true'),
	('Collection.View.CollectionComponentList.Display.Tag','Display collection component tag.', 'true'),

	('Collection.View.ParentCollectionList.Display.CreatedByUser','Display created by username for parent collection list.', 'false'),
	('Collection.View.ParentCollectionList.Display.CreatedOnDateTime','Display created on date/time for parent collection list.', 'false'),
	('Collection.View.ParentCollectionList.Display.Description','Display collection description for parent collection list.', 'false'),
	('Collection.View.ParentCollectionList.Display.Id','Display collection id for parent collection list.', 'true'),
	('Collection.View.ParentCollectionList.Display.LastModifiedByUser','Display last modified by username for parent collection list.', 'false'),
	('Collection.View.ParentCollectionList.Display.LastModifiedOnDateTime','Display last modified on date/time for parent collection list.', 'false'),
	('Collection.View.ParentCollectionList.Display.NumberOfItemsPerPage','Display specified number of items per page for parent collection list.', '25'),
	('Collection.View.ParentCollectionList.Display.OwnerUser','Display owner username for parent collection list.', 'false'),
	('Collection.View.ParentCollectionList.Display.OwnerGroup','Display owner group name for parent collection list.', 'false'),

	('Component.List.Display.CreatedByUser','Display created by username.', 'false'),
	('Component.List.Display.CreatedOnDateTime','Display created on date/time.', 'false'),
	('Component.List.Display.Description','Display component description.', 'true'),
	('Component.List.Display.DocumentationUri','Display documentation URI.', 'true'),
	('Component.List.Display.EstimatedCost','Display estimated cost.', 'false'),
	('Component.List.Display.Id','Display component id.', 'true'),
	('Component.List.Display.LastModifiedByUser','Display last modified by username.', 'false'),
	('Component.List.Display.LastModifiedOnDateTime','Display last modified on date/time.', 'false'),
	('Component.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
	('Component.List.Display.OwnerUser','Display owner username.', 'true'),
	('Component.List.Display.OwnerGroup','Display owner group name.', 'true'),
	('Component.List.Display.State','Display component state.', 'false'),
	('Component.List.Display.Type','Display component type.', 'true'),
	('Component.List.Display.TypeCategory','Display component type category.', 'true'),

	('Component.List.FilterBy.CreatedByUser','Find components that were created by username.', NULL),
	('Component.List.FilterBy.CreatedOnDateTime','Find components that were created on date/time.', NULL),
	('Component.List.FilterBy.Description','Find components by description.', NULL),
	('Component.List.FilterBy.DocumentationUri','Find components by documentation URI.', NULL),
	('Component.List.FilterBy.LastModifiedByUser','Find components that were last modified by username.', NULL),
	('Component.List.FilterBy.LastModifiedOnDateTime','Find components that were last modified on date/time.', NULL),
	('Component.List.FilterBy.EstimatedCost','Find components by estimated cost.', NULL),
	('Component.List.FilterBy.Name','Find components by name.', NULL),
	('Component.List.FilterBy.OwnerUser','Find components by owner username.', NULL),
	('Component.List.FilterBy.OwnerGroup','Find components by owner group name.', NULL),
	('Component.List.FilterBy.State','Find components by state.', NULL),
	('Component.List.FilterBy.Type','Find components by type.', NULL),
	('Component.List.FilterBy.TypeCategory','Find components by type category.', NULL),

	('ComponentType.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('ComponentTypeCategory.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('PropertyType.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('PropertyTypeCategory.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('Source.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('User.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('UserGroup.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25');


UNLOCK TABLES;

