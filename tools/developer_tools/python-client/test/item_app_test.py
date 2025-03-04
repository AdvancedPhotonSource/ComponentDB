import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models import NewAppInformation
from test.cdb_test_base import CdbTestBase


class ItemAppTest(CdbTestBase):

    def test_create_app(self):
        self.loginAsUser()
        # Gather Technical system and type
        domain = self.domainApi.get_domain_by_name(self.APP_DOMAIN_NAME)
        techsys = self.domainApi.get_domain_category_list(domain.id)
        specific_techsys = techsys[0]
        types = self.domainApi.get_item_category_allowed_types(specific_techsys.id)

        # Create New App
        new_app_info = NewAppInformation(
            name=self.NEW_APP_NAME,
            description=self.NEW_APP_DESCRIPTION,
            technical_system_list=[specific_techsys],
            type_list=types,
        )
        new_app = self.appApi.create_app(new_app_info)

        self.assertNotEqual(new_app, None, msg="Failed to create app")


if __name__ == "__main__":
    unittest.main()
