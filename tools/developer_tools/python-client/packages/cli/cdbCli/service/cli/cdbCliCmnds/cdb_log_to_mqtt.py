#!/usr/bin/env python

import logging
import click
import time
import datetime
import re
import json
from collections import deque
from CdbApiFactory import CdbApiFactory
import paho.mqtt.client as mqtt
from logging.handlers import TimedRotatingFileHandler

from cdbCli.common.cli.cliBase import CliBase
from rich import print


def start_status_logs():

    global FORMATTER
    FORMATTER = logging.Formatter(
        "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
    )
    global LOG_FILE
    LOG_FILE = "cdb_log_to_mqtt.log"
    global FILE_HANDLER
    FILE_HANDLER = TimedRotatingFileHandler(LOG_FILE, when="midnight")
    FILE_HANDLER.setFormatter(FORMATTER)
    # Set up the Main Logger
    global LOGGER
    LOGGER = logging.getLogger("main")
    LOGGER.addHandler(FILE_HANDLER)
    LOGGER.setLevel(logging.DEBUG)
    LOGGER.propagate = False
    # Set up a Logger for the MQTT Code
    global PAHO_LOGGER
    PAHO_LOGGER = logging.getLogger("paho_mqtt")
    PAHO_LOGGER.addHandler(FILE_HANDLER)
    PAHO_LOGGER.setLevel(logging.INFO)
    PAHO_LOGGER.propagate = False


# Set Up Data Structures for interacting with MQTT
MQTT_DATA = {}
MQTT_DATA["queue"] = deque()


def mqtt_on_publish(client, userdata, result):
    """Callback function for Paho when messages are published"""
    LOGGER.debug("MQTT Data published")
    pass


def mqtt_on_connect(client, userdata, flags, rc):
    """Callback function for Paho MQTT when connection is made
    to the server"""
    if rc == 0:
        LOGGER.info("MQTT Connected to broker")
        client.connect_flag = True  # Signal connection
        client.disconnect_flag = False
        client.publish(userdata["base_topic"], "ONLINE", qos=1, retain=True)

    else:
        LOGGER.info("MQTT Connection failed")


def mqtt_on_disconnect(client, userdata, rc):
    """Callback function when client disconnects"""
    logging.info("MQTT Disconnecting reason  " + str(rc))
    client.connect_flag = False
    client.disconnect_flag = True


def mqtt_on_message(client, userdata, message):
    """Callback function when message is received.  We just put the
    message in a queue"""
    LOGGER.info("On_Message Message Received: " + str(message.payload))
    userdata["queue"].append(message)


def set_up_mqtt_client(clientname, server, port, username, password, base_topic):
    """Sets up the MQTT Connection so that we can publish to it

    :param clientname: The name of this MQTT Applicaiton client
    :param server: Address of the MQTT Server
    :param port: TCP Port the server is runnig on
    :param username: Username on the MQTT Server
    :param password: Password for the MQTT Server
    :param base_topic:  The Base MQTT Topic underwhich we are sending messages."""

    client = mqtt.Client(clientname, False)
    client.on_connect = mqtt_on_connect
    client.on_disconnect = mqtt_on_disconnect
    client.on_message = mqtt_on_message
    client.on_publish = mqtt_on_publish
    client.connect_flag = False
    client.disconnect_flag = True
    client.enable_logger(logger=PAHO_LOGGER)
    client.username_pw_set(username, password)
    MQTT_DATA["base_topic"] = base_topic
    client.user_data_set(MQTT_DATA)
    client.will_set(base_topic, payload="OFFLINE", qos=1, retain=True)
    client.loop_start()
    client.connect(server, port)
    while client.disconnect_flag:
        time.sleep(0.1)
    LOGGER.info("Connected to MQTT Server and in Main Body")
    return client


def get_cdb_logs(delta_minutes, logApi):
    """Gets the CDB Logs for the last delta_minutes minutes and
    returns as a list of dicts

    :param delta_minutes: The time interval between sampling the logs
    :param logApi:  The CDB Api method to obtain the logs.


    """
    now = datetime.datetime.utcnow()
    time_delta = datetime.timedelta(minutes=delta_minutes)
    earlier = now - time_delta
    results = logApi.get_successful_entity_update_log_since_entered_date(str(earlier))
    results_list = [
        dict(
            [
                ("id", result.id),
                ("text", result.text),
                ("item_id", re.search("\[Item\ Id:\ (.*)\]", result.text).group(1)),
                ("entered_on_date_time", result.entered_on_date_time),
                ("effective_from_date_time", result.effective_from_date_time),
                ("effective_to_date_time", result.effective_to_date_time),
            ]
        )
        for result in results
        if "Item Id" in result.text
    ]
    return results_list


