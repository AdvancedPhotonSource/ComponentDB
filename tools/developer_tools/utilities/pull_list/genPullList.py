import argparse
import configparser
import gc
import logging
import os
import sys
from abc import ABC, abstractmethod

import openpyxl

CONFIG_SECTION_DEFAULT = "DEFAULT"
CONFIG_RES_DEFAULT_INPUT_DIR = "inputDir"
CONFIG_RES_DEFAULT_OUTPUT_DIR = "outputDir"

CONFIG_SECTION_LOADER = "LOADER"
CONFIG_RES_LOADER_INPUT_FILE = "inputFile"

KEY_ROW_VALID = "row valid"
KEY_ROW_VALID_INFO = "row valid info"
KEY_CDB_CABLE_NAME = "cdb cable name"
KEY_CDB_CABLE_TECH_SYSTEM = "cdb cable tech system"
KEY_CDB_CABLE_DESC = "cdb cable description"
KEY_CDB_CABLE_ID = "cdb cable id"
KEY_CDB_CABLE_ENDPOINTS = "cdb cable endpoints"
KEY_CDB_CABLE_TYPE = "cdb cable type"
KEY_CDB_CABLE_END1_DEVICE = "cdb cable end1 device"
KEY_CDB_CABLE_END1_PORT = "cdb cable end1 port"
KEY_CDB_CABLE_END1_CONNECTOR = "cdb cable end1 connector"
KEY_CDB_CABLE_END2_DEVICE = "cdb cable end2 device"
KEY_CDB_CABLE_END2_PORT = "cdb cable end2 port"
KEY_CDB_CABLE_END2_CONNECTOR = "cdb cable end2 connector"
KEY_CDB_CABLE_EXT_NAME = "cdb cable kabel name"
KEY_CDB_CABLE_IMPORT_ID = "cdb cable import id"
KEY_CDB_CABLE_ALT_ID = "cdb cable legacy id"
KEY_CDB_CABLE_END1_DESC = "cdb cable end1 description"
KEY_CDB_CABLE_END2_DESC = "cdb cable end2 description"


class ExcelColumnModel:

    def __init__(self, key, required=False):
        self.key = key
        self.required = required


class ExcelSheetModel:

    def __init__(self, column_specs):
        self.column_specs = column_specs
        self.sheet_name = None
        self.sheet = None
        self.workbook = None

    def set_sheet_name(self, sheet_name):
        self.sheet_name = sheet_name

    def set_sheet(self, sheet):
        self.sheet = sheet

    def set_workbook(self, workbook):
        self.workbook = workbook

    def validate_dimensions(self):

        # log actual dimensions
        num_actual_cols = self.sheet.max_column
        num_actual_rows = self.sheet.max_row
        logging.info("input spreadsheet dimensions: %d cols x %d rows" % (num_actual_cols, num_actual_rows))

        # validate num actual columns matches expected
        num_expected_cols = len(self.column_specs)
        if num_expected_cols != num_actual_cols:
            return False, "sheet: %s actual number of columns %d doesn't match expected number %d" % \
                   (self.sheet_name, num_actual_cols, num_expected_cols)
        else:
            return True, ""

    def load_rows(self):

        rows = []
        load_valid = True
        load_valid_info = ""
        first_row_ind = 2
        last_row_ind = self.sheet.max_row

        # iterate sheet rows
        for row_ind in range(first_row_ind, last_row_ind+1):
            row_dict = {}
            row_valid = True
            row_valid_info = ""
            # build dictionary of column values for row
            col_ind = 1
            for spec in self.column_specs:
                cell_value = self.sheet.cell(row_ind, col_ind).value
                if cell_value == None:
                    cell_value = ""
                if spec.required and cell_value == "":
                    row_valid = False
                    row_valid_info = row_valid_info + "Row: %d missing value for required column %s. " % (row_ind, spec.key)
                row_dict[spec.key] = cell_value
                col_ind = col_ind + 1
            row_dict[KEY_ROW_VALID] = row_valid
            row_dict[KEY_ROW_VALID_INFO] = row_valid_info
            rows.append(row_dict)
            logging.debug("row: %d dict: %s" % (row_ind, str(row_dict)))
            if not row_valid:
                load_valid = False
                load_valid_info = "Sheet contains invalid rows."

        return load_valid, load_valid_info, rows

