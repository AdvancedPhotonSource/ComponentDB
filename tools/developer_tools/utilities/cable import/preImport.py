#
# This file contains a pre-import framework for various CDB object types.  It supports reading an input spreadsheet
# file, and then creating an output spreadsheet file that contains the standard columns for CDB import.  It is intended
# to support the cable data collection process, which uses an Excel workbook to collect cable type and cable data from
# users.  The pre-import process takes that data collection workbook, and augments/formats it for import to CDB.
#
# The framework initially supports pre-import data transformation for 3 types of objects: sources, cable types, and
# cable designs.  To add support for a new type of object, you must provide concrete subclass implementations of the
# framework's PreImportHelper and OutputObject bases classes.  For an example, see the classes "SourceHelper" and
# SourceOutputObject, which together support the pre-import transformation of CDB "Source" objects.
#
# The framework opens the input spreadsheet and processes it row by row.  A python dictionary is created for each
# input row.  The helper class validates and filters the input rows.  The framework collects a list of "OutputObject"
# instances, and then iterates through them one by one, writing a row for each to the output spreadsheet.
#
# The roles of the framework classes that must be extended to add pre-import support for a new object type are as
# follows.
#
# The PreImportHelper subclass specifies the input spreadsheet columns via the input_column_list() method,
# including the column index and dictionary key name to use for each.  Not all input columns need be mapped,
# only those that are used to generate the values in the output objects.  Column labels are not needed in the input
# column specifications since we don't use them.  It also specifies the columns for the output spreadsheet via the
# output_column_list() method, including for each a column index, label, and getter method name on the output object
# to extract the column value.  It specifies the number of columns in the input spreadsheet (num_input_columns()) and
# the number of columns in the output spreadsheet (num_output_columns()).  It implements get_output_object() which
# takes the dictionary of values for a row from the input spreadsheet and produces an instance of the OutputObject
# subclass that will be used to generate a row in the output spreadsheet.  Finally, the tag() function returns the
# value that identifies the pre-import object type, e.g., "Source".  This value must match the "--type" command line
# option.
#
# The OutputObject subclass's primary responsibility is to transform the data read from an input spreadsheet row into
# the values needed for the corresponding row in the output spreadsheet.  Sometimes the output value is simply a copy of
# the input value, but in other cases, we might perform a CDB query API lookup to transform a name from the input file
# to the corresponding database identifier for that name in the output file.  Each getter method in the OutputObject
# corresponds to a column in the output spreadsheet.  It is these getter methods that perform the required data
# transformation, such as API query.  The column specifications in the helper's output_column_list() map the column
# index in the output spreadsheet to the appropriate getter method on the output object.

# Input spreadsheet format assumptions:
#   * contains a single worksheet
#   * contains 2 or more rows, header is on row 1, data starts on row 2
#   * there are no empty rows within the data


import argparse
import logging
import sys
from abc import ABC, abstractmethod

import xlrd
import xlsxwriter

from CdbApiFactory import CdbApiFactory
from cdbApi import ApiException


# constants

CABLE_TYPE_NAME_KEY = "name"
CABLE_TYPE_DESCRIPTION_KEY = "description"
CABLE_TYPE_LINK_URL_KEY = "linkUrl"
CABLE_TYPE_IMAGE_URL_KEY = "imageUrl"
CABLE_TYPE_MANUFACTURER_KEY = "manufacturer"
CABLE_TYPE_PART_NUMBER_KEY = "partNumber"
CABLE_TYPE_ALT_PART_NUMBER_KEY = "altPartNumber"
CABLE_TYPE_DIAMETER_KEY = "diameter"
CABLE_TYPE_WEIGHT_KEY = "weight"
CABLE_TYPE_CONDUCTORS_KEY = "conductors"
CABLE_TYPE_INSULATION_KEY = "insulation"
CABLE_TYPE_JACKET_COLOR_KEY = "jacketColor"
CABLE_TYPE_VOLTAGE_RATING_KEY = "voltageRating"
CABLE_TYPE_FIRE_LOAD_KEY = "fireLoad"
CABLE_TYPE_HEAT_LIMIT_KEY = "heatLimit"
CABLE_TYPE_BEND_RADIUS_KEY = "bendRadius"
CABLE_TYPE_RAD_TOLERANCE_KEY = "radTolerance"

