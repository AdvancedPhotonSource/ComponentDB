from cdbSeleniumModules.itemBase import ItemBase


class Location(ItemBase):

    VIEW_BASE_NAME = 'itemDomainLocation'
    ENTITY_TYPE_NAME = 'location'
    COLUMN_IDX = 1
    FORM_NAME = ENTITY_TYPE_NAME + '%sForm'
    LIST_FORM_NAME = FORM_NAME % 'List'
    VIEW_FORM_NAME = FORM_NAME % 'View'
    EDIT_FORM_NAME = FORM_NAME % 'Edit'

    def navigate_to_location_list(self):
        self._navigate_to_dropdown('administrativeButton', 'locationsButton', '%s/list' % self.VIEW_BASE_NAME)

    def test_location_pages(self):
        dataTableXpathFormula = '//*[@id="%s:%sListTreeTable_node_0"]/td[%d]/a'
        self._click_on_xpath(dataTableXpathFormula % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, self.COLUMN_IDX))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sViewEditButton' % (self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/edit' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sEditViewButton' % (self.EDIT_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)
