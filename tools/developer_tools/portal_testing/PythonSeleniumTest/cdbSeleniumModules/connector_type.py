from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase


class ConnectorType(CdbSeleniumModuleBase):

    VIEW_BASE_NAME = 'connectorType'
    ENTITY_TYPE_NAME = 'connectorType'
    COLUMN_IDX = 1
    FORM_NAME = ENTITY_TYPE_NAME + "%sForm"
    LIST_FORM_NAME = 'view' + FORM_NAME % 'CategoryList'    
    VIEW_FORM_NAME = FORM_NAME % 'View'
    EDIT_FORM_NAME = FORM_NAME % 'Edit'

    EXPORT_FORM_NAME = "exportConnectorTypeForm"
    EXPORT_FILE_NAME = 'ConnectorType Export.xlsx'

    def navigate_to_connector_type_list(self):
        self._navigate_to_dropdown('administrativeButton', 'connectorTypesButton', '%s/list' % self.VIEW_BASE_NAME)

    def test_connector_type_pages(self):
        dataTableXpathFormula = '//*[@id="%s:%sListDataTable_data"]/tr[1]/td[%d]/a'
        self._click_on_xpath(dataTableXpathFormula % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, self.COLUMN_IDX))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sViewEditButton' % (self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/edit' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sEditViewButton' % (self.EDIT_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)
    
    def export_connector_type(self, test):
        self._navigate_to_export_from_list(self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME)
        self._export(self.EXPORT_FORM_NAME, self.EXPORT_FILE_NAME, test)
    