CABLE_DESIGN_NAME_KEY = "name"
CABLE_DESIGN_LAYING_KEY = "laying"
CABLE_DESIGN_VOLTAGE_KEY = "voltage"
CABLE_DESIGN_OWNER_KEY = "owner"
CABLE_DESIGN_TYPE_KEY = "type"
CABLE_DESIGN_SRC_LOCATION_KEY = "srcLocation"
CABLE_DESIGN_SRC_ANS_KEY = "srcANS"
CABLE_DESIGN_SRC_ETPM_KEY = "srcETPM"
CABLE_DESIGN_SRC_ADDRESS_KEY = "srcAddress"
CABLE_DESIGN_SRC_DESCRIPTION_KEY = "srcDescription"
CABLE_DESIGN_DEST_LOCATION_KEY = "destLocation"
CABLE_DESIGN_DEST_ANS_KEY = "destANS"
CABLE_DESIGN_DEST_ETPM_KEY = "destETPM"
CABLE_DESIGN_DEST_ADDRESS_KEY = "destAddress"
CABLE_DESIGN_DEST_DESCRIPTION_KEY = "destDescription"

def register(helper_class):
    PreImportHelper.register(helper_class.tag(), helper_class)


class PreImportHelper(ABC):

    helperDict = {}

    def __init__(self):
        self.input_columns = {}
        self.initialize_input_columns()
        self.output_columns = {}
        self.initialize_output_columns()
        self.args = None
        self.api = None

    # registers helper concrete classes for lookup by tag
    @classmethod
    def register(cls, tag, the_class):
        cls.helperDict[tag] = the_class

    # returns helper class for specified tag
    @classmethod
    def get_helper_class(cls, tag):
        return cls.helperDict[tag]

    # checks if specified tag is valid
    @classmethod
    def is_valid_type(cls, tag):
        return tag in cls.helperDict

    # creates instance of class with specified tag
    @classmethod
    def create_helper(cls, tag):
        helper_class = cls.helperDict[tag]
        helper_instance = helper_class()
        return helper_instance

    @classmethod
    def num_output_cols(cls):
        return len(cls.output_column_list())

    # Allows subclasses to add command line parser args.  Default behavior is to do nothing.
    # e.g., "parser.add_argument("--cdbUser", help="CDB User ID for API login", required=True)"
    @staticmethod
    def add_parser_args(parser):
        pass

    # Returns registered tag for subclass.
    @staticmethod
    @abstractmethod  # must be innermost decorator
    def tag():
        pass

    # Returns expected number of columns in input spreadsheet.
    @classmethod
    @abstractmethod
    def num_input_cols(cls):
        pass

    # Returns list of column models for input spreadsheet.  Not all columns need be mapped, only the ones we wish to
    # read values from.
    @staticmethod
    @abstractmethod
    def input_column_list():
        pass

    # Returns list of column models for output spreadsheet.  Not all columns need be mapped, only the ones we wish to
    # write values to.
    @staticmethod
    @abstractmethod
    def output_column_list():
        pass

    # Returns an output object for the specified input object, or None if the input object is duplicate.
    @abstractmethod
    def get_output_object(self, input_dict):
        pass

    def set_args(self, args):
        self.args = args

    def get_args(self):
        return self.args

    def set_api(self, api):
        self.api = api

    # Builds dictionary whose keys are column index and value is column model object.
    def initialize_input_columns(self):
        for col in self.input_column_list():
            self.input_columns[col.index] = col

    # Builds dictionary whose keys are column index and value is column model object.
    def initialize_output_columns(self):
        for col in self.output_column_list():
            self.output_columns[col.index] = col

    # Handles cell value from input spreadsheet at specified column index for supplied input object.
    def handle_input_cell_value(self, input_dict, index, value):
        key = self.input_columns[index].property
        input_dict[key] = value

    # Returns column label for specified column index.
    def get_output_column_label(self, col_index):
        return self.output_columns[col_index].label

    # Returns value for output spreadsheet cell and supplied object at specified index.
    def get_output_cell_value(self, obj, index):
        # use reflection to invoke column getter method on supplied object
        val = getattr(obj, self.output_columns[index].property)()
        logging.debug("index: %d method: %s value: %s" % (index, self.output_columns[index].property, val))
        return val


