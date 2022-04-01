from cdbSeleniumModules.itemBase import ItemBase


class CableDesign(ItemBase):

    VIEW_BASE_NAME = 'itemDomainCableDesign'
    ENTITY_TYPE_NAME = 'cableDesign'
    COLUMN_IDX = 1
    FORM_NAME = ENTITY_TYPE_NAME + '%sForm'
    LIST_FORM_NAME = FORM_NAME % 'List'
    VIEW_FORM_NAME = FORM_NAME % 'View'
    EDIT_FORM_NAME = FORM_NAME % 'Edit'

    EXPORT_FORM_NAME = "exportCableDesignForm"
    EXPORT_FILE_NAME = 'Cable Design Export.xlsx'

    def navigate_to_cable_design_list(self):
        self._navigate_to_dropdown('designMenubarDropdownButton', 'cableDesignMenubarButton', '%s/list' % self.VIEW_BASE_NAME)

    def test_cable_design_pages(self):
        dataTableXpathFormula = '//*[@id="%s:%sListDataTable_data"]/tr[1]/td[%d]/a'
        self._click_on_xpath(dataTableXpathFormula % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, self.COLUMN_IDX))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sViewEditButton' % (self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/edit' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sEditViewButton' % (self.EDIT_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

    def export_cable_design(self, test):
        self._navigate_to_export_from_list(self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME)
        self._export(self.EXPORT_FORM_NAME, self.EXPORT_FILE_NAME, test)