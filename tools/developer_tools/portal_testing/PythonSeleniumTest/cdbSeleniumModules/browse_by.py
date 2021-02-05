import time

from selenium.common.exceptions import TimeoutException

from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase


class BrowseBy(CdbSeleniumModuleBase):

    BROWSE_BY_DROPDOWN_BUTTON_ID = 'browseByDropdownButton'
    BROWSE_BY_FUNCTION_BUTTON_ID = "browseByFunctionButton"
    BROWSE_BY_OWNER_BUTTON_ID = "browseByOwnerButton"
    BROWSE_BY_LOCATION_BUTTON_ID = "browseByLocationButton"

    BROWSE_BY_FUNCTION_URL_CONTAINS = "itemDomainCatalog/browseCategoryType"
    BROWSE_BY_OWNER_URL_CONTAINS = "itemDomainCatalog/browseOwner"
    BROWSE_BY_LOCATION_URL_CONTAINS = "itemDomainInventory/browseLocation"

    def navigate_to_browse_by_function(self):
        self._navigate_to_dropdown(self.BROWSE_BY_DROPDOWN_BUTTON_ID, self.BROWSE_BY_FUNCTION_BUTTON_ID,
                                   self.BROWSE_BY_FUNCTION_URL_CONTAINS)

    def navigate_to_browse_by_owner(self):
        self._navigate_to_dropdown(self.BROWSE_BY_DROPDOWN_BUTTON_ID, self.BROWSE_BY_OWNER_BUTTON_ID,
                                   self.BROWSE_BY_OWNER_URL_CONTAINS)

    def navigate_to_browse_by_location(self):
        self._navigate_to_dropdown(self.BROWSE_BY_DROPDOWN_BUTTON_ID, self.BROWSE_BY_LOCATION_BUTTON_ID,
                                   self.BROWSE_BY_LOCATION_URL_CONTAINS)

    def _verify_filter_view_data_table(self, test, empty, msg=None, form_id='componentForm'):
        data_table_row_x_path = '//*[@id="%s:filterViewDatatable_data"]/tr'
        data_table_row_x_path = data_table_row_x_path % form_id
        data_table_empty_row_class = 'ui-widget-content ui-datatable-empty-message'
        data_table_row = self._find_by_xpath(data_table_row_x_path)
        first_row_class = data_table_row.get_attribute("class")
        if empty:
            test.assertEqual(first_row_class, data_table_empty_row_class, msg=msg)
        if not empty:
            test.assertNotEqual(first_row_class, data_table_empty_row_class,
                                msg=msg)

    def test_browse_by_owner(self, test):
        self._verify_filter_view_data_table(test, True, msg='Table not empty by default')

        self._click_on_xpath('//*[@id="componentForm:filterViewItemCategorySelection"]/div[2]/ul/li[1]')
        time.sleep(1)

        self._verify_filter_view_data_table(test, False, msg='Table filters did not update table in time..')

    def test_browse_by_function(self, test):
        self._verify_filter_view_data_table(test, True, msg='Table not empty by default')

        # Click on first tech sys
        self._click_on_xpath('//*[@id="componentForm:filterViewItemCategorySelection"]/div[2]/ul/li[1]')
        time.sleep(1)

        self._verify_filter_view_data_table(test, False, msg='Table filters did not update table in time..')

    def test_browse_by_location(self, test):
        form_id='componentInstanceForm'
        self._verify_filter_view_data_table(test, True, msg='Table not empty by default', form_id=form_id)

        location_selection_first_item_xpath = '//*[@id="componentInstanceForm:filterViewItemHierarchySelectLevel:%s:filterViewItemLocationSelection"]/div[2]/ul/li[1]'


        # Click on first tech sys
        self._click_on_xpath(location_selection_first_item_xpath % '0')
        time.sleep(1)

        self._verify_filter_view_data_table(test, False, msg='Table filters did not update table in time..',
                                            form_id=form_id)

        # Verify that next level of locations loaded
        failed = False
        try:
            self._wait_for_visible_xpath(location_selection_first_item_xpath % '1')
        except TimeoutException:
            failed = True

        test.assertEqual(False, failed, msg='The next level of location hierarchy failed to load')