class ColumnModel:

    def __init__(self, col_index, property, label=""):
        self.index = col_index
        self.property = property
        self.label = label


class OutputObject(ABC):

    def __init__(self, helper, input_dict):
        self.helper = helper
        self.input_dict = input_dict


@register
class SourceHelper(PreImportHelper):

    def __init__(self):
        super().__init__()
        self.manufacturers = set()

    @staticmethod
    def tag():
        return "Source"

    @classmethod
    def num_input_cols(cls):
        return 17

    @classmethod
    def input_column_list(cls):
        column_list = [
            ColumnModel(col_index=2, property=CABLE_TYPE_MANUFACTURER_KEY),
        ]
        return column_list

    @staticmethod
    def output_column_list():
        column_list = [
            ColumnModel(col_index=0, property="get_name", label="Name"),
            ColumnModel(col_index=1, property="get_description", label="Description"),
            ColumnModel(col_index=2, property="get_contact_info", label="Contact Info"),
            ColumnModel(col_index=3, property="get_url", label="URL"),
        ]
        return column_list

    def get_output_object(self, input_dict):

        manufacturer = input_dict[CABLE_TYPE_MANUFACTURER_KEY]
        if len(manufacturer) == 0:
            logging.debug("manufacturer is empty")
            return None

        logging.debug("found manufacturer: %s" % manufacturer)

        if manufacturer not in self.manufacturers:
            # check to see if manufacturer exists as a CDB Source
            try:
                mfr_source = self.api.getSourceApi().get_source_by_name(manufacturer)
            except ApiException as ex:
                if "ObjectNotFound" not in ex.body:
                    logging.error("exception retrieving source for manufacturer: %s - %s" % (manufacturer, ex.body))
                    print("exception retrieving source for manufacturer: %s - %s" % (manufacturer, ex.body))
                mfr_source = None
            if mfr_source:
                logging.debug("source already exists for manufacturer: %s, skipping" % manufacturer)
                return None
            else:
                logging.debug("adding output object for unique manufacturer: %s" % manufacturer)
                self.manufacturers.add(manufacturer)
                return SourceOutputObject(helper=self, input_dict=input_dict)

        else:
            logging.debug("ignoring duplicate manufacturer: %s" % manufacturer)
            return None


class SourceOutputObject(OutputObject):

    def __init__(self, helper, input_dict):
        super().__init__(helper, input_dict)
        self.description = ""
        self.contact_info = ""
        self.url = ""

    def get_name(self):
        return self.input_dict[CABLE_TYPE_MANUFACTURER_KEY]

    def get_description(self):
        return self.description

    def get_contact_info(self):
        return self.contact_info

    def get_url(self):
        return self.url


