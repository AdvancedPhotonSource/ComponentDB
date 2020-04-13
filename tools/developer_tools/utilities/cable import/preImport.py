import argparse
import logging
import sys
from abc import ABC, abstractmethod

import xlrd
import xlsxwriter

from CdbApiFactory import CdbApiFactory
from cdbApi import ApiException


# constants
CABLE_TYPE_MANUFACTURER_KEY = "manufacturer"


def register(helper_class):
    PreImportHelper.register(helper_class.tag(), helper_class)


class PreImportHelper(ABC):

    helperDict = {}

    def __init__(self):
        self.input_columns = {}
        self.initialize_input_columns()
        self.output_columns = {}
        self.initialize_output_columns()
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
    @staticmethod
    @abstractmethod
    def num_input_cols():
        pass

    # Returns number of columns in output spreadsheet.
    @staticmethod
    @abstractmethod
    def num_output_cols():
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


@register
class SourceHelper(PreImportHelper):

    def __init__(self):
        super().__init__()
        self.manufacturers = set()

    @staticmethod
    def tag():
        return "Source"

    @staticmethod
    def num_input_cols():
        return 17

    @staticmethod
    def num_output_cols():
        return 4

    @classmethod
    def input_column_list(cls):
        column_list = [
            ColumnModel(col_index=4, property=CABLE_TYPE_MANUFACTURER_KEY),
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
        logging.debug("found manufacturer: %s" % manufacturer)

        if manufacturer not in self.manufacturers:
            # check to see if manufacturer exists as a CDB Source
            try:
                mfr_source = self.api.getSourceApi().get_source_by_name(manufacturer)
            except ApiException as ex:
                if "ObjectNotFound" not in ex.body:
                    logging.error("exception retrieving source for manufacturer: %s - %s" % (manufacturer, ex.body))
                mfr_source = None
            if mfr_source:
                logging.debug("source already exists for manufacturer: %s, skipping" % manufacturer)
                return None
            else:
                logging.debug("adding output object for unique manufacturer: %s" % manufacturer)
                self.manufacturers.add(manufacturer)
                return SourceOutputObject(input_dict=input_dict)

        else:
            logging.debug("ignoring duplicate manufacturer: %s" % manufacturer)
            return None


class ColumnModel:

    def __init__(self, col_index, property, label=""):
        self.index = col_index
        self.property = property
        self.label = label


class OutputObject(ABC):

    def __init__(self, input_dict):
        self.input_dict = input_dict


class SourceOutputObject(OutputObject):

    def __init__(self, input_dict):
        super().__init__(input_dict)
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
    helper.add_parser_args(parser)
    args = parser.parse_args()
    print("using inputFile: %s" % args.inputFile)
    print("using outputFile: %s" % args.outputFile)
    print("using logFile: %s" % args.logFile)
    print("pre-import type: %s" % args.type)
    print("cdb user id: %s" % args.cdbUser)
    print("cdb user password: %s" % args.cdbPassword)

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
    input_sheet = input_book.sheet_by_index(0)
    logging.info("input spreadsheet dimensions: %d x %d" % (input_sheet.nrows, input_sheet.ncols))

    # validate input spreadsheet dimensions
    if input_sheet.nrows < 2:
        sys.exit("no data in inputFile: %s" % args.inputFile)
    if input_sheet.ncols != helper.num_input_cols():
        sys.exit("inputFile %s doesn't contain expected number of columns: %d" % (args.inputFile, helper.num_input_cols()))

    # process rows from input spreadsheet
    output_objects = []
    input_rows = 0
    for row_count in range(input_sheet.nrows):

        # skip header
        if row_count == 0:
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