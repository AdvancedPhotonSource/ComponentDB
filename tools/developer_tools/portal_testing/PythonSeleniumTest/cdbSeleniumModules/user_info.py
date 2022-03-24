from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase


class UserInfo(CdbSeleniumModuleBase):

    VIEW_BASE_NAME = 'userInfo'
    ENTITY_TYPE_NAME = 'userInfo'
    COLUMN_IDX = 1
    FORM_NAME = "UserInfo%sForm"
    LIST_FORM_NAME = 'view' + FORM_NAME % 'List'
    VIEW_FORM_NAME = 'view' + FORM_NAME % ''
    EDIT_FORM_NAME = 'edit' + FORM_NAME % ''

    EXPORT_FORM_NAME = "exportUserInfoForm"
    EXPORT_FILE_NAME = 'UserInfo Export.xlsx'

    def navigate_to_user_info_list(self):
        self._navigate_to_dropdown('administrativeButton', 'usersButton', '%s/list' % self.VIEW_BASE_NAME)

    def test_user_info_pages(self):
        dataTableXpathFormula = '//*[@id="%s:%sListDataTable_data"]/tr[1]/td[%d]/a'
        self._click_on_xpath(dataTableXpathFormula % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, self.COLUMN_IDX))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sViewEditButton' % (self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/edit' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sEditViewButton' % (self.EDIT_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

    def export_user_info(self, test):
        self._navigate_to_export_from_list(self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME)
        self._export(self.EXPORT_FORM_NAME, self.EXPORT_FILE_NAME, test)