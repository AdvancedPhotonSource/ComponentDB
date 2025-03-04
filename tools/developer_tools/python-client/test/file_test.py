import unittest
from cdbApi.exceptions import OpenApiException
from test.cdb_test_base import CdbTestBase


class FileTest(CdbTestBase):
    def test_upload_document(self):
        self.loginAsAdmin()
        upload = self.factory.createFileUploadObject(self.SAMPLE_DOC_PATH)
        self.itemApi.upload_image_for_item(self.INVENTORY_ITEM_ID, file_upload_object=upload)

    def test_upload_image(self):
        self.loginAsAdmin()
        upload = self.factory.createFileUploadObject(self.SAMPLE_IMAGE_PATH)
        self.itemApi.upload_image_for_item(self.INVENTORY_ITEM_ID, file_upload_object=upload)


if __name__ == "__main__":
    unittest.main()