@register
class CableTypeHelper(PreImportHelper):

    def __init__(self):
        super().__init__()
        self.source_dict = {}

    # Adds helper specific command line args.
    # e.g., "parser.add_argument("--cdbUser", help="CDB User ID for API login", required=True)"
    @staticmethod
    def add_parser_args(parser):
        parser.add_argument("--ownerId", help="CDB technical system ID for owner", required=True)

    def set_args(self, args):
        super().set_args(args)
        print("owner CDB technical system id: %s" % args.ownerId)

    @staticmethod
    def tag():
        return "CableType"

    @classmethod
    def num_input_cols(cls):
        return 17

    @classmethod
    def input_column_list(cls):
        column_list = [
            ColumnModel(col_index=0, property=CABLE_TYPE_NAME_KEY),
            ColumnModel(col_index=1, property=CABLE_TYPE_DESCRIPTION_KEY),
            ColumnModel(col_index=2, property=CABLE_TYPE_MANUFACTURER_KEY),
            ColumnModel(col_index=3, property=CABLE_TYPE_PART_NUMBER_KEY),
            ColumnModel(col_index=4, property=CABLE_TYPE_ALT_PART_NUMBER_KEY),
            ColumnModel(col_index=5, property=CABLE_TYPE_DIAMETER_KEY),
            ColumnModel(col_index=6, property=CABLE_TYPE_WEIGHT_KEY),
            ColumnModel(col_index=7, property=CABLE_TYPE_CONDUCTORS_KEY),
            ColumnModel(col_index=8, property=CABLE_TYPE_INSULATION_KEY),
            ColumnModel(col_index=9, property=CABLE_TYPE_JACKET_COLOR_KEY),
            ColumnModel(col_index=10, property=CABLE_TYPE_VOLTAGE_RATING_KEY),
            ColumnModel(col_index=11, property=CABLE_TYPE_FIRE_LOAD_KEY),
            ColumnModel(col_index=12, property=CABLE_TYPE_HEAT_LIMIT_KEY),
            ColumnModel(col_index=13, property=CABLE_TYPE_BEND_RADIUS_KEY),
            ColumnModel(col_index=14, property=CABLE_TYPE_RAD_TOLERANCE_KEY),
            ColumnModel(col_index=15, property=CABLE_TYPE_LINK_URL_KEY),
            ColumnModel(col_index=16, property=CABLE_TYPE_IMAGE_URL_KEY),
        ]
        return column_list

    @staticmethod
    def output_column_list():
        column_list = [
            ColumnModel(col_index=0, property="get_name", label=CABLE_TYPE_NAME_KEY),
            ColumnModel(col_index=1, property="get_description", label=CABLE_TYPE_DESCRIPTION_KEY),
            ColumnModel(col_index=2, property="get_link_url", label=CABLE_TYPE_LINK_URL_KEY),
            ColumnModel(col_index=3, property="get_image_url", label=CABLE_TYPE_IMAGE_URL_KEY),
            ColumnModel(col_index=4, property="get_manufacturer_id", label=CABLE_TYPE_MANUFACTURER_KEY),
            ColumnModel(col_index=5, property="get_part_number", label=CABLE_TYPE_PART_NUMBER_KEY),
            ColumnModel(col_index=6, property="get_alt_part_number", label=CABLE_TYPE_ALT_PART_NUMBER_KEY),
            ColumnModel(col_index=7, property="get_diameter", label=CABLE_TYPE_DIAMETER_KEY),
            ColumnModel(col_index=8, property="get_weight", label=CABLE_TYPE_WEIGHT_KEY),
            ColumnModel(col_index=9, property="get_conductors", label=CABLE_TYPE_CONDUCTORS_KEY),
            ColumnModel(col_index=10, property="get_insulation", label=CABLE_TYPE_INSULATION_KEY),
            ColumnModel(col_index=11, property="get_jacket_color", label=CABLE_TYPE_JACKET_COLOR_KEY),
            ColumnModel(col_index=12, property="get_voltage_rating", label=CABLE_TYPE_VOLTAGE_RATING_KEY),
            ColumnModel(col_index=13, property="get_fire_load", label=CABLE_TYPE_FIRE_LOAD_KEY),
            ColumnModel(col_index=14, property="get_heat_limit", label=CABLE_TYPE_HEAT_LIMIT_KEY),
            ColumnModel(col_index=15, property="get_bend_radius", label=CABLE_TYPE_BEND_RADIUS_KEY),
            ColumnModel(col_index=16, property="get_rad_tolerance", label=CABLE_TYPE_RAD_TOLERANCE_KEY),
            ColumnModel(col_index=17, property="get_owner_id", label="owner"),
        ]
        return column_list

    def get_output_object(self, input_dict):

        logging.debug("adding output object for: %s" % input_dict[CABLE_TYPE_NAME_KEY])
        return CableTypeOutputObject(helper=self, input_dict=input_dict)

    def has_mfr(self, mfr):
        return mfr in self.source_dict

    def get_id_for_mfr(self, mfr):
        return self.source_dict[mfr]

    def set_id_for_mfr(self, mfr, id):
        self.source_dict[mfr] = id


