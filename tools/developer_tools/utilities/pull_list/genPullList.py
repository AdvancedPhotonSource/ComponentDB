import argparse
import configparser
import logging
import os
import sys
from abc import ABC, abstractmethod


class CableInfoLoader:

    def __init__(self):
        pass

    def initialize(self):
        return True, ""

    def load_records(self):
        return True, "", []


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
    option_input_dir = get_config_resource(config_preimport, 'DEFAULT', 'inputDir', True)
    if not os.path.isdir(option_input_dir):
        fatal_error("'[%s] inputDir' directory: %s does not exist, exiting" % ('DEFAULT', option_input_dir))

    # process outputDir option
    option_output_dir = get_config_resource(config_preimport, 'DEFAULT', 'outputDir', True)
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
    cable_loader = CableInfoLoader()
    (loader_init_valid, loader_init_valid_str) = cable_loader.initialize()
    if not loader_init_valid:
        fatal_error("CableInfoLoader initialization failed: " + loader_init_valid_str)
    (load_valid, load_valid_str, cable_records) = cable_loader.load_records()
    if not load_valid:
        fatal_error("CableInfoLoader loader failed: " + load_valid_str)

    #
    # Invoke each CableModule for each cable record.
    #
    module_list = []
    module_list.append(TestModule())
    for module in module_list:
        (module_init_valid, module_init_valid_str) = module.initialize()
        if not module_init_valid:
            fatal_error("Module initialization failed: " + module_init_valid_str)
        (process_valid, process_valid_str) = module.process_records(cable_records)
        if not process_valid:
            fatal_error("Module processing failed: " + process_valid_str)


    #
    # Generate pull list output for each cable record.
    #
    generator = PullListGenerator()
    (generator_init_valid, generator_init_valid_str) = generator.initialize()
    if not generator_init_valid:
        fatal_error("Module initialization failed: " + generator_init_valid_str)
    (generate_valid, generate_valid_str) = generator.generate(cable_records)
    if not generate_valid:
        fatal_error("Module processing failed: " + generate_valid_str)


if __name__ == '__main__':
    main()