class ExcelWorkbookModel:

    def __init__(self, filename, sheets):
        self.filename = filename
        self.sheets = sheets
        self.workbook = None

    def initialize(self):

        init_valid = True
        init_valid_info = ""

        # create  openpyxl workbook
        self.workbook = openpyxl.load_workbook(self.filename)

        # process sheets
        sheet_names = self.workbook.sheetnames
        sheet_index = 0
        for sheet in self.sheets:
            sheet_name = sheet_names[sheet_index]
            sheet.set_sheet_name(sheet_name)
            sheet.set_sheet(self.workbook[sheet_name])
            sheet.set_workbook(self.workbook)
            sheet_init_valid, sheet_init_valid_info = sheet.validate_dimensions()
            if not sheet_init_valid:
                fatal_error(sheet_init_valid_info)
            sheet_index = sheet_index + 1

        return init_valid, init_valid_info


class CableInfoLoader:

    def __init__(self, input_dir, config):
        self.input_dir = input_dir
        self.config = config
        self.workbook = None
        self.sheet = None

    def initialize(self):

        init_wb_valid = True
        init_wb_valid_info = ""

        # get input filename from config
        input_filename = get_config_resource(self.config, CONFIG_SECTION_LOADER, CONFIG_RES_LOADER_INPUT_FILE, True)
        file_input = self.input_dir + "/" + input_filename
        if not os.path.isfile(file_input):
            fatal_error("'[%s] inputFile' file: %s does not exist in directory: %s, exiting" %
                        (CONFIG_SECTION_LOADER, input_filename, self.input_dir))

        # create column, sheet, workbook models
        specs = [
            ExcelColumnModel(key=KEY_CDB_CABLE_NAME, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_TECH_SYSTEM, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_DESC, required=False),
            ExcelColumnModel(key=KEY_CDB_CABLE_ID, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_ENDPOINTS, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_TYPE, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_END1_DEVICE, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_END1_PORT, required=False),
            ExcelColumnModel(key=KEY_CDB_CABLE_END1_CONNECTOR, required=False),
            ExcelColumnModel(key=KEY_CDB_CABLE_END2_DEVICE, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_END2_PORT, required=False),
            ExcelColumnModel(key=KEY_CDB_CABLE_END2_CONNECTOR, required=False),
            ExcelColumnModel(key=KEY_CDB_CABLE_EXT_NAME, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_IMPORT_ID, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_ALT_ID, required=False),
            ExcelColumnModel(key=KEY_CDB_CABLE_END1_DESC, required=True),
            ExcelColumnModel(key=KEY_CDB_CABLE_END2_DESC, required=True),
        ]
        self.sheet = ExcelSheetModel(specs)
        self.workbook = ExcelWorkbookModel(file_input, [self.sheet])
        (init_wb_valid, init_wb_valid_info) = self.workbook.initialize()

        return init_wb_valid, init_wb_valid_info

    def load_records(self):
        (load_valid, load_valid_info, rows) = self.sheet.load_rows()
        return load_valid, load_valid_info, rows

    def finalize(self):
        self.sheet = None
        self.workbook = None
        self.config = None
        self.input_dir = None
        return True, ""


class CableInfoModule(ABC):

    def __init__(self):
        pass

    def initialize(self):
        return True, ""

    def process_records(self, records):
        return True, ""


class TestModule(CableInfoModule):

    def __init__(self):
        pass


class PullListGenerator:
    def __init__(self):
        pass

    def initialize(self):
        return True, ""

    def generate(self, records):
        return True, ""


def fatal_error(error_msg):
    print()
    print("ERROR ====================")
    print()
    print(error_msg)
    sys.exit(0)


