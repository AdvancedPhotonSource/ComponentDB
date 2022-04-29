from cdbSeleniumModules.itemBase import ItemBase


class CableInventory(ItemBase):

    VIEW_BASE_NAME = 'itemDomainCableInventory'
    ENTITY_TYPE_NAME = 'cableInventory'
    COLUMN_IDX = 3
    FORM_NAME = ENTITY_TYPE_NAME + '%sForm'
    LIST_FORM_NAME = FORM_NAME % 'List'
    VIEW_FORM_NAME = FORM_NAME % 'View'
    EDIT_FORM_NAME = FORM_NAME % 'Edit'

    DATA_TABLE_XPATH_FORMULA = '//*[@id="%s:%sListDataTable_data"]/tr[1]/td[%d]/a'

    def navigate_to_cable_inventory_list(self):
        self._navigate_to_dropdown('inventoryDropdownButton', 'cableInventoryButton', '%s/list' % self.VIEW_BASE_NAME)

    def test_cable_inventory_pages(self):
        
        self._click_on_xpath(self.DATA_TABLE_XPATH_FORMULA % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, self.COLUMN_IDX))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sViewEditButton' % (self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/edit' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sEditViewButton' % (self.EDIT_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

    def test_cable_inventory_detail_page(self, test): 
        self._click_on_xpath(self.DATA_TABLE_XPATH_FORMULA % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, self.COLUMN_IDX))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)        

        self._add_log_to_item(self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME, "Cable Inventory Log.")
        self._clear_notifications()
        self._add_property_to_item(test, self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME, "Cable Inventory Property.")
        self._add_image_to_item(self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME)