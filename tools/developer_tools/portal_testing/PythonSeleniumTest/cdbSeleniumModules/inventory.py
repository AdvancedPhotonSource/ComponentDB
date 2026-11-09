from selenium.common.exceptions import StaleElementReferenceException
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait

from cdbSeleniumModules.itemBase import ItemBase


class Inventory(ItemBase):

    VIEW_BASE_NAME = 'itemDomainInventory'
    ENTITY_TYPE_NAME = 'componentInstance'
    COLUMN_IDX = 3
    FORM_NAME = ENTITY_TYPE_NAME + '%sForm'
    LIST_FORM_NAME = FORM_NAME % 'List'
    VIEW_FORM_NAME = FORM_NAME % 'View'
    EDIT_FORM_NAME = FORM_NAME % 'Edit'
    IMPORT_FORM_NAME = "importInventoryForm"

    DATA_TABLE_XPATH_FORMULA = '//*[@id="%s:%sListDataTable_data"]/tr[1]/td[%d]/a'

    IMPORT_FILE_NAME = "Inventory Import.xlsx"

    ASSIGNMENT_DIALOG_ID = VIEW_FORM_NAME + ':machineAssignmentDialog'
    ASSIGNMENT_TREE_ID = VIEW_FORM_NAME + ':itemMachineDesignListDataTableinventoryMachineAssignment'
    REPLACEMENT_DIALOG_ID = VIEW_FORM_NAME + ':machineAssignmentReplacementConfirmDialog'

    TEST_INVENTORY_ID = 130
    TEST_MACHINE_COMPONENT_1 = [
        'Machine Assignment Test Source', 'Machine Assignment Test Component 1']
    TEST_MACHINE_COMPONENT_2 = [
        'Machine Assignment Test Source', 'Machine Assignment Test Component 2']
    TEST_REPLACEMENT_DESTINATION = [
        'Machine Assignment Replacement Root',
        'Machine Assignment Replacement Destination']

    UI_ACTION_TIMEOUT = 30
    LOCATION_DETAILS_OUTPUT_CSS = "[id$=':locationDetails']"
    LOCATION_DETAILS_EDIT_XPATH = "//a[contains(@id, 'locationDetailsViewOutputEditShowButton')]"
    # Only one p:inplace editor is open at a time; its content holds the input, check and cancel.
    INPLACE_CONTENT_XPATH = "//*[contains(concat(' ', @class, ' '), ' ui-inplace-content ')]"
    LOCATION_DETAILS_WIDGET = 'locationDetailsViewOutputEditableWidget'
    ASSIGNED_ITEM_DIALOG_ID = VIEW_FORM_NAME + ':updateAssignedInventoryItemDialog'

    def open_inventory_by_id(self, inventory_id):
        self._open_inventory_by_id(inventory_id)

    def get_location_details(self):
        return self.driver.find_element(By.CSS_SELECTOR, self.LOCATION_DETAILS_OUTPUT_CSS).text.strip()

    def _open_inplace_editor(self):
        def visible_editor(driver):
            for content in driver.find_elements(By.XPATH, self.INPLACE_CONTENT_XPATH):
                if content.is_displayed():
                    return content
            return False
        return WebDriverWait(self.driver, self.UI_ACTION_TIMEOUT,
                             ignored_exceptions=(StaleElementReferenceException,)).until(visible_editor)

    def set_location_details_inline(self, details):
        """Edit location details in place on the inventory view page (saves the item)."""
        self._wait_for_clickable_xpath(self.LOCATION_DETAILS_EDIT_XPATH).click()
        editor = self._open_inplace_editor()
        details_input = editor.find_element(By.XPATH, ".//input[@type='text']")
        details_input.clear()
        if details:
            details_input.send_keys(details)
        buttons = [b for b in editor.find_elements(By.XPATH, ".//button") if b.is_displayed()]
        if buttons:
            buttons[0].click()  # check (save); the second is cancel
        else:
            # Same as the check button's oncomplete: submit the inplace editor.
            self.driver.execute_script("PF('%s').save()" % self.LOCATION_DETAILS_WIDGET)
        WebDriverWait(self.driver, self.UI_ACTION_TIMEOUT,
                      ignored_exceptions=(StaleElementReferenceException,)).until(
            lambda driver: self.get_location_details() == (details or ''))
        self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)

    def update_assembly_element(self, element_name, new_item_id, new_item_name):
        """Assign an inventory item to an assembly element via the edit-assignment dialog.

        Rows are selected by PrimeFaces row key (item id); ``new_item_name`` is used to
        filter when the item is not on the first page.
        """
        edit_xpath = ("//tr[td[normalize-space(.)=%s]]//a[contains(@id, 'AssignedItemEditActionButton')]"
                      % self._xpath_literal(element_name))
        self._wait_for_clickable_xpath(edit_xpath).click()
        self._wait_for_visible_id(self.ASSIGNED_ITEM_DIALOG_ID)

        row_xpath = ("//*[@id='%s']//tbody[contains(@id, 'ItemSelectDataTable')]/tr[@data-rk='%s']"
                     % (self.ASSIGNED_ITEM_DIALOG_ID, new_item_id))
        if not self.driver.find_elements(By.XPATH, row_xpath):
            name_filter = self._wait_for_visible_xpath(
                "//*[@id='%s']//input[contains(@id, 'NameColumn:filter')]" % self.ASSIGNED_ITEM_DIALOG_ID)
            name_filter.clear()
            name_filter.send_keys(new_item_name)
        WebDriverWait(self.driver, self.UI_ACTION_TIMEOUT).until(
            EC.element_to_be_clickable((By.XPATH, row_xpath))).click()
        # Selection re-renders the dialog panel under the loading dialog.
        self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)

        save_xpath = "//*[@id='%s']//button[.//span[text()='Save']]" % self.ASSIGNED_ITEM_DIALOG_ID
        self._wait_for_clickable_xpath(save_xpath).click()
        self._wait_for_invisible_by_id(self.ASSIGNED_ITEM_DIALOG_ID, timeout=self.UI_ACTION_TIMEOUT)
        self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)

    def navigate_to_inventory_list(self):
        self._navigate_to_dropdown('inventoryDropdownButton', 'componentInstanceButton', '%s/list' % self.VIEW_BASE_NAME)

    def test_inventory_pages(self):
        self._click_on_xpath(self.DATA_TABLE_XPATH_FORMULA % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, self.COLUMN_IDX))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sViewEditButton' % (self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/edit' % self.VIEW_BASE_NAME)

        self._click_on_id('%s:%sEditViewButton' % (self.EDIT_FORM_NAME, self.ENTITY_TYPE_NAME))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

    def test_inventory_detail_page(self, test):
        self._click_on_xpath(self.DATA_TABLE_XPATH_FORMULA % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, self.COLUMN_IDX))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

        self._add_log_to_item(self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME, "Inventory Log.")
        self._clear_notifications()
        self._add_property_to_item(test, self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME, "Inventory Property.")
        self._add_image_to_item(self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME)

    def test_machine_assignment_acceptance(self, test):
        """Acceptance coverage for inventory-side machine assignment."""
        self._open_inventory_by_id(self.TEST_INVENTORY_ID)

        # Selection and Installed/Planned state are available from Housing edit.
        self._open_machine_assignment()
        test.assertTrue(self._wait_for_visible_id(self.ASSIGNMENT_DIALOG_ID).is_displayed())
        install_button = self._wait_for_id(self.VIEW_FORM_NAME + ':machineAssignmentInstalled')
        test.assertIn('Installed', install_button.text)

        # Select an empty destination and save as Planned; persistence/view refresh
        # are verified by reopening the dialog and checking its restored state.
        self._select_machine(self.TEST_MACHINE_COMPONENT_1)
        install_button.click()
        save_button = self._wait_for_xpath(
            "//*[@id='%s:machineAssignmentDialogFooter']//button[span[text()='Save']]" % self.VIEW_FORM_NAME)
        save_button.click()
        self._wait_for_invisible_by_id(self.ASSIGNMENT_DIALOG_ID)
        self._open_machine_assignment()
        selected = self._wait_for_xpath(
            "//*[@id='%s']//tr[@aria-selected='true']" % self.ASSIGNMENT_TREE_ID)
        test.assertIn(self.TEST_MACHINE_COMPONENT_1[-1], selected.text)
        test.assertIn('Planned', self._wait_for_id(self.VIEW_FORM_NAME + ':machineAssignmentInstalled').text)

        # Moving to another destination releases the source assignment first.
        self._select_machine(self.TEST_MACHINE_COMPONENT_2)
        save_button = self._wait_for_xpath(
            "//*[@id='%s:machineAssignmentDialogFooter']//button[span[text()='Save']]" % self.VIEW_FORM_NAME)
        save_button.click()
        self._wait_for_invisible_by_id(self.ASSIGNMENT_DIALOG_ID)
        self._open_machine_assignment()
        selected = self._wait_for_xpath(
            "//*[@id='%s']//tr[@aria-selected='true']" % self.ASSIGNMENT_TREE_ID)
        test.assertIn(self.TEST_MACHINE_COMPONENT_2[-1], selected.text)

        # Clear swaps inventory for its catalog and removes Housing assignment.
        clear_button = self._wait_for_xpath(
            "//*[@id='%s:machineAssignmentDialogFooter']//button[span[text()='Clear']]" % self.VIEW_FORM_NAME)
        clear_button.click()
        self._wait_for_invisible_by_id(self.ASSIGNMENT_DIALOG_ID)
        self._open_machine_assignment()
        test.assertFalse(self._has_selected_machine())

        # Different-catalog destination requires confirmation and completes all steps.
        self._select_machine(self.TEST_REPLACEMENT_DESTINATION)
        save_button = self._wait_for_xpath(
            "//*[@id='%s:machineAssignmentDialogFooter']//button[span[text()='Save']]" % self.VIEW_FORM_NAME)
        save_button.click()
        confirm_dialog = self._wait_for_visible_id(self.REPLACEMENT_DIALOG_ID)
        test.assertTrue(confirm_dialog.is_displayed())
        test.assertIn('Clear destination assignment and update catalog', confirm_dialog.text)
        test.assertIn('Assign inventory to destination machine element', confirm_dialog.text)

        self._click_on_xpath(
            "//*[@id='%s']//button[span[text()='Clear Destination and Assign']]" % self.REPLACEMENT_DIALOG_ID)
        done_button = self._wait_for_clickable_xpath(
            "//*[@id='%s']//button[span[text()='Done']]" % self.REPLACEMENT_DIALOG_ID)
        completed_icons = self.driver.find_elements(
            By.CSS_SELECTOR, '#%s .fa-check-square-o' % self.REPLACEMENT_DIALOG_ID.replace(':', '\\:'))
        test.assertEqual(2, len(completed_icons), 'Destination preparation and assignment should complete')
        done_button.click()
        self._wait_for_invisible_by_id(self.ASSIGNMENT_DIALOG_ID)

        # Reopen to verify replacement persistence, then clear for test cleanup.
        self._open_machine_assignment()
        selected = self._wait_for_xpath(
            "//*[@id='%s']//tr[@aria-selected='true']" % self.ASSIGNMENT_TREE_ID)
        test.assertIn(self.TEST_REPLACEMENT_DESTINATION[-1], selected.text)
        clear_button = self._wait_for_xpath(
            "//*[@id='%s:machineAssignmentDialogFooter']//button[span[text()='Clear']]" % self.VIEW_FORM_NAME)
        clear_button.click()
        self._wait_for_invisible_by_id(self.ASSIGNMENT_DIALOG_ID)

    def _open_inventory_by_id(self, inventory_id):
        self.driver.get('%s/views/%s/view.xhtml?id=%s' % (
            self.driver.current_url.split('/views/')[0], self.VIEW_BASE_NAME, inventory_id))
        self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

    def _open_machine_assignment(self):
        self._wait_for_id_and_click(self.VIEW_FORM_NAME + ':machineAssignmentEditButton')
        self._wait_for_visible_id(self.ASSIGNMENT_DIALOG_ID)

    def _select_machine(self, hierarchy):
        """Expand the known test hierarchy and select its exact leaf."""
        for name in hierarchy[:-1]:
            row = self._wait_for_machine_row(name)
            toggler = row.find_element(By.CSS_SELECTOR, '.ui-treetable-toggler')
            if 'ui-icon-triangle-1-e' in toggler.get_attribute('class'):
                toggler.click()
                self._wait_for_machine_row(hierarchy[-1])

        self._wait_for_machine_row(hierarchy[-1]).click()

    def _wait_for_machine_row(self, name):
        row_xpath = ("//*[@id='%s']//tr[.//span[normalize-space(text())=%s]]"
                     % (self.ASSIGNMENT_TREE_ID, self._xpath_literal(name)))
        return self._wait_for_clickable_xpath(row_xpath)

    @staticmethod
    def _xpath_literal(value):
        if "'" not in value:
            return "'%s'" % value
        if '"' not in value:
            return '"%s"' % value
        parts = value.split("'")
        return "concat(%s)" % ", \"'\", ".join("'%s'" % part for part in parts)

    def _has_selected_machine(self):
        selected = self.driver.find_elements(
            By.XPATH, "//*[@id='%s']//tr[@aria-selected='true']" % self.ASSIGNMENT_TREE_ID)
        return len(selected) > 0

    def test_import_create(self, test):
        self._wait_for_id_and_click('%s:%sImportButton' % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME))
        table_results = self._import_navigate_to_verification_data_table(self.IMPORT_FORM_NAME, self.IMPORT_FILE_NAME)
        test.assertEqual(len(table_results), 25, msg='25 items were imported in the spreadsheet')
        self._import_complete(self.IMPORT_FORM_NAME, self.VIEW_BASE_NAME)
