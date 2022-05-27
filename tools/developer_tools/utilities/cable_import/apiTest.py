import argparse
import sys

import urllib3

from CdbApiFactory import CdbApiFactory
from cdbApi import ApiException, UpdateCableDesignAssignedItemInformation


def main():

    # parse command line args
    parser = argparse.ArgumentParser()
    parser.add_argument("--cdbUrl", help="CDB system URL")
    parser.add_argument("--cdbUser", help="CDB User ID for API login")
    parser.add_argument("--cdbPassword", help="CDB User password for API login")
    args = parser.parse_args()

    print()
    print("COMMAND LINE ARGS ====================")
    print()
    print("cdbUrl: %s" % args.cdbUrl)
    print("cdbUser: %s" % args.cdbUser)
    if args.cdbPassword is not None and len(args.cdbPassword) > 0:
        password_string = "********"
    else:
        password_string = args.cdbPassword
    print("cdbPassword: %s" % password_string)

    # open connection to CDB
    api = CdbApiFactory(args.cdbUrl)
    try:
        api.authenticateUser(args.cdbUser, args.cdbPassword)
        api.testAuthenticated()
    except (ApiException, urllib3.exceptions.MaxRetryError) as exc:
        print("CDB login failed user: %s message: %s, exiting" % (args.cdbUser, exc))
        sys.exit(0)

    print("api connected: %s" % api)

    # request_obj = UpdateCableDesignAssignedItemInformation(cd_item_id=177080)
    # cable_design_item = api.getCableDesignItemApi().clear_cable_type(update_cable_design_assigned_item_information=request_obj)

    # request_obj = UpdateCableDesignAssignedItemInformation(cd_item_id=177081)
    # cable_design_item = api.getCableDesignItemApi().clear_assigned_inventory(update_cable_design_assigned_item_information=request_obj)

    # request_obj = UpdateCableDesignAssignedItemInformation(cd_item_id=177081, cable_type_name="craigmcc pump controller cable")
    # cable_design_item = api.getCableDesignItemApi().update_cable_type_name(update_cable_design_assigned_item_information=request_obj)

    # request_obj = UpdateCableDesignAssignedItemInformation(cd_item_id=177081, cable_type_id=176842)
    # cable_design_item = api.getCableDesignItemApi().update_cable_type_id(update_cable_design_assigned_item_information=request_obj)

    # request_obj = UpdateCableDesignAssignedItemInformation(cd_item_id=177081, inventory_id=176721, is_installed=True)
    # cable_design_item = api.getCableDesignItemApi().update_assigned_inventory_id(update_cable_design_assigned_item_information=request_obj)

    # request_obj = UpdateCableDesignAssignedItemInformation(cd_item_id=177080, cable_type_name="craigmcc pump controller cable", inventory_tag="Unit: 000000002", is_installed=True)
    # cable_design_item = api.getCableDesignItemApi().update_assigned_inventory_name(update_cable_design_assigned_item_information=request_obj)

    request_obj = UpdateCableDesignAssignedItemInformation(cd_item_id=177081, is_installed=False)
    cable_design_item = api.getCableDesignItemApi().update_installation_status(update_cable_design_assigned_item_information=request_obj)

    print(cable_design_item)


if __name__ == '__main__':
    main()