class CableTypeOutputObject(OutputObject):

    def __init__(self, helper, input_dict):
        super().__init__(helper, input_dict)

    def get_name(self):
        return self.input_dict[CABLE_TYPE_NAME_KEY]

    def get_description(self):
        return self.input_dict[CABLE_TYPE_DESCRIPTION_KEY]

    def get_link_url(self):
        return self.input_dict[CABLE_TYPE_LINK_URL_KEY]

    def get_image_url(self):
        return self.input_dict[CABLE_TYPE_IMAGE_URL_KEY]

    def get_manufacturer_id(self):
        manufacturer = self.input_dict[CABLE_TYPE_MANUFACTURER_KEY]

        if manufacturer == "" or manufacturer is None:
            return ""

        if self.helper.has_mfr(manufacturer):
            return self.helper.get_id_for_mfr(manufacturer)
        else:
            # check to see if manufacturer exists as a CDB Source
            mfr_source = None
            try:
                mfr_source = self.helper.api.getSourceApi().get_source_by_name(manufacturer)
            except ApiException as ex:
                sys.exit("exception retrieving source for manufacturer: %s - %s" % (manufacturer, ex.body))

            if mfr_source:
                source_id = mfr_source.id
                logging.debug("found source for manufacturer: %s, id: %s" % (manufacturer, source_id))
                self.helper.set_id_for_mfr(manufacturer, source_id)
                return source_id
            else:
                sys.exit("no source found for manufacturer: %s" % manufacturer)

    def get_part_number(self):
        return self.input_dict[CABLE_TYPE_PART_NUMBER_KEY]

    def get_alt_part_number(self):
        return self.input_dict[CABLE_TYPE_ALT_PART_NUMBER_KEY]

    def get_diameter(self):
        return self.input_dict[CABLE_TYPE_DIAMETER_KEY]

    def get_weight(self):
        return self.input_dict[CABLE_TYPE_WEIGHT_KEY]

    def get_conductors(self):
        return self.input_dict[CABLE_TYPE_CONDUCTORS_KEY]

    def get_insulation(self):
        return self.input_dict[CABLE_TYPE_INSULATION_KEY]

    def get_jacket_color(self):
        return self.input_dict[CABLE_TYPE_JACKET_COLOR_KEY]

    def get_voltage_rating(self):
        return self.input_dict[CABLE_TYPE_VOLTAGE_RATING_KEY]

    def get_fire_load(self):
        return self.input_dict[CABLE_TYPE_FIRE_LOAD_KEY]

    def get_heat_limit(self):
        return self.input_dict[CABLE_TYPE_HEAT_LIMIT_KEY]

    def get_bend_radius(self):
        return self.input_dict[CABLE_TYPE_BEND_RADIUS_KEY]

    def get_rad_tolerance(self):
        return self.input_dict[CABLE_TYPE_RAD_TOLERANCE_KEY]

    def get_owner_id(self):
        return self.helper.get_args().ownerId


