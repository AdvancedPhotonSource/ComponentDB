import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models import PropertyValue, PropertyMetadata, AllowedPropertyValue
from test.cdb_test_base import CdbTestBase


class PropertyTest(CdbTestBase):

    def test_delete_item_property(self):
        self.loginAsAdmin()
        self.propertyValueApi.delete_property_by_id(34)

    def test_update_item_property(self):
        # Try adding status
        status_type = self.propertyTypeApi.get_inventory_status_property_type()

        property_value = PropertyValue(property_type=status_type, value="Unknown")
        failed = False

        self.loginAsAdmin()

        try:
            self.itemApi.add_item_property_value(
                self.INVENTORY_ITEM_ID, property_value=property_value
            )
        except OpenApiException as ex:
            failed = True
        self.assertEqual(failed, True, msg="Adding status should be prevented")

        # Try adding a new property value
        test_property_type = self.propertyTypeApi.get_property_type_by_name(
            self.TEST_PROPERTY_TYPE_NAME
        )
        property_value = PropertyValue(
            property_type=test_property_type, value="Test Val"
        )
        property_value = self.itemApi.add_item_property_value(
            self.INVENTORY_ITEM_ID, property_value=property_value
        )

        self.assertNotEqual(property_value, None, msg="Property value not returned.")

        property_id = property_value.id

        properties = self.itemApi.get_properties_for_item(self.INVENTORY_ITEM_ID)
        found = False
        for property in properties:
            if property_id == property.id:
                found = True
                break

        self.assertEqual(
            found, True, msg="Could not find new property in item properties."
        )

        # Try updating property value
        new_value = "updatedValue"
        property_value.value = new_value
        property_value = self.itemApi.update_item_property_value(
            self.INVENTORY_ITEM_ID, property_value=property_value
        )
        self.assertEqual(
            property_value.value, new_value, msg="Failed to update value of property."
        )

        # Try updating property metadata
        metadata = PropertyMetadata(metadata_key="test", metadata_value="test")
        result = self.itemApi.update_item_property_metadata(
            self.INVENTORY_ITEM_ID, property_id, property_metadata=metadata
        )
        self.assertNotEqual(result, None, msg="No value for updating metadata")

        metadata = self.propertyValueApi.get_property_value_metadata(property_id)
        self.assertNotEqual(metadata, None)

        metadata_dict = {
            "test": "updatedTest",
            "metadataKey1": "1234",
            "metadataKey2": "abcd",
        }

        metadataList = (
            self.propertyValueApi.add_or_update_property_value_metadata_by_map(
                property_id, metadata_dict
            )
        )

        self.assertEqual(
            len(metadataList),
            len(metadata_dict),
            msg="The metadata list was not saved properly.",
        )
        for metadata in metadataList:
            key = metadata.metadata_key
            value = metadata.metadata_value
            expected_value = metadata_dict[key]
            self.assertEqual(
                value,
                expected_value,
                msg="Metadata value does not match expected saved value",
            )

    def test_add_allowed_property_value(self):
        self.loginAsAdmin()
        av = AllowedPropertyValue(
            value="TEST", units="units", description="description", sort_order=1
        )
        result = self.propertyTypeApi.add_allowed_value(
            self.CONTROL_INTERFACE_PROPERTY_TYPE_ID, allowed_property_value=av
        )
        self.assertNotEqual(
            result, None, msg="Failed to return newly created allowed value."
        )

        updatedProp = self.propertyTypeApi.get_property_type_by_id(
            self.CONTROL_INTERFACE_PROPERTY_TYPE_ID
        )
        allowed_values = updatedProp.sorted_allowed_property_value_list

        found = False
        for allowed_value in allowed_values:
            if allowed_value.value == av.value:
                found = True
                break

        self.assertEquals(
            found,
            True,
            msg="The allowed value was not found under the updated property type. ",
        )


if __name__ == "__main__":
    unittest.main()