def get_config_resource(config, section, key, is_required, print_value=True, print_mask=None):
    value = None
    if section not in config:
        fatal_error("Invalid config section: %s, exiting" % section)
    if key not in config[section]:
        if is_required:
            fatal_error("Config key: %s not found in section: %s, exiting" % (key, section))
    else:
        value = config[section][key]
        if is_required and len(value) == 0:
            fatal_error("value not provided for required option '[%s] %s', exiting" % (section, key))
    if print_value:
        print_obj = value
        if value is not None:
            if print_mask is not None:
                if len(value) > 0:
                    print_obj = print_mask
        print("[%s] %s: %s" % (section, key, print_obj))
    return value


def main():

    # parse command line args
    parser = argparse.ArgumentParser()
    parser.add_argument("--configDir", help="Directory containing script config files.", required=True)
    args = parser.parse_args()

    print()
    print("==================== genPullList.py ====================")
    print()
    print("configDir: %s" % args.configDir)

    #
    # Determine config file names and paths and test.
    #

    option_config_dir = args.configDir
    if not os.path.isdir(option_config_dir):
        fatal_error("Specified configDir: %s does not exist, exiting" % option_config_dir)

    file_config_main = option_config_dir + "/genPullList.conf"
    if not os.path.isfile(file_config_main):
        fatal_error("'genPullList.conf' file not found in configDir: %s', exiting" % option_config_dir)

    #
    # Process options.
    #

    # read config files
    config_preimport = configparser.ConfigParser()
    config_preimport.read(file_config_main)

    print()
    print("preimport.conf OPTIONS ====================")
    print()

    # process inputDir option
    option_input_dir = get_config_resource(config_preimport, CONFIG_SECTION_DEFAULT, CONFIG_RES_DEFAULT_INPUT_DIR, True)
    if not os.path.isdir(option_input_dir):
        fatal_error("'[%s] inputDir' directory: %s does not exist, exiting" % ('DEFAULT', option_input_dir))

    # process outputDir option
    option_output_dir = get_config_resource(config_preimport, CONFIG_SECTION_DEFAULT, CONFIG_RES_DEFAULT_OUTPUT_DIR, True)
    if not os.path.isdir(option_output_dir):
        fatal_error("'[%s] outputDir' directory: %s does not exist, exiting" % ('DEFAULT', option_output_dir))

    #
    # Configure logging.
    #

    # log file
    file_log = "%s/genPullList.log" % option_output_dir

    # configure logging
    logging.basicConfig(filename=file_log, filemode='w', level=logging.DEBUG, format='%(levelname)s - %(message)s')


    #
    # Load cable records.
    #

    loader = CableInfoLoader(option_input_dir, config_preimport)

    (loader_init_valid, loader_init_valid_info) = loader.initialize()
    if not loader_init_valid:
        fatal_error("CableInfoLoader initialization failed: " + loader_init_valid_info)

    (load_valid, load_valid_info, cable_records) = loader.load_records()
    if not load_valid:
        fatal_error("CableInfoLoader loader failed: " + load_valid_info)

    (loader_fini_valid, loader_fini_valid_info) = loader.finalize()
    if not loader_fini_valid:
        fatal_error("CableInfoLoader initialization failed: " + loader_fini_valid_info)

    # run garbage collection
    loader = None
    unreachable = gc.collect()
    logging.debug("gc loader unreachable: " + str(unreachable))
    logging.debug("gc loader stats: " + str(gc.get_stats()))

    #
    # Invoke each CableModule for each cable record.
    #
    module_list = []
    module_list.append(TestModule())
    for module in module_list:
        (module_init_valid, module_init_valid_info) = module.initialize()
        if not module_init_valid:
            fatal_error("Module initialization failed: " + module_init_valid_info)
        (process_valid, process_valid_info) = module.process_records(cable_records)
        if not process_valid:
            fatal_error("Module processing failed: " + process_valid_info)


    #
    # Generate pull list output for each cable record.
    #
    generator = PullListGenerator()
    (generator_init_valid, generator_init_valid_info) = generator.initialize()
    if not generator_init_valid:
        fatal_error("Module initialization failed: " + generator_init_valid_info)
    (generate_valid, generate_valid_info) = generator.generate(cable_records)
    if not generate_valid:
        fatal_error("Module processing failed: " + generate_valid_info)


if __name__ == '__main__':
    main()