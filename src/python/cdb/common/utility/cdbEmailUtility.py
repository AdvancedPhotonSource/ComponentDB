#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.utility.configurationManager import ConfigurationManager
from cdb.common.exceptions.configurationError import ConfigurationError
from cdb.common.exceptions.cdbException import CdbException


class CdbEmailUtility:
    """
    Singleton Utility is used for sending out emails from CDB.
    """

    CONFIG_SECTION_NAME = 'EmailUtility'
    CONFIG_OPTION_NAME_LIST = ['emailMode',
                               'emailSmtpServer',
                               'emailSenderEmail',
                               'emailAdminNotificationEmail',
                               'emailSubjectStart']

    class emailUtilityModes:
        """
        Defines constants for the various modes possible in configuration for email utility.
        """
        development = 'development'
        developmentWithEmail = 'developmentWithEmail'
        production = 'production'

    # Singleton
    __instance = None

    @classmethod
    def getInstance(cls):
        from cdb.common.utility.cdbEmailUtility import CdbEmailUtility
        try:
            cdbEmailUtility = CdbEmailUtility()
        except CdbEmailUtility as utility:
            cdbEmailUtility = utility
        return cdbEmailUtility

    def __init__(self):
        if CdbEmailUtility.__instance:
            raise CdbEmailUtility.__instance
        CdbEmailUtility.__instance = self
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

        cm = ConfigurationManager.getInstance()
        cm.setOptionsFromConfigFile(CdbEmailUtility.CONFIG_SECTION_NAME, CdbEmailUtility.CONFIG_OPTION_NAME_LIST)

        self.mode = cm.getEmailMode()
        self.__verifyValidMode(self.mode)
        self.logger.debug("Running in '%s' mode." % self.mode)

        self.senderEmail = cm.getEmailSenderEmail()
        self.adminNotificationEmail = cm.getEmailAdminNotificationEmail()
        self.cdbSubjectStart = cm.getEmailSubject()

        self.smtpServer = cm.getEmailSmtpServer()

    def __verifyValidMode(self, mode):
        if mode == self.emailUtilityModes.development:
            return
        elif mode == self.emailUtilityModes.developmentWithEmail:
            return
        elif mode == self.emailUtilityModes.production:
            return
        else:
            raise ConfigurationError('The mode specified in configuration for email utility: %s is not valid.' % mode)

    def __getEmailToUse(self, email):
        if self.mode == self.emailUtilityModes.production:
            return email
        elif self.mode == self.emailUtilityModes.developmentWithEmail:
            return self.adminNotificationEmail
        else:
            return None

    def __getHtmlMessageForAdmin(self, originalEmail):
        if self.mode != self.emailUtilityModes.production:
            return 'This email was originally addressed to %s <br/>' % originalEmail
        else:
            return ''

    def __sendEmail(self, originalEmail, subject, messageHtmlText):
        emailTo = self.__getEmailToUse(originalEmail)
        messageForAdmin = self.__getHtmlMessageForAdmin(originalEmail)

        htmlMessage = """
                <html>
                    <head></head>
                    <body>
                        %s
                        <p>%s</p>
                        <br/><br/>
                        <p>
                            <pre>
-------------------------------------
--Please do not reply to this email--
-------------------------------------
                            </pre>
                        </p>
                    </body>
                </html>
                """ % (messageForAdmin, messageHtmlText)

        if emailTo is not None:
            htmlPart = MIMEText(htmlMessage, 'html')
            plainPart = MIMEText(messageHtmlText, 'plain')

            emailMessage = MIMEMultipart('alternative')
            emailMessage.attach(plainPart)
            emailMessage.attach(htmlPart)
            emailMessage['From'] = self.senderEmail
            emailMessage['To'] = emailTo
            emailMessage['Subject'] = subject
            messageString = emailMessage.as_string()

            if originalEmail == emailTo:
                self.logger.debug("Sending email message to %s" % emailTo)
            else:
                self.logger.debug("Sending email message to %s, originally addressed to %s" % (emailTo, originalEmail))
            try:
                emailService = smtplib.SMTP(self.smtpServer)
                emailService.sendmail(self.senderEmail, emailTo, messageString)
                emailService.close()
            except Exception as ex:
                self.logger.error('Cannot send email message: %s' % ex)

        else:
            self.logger.debug('Email will not be sent to %s' % originalEmail)
            self.logger.debug('Contents: %s: ' % messageHtmlText)

    def __generateNotificationSubject(self, nameOfNotification):
        return '%s %s Notification' % (self.cdbSubjectStart, nameOfNotification)

    def sendEmailNotification(self, email, nameOfNotification, messageToSend):
        """
        Sends a notification from CDB.

        :param email: (String) email to send notification to
        :param nameOfNotification: (String) Name of notification; placed in subject.
        :param messageToSend: (String) Contents of the email message.
        """
        self.logger.debug("Preparing system notification email to %s." % email)
        subject = self.__generateNotificationSubject(nameOfNotification)
        self.__sendEmail(email, subject, messageToSend)

    def sendExceptionNotification(self, exceptionObject, tracebackFormatExc, functionDescription = None):
        self.logger.debug("Preparing exception email for %s." % self.adminNotificationEmail)
        # Add a space if function description was provided.
        if functionDescription is not None:
            functionDescription = ' in ' + functionDescription
        else:
            functionDescription = ''

        message = 'An exception occurred%s in cdb web service: <br/>' % functionDescription
        if isinstance(exceptionObject, CdbException):
            message += exceptionObject.getJsonRep()
        else:
            message += '<pre>%s : %s</pre>' % (type(exceptionObject).__name__, str(exceptionObject))
        message += '<br/><br/>Stacktrace: <br/> <pre>%s</pre>' % tracebackFormatExc

        subject = self.__generateNotificationSubject("Exception")

        self.__sendEmail(self.adminNotificationEmail, subject, message)

    @staticmethod
    def generateSimpleHtmlTableMessage(informationOrderedDictionary):
        """
        Generates a table based on a dictionary provided.

        :param informationOrderedDictionary: (OrderedDict) dict of information to show in table
        :return: (String) html table representation of dictionary provided.
        """
        result = "<table>"

        for infoItemKey, infoItemValue in informationOrderedDictionary.items():
            if infoItemValue is not None and infoItemValue != '':
                infoItemValueTdAttributes = ''
                if str(infoItemValue).isdigit():
                    infoItemValueTdAttributes = "align='right'"

                row = "<tr><td>%s&nbsp;</td><td %s>%s</td></tr>" % (infoItemKey, infoItemValueTdAttributes, infoItemValue)
                result += row

        result += "</table>"
        return result

