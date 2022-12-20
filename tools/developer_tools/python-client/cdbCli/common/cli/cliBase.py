#!/usr/bin/env python
import csv
from email.policy import default
import os
import sys

from click import prompt,echo

from getpass import getpass
from urllib3.exceptions import MaxRetryError

import click

from CdbApiFactory import CdbApiFactory
from cdbApi import ApiException
from cdbCli.common.utility.configurationManager import ConfigurationManager

from rich import print
from rich.panel import Panel
from rich.table import Table
from rich.json import JSON

FORMAT_RICH_OPT = 'Rich'
FORMAT_JSON_OPT = "JSON"
PYTHON_OBJ_FORMAT = "Dict"
FORMAT_OPTS = [FORMAT_RICH_OPT, FORMAT_JSON_OPT, PYTHON_OBJ_FORMAT]

class CliBase:

    api_factory = None

    PANEL_LINE_FORMAT = "[blue]%-25s[/blue] %s\n"    

    def __init__(self,portal_key=None):
        self.config = ConfigurationManager.get_instance()
        if portal_key:
            self._get_api_factory(portal_key)
            
    def _get_api_factory(self,portal_key=None):
        if CliBase.api_factory is None:
            if portal_key:
                portal_URL = self.config.get_portal_address(portal_key)
            else:
                portal_URL = self.config.get_portal_address()
            CliBase.api_factory = CdbApiFactory(portal_URL)
        return CliBase.api_factory

    def get_session_token(self):
        session_file_path = self.config.get_session_file_path()
        if os.path.exists(session_file_path):
            session_file = open(session_file_path, 'r')
            return session_file.read()
        return None

    def set_session_token(self, session_token):
        session_file_path = self.config.get_session_file_path()
        directory = os.path.dirname(session_file_path)
        if not os.path.exists(directory):
            os.makedirs(directory)

        session_file = open(session_file_path, 'w')
        session_file.write(session_token)

    def require_api(self,portal_key=None):
        apiFactory = self._get_api_factory(portal_key)
        return apiFactory

    def require_authenticated_api(self,
                                  prompt_string="The command requires authentication.",
                                  portal_key=None):
        factory = self.require_api(portal_key)
        factory.getItemApi()

        try:
            token = self.get_session_token()
            if token is not None:
                factory.setAuthenticateToken(token)
            factory.testAuthenticated()
            return(factory)
        except ApiException as ex:
            pass
        # The session key doesn't work.  We now 
        # See if the username and password are in the Configuration data
        username,password = self.config.get_session_credentials()
        if (username != None) and (password != None):
            try:
                factory.authenticateUser(username, password)
                token = factory.getAuthenticateToken()
                self.set_session_token(token)
                return(factory)
            except ApiException as ex:
                exObj = factory.parseApiException(ex)
                echo("Local configured password failed: %s" % exObj.simple_name)

        # Need to prompt for credentials
        echo(prompt_string)
        username = prompt("Username")
        password = getpass("Password: ")
        try:
            factory.authenticateUser(username, password)
            token = factory.getAuthenticateToken()
            self.set_session_token(token)
            return factory
        except ApiException as ex:
            exObj = factory.parseApiException(ex)
            raise Exception("%s - %s" % (exObj.simple_name, exObj.message))

    @staticmethod
    def print_cdb_obj(cdb_object):
        cdb_object = str(cdb_object) + '\n'
        echo(cdb_object)

    # TODO Add a print list cdb of cdb object

    def prepare_cli_input_csv_reader(self, input_file, stdin_prompt):
        reader = csv.reader(input_file)
        stdin_tty_mode = (input_file == sys.stdin) and sys.stdin.isatty()
    
        if stdin_tty_mode:
            print(stdin_prompt)
        else:
            # Removes header located in first row
            next(reader)

        return reader, stdin_tty_mode
    
def simple_obj_list_to_str(list):
    result = ""
    for obj in list:
        result += obj.name + ", "

    return result[:-2]
    
