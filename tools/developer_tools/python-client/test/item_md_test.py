import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models import (
    NewControlRelationshipInformation,
    NewMachinePlaceholderOptions,
    PromoteMachineElementInformation,
    RepresentingAssemblyElementForMachineInformation,
    UpdateMachineAssignedItemInformation,
    MachineDesignConnectorListObject,
    ControlRelationshipHierarchy,
)
from test.cdb_test_base import CdbTestBase


class ItemMdTest(CdbTestBase):

    def test_machine_api(self):
        items = self.machineDesignApi.get_machine_design_item_list()
        self.assertNotEqual(items, None)

        md_id = items[0].id
        md_item_by_id = self.machineDesignApi.get_machine_design_item_by_id(md_id)
        self.assertNotEqual(md_item_by_id, None)

    def test_md_update_assigned_item(self):
        self.loginAsAdmin()

        updateInfo = UpdateMachineAssignedItemInformation(
            md_item_id=self.MACHINE_DESIGN_ID, assigned_item_id=self.INVENTORY_ITEM_ID
        )
        result = self.machineDesignApi.update_assigned_item(updateInfo)
        self.assertNotEqual(result, None)

        # revert back
        updateInfo = UpdateMachineAssignedItemInformation(
            md_item_id=self.MACHINE_DESIGN_ID, assigned_item_id=self.CATALOG_ITEM_ID
        )
        result = self.machineDesignApi.update_assigned_item(updateInfo)
        self.assertNotEqual(result, None)

    def test_md_create_placeholder(self):
        self.loginAsAdmin()

        options = NewMachinePlaceholderOptions(name="Created From API")

        newMachine = self.machineDesignApi.create_placeholder(
            self.MACHINE_DESIGN_ID, options
        )
        self.assertNotEqual(newMachine, None)

        options = NewMachinePlaceholderOptions(name="Created Child From API")
        newMachine = self.machineDesignApi.create_placeholder(newMachine.id, options)
        self.assertNotEqual(newMachine, None)

    def test_md_move_machine(self):
        self.loginAsAdmin()

        hierarchy = self.itemApi.get_item_hierarchy_by_id(self.MACHINE_DESIGN_PARENT_ID)

        self.assertEqual(
            self.verify_contained_item(hierarchy, self.MACHINE_DESIGN_CHILD_ID),
            False,
            msg="The item was already assigned to machine parent",
        )

        element = self.machineDesignApi.move_machine(
            self.MACHINE_DESIGN_CHILD_ID, self.MACHINE_DESIGN_PARENT_ID
        )

        self.assertNotEqual(element, None, msg="No result given from move machine")

        hierarchy = self.itemApi.get_item_hierarchy_by_id(self.MACHINE_DESIGN_PARENT_ID)

        self.assertEqual(
            self.verify_contained_item(hierarchy, self.MACHINE_DESIGN_CHILD_ID),
            True,
            msg="The move item command failed to move machine to new parent.",
        )

    def test_md_control_api(self):
        self.loginAsAdmin()

        top_level_placeholder_opts = NewMachinePlaceholderOptions(
            name=self.MD_CONTROL_NAME_TOP_LEVEL, project_id=self.PROJECT_ID
        )
        top_level_control_node = self.machineDesignApi.create_control_element(
            top_level_placeholder_opts
        )
        self.assertNotEqual(
            top_level_control_node,
            None,
            msg="No result given from create top level control element",
        )

        top_level_2_placeholder_opts = NewMachinePlaceholderOptions(
            name=self.MD_CONTROL_NAME_TOP_LEVEL_2, project_id=self.PROJECT_ID
        )
        another_top_level_control_node = self.machineDesignApi.create_control_element(
            top_level_2_placeholder_opts
        )
        self.assertNotEqual(
            another_top_level_control_node,
            None,
            msg="No result given from create second top level control element",
        )

        next_level_placeholder_opts = NewMachinePlaceholderOptions(
            name=self.MD_CONTROL_NAME
        )
        control_node = self.machineDesignApi.create_placeholder(
            top_level_control_node.id, next_level_placeholder_opts
        )
        self.assertNotEqual(
            control_node,
            None,
            msg="No result given from create next level control element",
        )

        property_type = (
            self.propertyTypeApi.get_control_interface_to_parent_property_type()
        )
        self.assertNotEqual(
            property_type, None, msg="Failed to fetch control interface property type."
        )
        first_allowed_value = property_type.sorted_allowed_property_value_list[0]
        valid_control_interface = first_allowed_value.value

        # Try to create a control relationship between control elements.
        between_control_element_relationship_opts = NewControlRelationshipInformation(
            controlling_machine_id=control_node.id,
            controlled_machine_id=another_top_level_control_node.id,
            control_interface_to_parent=valid_control_interface,
        )
        failed = False
        try:
            self.machineDesignApi.create_control_relationship(
                between_control_element_relationship_opts
            )
        except OpenApiException as ex:
            failed = True

        self.assertEqual(
            failed,
            True,
            msg="No exception, control element controlled by control element.",
        )

        machine_control_relationship_opts = NewControlRelationshipInformation(
            controlled_machine_id=self.MACHINE_DESIGN_PARENT_ID,
            controlling_machine_id=self.MACHINE_DESIGN_CHILD_ID,
            control_interface_to_parent=valid_control_interface,
        )

        # try create machine relationship before hierarchy
        failed = False
        try:
            self.machineDesignApi.create_control_relationship(
                machine_control_relationship_opts
            )
        except OpenApiException as ex:
            failed = True

        self.assertEqual(
            failed,
            True,
            msg="No exception, Machine controlling machine without connection to control hierarchy.",
        )

        control_relationship_opts = NewControlRelationshipInformation(
            controlled_machine_id=self.MACHINE_DESIGN_CHILD_ID,
            controlling_machine_id=control_node.id,
            control_interface_to_parent=valid_control_interface,
        )
        control_result = self.machineDesignApi.create_control_relationship(
            control_relationship_opts
        )
        self.assertNotEqual(
            control_result,
            None,
            msg="No result given from creation of initial controlled machine.",
        )

        control_result = self.machineDesignApi.create_control_relationship(
            machine_control_relationship_opts
        )
        self.assertNotEqual(
            control_result,
            None,
            msg="No result given from creation of machine controlled by machine.",
        )

        # try invalid type
        invalid_type_relationship_opts = NewControlRelationshipInformation(
            controlled_machine_id=self.MACHINE_DESIGN_ID,
            controlling_machine_id=self.MACHINE_DESIGN_PARENT_ID,
            control_interface_to_parent=self.INVALID_CONTROL_INTERFACE,
        )
        failed = False
        try:
            self.machineDesignApi.create_control_relationship(
                invalid_type_relationship_opts
            )
        except OpenApiException as ex:
            failed = True

        self.assertEqual(
            failed, True, msg="Invalid control interface was entered without exception."
        )

    def test_create_promoted_machine_element(self):
        self.loginAsAdmin()

        newPlaceholderOpts = NewMachinePlaceholderOptions(name="PromotedTest")
        parent_machine = self.machineDesignApi.create_placeholder(
            parent_md_id=self.MACHINE_DESIGN_ID,
            new_machine_placeholder_options=newPlaceholderOpts,
        )

        parent_assembly_id = self.CATALOG_ITEM_ID

        self.machineDesignApi.update_assigned_item(
            UpdateMachineAssignedItemInformation(
                md_item_id=parent_machine.id, assigned_item_id=parent_assembly_id
            )
        )

        hierarchy = self.itemApi.get_item_hierarchy_by_id(parent_assembly_id)

        for i, child in enumerate(hierarchy.child_items):
            name = child.element_name
            name += "  - " + str(i)
            assembly_id = child.element_id

            promote_data = PromoteMachineElementInformation(
                parent_md_item_id=parent_machine.id,
                assembly_element_id=assembly_id,
                new_name=name,
            )

            promoted_machine = (
                self.machineDesignApi.promote_assembly_element_to_machine(promote_data)
            )

            self.assertNotEqual(
                None,
                promoted_machine,
                msg="Promoted machine design has not been returned. ",
            )

    def test_update_representing_assembly_element_for_machine(self):
        # Fetch parent of machine
        housing_hierarchy = self.machineDesignApi.get_housing_hierarchy_by_id(
            self.MACHINE_TO_REPRESENT_ID
        )

        child_item = housing_hierarchy.child_items[0]
        parent_item_id = None

        while child_item is not None:
            if child_item.item.id == self.MACHINE_TO_REPRESENT_ID:
                parent_item_id = housing_hierarchy.item.id
                break
            housing_hierarchy = child_item
            child_item = housing_hierarchy.child_items[0]

        self.assertNotEqual(
            parent_item_id,
            None,
            msg="Parent item id could not be found for representing machine.",
        )

        machine = self.machineDesignApi.get_machine_design_item_by_id(parent_item_id)
        assembly = machine.assigned_item
        assembly_hierarchy = self.itemApi.get_item_hierarchy_by_id(assembly.id)

        element_id = None
        for child in assembly_hierarchy.child_items:
            if child.element_name == self.ELEMENT_NAME_TO_REPRESENT:
                element_id = child.element_id
                break

        self.assertNotEqual(
            element_id,
            None,
            msg="Element id for %s could not be found" % self.ELEMENT_NAME_TO_REPRESENT,
        )

        self.loginAsAdmin()
        info = RepresentingAssemblyElementForMachineInformation(
            machine_id=self.MACHINE_TO_REPRESENT_ID, assembly_element_id=element_id
        )

        rep_machine = (
            self.machineDesignApi.update_representing_assembly_element_for_machine(info)
        )
        self.assertEquals(
            rep_machine.assigned_represented_element.id,
            element_id,
            msg="The represented element was not assigned to item.",
        )

        # Clear represented element.
        info.assembly_element_id = None
        rep_machine = (
            self.machineDesignApi.update_representing_assembly_element_for_machine(info)
        )

        self.assertEquals(
            rep_machine.assigned_represented_element,
            None,
            msg="The reresented assembly element failed to clear.",
        )

    def test_get_control_hierarchy_for_machine(self):
        result: ControlRelationshipHierarchy = (
            self.machineDesignApi.get_control_hierarchy_for_machine_element(
                machine_id=self.MACHINE_DESIGN_ID
            )
        )

        self.assertEqual(
            result.__len__(), 1, msg="One result of control hierarchy should be found."
        )
        result = result[0]
        self.assertEqual(
            result.machine_item.id,
            self.MD_CONTROL_EXPECTED_PARENT_ID,
            msg="Not expected control parent found.",
        )
        self.assertEqual(
            result.child_item.interface_to_parent,
            self.MD_CONTROL_INTERFACE_TO_PARENT,
            msg="Inteface to parent found is not correct.",
        )
        self.assertEqual(
            result.child_item.machine_item.id,
            self.MACHINE_DESIGN_ID,
            msg="Not expected controlled machine found.",
        )

    def test_get_machine_connection_list(self):
        result: list[MachineDesignConnectorListObject] = (
            self.machineDesignApi.get_machine_design_connector_list(
                self.MACHINE_DESIGN_PARENT_ID
            )
        )

        self.assertEqual(len(result), 1)

        connection = result[0]
        self.assertEqual(connection.connected_items[0].id, self.MACHINE_DESIGN_CHILD_ID)
        self.assertEqual(connection.connected_cables[0].id, self.CABLE_DESIGN_ITEM_ID)

    def test_unassign_template_from_machine(self):
        template = self.itemApi.get_created_from_template(
            self.MD_CREATED_FROM_TEMPLATE_ID
        )
        self.assertNotEqual(
            template, None, msg="Template not assigned to machine create from template."
        )

        self.loginAsAdmin()
        self.machineDesignApi.unassign_template_from_machine_element(
            self.MD_CREATED_FROM_TEMPLATE_ID
        )

        try:
            template = self.itemApi.get_created_from_template(
                self.MD_CREATED_FROM_TEMPLATE_ID
            )
        except OpenApiException:
            template = None
        self.assertEqual(template, None, msg="Template was not unassigned sucessfully.")


if __name__ == "__main__":
    unittest.main()
