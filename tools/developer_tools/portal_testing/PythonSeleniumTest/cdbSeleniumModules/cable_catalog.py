from cdbSeleniumModules.itemBase import ItemBase


class CableCatalog(ItemBase):

    VIEW_BASE_NAME = 'itemDomainCableCatalog'
    ENTITY_TYPE_NAME = 'cableCatalog'
    FORM_NAME = ENTITY_TYPE_NAME + '%sForm'
    LIST_FORM_NAME = FORM_NAME % 'List'
    VIEW_FORM_NAME = FORM_NAME % 'View'
    EDIT_FORM_NAME = FORM_NAME % 'Edit'

    def navigate_to_cable_catalog_list(self):
        self._navigate_to_dropdown('catalogDropdownButton', 'cableCatalogButton', '%s/list' % self.VIEW_BASE_NAME)

    def test_cable_catalog_pages(self):
        dataTableXpathFormula = '//*[@id="%s:%sListDataTable_data"]/tr[1]/td[2]/a'
        self._click_on_xpath(dataTableXpathFormula % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sViewEditButton' % (self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/edit' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sEditViewButton' % (self.EDIT_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)