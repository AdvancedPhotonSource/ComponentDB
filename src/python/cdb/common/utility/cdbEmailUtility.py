import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart


class CdbEmailUtility:
    """
    Utility is used for sending out emails from CDB.
    """

    def __init__(self):
        self.emailService = smtplib.SMTP('localhost')
        self.senderEmail = 'cdb@aps.anl.gov'

    def sendEmailNotification(self, email, nameOfNotification, messageToSend):
        """
        Sends a notification from CDB.

        :param email: (String) email to send notification to
        :param nameOfNotification: (String) Name of notification; placed in subject.
        :param messageToSend: (String) Contents of the email message.
        """
        message = self.__prepareEmailMessage(messageToSend, email)
        message['Subject'] = self.__generateNotificationSubject(nameOfNotification)
        self.emailService.sendmail(self.senderEmail, email, message.as_string())

    def __generateNotificationSubject(self, nameOfNotification):
        return '[CDB] %s Notification' % nameOfNotification

    def __prepareEmailMessage(self, message, email):
    	htmlMessage = """
	<html>
	  <head></head>
	  <body>
	    <p>%s</p>
	    <br/>
	    <br/>
	    <p>
	      ---------------------------------------------------- <br/>
	      Please do not reply to this email
	    </p>
	  </body>
	</html>
	""" % message

        htmlPart = MIMEText(htmlMessage, 'html')
	plainPart = MIMEText(message, 'plain')
	emailMessage = MIMEMultipart('alternative')
	emailMessage.attach(plainPart)
	emailMessage.attach(htmlPart)
	emailMessage['From'] = self.senderEmail
        emailMessage['To'] = email
        return emailMessage

if __name__ == '__main__':
    emailUtility = CdbEmailUtility()
    emailUtility.sendEmailNotification('djarosz@aps.anl.gov', 'Test', 'This is a test notification from CDB.')
