import ConfigParser, os

class test():
    CONFIG_PATH = '../pdmLink.cfg'
    CONFIG_SECTION_NAME = 'PDMLink'
    CONFIG_PDMLINK_USER_KEY = 'pdmLinkUser'
    CONFIG_PDMLINK_PASS_KEY = 'pdmLinkPass'
    CONFIG_PDMLINK_URL_KEY = 'pdmLinkUrl'
    CONFIG_ICMS_URL_KEY = 'icmsUrl'
    CONFIG_ICMS_USER_KEY = 'icmsUser'
    CONFIG_ICMS_PASS_KEY = 'icmsPass'

    def __init__(self):

        config = ConfigParser.ConfigParser()
        config.readfp(open(self.CONFIG_PATH))

        pdmlinkUser = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_PDMLINK_USER_KEY)
        pdmlinkPass = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_PDMLINK_PASS_KEY)
        pdmLinkUrl = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_PDMLINK_URL_KEY)
        icmsUrl = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_ICMS_URL_KEY)
        icmsUser = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_ICMS_USER_KEY)
        icmsPass = config.get(self.CONFIG_SECTION_NAME, self.CONFIG_ICMS_PASS_KEY)

        print (pdmlinkUser, pdmlinkPass, pdmLinkUrl, icmsUrl, icmsUser, icmsPass)


test()