
LOCK TABLES `setting_type` WRITE;

INSERT INTO `setting_type` (`name`, `description`, `default_value`)
VALUES
	('AllowedPropertyValue.List.Display.Description','Display allowed property value description.', 'false'),
	('AllowedPropertyValue.List.Display.Id','Display allowed property value id.', 'true'),
	('AllowedPropertyValue.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),
	('AllowedPropertyValue.List.Display.SortOrder','Display allowed property value sort order.', 'true'),
	('AllowedPropertyValue.List.Display.Units','Display allowed property value units.', 'true'),

	('AllowedPropertyValue.List.FilterBy.Description','Filter for allowed property value description.', NULL),
	('AllowedPropertyValue.List.FilterBy.SortOrder','Filter for allowed property value class sort order.', NULL),
	('AllowedPropertyValue.List.FilterBy.Units','Filter for allowed property value units.', NULL),
	('AllowedPropertyValue.List.FilterBy.Value','Filter for allowed property value.', NULL),

	('Component.List.Display.CreatedByUser', 'Display created by username.', 'false'),
	('Component.List.Display.CreatedOnDateTime', 'Display created on date/time.', 'false'),
	('Component.List.Display.Description', 'Display component description.', 'true'),
	('Component.List.Display.Id', 'Display component id.', 'true'),
	('Component.List.Display.LastModifiedByUser', 'Display last modified by username.', 'false'),
	('Component.List.Display.LastModifiedOnDateTime', 'Display last modified on date/time.', 'false'),
	('Component.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),
	('Component.List.Display.OwnerUser', 'Display owner username.', 'true'),
	('Component.List.Display.OwnerGroup', 'Display owner group name.', 'true'),
	('Component.List.Display.Type', 'Display component type.', 'true'),
	('Component.List.Display.TypeCategory', 'Display component type category.', 'true'),

	('Component.List.FilterBy.CreatedByUser', 'Filter for components that were created by username.', NULL),
	('Component.List.FilterBy.CreatedOnDateTime', 'Filter for components that were created on date/time.', NULL),
	('Component.List.FilterBy.Description', 'Filter for components by description.', NULL),
	('Component.List.FilterBy.LastModifiedByUser', 'Filter for components that were last modified by username.', NULL),
	('Component.List.FilterBy.LastModifiedOnDateTime', 'Filter for components that were last modified on date/time.', NULL),
	('Component.List.FilterBy.Name', 'Filter for components by name.', NULL),
	('Component.List.FilterBy.OwnerUser', 'Filter for components by owner username.', NULL),
	('Component.List.FilterBy.OwnerGroup', 'Filter for components by owner group name.', NULL),
	('Component.List.FilterBy.Type', 'Filter for components by type.', NULL),
	('Component.List.FilterBy.TypeCategory', 'Filter for components by type category.', NULL),

	('ComponentSource.List.Display.Cost', 'Display component cost.', 'true'),
	('ComponentSource.List.Display.Description', 'Display component source description.', 'true'),
	('ComponentSource.List.Display.Id', 'Display component source id.', 'true'),
	('ComponentSource.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),
	('ComponentSource.List.Display.PartNumber', 'Display component part number.', 'true'),

	('ComponentSource.List.FilterBy.Cost', 'Filter for component sources by cost.', NULL),
	('ComponentSource.List.FilterBy.Description', 'Filter for component sources by description.', NULL),
	('ComponentSource.List.FilterBy.PartNumber', 'Filter for component sources by part number.', NULL),
	('ComponentSource.List.FilterBy.SourceName', 'Filter for component sources by name.', NULL),

	('ComponentType.List.Display.Category','Display component type category.', true),
	('ComponentType.List.Display.Description', 'Display component type description.', 'true'),
	('ComponentType.List.Display.Id', 'Display component type id.', 'true'),
	('ComponentType.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),

	('ComponentType.List.FilterBy.Category','Filter for component type category.', NULL),
	('ComponentType.List.FilterBy.Description','Filter for component type description.', NULL),
	('ComponentType.List.FilterBy.Name','Filter for component type name.', NULL),

	('ComponentTypeCategory.List.Display.Description', 'Display component type category description.', 'true'),
	('ComponentTypeCategory.List.Display.Id', 'Display component type category id.', 'true'),
	('ComponentTypeCategory.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),

	('ComponentTypeCategory.List.FilterBy.Description','Filter for component type category description.', NULL),
	('ComponentTypeCategory.List.FilterBy.Name','Filter for component type category name.', NULL),

	('ComponentTypePropertyType.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),
	('ComponentTypePropertyType.List.Display.Id', 'Display component type property type id.', 'true'),
	('ComponentTypePropertyType.List.FilterBy.PropertyTypeName', 'Filter for component type property type name.', NULL),

	('Design.List.Display.CreatedByUser', 'Display created by username.', 'false'),
	('Design.List.Display.CreatedOnDateTime', 'Display created on date/time.', 'false'),
	('Design.List.Display.Description', 'Display design description.', 'true'),
	('Design.List.Display.Id', 'Display design id.', 'true'),
	('Design.List.Display.LastModifiedByUser', 'Display last modified by username.', 'false'),
	('Design.List.Display.LastModifiedOnDateTime', 'Display last modified on date/time.', 'false'),
	('Design.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),
	('Design.List.Display.OwnerUser', 'Display owner username.', 'true'),
	('Design.List.Display.OwnerGroup', 'Display owner group name.', 'true'),

	('Design.List.FilterBy.CreatedByUser', 'Filter for designs that were created by username.', NULL),
	('Design.List.FilterBy.CreatedOnDateTime', 'Filter for designs that were created on date/time.', NULL),
	('Design.List.FilterBy.Description', 'Filter for designs by description.', NULL),
	('Design.List.FilterBy.LastModifiedByUser', 'Filter for designs that were last modified by username.', NULL),
	('Design.List.FilterBy.LastModifiedOnDateTime', 'Filter for designs that were last modified on date/time.', NULL),
	('Design.List.FilterBy.Name', 'Filter for designs by name.', NULL),
	('Design.List.FilterBy.OwnerUser', 'Filter for designs by owner username.', NULL),
	('Design.List.FilterBy.OwnerGroup', 'Filter for designs by owner group name.', NULL),

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

	('Log.List.Display.EnteredOnDateTime','Display log entry entered on date/time.', 'true'),
	('Log.List.Display.EnteredByUser','Display log entry entered by user.', 'true'),
	('Log.List.Display.Id','Display log entry id.', 'true'),
	('Log.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('Log.List.FilterBy.EnteredOnDateTime','Filter for log entry entered on date/time.', NULL),
	('Log.List.FilterBy.EnteredByUser','Filter for log entry entered by user.', NULL),
	('Log.List.FilterBy.Text','Filter for log entry text.', NULL),

	('PropertyType.List.Display.Category','Display property type category.', 'true'),
	('PropertyType.List.Display.DefaultUnits','Display property type default units.', 'false'),
	('PropertyType.List.Display.DefaultValue','Display property type default value.', 'false'),
	('PropertyType.List.Display.Description','Display property type description.', 'true'),
	('PropertyType.List.Display.Handler','Display property type class handler.', 'false'),
	('PropertyType.List.Display.Id','Display property type id.', 'true'),
	('PropertyType.List.Display.IsDynamic','Display dynamic property type designation.', 'false'),
	('PropertyType.List.Display.IsUserWriteable','Display user-writeable property type designation.', 'false'),
	('PropertyType.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('PropertyType.List.FilterBy.Category','Filter for property type category.', NULL),
	('PropertyType.List.FilterBy.DefaultUnits','Filter for property type default units.', NULL),
	('PropertyType.List.FilterBy.DefaultValue','Filter for property type default value.', NULL),
	('PropertyType.List.FilterBy.Description','Filter for property type description.', NULL),
	('PropertyType.List.FilterBy.Handler','Filter for property type class handler.', NULL),
	('PropertyType.List.FilterBy.IsDynamic','Filter for dynamic property type designation.', NULL),
	('PropertyType.List.FilterBy.IsUserWriteable','Filter for user-writeable property type designation.', NULL),
	('PropertyType.List.FilterBy.Name','Filter for property type name.', NULL),

	('PropertyTypeCategory.List.Display.Description','Display property type category description.', 'true'),
	('PropertyTypeCategory.List.Display.Id','Display property type category id.', 'true'),
	('PropertyTypeCategory.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('PropertyTypeCategory.List.FilterBy.Description','Filter for property type category description.', NULL),
	('PropertyTypeCategory.List.FilterBy.Name','Filter for property type category name.', NULL),

	('PropertyTypeHandler.List.Display.Description','Display property type handler description.', 'true'),
	('PropertyTypeHandler.List.Display.Id','Display property type handler id.', 'true'),
	('PropertyTypeHandler.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('PropertyTypeHandler.List.FilterBy.Description','Filter for property type handler description.', NULL),
	('PropertyTypeHandler.List.FilterBy.Name','Filter for property type handler name.', NULL),

	('PropertyValue.List.Display.Description', 'Display value entry description.', 'false'),
	('PropertyValue.List.Display.EnteredOnDateTime','Display value entry entered on date/time.', 'false'),
	('PropertyValue.List.Display.EnteredByUser','Display value entry entered by user.', 'false'),
	('PropertyValue.List.Display.Id','Display property value entry id.', 'true'),
	('PropertyValue.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),
	('PropertyValue.List.Display.Tag', 'Display property value tag.', 'true'),
	('PropertyValue.List.Display.TypeCategory', 'Display property value type category.', 'false'),
	('PropertyValue.List.Display.Units', 'Display value units.', 'true'),

	('PropertyValue.List.FilterBy.Description','Filter for value description.', NULL),
	('PropertyValue.List.FilterBy.EnteredOnDateTime','Filter for value entry entered on date/time.', NULL),
	('PropertyValue.List.FilterBy.EnteredByUser','Filter for value entry entered by user.', NULL),
	('PropertyValue.List.FilterBy.Tag', 'Filter for property value tag.', 'true'),
	('PropertyValue.List.FilterBy.Type', 'Filter for property value type.', NULL),
	('PropertyValue.List.FilterBy.TypeCategory', 'Filter for property value type category.', NULL),
	('PropertyValue.List.FilterBy.Units', 'Filter for value units.', NULL),
	('PropertyValue.List.FilterBy.Value','Filter for value entry.', NULL),

	('PropertyValueHistory.List.Display.Description', 'Display value entry description.', 'true'),
	('PropertyValueHistory.List.Display.EnteredOnDateTime','Display value entry entered on date/time.', 'true'),
	('PropertyValueHistory.List.Display.EnteredByUser','Display value entry entered by user.', 'true'),
	('PropertyValueHistory.List.Display.Id','Display property value entry id.', 'false'),
	('PropertyValueHistory.List.Display.NumberOfItemsPerPage', 'Display specified number of items per page.', '25'),
	('PropertyValueHistory.List.Display.Tag', 'Display value tag.', 'true'),
	('PropertyValueHistory.List.Display.Units', 'Display value units.', 'true'),

	('PropertyValueHistory.List.FilterBy.Description','Filter for value description.', NULL),
	('PropertyValueHistory.List.FilterBy.EnteredOnDateTime','Filter for value entry entered on date/time.', NULL),
	('PropertyValueHistory.List.FilterBy.EnteredByUser','Filter for value entry entered by user.', NULL),
	('PropertyValueHistory.List.FilterBy.Tag', 'Filter for value tag.', 'true'),
	('PropertyValueHistory.List.FilterBy.Units', 'Filter for value units.', NULL),
	('PropertyValueHistory.List.FilterBy.Value','Filter for value entry.', NULL),

	('Search.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('Source.List.Display.Description','Display source description.', 'true'),
	('Source.List.Display.Id','Display source id.', 'true'),
	('Source.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('Source.List.FilterBy.Description','Filter for source description.', NULL),
	('Source.List.FilterBy.Name','Filter for source name.', NULL),

	('UserInfo.List.Display.Description','Display user description.', 'false'),
	('UserInfo.List.Display.Email','Display user email.', 'true'),
	('UserInfo.List.Display.FirstName','Display user first name.', 'true'),
	('UserInfo.List.Display.Id','Display user id.', 'true'),
	('UserInfo.List.Display.Groups','Display user groups.', 'true'),
	('UserInfo.List.Display.LastName','Display user last name.', 'true'),
	('UserInfo.List.Display.MiddleName','Display user middle name.', 'false'),
	('UserInfo.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('UserInfo.List.FilterBy.Description','Filter for user description.', NULL),
	('UserInfo.List.FilterBy.Email','Filter for user email.', NULL),
	('UserInfo.List.FilterBy.FirstName','Filter for user first name.', NULL),
	('UserInfo.List.FilterBy.Groups','Filter for user groups.', NULL),
	('UserInfo.List.FilterBy.LastName','Filter for user last name.', NULL),
	('UserInfo.List.FilterBy.MiddleName','Filter for user middle name.', NULL),
	('UserInfo.List.FilterBy.Username','Filter for username.', NULL),

	('UserGroup.List.Display.Description','Display user group description.', 'true'),
	('UserGroup.List.Display.Id','Display user group id.', 'true'),
	('UserGroup.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25'),

	('UserGroup.List.FilterBy.Description','Filter for user group description.', NULL),
	('UserGroup.List.FilterBy.Name','Filter for user group name.', NULL),

	('UserSetting.List.Display.NumberOfItemsPerPage','Display specified number of items per page.', '25');


UNLOCK TABLES;