@register
class CableDesignHelper(PreImportHelper):

    def __init__(self):
        super().__init__()
        self.cable_type_dict = {}
        self.endpoint_dict = {}

    # Adds helper specific command line args.
    # e.g., "parser.add_argument("--cdbUser", help="CDB User ID for API login", required=True)"
    @staticmethod
    def add_parser_args(parser):
        parser.add_argument("--ownerId", help="CDB technical system ID for owner (item_category table)", required=True)
        parser.add_argument("--projectId", help="CDB item category ID for project (item_project table)", required=True)

    def set_args(self, args):
        super().set_args(args)
        print("owner CDB technical system (owner) id: %s" % args.ownerId)
        print("owner CDB item category (project) id: %s" % args.projectId)

    @staticmethod
    def tag():
        return "CableDesign"

    @classmethod
    def num_input_cols(cls):
        return 20

    @classmethod
    def input_column_list(cls):
        column_list = [
            ColumnModel(col_index=0, property=CABLE_DESIGN_NAME_KEY),
            ColumnModel(col_index=1, property=CABLE_DESIGN_LAYING_KEY),
            ColumnModel(col_index=2, property=CABLE_DESIGN_VOLTAGE_KEY),
            ColumnModel(col_index=3, property=CABLE_DESIGN_OWNER_KEY),
            ColumnModel(col_index=4, property=CABLE_DESIGN_TYPE_KEY),
            ColumnModel(col_index=5, property=CABLE_DESIGN_SRC_LOCATION_KEY),
            ColumnModel(col_index=6, property=CABLE_DESIGN_SRC_ANS_KEY),
            ColumnModel(col_index=7, property=CABLE_DESIGN_SRC_ETPM_KEY),
            ColumnModel(col_index=8, property=CABLE_DESIGN_SRC_ADDRESS_KEY),
            ColumnModel(col_index=9, property=CABLE_DESIGN_SRC_DESCRIPTION_KEY),
            ColumnModel(col_index=10, property=CABLE_DESIGN_DEST_LOCATION_KEY),
            ColumnModel(col_index=11, property=CABLE_DESIGN_DEST_ANS_KEY),
            ColumnModel(col_index=12, property=CABLE_DESIGN_DEST_ETPM_KEY),
            ColumnModel(col_index=13, property=CABLE_DESIGN_DEST_ADDRESS_KEY),
            ColumnModel(col_index=14, property=CABLE_DESIGN_DEST_DESCRIPTION_KEY),
        ]
        return column_list

    @staticmethod
    def output_column_list():
        column_list = [
            ColumnModel(col_index=0, property="get_name", label="name"),
            ColumnModel(col_index=1, property="get_alt_name", label="alt name"),
            ColumnModel(col_index=2, property="get_ext_name", label="ext cable name"),
            ColumnModel(col_index=3, property="get_import_id", label="import cable id"),
            ColumnModel(col_index=4, property="get_alt_id", label="alt cable id"),
            ColumnModel(col_index=5, property="get_description", label="description"),
            ColumnModel(col_index=6, property="get_laying", label="laying"),
            ColumnModel(col_index=7, property="get_voltage", label="voltage"),
            ColumnModel(col_index=8, property="get_owner_id", label="owner id"),
            ColumnModel(col_index=9, property="get_project_id", label="project id"),
            ColumnModel(col_index=10, property="get_cable_type_id", label="cable type id"),
            ColumnModel(col_index=11, property="get_endpoint1_id", label="endpoint1 id"),
            ColumnModel(col_index=12, property="get_endpoint1_description", label="endpoint1 description"),
            ColumnModel(col_index=13, property="get_endpoint2_id", label="endpoint2 id"),
            ColumnModel(col_index=14, property="get_endpoint2_description", label="endpoint2 description"),
        ]
        return column_list

    def get_output_object(self, input_dict):

        logging.debug("adding output object for: %s" % input_dict[CABLE_TYPE_NAME_KEY])
        return CableDesignOutputObject(helper=self, input_dict=input_dict)

    def has_cable_type(self, cable_type):
        return cable_type in self.cable_type_dict

    def get_id_for_cable_type(self, cable_type):
        return self.cable_type_dict[cable_type]

    def set_id_for_cable_type(self, cable_type, id):
        self.cable_type_dict[cable_type] = id

    def has_endpoint(self, endpoint):
        return endpoint in self.endpoint_dict

    def get_id_for_endpoint(self, endpoint):
        return self.endpoint_dict[endpoint]

    def set_id_for_endpoint(self, endpoint, id):
        self.endpoint_dict[endpoint] = id


