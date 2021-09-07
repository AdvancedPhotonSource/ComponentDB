import csv
import json

from CdbApiFactory import CdbApiFactory
from cdbApi import NewMachinePlaceholderOptions, NewControlRelationshipInformation, ApiException, \
    NewRunningRelationshipInformation

INTERFACE_KEY = "Interface"
LEVEL_PREFIX_KEY = "Level "
IS_CONTROL_ELEMENT_KEY = "Control Element"
RUNNING_ON_KEY = "Running on"
ITEM_PROJECT_KEY = "Item Project"

ITEM_REC_NAME_KEY = "name"
ITEM_REC_IS_CONTROL_KEY = "is_control"
ITEM_REC_RUNNING_ON_KEY = "running_on"

PRINT_INDENT = "------"

# Script configuration
csv_file_path = './test_input.csv'
cdb_url = "http://localhost:8080/cdb"
cdb_user = 'cdb'
cdb_pass = 'cdb'

cdb_api = CdbApiFactory(cdb_url)
cdb_api.authenticateUser(cdb_user, cdb_pass)

machine_api = cdb_api.getMachineDesignItemApi()
result_dict = {}


def get_id_for_item(item_rec=None, item_name=None):
    if item_rec is not None:
        item_name = item_rec[ITEM_REC_NAME_KEY]

    if item_name is None:
        print("no name provided")
        return

    parent_result_id = result_dict.get(item_name)

    if parent_result_id is None:
        try:
            parent_machine = machine_api.get_machine_design_items_by_name(item_name)
            parent_result_id = parent_machine[0].id
        except Exception:
            if item_rec is not None:
                is_control = item_rec[ITEM_REC_IS_CONTROL_KEY]
            else:
                is_control = False

            if is_control:
                opts = NewMachinePlaceholderOptions(name=item_name, project_id=1)
                parent_machine = machine_api.create_control_element(opts)
                parent_result_id = parent_machine.id
            else:
                raise Exception("Could not find item with name: " + item_name)

        result_dict[item_name] = parent_result_id

    return parent_result_id


def add_running_on_relationship(item_rec):
    if not item_rec[ITEM_REC_IS_CONTROL_KEY]:
        return

    running_on = item_rec[ITEM_REC_RUNNING_ON_KEY]
    if running_on != '':
        running_on_id = get_id_for_item(item_name=running_on)
        control_id = get_id_for_item(item_rec=item_rec)

    running_relationship = NewRunningRelationshipInformation(
        machine_design_id=running_on_id,
        control_type_machine_id=control_id
    )
    machine_api.create_running_on_relationship(running_relationship)


with open(csv_file_path) as csv_file:
    control_hierarchy_data = csv.DictReader(csv_file)

    parents = []

    for row in control_hierarchy_data:
        try_index = len(parents)
        while try_index > -1:
            next_node_key = LEVEL_PREFIX_KEY + str(try_index)
            item_name = None
            if next_node_key in row.keys():
                item_name = row[next_node_key]
            if item_name is not None and item_name != '':
                item_is_control = row[IS_CONTROL_ELEMENT_KEY] != ''
                item_rec = {ITEM_REC_NAME_KEY: item_name, ITEM_REC_IS_CONTROL_KEY: item_is_control}

                if item_is_control:
                    running_on = row[RUNNING_ON_KEY]
                    item_rec[ITEM_REC_RUNNING_ON_KEY] = running_on

                parents = parents[:try_index]
                parents.append(item_rec)
                break
            else:
                try_index -= 1

        try:
            if len(parents) < 2:
                if len(parents) == 1:
                    parent = parents[0]
                    get_id_for_item(item_rec=parent)
                    add_running_on_relationship(parent)
                continue

            parent = parents[-2]
            child = parents[-1]
            parent_name = parent[ITEM_REC_NAME_KEY]
            child_name = child[ITEM_REC_NAME_KEY]

            print(PRINT_INDENT * (try_index - 1) + parent_name)
            print(PRINT_INDENT * try_index + child_name)

            interface_name = row[INTERFACE_KEY]

            parent_id = get_id_for_item(item_rec=parent)

            if parent[ITEM_REC_IS_CONTROL_KEY] and child[ITEM_REC_IS_CONTROL_KEY]:
                opts = NewMachinePlaceholderOptions(name=child_name, project_id=1)
                machine_api.create_placeholder(parent_id, opts)
                continue
            else:
                child_id = get_id_for_item(item_rec=child)

            add_running_on_relationship(parent)
            add_running_on_relationship(child)
        except Exception as ex:
            print(ex)
            continue

        try:
            opts = NewControlRelationshipInformation(controlled_machine_id=child_id,
                                                     controlling_machine_id=parent_id,
                                                     control_interface_to_parent=interface_name)
            machine_api.create_control_relationship(opts)
        except ApiException as ex:
            exception_body = ex.body
            exception_body = json.loads(exception_body)
            print(exception_body['message'])

