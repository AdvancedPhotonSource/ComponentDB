import hashlib
import os
import unittest
from datetime import datetime

from cdbApi.exceptions import OpenApiException
from cdbApi.models.log_entry_edit_information import LogEntryEditInformation
from test.cdb_test_base import CdbTestBase


class LogTest(CdbTestBase):

    def test_log_api(self):
        self.loginAsUser()
        fail = False
        try:
            result = self.logApi.get_successful_login_log()
        except OpenApiException as ex:
            fail = True
        self.assertEqual(fail, True)

        self.loginAsAdmin()
        result = self.logApi.get_successful_login_log()
        self.assertNotEqual(result, None)

    def test_log_attachment_round_trip(self):
        self.loginAsAdmin()

        # Create a log entry on a known item and capture its id.
        log_message = "TEST LOG for attachment entered on %s" % datetime.now()
        created_log = self.itemApi.add_log_entry_to_item(
            log_entry_edit_information=LogEntryEditInformation(
                item_id=self.INVENTORY_ITEM_ID, log_entry=log_message
            )
        )
        log_id = created_log.id
        self.assertIsNotNone(log_id, msg="Created log did not have an id")

        # Upload a file attachment to the log entry.
        upload = self.factory.createFileUploadObject(self.SAMPLE_DOC_PATH)
        upload_result = self.itemApi.upload_log_attachment_for_item(
            self.INVENTORY_ITEM_ID, log_id, file_upload_object=upload
        )
        self.assertIsNotNone(upload_result.attachment_list)
        self.assertEqual(len(upload_result.attachment_list), 1)

        # Fetch the item logs and confirm the attachment metadata is exposed.
        logs = self.itemApi.get_logs_for_item(self.INVENTORY_ITEM_ID)
        target_log = None
        for log in logs:
            if log.id == log_id:
                target_log = log
                break
        self.assertIsNotNone(target_log, msg="Uploaded log was not found")
        self.assertIsNotNone(target_log.attachment_list)
        self.assertEqual(len(target_log.attachment_list), 1)

        attachment = target_log.attachment_list[0]
        attachment_id = attachment.id
        self.assertIsNotNone(attachment_id)
        self.assertEqual(
            attachment.original_filename, os.path.basename(self.SAMPLE_DOC_PATH)
        )

        # Download the attachment and confirm the bytes match the uploaded file.
        response = self.downloadsApi.get_log_attachment_without_preload_content(
            attachment_id
        )
        downloaded_bytes = response.read()
        expected_size = os.path.getsize(self.SAMPLE_DOC_PATH)
        self.assertEqual(len(downloaded_bytes), expected_size)

        # Confirm the downloaded file's checksum matches the uploaded file.
        with open(self.SAMPLE_DOC_PATH, "rb") as uploaded_file:
            uploaded_checksum = hashlib.sha256(uploaded_file.read()).hexdigest()
        downloaded_checksum = hashlib.sha256(downloaded_bytes).hexdigest()
        self.assertEqual(
            downloaded_checksum,
            uploaded_checksum,
            msg="Downloaded attachment checksum did not match uploaded file",
        )

        # A bogus attachment id should raise.
        fail = False
        try:
            self.downloadsApi.get_log_attachment(999999999)
        except OpenApiException as ex:
            fail = True
        self.assertEqual(fail, True)

    def test_log_attachment_upload_permission_denied(self):
        # Admin creates the log entry on an item testUser cannot write.
        item_id = self.CATALOG_ITEM_ID
        self.loginAsAdmin()
        log_message = "TEST LOG perm check entered on %s" % datetime.now()
        created_log = self.itemApi.add_log_entry_to_item(
            log_entry_edit_information=LogEntryEditInformation(
                item_id=item_id, log_entry=log_message
            )
        )
        log_id = created_log.id

        # testUser attempts to attach a file -> should be denied.
        self.loginAsUser()
        upload = self.factory.createFileUploadObject(self.SAMPLE_DOC_PATH)
        fail = False
        try:
            self.itemApi.upload_log_attachment_for_item(
                item_id, log_id, file_upload_object=upload
            )
        except OpenApiException as ex:
            fail = True
        self.assertEqual(fail, True)


if __name__ == "__main__":
    unittest.main()