class CableDesignOutputObject(OutputObject):

    def __init__(self, helper, input_dict):
        super().__init__(helper, input_dict)

    def get_name(self):
        return "#cdbid#"

    def get_alt_name(self):
        return self.input_dict[CABLE_DESIGN_SRC_ETPM_KEY] + ":" + self.input_dict[CABLE_DESIGN_DEST_ETPM_KEY] + ":#cdbid#"

    def get_ext_name(self):
        return self.input_dict[CABLE_DESIGN_NAME_KEY]

    def get_import_id(self):
        return ""

    def get_alt_id(self):
        return ""

    def get_description(self):
        return ""

    def get_laying(self):
        return self.input_dict[CABLE_DESIGN_LAYING_KEY]

    def get_voltage(self):
        return self.input_dict[CABLE_DESIGN_VOLTAGE_KEY]

    def get_owner_id(self):
        return self.helper.get_args().ownerId

    def get_project_id(self):
        return self.helper.get_args().projectId

    def get_cable_type_id(self):        
        cable_type_name = self.input_dict[CABLE_DESIGN_TYPE_KEY]

        if cable_type_name == "" or cable_type_name is None:
            return ""
        
        if self.helper.has_cable_type(cable_type_name):
            return self.helper.get_id_for_cable_type(cable_type_name)
        else:
            # check to see if cable type exists in CDB by name
            cable_type_object = None
            try:
                cable_type_object = self.helper.api.getCableCatalogItemApi().get_cable_catalog_item_by_name(cable_type_name)
            except ApiException as ex:
                sys.exit("exception retrieving cable catalog item: %s - %s" % (cable_type_name, ex.body))
    
            if cable_type_object:
                cable_type_id = cable_type_object.id
                logging.debug("found cable type with name: %s, id: %s" % (cable_type_name, cable_type_id))
                self.helper.set_id_for_cable_type(cable_type_name, cable_type_id)
                return cable_type_id
            else:
                sys.exit("no cable type found with name: %s" % cable_type_name)

    def get_endpoint_id(self, input_column_key):
        endpoint_name = self.input_dict[input_column_key]

        if endpoint_name == "" or endpoint_name is None:
            return ""

        if self.helper.has_endpoint(endpoint_name):
            return self.helper.get_id_for_endpoint(endpoint_name)
        else:
            # check to see if endpoint exists in CDB by name
            endpoint_object = None
            try:
                endpoint_object = self.helper.api.getMachineDesignItemApi().get_machine_design_item_by_name(endpoint_name)
            except ApiException as ex:
                msg_string = ""
                items = self.helper.api.getMachineDesignItemApi().get_detailed_md_search_results(endpoint_name)
                for item in items:
                    msg_string = msg_string + item.object_name + ", "
                if len(msg_string) > 0:
                    print("candidate items located by fuzzy search: " + msg_string)
                sys.exit("exception retrieving machine design item: %s - %s" % (endpoint_name, ex.body))

            if endpoint_object:
                endpoint_id = endpoint_object['id']
                logging.debug("found machine design item with name: %s, id: %s" % (endpoint_name, endpoint_id))
                self.helper.set_id_for_endpoint(endpoint_name, endpoint_id)
                return endpoint_id
            else:
                sys.exit("no machine design item found with name: %s" % endpoint_name)

    def get_endpoint1_id(self):
        return self.get_endpoint_id(CABLE_DESIGN_SRC_ETPM_KEY)

    def get_endpoint1_description(self):
        return self.input_dict[CABLE_DESIGN_SRC_LOCATION_KEY] + ":" + self.input_dict[CABLE_DESIGN_SRC_ANS_KEY] + ":" + self.input_dict[CABLE_DESIGN_SRC_ETPM_KEY] + ":" + self.input_dict[CABLE_DESIGN_SRC_ADDRESS_KEY] + ":" + self.input_dict[CABLE_DESIGN_SRC_DESCRIPTION_KEY]

    def get_endpoint2_id(self):
        return self.get_endpoint_id(CABLE_DESIGN_DEST_ETPM_KEY)

    def get_endpoint2_description(self):
        return self.input_dict[CABLE_DESIGN_DEST_LOCATION_KEY] + ":" + self.input_dict[CABLE_DESIGN_DEST_ANS_KEY] + ":" + self.input_dict[CABLE_DESIGN_DEST_ETPM_KEY] + ":" + self.input_dict[CABLE_DESIGN_DEST_ADDRESS_KEY] + ":" + self.input_dict[CABLE_DESIGN_DEST_DESCRIPTION_KEY]