@click.command()
@click.option(
    "--cdb_server",
    default="https://cdb.aps.anl.gov/cdb",
    help="Address of the CDB Server",
)
@click.option(
    "--mqtt_server", default="cooper.aps.anl.gov", help="Address of the MQTT Server"
)
@click.option("--mqtt_port", default=1883, help="TCP Port Number for the MQTT Server")
@click.option(
    "--mqtt_clientname",
    default="cdb_log_to_mqtt",
    help="Unique MQTT clientname to identify this app",
)
@click.option(
    "--mqtt_applicationtopic",
    default="CDBItemUpdate",
    help="Top level MQTT Topic under which messages are sent.",
)
@click.option(
    "--sleepsecs",
    default=60,
    help="Time polling interval in seconds between sampling the CDB Logs",
)
@click.argument("mqtt_user")
@click.argument("mqtt_password")
def cdb_log_to_mqtt(
    cli,
    cdb_server,
    mqtt_server,
    mqtt_port,
    mqtt_clientname,
    mqtt_applicationtopic,
    sleepsecs,
    mqtt_user,
    mqtt_password,
):

    """This application periodically downloads the logs from the CDB Server and scans the log for changes to entity
    items.   If a change is reported by the log within the polling interval then a short message containing the
    item id is sent to the channel:

    \b
    <MQTT_APPLICATIONTOPIC>/ALL/

    \b
    A second message is sent to the channel

    \b
    <MQTT_APPLICATIONTOPIC>/<ITEM ID>

    \b
    In addition,  Online and Offline statuses are reported on the <MQTT_APPLICATION> channel directly.

    \b
    One use of this logger is to simulate a "Report by Exception" process where a CDB Client is waiting for
    a change to an item before using the CDB API to request the data.
    """
    # Start up the program status logging

    start_status_logs()

    # Attach to the communication channels
    mqtt_client = set_up_mqtt_client(
        mqtt_clientname,
        mqtt_server,
        mqtt_port,
        mqtt_user,
        mqtt_password,
        mqtt_applicationtopic,
    )

    # And now the CDB Authorization
    #    apiFactory = CdbApiFactory(cdb_server)    
    try:
        cli.api_factory = CdbApiFactory(cdb_server)
        apiFactory = cli.require_authenticated_api(
            "Authentication as an Administrator is required."
        )
        apiFactory.testAuthenticated()
        LOGGER.info("CDB Factory object is authenticated")
    except:
        print("Sorry, CDB factory authentication not valid")
        LOGGER.info("Sorry, CDB factory authentication not valid")
        exit
    itemApi = apiFactory.getItemApi()
    logApi = apiFactory.getLogApi()

    # If we are still running, we can begin our loop.  It is pretty simple.
    # We will continuous get the logs from the CDB Server, scan it for
    # changes and if so, send a message to the ALL channel and also
    # to an item number specific channel:

    # We initially assume that we are only going to send messages
    # from this point forward.

    # The time interval for sleeping is given to us in sleepsecs.
    # The total cycle time for sending data to the MQTT server is
    # going to be the sleep time + the retrieval and processing time
    # for the messages.   So, we will calculate a minimum time_delta
    # in order to get the logs

    time_delta = 100 * sleepsecs if sleepsecs > 36 else 3600
    LOGGER.info("Time length of CDB Logs (secs): " + str(time_delta))
    previous_log_results = get_cdb_logs(time_delta, logApi)
    try:
        while True:
            log_results = get_cdb_logs(time_delta, logApi)
            # Get the list of all of the log messages we need
            # to process
            new_log_messages = [
                message
                for message in log_results
                if message not in previous_log_results
            ]
            item_ids_to_log = {message["item_id"] for message in new_log_messages}

            # Now, we process the messages
            # For our initial server, we just want to notify that there
            # is a change so our message will be simple.  We can build
            # on it later.  We will just send the "now" time and the id.
            LOGGER.debug(
                "In Loop, number of item_ids to send: " + str(len(item_ids_to_log))
            )
            for item_id in item_ids_to_log:
                message = {"id": item_id, "date": str(datetime.datetime.now())}
                json_string = json.dumps(message)
                all_topic = mqtt_applicationtopic + "/ALL/"
                item_topic = mqtt_applicationtopic + "/" + str(item_id) + "/"
                mqtt_client.publish(all_topic, json_string)
                mqtt_client.publish(item_topic, json_string)
                LOGGER.debug("String Send to ALL Channel :" + json_string)
            previous_log_results = log_results
            time.sleep(sleepsecs)
    except:
        logging.exception("Exiting")
        mqtt_client.publish(mqtt_applicationtopic, "OFFLINE (normal)")
        mqtt_client.disconnect()
        mqtt_client.loop_stop()


if __name__ == "__main__":
    cdb_log_to_mqtt()
