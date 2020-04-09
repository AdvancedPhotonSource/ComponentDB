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

    # register helper concrete classes for lookup by tag
    @classmethod
    def register(cls, tag, the_class):
        cls.helperDict[tag] = the_class

    # return helper class for specified tag
    @classmethod
    def get_helper_class(cls, tag):
        return cls.helperDict[tag]

    # check if specified tag is valid
    @classmethod
    def is_valid_type(cls, tag):
        return tag in cls.helperDict

    # create instance of class with specified tag
    @classmethod
    def create_helper(cls, tag, helper_opts):
        helper_class = cls.helperDict[tag]
        helper_instance = helper_class(helper_opts)
        return helper_instance

    # for subclass to return its registered tag
    @staticmethod
    @abstractmethod  # must be innermost decorator
    def tag():
        pass

    # for subclass to return the number of input columns
    @staticmethod
    @abstractmethod
    def num_input_cols():
        pass

    # for subclass to return the number of output columns
    @staticmethod
    @abstractmethod
    def num_output_cols():
        pass

    # initialize dict of input columns
    @abstractmethod
    def initialize_input_columns(self):
        pass

    def __init__(self, helper_opts):
        self.opts = helper_opts
        self.input_columns = self.initialize_input_columns()
        self.output_columns = self.initialize_output_columns()


@register
class SourceHelper(PreImportHelper):

    def __init__(self, helper_opts):
        super().__init__(helper_opts)

    @staticmethod
    def tag():
        return "Source"

    @staticmethod
    def num_input_cols():
        return 17

    @staticmethod
    def num_output_cols():
        return 4

    def initialize_input_columns(self):
        columns = {4: "set_manufacturer"}
        return columns

    def initialize_output_columns(self):
        columns = {0: "get_name"}
        return columns


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

    def __init__(self, o):
        super().__init__(o)
        self.name = ""

    def get_name(self):
        return self.input_object.get_manufacturer()


def main():

    # process command line

    parser = argparse.ArgumentParser()
    parser.add_argument("inputFile", help="Data collection workbook xlsx file with 'cable specs' tab")
    parser.add_argument("outputFile", help="Official CDB Sources import format xlsx file")
    parser.add_argument("--type", help="Type of pre-import processing (Source, CableType, or CableDesign)", required=True)
    parser.add_argument("--logFile", help="File for log output", required=True)
    args = parser.parse_args()
    print("using inputFile: %s" % args.inputFile)
    print("using outputFile: %s" % args.outputFile)
    print("using logFile: %s" % args.logFile)
    print("pre-import type: %s" % args.type)

    # open connection to CDB
    api = CdbApiFactory("http://localhost:8080/cdb")
    try:
        api.authenticateUser("cdb", "cdb")
        api.testAuthenticated()
    except ApiException:
        sys.exit("CDB login failed")
    source_api = api.getSourceApi()

    # process input spreadsheet

    input_book = xlrd.open_workbook(args.inputFile)
    input_sheet = input_book.sheet_by_index(0)
    print("input spreadsheet dimensions: %d x %d" % (input_sheet.nrows, input_sheet.ncols))

    opts = None
    helper = PreImportHelper.create_helper(args.type, opts)

    if input_sheet.nrows < 2:
        sys.exit("no data in inputFile: %s" % args.inputFile)
    if input_sheet.ncols != helper.num_input_cols():
        sys.exit("inputFile %s doesn't contain expected number of columns: %d" % (args.inputFile, helper.num_input_cols()))

    manufacturers = set()
    output_objects = []
    for row_count in range(input_sheet.nrows):

        # skip header
        if row_count == 0:
            continue

        input_object = SourceInputObject()

        for col_count in range(helper.num_input_cols()):
            if col_count in helper.input_columns:
                # read cell value from spreadsheet
                val = input_sheet.cell(row_count, col_count).value
                # use reflection to invoke setter on input object
                getattr(input_object, helper.input_columns[col_count])(val)

        manufacturer = input_object.get_manufacturer()
        print("row %d found manufacturer: %s" % (row_count, manufacturer))
        if manufacturer not in manufacturers:

            # check to see if manufacturer exists as a CDB Source
            try:
                mfr_source = source_api.get_source_by_name(manufacturer)
            except ApiException as ex:
                if "ObjectNotFound" not in ex.body:
                    print("exception retrieving source for manufacturer: %s - %s" % (manufacturer, ex.body))
                mfr_source = None
            if mfr_source:
                print("source already exists for manufacturer: %s, skipping" % (manufacturer))
            else:
                manufacturers.add(manufacturer)
                output_objects.append(SourceOutputObject(input_object))

    # create output spreadsheet
    output_book = xlsxwriter.Workbook(args.outputFile)
    output_sheet = output_book.add_worksheet()

    # write output spreadsheet header row
    row_count = 0
    output_sheet.write(row_count, 0, "Name")
    output_sheet.write(row_count, 1, "Description")
    output_sheet.write(row_count, 2, "Contact Info")
    output_sheet.write(row_count, 3, "URL")

    for output_obj in output_objects:

        row_count = row_count + 1

        for col_count in range(helper.num_output_cols()):
            if col_count in helper.output_columns:

                # write call value to spreadsheet using reflection on getter method name
                val = getattr(output_obj, helper.output_columns[col_count])()
                print("writing value: %s" % val)
                output_sheet.write(row_count, col_count, val)

        # write row to output spreadsheet for this manufacturer
        print("adding manufacturer: %s to output spreadsheet" % output_obj.get_name())

    # save output spreadsheet
    output_book.close()

    # close CDB connection
    try:
        api.logOutUser()
    except ApiException:
        sys.exit("CDB logout failed")


if __name__ == '__main__':
    main()