import cherrypy
import sparePartsTaks
from cdb.common.utility.loggingManager import LoggingManager
from cdb.cdb_web_service.tasks.sparePartsTaks import SparePartsTask

class CdbBackgroundTaskManager():

    def __init__(self):
        self.sparePartsTask = SparePartsTask()
        self.cherryPyBackgroundTasks = cherrypy.process.plugins.BackgroundTask(1, self.scheduledTasks)
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

    def start(self):
        self.cherryPyBackgroundTasks.start()

    def scheduledTasks(self):
        self.logger.debug("Executing scheduled tasks.")
        self.sparePartsTask.checkSpares()