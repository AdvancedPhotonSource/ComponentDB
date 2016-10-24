import cherrypy
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.utility.loggingManager import LoggingManager
from cdb.cdb_web_service.tasks.sparePartsTaks import SparePartsTask
from cdb.common.utility.cdbEmailUtility import CdbEmailUtility
import traceback


class CdbBackgroundTaskManager:

    BACKGROUND_TASKS_TIME_IN_SECONDS = 3600

    def __init__(self):
        self.sparePartsTask = SparePartsTask()
        self.cherryPyBackgroundTasks = cherrypy.process.plugins.BackgroundTask(self.BACKGROUND_TASKS_TIME_IN_SECONDS,
                                                                               self.scheduledTasks)
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
        self.emailUtility = CdbEmailUtility.getInstance()

    def start(self):
        self.cherryPyBackgroundTasks.start()

    def scheduledTasks(self):
        try:
            self.logger.debug("Executing scheduled tasks.")
            self.sparePartsTask.checkSpares()
        except Exception as ex:
            self.logger.error(ex)
            tracebackFormatExc = traceback.format_exc()
            self.emailUtility.sendExceptionNotification(ex, tracebackFormatExc, 'Scheduled Tasks')