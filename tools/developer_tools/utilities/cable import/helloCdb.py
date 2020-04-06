from CdbApiFactory import CdbApiFactory
from cdbApi import SimpleLocationInformation

api = CdbApiFactory("http://localhost:8080/cdb")
api.authenticateUser("cdb", "cdb")

itemApi = api.getItemApi()
items = itemApi.get_catalog_items()
print(items)

# loc = SimpleLocationInformation()
# itemApi.update_item_location(loc)

api.logOutUser()