def print_results(console, result_obj, format=FORMAT_RICH_OPT, pager=False, table_style = [], header_style={}, **kwargs):
    if format == FORMAT_RICH_OPT:
        printables = create_rich_result_obj(result_obj, table_style, header_style)
    else:                            
        if format == FORMAT_JSON_OPT:
            printables = [JSON.from_data(result_obj)] 
        elif format == PYTHON_OBJ_FORMAT:
            printables = [result_obj]        

    if pager:
        with console.pager():
            print_printables(console, printables)
    else:
        print_printables(console, printables)

def create_rich_result_obj(result_obj, table_style=[], header_style={}):
    printables = []

    for section in result_obj.keys():
        if section in header_style.keys():
            title = "[%s]%s" % (header_style[section], section)
        else:
            title = section 

        section_contents = result_obj[section]

        if section_contents.__len__() > 0:
            key_length = section_contents[0].keys().__len__()
            if key_length == 1:
                value = ""
                for content in section_contents: 
                    data_keys = content.keys()

                    for data_key in data_keys:
                        data_val = content[data_key]
                        if not data_val and data_val != 0:
                            data_val = ''
                        value += CliBase.PANEL_LINE_FORMAT % (data_key, data_val)
                                
                panel = Panel(value[:-1], title=title)

                printables.append(panel)
            elif key_length > 1:
                # Table
                headers = section_contents[0].keys()
                table = Table(title=title, expand=True, show_lines=True)

                for i, header in enumerate(headers): 
                    if i < len(table_style):
                        style = table_style[i]
                        table.add_column(header, style=style)
                    else:
                        table.add_column(header)

                for content in section_contents: 
                    row_list = []
                    for value in content.values():
                        row_list.append(str(value))
                    row_contents = tuple(row_list)

                    table.add_row(*row_contents)

                printables.append(table)

    return printables        

def print_printables(console, printables):
    for printable in printables:
        console.print(printable)

def wrap_print_format_cli_click_options(function):
    function = click.option(
        "--format",

        default=FORMAT_RICH_OPT,
        type=click.Choice(FORMAT_OPTS, case_sensitive=False),        
        help="How the results will be displayed."
    )(function)
    return function

def wrap_common_cli_click_options(function):    
    function = click.option(
        "--add-log-to-item",
        is_flag=True,
        help="Add a log entry to the machine item after the change is made."
    )(function)
    return function

def cli_command_api_exception_handler(func):
    '''
    Exception decorator prints the exception in the expected format for the user. 
    
    Decorator to be applied on the the cli_helper function and not on the 'click' function. 
    '''
    def Inner_Function(*args, **kwargs):
        if 'cli' not in kwargs and 'factory' not in kwargs:
            raise Exception("'cli' or 'factory' must be a kwargs parameter to use decorator. Other recommended params are 'console' and 'format'")
        try:
            return func(*args, **kwargs)
        except ApiException as ex:
            if 'cli' in kwargs.keys():                
                cli : CliBase = kwargs['cli']
                factory = cli.api_factory
            else:
                factory = kwargs['factory']

            exObj = factory.parseApiException(ex)
            if 'console' in kwargs:         
                exceptionDictList = [] 
                printDict = {'Exception': exceptionDictList}
                header_style = {'Exception': 'red'}
                
                exceptionDictList.append({"HTTP Status": exObj.status})
                exceptionDictList.append({"Name": exObj.simple_name})
                exceptionDictList.append({"Message": exObj.message})

                print_results(result_obj=printDict, header_style=header_style, **kwargs)
            else:
                msg = "%s(%s) - %s" % (exObj.simple_name, exObj.status, exObj.message)
                print(msg)
            return ex
        except MaxRetryError as ex:
            # Connection issue
            print(ex)
            return ex
        
    return Inner_Function

if __name__ == "__main__":
    cli = CliBase()
    factory = cli.require_authenticated_api()
