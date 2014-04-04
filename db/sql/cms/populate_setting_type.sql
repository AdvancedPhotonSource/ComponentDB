
LOCK TABLES `setting_type` WRITE;

INSERT INTO `setting_type` (`name`, `description`, `default_value`)
VALUES
	('Component.List.Show.ComponentName','Show component name.', 'true'),
	('Component.List.Show.DocumentationUri','Show documentation URI.', 'true'),
	('Component.List.Show.ComponentState','Show component state.', 'true'),
	('Component.List.Show.ComponentType','Show component type.', 'true'),
	('Component.List.Show.OwnerUsername','Show owner username.', 'true'),
	('Component.List.Show.OwnerGroupName','Show owner group name.', 'true'),
	('Component.List.Show.CreatedByUsername','Show created by username.', 'true'),
	('Component.List.Show.CreatedOnDateTime','Show created on date/time.', 'true'),
	('Component.List.Show.LastModifiedByUsername','Show last modified by username.', 'true'),
	('Component.List.Show.LastModifiedOnDateTime','Show last modified on date/time.', 'true'),
	('Component.List.ShowEstimatedCost','Show estimated cost.', 'true'),
	('Component.List.FilterBy.ComponentName','Find components by component name.', NULL),
	('Component.List.FilterBy.DocumentationUri','Find components by documentation URI.', NULL),
	('Component.List.FilterBy.ComponentState','Find components by component state.', NULL),
	('Component.List.FilterBy.ComponentType','Find components by component type.', NULL),
	('Component.List.FilterBy.OwnerUsername','Find components by owner username.', NULL),
	('Component.List.FilterBy.OwnerGroupName','Find components by owner group name.', NULL),
	('Component.List.FilterBy.CreatedByUsername','Find components that were created by username.', NULL),
	('Component.List.FilterBy.CreatedOnDateTime','Find components that were created on date/time.', NULL),
	('Component.List.FilterBy.LastModifiedByUsername','Find components that were last modified by username.', NULL),
	('Component.List.FilterBy.LastModifiedOnDateTime','Find components that were last modified on date/time.', NULL),
	('Component.List.FilterBy.EstimatedCost','Find components by estimated cost.', NULL);

UNLOCK TABLES;

