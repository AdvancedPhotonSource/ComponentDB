import pandas as pd
import yaml

from CdbApiFactory import CdbApiFactory
from cdbApi import NewMachinePlaceholderOptions, NewControlRelationshipInformation, ApiException
from cdbApi.models.item_permissions import ItemPermissions

INTERFACE_KEY = "Interface"
LEVEL_PREFIX_KEY = "Level "
CONTROL_GROUP_NAME_KEY = "Control Group"
MACHINE_PARENT_NAME_KEY = "Parent Item"
ITEM_PROJECT_KEY = "Project"
ITEM_OWNER_GROUP_KEY = "Owner Group"
ITEM_OWNER_USER_KEY = "Owner User"
GLOBAL_RELATIONSHIP_KEY = "Global Relationship"

ITEM_REC_NAME_KEY = "name"
ITEM_REC_CONTROL_GROUP_NAME_KEY = "control_group"
ITEM_REC_MACHINE_PARENT_NAME_KEY = "machine_parent_name"
ITEM_REC_ROW_KEY = "Row"
ITEM_REC_GLOBAL = "Global"

CONTROL_ENTITY_TYPE_ID = 9

PRINT_INDENT = "------"

# Read YAML file
with open("config.yaml", 'r') as stream:
    config_yaml = yaml.safe_load(stream)

# Script configuration
xlsx_file_path = config_yaml['data_source']['xlsx_path']
sheet_list = config_yaml['data_source']['sheet_list']
multiple_match_top_machine_name = config_yaml['data_source']['multiple_match_top_level_name']
cdb_url = config_yaml['cdb']['url']
cdb_user = config_yaml['cdb']['user']
cdb_pass = config_yaml['cdb']['password']
error_file_path = config_yaml['error_file_path']

cdb_api = CdbApiFactory(cdb_url)
cdb_api.authenticateUser(cdb_user, cdb_pass)

machine_api = cdb_api.getMachineDesignItemApi()
item_api = cdb_api.getItemApi()
user_api = cdb_api.getUsersApi()
result_dict = {}

def append_error_log(error_message):
    with open(error_file_path, 'a') as file:
        file.write(error_message + "\n")
    
def isNaN(num):
    return num!= num

def convert_sheet_to_dict(df):
    columns = df.columns
    data_list = []

    nan = float("nan")

    for row in range(df.shape[0]): 
        row_data = {}
        for col in range(df.shape[1]):

            key = columns.values[col]
            value = df.iat[row,col]
            if isNaN(value):
                value = ''
            row_data[key] = value

        data_list.append(row_data)
    return data_list

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
            
            if create_control_group:
                for parent in parent_machine:
                    if parent.entity_type_list: 
                        entity_type = parent.entity_type_list[0]
                        if entity_type.id == CONTROL_ENTITY_TYPE_ID:
                           parent_result_id = parent.id 
                           break

                if parent_result_id is None:
                    raise Exception("No Control machine found.")
            else:
                if len(parent_machine) > 1:
                    result_machine = None
                    for parent in parent_machine:
                        hierarchy = machine_api.get_housing_hierarchy_by_id(parent.id)
                        if hierarchy is not None:
                            top_level_item = hierarchy.item
                            if top_level_item.name == multiple_match_top_machine_name:
                                result_machine = parent
                                break

                    if result_machine:
                        parent_result_id = result_machine.id
                    else: 
                        raise Exception("None of the results match the top level parent.")
                else:            
                    parent_result_id = parent_machine[0].id                            
        except Exception as ex:
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
                ex_message=""
                if type(ex) == ApiException:
                    ex_obj = cdb_api.parseApiException(ex)
                    ex_message = ex_obj.message
                else:
                    ex_message = ex.__str__()
                raise Exception("Error fetching name: %s (%s)" % (item_name, ex_message))

        result_dict[item_name] = parent_result_id

    return parent_result_id


def process_item_rec(parent_rec, try_index, interface_name, child_rec=None, row_num=None, linked_parent_machine=None):
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

            if linked_machine_parent is not None:
                # Add linked machine parent information. 
                linked_parent_name = linked_parent_machine[ITEM_REC_NAME_KEY]
                linked_parent_id = get_id_for_item(linked_parent_name)
                opts.linked_parent_machine_id = linked_parent_id
            machine_api.create_control_relationship(opts)
    except ApiException as ex:
        exception_body = cdb_api.parseApiException(ex)        
        message = "Error processing line %d: %s" % (row_num, exception_body.message)
        append_error_log(message)
        print(message)

    except Exception as ex:
        ex_msg = ex
        if hasattr(ex, 'args') and len(ex.args):
            ex_msg = ex.args
        msg = "Error processing line %d: %s" % (row_num, str(ex_msg))
        append_error_log(msg)
        print(msg)

with open(xlsx_file_path, 'rb') as xlsx_file:
    # Ensure that all sheet names are valid
    for sheet in sheet_list:
        pd.read_excel(xlsx_file, sheet_name=sheet)

    for sheet in sheet_list:
        start_message = "Processing: %s" % sheet
        append_error_log(start_message)
        print(start_message)

        df = pd.read_excel(xlsx_file, sheet_name=sheet)
        control_hierarchy_data = convert_sheet_to_dict(df)

        parents = []

        for i, row in enumerate(control_hierarchy_data):
            # Skip index 0 and header row 
            row_num = i+2

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
                    global_relationship = row[GLOBAL_RELATIONSHIP_KEY]
                    global_relationship = global_relationship
                    
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
                    process_item_rec(parent, try_index, interface_name, row_num=row_num)
                continue

            parent = parents[-2]
            child = parents[-1]
            linked_machine_parent = None

            if (len(parents) >= 3) and not global_relationship:
                linked_machine_parent = parents[-3]

            process_item_rec(parent, try_index, interface_name, child, row_num=row_num, linked_parent_machine=linked_machine_parent)

        end_message = "Finished: %s" % sheet
        append_error_log(end_message)
        print(end_message)

# Add new line after complete.
append_error_log("Done\n")
