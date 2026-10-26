import unittest
from cdbApi.exceptions import OpenApiException
from test.cdb_test_base import CdbTestBase


class DomainTest(CdbTestBase):

    def test_domain_api(self):
        domains = self.domainApi.get_domain_list()
        self.assertNotEqual(domains, None)

        domain_id = domains[0].id
        domain_by_id = self.domainApi.get_domain_by_id(domain_id)
        self.assertNotEqual(domain_by_id, None)


if __name__ == "__main__":
    unittest.main()
