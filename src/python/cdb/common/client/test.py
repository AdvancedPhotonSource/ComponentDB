from cdb.cdb_web_service.api.itemRestApi import ItemRestApi

itemRestApi = ItemRestApi('cdb','cdb1', 'bioroid', 10232, 'http')

itemRestApi.addLogEntryToItemWithQrId(10, 'test')


