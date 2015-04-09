#!/usr/bin/env python

from cdbObject import CdbObject
from cdb.common.exceptions.invalidArgument import InvalidArgument

class PdmLinkDrawing(CdbObject):

    DEFAULT_KEY_LIST = ['name', 'windchillUrl', 'RESP_ENG', 'DRAFTER', 'WBS_DESCRIPTION', 'TITLE1',
                        'TITLE2', 'TITLE3', 'TITLE4', 'TITLE5', 'revisionList']
    VALID_EXTENSION_LIST = [ 'drw', 'prt', 'asm' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

    # Check drawing name
    @classmethod
    def checkDrawingName(cls, drawingName):
        if drawingName.find('.') < 0 or drawingName.split('.')[-1].lower() not in cls.VALID_EXTENSION_LIST:
            raise InvalidArgument('PDMLink drawing name must have one of the following extensions: %s' % cls.VALID_EXTENSION_LIST)

