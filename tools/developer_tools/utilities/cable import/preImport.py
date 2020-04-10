import argparse
import sys
from abc import ABC, abstractmethod

import xlrd
import xlsxwriter

from CdbApiFactory import CdbApiFactory
from cdbApi import ApiException


def register(helper_class):
    PreImportHelper.register(helper_class.tag(), helper_class)


class PreImportHelper(ABC):

    helperDict = {}

    def __init__(self, api, opts):
        self.api = api
        self.opts = opts
        self.input_columns = {}
        self.initialize_input_columns()
        self.output_columns = {}
        self.initialize_output_columns()

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
    def create_helper(cls, tag, api, helper_opts):
        helper_class = cls.helperDict[tag]
        helper_instance = helper_class(api, helper_opts)
        return helper_instance

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

    # Builds dictionary whose keys are column index and value is column model object.
    def initialize_input_columns(self):
        for col in self.input_column_list():
            self.input_columns[col.index] = col

    # Builds dictionary whose keys are column index and value is column model object.
    def initialize_output_columns(self):
        for col in self.output_column_list():
            self.output_columns[col.index] = col

    # Handles cell value from input spreadsheet at specified column index for supplied input object.
    def handle_input_cell_value(self, obj, index, value):
        # use reflection to invoke column setter method on supplied object
        getattr(obj, self.input_columns[index].method)(value)

    # Returns an output object for the specified input object, or None if the input object is duplicate.
    @abstractmethod
    def get_output_object(obj):
        pass

    # Returns column label for specified column index.
    def get_output_column_label(self, col_index):
        return self.output_columns[col_index].label

    # Returns value for output spreadsheet cell and supplied object at specified index.
    def get_output_cell_value(self, obj, index):
        # use reflection to invoke column getter method on supplied object
        val = getattr(obj, self.output_columns[index].method)()
        print("index: %d method: %s value: %s" % (index, self.output_columns[index].method, val))
        return val


@register
class SourceHelper(PreImportHelper):

    def __init__(self, api, opts):
        super().__init__(api, opts)
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

    @staticmethod
    def input_column_list():
        column_list = [
            ColumnModel(col_index=4, method="set_manufacturer"),
        ]
        return column_list

    @staticmethod
    def output_column_list():
        column_list = [
            ColumnModel(col_index=0, method="get_name", label="Name"),
            ColumnModel(col_index=1, method="get_description", label="Description"),
            ColumnModel(col_index=2, method="get_contact_info", label="Contact Info"),
            ColumnModel(col_index=3, method="get_url", label="URL"),
        ]
        return column_list

    def get_output_object(self, obj):

        manufacturer = obj.get_manufacturer()
        print("found manufacturer: %s" % manufacturer)

        if manufacturer not in self.manufacturers:
            # check to see if manufacturer exists as a CDB Source
            try:
                mfr_source = self.api.getSourceApi().get_source_by_name(manufacturer)
            except ApiException as ex:
                if "ObjectNotFound" not in ex.body:
                    print("exception retrieving source for manufacturer: %s - %s" % (manufacturer, ex.body))
                mfr_source = None
            if mfr_source:
                print("source already exists for manufacturer: %s, skipping" % manufacturer)
                return None
            else:
                print("adding output object for unique manufacturer: %s" % manufacturer)
                self.manufacturers.add(manufacturer)
                return SourceOutputObject(input_object=obj)

        else:
            print("ignoring duplicate manufacturer: %s" % manufacturer)
            return None


class ColumnModel:

    def __init__(self, col_index, method, label=""):
        self.index = col_index
        self.method = method
        self.label = label


class InputObject(ABC):
    pass


class SourceInputObject(InputObject):

    def __init__(self):
        self.manufacturer = ""

    def set_manufacturer(self, m):
        self.manufacturer = m

    def get_manufacturer(self):
        return self.manufacturer


class OutputObject(ABC):

    def __init__(self, o):
        self.input_object = o


class SourceOutputObject(OutputObject):

    def __init__(self, input_object):
        super().__init__(input_object)
        self.description = ""
        self.contact_info = ""
        self.url = ""

    def get_name(self):
        return self.input_object.get_manufacturer()

    def get_description(self):
        return self.description

    def get_contact_info(self):
        return self.contact_info

    def get_url(self):
        return self.url


def main():

    # process command line
    parser = argparse.ArgumentParser()
    parser.add_argument("inputFile", help="Data collection workbook xlsx file with 'cable specs' tab")
    parser.add_argument("outputFile", help="Official CDB Sources import format xlsx file")
    parser.add_argument("--type", help="Type of pre-import processing (Source, CableType, or CableDesign)", required=True)
    parser.add_argument("--logFile", help="File for log output", required=True)
    parser.add_argument("--cdbUser", help="CDB User ID for API login", required=True)
    parser.add_argument("--cdbPassword", help="CDB User password for API login", required=True)
    args = parser.parse_args()
    print("using inputFile: %s" % args.inputFile)
    print("using outputFile: %s" % args.outputFile)
    print("using logFile: %s" % args.logFile)
    print("pre-import type: %s" % args.type)
    print("cdb user id: %s" % args.cdbUser)
    print("cdb user password: %s" % args.cdbPassword)

    # open connection to CDB
    api = CdbApiFactory("http://localhost:8080/cdb")
    try:
        api.authenticateUser(args.cdbUser, args.cdbPassword)
        api.testAuthenticated()
    except ApiException:
        sys.exit("CDB login failed")

    # open input spreadsheet
    input_book = xlrd.open_workbook(args.inputFile)
    input_sheet = input_book.sheet_by_index(0)
    print("input spreadsheet dimensions: %d x %d" % (input_sheet.nrows, input_sheet.ncols))

    # create instance of appropriate helper subclass
    opts = None
    helper = PreImportHelper.create_helper(args.type, api, opts)

    # validate input spreadsheet dimensions
    if input_sheet.nrows < 2:
        sys.exit("no data in inputFile: %s" % args.inputFile)
    if input_sheet.ncols != helper.num_input_cols():
        sys.exit("inputFile %s doesn't contain expected number of columns: %d" % (args.inputFile, helper.num_input_cols()))

    # process rows from input spreadsheet
    output_objects = []
    for row_count in range(input_sheet.nrows):

        # skip header
        if row_count == 0:
            continue

        print("processing row %d from input spreadsheet" % (row_count+1))

        input_object = SourceInputObject()

        for col_count in range(helper.num_input_cols()):
            if col_count in helper.input_columns:
                # read cell value from spreadsheet
                val = input_sheet.cell(row_count, col_count).value
                helper.handle_input_cell_value(obj=input_object, index=col_count, value=val)

        output_obj = helper.get_output_object(obj=input_object)
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
    if len(output_objects) == 0:
        print("no output objects, output spreadsheet will be empty")
    for output_obj in output_objects:

        row_count = row_count + 1

        print("processing row %d in output spreadsheet" % (row_count+1))

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


if __name__ == '__main__':
    main()