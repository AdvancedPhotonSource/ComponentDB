"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdb.common.api.cdbRestApi import CdbRestApi


class ItemRestApi(CdbRestApi):
	def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
		CdbRestApi.__init__(self, username, password, host, port, protocol)
		self.configurationManager.setContextRoot('/cdb/api')

	def getItemById(self, itemId):
		url = "%s/items/ById/%s" % (self.getContextRoot(), itemId)

		response = self.sendRequest(url, method="GET", contentType='application/json')

		return response

	def getItemByQrId(self, itemQrId):
		url = "%s/items/ByQrId/%s" % (self.getContextRoot(), itemQrId)

		response = self.sendRequest(url, method="GET", contentType='application/json')

		return response


if __name__ == "__main__":
	itemApi = ItemRestApi(username='cdb', password='cdb', host='localhost', port=8080, protocol='http')

	print itemApi.getItemById(500)['name']
	print itemApi.getItemById(1233)['name']
	print itemApi.getItemById(51)['name']
	print itemApi.getItemById(5)['name']
	print itemApi.getItemById(3000)['name']

	print itemApi.getItemByQrId(240)['name']

"""
	
	import urllib2
	
	from cdb.common.client.sessionManager import SessionManager
	
	sessionMgr = SessionManager()
	
	#urlopener = urllib2.build_opener(urllib2.HTTPHandler)
	
	#print urlopener.open('http://localhost:8080/cdb/api/propertyType/all')
	
	sessionMgr.establishSession('http://localhost:8080/cdb/api', 'cdb', 'cdb', selector='/login')
	
	print ''
	
	print sessionMgr.sendRequest('http://localhost:8080/cdb/api/propertyType/all', "GET", contentType='application/json')
	
	#print sessionMgr.sendSessionRequest('/propertyType/add/somethin23g', 'PUT')
"""
