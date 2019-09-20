from cdbApi.api.item_api import ItemApi
from cdbApi.api.downloads_api import DownloadsApi
from cdbApi.api.property_api import PropertyApi
from cdbApi.api.users_api import UsersApi
from cdbApi.api_client import ApiClient
from cdbApi.configuration import Configuration


class CdbApiFactory:

	def __init__(self, cdbUrl):
		config = Configuration(host=cdbUrl)
		apiClient = ApiClient(configuration=config)
		self.itemApi = ItemApi(api_client=apiClient)
		self.downloadsApi = DownloadsApi(api_client=ApiClient)
		self.propertyApi = PropertyApi(api_client=ApiClient)
		self.usersApi = UsersApi(api_client=ApiClient)

	def getItemApi(self):
		return self.itemApi

	def getDownloadApi(self):
		return self.downloadsApi

	def getPropertyApi(self):
		return self.propertyApi

	def getUsersApi(self):
		return self.usersApi


if __name__ == '__main__':
	# Example
	apiFactory = CdbApiFactory("https://cdb-dev.aps.anl.gov/cdb_dev")
	itemApi = apiFactory.getItemApi()

	catalogItems = itemApi.get_catalog_items()
	catalogItem = catalogItems[0]

	# Lists of items seem to be lists of dict items
	catalogId = catalogItem.get('id')

	# Single items seem to be appropriate type
	catalogFetchedById = itemApi.get_item_by_id_from_api(catalogId)
	print(catalogFetchedById.name)

	inventoryItemPerCatalog = itemApi.get_items_derived_from_item_by_item_id(catalogId)
	print(inventoryItemPerCatalog)