def main():

    # find --type command line parameter so we can create the appropriate helper subclass before proceeding
    type_arg = "--type"
    if type_arg not in sys.argv:
        sys.exit("--type command line parameter not specified, exiting")
    else:
        type_index = sys.argv.index(type_arg)
        if type_index < (len(sys.argv) - 1):
            type_arg_value = sys.argv[type_index+1]
        else:
            sys.exit("no value specified for --type command line parameter, exiting")

    # create instance of appropriate helper subclass
    if not PreImportHelper.is_valid_type(type_arg_value):
        sys.exit("unknown value for type parameter, expected Source, CableType, or CableDesign, got: %s" % type_arg_value)
    helper = PreImportHelper.create_helper(type_arg_value)

    # process other command line arguments
    parser = argparse.ArgumentParser()
    parser.add_argument("inputFile", help="Data collection workbook xlsx file with 'cable specs' tab")
    parser.add_argument("outputFile", help="Official CDB Sources import format xlsx file")
    parser.add_argument(type_arg, help="Type of pre-import processing (Source, CableType, or CableDesign)", required=True)
    parser.add_argument("--logFile", help="File for log output", required=True)
    parser.add_argument("--cdbUser", help="CDB User ID for API login", required=True)
    parser.add_argument("--cdbPassword", help="CDB User password for API login", required=True)
    parser.add_argument("--sheetIndex", help="Index of worksheet within workbook (0-based)", required=True)
    parser.add_argument("--headerIndex", help="Index of header row in worksheet (0-based)", required=True)
    parser.add_argument("--firstDataIndex", help="Index of first data row in worksheet (0-based)", required=True)
    parser.add_argument("--lastDataIndex", help="Index of last data row in worksheet (0-based)", required=True)
    helper.add_parser_args(parser)
    args = parser.parse_args()
    print("using inputFile: %s" % args.inputFile)
    print("using outputFile: %s" % args.outputFile)
    print("using logFile: %s" % args.logFile)
    print("pre-import type: %s" % args.type)
    print("cdb user id: %s" % args.cdbUser)
    print("cdb user password: %s" % args.cdbPassword)
    print("worksheet index: %s" % args.sheetIndex)
    print("header row index: %s" % args.headerIndex)
    print("first data row index: %s" % args.firstDataIndex)
    print("last data row index: %s" % args.lastDataIndex)
    helper.set_args(args)

    # configure logging
    logging.basicConfig(filename=args.logFile, filemode='w', level=logging.DEBUG, format='%(levelname)s - %(message)s')

    # open connection to CDB
    api = CdbApiFactory("http://localhost:8080/cdb")
    try:
        api.authenticateUser(args.cdbUser, args.cdbPassword)
        api.testAuthenticated()
    except ApiException:
        sys.exit("CDB login failed")
    helper.set_api(api)

    # open input spreadsheet
    input_book = xlrd.open_workbook(args.inputFile)
    input_sheet = input_book.sheet_by_index(int(args.sheetIndex))
    logging.info("input spreadsheet dimensions: %d x %d" % (input_sheet.nrows, input_sheet.ncols))

    # validate input spreadsheet dimensions
    if input_sheet.nrows < int(args.firstDataIndex)+1:
        sys.exit("no data in inputFile: %s" % args.inputFile)
    if input_sheet.ncols != helper.num_input_cols():
        sys.exit("inputFile %s doesn't contain expected number of columns: %d" % (args.inputFile, helper.num_input_cols()))

    # process rows from input spreadsheet
    output_objects = []
    input_rows = 0
    for row_count in range(input_sheet.nrows):

        # stop when we reach lastDataIndex
        if row_count > int(args.lastDataIndex):
            break

        # skip header
        if row_count < int(args.firstDataIndex):
            continue

        input_rows = input_rows + 1

        logging.debug("processing row %d from input spreadsheet" % (row_count+1))

        input_dict = {}

        for col_count in range(helper.num_input_cols()):
            if col_count in helper.input_columns:
                # read cell value from spreadsheet
                val = input_sheet.cell(row_count, col_count).value
                helper.handle_input_cell_value(input_dict=input_dict, index=col_count, value=val)

        output_obj = helper.get_output_object(input_dict=input_dict)
        if output_obj:
            output_objects.append(output_obj)

    # create output spreadsheet
    output_book = xlsxwriter.Workbook(args.outputFile)
    output_sheet = output_book.add_worksheet()

    # write output spreadsheet header row
    row_count = 0
    for col_count in range(helper.num_output_cols()):
        if col_count in helper.output_columns:
            label = helper.get_output_column_label(col_index=col_count)
            output_sheet.write(row_count, col_count, label)

    # process output spreadsheet rows
    output_rows = 0
    if len(output_objects) == 0:
        logging.info("no output objects, output spreadsheet will be empty")
    for output_obj in output_objects:

        row_count = row_count + 1
        output_rows = output_rows + 1

        logging.debug("processing row %d in output spreadsheet" % (row_count+1))

        for col_count in range(helper.num_output_cols()):
            if col_count in helper.output_columns:

                val = helper.get_output_cell_value(obj=output_obj, index=col_count)
                output_sheet.write(row_count, col_count, val)

    # save output spreadsheet
    output_book.close()

    # close CDB connection
    try:
        api.logOutUser()
    except ApiException:
        sys.exit("CDB logout failed")

    # print summary
    summary_msg = "SUMMARY: processed %d input rows and wrote %d output rows" % (input_rows, output_rows)
    print()
    print(summary_msg)
    logging.info(summary_msg)


if __name__ == '__main__':
    main()