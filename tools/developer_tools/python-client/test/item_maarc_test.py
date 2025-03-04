import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models import NewMAARCInformation
from test.cdb_test_base import CdbTestBase


class ItemMAARCTest(CdbTestBase):

    def test_create_maarc_item(self):
        new_maarc_top_level_information = NewMAARCInformation()

        project_list = self.itemApi.get_item_project_list()
        domain = self.domainApi.get_domain_by_name(self.MAARC_DOMAIN_NAME)
        entityTypeList = self.domainApi.get_allowed_entity_type_list(domain.id)
        new_maarc_top_level_information.name = "Top Level MAARC"
        new_maarc_top_level_information.item_projects_list = project_list
        new_maarc_top_level_information.entity_type_list = [entityTypeList[0]]

        self.loginAsAdmin()
        parent = self.maarcApi.create(new_maarc_top_level_information)
        self.assertNotEqual(
            parent.id, None, msg="Top level item not created successfully"
        )

        new_maarc_child_information = new_maarc_top_level_information
        new_maarc_child_information.name = "child maarc item"
        new_maarc_child_information.parent_item_id = parent.id

        child = self.maarcApi.create(new_maarc_child_information)
        self.assertNotEqual(parent.id, None, msg="child Item not created successfully")

        # Fetch and test hierarchy
        hierarchy = self.itemApi.get_item_hierarchy_by_id(parent.id)
        hierarchy_child_item = hierarchy.child_items[0].item
        self.assertEqual(
            hierarchy_child_item.id,
            child.id,
            msg="Hierarchy fetched does not match what was created",
        )

    def test_create_maarc_relationship(self):
        maarc_relationships = self.maarcApi.get_maarc_connection_relationship_list(
            self.MAARC_ITEM_ID
        )
        count_relationships = len(maarc_relationships)
        inital_count = 0

        self.assertEqual(
            count_relationships,
            inital_count,
            msg="Relationships to maarc item already exist.",
        )

        self.loginAsAdmin()
        self.maarcApi.create_maarc_connection_relationship(
            self.MAARC_ITEM_ID, self.INVENTORY_ITEM_ID
        )

        maarc_relationships = self.maarcApi.get_maarc_connection_relationship_list(
            self.MAARC_ITEM_ID
        )
        count_relationships = len(maarc_relationships)

        self.assertEqual(
            count_relationships,
            inital_count + 1,
            msg="New maarc relationship not found.",
        )
        relevant_relationship = None

        for relationship in maarc_relationships:
            if relationship.first_item.id == self.INVENTORY_ITEM_ID:
                relevant_relationship = relationship
                break

        self.assertNotEqual(
            relevant_relationship, None, msg="Could not find newly added relationship."
        )



if __name__ == "__main__":
    unittest.main()
