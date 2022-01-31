import csv
import json

from CdbApiFactory import CdbApiFactory
from boto.dynamodb.item import Item
from cdbApi import NewMachinePlaceholderOptions, NewControlRelationshipInformation, ApiException
from cdbApi.models.item_permissions import ItemPermissions
from pkg_resources import empty_provider

INTERFACE_KEY = "Interface"
LEVEL_PREFIX_KEY = "Level "
CONTROL_GROUP_NAME_KEY = "Control Group"
MACHINE_PARENT_NAME_KEY = "Parent Item"
ITEM_PROJECT_KEY = "Project"
ITEM_OWNER_GROUP_KEY = "Owner Group"
ITEM_OWNER_USER_KEY = "Owner User"

ITEM_REC_NAME_KEY = "name"
ITEM_REC_CONTROL_GROUP_NAME_KEY = "control_group"
ITEM_REC_MACHINE_PARENT_NAME_KEY = "machine_parent_name"
ITEM_REC_ROW_KEY = "Row"

PRINT_INDENT = "------"

# Script configuration
csv_file_path = './test_input.csv'
cdb_url = "http://localhost:8080/cdb"
cdb_user = 'cdb'
cdb_pass = 'cdb'

cdb_api = CdbApiFactory(cdb_url)
cdb_api.authenticateUser(cdb_user, cdb_pass)

machine_api = cdb_api.getMachineDesignItemApi()
item_api = cdb_api.getItemApi()
user_api = cdb_api.getUsersApi()
result_dict = {}


def get_id_for_item(item_name, create_control_group=False, row=None):
    if item_name.startswith("#"):
        item_name = item_name[1:]
    else:
        if item_name.isnumeric():
            return int(item_name)

    parent_result_id = result_dict.get(item_name)

    if parent_result_id is None:
        try:
            parent_machine = machine_api.get_machine_design_items_by_name(item_name)
            parent_result_id = parent_machine[0].id
        except Exception:
            if create_control_group:
                project = row[ITEM_PROJECT_KEY]
                project_id = -1
                if project.startswith("#"):
                    # Find by name
                    project = project[1:]
                    item_project_list = item_api.get_item_project_list()
                    for item_project in item_project_list:
                        if item_project.name == project:
                            project_id = item_project.id
                else:
                    project_id = project

                if project_id == -1:
                    raise Exception("Could not find project specified.")

                opts = NewMachinePlaceholderOptions(name=item_name, project_id=project_id)
                parent_machine = machine_api.create_control_element(opts)
                parent_result_id = parent_machine.id

                owner_group = row[ITEM_OWNER_GROUP_KEY]
                owner_user = row[ITEM_OWNER_USER_KEY]

                if owner_user != '' or owner_group != '':
                    new_permissions = ItemPermissions(item_id=parent_result_id)

                    if owner_user != '':
                        owner_user_obj = None
                        if owner_user.startswith("#"):
                            owner_user_obj = user_api.get_user_by_username(owner_user[1:])
                        else:
                            users = user_api.get_all1()
                            for user in users:
                                if user.id == owner_user:
                                    owner_user_obj = user
                                    break
                        if owner_user_obj is None:
                            raise Exception("Cannot find user: " + owner_user)
                        new_permissions.owner_user = owner_user_obj

                    if owner_group != '':
                        owner_group_obj = None
                        if owner_group.startswith("#"):
                            owner_group_obj = user_api.get_group_by_name(owner_group[1:])
                        else:
                            groups = user_api.get_all_groups()
                            for group in groups:
                                if group.id == owner_group:
                                    owner_group_obj = group
                                    break
                        if owner_group_obj is None:
                            raise Exception("Cannot find group: " + owner_group)
                        new_permissions.owner_group = owner_group_obj

                    item_api.update_item_permission(item_permissions=new_permissions)
            else:
                raise Exception("Could not find item with name: " + item_name)

        result_dict[item_name] = parent_result_id

    return parent_result_id


def process_item_rec(parent_rec, try_index, interface_name, child_rec=None):
    parent_machine_parent = parent_rec[ITEM_REC_MACHINE_PARENT_NAME_KEY]
    parent_control_group = parent_rec[ITEM_REC_CONTROL_GROUP_NAME_KEY]
    parent_name = parent_rec[ITEM_REC_NAME_KEY]

    try:
        try:
            parent_id = get_id_for_item(parent_name)
        except Exception as ex:
            parent_id = None

        print(PRINT_INDENT * (try_index - 1) + parent_name)

        if parent_id is None or child_rec is None:
            control_group_id = get_id_for_item(parent_control_group, create_control_group=True, row=parent_rec[ITEM_REC_ROW_KEY])
            if parent_machine_parent is None:
                print("Error with the record for " + item_name)
                return
            if parent_id is None:
                parent_machine_id = get_id_for_item(parent_machine_parent)
                opts = NewMachinePlaceholderOptions(name=parent_name, project_id=1)
                parent_item = machine_api.create_placeholder(parent_md_id=parent_machine_id, new_machine_placeholder_options=opts)
                parent_id = parent_item.id

            opts = NewControlRelationshipInformation(controlled_machine_id=parent_id,
                                                     controlling_machine_id=control_group_id,
                                                     control_interface_to_parent=interface_name)
            machine_api.create_control_relationship(opts)
        if child_rec is not None:
            child_name = child_rec[ITEM_REC_NAME_KEY]

            print(PRINT_INDENT * try_index + child_name)

            child_id = get_id_for_item(child_name)

            opts = NewControlRelationshipInformation(controlled_machine_id=child_id,
                                                     controlling_machine_id=parent_id,
                                                     control_interface_to_parent=interface_name)
            machine_api.create_control_relationship(opts)
    except Exception as ex:
        print(ex)
    except ApiException as ex:
        exception_body = ex.body
        exception_body = json.loads(exception_body)
        print(exception_body['message'])


with open(csv_file_path) as csv_file:
    control_hierarchy_data = csv.DictReader(csv_file)

    parents = []

    for row in control_hierarchy_data:
        if row[MACHINE_PARENT_NAME_KEY].startswith("//"):
            continue

        empty = True
        for data_key in row.keys():
            if row[data_key] != '':
                empty = False
                break
        if empty:
            continue

        try_index = len(parents) + 1
        while try_index > 0:
            next_node_key = LEVEL_PREFIX_KEY + str(try_index)
            item_name = None
            if next_node_key in row.keys():
                item_name = row[next_node_key]
            if item_name is not None and item_name != '':
                control_group_name = row[CONTROL_GROUP_NAME_KEY]
                machine_parent_name = row[MACHINE_PARENT_NAME_KEY]
                item_rec = {ITEM_REC_NAME_KEY: item_name, ITEM_REC_CONTROL_GROUP_NAME_KEY: control_group_name,
                            ITEM_REC_MACHINE_PARENT_NAME_KEY: machine_parent_name, ITEM_REC_ROW_KEY: row}

                parents = parents[:try_index - 1]
                parents.append(item_rec)
                break
            else:
                try_index -= 1

        interface_name = row[INTERFACE_KEY]

        if len(parents) < 2:
            if len(parents) == 1:
                parent = parents[0]
                process_item_rec(parent, try_index, interface_name)
            continue

        parent = parents[-2]
        child = parents[-1]

        process_item_rec(parent, try_index, interface_name, child)


