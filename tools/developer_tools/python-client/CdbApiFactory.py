#!/usr/bin/env python

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.
import base64
import os

from cdbApi import ApiException, DomainApi, FileUploadObject, LocationItemsApi, LogApi, PropertyValueApi, \
	ComponentCatalogItemsApi, ComponentInventoryItemsApi, ApiExceptionMessage, CableImportApi, AppItemsApi, MAARCItemsApi
from cdbApi.api.item_api import ItemApi
from cdbApi.api.downloads_api import DownloadsApi
from cdbApi.api.property_type_api import PropertyTypeApi
from cdbApi.api.search_api import SearchApi
from cdbApi.api.users_api import UsersApi
from cdbApi.api.sources_api import SourcesApi
from cdbApi.api.cable_catalog_items_api import CableCatalogItemsApi
from cdbApi.api.cable_design_items_api import CableDesignItemsApi
from cdbApi.api.machine_design_items_api import MachineDesignItemsApi
from cdbApi.api.connector_types_api import ConnectorTypesApi
from cdbApi.api_client import ApiClient
from cdbApi.api.authentication_api import AuthenticationApi
from cdbApi.configuration import Configuration


class CdbApiFactory:

	HEADER_TOKEN_KEY = "token"
	URL_FORMAT = "%s/views/item/view?id=%s"

	LOCATION_DOMAIN_ID = 1
	CATALOG_DOMAIN_ID = 2
	INVENTORY_DOMAIN_ID = 3
	MAARC_DOMAIN_ID= 5
	MACHINE_DESIGN_DOMAIN_ID = 6
	CABLE_CATALOG_DOMAIN_ID = 7
	CABLE_INVENTORY_DOMAIN_ID = 8
	CABLE_DESIGN_DOMAIN_ID = 9

	def __init__(self, cdbUrl):
		self.cdbUrl = cdbUrl
		self.config = Configuration(host=self.cdbUrl)
		self.apiClient = ApiClient(configuration=self.config)
		self.itemApi = ItemApi(api_client=self.apiClient)
		self.downloadsApi = DownloadsApi(api_client=self.apiClient)
		self.propertyTypeApi = PropertyTypeApi(api_client=self.apiClient)
		self.propertyValueApi = PropertyValueApi(api_client=self.apiClient)
		self.usersApi = UsersApi(api_client=self.apiClient)
		self.domainApi = DomainApi(api_client=self.apiClient)
		self.sourceApi = SourcesApi(api_client=self.apiClient)
		self.logApi = LogApi(api_client=self.apiClient)
		self.cableCatalogItemApi = CableCatalogItemsApi(api_client=self.apiClient)
		self.cableDesignItemApi = CableDesignItemsApi(api_client=self.apiClient)
		self.machineDesignItemApi = MachineDesignItemsApi(api_client=self.apiClient)
		self.maarcItemApi = MAARCItemsApi(api_client=self.apiClient)
		self.appItemApi = AppItemsApi(api_client=self.apiClient)
		self.locationItemApi = LocationItemsApi(api_client=self.apiClient)
		self.componentCatalogItemApi = ComponentCatalogItemsApi(api_client=self.apiClient)
		self.componentInventoryItemApi = ComponentInventoryItemsApi(api_client=self.apiClient)
		self.connectorTypesApi = ConnectorTypesApi(api_client=self.apiClient)
		self.cableImportApi = CableImportApi(api_client=self.apiClient)
		self.searchApi = SearchApi(api_client=self.apiClient)

		self.authApi = AuthenticationApi(api_client=self.apiClient)

	def getItemApi(self) -> ItemApi:
		return self.itemApi

	def getDomainApi(self) -> DomainApi:
		return self.domainApi

	def getDownloadApi(self) -> DownloadsApi:
		return self.downloadsApi

	def getPropertyTypeApi(self) -> PropertyTypeApi:
		return self.propertyTypeApi

	def getPropertyValueApi(self) -> PropertyValueApi:
		return self.propertyValueApi

	def getUsersApi(self) -> UsersApi:
		return self.usersApi

	def getSourceApi(self) -> SourcesApi:
		return self.sourceApi

	def getLogApi(self) -> LogApi:
		return self.logApi

	def getCableCatalogItemApi(self) -> CableCatalogItemsApi:
		return self.cableCatalogItemApi

	def getCableDesignItemApi(self) -> CableDesignItemsApi:
		return self.cableDesignItemApi

	def getMachineDesignItemApi(self) -> MachineDesignItemsApi:
		return self.machineDesignItemApi
	
	def getMAARCItemApi(self) -> MAARCItemsApi:
		return self.maarcItemApi
	
	def getAppItemApi(self) -> AppItemsApi:
		return self.appItemApi

	def getLocationItemApi(self) -> LocationItemsApi:
		return self.locationItemApi

	def getComponentCatalogItemApi(self) -> ComponentCatalogItemsApi:
		return self.componentCatalogItemApi

	def getConnectorTypesApi(self) -> ConnectorTypesApi:
		return self.connectorTypesApi

	def getCableImportApi(self) -> CableImportApi:
		return self.cableImportApi

	def getSearchApi(self) -> SearchApi:
		return self.searchApi

	def generateCDBUrlForItemId(self, itemId):
		return self.URL_FORMAT % (self.cdbUrl, str(itemId))

	def authenticateUser(self, username, password):
		response = self.authApi.authenticate_user_with_http_info(username=username, password=password)

		token = response[-1][self.HEADER_TOKEN_KEY]
		self.setAuthenticateToken(token)

	def setAuthenticateToken(self, token):
		self.apiClient.set_default_header(self.HEADER_TOKEN_KEY, token)

	def getAuthenticateToken(self):
		return self.apiClient.default_headers[self.HEADER_TOKEN_KEY]

	def testAuthenticated(self):
		self.authApi.verify_authenticated()

	def logOutUser(self):
		self.authApi.log_out()

	@classmethod
	def createFileUploadObject(cls, filePath):
		data = open(filePath, "rb").read()
		b64String = base64.b64encode(data).decode()

		fileName = os.path.basename(filePath)
		return FileUploadObject(file_name=fileName, base64_binary=b64String)

	def parseApiException(self, openApiException):
		responseType = ApiExceptionMessage.__name__
		openApiException.data = openApiException.body
		exObj = self.apiClient.deserialize(openApiException, responseType)
		exObj.status = openApiException.status
		return exObj

def run_command():
	# Example
	print("\nEnter cdb URL (ex: https://cdb.aps.anl.gov/cdb): ")
	hostname = input()
        
	apiFactory = CdbApiFactory(hostname)
	itemApi = apiFactory.getItemApi()

	catalogItems = itemApi.get_catalog_items()
	catalogItem = catalogItems[0]

	# Lists of items seem to be lists of dict items
	catalogId = catalogItem.id

	# Single items seem to be appropriate type
	catalogFetchedById = itemApi.get_item_by_id(catalogId)
	print(catalogFetchedById.name)

	inventoryItemPerCatalog = itemApi.get_items_derived_from_item_by_item_id(catalogId)
	print(inventoryItemPerCatalog)

	print("\n\n\nWould you like to test authentication? (y/N): ")
	resp = input()
	if resp == 'y' or resp == "Y":
		import getpass
		print("Username: ")
		username = input()
		print("Password: ")
		password = getpass.getpass()

		try:
			apiFactory.authenticateUser(username, password)
			apiFactory.testAuthenticated()
			apiFactory.logOutUser()
		except ApiException:
			print("Authentication failed!")
			exit(1)

		print("Success!")

if __name__ == '__main__':
	run_command()
